#!/usr/bin/env bash
set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log() { printf "\n${GREEN}==>${NC} %s\n" "$1"; }
error() { printf "${RED}ERROR:${NC} %s\n" "$1" >&2; }
warn() { printf "${YELLOW}WARN:${NC} %s\n" "$1"; }

root=$(cd "$(dirname "$0")" && pwd)

# Function to start a service in background
start_service() {
  local service_name=$1
  local service_dir=$2
  local port=$3
  
  log "Starting $service_name on port $port..."
  
  local runner
  if [[ -x "$root/$service_dir/mvnw" ]]; then
    runner="./mvnw spring-boot:run"
  elif [[ -x "$root/$service_dir/gradlew" ]]; then
    runner="./gradlew bootRun"
  else
    error "No build script found in $service_dir"
    return 1
  fi
  
  # Start in background
  (cd "$root/$service_dir" && $runner > "$root/$service_dir.log" 2>&1 & echo $! > "$root/$service_dir.pid")
  
  # Wait for service to be ready
  local max_attempts=30
  local attempt=0
  while [[ $attempt -lt $max_attempts ]]; do
    if curl -s "http://localhost:$port/health" >/dev/null 2>&1 || curl -s "http://localhost:$port" >/dev/null 2>&1; then
      log "✅ $service_name is running on port $port"
      return 0
    fi
    attempt=$((attempt + 1))
    sleep 1
  done
  
  error "$service_name failed to start within 30 seconds"
  return 1
}

# Function to stop all services
cleanup() {
  log "Cleaning up..."
  for pid_file in "$root"/*.pid; do
    if [[ -f "$pid_file" ]]; then
      local pid=$(cat "$pid_file")
      if kill -0 "$pid" 2>/dev/null; then
        log "Stopping process $pid..."
        kill "$pid" 2>/dev/null || true
      fi
      rm "$pid_file"
    fi
  done
}

trap cleanup EXIT

# Check prerequisites
log "Checking prerequisites..."
command -v curl >/dev/null 2>&1 || { error "curl is required"; exit 1; }

# Start services
log "Starting required services..."
start_service "account" "account" 8081 || exit 1
sleep 2
start_service "labtest" "labtest" 8083 || exit 1

# Run integration tests
log "Running integration tests..."
if [[ -x "$root/test-labtest-service.bash" ]]; then
  "$root/test-labtest-service.bash"
else
  error "test-labtest-service.bash not found or not executable"
  exit 1
fi
