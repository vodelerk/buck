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

package com.example;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;

public class SupWorld {

  private SupWorld() {}

  public static void main(String... args) throws IOException {
    URL url = SupWorld.class.getResource("/com/another/supworld.txt");
    try (InputStream inputStream = url.openStream()) {
      int ch;
      while ((ch = inputStream.read()) != -1) {
        System.out.print((char) ch);
      }
    }
  }
}
