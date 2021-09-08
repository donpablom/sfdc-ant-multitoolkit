#!/bin/bash
echo "ACTUAL COMMIT: ${CI_COMMIT_SHA}"
if [[ ! -z "$CI_COMMIT_BRANCH" ]];
then
    echo "This is normal commit to ${CI_COMMIT_BRANCH}"
    echo "Try to get previous successfull build"
    python3 ./get_commit.py -s $CI_COMMIT_BRANCH -t $GITLAB_TOKEN -p $CI_PROJECT_ID > result
elif [[ ! -z "$CI_MERGE_REQUEST_IID" ]];
then
    echo "This is merge request nr $CI_MERGE_REQUEST_IID from $CI_MERGE_REQUEST_SOURCE_BRANCH_NAME to $CI_MERGE_REQUEST_TARGET_BRANCH_NAME"
    echo "Trying to get revision from $CI_MERGE_REQUEST_TARGET_BRANCH_NAME"
    python3 ./get_commit.py -s $CI_MERGE_REQUEST_TARGET_BRANCH_NAME -t $GITLAB_TOKEN -p $CI_PROJECT_ID -m target > result
fi

