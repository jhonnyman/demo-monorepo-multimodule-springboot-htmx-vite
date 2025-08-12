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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class I18n implements WebMvcConfigurer {

  public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
  public static final String LANGUAGE_QUERY_PARAM = "lang";
  public static final String MESSAGE_BUNDLE_ENCODING = StandardCharsets.UTF_8.name();
  public static final String MESSAGE_BUNDLE_PREFIX = "messages/msg";

  public static final List<Locale> SUPPORTED_LOCALES = List.of(Locale.ENGLISH, Locale.of("pt"));

  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    localeResolver.setDefaultLocale(DEFAULT_LOCALE);
    localeResolver.setSupportedLocales(SUPPORTED_LOCALES);
    return localeResolver;
  }

  /**
   * Overrides the translation via query param
   *
   * @return
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName(LANGUAGE_QUERY_PARAM);
    return lci;
  }

  /** Add interceptor to change locale during requests */
  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

  /**
   * Configures the message source. For better configuration experience we're using the {@link
   * IcuResourceBundleMessageSource}.
   *
   * @return
   */
  @Bean
  public MessageSource messageSource() {
    IcuResourceBundleMessageSource messageSource = new IcuResourceBundleMessageSource();
    messageSource.setBasename(MESSAGE_BUNDLE_PREFIX);
    messageSource.setDefaultEncoding(MESSAGE_BUNDLE_ENCODING);
    return messageSource;
  }
}
