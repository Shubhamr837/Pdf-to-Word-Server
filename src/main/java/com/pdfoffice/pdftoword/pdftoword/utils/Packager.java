package com.pdfoffice.pdftoword.pdftoword.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Packager {
    public static void packZip(File output, List<File> sources) throws IOException {
        System.out.println("Packaging to " + output.getName());
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(output));
        zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

        for (File source : sources) {
            if (source.isDirectory()) {
                zipDir(zipOut, "", source);
            } else {
                zipFile(zipOut, "", source);
            }
            source.delete();
        }
        zipOut.flush();
        zipOut.close();
        System.out.println("Done");
    }

    private static String buildPath(String path, String file) {
        if (path == null || path.isEmpty()) {
            return file;
        } else {
            return path + "/" + file;
        }
    }

    private static void zipDir(ZipOutputStream zos, String path, File dir) throws IOException {
        if (!dir.canRead()) {
            System.out.println("Cannot read " + dir.getCanonicalPath() + " (maybe because of permissions)");
            return;
        }

        File[] files = dir.listFiles();
        path = buildPath(path, dir.getName());
        System.out.println("Adding Directory " + path);

        for (File source : files) {
            if (source.isDirectory()) {
                zipDir(zos, path, source);
            } else {
                zipFile(zos, path, source);
            }
        }

        System.out.println("Leaving Directory " + path);
    }

    private static void zipFile(ZipOutputStream zos, String path, File file) throws IOException {
        if (!file.canRead()) {
            System.out.println("Cannot read " + file.getCanonicalPath() + " (maybe because of permissions)");
            return;
        }

        System.out.println("Compressing " + file.getName());
        zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));

        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[4092];
        int byteCount = 0;
        while ((byteCount = fis.read(buffer)) != -1) {
            zos.write(buffer, 0, byteCount);
            System.out.print('.');
            System.out.flush();
        }
        System.out.println();

        fis.close();
        zos.closeEntry();
    }

    public static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}