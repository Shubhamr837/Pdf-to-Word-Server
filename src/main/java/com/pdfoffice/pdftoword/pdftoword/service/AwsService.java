package com.pdfoffice.pdftoword.pdftoword.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pdfoffice.pdftoword.pdftoword.utils.CommonConstants;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

@Component
public class AwsService {
    private AWSCredentials credentials;
    private AmazonS3 s3Client;

    public AwsService() {
        credentials = new BasicAWSCredentials(System.getenv("AWS_ACCESS_ID"), System.getenv("AWS_SECRET_KEY"));
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public URL uploadFile(File file) {
        URL url;

        try {
            // Set the presigned URL to expire after one hour.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            s3Client.putObject(
                    new PutObjectRequest(CommonConstants.BUCKET_NAME, file.getName(), file)
                            .withCannedAcl(CannedAccessControlList.Private));

            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(CommonConstants.BUCKET_NAME, file.getName())
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

            System.out.println("Pre-Signed URL: " + url.toString());
            file.delete();
            return url;
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
