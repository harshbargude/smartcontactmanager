#Use officila maven image to build the Spring Boot app
FROM maven:3.8.4-openjdk-17 AS build

# Set the working dir
WORKDIR /app

#copy pom.xml file and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

#Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

#USe an official OpenJDK image to run the applicatin
FROM openjdk:17-jdk-slim

# Set the working dir
WORKDIR /app

#copy the build JAR file from the build stage
COPY --from=build /app/target/smartcontactmanager-0.0.1-SNAPSHOT.jar .

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/smartcontactmanager-0.0.1-SNAPSHOT.jar"]
