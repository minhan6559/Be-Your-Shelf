#!/bin/bash
set -e

JDK_DIR="jdk"
JDK_URL="https://download.java.net/openjdk/jdk22/ri/openjdk-22+36_linux-x64_bin.tar.gz"

# 1. Check if JDK already exists
if [ -d "$JDK_DIR" ]; then
    echo "JDK already present in $JDK_DIR"
    exit 0
fi

# 2. Download JDK
echo "Downloading JDK..."
curl -L -o jdk.tar.gz "$JDK_URL"

# 3. Extract and rename
echo "Extracting..."
tar -xzf jdk.tar.gz

# Rename extracted "jdk-22" folder (or similar) to just "jdk"
EXTRACTED_DIR=$(tar -tzf jdk.tar.gz | head -1 | cut -f1 -d"/")
if [ -d "$EXTRACTED_DIR" ] && [ "$EXTRACTED_DIR" != "$JDK_DIR" ]; then
    mv "$EXTRACTED_DIR" "$JDK_DIR"
fi

# 4. Clean up
rm jdk.tar.gz

echo "JDK installed locally in $JDK_DIR"
