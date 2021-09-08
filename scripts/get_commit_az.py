#!/usr/bin/python
import requests
import base64
import sys
import os
import json
from datetime import datetime
import argparse
import re

parser = argparse.ArgumentParser()
parser.add_argument('-s', '--source', required=True, help='Source branch to start looking at')
parser.add_argument('-t', '--token', required=True, help='gitlab personal access token')
parser.add_argument('-m', '--mode', required=False, help="Mode: commit on [target] or last [successful]")
args = parser.parse_args()


def main_func():
  if args.mode == 'target' or args.mode == 'successful':
    mode = args.mode
  else:
    mode = 'successful'
  successful = False
  commit =''
  branch = args.source
  #headers = {'PRIVATE-TOKEN': args.token,}
  project = os.getenv('SYSTEM_TEAMPROJECT')
  repo = os.getenv('BUILD_REPOSITORY_NAME')
  base_url = 'https://dev.azure.com/devopsatcpoland/{0}/_apis/git/repositories/{1}/commits'.format(project, repo)

  #Get HEAD commit of given source branch
  url = '{0}?searchCriteria.itemVersion.version={1}&api-version=5.1'.format(base_url, branch)
  result = json.loads(requests.get(url, auth=(args.token, ''), timeout=5).text)
  commit = result["value"][0]["commitId"]

  while successful != True:
    #Get status for commit
    url = '{0}/{1}/statuses?top=100&api-version=5.1'.format(base_url, commit)
    result = json.loads(requests.get(url, auth=(args.token, ''), timeout=5).text)
    if result["value"]:
      if result["value"][0]['state']:
        pipeline_status = result["value"][0]['state']
    else:
      pipeline_status = "succeeded"

    #Get previous commit
    url = '{0}/{1}'.format(base_url, commit)
    result = json.loads(requests.get(url, auth=(args.token, ''), timeout=5).text)
    previous_commit = result["parents"][0]

    if commit == os.getenv("BUILD_SOURCEVERSION"):
      commit = previous_commit
      continue

    if mode == 'successful':
      if pipeline_status != 'succeeded':
        commit = previous_commit
      else:
        successful = True
    else:
      successful = True

  print(commit)


if __name__ == '__main__':
  main_func()
