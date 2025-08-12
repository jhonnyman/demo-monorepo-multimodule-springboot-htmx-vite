# Demo (Gradle + Maven)

This project is a multi-module Java monorepo supporting both Maven and Gradle builds.

- **Vendor modules** (spring-boot, vite) act as dependency platforms/conventions.
- **Application modules** (demo-app) can apply vendor conventions to get dependencies/plugins automatically.
- All Maven files are preserved.

## Gradle Usage

- Build all modules: `./gradlew build`
- Build a specific module: `./gradlew :demo-app:build`

## Structure

- `demo-vendors` — vendor conventions (spring-boot, quarkus, webjars)
- `demo-app` — application modules

## Docker Compose

Docker compose services are composed of the following

- Keycloak: Authentication and Authorization
- Minio: Stores and serves files, recordings
- Postgresql: Main database
- Vector: Log collector agent
- Loki: Log aggregator
- Grafana Visualization

```sh
# Setup Docker volumes on the computer
# Grant execute permissions
chmod +x scripts/dev/setup-docker-compose-volumes.sh

# Setup and configure docker volumes
./scripts/dev/setup-docker-compose-volumes.sh
```

```sh
# Launch docker compose services
docker compose -f development/docker/docker-compose.yaml -f development/docker/observability.docker-compose.yaml down -v
```

```sh
# Tear down docker compose
docker compose -f development/docker/docker-compose.yaml -f development/docker/observability.docker-compose.yaml down -v
```

## Notes

- Gradle and Maven builds are kept in sync.
- Vendor modules encapsulate dependencies/plugins for easy reuse.

## Demo monorepo

Contains all sub application modules, vendors and dependencies.

## License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

## Instructions for Developers

```sh
# Run a specific module
gradle :demo-app:bootRun
```

```sh
# Run live reload when updating static assets
gradle processResources --continuous
```

```sh
# Run Demo App project in parallel
./scripts/dev/run-demo-app-dev.sh
```

Applications have 3 different modes:

- Mock server
    - Use it for iterating on th UI
- Fully integrated
    - Run with real implementations for E2E tests and production
- In memory
    - All repository and event implementations are replaced with in-memory ones for faster development

## Frontend Monorepo Structure

### Overview

- Uses npm workspaces for modular frontend development.
- Each app module has its own frontend in `src/main/frontend`.
- Shared code lives in `demo-vendors/vite-ts`.
- Vite is used for fast, simple builds.
- Built assets are output to the backend's `src/main/resources/static/` directory.
- All routing is handled by Spring Boot (no client-side routing).
- Eventually all static resources will be loaded from Nginx directly

### How to Build

1. Install dependencies from the root:

    ```sh
    npm install
    ```

2. Build shared package:

    ```sh
    npm run build --workspace=<workspace-path>
    ```

3. Build a module frontend (example for demo-app):

    ```sh
    npm run build --workspace=demo-app-frontend
    ```

### Adding a New Frontend Module

- Create a `src/main/frontend` directory in your module.
- Add a `package.json`, `tsconfig.json`, and `vite.config.ts` (see demo-app for example).
- Add your source files in `src/`.
- Configure Vite to output to `../resources/static`.
- Add the new workspace to the root `package.json`.
