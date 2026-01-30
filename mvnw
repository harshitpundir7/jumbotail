#!/bin/sh
# Maven Wrapper script
# Falls back to system Maven if wrapper JAR is not executable

# Resolve the directory of this script
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Check for mvn command directly
if command -v mvn > /dev/null 2>&1; then
    exec mvn "$@"
fi

# Check MAVEN_HOME
if [ -n "$MAVEN_HOME" ] && [ -x "$MAVEN_HOME/bin/mvn" ]; then
    exec "$MAVEN_HOME/bin/mvn" "$@"
fi

# Check for Java
if ! command -v java > /dev/null 2>&1; then
    echo "Error: Java is required but not found" >&2
    exit 1
fi

echo "Error: Maven is not installed. Please install Maven first." >&2
echo "  - On Ubuntu/Debian: sudo apt install maven" >&2
echo "  - On macOS: brew install maven" >&2
echo "  - Or download from: https://maven.apache.org/download.cgi" >&2
exit 1
