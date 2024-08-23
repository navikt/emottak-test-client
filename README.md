# emottak-test-client

## TLDR Commands

| Command                                                                               | Description                                     |
|---------------------------------------------------------------------------------------|-------------------------------------------------|
| `./gradlew dev`                                                                       | Runs both the backend and frontend concurrently |
| `./gradlew run`                                                                       | Runs only the Ktor backend                      |
| `cd frontend && yarn dev`                                                             | Runs only the Next.js frontend                  |
| `docker build -t emottak-test-client .`                                               | Build local docker image                        |
| `docker run --name emottak-test-client -p 8080:8080 -p 3000:3000 emottak-test-client` | Run local docker image                          |
| `docker rm -f emottak-test-client`                                                    | Stop the local container                        |

## Running the Full Stack Application

To run both the backend and frontend concurrently in development mode with hot-reloading, use the following command:

```bash
./gradlew dev
```

This command will start the Ktor backend and the Next.js frontend together. The backend will be accessible
at http://localhost:8080, and the frontend will be accessible at http://localhost:3000

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

## Docker

```bash
# build
docker build -t emottak-test-client .  

# run
docker run --name emottak-test-client -p 8080:8080 -p 3000:3000 emottak-test-client

# remove forcefully
docker rm -f emottak-test-client
```