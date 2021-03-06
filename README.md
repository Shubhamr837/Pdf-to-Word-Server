# Pdf-to-Word-Server
Pdf to image and pdf to word conversion Spring boot application.
The kubernetes files can be found in the k8s folder .
the application uses Apache POI and Apache PDFbox libraries.

Prior knowledge of following are needed to run the application .

1 . kubernetes

2 . GCP or aws

3 . continous integration

This Container has to be deployed in Kubernetes with two other repository :-
https://github.com/Shubhamr837/Pdf-Office-Eureka-Server .
https://github.com/Shubhamr837/Pdf-Office-Zuul-Proxy .

Android App that uses this app for conversion:
https://github.com/Shubhamr837/Pdf_Office

The kubernetes deployment and service file are in k8 folder .
The application registers in the eureka server with name 'pdftoword' and the requests are routed through the zuul proxy application .

google cloud build is used for continous integration which you can configure accordingly for your account .
The image url in kubernetes deployment file also has to be configured accordingly.
The docker file is encrypted as it contains the environment variables for login in AWS for using aws s3 service .

To use this server create your Own Dockerfile With an AWS_ACCESS_ID and AWS_SECRET_KEY environment variables obtained from aws or it can be directly written in Code in package com.pdfoffice.pdftoword.pdftoword.utils.CommonConstants.
The aws s3 bucketname and region has to be configured in package com.pdfoffice.pdftoword.pdftoword.utils.CommonConstants

Following are the apis for this application .

The {server url} is {zuul-proxy-url}/pdftoword. The zuul proxy url is the url of the zuul proxy appliation which routes requests to this service . It can be obtained after the zuul proxy kubernetes service is run .

Converting PDF to Images.

http://{server url}/pdf/images

This api has to be called with POST method with header ("Content-Type", "application/x-binary; utf-8")
the body should be a binary file that is a PDF. 
It returns the json response which contains the download link (From aws s3) the file would be a zip file containing images.

Converting Images to PDF

http://{server url}/images/pdf

This api has to be called with POST method with header ("Content-Type", "application/x-binary; utf-8")
the body should be a binary file that is a zip file containing images . 
It returns the json response which contains the download link (From aws s3) which is a pdf.

Converting pdf to Docx

http://{server url}/pdf/docx

This api has to be called with POST method with header ("Content-Type", "application/x-binary; utf-8")
the body should be a binary which is a pdf file . 
It returns the json response which contains the download link (From aws s3) which would be a docx file.

Converting Docx to PDF

http://{server url}/docx/pdf

This api has to be called with POST method with header ("Content-Type", "application/x-binary; utf-8")
the body should be a binary which is a DOCX file . 
It returns the json response which contains the download link (From aws s3) which would be a PDF file.


