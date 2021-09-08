import info.EnvironmentInfo
import packagecreator.PackageCreatorForSonarqube
import util.AntWrapper
import info.ConnectionInfo

//Initialize
String userName = "${properties['sf.username']}"
String password = "${properties['sf.password']}"
String serverurl = "${properties['sf.serverurl']}"
String apiversion = "${properties['sf.apiversion']}"
String srcDir = "${properties['component.src']}"
String destDir = "${properties['component.deploy_delta']}"
String tempDir = "${properties['component.temp_dir']}"
String versions = "${properties['git.versions']}"
String gitExecutable = "${properties['git.executable']}"
String branchName = "${properties['sonar.branch']}"
String workingDir = "${properties['working.dir']}"
String organization = "${properties['org']}"

ConnectionInfo connectionInfo = new ConnectionInfo(userName,
                                                   password,
                                                   serverurl,
                                                   apiversion);
EnvironmentInfo environmentInfo = new EnvironmentInfo(srcDir,
                                                      destDir,
                                                      tempDir,
                                                      versions,
                                                      gitExecutable,
                                                      organization)

AntWrapper antWrapper = new AntWrapper();

Filter filter = new Filter(Filter.CreateFor.DEPLOY)
filter.addFilter()

//Create delta deployment package for sonarqube analyze
PackageCreatorForSonarqube packageCreator = new PackageCreatorForSonarqube()
def returnValue = packageCreator.createPackageForSonarAnalyze(connectionInfo, environmentInfo, antWrapper, filter.metadataFilter, branchName, workingDir )

if (null == returnValue) {
    properties['is.error'] = "true"
} else {
    properties['is.error'] = "false"
}
