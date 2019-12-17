package com.pdfoffice.pdftoword.pdftoword.service;

import com.pdfoffice.pdftoword.pdftoword.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class FileService {
    private ServiceConfig serviceConfig;
    @Autowired
    public FileService(ServiceConfig serviceConfig){
        this.serviceConfig = serviceConfig;
    }
    public File createFile(){
        return new File(System.getProperty("user.dir"));
    }
    public void getFile(){

    }
}
