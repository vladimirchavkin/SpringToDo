FROM openjdk:21
WORKDIR /app
COPY ./target/SpringToDo-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8088
CMD ["java","-jar","app.jar"]