#!/bin/sh

jsonOutput=$(
  curl -s --request POST -u "$SONATYPE_USERNAME:$SONATYPE_PASSWORD" \
    --url https://s01.oss.sonatype.org/service/local/staging/profiles/"${SONATYPE_STAGING_PROFILE_ID}"/start \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json' \
    --data '{ "data": {"description" : "'"$SONATYPE_DESCRIPTION"'"} }'
)

stagingRepositoryId=$(echo "$jsonOutput" | jq -r '.data.stagedRepositoryId')

if [ -z "$stagingRepositoryId" ]; then
  echo "Error while creating the staging repository."
  exit 1
else
echo "repository_id=$stagingRepositoryId" >> $GITHUB_OUTPUT
fi