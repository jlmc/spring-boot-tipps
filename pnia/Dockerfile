FROM openjdk:17-alpine as BUILD_IMAGE

ENV APP_HOME /app
RUN mkdir $APP_HOME
WORKDIR $APP_HOME


ADD src $APP_HOME/src
ADD pom.xml $APP_HOME/pom.xml
ADD mvnw.cmd $APP_HOME/mvnw.cmd
ADD mvnw $APP_HOME/mvnw
ADD .mvn $APP_HOME/.mvn
ADD prefixes.txt $APP_HOME/prefixes.txt


RUN $APP_HOME/mvnw clean package

FROM openjdk:17-alpine
WORKDIR /app

COPY --from=BUILD_IMAGE /app/target/pnia-0.0.1-SNAPSHOT.jar .
COPY --from=BUILD_IMAGE /app/prefixes.txt .

#ENTRYPOINT java -jar /app/target/pnia-0.0.1-SNAPSHOT.jar
# PNIA_PREFIXES_FILE_PATH=/app/prefixes.txt
# docker run --rm -e "PNIA_PREFIXES_FILE_PATH=/app/prefixes.txt" x1 java -jar /app/pnia-0.0.1-SNAPSHOT.jar
