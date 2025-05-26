FROM gradle:8.10-jdk21 AS backend-builder

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

WORKDIR /backend
COPY . .

FROM openjdk:21-jdk-slim AS final-backend

RUN useradd --uid 10000 --create-home runner
USER 10000

ENV NODE_ENV=production
ENV PRODUCTION=true

WORKDIR /app
COPY --from=backend-builder /backend/build/libs/app.jar /app/backend.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/backend.jar", "--server.port=8080"]
