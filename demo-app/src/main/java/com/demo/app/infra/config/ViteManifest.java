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
package com.demo.app.infra.config;

import com.demo.app.infra.dto.ViteManifestEntry;
import com.demo.app.infra.dto.ViteManifestMapper;
import com.demo.app.infra.exceptions.BadConfigurationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Processes a Vite manifest.json and detects a given asset location based on the project path and
 * Vite generated hash suffix
 *
 * <p>{@see https://vite.dev/}
 */
@Component
public final class ViteManifest {
  Logger logger = Logger.getLogger(ViteManifest.class.getName());

  private final Map<String, ViteManifestEntry> manifest;

  public ViteManifest(DemoAppEnvVars envVars) throws BadConfigurationException {
    ObjectMapper mapper = new ObjectMapper();

    try (InputStream manifestStream = openManifestStream(envVars.getViteManifestLocation())) {
      logger.log(
          Level.CONFIG,
          String.format(
              "Attempting to load ViteManfiest from %s", envVars.getViteManifestLocation()),
          envVars.getViteManifestLocation());

      TypeReference<Map<String, Map<String, Object>>> typeRef =
          new TypeReference<Map<String, Map<String, Object>>>() {};
      Map<String, Map<String, Object>> raw = mapper.readValue(manifestStream, typeRef);
      manifestStream.close();

      this.manifest =
          raw.entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      (e) ->
                          ViteManifestMapper.mapRawEntryToViteManifestEntry(
                              e, envVars.getAppUrl())));

      logger.log(
          Level.CONFIG,
          String.format(
              "Succeeded loading and parsing Vite manifest at %s",
              envVars.getViteManifestLocation()));
    } catch (IOException e) {
      throw new BadConfigurationException(
          String.format(
              "Failed to load and parse manifest at %s", envVars.getViteManifestLocation()),
          e.getCause());
    }
  }

  private InputStream openManifestStream(String location) throws IOException {
    if (location.startsWith("classpath:")) {
      String path = location.substring("classpath:".length());
      InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
      if (stream == null) throw new IOException("Classpath resource not found: " + path);
      return stream;
    }
    return new FileInputStream(new File(location));
  }

  /**
   * Helper function that obtains the asset file location from Vite Manifest. It is used in
   * Thymeleaf to calculate a given javascript or css asset file location taking into account Vite
   * hash suffix for caching.
   *
   * @param entry
   * @return
   */
  public String asset(String entry) {
    return "/" + manifest.get(entry);
  }

  /**
   * Returns the entire manifest entry for a given source file
   *
   * @param entry
   * @return
   */
  public ViteManifestEntry manifestEntry(String entry) {
    return manifest.get(entry);
  }
}
