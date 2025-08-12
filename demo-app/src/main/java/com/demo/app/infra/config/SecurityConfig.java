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

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private DemoAppEnvVars envVars;

  public SecurityConfig(DemoAppEnvVars envVars) {
    this.envVars = envVars;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/**")
                    .fullyAuthenticated()
                    .requestMatchers("/csrf")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
        .oauth2Login(Customizer.withDefaults())
        .csrf(
            csrf ->
                // Stores CSRF in a cookie not accessible via
                // Javascript as all requests shall be driven via HTMX
                csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
                    // Default configuration for BREACH protection
                    .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
                    // Disable for API endpoints as it does not make sense
                    .ignoringRequestMatchers("/api/**"))
        .logout(
            logout ->
                logout
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
                          boolean isJson =
                              request.getHeader("Accept") != null
                                  && request.getHeader("Accept").contains("application/json");

                          if (isHtmx) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setHeader("HX-Redirect", "/login");
                          } else if (isJson) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response
                                .getWriter()
                                .write("{\"error\":\"unauthorized\",\"redirect\":\"/login\"}");
                          } else {
                            response.sendRedirect(envVars.getOauthRedirectUri());
                          }
                        })
                    .accessDeniedPage("/access-denied"));
    return http.build();
  }
}
