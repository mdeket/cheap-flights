FROM maven:3-jdk-11-slim
ARG PROFILE
ARG NEO4J_PASSWORD
ARG JWT_SECRET
ENV PROFILE ${PROFILE}
ENV NEO4J_PASSWORD ${NEO4J_PASSWORD}
ENV JWT_SECRET ${JWT_SECRET}
EXPOSE 8080
COPY . /app/
WORKDIR /app
RUN mvn clean package -DskipTests
ENV JAVA_OPTS -Dspring.profiles.active=$PROFILE -Dneo4j.password=$NEO4J_PASSWORD -Djwt.secret=$JWT_SECRET
ENTRYPOINT echo java $JAVA_OPTS -jar target/tickets-0.0.1-SNAPSHOT.jar; exec java $JAVA_OPTS -jar target/tickets-0.0.1-SNAPSHOT.jar
