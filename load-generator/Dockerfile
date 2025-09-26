FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/load-generator.jar load-generator.jar

EXPOSE 10200

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100"

CMD ["sh", "-c", "java $JAVA_OPTS -jar load-generator.jar"]