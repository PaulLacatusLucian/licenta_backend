package com.cafeteria.cafeteria_plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class CafeteriaPluginApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafeteriaPluginApplication.class, args);

		File uploadDir = new File("uploads/");
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
	}

}

