package com.pdfoffice.pdftoword.pdftoword.service;

import com.pdfoffice.pdftoword.pdftoword.model.Response;
import com.pdfoffice.pdftoword.pdftoword.utils.Packager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ImageToPdf {
    @Autowired
    FileService fileService;
    @Autowired
    AwsService awsService;
    private int i = 0;
    private PDPage page;
    private File pdf_file;
    private String download_link;

    @RequestMapping(method = RequestMethod.POST, value = "/images/pdf")
    public ResponseEntity<Response> ImagesToPdf(InputStream in) throws IOException {

        String file_name = java.util.UUID.randomUUID().toString();
        if (file_name.length() > 25)
            file_name = file_name.substring(0, 25);

        File zip_file = fileService.createFile(file_name + ".zip");
        System.out.println("created zipfile at " + zip_file.getAbsolutePath());
        FileOutputStream fileOutputStream = new FileOutputStream(zip_file);
        int len;
        byte[] buffer = new byte[1024];
        while (-1 != (len = in.read(buffer))) {
            fileOutputStream.write(buffer, 0, len);
        }
        try {
            Files.copy(in, zip_file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
        }

        new File(System.getProperty("user.dir") + "/" + file_name).mkdir();

        File temp_directory = new File(System.getProperty("user.dir") + "/" + file_name);
        Packager.unzip(zip_file.getAbsolutePath(), System.getProperty("user.dir") + "/" + file_name);
        zip_file.delete();
        File[] img_files = temp_directory.listFiles();


        try (PDDocument doc = new PDDocument()) {
            for (i = 0; i < img_files.length; i++) {  //we will add the image to the i page.
                InputStream inputStream = new FileInputStream(img_files[i]);
                BufferedImage bimg = ImageIO.read(inputStream);
                float width = bimg.getWidth();
                float height = bimg.getHeight();
                PDPage page = new PDPage(new PDRectangle(width, height));
                doc.addPage(page);
                page = doc.getPage(i);
                // createFromFile is the easiest way with an image file
                // if you already have the image in a BufferedImage,
                // call LosslessFactory.createFromImage() instead
                PDImageXObject pdImage = PDImageXObject.createFromFile(img_files[i].getAbsolutePath(), doc);

                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    // reduce this value if the image is too large
                    float scale = 1f;
                    contentStream.drawImage(pdImage, 0, 0);
                }
                img_files[i].delete();
            }

            pdf_file = fileService.createFile(file_name + ".pdf");

            doc.save(pdf_file);
            download_link = awsService.uploadFile(pdf_file).toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pdf_file.delete();
            temp_directory.delete();
        }

        return ResponseEntity.ok().header("Content-Type", "application/json").body(new Response(download_link));

    }

    @RequestMapping(method = RequestMethod.POST, value = "/pdf/images")
    public ResponseEntity<Response> PdfToImages(InputStream in) throws Exception {
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
        String dir_name = "temp";
        List<File> img_files = convertPdfToImage(pdf_file, dir_name);
        pdf_file.delete();
        File zip_file = fileService.createFile("temp.zip");
        Packager.packZip(zip_file, img_files);
        download_link = awsService.uploadFile(zip_file).toString();
        zip_file.delete();

        return ResponseEntity.ok().header("Content-Type", "application/json").body(new Response(download_link));
    }

    public List<File> convertPdfToImage(File file, String destination) throws Exception {

        File destinationFile = new File(destination);

        if (!destinationFile.exists()) {
            destinationFile.mkdir();
            System.out.println("DESTINATION FOLDER CREATED -> " + destinationFile.getAbsolutePath());
        } else if (destinationFile.exists()) {
            System.out.println("DESTINATION FOLDER ALLREADY CREATED!!!");
        } else {
            System.out.println("DESTINATION FOLDER NOT CREATED!!!");
        }

        if (file.exists()) {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            List<File> fileList = new ArrayList<File>();

            String fileName = file.getName().replace(".pdf", "");
            System.out.println("CONVERTER START.....");

            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                File fileTemp = new File(destination + fileName + "_" + i + ".jpg"); // jpg or png
                BufferedImage image = renderer.renderImageWithDPI(i, 200);
                // 200 is sample dots per inch.
                // if necessary, change 200 into another integer.
                ImageIO.write(image, "JPEG", fileTemp); // JPEG or PNG
                fileList.add(fileTemp);
            }
            doc.close();
            System.out.println("CONVERTER STOPTED.....");
            System.out.println("IMAGE SAVED AT -> " + destinationFile.getAbsolutePath());
            return fileList;
        } else {
            System.err.println(file.getName() + " FILE DOES NOT EXIST");
        }
        return null;
    }
}
