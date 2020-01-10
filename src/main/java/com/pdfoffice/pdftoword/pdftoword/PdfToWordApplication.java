package com.pdfoffice.pdftoword.pdftoword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class PdfToWordApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfToWordApplication.class, args);
	}

	@RequestMapping(value = "/")
	public ResponseEntity healthcheck() {
		return ResponseEntity.ok().build();
	}

}