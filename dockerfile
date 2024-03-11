FROM amazoncorretto:17.0.7-alpine
ARG JAR_FILE=target/bankNew-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]