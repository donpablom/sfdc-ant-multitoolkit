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
parser.add_argument('-p', '--project', required=True, help='gitlab project id')
parser.add_argument('-m', '--mode', required=False, help="Mode: commit on [target] or last [successful]")
args = parser.parse_args()


def main_func():
    if args.mode == 'target' or args.mode == 'successful':
        mode = args.mode
    else:
        mode = 'successful'
    successful = False
    commit = args.source
    headers = {'PRIVATE-TOKEN': args.token,}
    project = args.project
    base_url = 'https://gitlab.com/api/v4/projects/{0}/repository/commits/'.format(project)

    while successful != True:
        url = base_url + commit
        result = json.loads(requests.get(url, headers=headers).text)
        commit = result["id"]
        previous_commit = result["parent_ids"][0]
        pipeline_status = result["status"]

        if commit == os.getenv("CI_COMMIT_SHA"):
            commit = previous_commit
            continue

        if mode == 'successful':
            if pipeline_status != 'success':
                commit = previous_commit
            else:
                successful = True
        else:
            successful = True

    print(commit)


if __name__ == '__main__':
    main_func()
