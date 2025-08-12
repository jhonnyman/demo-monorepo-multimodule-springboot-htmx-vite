#!/bin/sh

# Start all processes in a new process group
set -m

npm run build:watch -w @demo.com/demo-app &
NPM_PID=$!

gradle :demo-app:bootRun &
BOOTRUN_PID=$!

# gradle processResources --continuous &
# RESOURCES_PID=$!

# Trap SIGINT/SIGTERM and kill all child processes
trap "kill $NPM_PID $BOOTRUN_PID" SIGINT SIGTERM EXIT

# Wait for all background jobs
wait