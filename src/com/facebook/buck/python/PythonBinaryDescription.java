/*
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.python;

import com.facebook.buck.cxx.toolchain.CxxBuckConfig;
import com.facebook.buck.cxx.toolchain.CxxPlatform;
import com.facebook.buck.cxx.toolchain.linker.WindowsLinker;
import com.facebook.buck.file.WriteFile;
import com.facebook.buck.io.filesystem.ProjectFilesystem;
import com.facebook.buck.log.Logger;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.BuildTargets;
import com.facebook.buck.model.Flavor;
import com.facebook.buck.model.FlavorDomain;
import com.facebook.buck.model.InternalFlavor;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.CellPathResolver;
import com.facebook.buck.rules.CommonDescriptionArg;
import com.facebook.buck.rules.DefaultSourcePathResolver;
import com.facebook.buck.rules.Description;
import com.facebook.buck.rules.HasDeclaredDeps;
import com.facebook.buck.rules.HasTests;
import com.facebook.buck.rules.ImplicitDepsInferringDescription;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.rules.SourcePathRuleFinder;
import com.facebook.buck.rules.SymlinkTree;
import com.facebook.buck.rules.TargetGraph;
import com.facebook.buck.rules.args.MacroArg;
import com.facebook.buck.rules.coercer.PatternMatchedCollection;
import com.facebook.buck.util.HumanReadableException;
import com.facebook.buck.util.MoreCollectors;
import com.facebook.buck.util.Optionals;
import com.facebook.buck.util.immutables.BuckStyleImmutable;
import com.facebook.buck.versions.HasVersionUniverse;
import com.facebook.buck.versions.VersionRoot;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.immutables.value.Value;

public class PythonBinaryDescription
    implements Description<PythonBinaryDescriptionArg>,
        ImplicitDepsInferringDescription<
            PythonBinaryDescription.AbstractPythonBinaryDescriptionArg>,
        VersionRoot<PythonBinaryDescriptionArg> {

  private static final Logger LOG = Logger.get(PythonBinaryDescription.class);

  private final PythonBuckConfig pythonBuckConfig;
  private final FlavorDomain<PythonPlatform> pythonPlatforms;
  private final CxxBuckConfig cxxBuckConfig;
  private final CxxPlatform defaultCxxPlatform;
  private final FlavorDomain<CxxPlatform> cxxPlatforms;

  public PythonBinaryDescription(
      PythonBuckConfig pythonBuckConfig,
      FlavorDomain<PythonPlatform> pythonPlatforms,
      CxxBuckConfig cxxBuckConfig,
      CxxPlatform defaultCxxPlatform,
      FlavorDomain<CxxPlatform> cxxPlatforms) {
    this.pythonBuckConfig = pythonBuckConfig;
    this.pythonPlatforms = pythonPlatforms;
    this.cxxBuckConfig = cxxBuckConfig;
    this.defaultCxxPlatform = defaultCxxPlatform;
    this.cxxPlatforms = cxxPlatforms;
  }

  @Override
  public Class<PythonBinaryDescriptionArg> getConstructorArgType() {
    return PythonBinaryDescriptionArg.class;
  }

  public static BuildTarget getEmptyInitTarget(BuildTarget baseTarget) {
    return baseTarget.withAppendedFlavors(InternalFlavor.of("__init__"));
  }

  public static SourcePath createEmptyInitModule(
      BuildTarget buildTarget,
      ProjectFilesystem projectFilesystem,
      BuildRuleParams params,
      BuildRuleResolver resolver) {
    BuildTarget emptyInitTarget = getEmptyInitTarget(buildTarget);
    Path emptyInitPath = BuildTargets.getGenPath(projectFilesystem, buildTarget, "%s/__init__.py");
    WriteFile rule =
        resolver.addToIndex(
            new WriteFile(
                emptyInitTarget,
                projectFilesystem,
                params.withoutDeclaredDeps().withoutExtraDeps(),
                "",
                emptyInitPath,
                /* executable */ false));
    return rule.getSourcePathToOutput();
  }

  public static ImmutableMap<Path, SourcePath> addMissingInitModules(
      ImmutableMap<Path, SourcePath> modules, SourcePath emptyInit) {

    Map<Path, SourcePath> initModules = new LinkedHashMap<>();

    // Insert missing `__init__.py` modules.
    Set<Path> packages = new HashSet<>();
    for (Path module : modules.keySet()) {
      Path pkg = module;
      while ((pkg = pkg.getParent()) != null && !packages.contains(pkg)) {
        Path init = pkg.resolve("__init__.py");
        if (!modules.containsKey(init)) {
          initModules.put(init, emptyInit);
        }
        packages.add(pkg);
      }
    }

    return ImmutableMap.<Path, SourcePath>builder().putAll(modules).putAll(initModules).build();
  }

  private PythonInPlaceBinary createInPlaceBinaryRule(
      BuildTarget buildTarget,
      ProjectFilesystem projectFilesystem,
      BuildRuleParams params,
      BuildRuleResolver resolver,
      PythonPlatform pythonPlatform,
      CxxPlatform cxxPlatform,
      String mainModule,
      Optional<String> extension,
      PythonPackageComponents components,
      ImmutableSet<String> preloadLibraries) {

    // We don't currently support targeting Windows.
    if (cxxPlatform.getLd().resolve(resolver) instanceof WindowsLinker) {
      throw new HumanReadableException(
          "%s: cannot build in-place python binaries for Windows (%s)",
          buildTarget, cxxPlatform.getFlavor());
    }

    // Add in any missing init modules into the python components.
    SourcePath emptyInit = createEmptyInitModule(buildTarget, projectFilesystem, params, resolver);
    components = components.withModules(addMissingInitModules(components.getModules(), emptyInit));

    BuildTarget linkTreeTarget = buildTarget.withAppendedFlavors(InternalFlavor.of("link-tree"));
    Path linkTreeRoot = BuildTargets.getGenPath(projectFilesystem, linkTreeTarget, "%s");
    SymlinkTree linkTree =
        resolver.addToIndex(
            new SymlinkTree(
                linkTreeTarget,
                projectFilesystem,
                linkTreeRoot,
                ImmutableMap.<Path, SourcePath>builder()
                    .putAll(components.getModules())
                    .putAll(components.getResources())
                    .putAll(components.getNativeLibraries())
                    .build()));

    return PythonInPlaceBinary.from(
        buildTarget,
        projectFilesystem,
        params,
        resolver,
        cxxPlatform,
        pythonPlatform,
        mainModule,
        components,
        extension.orElse(pythonBuckConfig.getPexExtension()),
        preloadLibraries,
        pythonBuckConfig.legacyOutputPath(),
        linkTree,
        pythonPlatform.getEnvironment());
  }

  PythonBinary createPackageRule(
      BuildTarget buildTarget,
      ProjectFilesystem projectFilesystem,
      BuildRuleParams params,
      BuildRuleResolver resolver,
      SourcePathRuleFinder ruleFinder,
      PythonPlatform pythonPlatform,
      CxxPlatform cxxPlatform,
      String mainModule,
      Optional<String> extension,
      PythonPackageComponents components,
      ImmutableList<String> buildArgs,
      PythonBuckConfig.PackageStyle packageStyle,
      ImmutableSet<String> preloadLibraries) {

    switch (packageStyle) {
      case INPLACE:
        return createInPlaceBinaryRule(
            buildTarget,
            projectFilesystem,
            params,
            resolver,
            pythonPlatform,
            cxxPlatform,
            mainModule,
            extension,
            components,
            preloadLibraries);

      case STANDALONE:
        return PythonPackagedBinary.from(
            buildTarget,
            projectFilesystem,
            params,
            ruleFinder,
            pythonPlatform,
            pythonBuckConfig.getPexTool(resolver),
            buildArgs,
            pythonBuckConfig.getPexExecutor(resolver).orElse(pythonPlatform.getEnvironment()),
            extension.orElse(pythonBuckConfig.getPexExtension()),
            pythonPlatform.getEnvironment(),
            mainModule,
            components,
            preloadLibraries,
            pythonBuckConfig.shouldCacheBinaries(),
            pythonBuckConfig.legacyOutputPath());

      default:
        throw new IllegalStateException();
    }
  }

  private CxxPlatform getCxxPlatform(BuildTarget target, AbstractPythonBinaryDescriptionArg args) {
    return cxxPlatforms
        .getValue(target)
        .orElse(args.getCxxPlatform().map(cxxPlatforms::getValue).orElse(defaultCxxPlatform));
  }

  @Override
  public PythonBinary createBuildRule(
      TargetGraph targetGraph,
      BuildTarget buildTarget,
      ProjectFilesystem projectFilesystem,
      BuildRuleParams params,
      BuildRuleResolver resolver,
      CellPathResolver cellRoots,
      PythonBinaryDescriptionArg args) {
    if (!(args.getMain().isPresent() ^ args.getMainModule().isPresent())) {
      throw new HumanReadableException(
          "%s: must set exactly one of `main_module` and `main`", buildTarget);
    }
    Path baseModule = PythonUtil.getBasePath(buildTarget, args.getBaseModule());

    String mainModule;
    ImmutableMap.Builder<Path, SourcePath> modules = ImmutableMap.builder();
    SourcePathRuleFinder ruleFinder = new SourcePathRuleFinder(resolver);
    SourcePathResolver pathResolver = DefaultSourcePathResolver.from(ruleFinder);

    // If `main` is set, add it to the map of modules for this binary and also set it as the
    // `mainModule`, otherwise, use the explicitly set main module.
    if (args.getMain().isPresent()) {
      LOG.warn(
          "%s: parameter `main` is deprecated, please use `main_module` instead.", buildTarget);
      String mainName = pathResolver.getSourcePathName(buildTarget, args.getMain().get());
      Path main = baseModule.resolve(mainName);
      modules.put(baseModule.resolve(mainName), args.getMain().get());
      mainModule = PythonUtil.toModuleName(buildTarget, main.toString());
    } else {
      mainModule = args.getMainModule().get();
    }
    // Build up the list of all components going into the python binary.
    PythonPackageComponents binaryPackageComponents =
        PythonPackageComponents.of(
            modules.build(),
            /* resources */ ImmutableMap.of(),
            /* nativeLibraries */ ImmutableMap.of(),
            /* prebuiltLibraries */ ImmutableSet.of(),
            /* zipSafe */ args.getZipSafe());
    // Extract the platforms from the flavor, falling back to the default platforms if none are
    // found.
    PythonPlatform pythonPlatform =
        pythonPlatforms
            .getValue(buildTarget)
            .orElse(
                pythonPlatforms.getValue(
                    args.getPlatform()
                        .<Flavor>map(InternalFlavor::of)
                        .orElse(pythonPlatforms.getFlavors().iterator().next())));
    CxxPlatform cxxPlatform = getCxxPlatform(buildTarget, args);
    PythonPackageComponents allPackageComponents =
        PythonUtil.getAllComponents(
            buildTarget,
            projectFilesystem,
            params,
            resolver,
            ruleFinder,
            PythonUtil.getDeps(pythonPlatform, cxxPlatform, args.getDeps(), args.getPlatformDeps())
                .stream()
                .map(resolver::getRule)
                .collect(MoreCollectors.toImmutableList()),
            binaryPackageComponents,
            pythonPlatform,
            cxxBuckConfig,
            cxxPlatform,
            args.getLinkerFlags()
                .stream()
                .map(
                    MacroArg.toMacroArgFunction(
                            PythonUtil.MACRO_HANDLER, buildTarget, cellRoots, resolver)
                        ::apply)
                .collect(MoreCollectors.toImmutableList()),
            pythonBuckConfig.getNativeLinkStrategy(),
            args.getPreloadDeps());
    return createPackageRule(
        buildTarget,
        projectFilesystem,
        params,
        resolver,
        ruleFinder,
        pythonPlatform,
        cxxPlatform,
        mainModule,
        args.getExtension(),
        allPackageComponents,
        args.getBuildArgs(),
        args.getPackageStyle().orElse(pythonBuckConfig.getPackageStyle()),
        PythonUtil.getPreloadNames(resolver, cxxPlatform, args.getPreloadDeps()));
  }

  @Override
  public void findDepsForTargetFromConstructorArgs(
      BuildTarget buildTarget,
      CellPathResolver cellRoots,
      AbstractPythonBinaryDescriptionArg constructorArg,
      ImmutableCollection.Builder<BuildTarget> extraDepsBuilder,
      ImmutableCollection.Builder<BuildTarget> targetGraphOnlyDepsBuilder) {
    // We need to use the C/C++ linker for native libs handling, so add in the C/C++ linker to
    // parse time deps.
    extraDepsBuilder.addAll(getCxxPlatform(buildTarget, constructorArg).getLd().getParseTimeDeps());

    if (constructorArg.getPackageStyle().orElse(pythonBuckConfig.getPackageStyle())
        == PythonBuckConfig.PackageStyle.STANDALONE) {
      Optionals.addIfPresent(pythonBuckConfig.getPexTarget(), extraDepsBuilder);
      Optionals.addIfPresent(pythonBuckConfig.getPexExecutorTarget(), extraDepsBuilder);
    }
  }

  @BuckStyleImmutable
  @Value.Immutable
  interface AbstractPythonBinaryDescriptionArg
      extends CommonDescriptionArg, HasDeclaredDeps, HasTests, HasVersionUniverse {
    Optional<SourcePath> getMain();

    Optional<String> getMainModule();

    @Value.Default
    default PatternMatchedCollection<ImmutableSortedSet<BuildTarget>> getPlatformDeps() {
      return PatternMatchedCollection.of();
    }

    Optional<String> getBaseModule();

    Optional<Boolean> getZipSafe();

    ImmutableList<String> getBuildArgs();

    Optional<String> getPlatform();

    Optional<Flavor> getCxxPlatform();

    Optional<PythonBuckConfig.PackageStyle> getPackageStyle();

    ImmutableSet<BuildTarget> getPreloadDeps();

    ImmutableList<String> getLinkerFlags();

    Optional<String> getExtension();
  }
}
