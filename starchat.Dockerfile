FROM openjdk:11-jre-slim
WORKDIR /starchat
COPY . /starchat
CMD ["java", "-jar", "target/starchat-1.0.0.jar"]
