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

package com.facebook.buck.rules.keys;

import com.facebook.buck.model.BuckVersion;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.rules.RuleKeyAppendable;
import com.facebook.buck.rules.RuleKeyObjectSink;
import com.google.common.base.Preconditions;
import java.nio.file.Path;

public class RuleKeyFieldLoader {

  private final int seed;

  public RuleKeyFieldLoader(int seed) {
    this.seed = seed;
  }

  void setFields(RuleKeyObjectSink builder, BuildRule buildRule, RuleKeyType ruleKeyType) {
    // "." is not a valid first character for a field name, nor a valid character for rule attribute
    // name and so the following fields will never collide with other stuff.
    builder.setReflectively(".cache_key_seed", seed);
    builder.setReflectively(".target_name", buildRule.getBuildTarget().getFullyQualifiedName());
    builder.setReflectively(".build_rule_type", buildRule.getType());
    builder.setReflectively(".buckversion", BuckVersion.getVersion());
    builder.setReflectively(".rule_key_type", ruleKeyType);

    // We currently cache items using their full buck-out path, so make sure this is reflected in
    // the rule key.
    Path buckOutPath = buildRule.getProjectFilesystem().getBuckPaths().getConfiguredBuckOut();
    builder.setReflectively(".out", buckOutPath.toString());

    // Add in any extra details to the rule key via the rule's `appendToRuleKey` method.
    buildRule.appendToRuleKey(builder);

    // We used to require build rules to piggyback on the `RuleKeyAppendable` type to add in
    // additional details, but have since switched to using a method in the build rule class, so
    // error out if we see the `RuleKeyAppendable` being used improperly.
    Preconditions.checkArgument(!(builder instanceof RuleKeyAppendable));

    AlterRuleKeys.amendKey(builder, buildRule);
  }
}
