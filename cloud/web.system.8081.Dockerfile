FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM amazoncorretto:17.0.14
WORKDIR /app
COPY --from=builder /app/web/web-system-8081/target/classes /app/classes
COPY --from=builder /app/web/web-system-8081/target/*.jar /app/web-system-8081.jar
EXPOSE 8081
CMD ["java", "-jar", "web-system-8081.jar"]