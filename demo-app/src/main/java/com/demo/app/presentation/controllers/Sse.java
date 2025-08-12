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
package com.demo.app.presentation.controllers;

import com.demo.app.infra.sse.SseManager;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class Sse {

  private final SseManager sseManager;

  public Sse(SseManager sseManager) {
    this.sseManager = sseManager;
  }

  @GetMapping("/sse/ping")
  public SseEmitter ping() {
    return sseManager.createEmiter(Optional.of("pong"));
  }

  @GetMapping("/sse/health")
  public ResponseEntity<Void> health() {
    return ResponseEntity.noContent().build();
  }
}
