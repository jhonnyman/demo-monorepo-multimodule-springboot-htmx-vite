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
package demo.vendors;

import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SpringBootConventionsPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getPluginManager().apply("org.springframework.boot");
    project.getPluginManager().apply("io.spring.dependency-management");
    project.getPluginManager().apply("java");
    project
        .getConfigurations()
        .all(
            config -> {
              config.exclude(
                  Map.of(
                      "group", "org.springframework.boot", "module", "spring-boot-starter-tomcat"));
            });
  }
}
