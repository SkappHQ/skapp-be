FROM eclipse-temurin:22-jdk-ubi9-minimal
COPY target/skapp-0.0.1.jar skapp.jar
EXPOSE 8080
CMD ["java","-jar","skapp.jar"]
