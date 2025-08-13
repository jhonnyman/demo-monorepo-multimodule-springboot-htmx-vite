package com.demo.app.presentation.views;

import com.demo.app.infra.storage.MinioManager;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TestimonyView {
  Logger logger = Logger.getLogger(TestimonyView.class.getName());

  private MinioManager minioConfig;

  public TestimonyView(MinioManager minioConfig) {
    this.minioConfig = minioConfig;
  }

  @GetMapping("/testimony/{fileName}")
  public String getFile(@PathVariable String fileName, Model model) {
    try {
      // Create a signed URL valid for 1 hour
      String fileUrl =
          minioConfig
              .minioClient()
              .getPresignedObjectUrl(
                  GetPresignedObjectUrlArgs.builder()
                      .method(Method.GET)
                      .bucket(minioConfig.getDefaultBucket())
                      .object(fileName)
                      .expiry(1, TimeUnit.HOURS)
                      .build());

      model.addAttribute("fileUrl", fileUrl);
      model.addAttribute("fileName", fileName);
      return "pages/testimony-view"; // Thymeleaf template
    } catch (Exception e) {
      model.addAttribute("error", "File not found or error retrieving it.");
      return "common/error";
    }
  }
}
