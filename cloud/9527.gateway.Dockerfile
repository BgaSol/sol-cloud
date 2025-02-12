FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM amazoncorretto:17.0.14
WORKDIR /app
COPY --from=builder /app/gateway-9527/target/*.jar /app/gateway-9527.jar
EXPOSE 9527
CMD ["java", "-jar", "gateway-9527.jar"]