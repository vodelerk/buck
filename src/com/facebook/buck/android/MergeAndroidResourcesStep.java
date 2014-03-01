/*
 * Copyright 2012-present Facebook, Inc.
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

package com.facebook.buck.android;

import static com.google.common.collect.Ordering.natural;

import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.step.Step;
import com.facebook.buck.util.MoreStrings;
import com.facebook.buck.util.ProjectFilesystem;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MergeAndroidResourcesStep implements Step {

  private static final Pattern TEXT_SYMBOLS_LINE = Pattern.compile("(\\S+) (\\S+) (\\S+) (.+)");

  private final ImmutableMap<Path, String> symbolsFileToRDotJavaPackage;
  private final Path pathToGeneratedJavaFiles;

  /**
   * Merges text symbols files from {@code aapt} into R.java files that can be compiled.
   * @param symbolsFileToRDotJavaPackage For each entry in the map, the key is a path to a symbols
   *     file generated by {@code aapt} using the {@code --output-text-symbols} flag. The value is
   *     the Java package for the corresponding R.java file.
   * @param pathToGeneratedJavaFiles the directory where the generated R.java files should be
   *     written. Admittedly, this command could write such files to a {@code /tmp} directory, but
   *     it is convenient to have the R.java files written to a known location for debugging. This
   *     directory should exist and be empty before this command is run.
   */
  public MergeAndroidResourcesStep(
      Map<Path, String> symbolsFileToRDotJavaPackage,
      Path pathToGeneratedJavaFiles) {
    this.symbolsFileToRDotJavaPackage = ImmutableMap.copyOf(symbolsFileToRDotJavaPackage);
    this.pathToGeneratedJavaFiles = Preconditions.checkNotNull(pathToGeneratedJavaFiles);
  }

  @Override
  public int execute(ExecutionContext context) {
    try {
      doExecute(context);
      return 0;
    } catch (IOException e) {
      e.printStackTrace(context.getStdErr());
      return 1;
    }
  }

  private void doExecute(ExecutionContext context) throws IOException {
    // A symbols file may look like:
    //
    //    int id placeholder 0x7f020000
    //    int string debug_http_proxy_dialog_title 0x7f030004
    //    int string debug_http_proxy_hint 0x7f030005
    //    int string debug_http_proxy_summary 0x7f030003
    //    int string debug_http_proxy_title 0x7f030002
    //    int string debug_ssl_cert_check_summary 0x7f030001
    //    int string debug_ssl_cert_check_title 0x7f030000
    //
    // Note that there are four columns of information:
    // - the type of the resource id (always seems to be int or int[], in practice)
    // - the type of the resource
    // - the name of the resource
    // - the value of the resource id
    //
    // In order to convert this to R.java, all resources of the same type are grouped into a static
    // class of that name. The static class contains static values that correspond to the resource
    // (type, name, value) tuples.
    //
    // The first step is to merge symbol files of the same package type and resource type/name.
    // That is, within a package type, each resource type/name pair must be unique. If there are
    // multiple pairs, only one will be written to the R.java file.
    //
    // Because the resulting files do not match their respective resources.arsc, the values are
    // meaningless and do not represent the usable final result.  This is why the R.java file is
    // written without using final so that javac will not inline the values.  Unfortunately,
    // though Robolectric doesn't read resources.arsc, it does assert that all the R.java resource
    // ids are unique.  This forces us to re-enumerate new unique ids.
    SortedSetMultimap<String, Resource> rDotJavaPackageToResources = sortSymbols(
        symbolsFileToRDotJavaPackage,
        context.getProjectFilesystem(),
        true /* reenumerate */);

    // Create an R.java file for each package.
    for (String rDotJavaPackage : rDotJavaPackageToResources.keySet()) {
      // Create the content of R.java.
      SortedSet<Resource> resources = rDotJavaPackageToResources.get(rDotJavaPackage);

      // Write R.java in the pathToGeneratedJavaFiles directory. Admittedly, this will be written
      // to /tmp/com.example.stuff/R.java rather than /tmp/com/example/stuff/R.java. It turns out
      // that directory structure does not matter to javac.

      // Determine the path to R.java.
      File rDotJava = getOutputFile(pathToGeneratedJavaFiles, rDotJavaPackage);

      // Then write R.java to the output directory.
      Files.createParentDirs(rDotJava);

      try (BufferedWriter writer = Files.newWriter(rDotJava, Charsets.UTF_8)) {
        writeJavaCodeForPackageAndResources(
            new PrintWriter(writer),
            rDotJavaPackage,
            resources);
      }
    }
  }

  @VisibleForTesting
  static SortedSetMultimap<String, Resource> sortSymbols(
      Map<Path, String> symbolsFileToRDotJavaPackage,
      ProjectFilesystem filesystem,
      boolean reenumerate) {
    // If we're reenumerating, start at 0x7f01001 so that the resulting file is human readable.
    // This value range (0x7f010001 - ...) is easier to spot as an actual resource id instead of
    // other values in styleable which can be enumerated integers starting at 0.
    IntEnumerator enumerator = reenumerate ? new IntEnumerator(0x7f01001) : null;
    SortedSetMultimap<String, Resource> rDotJavaPackageToSymbolsFiles = TreeMultimap.create();
    for (Map.Entry<Path, String> entry : symbolsFileToRDotJavaPackage.entrySet()) {
      Path symbolsFile = entry.getKey();

      // Read the symbols file and parse each line as a Resource.
      List<String> linesInSymbolsFile;
      try {
        linesInSymbolsFile = FluentIterable.from(filesystem.readLines(symbolsFile))
            .filter(MoreStrings.NON_EMPTY)
            .toList();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      String packageName = entry.getValue();
      for (String line : linesInSymbolsFile) {
        Matcher matcher = TEXT_SYMBOLS_LINE.matcher(line);
        boolean isMatch = matcher.matches();
        Preconditions.checkState(isMatch, "Should be able to match '%s'.", line);
        String idType = matcher.group(1);
        String type = matcher.group(2);
        String name = matcher.group(3);
        String idValue = matcher.group(4);

        // We're only doing the remapping so Roboelectric is happy and it is already ignoring the
        // id references found in the styleable section.  So let's do that as well so we don't have
        // to get fancier than is needed.  That is, just re-enumerate all app-level resource ids
        // and ignore everything else, allowing the styleable references to be messed up.
        String idValueToUse = idValue;
        if (reenumerate && idValue.startsWith("0x7f")) {
          idValueToUse = String.format("0x%08x", enumerator.next());
        }

        Resource resource = new Resource(idType, type, name, idValue, idValueToUse);
        rDotJavaPackageToSymbolsFiles.put(packageName, resource);
      }
    }
    return rDotJavaPackageToSymbolsFiles;
  }

  public static String generateJavaCodeForPackageWithoutResources(String packageName) {
    return generateJavaCodeForPackageAndResources(packageName, ImmutableSortedSet.<Resource>of());
  }

  public static String generateJavaCodeForPackageAndResources(String packageName,
      SortedSet<Resource> resources) {
    StringBuilder b = new StringBuilder();

    try (PrintWriter writer = new PrintWriter(CharStreams.asWriter(b))) {
      writeJavaCodeForPackageAndResources(writer, packageName, resources);
    } catch (IOException e) {
      // Impossible.
      throw new RuntimeException(e);
    }
    return b.toString();
  }

  /**
   * Writes an intermediate R.java with dummy values influenced by the also dummy values created by
   * {@code aapt} when building intermediate artifacts.
   *
   * @param writer Output writer for the Java source.
   * @param packageName Package of the resulting R.java file.
   * @param resources Sorted set of resources parsed from R.txt.  First sorted by type then name.
   */
  private static void writeJavaCodeForPackageAndResources(
      PrintWriter writer,
      String packageName,
      SortedSet<Resource> resources) throws IOException {
    Preconditions.checkNotNull(writer);
    Preconditions.checkNotNull(packageName);
    Preconditions.checkNotNull(resources);

    writer.append("package ").append(packageName).append(';').print('\n');
    writer.print('\n');
    writer.print("public class R {\n");
    writer.print('\n');

    String lastType = null;
    for (Resource res : resources) {
      String type = res.type;
      if (!type.equals(lastType)) {
        // If the previous type needs, to be closed, then close it.
        if (lastType != null) {
          writer.print("  }\n");
          writer.print('\n');
        }

        // Now start the block for the new type.
        writer.append("  public static class ").append(type).append(" {").print('\n');
        lastType = type;
      }

      // Write out the resource.
      // Write as an int.
      writer.print(String.format("    public static %s %s=%s;\n",
          res.idType,
          res.name,
          res.idValueToWrite));
    }

    // If some type was written (e.g., the for loop was entered), then the last type needs to be
    // closed.
    if (lastType != null) {
      writer.print("  }\n");
      writer.print('\n');
    }

    // Close the class definition.
    writer.print("}\n");
  }

  public static Path getOutputFilePath(Path pathToGeneratedJavaFiles, String rDotJavaPackage) {
    return getOutputFile(pathToGeneratedJavaFiles, rDotJavaPackage).toPath();
  }

  private static File getOutputFile(Path pathToGeneratedJavaFiles, String rDotJavaPackage) {
    return pathToGeneratedJavaFiles.resolve(rDotJavaPackage).resolve("R.java").toFile();
  }

  /** Represents a row from a symbols file generated by {@code aapt}. */
  @VisibleForTesting
  static class Resource implements Comparable<Resource> {
    @VisibleForTesting final String idType;
    @VisibleForTesting final String type;
    @VisibleForTesting final String name;
    @VisibleForTesting final String originalIdValue;
    @VisibleForTesting final String idValueToWrite;

    public Resource(String idType, String type, String name, String originalIdValue,
        String idValueToWrite) {
      this.idType = Preconditions.checkNotNull(idType);
      this.type = Preconditions.checkNotNull(type);
      this.name = Preconditions.checkNotNull(name);
      this.originalIdValue = Preconditions.checkNotNull(originalIdValue);
      this.idValueToWrite = Preconditions.checkNotNull(idValueToWrite);
    }

    /**
     * A collection of Resources should be sorted such that Resources of the same type should be
     * grouped together, and should be alphabetized within that group.
     */
    @Override
    public int compareTo(Resource that) {
      return ComparisonChain.start()
          .compare(this.type, that.type)
          .compare(this.name, that.name)
          .result();
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Resource)) {
        return false;
      }

      Resource that = (Resource)obj;
      return Objects.equal(this.type, that.type) && Objects.equal(this.name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(type, name);
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(Resource.class)
          .add("idType", idType)
          .add("type", type)
          .add("name", name)
          .add("originalIdValue", originalIdValue)
          .add("idValueToWrite", idValueToWrite)
          .toString();
    }
  }

  @Override
  public String getShortName() {
    return "android-res-merge";
  }

  @Override
  public String getDescription(ExecutionContext context) {
    ImmutableList<String> sortedSymbolsFiles =
        FluentIterable.from(symbolsFileToRDotJavaPackage.keySet())
            .transform(Functions.toStringFunction())
            .toSortedList(natural());
    return getShortName() + " " + Joiner.on(' ').join(sortedSymbolsFiles)
        + " -o " + pathToGeneratedJavaFiles;
  }

  private static class IntEnumerator {
    private int value;

    public IntEnumerator(int start) {
      value = start;
    }

    public int next() {
      Preconditions.checkState(value < Integer.MAX_VALUE, "Stop goofing off");
      return value++;
    }
  }

}
