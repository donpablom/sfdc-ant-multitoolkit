#!/bin/bash
if [ -z "$CHANGE_BRANCH" ]
then
  localBranchName="${GIT_BRANCH}"
else
  localBranchName="${CHANGE_BRANCH}"
fi

if [ -z "$CHANGE_TARGET" ]
then
  targetBranchName="${GIT_BRANCH}"
else
  targetBranchName="${CHANGE_TARGET}"
fi
prefix="https://"

if [ -z "$GIT_URL" ]
then
  gitLink=${GIT_URL_1#"$prefix"}
else
  gitLink=${GIT_URL#"$prefix"}
fi

if [ "$localBranchName" == "$targetBranchName" ];
then
  echo "Previous commit: ${GIT_PREVIOUS_SUCCESSFUL_COMMIT}"
  if [ -z "${GIT_PREVIOUS_SUCCESSFUL_COMMIT}" ];
    then
    git show --name-only HEAD^1 | grep ^commit | cut -f 2 -d ' ' > gitTargetRevision.variable
    cat gitTargetRevision.variable
  else
    echo ${GIT_PREVIOUS_SUCCESSFUL_COMMIT} > gitTargetRevision.variable
    fi
#  git ls-remote "https://${BITBUCKET_COMMON_CREDS_USR}:${BITBUCKET_COMMON_CREDS_PSW}@"$gitLink | grep -w refs/heads/$localBranchName | cut -f 1 >> gitTargetRevision.variable
else
  git ls-remote "https://${BITBUCKET_COMMON_CREDS_USR}:${BITBUCKET_COMMON_CREDS_PSW}@"$gitLink | egrep -w "refs/heads/${targetBranchName}$" | cut -f 1 > gitTargetRevision.variable
fi
