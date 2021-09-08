import info.ConnectionInfo
import util.ToolingAPIConnector

String userName =  ""
String password = ""
String serverurl = ""
String apiversion = "52.0"

ConnectionInfo connectionInfo = new ConnectionInfo(userName,
                                                   password,
                                                   serverurl,
                                                   apiversion)

ToolingAPIConnector toolingAPIConnector = new ToolingAPIConnector(connectionInfo)
toolingAPIConnector.createConnection()
toolingAPIConnector.exportCodeCoverage("C:/ANT/coverage.csv")
toolingAPIConnector.closeConnection()
