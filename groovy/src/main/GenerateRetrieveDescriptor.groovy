import util.AntWrapper
import info.ConnectionInfo
import info.EnvironmentRetrieverInfo
import util.FilterSet
import util.InstalledPackageFilterSet
import retriever.Retriever
import util.MetadataType
import util.MetadataTypes

import java.util.regex.Pattern

//Initialize
String userName = "${properties['sf.username']}"
String password = "${properties['sf.password']}"
String serverurl = "${properties['sf.serverurl']}"
String apiversion = "${properties['sf.apiversion']}"
String targetDir = "${properties['component.retrieve.target']}"
String tempDir = "${properties['component.retrieve.temp']}"
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

//Retrieve
Retriever retriever = new Retriever()
MetadataTypes metadataTypes = retriever.retrieve(connectionInfo, environmentInfo, antWrapper, filter.metadataFilter)
println "Found " + metadataTypes.xmlName2MetadataType.size() + "  metadata types"

//Write package.xml
metadataTypes.writePackageXMLs(connectionInfo, environmentInfo, antWrapper)
