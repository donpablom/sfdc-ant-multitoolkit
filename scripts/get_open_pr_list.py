#!/usr/bin/python
import requests
import base64
import sys
import json
from datetime import datetime
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-u', '--username', required=True, help='User name for the org')
parser.add_argument('-p', '--password', required=True, help='Password')
parser.add_argument('-r', '--resultfile', required=True, help='Path to file where results will be written')
args = parser.parse_args()


url = 'https://github.com/sample/sampleRepo/'
data = requests.get(url, auth=(args.username, args.password))
json_data = json.loads(data.text)['values']

results = open(args.resultfile, "w+")
results.write("PR id|Author name|Author email|Title|Created| Last Updated| Link to PR\n")
for pr in json_data:
  results.write("{}|{}|{}|{}|{}|{}|{}\n".format(pr['id'], pr["author"]["user"]["name"], pr["author"]["user"]["emailAddress"], pr["title"], datetime.fromtimestamp(int(pr["createdDate"])/1000), datetime.fromtimestamp(int(pr["updatedDate"])/1000), pr['links']['self'][0]['href']))
results.close()
