# FROM node:22 AS builder

# WORKDIR /app

# RUN npm i -g @angular/cli

# COPY frontend/src src
# COPY frontend/package.json .
# COPY frontend/package-lock.json .
# COPY frontend/angular.json .
# COPY frontend/tsconfig.app.json .
# COPY frontend/tsconfig.json .

# RUN npm ci 
# RUN npm install flowbite
# RUN ng build

FROM maven:3.9.9-eclipse-temurin-23 AS bootstrap

ARG APP_DIR=/myapp

WORKDIR ${APP_DIR}

COPY src src
COPY .mvn .mvn
COPY pom.xml .
COPY mvnw .
# COPY --from=builder app/dist/client-side/browser src/main/resources/static

RUN mvn clean package -Dmaven.test.skip=true

FROM maven:3.9.9-eclipse-temurin-23

COPY --from=bootstrap /myapp/target/FlavourFoundry-0.0.1-SNAPSHOT.jar app.jar
COPY gen-lang-client-0165802748-384bfd47c790.json .

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT java -Duser.timezone=Asia/Singapore -jar app.jar