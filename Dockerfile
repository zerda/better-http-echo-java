FROM maven:3.6-jdk-8-alpine AS builder

WORKDIR /build
COPY ./pom.xml /build/
RUN mvn dependency:go-offline -B

COPY ./src/ /build/src/
RUN mvn package -B
ARG JAR_FILE=/build/target/*.jar
RUN java -Djarmode=layertools -jar ${JAR_FILE} extract --destination /build/layers/

# Stage 2
FROM openjdk:8-jre-alpine

ENV SERVER_PORT 80
EXPOSE 80

WORKDIR /app
COPY --from=builder /build/layers/dependencies/ ./
COPY --from=builder /build/layers/spring-boot-loader ./
COPY --from=builder /build/layers/snapshot-dependencies/ ./
COPY --from=builder /build/layers/application/ ./

ENTRYPOINT java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom "org.springframework.boot.loader.JarLauncher"
