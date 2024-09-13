#!/bin/sh

response=$(curl -s --request POST -u "$SONATYPE_USERNAME:$SONATYPE_PASSWORD" \
  --url https://s01.oss.sonatype.org/service/local/staging/bulk/drop \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{ "data" : {"stagedRepositoryIds":["'"$SONATYPE_REPOSITORY_ID"'"], "description":"Drop '"$SONATYPE_REPOSITORY_ID"'." } }')

if [ ! -z "$response" ]; then
    echo "Error while dropping staged repository $SONATYPE_REPOSITORY_ID : $response."
    exit 1
fi