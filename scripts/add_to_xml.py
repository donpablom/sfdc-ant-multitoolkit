#!/usr/bin/python
import base64
import sys
import json
import os
from os import listdir
from datetime import datetime
import argparse
import re

def filter(path):
  """ Read content of the directory and skip all meta.xml files"""
  result = [ x for x in listdir(path) if "meta.xml" not in x ]
  return result

def get_meta(path):
  """ Get from directory only meta.xml files. """
  return [ re.sub("-meta.xml","",x) for x in listdir(path) if "meta.xml" in x ]

def search_for_files(path, addition="/"):
  """ Read content of the directory and search for files"""
  result = [addition + re.sub('\..*',"",f) for f in listdir(path) if os.path.isfile(path +"/" + f)]
  return result

def get_file_names(dir_path):
  onlyfiles = []
  onlyfiles += get_meta(dir_path)
  for it in filter(dir_path):
    if os.path.isdir(dir_path +"/" + it):
      onlyfiles += search_for_files(dir_path +"/" + it, it + "/")
    else:
      onlyfiles.append(re.sub(r'\..*',"",it))
  return onlyfiles

def handle_missing_types():
  #Check if directory from missing_types is present in package
  #and then check if appropriate line exists in package.xml
  #if not - parse contents of the directory and add to package.xml
  files_to_add = ""
  for directory in missing_types:
    print("handleMiss")
    found = False
    dir_path = args.source + "/" + directory
    with open(args.packagefile) as f:
      if missing_types[directory] in f.read():
          found = True
    if os.path.exists(dir_path) and not found:
      onlyfiles = get_file_names(dir_path)

      files_to_add +="\t<types>\n"
      for file in onlyfiles:
        if file not in files_to_add:
          files_to_add += "\t\t<members>{}</members>\n".format(file)
      files_to_add += "\t\t{}\n\t</types>\n".format(missing_types[directory])
  return files_to_add

def handle_reports():
  #Check if reports are in the package:
  dir_path = args.source + "/reports/"
  list_reports = []
  if os.path.exists(dir_path):
    #create a list of directories
    for r, d, f in os.walk(dir_path):
      print("handleReports")
      root = r.replace(dir_path, "")
      root_dir = root + "/"
      if root:
        if root not in list_reports:
          list_reports.append(root)
        root = root_dir
      else:
        root = ""
      if d:
        for directory in d:
          if root + directory not in list_reports:
            list_reports.append(root + directory)
      if f:
        for file in f:
          if "meta.xml" not in file and file not in list_reports:
            list_reports.append(root +  file.replace(".report",""))

  reports=""
  regexp = re.compile(r"^unfiled\$public$")
  for report in list_reports:
    if not regexp.search(report):
        reports += "\t\t<members>{}</members>\n".format(report)

  reports += "\t\t<name>Report</name>\n"
  return reports

def handle_objects():
  #Check if objects are in the package:
  dir_path = args.source + "/objects/"
  list_objects = []
  if os.path.exists(dir_path):
    #create a list of directories
    for r, d, f in os.walk(dir_path):
      print("handleObjects")
      root = r.replace(dir_path, "")
      root_dir = root + "/"
      if root:
        if root not in list_objects:
          list_objects.append(root)
        root = root_dir
      else:
        root = ""
      if d:
        for directory in d:
          if root + directory not in list_objects:
            list_objects.append(root + directory)
      if f:
        for file in f:
          if "meta.xml" not in file and file not in list_objects:
            list_objects.append(root +  file.replace(".object",""))

  objects=""
  for report in list_objects:
    objects += "\t\t<members>{}</members>\n".format(report)

  objects += "\t\t<name>CustomObject</name>\n"
  return objects

missing_types = {
  "translations" : "<name>Translations</name>",
  "networks" : "<name>Network</name>",
  "email" : "<name>EmailTemplate</name>",
  "namedCredential" : "<name>NamedCredential</name>",
  "site" : "<name>CustomSite</name>",
  "topicsForObjects" : "<name>TopicsForObjects</name>",
  "lwc":"<name>LightningComponentBundle</name>"
}

fixed_types = {
  "objects" : "<name>CustomObject</name>"
}

parser = argparse.ArgumentParser()
parser.add_argument('-s', '--source', required=True, help='Directory with the package')
parser.add_argument('-p', '--packagefile', required=True, help='Path to existing package.xml file')
args = parser.parse_args()

print("Start")
files_to_add = handle_missing_types()

objects = handle_objects()
reports = handle_reports()

package = ""
with open(args.packagefile,'r') as f:
    package = f.read()
package = package.replace('<version>', files_to_add + "\t<version>")

if "<name>CustomObject</name>" in package:
 package = package.replace('<name>CustomObject</name>',objects)

if "<name>Report</name>" in package:
  package = package.replace('<name>Report</name>', reports)
else:
  package = package.replace('<version>', "\t<types>\n{}\t</types>\n\t<version>".format(reports))

f = open(args.packagefile, "w")
f.write(package)
f.close()
print("Finish")
