FROM openjdk:8-jdk-alpine AS app
COPY ./target/picture-server-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xms4g", "-Xmx4g", "-jar", "picture-server-0.0.1-SNAPSHOT.jar"]

