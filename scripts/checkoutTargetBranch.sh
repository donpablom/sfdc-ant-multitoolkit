#!/bin/bash

if [ -z "$CHANGE_TARGET" ]
then
  targetBranchName=${GIT_BRANCH}
else
  targetBranchName=${CHANGE_TARGET}
fi

echo $targetBranchName

prefix="https://"

if [ -z "$GIT_URL" ]
then
  gitLink=${GIT_URL_1#"$prefix"}
else
  gitLink=${GIT_URL#"$prefix"}
fi

rm -rf buildscripts/build/artifact/ctarget
mkdir buildscripts/build/artifact/ctarget
cd buildscripts/build/artifact/ctarget

git clone --single-branch --branch $targetBranchName "https://${BITBUCKET_COMMON_CREDS_USR}:${BITBUCKET_COMMON_CREDS_PSW}@"$gitLink

