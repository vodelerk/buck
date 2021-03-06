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

package com.facebook.buck.artifact_cache;

import com.facebook.buck.event.BuckEventBus;
import com.facebook.buck.io.filesystem.ProjectFilesystem;
import com.facebook.buck.slb.HttpService;
import com.facebook.buck.util.immutables.BuckStyleImmutable;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@BuckStyleImmutable
interface AbstractNetworkCacheArgs {
  String getCacheName();

  ArtifactCacheMode getCacheMode();

  String getRepository();

  String getScheduleType();

  HttpService getFetchClient();

  HttpService getStoreClient();

  CacheReadMode getCacheReadMode();

  ProjectFilesystem getProjectFilesystem();

  BuckEventBus getBuckEventBus();

  ListeningExecutorService getHttpFetchExecutorService();

  ListeningExecutorService getHttpWriteExecutorService();

  String getErrorTextTemplate();

  Optional<Long> getMaxStoreSizeBytes();
}
