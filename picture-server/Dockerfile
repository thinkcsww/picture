FROM mdock.daumkakao.io/library/eclipse-temurin:8-jre-focal AS app
COPY ./target/picture-server-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-Xms4g", "-Xmx4g", "-jar", "picture-server-0.0.1-SNAPSHOT.jar"]

