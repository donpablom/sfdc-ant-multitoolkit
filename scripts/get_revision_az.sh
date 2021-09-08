#!/bin/bash
echo "TESTS"
echo "ACTUAL COMMIT: ${BUILD_SOURCEVERSION}"
if [[ "$BUILD_REASON" != "PullRequest" ]];
then
    echo "This is normal commit to ${BUILD_SOURCEBRANCHNAME}"
    echo "Try to get previous successfull build"
    python3 ./get_commit_az.py -s $BUILD_SOURCEBRANCHNAME -t $TOKEN > result
elif [[ ! -z "$SYSTEM_PULLREQUEST_PULLREQUESTID" && "$BUILD_REASON" == "PullRequest" ]];
then
    TARGET=$(echo $SYSTEM_PULLREQUEST_TARGETBRANCH | sed 's/refs\/heads\///g')
    SOURCE=$(echo $SYSTEM_PULLREQUEST_SOURCEBRANCH | sed 's/refs\/heads\///g')
    echo "This is merge request nr $SYSTEM_PULLREQUEST_PULLREQUESTID from $SOURCE to $TARGET"
    echo "Trying to get revision from $TARGET"
    python3 ./get_commit_az.py -s $TARGET -t $TOKEN -m target > result
fi
