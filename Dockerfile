FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:9-jdk17
WORKDIR /usr/local/tomcat

COPY --from=builder /app/target/ecommerce.war webapps/ecommerce.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
