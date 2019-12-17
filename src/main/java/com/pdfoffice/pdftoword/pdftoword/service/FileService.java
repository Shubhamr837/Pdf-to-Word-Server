package com.pdfoffice.pdftoword.pdftoword.service;

import com.pdfoffice.pdftoword.pdftoword.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileService {
    private ServiceConfig serviceConfig;
    @Autowired
    public FileService(ServiceConfig serviceConfig){
        this.serviceConfig = serviceConfig;
    }

    public File createFile(String type) {
        return new File(System.getProperty("user.dir"), "document." + type);
    }
    public void getFile(){

    }
}
