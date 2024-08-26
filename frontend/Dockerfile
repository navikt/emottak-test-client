FROM node:18-alpine AS frontend-builder
WORKDIR /frontend
COPY . .
RUN yarn install && yarn build
RUN yarn install --production

FROM node:20-bookworm-slim AS final-frontend

RUN useradd --uid 10000 --create-home runner
USER 10000

RUN mkdir -p /home/runner/.cache/yarn && \
    mkdir -p /home/runner/.yarn

ENV NODE_ENV=production
ENV PRODUCTION=true

WORKDIR /app
COPY --from=frontend-builder /frontend/.next /app/.next
COPY --from=frontend-builder /frontend/public /app/public
COPY --from=frontend-builder /frontend/package.json /app/package.json
COPY --from=frontend-builder /frontend/yarn.lock /app/yarn.lock
COPY --from=frontend-builder /frontend/node_modules /app/node_modules

EXPOSE 3000

CMD ["yarn", "start", "--port", "3000"]
