FROM eclipse-temurin:21-jdk-alpine

ENV NODE_ENV=production
ENV PRODUCTION=true

COPY build/libs/app.jar /app/app.jar

WORKDIR /app
USER nonroot

CMD [ "app.jar" ]
