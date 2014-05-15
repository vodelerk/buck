package org.openqa.selenium.buck.file;

import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.BuildTargets;
import com.facebook.buck.rules.AbstractBuildable;
import com.facebook.buck.rules.BuildContext;
import com.facebook.buck.rules.BuildableContext;
import com.facebook.buck.rules.RuleKey;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePaths;
import com.facebook.buck.step.Step;
import com.facebook.buck.step.fs.CopyStep;
import com.facebook.buck.step.fs.MakeCleanDirectoryStep;
import com.facebook.buck.step.fs.MkdirStep;
import com.facebook.buck.zip.ZipStep;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Zip extends AbstractBuildable {

  private final Path output;
  private final ImmutableSortedSet<SourcePath> sources;
  private final Path localPath;
  private final Path scratchDir;

  public Zip(BuildTarget target, ImmutableSortedSet<SourcePath> sources) {
    this.sources = Preconditions.checkNotNull(sources);

    Preconditions.checkNotNull(target);
    this.output = BuildTargets.getGenPath(target, "%s.zip");
    this.scratchDir = BuildTargets.getBinPath(target, "%s.zip.scratch");
    this.localPath = Paths.get(target.getBasePath());
  }

  @Override
  public Collection<Path> getInputsToCompareToOutput() {
    return SourcePaths.filterInputsToCompareToOutput(sources);
  }

  @Override
  public List<Step> getBuildSteps(BuildContext context, BuildableContext buildableContext) {
    ImmutableList.Builder<Step> commands = ImmutableList.builder();

    commands.add(new MakeCleanDirectoryStep(output.getParent()));
    commands.add(new MakeCleanDirectoryStep(scratchDir));

    Set<Path> createdDir = Sets.newHashSet();
    for (SourcePath source : sources) {
      Path path = source.resolve();

      Path localName = localPath.relativize(path);

      Path destination = scratchDir.resolve(localName);

      if (Files.isDirectory(path)) {
        if (createdDir.add(path)) {
          commands.add(new MkdirStep(destination));
        }
      } else {
        if (createdDir.add(path)) {
          commands.add(new MkdirStep(destination));
        }
        commands.add(CopyStep.forFile(path, destination));
      }
    }

    commands.add(new ZipStep(
            output,
            ImmutableSortedSet.<Path>of(),
            /* junk paths */ false,
            ZipStep.DEFAULT_COMPRESSION_LEVEL,
            scratchDir));

    return commands.build();
  }

  @Override
  public RuleKey.Builder appendDetailsToRuleKey(RuleKey.Builder builder) {
    return null;
  }

  @Override
  public Path getPathToOutputFile() {
    return output;
  }
}
