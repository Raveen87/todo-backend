#

This is an implementation of the [Todo-Backend project](https://www.todobackend.com/) using Java 17.
The implementation uses a non-blocking approach with Spring WebFlux and R2DBC.

## Prerequisites

For local development you need:

* Java 17
* Maven
* Docker

To run the production environment you only need Docker.

## Local development

To get started with local development you just need to spin up a Docker container with Postgres and set an environment variable.

* Run `docker compose -f docker-compose_dev.yml up -d` to get the Postgres DB running
* Run the service either in your favorite IDE or on the command line
  - To run in IDE: Set the active profile to _dev_ either with environment variable `SPRING_PROFILES_ACTIVE=dev` or with VM argument `-Dspring.profiles.active=dev`
  - To run on command line: Execute `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

The service runs on port 8080.

## Local production environment

It's possible to start up the project in a production environment locally with Docker. A Postgres container will be created and the todo service will be built into a container as well.

* Run `docker compose up -d`

The service runs on port 8080.

Please note that if you make any modifications and would like to recompile the project, it's required to start it with the `--build` flag:

`docker compose up --build -d`

## Information

This project uses [lombok](https://projectlombok.org/), so in order for the code to work in an IDE it requires a lombok plugin.

Since there's no business logic there are only integration tests and no unit tests.
