package com.pdfoffice.pdftoword.pdftoword.service;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
public class PdfToWordService {
    @Autowired
    private FileService fileService;

    @GetMapping("/doc")
    public String pdftoword(@RequestParam("file") MultipartFile file) throws IOException {
        File pdf_file = fileService.createFile("pdf");
        InputStream is = null;
        try {
            is = file.getInputStream();
            Files.copy(is, pdf_file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        PDDocument pdfDocument = null;
        pdfDocument = PDDocument.load(pdf_file);

        if (pdfDocument == null) {
            return "error";
        }
        PDFTextStripper pdfStripper = new PDFTextStripper();
        XWPFDocument document = new XWPFDocument();
        int no_of_pages = pdfDocument.getNumberOfPages();

        for (int i = 1; i <= no_of_pages; i++) {
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);
            pdfStripper.setSortByPosition(true);
            String text = pdfStripper.getText(pdfDocument);

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun para_run = paragraph.createRun();
            para_run.addBreak(BreakType.PAGE);
            para_run.setText(text);
        }
        File doc_file = fileService.createFile("doc");
        FileOutputStream out = new FileOutputStream(doc_file);
        document.write(out);
        document.close();
        pdfDocument.close();
        pdf_file.delete();

        return doc_file.getAbsolutePath();
    }
}
