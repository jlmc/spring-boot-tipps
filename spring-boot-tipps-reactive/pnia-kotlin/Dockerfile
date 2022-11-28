FROM openjdk:17-alpine as BUILD_IMAGE

ENV APP_HOME /app
RUN mkdir $APP_HOME
WORKDIR $APP_HOME


ADD src $APP_HOME/src
ADD build.gradle.kts $APP_HOME/build.gradle.kts
ADD settings.gradle.kts $APP_HOME/settings.gradle.kts
ADD gradlew $APP_HOME/gradlew
ADD gradlew.bat $APP_HOME/gradlew.bat
ADD gradle $APP_HOME/gradle
ADD prefixes.txt $APP_HOME/prefixes.txt


RUN $APP_HOME/gradlew build

FROM openjdk:17-alpine
WORKDIR /app

COPY --from=BUILD_IMAGE /app/build/libs/pnia-kotlin-0.0.1-SNAPSHOT.jar .
COPY --from=BUILD_IMAGE /app/prefixes.txt .

#ENTRYPOINT java -jar /app/target/pnia-0.0.1-SNAPSHOT.jar
# PNIA_PREFIXES_FILE_PATH=/app/prefixes.txt
# docker run --rm -e "PNIA_PREFIXES_FILE_PATH=/app/prefixes.txt" x1 java -jar /app/pnia-0.0.1-SNAPSHOT.jar
