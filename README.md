# emottak-test-client

## TLDR Commands

| Command                                                                                                                   | Description                                                        |
|---------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------|
| `./gradlew dev`                                                                                                           | Runs both the backend and frontend concurrently (requires ENVs set) |
| `./gradlew run`                                                                                                           | Runs only the Ktor backend (requires ENVs set)                     |
| `cd frontend && yarn dev`                                                                                                 | Runs only the Next.js frontend                                     |
| `docker build -t emottak-test-client .`                                                                                   | Build local docker image                                           |
| `docker run --name emottak-test-client-backend -p 8080:8080 -t emottak-test-client-backend -f backend/Dockerfile backend` | Run local docker image of backend (requires ENVs passed in)        |
| `docker run --name emottak-test-client -p 3000:3000 -t emottak-test-client -f frontend/Dockerfile frontend`               | Run local docker image of frontend                                 |
| `docker rm -f emottak-test-client`                                                                                        | Forcefully stop and remove a container                             |

## Running the Full Stack Application

To run both the backend and frontend concurrently in development mode with hot-reloading, use the following command:

```bash
./gradlew dev
```

This command will start the Ktor backend and the Next.js frontend together. The backend will be accessible
at http://localhost:8080, and the frontend will be accessible at http://localhost:3000

## Required Envs

| ENV | What     |
| ----|----------|
| VIRKSOMHETSSERTIFIKAT_SIGNERING | key      |
| VIRKSOMHETSSERTIFIKAT_CREDENTIALS | password | 

You can find these values in https://console.nav.cloud.nais.io under "Secrets" for the `emottak-test-client` app.

## Better Logging for local development

Add the following ENVs:

| ENV           | What                    | Why   |
|---------------|-------------------------|-------| 
| LOCAL_LOGGING | boolean | This will change logging to a readable format (not JSON) |
| EMOTTAK_LOGGING_LEVEL | "DEBUG", "INFO", etc | Recommend setting this to DEBUG for local development |


## Running the Backend

To run the backend only, use the following command:

```bash
./gradlew backend
```

This command will start the Ktor backend.

## Running the Frontend

To run the frontend only, use the following command:

```bash
./gradlew frontend
```

This command will start the Next.js frontend.

## Logging

### Set LOCAL_LOGGING

To get human readable logs during local development, set the "LOCAL_LOGGING" environment variable to "true".
By default all logs are parsed as JSON.