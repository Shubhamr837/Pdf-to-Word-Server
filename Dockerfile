FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV AWS_ACCESS_ID=AKIASTFVKUSN5V7VHUWZ
ENV AWS_SECRET_KEY=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
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
