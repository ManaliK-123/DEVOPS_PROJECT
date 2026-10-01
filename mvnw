#!/bin/sh
if command -v mvn >/dev/null 2>&1; then
    mvn "$@"
elif [ -f "./.tools/apache-maven-3.9.6/bin/mvn" ]; then
    "./.tools/apache-maven-3.9.6/bin/mvn" "$@"
else
    echo "Maven not found in PATH or .tools directory"
    exit 1
fi
