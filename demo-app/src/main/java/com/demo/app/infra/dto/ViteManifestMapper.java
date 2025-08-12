/*
 * Copyright 2025 jhonnyman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.demo.app.infra.dto;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ViteManifestMapper {

  public static ViteManifestEntry mapRawEntryToViteManifestEntry(
      Entry<String, Map<String, Object>> e, String appUrl) {
    var objectValue = e.getValue();
    List<String> imports =
        objectValue.get("imports") != null
            ? ((List<String>) objectValue.get("imports"))
                .stream().map(el -> appUrl + "/" + el).toList()
            : List.of();
    List<String> css =
        objectValue.get("css") != null
            ? ((List<String>) objectValue.get("css")).stream().map(el -> appUrl + "/" + el).toList()
            : List.of();

    return new ViteManifestEntry(
        appUrl + "/" + (String) objectValue.get("file"),
        (String) objectValue.get("name"),
        (String) objectValue.get("src"),
        (Boolean) objectValue.get("isEntry"),
        imports,
        css);
  }
}
