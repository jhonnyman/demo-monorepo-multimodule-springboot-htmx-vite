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
package com.demo.app.infra.storage;

import io.minio.MinioClient;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioManager {

  private String url;

  private String accessKey;

  private String accessSecret;

  private String defaultBucket;

  @Inject
  public MinioManager(
      @Value("${minio.url}") String url,
      @Value("${minio.access.name}") String accessKey,
      @Value("${minio.access.secret}") String accessSecret,
      @Value("${minio.bucket.name}") String defaultBucket) {
    this.url = url;
    this.accessKey = accessKey;
    this.accessSecret = accessSecret;
    this.defaultBucket = defaultBucket;
  }

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder().endpoint(url).credentials(accessKey, accessSecret).build();
  }

  public String getDefaultBucket() {
    return defaultBucket;
  }
}
