# ReliefTrack

Smart Disaster Relief Coordination System

## Overview

ReliefTrack is a JavaFX and SQLite-based desktop application for disaster relief coordination. It helps relief organizations manage warehouses, inventory, emergency requests, dispatches, and operational reports through a modern user interface.

## Features

- User authentication with role-based access (powered by a custom **Hash Table** cache)
- Warehouse, inventory, and relief item management
- Emergency request triage (ordered using a custom **Priority Queue**) and dispatch scheduling
- Low-stock alerts and request prioritization
- Warehouse routing using a custom **Graph** and **Dijkstra's shortest path algorithm**
- In-memory inventory indexing and prefix searches (powered by custom **AVL Trees**)
- Operational dashboards and report summaries

## Data Structures Implemented

This project is built around custom-designed data structures to demonstrate efficiency in logistics operations:
*   **AVL Tree** ([AVLTree.java](file:///home/inoj/new2/ReliefTrack/src/main/java/com/relieftrack/datastructure/avl/AVLTree.java)): A self-balancing binary search tree used for indexing items alphabetically and matching name prefixes in $O(\log n)$ time.
*   **Hash Table** ([HashTable.java](file:///home/inoj/new2/ReliefTrack/src/main/java/com/relieftrack/datastructure/hashtable/HashTable.java)): A separate-chaining hash map with dynamic resizing, used to cache user login credentials for $O(1)$ lookup performance.
*   **Priority Queue** ([PriorityQueue.java](file:///home/inoj/new2/ReliefTrack/src/main/java/com/relieftrack/datastructure/queue/PriorityQueue.java)): A linked-list queue that enqueues requests in priority order: Critical, High, Medium, Low.
*   **Graph** ([Graph.java](file:///home/inoj/new2/ReliefTrack/src/main/java/com/relieftrack/datastructure/graph/Graph.java)): An adjacency-list undirected graph implementing Dijkstra's algorithm to calculate the shortest transport route between Sri Lankan depots.

## Technologies

- Java 17
- JavaFX 17
- SQLite
- Maven

## Prerequisites

- Java 17 JDK installed
- Maven 3.8 or later

## Build

```bash
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

## Default Credentials

Use the following credentials to log in:
*   **Username**: `admin`
*   **Password**: `admin123`

## Project Structure

- `src/main/java` — application source code
- `src/main/resources` — FXML views, CSS, and resources
- `data` — SQLite database files and seeded demo content
- `pom.xml` — Maven build configuration

## Notes

- The app initializes the database automatically on first launch.
- Demo data (Sri Lankan locations, requests, and dispatches) and a default admin user are seeded when the database is empty.
- Admin users can manage users, warehouses, inventory, requests, dispatches, and reports.

## License

This project is licensed under the MIT License.
