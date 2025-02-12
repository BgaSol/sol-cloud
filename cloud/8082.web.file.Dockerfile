FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM amazoncorretto:17.0.14
WORKDIR /app
COPY --from=builder /app/web/web-file-8082/target/classes /app/classes
COPY --from=builder /app/web/web-file-8082/target/*.jar /app/web-file-8082.jar
EXPOSE 8082
CMD ["java", "-jar", "web-file-8082.jar"]