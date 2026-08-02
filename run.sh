#!/usr/bin/env bash

set -e
cd "$(dirname "$0")"

echo "Starting ReliefTrack..."
mvn javafx:run
