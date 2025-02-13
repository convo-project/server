FROM openjdk:17-alpine
ARG JAR_FILE=/build/libs/convo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /convo.jar
EXPOSE 8090
ENTRYPOINT exec java -jar -Duser.timezone=Asia/Seoul convo.jar