FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21

ENV NODE_ENV=production
ENV PRODUCTION=true

COPY build/libs/app.jar /app/app.jar

WORKDIR /app
USER nonroot

CMD [ "app.jar" ]
