package com.pdfoffice.pdftoword.pdftoword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PdfToWordApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfToWordApplication.class, args);
	}

}
