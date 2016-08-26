/*
 * Copyright 2016-present Facebook, Inc.
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

package com.facebook.buck.jvm.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.facebook.buck.io.ProjectFilesystem;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.BuildTargetFactory;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.DefaultTargetNodeToBuildRuleTransformer;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.rules.SourcePaths;
import com.facebook.buck.rules.TargetGraph;
import com.facebook.buck.shell.GenruleBuilder;
import com.facebook.buck.testutil.FakeProjectFilesystem;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nullable;

/**
 * Tests for the classpath provider facilities.
 */
public class JavaLibraryClasspathProviderTest {

  private BuildRule a;
  private BuildRule b;
  private BuildRule c;
  private BuildRule d;
  private BuildRule e;
  private SourcePathResolver resolver;
  private Path basePath;
  private Function<Path, SourcePath> sourcePathFunction;

  @Before
  public void setUp() throws Exception {
    ProjectFilesystem filesystem = new FakeProjectFilesystem();
    BuildRuleResolver ruleResolver = new BuildRuleResolver(
        TargetGraph.EMPTY,
        new DefaultTargetNodeToBuildRuleTransformer());
    resolver = new SourcePathResolver(ruleResolver);
    basePath = filesystem.getRootPath();

    // Create our target graph. All nodes are JavaLibrary except b
    //              a   (exports c)
    //            /  \
    //(non java) b    c (exports e)
    //           |    |
    //           d    e
    d = makeRule("//foo:d",
        ImmutableSet.of("foo", "d.java"),
        ImmutableSet.<BuildRule>of(),
        ruleResolver,
        filesystem);

    sourcePathFunction = SourcePaths.toSourcePath(filesystem);
    b = GenruleBuilder.newGenruleBuilder(BuildTargetFactory.newInstance("//foo:b"))
        .setSrcs(ImmutableList.of(
            sourcePathFunction.apply(Paths.get("foo", "b.java"))))
        .setCmd("echo $(classpath //foo:d")
        .setOut("b.out")
        .build(ruleResolver);

    e = makeRule("//foo:e",
        ImmutableSet.of("foo", "e.java"),
        ImmutableSet.<BuildRule>of(),
        ruleResolver,
        filesystem);

    // exported
    c = makeRule("//foo:c",
        ImmutableSet.of("foo", "c.java"),
        ImmutableSet.of(e),
        ImmutableSet.of(e),  // exported
        ruleResolver,
        filesystem);

    a = makeRule("//foo:a",
        ImmutableSet.of("foo", "a.java"),
        ImmutableSet.of(b, c),
        ImmutableSet.of(c),
        ruleResolver,
        filesystem);

  }

  @Test
  public void getOutputClasspathEntries() throws Exception {
    JavaLibrary aLib = (JavaLibrary) a;
    assertEquals(
        ImmutableSet.of(
            getFullOutput(a),
            getFullOutput(c), // a exports c
            getFullOutput(e)  // c exports e
        ),
        JavaLibraryClasspathProvider.getOutputClasspathJars(
            aLib,
            resolver,
            Optional.of(sourcePathFunction.apply(aLib.getPathToOutput())))
    );
  }

  @Test
  public void getClasspathEntries() throws Exception {
    assertEquals(
        ImmutableSetMultimap.builder()
            .put(a, getFullOutput(a))
            .put(a, getFullOutput(c))  // a exports c
            .put(c, getFullOutput(e))  // c exports e
            .put(a, getFullOutput(e))  // so a transitively has e
            .put(c, getFullOutput(c))
            .put(e, getFullOutput(e))
            // b is non-java so b and d do not appear
            .build(),
        JavaLibraryClasspathProvider.getClasspathEntries(ImmutableSet.of(a))
    );

    assertEquals(
        ImmutableSetMultimap.of(
            c, getFullOutput(c),
            c, getFullOutput(e), // c exports e
            e, getFullOutput(e),
            d, getFullOutput(d)
            // b is non-java so b and d do not appear
        ),
        JavaLibraryClasspathProvider.getClasspathEntries(ImmutableSet.of(c, d))
    );
  }

  @Test
  public void getClasspathDeps() {
    assertEquals(
        ImmutableSet.of(
            a, c, e
        ),
        JavaLibraryClasspathProvider.getClasspathDeps(ImmutableSet.of(a))
    );

    assertEquals(
        ImmutableSet.of(
            d, c, e
        ),
        JavaLibraryClasspathProvider.getClasspathDeps(ImmutableSet.of(d, c))
    );
  }

  @Test
  public void getTransitiveClasspathEntries() throws Exception {
    JavaLibrary aLib = (JavaLibrary) a;
    assertEquals(
        ImmutableSetMultimap.builder()
            .put(a, getFullOutput(a))
            .put(a, getFullOutput(c))  // a exports c
            .put(c, getFullOutput(e))  // c exports e
            .put(a, getFullOutput(e))  // so a transitively has e
            .put(c, getFullOutput(c))
            .put(e, getFullOutput(e))
            // b is non-java so b and d do not appear
            .build(),
        JavaLibraryClasspathProvider.getTransitiveClasspathEntries(
            aLib, resolver, Optional.of(sourcePathFunction.apply(aLib.getPathToOutput())))
    );
  }

  @Test
  public void getTransitiveClasspathDeps() throws Exception {
    // Add one transitive dep with its own dep, one maven-coordinate, one empty
    ProjectFilesystem filesystem = FakeProjectFilesystem.createJavaOnlyFilesystem();
    BuildRuleResolver ruleResolver = new BuildRuleResolver(
        TargetGraph.EMPTY,
        new DefaultTargetNodeToBuildRuleTransformer());
    BuildRule mavenCoord =
        JavaLibraryBuilder.createBuilder(BuildTargetFactory.newInstance("//:maven-coord"), filesystem)
            .setMavenCoords("group:identifer:1.0")
            .build(ruleResolver);
    BuildRule noOutput = makeRule(
        "//:empty",
        ImmutableSet.<String>of(),
        ImmutableSet.<BuildRule>of(mavenCoord),
        ruleResolver,
        filesystem);
    JavaLibrary dep = makeRule(
        "//:dep",
        ImmutableSet.of("Foo.java"),
        ImmutableSet.<BuildRule>of(),
        ruleResolver,
        filesystem);
    JavaLibrary root = makeRule(
        "//:root",
        ImmutableSet.<String>of(),
        ImmutableSet.of(noOutput, dep),
        ruleResolver,
        filesystem);


    assertEquals(
        ImmutableSet.of(
            dep, mavenCoord
        ),
        JavaLibraryClasspathProvider.getTransitiveClasspathDeps(
            root) // Root does not have an output jar
    );

    assertEquals(
        ImmutableSet.of(
            dep
        ),
        JavaLibraryClasspathProvider.getTransitiveClasspathDeps(
            dep)); // Whereas dep does have an output jar
  }

  @Test
  public void getDeclaredClasspathEntries() throws Exception {
    JavaLibrary aLib = (JavaLibrary) a;
    assertEquals(
        ImmutableSetMultimap.builder()
            .put(c, getFullOutput(c))
            .put(c, getFullOutput(e))  // c exports e
            // b is non-java so b and d do not appear
            .build(),
        JavaLibraryClasspathProvider.getDeclaredClasspathEntries(aLib)
    );
  }

  @Test
  public void getJavaLibraryDeps() throws Exception {
    assertThat(
        JavaLibraryClasspathProvider.getJavaLibraryDeps(ImmutableList.of(a, b, c, d, e)),
        Matchers.containsInAnyOrder(a, c, d, e)
    );
  }

  private Path getFullOutput(BuildRule lib) {
    return basePath.resolve(lib.getPathToOutput().toString());
  }

  private static JavaLibrary makeRule(
      String target,
      Iterable<String> srcs,
      Iterable<BuildRule> deps,
      BuildRuleResolver ruleResolver,
      ProjectFilesystem filesystem) throws Exception {
    return makeRule(target, srcs, deps, null, ruleResolver, filesystem);
  }

  private static JavaLibrary makeRule(
      String target,
      Iterable<String> srcs,
      Iterable<BuildRule> deps,
      @Nullable Iterable<BuildRule> exportedDeps,
      BuildRuleResolver ruleResolver,
      final ProjectFilesystem filesystem) throws Exception {
    JavaLibraryBuilder builder;
    BuildTarget parsedTarget = BuildTargetFactory.newInstance(target);
    builder = JavaLibraryBuilder.createBuilder(parsedTarget);

    for (String src : srcs) {
      builder.addSrc(filesystem.getBuckPaths().getGenDir().resolve(src));
    }
    for (BuildRule dep : deps) {
      builder.addDep(dep.getBuildTarget());
    }

    if (exportedDeps != null) {
      for (BuildRule dep : exportedDeps) {
        builder.addExportedDep(dep.getBuildTarget());
      }
    }
    return (JavaLibrary) ruleResolver.addToIndex(builder.build(ruleResolver));
  }
}