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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Authentication extends BaseController {

  @PostMapping("/logout")
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      @AuthenticationPrincipal OidcUser oidcUser)
      throws IOException, ServletException {
    String idToken = oidcUser.getIdToken().getTokenValue();
    request.logout();

    String logoutUrl =
        "http://localhost:8081/realms/demo-iam/protocol/openid-connect/logout"
            + "?id_token_hint="
            + idToken
            + "&post_logout_redirect_uri=https://localhost:8443/login/oauth2/code/keycloak";

    response.sendRedirect(logoutUrl);
  }

  @GetMapping("/login")
  public ResponseEntity<Void> login(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String keycloakUrl = "/oauth2/authorization/keycloak";

    if (isHtmxRequest(request)) {
      return ResponseEntity.ok().header("HX-Redirect", keycloakUrl).build();
    }

    response.sendRedirect(keycloakUrl);
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).build();
  }

  @GetMapping("/signup")
  public ResponseEntity<Void> signup(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String keycloakUrl = "/oauth2/authorization/keycloak?kc_action=register";

    if (isHtmxRequest(request)) {
      return ResponseEntity.ok().header("HX-Redirect", keycloakUrl).build();
    }

    response.sendRedirect(keycloakUrl);
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).build();
  }
}
