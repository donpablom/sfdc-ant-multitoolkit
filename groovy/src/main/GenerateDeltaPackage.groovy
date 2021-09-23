import info.EnvironmentInfo
import packagecreator.PackageCreator
import util.AntWrapper
import info.ConnectionInfo

//Initialize
println "#####Initialize####"
String userName = "${properties['sf.username']}"
println "userName:" + userName
String password = "${properties['sf.password']}"
String serverurl = "${properties['sf.serverurl']}"
println "serverurl:" + serverurl
String organization = "${properties['org']}"
println "organization:" + organization
String apiversion = "${properties['sf.apiversion']}"
println "apiversion:" + apiversion
String srcDir = "${properties['component.src']}"
println "srcDir:" + srcDir
String destDir = "${properties['component.deploy_delta']}"
println "destDir:" + destDir
String tempDir = "${properties['component.temp_dir']}"
println "tempDir:" + tempDir
String versions = "${properties['git.versions']}"
println "versions:" + versions
String gitExecutable = "${properties['git.executable']}"
Boolean isHotfix = "${properties['hotfix']}".toBoolean()
println "isHotfix:" + isHotfix

def scriptDir = getClass().protectionDomain.codeSource.location.path
String currentDir = new File(".").getAbsolutePath()
//println "Current location:" + scriptDir
//println "Current user location:" + System.properties['user.dir']
//println "Current base directory:" + "${properties['basedir']}"
//println "Current prtoject build dir :" + "${properties['project.build.directory']}"
//println "Current directory:" + currentDir

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

//Create delta deployment package
PackageCreator packageCreator = new PackageCreator()
packageCreator.createPackage(connectionInfo, environmentInfo, antWrapper, filter.metadataFilter, isHotfix)
