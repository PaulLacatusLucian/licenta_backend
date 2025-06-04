package com.cafeteria.cafeteria_plugin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File directory = new File(uploadDir);
        String absolutePath = directory.getAbsolutePath();

        String resourceLocation = "file:///" + absolutePath.replace("\\", "/");
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        System.out.println("Bilder-Resource-Handler wird konfiguriert mit Pfad: " + resourceLocation);

        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}