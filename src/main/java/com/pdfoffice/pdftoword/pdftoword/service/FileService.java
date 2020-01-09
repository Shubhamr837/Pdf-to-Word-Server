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

    public File createFile(String file_name) {

        return new File(System.getProperty("user.dir"), file_name);
    }
    public void getFile(){

    }
}
