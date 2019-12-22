package com.pdfoffice.pdftoword.pdftoword.model;

public class Response {
    private String download_link;

    public Response(String download_link) {
        this.download_link = download_link;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link() {
        this.download_link = download_link;
    }
}
