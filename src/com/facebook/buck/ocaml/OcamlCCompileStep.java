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

package com.facebook.buck.ocaml;

import com.facebook.buck.cxx.CxxHeaders;
import com.facebook.buck.rules.AddToRuleKey;
import com.facebook.buck.rules.AddsToRuleKey;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.rules.Tool;
import com.facebook.buck.rules.args.Arg;
import com.facebook.buck.shell.ShellStep;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.util.Escaper;
import com.facebook.buck.util.RichStream;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/** Compilation step for C interoperability files. */
public class OcamlCCompileStep extends ShellStep {

  private final SourcePathResolver resolver;
  private final Args args;

  OcamlCCompileStep(SourcePathResolver resolver, Path workingDirectory, Args args) {
    super(workingDirectory);
    this.resolver = resolver;
    this.args = args;
  }

  @Override
  public String getShortName() {
    return "OCaml C compile";
  }

  @Override
  protected ImmutableList<String> getShellCommandInternal(ExecutionContext context) {
    ImmutableList.Builder<String> cmd =
        ImmutableList.<String>builder()
            .addAll(args.ocamlCompiler.getCommandPrefix(resolver))
            .addAll(OcamlCompilables.DEFAULT_OCAML_FLAGS);

    if (args.stdlib.isPresent()) {
      cmd.add("-nostdlib", OcamlCompilables.OCAML_INCLUDE_FLAG, args.stdlib.get());
    }

    return cmd.add("-c")
        .add("-annot")
        .add("-bin-annot")
        .add("-cc", args.cCompiler.get(0))
        .addAll(
            RichStream.<String>empty()
                .concat(Stream.of("-Wall", "-Wextra", "-o", args.output.toString()))
                .concat(args.cCompiler.subList(1, args.cCompiler.size()).stream())
                .concat(Arg.stringify(args.cFlags, resolver).stream())
                // The ocaml compiler invokes the C compiler, along with these flags, using the
                // shell, so we have to pre-shell-escape them.
                .map(Escaper.BASH_ESCAPER::apply)
                .flatMap(f -> Stream.of("-ccopt", f))
                .toImmutableList())
        .add(resolver.getAbsolutePath(args.input).toString())
        .build();
  }

  @Override
  public ImmutableMap<String, String> getEnvironmentVariables(ExecutionContext context) {
    return args.environment;
  }

  public static class Args implements AddsToRuleKey {
    public final ImmutableMap<String, String> environment;
    @AddToRuleKey public final Tool ocamlCompiler;
    @AddToRuleKey public final ImmutableList<String> cCompiler;
    @AddToRuleKey public final ImmutableList<Arg> cFlags;
    @AddToRuleKey public final Optional<String> stdlib;

    @AddToRuleKey(stringify = true)
    public final Path output;

    @AddToRuleKey public final SourcePath input;
    @AddToRuleKey public final ImmutableList<CxxHeaders> includes;

    public Args(
        ImmutableMap<String, String> environment,
        ImmutableList<String> cCompiler,
        Tool ocamlCompiler,
        Optional<String> stdlib,
        Path output,
        SourcePath input,
        ImmutableList<Arg> cFlags,
        ImmutableList<CxxHeaders> includes) {
      this.environment = environment;
      this.cCompiler = cCompiler;
      this.ocamlCompiler = ocamlCompiler;
      this.stdlib = stdlib;
      this.output = output;
      this.input = input;
      this.cFlags = cFlags;
      this.includes = includes;
    }
  }
}
