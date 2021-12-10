# https://spring.io/guides/topicals/spring-boot-docker/
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE

# If passing the JAR_FILE as a argument
# COPY ${JAR_FILE} app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]

# Hardcoded for Maven
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

# Building passing the JAR_FILE argument
# docker build --build-arg JAR_FILE=target/*.jar -t mborges/astra-springboot154 .

# Building when the JAR_FILE is hardcoded
# docker build -t mborges/astra-springboot154 .

# Running
# docker run -p 8080:8080 mborges/astra-springboot154