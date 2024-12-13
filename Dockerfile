FROM openjdk:21-jdk
COPY target/skapp-0.0.1.jar skapp.jar
EXPOSE 8080
CMD ["java","-jar","skapp-1.0.0.jar"]
