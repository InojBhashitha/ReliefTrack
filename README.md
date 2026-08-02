# ReliefTrack

Smart Disaster Relief Coordination System

## Overview

ReliefTrack is a JavaFX and SQLite-based desktop application for disaster relief coordination. It helps relief organizations manage warehouses, inventory, emergency requests, dispatches, and operational reports through a modern user interface.

## Features

- User authentication with role-based access
- Warehouse, inventory, and relief item management
- Emergency request triage and dispatch scheduling
- Low-stock alerts and request prioritization
- Warehouse routing using graph-based shortest-path calculations
- Operational dashboards and report summaries

## Technologies

- Java 17
- JavaFX
- SQLite
- Maven

## Prerequisites

- Java 17 JDK installed
- Maven 3.8 or later

## Build

```bash
cd /home/inoj/dev/ReliefTrack
mvn clean package
```

## Run

Run from Maven:

```bash
mvn javafx:run
```

Or use the provided launcher script:

```bash
./run.sh
```

On Windows:

```cmd
run.bat
```

## Package

A packaged jar is created at:

```bash
target/relieftrack-1.0-SNAPSHOT.jar
```

Run the package with:

```bash
java -jar target/relieftrack-1.0-SNAPSHOT.jar
```

> If JavaFX is not found, use the Maven launcher (`mvn javafx:run`) because it resolves JavaFX runtime dependencies automatically.

## Project Structure

- `src/main/java` — application source code
- `src/main/resources` — FXML views, CSS, and resources
- `data` — SQLite database files and seeded demo content
- `pom.xml` — Maven build configuration

## Notes

- The app initializes the database automatically on first launch.
- Demo data and a default admin user are seeded when the database is empty.
- Admin users can manage users, warehouses, inventory, requests, dispatches, and reports.

## License

This project is licensed under the MIT License.
