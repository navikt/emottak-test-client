FROM gradle:8.10-jdk21 AS backend-builder
WORKDIR /backend
COPY backend/ .
RUN gradle build


FROM node:18-alpine AS frontend-builder
WORKDIR /frontend
COPY frontend/ .
RUN yarn install && yarn build
RUN yarn install --production


FROM openjdk:21-jdk-slim AS final-runtime

# Install Node.js & Yarn
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g yarn && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Our Non-root user
RUN useradd --uid 10000 --create-home runner
USER 10000

RUN mkdir -p /home/runner/.cache/yarn && \
    mkdir -p /home/runner/.yarn


ENV NODE_ENV=production
ENV PRODUCTION=true
ENV BACKEND_PORT=8080
ENV FRONTEND_PORT=3000

WORKDIR /app

COPY --from=backend-builder /backend/build/libs/app.jar /app/backend.jar

COPY --from=frontend-builder /frontend/.next /app/.next
COPY --from=frontend-builder /frontend/public /app/public
COPY --from=frontend-builder /frontend/package.json /app/package.json
COPY --from=frontend-builder /frontend/yarn.lock /app/yarn.lock
COPY --from=frontend-builder /frontend/node_modules /app/node_modules

EXPOSE ${BACKEND_PORT}
EXPOSE ${FRONTEND_PORT}

CMD ["sh", "-c", "java -jar /app/backend.jar --server.port=${BACKEND_PORT} & yarn start --port ${FRONTEND_PORT}"]
