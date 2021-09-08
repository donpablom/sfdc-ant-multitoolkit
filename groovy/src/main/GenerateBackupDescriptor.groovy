import util.AntWrapper
import info.ConnectionInfo
import info.EnvironmentRetrieverInfo
import util.FilterSet
import retriever.Retriever
import util.MetadataTypes

import java.util.regex.Pattern

//Initialize
String userName = "${properties['sf.username']}"
String password = "${properties['sf.password']}"
String serverurl = "${properties['sf.serverurl']}"
String apiversion = "${properties['sf.apiversion']}"
String targetDir = "${properties['git.backup.folder']}"
String tempDir = "${properties['component.backup.temp']}"
String forceExecutable = "${properties['force.executable']}"


ConnectionInfo connectionInfo = new ConnectionInfo(userName,
                                                   password,
                                                   serverurl,
                                                   apiversion);
EnvironmentInfo environmentInfo = new EnvironmentInfo(targetDir,
                                                      tempDir,
                                                      forceExecutable)
AntWrapper antWrapper = new AntWrapper();

Filter filter = new Filter(Filter.CreateFor.RETRIEVE)
filter.metadataFilter.skipInstalledPackages = true
filter.addFilter()

//For backup purposes, filter out those metadata components
//where there are too many components
if (true) {
    List toKeep = new ArrayList();
    List toSkip = new ArrayList();
    toSkip.add(Pattern.compile(/^roles\/.*\.role$/))
    filter.metadataFilter.fileNamesFilterSet.put("Role", new FilterSet(toKeep, toSkip))
}
if (true) {
    List toKeep = new ArrayList();
    List toSkip = new ArrayList();
    toKeep.add(Pattern.compile(/^territories\/.*\.territory$/))
    filter.metadataFilter.fileNamesFilterSet.put("Territory", new FilterSet(toKeep, toSkip))
}

//Retrieve
Retriever retriever = new Retriever()
MetadataTypes metadataTypes = retriever.retrieve(connectionInfo, environmentInfo, antWrapper, filter.metadataFilter)
println "Found " + metadataTypes.xmlName2MetadataType.size() + "  metadata types"

//Write package.xml
metadataTypes.writePackageXMLs(connectionInfo, environmentInfo, antWrapper)
