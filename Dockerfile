FROM eclipse-temurin:22-jdk-ubi9-minimal
WORKDIR /app
COPY target/skapp-0.0.1.jar skapp-0.0.1.jar
EXPOSE 8080
CMD ["java","-jar","skapp-0.0.1.jar"]