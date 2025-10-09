FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/server.jar server.jar

EXPOSE 10201

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100"

CMD ["sh", "-c", "java $JAVA_OPTS -jar server.jar"]