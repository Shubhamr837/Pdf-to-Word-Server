package com.pdfoffice.pdftoword.pdftoword.service;


import com.pdfoffice.pdftoword.pdftoword.model.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping(method = RequestMethod.POST, value = "/convert")
public class PdfToWordService {
    private String download_link;
    @Autowired
    private FileService fileService;
    @Autowired
    private AwsService awsService;

    @RequestMapping(method = RequestMethod.POST, value = "/docx")
    public ResponseEntity pdftoword(InputStream in) throws IOException {
        File pdf_file = fileService.createFile("pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(pdf_file);
        int len;
        byte[] buffer = new byte[1024];
        while (-1 != (len = in.read(buffer))) {
            fileOutputStream.write(buffer, 0, len);
        }
        try {
            Files.copy(in, pdf_file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        PDDocument pdfDocument = null;
        pdfDocument = PDDocument.load(pdf_file);
        pdf_file.delete();
        if (pdfDocument == null) {
            return ResponseEntity.badRequest().body(new Response(null));
        }
        PDFTextStripper pdfStripper = new PDFTextStripper();
        XWPFDocument document = new XWPFDocument();
        int no_of_pages = pdfDocument.getNumberOfPages();

        for (int i = 1; i <= no_of_pages; i++) {
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);
            pdfStripper.setSortByPosition(true);
            String text = pdfStripper.getText(pdfDocument);
            String[] lines = text.split(System.getProperty("line.separator"));

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun para_run = paragraph.createRun();


            for (String temp : lines) {
                para_run.setText(temp);
                para_run.addCarriageReturn();
            }


        }
        File doc_file = fileService.createFile(java.util.UUID.randomUUID().toString() + ".docx");
        FileOutputStream out = new FileOutputStream(doc_file);
        document.write(out);
        System.out.println(doc_file.getAbsolutePath());
        document.close();
        pdfDocument.close();
        out.close();
        download_link = awsService.uploadFile(doc_file).toString();

        return ResponseEntity.ok().header("Content-Type", "application/json").body(new Response(download_link));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/txt")
    public ResponseEntity pdfToTxt(InputStream in) throws IOException {
        File pdf_file = fileService.createFile("pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(pdf_file);
        int len;
        byte[] buffer = new byte[1024];
        while (-1 != (len = in.read(buffer))) {
            fileOutputStream.write(buffer, 0, len);
        }
        try {
            Files.copy(in, pdf_file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        PDDocument pdfDocument = null;
        pdfDocument = PDDocument.load(pdf_file);
        pdf_file.delete();
        if (pdfDocument == null) {
            return ResponseEntity.badRequest().body(new Response(null));
        }
        PDFTextStripper pdfStripper = new PDFTextStripper();

        String text = pdfStripper.getText(pdfDocument);
        File txt_file = fileService.createFile(java.util.UUID.randomUUID().toString() + ".txt");
        Files.write(Paths.get(txt_file.getAbsolutePath()), text.getBytes());

        pdfDocument.close();
        download_link = awsService.uploadFile(txt_file).toString();

        return ResponseEntity.ok().header("Content-Type", "application/json").body(new Response(download_link));
    }
}
