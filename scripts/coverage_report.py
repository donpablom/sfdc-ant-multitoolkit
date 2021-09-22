#!/usr/bin/env python3
from sfcoverage import SfCoverage
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-u', '--username', required=True, help='User name for the org')
parser.add_argument('-p', '--password', required=True, help='Password')
parser.add_argument('-o','--org', required=True, help='The org name that you want to connect to')
parser.add_argument('-d','--domain', required=True, help='Org domain (test, eu..) ')
parser.add_argument('-c', '--classlistfile', required=False, help='File with classes list')
parser.add_argument('-l', '--classlistdir', required=False, help='Directory with classes from package')
parser.add_argument('-r', '--resultfile', required=True, help='Filename for code coverage report')
args = parser.parse_args()

print('===== CHECK CODE COVERAGE ON: ' + args.org + ' =======')

sfCov = SfCoverage(args.username, args.password, args.domain)
sfCov.connect()

#classes = sfCov.get_classes(args.classfilespath)
if args.classlistfile:
  with open(args.classlistfile) as fh:
    classes = fh.readlines()
elif args.classlistdir:
  classes = sfCov.get_classes(args.classlistdir)

results = open(args.resultfile, "w+")
results.write("Class or Trigger,Test Class Name, Number of covered lines,Number of uncovered lines,Number of lines in total,Coverage,Number of tests run,Number of failed tests, Failures details\n")
for className in classes:
  print("=====> Checking coverage for class: " + className.rstrip())
  coverage = sfCov.get_coverage_from_salesforce(className.rstrip(), results)

results.close()
