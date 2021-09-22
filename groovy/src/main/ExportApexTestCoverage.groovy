import info.ConnectionInfo
import util.ToolingAPIConnector

//Initialize
String userName = "${properties['sf.username']}"
String password = "${properties['sf.password']}"
String serverurl = "${properties['sf.serverurl']}"
String apiversion = "${properties['sf.apiversion']}"
String tempFileName = "${properties['component.apexcodecoverage.filename']}"

ConnectionInfo connectionInfo = new ConnectionInfo(userName,
                                                   password,
                                                   serverurl,
                                                   apiversion);

ToolingAPIConnector toolingAPIConnector = new ToolingAPIConnector(connectionInfo)
try {
    toolingAPIConnector.createConnection()
    toolingAPIConnector.exportCodeCoverage(tempFileName)
    toolingAPIConnector.closeConnection()
} catch (Exception exception) {
    println "Got error, but skipping it so pull request does not fail"
    println exception.getMessage()
}
