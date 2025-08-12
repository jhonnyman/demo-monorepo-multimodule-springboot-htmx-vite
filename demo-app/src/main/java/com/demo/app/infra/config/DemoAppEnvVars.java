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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DemoAppEnvVars {

  private String appUrl;
  private String viteManifestLocation;
  private String oauthRedirectUri;

  public DemoAppEnvVars() {}

  @Autowired
  public DemoAppEnvVars(Environment environment) {
    appUrl = environment.getProperty("app.url");
    viteManifestLocation = environment.getProperty("demo.vite.manifest-location");
    oauthRedirectUri =
        environment.getProperty("spring.security.oauth2.client.registration.keycloak.redirect-uri");
  }

  public String getViteManifestLocation() {
    return viteManifestLocation;
  }

  public String getOauthRedirectUri() {
    return oauthRedirectUri;
  }

  public String getAppUrl() {
    return appUrl;
  }
}
