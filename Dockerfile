FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV AWS_ACCESS_KEY_ID=AKIASTFVKUSNQXINESXB
ENV AWS_SECRET_ACCESS_KEY=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
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
