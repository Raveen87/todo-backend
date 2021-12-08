FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

# copy the pom and src code to the container
COPY . ./

# package our application code
RUN mvn clean package

FROM openjdk:17.0.1-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar /todo-backend.jar

# set the startup command to execute the jar
CMD ["java", "-jar", "/todo-backend.jar"]
