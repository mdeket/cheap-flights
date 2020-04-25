FROM maven:3-jdk-11-slim
EXPOSE 8080
COPY . /app/
WORKDIR /app
RUN mvn clean package -DskipTests
ENTRYPOINT echo java -jar target/tickets-0.0.1-SNAPSHOT.jar; exec java -jar target/tickets-0.0.1-SNAPSHOT.jar
