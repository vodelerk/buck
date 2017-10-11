/*
 * Copyright 2017-present Facebook, Inc.
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

package com.facebook.buck.rules;

import org.immutables.value.Value;

/**
 * Identifies a class that uses {@link AddToRuleKey} annotations to indicate fields that should be
 * added to rule keys.
 *
 * <p>{@link Value.Immutable} annotated classes can use {@link AddToRuleKey} on methods to indicate
 * that the method's return value should be added to rule keys.
 */
public interface AddsToRuleKey {}