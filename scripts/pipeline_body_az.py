#!/usr/bin/python
import requests
import base64
import sys
import os
import json
from datetime import datetime
import argparse
import re


credentials = {
    "develop" :{
        "ci_env" : "CI",
        "ci_user" : "DEMO_USR",
        "ci_pass" : "DEMO_PASS",
        "target_env" : "DEMO",
        "target_user" : "DEMO_USR",
        "target_pass" : "DEMO_PASS",
    },
    "master" : {
        "ci_env" : "CI",
        "ci_user" : "DEMO_USR",
        "ci_pass" : "DEMO_PASS",
        "target_env" : "DEMO",
        "target_user" : "DEMO_USR",
        "target_pass" : "DEMO_PASS",
    },
    "test_branch" : {
        "ci_env" : "CI",
        "ci_user" : "DEMO_USR",
        "ci_pass" : "DEMO_PASS",
        "target_env" : "DEMO",
        "target_user" : "DEMO_USR",
        "target_pass" : "DEMO_PASS",
    }
}

build_map = {
  "test_branch" : {
    "mr" : {
      "build" : True,
      "ci_validate": True,
      "ci_deploy": False,
      "target_validate": True,
      "target_deploy" : False
    },
    "merge" : {
      "build" : True,
      "ci_validate": True,
      "ci_deploy": False,
      "target_validate": True,
      "target_deploy" : False
    }
  },
  "master" : {
    "mr" : {
      "build" : True,
      "ci_validate": True,
      "ci_deploy": False,
      "target_validate": True,
      "target_deploy" : False
    },
    "merge" : {
      "build" : True,
      "ci_validate": True,
      "ci_deploy": False,
      "target_validate": True,
      "target_deploy" : False
    }
  },
  "release" : {
    "mr" : {
      "build" : True,
      "ci_validate": True,
      "ci_deploy": False,
      "target_validate": True,
      "target_deploy" : False
    },
    "merge" : {
      "build" : True,
      "ci_validate": True,
      "ci_deploy": False,
      "target_validate": True,
      "target_deploy" : False
    }
  },
  "default" : {
      "build" : True,
      "ci_validate": False,
      "ci_deploy": False,
      "target_validate": False,
      "target_deploy" : False
  }
}

ant_params = {
  "build" : {
    "module" : "delta.package",
    "additional_params" : ""
  },
  "ci_validate" : {
    "module" : "delta.package.validate",
    "additional_params" : "-Drun.specified.tests=true -lib ../lib/ant-salesforce.jar"
  },
  "ci_deploy": {
    "module" : "delta.package.deploy.exec",
    "additional_params" : ""
  },
  "target_validate" : {
    "module" : "delta.package.validate",
    "additional_params" : ""
  },
  "target_deploy" : {
    "module" : "delta.package.deploy.exec",
    "additional_params" : ""
  },
}
def check_if_package_empty(path):
  if os.path.exists(path):
    directory = os.listdir(path)
    if not directory:
      return True
    elif len(directory) == 1 and "package.xml" in directory:
      return True
    else:
      return False
  else:
    sys.exit("No such file or directory!")
  return False

def get_env_variable(variable):
    return os.getenv(variable.replace("$",""))

def prepare_ant_commad(module, revision, server_url, usr, psw, target, source_dir, additional_params=''):
    return "ant -v {0} -Dcomponent.src={1} -Dgit.versions={2} -Dsf.serverurl={3} -Dorg={4} -Dsf.username={5} -Dsf.password={6} {7}".format(
        module,
        source_dir,
        revision,
        server_url,
        target,
        usr,
        psw,
        additional_params
    )

def run_command(command):
  result = os.system(command)
  if result != 0:
    sys.exit("------> An error occured during execution of command: {0}".format(command))

def get_source_dir():
  source_dir=""
  if os.path.exists("../../src/"):
    source_dir = "../../src"
  elif os.path.exists("../../force-app/main/default"):
    source_dir = "../../force-app/main/default"

  return source_dir

parser = argparse.ArgumentParser()
parser.add_argument('-m', '--mode', required=True, help='[build], [ci_validate], [ci_deploy], [target_validate], [target_deploy]')
args = parser.parse_args()

def main_func():
  server_url = os.getenv("DEMOSERVER")
  artifact_dir = "./artifact/"
  package_dir = "cdelta"
  artifact_path = artifact_dir + package_dir
  builds_map = {}
  creds_map = {}
  source_dir = get_source_dir()
  pr_source_branch = os.getenv("SYSTEM_PULLREQUEST_SOURCEBRANCH").replace("refs/heads/","") if os.getenv("SYSTEM_PULLREQUEST_SOURCEBRANCH") else ''
  pr_target_branch = os.getenv("SYSTEM_PULLREQUEST_TARGETBRANCH").replace("refs/heads/","") if os.getenv("SYSTEM_PULLREQUEST_TARGETBRANCH") else ''

  print("{0}, {1}, {2}".format(pr_target_branch,os.getenv("BUILD_SOURCEBRANCHNAME"),os.getenv("GIT_REVISION")))
  if os.getenv('BUILD_REASON') == 'PullRequest' and pr_target_branch and pr_target_branch in build_map:
    print("This is a merge request from {0} to {1}".format(
        pr_source_branch,
        pr_target_branch
    ))
    builds_map = build_map[pr_target_branch]['mr']
    creds_map = credentials[pr_target_branch]
  elif os.getenv('BUILD_REASON') != 'PullRequest' and os.getenv("BUILD_SOURCEBRANCHNAME") in build_map:
    print("This is a merge on {0} branch".format(
            os.getenv("BUILD_SOURCEBRANCHNAME")
        ))
    builds_map = build_map[os.getenv("BUILD_SOURCEBRANCHNAME")]['merge']
    creds_map = credentials[os.getenv("BUILD_SOURCEBRANCHNAME")]
  else:
    print("Other type of branch!")

  if not builds_map and not creds_map:
    print("Nothinng to do!")
  else:
    print("This is the {0} mode - working on (map value set to {1})".format(args.mode, builds_map[args.mode]))
    if args.mode == "build" and builds_map[args.mode] == True:
      print(prepare_ant_commad(ant_params[args.mode]["module"], os.getenv("GIT_REVISION"), server_url, os.getenv(creds_map["target_user"]), os.getenv(creds_map["target_pass"]), creds_map["target_env"], source_dir, ant_params[args.mode]["additional_params"]))
      run_command(prepare_ant_commad(ant_params[args.mode]["module"], os.getenv("GIT_REVISION"), server_url, os.getenv(creds_map["target_user"]), os.getenv(creds_map["target_pass"]), creds_map["target_env"], source_dir, ant_params[args.mode]["additional_params"]))
      if "force-app" in source_dir and os.path.exists("{}force-app".format(artifact_dir)):
        run_command("cp ../../sfdx-project.json {}".format(artifact_dir))
        run_command("cd {0} && sfdx force:source:convert -r ./force-app/ -d ./{1}".format(artifact_dir, package_dir))

      if os.path.exists(artifact_path) and not check_if_package_empty(artifact_path):
        run_command("sgp -s {0} -o {0} -a 52.0".format(artifact_path))
        run_command("../scripts/add_to_xml.py -s {0} -p {0}/package.xml".format(artifact_path))
        run_command("cd {0} && zip -r package.zip {1}".format(artifact_dir, package_dir))
    elif not check_if_package_empty(artifact_path) and builds_map[args.mode] == True:
      run_command(prepare_ant_commad(ant_params[args.mode]["module"], os.getenv("GIT_REVISION"), server_url, os.getenv(creds_map["target_user"]), os.getenv(creds_map["target_pass"]), creds_map["target_env"], source_dir, ant_params[args.mode]["additional_params"]))
    else:
      print("Package is empty or mode set to false")


if __name__ == '__main__':
    main_func()
