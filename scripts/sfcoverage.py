import requests
import json
import os
from simple_salesforce import Salesforce

class SfCoverage:
  def __init__(self, username, password, doma):
    self.username = username
    self.password = password
    self.doma = doma

  def connect(self):
    """ Connect to salesforce"""
    session = requests.Session()
    session.headers.update({"Content-Type": "application/json"})
    # manipulate the session instance (optional)
    self.sf = Salesforce(
    username=self.username, password=self.password, security_token='', domain=self.doma, version='52.0',
    session=session)

  def get_classes(self, dir):
    """Get classes from package, skip meta.xml files and test classes (files with _Test).
      :returns: list with class names from package
    """
    classes = []
    for file in os.listdir(dir):
      if file.endswith(".cls") and "_Test" not in file:
        classes.append(file[:-4])
    return classes

  def get_coverage_from_salesforce(self, class_name, result_file):
    """Get information about covered and uncovered lines for given classes.
    Calculate coverage, save all information to file and return coverage.
    :param class_name - class for which query will be run
    :param result_file - file with coverage results
    :returns: coverage for class"""
    coverage = 0.0
    lines_checked = 0
    lines_uncovered = 0
    failed_tests = 0
    all_tests = 0
    failures = "None"
    try:
      with open('class_exceptions.json', 'r') as f:
        exceptions = json.load(f)['exceptions']

      if (class_name in exceptions):
        class_test = exceptions[class_name]
      else:
        class_test = class_name + "_Test"

      requestBody = { "tests" : [{ "className" : "{}".format(class_test) }]}
      result = self.sf.restful('tooling/runTestsSynchronous', method='POST', data=json.dumps(requestBody))
      all_tests = int(result['numTestsRun']) if result['numTestsRun'] else 0
      failed_tests = int(result['numFailures']) if result['numFailures'] else 0
      if result['failures']:
        failures = "\""
        for failure in result['failures']:
          failures += failure['name'] + ':' + failure['methodName'] + ' -> ' + failure['message'] + ' -> ' + failure['stackTrace'] + "\n"
        failures += "\""

      lines_checked = int(result['codeCoverage'][0]['numLocations']) if result['codeCoverage'][0]['numLocations'] else 0
      lines_uncovered = int(result['codeCoverage'][0]['numLocationsNotCovered']) if result['codeCoverage'][0]['numLocationsNotCovered'] else 0


    except Exception as e:
      print(type(e))
      print(e)

    if lines_checked != 0:
      coverage = 100 - ((lines_uncovered * 100) / lines_checked)

    result_file.write("{0},{1},{2},{3},{4},{5:.2f},{6},{7},{8}\n".format(class_name, class_test, lines_checked - lines_uncovered, lines_uncovered, lines_checked, coverage, all_tests, failed_tests, failures ))
    print("====== For {} run tests from: {} ====".format(class_name, class_test))
    print("Lines covered: {} \nLines uncovered {} \nLines in total: {} \n\nCoverage: {:.2f}% \n".format(lines_checked - lines_uncovered, lines_uncovered, lines_checked, coverage))
    print("Tests run: {}\nTests failed: {}".format(all_tests, failed_tests))
    print("Failures details: \n\t{}".format(failures.replace("\"","").replace("\n","\n\t")))

    return float("{0:.2f}".format(coverage))
    #return lines_checked
