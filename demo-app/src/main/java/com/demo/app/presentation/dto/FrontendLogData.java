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
package com.demo.app.presentation.dto;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public record FrontendLogData(
    LogLevel level, @Nonnull String message, Optional<UUID> userId, Optional<ErrorData> error) {

  public enum LogLevel {
    ERROR(Level.SEVERE),
    WARN(Level.WARNING),
    INFO(Level.INFO),
    DEBUG(Level.FINE);

    private Level javaLevel;

    private LogLevel(Level value) {
      this.javaLevel = value;
    }

    public Level getJavaLevel() {
      return javaLevel;
    }
  }

  public record ErrorData(
      String message,
      Optional<String> cause,
      Optional<String> stack,
      Optional<String> source,
      Optional<Integer> lineno,
      Optional<Integer> colno) {}

  public Level getJavaLevel() {
    return Level.parse(level.toString());
  }
}
