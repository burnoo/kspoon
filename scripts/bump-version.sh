#!/bin/bash

# Check if a parameter is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <major|minor|patch>"
    exit 1
fi

# File path
file="kspoon/build.gradle.kts"

# Read the file and find the version line
while IFS= read -r line; do
    if [[ $line =~ [0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT ]]; then
        version_line=$line
        break
    fi
done < "$file"

# Extract the current version
current_version=$(echo "$version_line" | grep -oE '[0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT')
IFS='.' read -r major minor patch <<< "$(echo "$current_version" | grep -oE '[0-9]+\.[0-9]+\.[0-9]+')"

# Determine the new version based on the input parameter
if [ "$1" == "major" ]; then
    new_major=$((major + 1))
    new_version="${new_major}.0.0"
elif [ "$1" == "minor" ]; then
    new_minor=$((minor + 1))
    new_version="${major}.${new_minor}.0"
elif [ "$1" == "patch" ]; then
    new_patch=$((patch + 1))
    new_version="${major}.${minor}.${new_patch}"
else
    echo "Invalid parameter. Use 'major', 'minor', or 'patch'."
    exit 1
fi

# Version with -SNAPSHOT for the file
new_version_snapshot="${new_version}-SNAPSHOT"

# Replace the version in the file
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    sed -i '' "s/$current_version/$new_version_snapshot/" "$file"
else
    # Linux and other UNIX systems
    sed -i "s/$current_version/$new_version_snapshot/" "$file"
fi

# Set the new version as a GitHub Actions output without -SNAPSHOT
echo "NEW_VERSION=$new_version" >> $GITHUB_ENV

echo "Version bumped from $current_version to $new_version_snapshot"
