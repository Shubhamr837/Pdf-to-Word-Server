package com.pdfoffice.pdftoword.pdftoword.service;

import com.pdfoffice.pdftoword.pdftoword.model.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/convert/pdf")
public class WordtoPdf {
    @RequestMapping(method = RequestMethod.POST, value = "/doc")
    public ResponseEntity<Response> pdfToDocx(InputStream in) {

        return ResponseEntity.notFound().header("Content-Type", "application/json").build();

    }
}
