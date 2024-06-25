FROM openjdk:17

ARG APP_JAR=*.jar
COPY ${APP_JAR} project-0.0.1.jar

ENTRYPOINT [ "java","-jar", "project-0.0.1.jar" ]

EXPOSE 8080