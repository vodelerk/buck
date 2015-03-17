/*
 * Copyright 2015-present Facebook, Inc.
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

package com.facebook.buck.apple.xcode;

import com.facebook.buck.testutil.integration.DebuggableTemporaryFolder;
import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TestDataHelper;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class ProjectIntegrationTest {

  @Rule
  public DebuggableTemporaryFolder temporaryFolder = new DebuggableTemporaryFolder();

  @Test
  public void testBuckProjectGeneratedSchemeOnlyIncludesDependenciesWithoutTests()
      throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "project_generated_scheme_only_includes_dependencies",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand(
        "project",
        "--without-tests",
        "//Apps:workspace");
    result.assertSuccess();

    workspace.verify();
  }

  @Test
  public void testBuckProjectGeneratedSchemeIncludesTestsAndDependencies() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "project_generated_scheme_includes_tests_and_dependencies",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand(
        "project",
        "//Apps:workspace");
    result.assertSuccess();

    workspace.verify();
  }

  @Test
  public void testBuckProjectGeneratedSchemeIncludesTestsAndDependenciesInADifferentBuckFile()
      throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "project_generated_scheme_includes_tests_and_dependencies_in_a_different_buck_file",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand(
        "project",
        "//Apps:workspace");
    result.assertSuccess();

    workspace.verify();
  }

  @Test
  public void testBuckProjectGeneratedSchemesDoNotIncludeOtherTests()
      throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "project_generated_schemes_do_not_include_other_tests",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand("project");
    result.assertSuccess();

    workspace.verify();
  }

  @Test
  public void generatingAllWorkspacesWillNotIncludeAllProjectsInEachOfThem() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "generating_all_workspaces_will_not_include_all_projects_in_each_of_them",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand("project");
    result.assertSuccess();

    workspace.verify();
  }

  @Test
  public void generatingCombinedProject() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "generating_combined_project",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand(
        "project",
        "--combined-project",
        "--without-tests",
        "//Apps:workspace");
    result.assertSuccess();

    workspace.verify();
  }

  @Test
  public void generatingCombinedProjectWithTests() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this,
        "generating_combined_project_with_tests",
        temporaryFolder);
    workspace.setUp();

    ProjectWorkspace.ProcessResult result = workspace.runBuckCommand(
        "project",
        "--combined-project",
        "//Apps:workspace");
    result.assertSuccess();

    workspace.verify();
  }

}
