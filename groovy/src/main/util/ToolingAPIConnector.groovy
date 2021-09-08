package util

import info.ConnectionInfo
import com.sforce.soap.partner.LoginResult
import com.sforce.soap.partner.PartnerConnection
import com.sforce.ws.ConnectorConfig
import groovy.json.JsonOutput

import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

/**
 * A connector to the Salesforce Tooling API
 */
class ToolingAPIConnector {

    private ConnectionInfo connectionInfo
    private Session session

    /**
     * Constructor.
     *
     * @param connectionInfo
     */
    public ToolingAPIConnector(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo
    }

    /**
     * Create a connection and login
     */
    public void createConnection() {
        try {
            ConnectorConfig connectorConfig = createConnectorConfig(null, connectionInfo.getSoapURl())
            PartnerConnection partnerConnection = new PartnerConnection(connectorConfig)
            LoginResult loginResult = partnerConnection.login(connectionInfo.userName,
                                                              connectionInfo.password)
            session = new Session(loginResult)
        } catch (Exception exception) {
            String message = exception.message
            throw new Exception(message, exception)
        }
    }

    /**
     * Logout and destroy the connection
     */
    public void closeConnection() {
        if (session) {
            ConnectorConfig connectorConfig = createConnectorConfig(session.sessionId, session.serverUrl)
            PartnerConnection partnerConnection = new PartnerConnection(connectorConfig)
            partnerConnection.logout()
        } else {
            println 'No session is open'
        }
        session = null
    }

    /**
     * Executes a salesforce object query language using tooling API
     * @param soql the salesforce object query language
     * @return the result from server in JSON format
     */
    public String executeQuery(String soql) {
        HTTPBuilder http = new HTTPBuilder(ConnectionInfo.getEndPoint(session))
        Map<String, String> headers = new HashMap<String, String>()
        headers.put("Authorization", "OAuth " + session.sessionId)
        headers.put("Content-Type", "application/json")
        http.setHeaders(headers)
        String queryResultJson = ""
        def html = http.get( path : connectionInfo.getPathQuery(session),
                             contentType : "application/json",
                             query : [q: """${soql}""" ] ) {resp, reader ->
            queryResultJson = JsonOutput.toJson(reader)
        }
        return queryResultJson
    }

    /**
     * Opens a connection, queries for the code coverage overview and closes the connection.
     * @return List of maps, while each map represents one Apex classes are trigger with the following information:
     * <ul>
     *     <li>Name: Class or trigger name</li>
     *     <li>ApexClassOrTriggerId: Class or trigger Id</li>
     *     <li>NumLinesCovered: covered count of lines</li>
     *     <li>NumLinesUncovered: uncovered count of lines</li>
     *     <li>NumLinesTotal: total count of lines</li>
     *     <li>Coverage: Code coverage</li>
     * </ul>
     */
    public Double getCodeCoverageOverview() {
        String query = "SELECT PercentCovered FROM ApexOrgWideCoverage"
        String queryResultJson = executeQuery(query)
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object result = jsonSlurper.parseText(queryResultJson)
        Double percentCovered = 0.0
        for ( Map record : ((Map)result).get("records")) {
            percentCovered = Double.parseDouble((String)record.get("PercentCovered"))
        }
        return percentCovered
    }

    /**
     * Opens a connection, queries for the code coverage and closes the connection.
     * @return List of maps, while each map represents one Apex classes are trigger with the following information:
     * <ul>
     *     <li>Name: Class or trigger name</li>
     *     <li>ApexClassOrTriggerId: Class or trigger Id</li>
     *     <li>NumLinesCovered: covered count of lines</li>
     *     <li>NumLinesUncovered: uncovered count of lines</li>
     *     <li>NumLinesTotal: total count of lines</li>
     *     <li>Coverage: Code coverage</li>
     * </ul>
     */
    public List<Map<String, Object>> getCodeCoverages() {
        String query = "SELECT    ApexClassOrTriggerId, " +
                                 "ApexClassOrTrigger.Name, " +
                                 "NumLinesCovered, " +
                                 "NumLinesUncovered " +
                       "FROM      ApexCodeCoverageAggregate " +
                       "WHERE     ApexClassOrTriggerId != NULL " +
                             "AND ApexClassOrTrigger.Name != NULL " +
                           //"AND (NumLinesCovered > 0 OR NumLinesUncovered > 0) " +
                           //"AND NumLinesCovered != NULL " +
                           //"AND NumLinesUncovered != NULL " +
                       "ORDER BY  ApexClassOrTrigger.Name"
        String queryResultJson = executeQuery(query)
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object result = jsonSlurper.parseText(queryResultJson)
        List<Map<String, Object>> codeCoverages = new ArrayList<Map<String, Object>>();
        for ( Map record : ((Map)result).get("records")) {
            Map apexClassOrTrigger = record.get("ApexClassOrTrigger")
            String apexClassOrTriggerName = apexClassOrTrigger.get("Name")

            String apexClassOrTriggerId = record.get("ApexClassOrTriggerId")
            Long coveredLines = (null != record.get("NumLinesCovered")) ? Integer.parseInt((String)record.get("NumLinesCovered")) : 0
            Long uncoveredLines = (null != record.get("NumLinesUncovered")) ? Integer.parseInt((String)record.get("NumLinesUncovered")) : 0
            Long totalLines = coveredLines + uncoveredLines
            Double coverage = 0.0
            if ( 0 == totalLines ) {
                coverage = 100.0
            } else {
                coverage = (coveredLines / totalLines) * 100
                coverage = coverage.round(2)
            }

            Map<String, Object> codeCoverage = new HashMap<>()
            codeCoverage.put("Name", apexClassOrTriggerName)
            codeCoverage.put("ApexClassOrTriggerId", apexClassOrTriggerId)
            codeCoverage.put("NumLinesCovered", coveredLines)
            codeCoverage.put("NumLinesUncovered", uncoveredLines)
            codeCoverage.put("NumLinesTotal", coveredLines + uncoveredLines)
            codeCoverage.put("Coverage", coverage)

            codeCoverages.add(codeCoverage)
        }
        codeCoverages = codeCoverages.sort{ a, b -> ((String)a.get("Name")).toLowerCase() <=> ((String)b.get("Name")).toLowerCase() }
        return codeCoverages
    }

    /**
     * Exports the code coverage overview and code coverages for Apex classes as CSV
     * into a file.
     * @param fileName
     */
    public void exportCodeCoverage(String fileName) {
        Double percentCovered = getCodeCoverageOverview()
        List<Map<String, Object>> codeCoverages = getCodeCoverages()
        String content = ""
        content += "Overall code coverage," + percentCovered + "\n"
        content += "\n"
        content += "To get reliable results switch off Setup->Develop->Apex Test Execution->Options->Disable Parallel Apex Testing\n"
        content += "\n"
        content += "Class or Trigger,NumLinesCovered,NumLinesUncovered,NumLinesTotal,Coverage\n"
        for ( Map<String, Object> codeCoverage: codeCoverages ) {
            content += codeCoverage.get("Name") + "," + codeCoverage.get("NumLinesCovered") + "," + codeCoverage.get("NumLinesUncovered") + "," + codeCoverage.get("NumLinesTotal") + "," + codeCoverage.get("Coverage") + "\n"
        }
        File file = new File(fileName)
        file.write(content)
    }


    /**
     * Creates a connector config with the session id and server url
     * @param sessionId the session id provided by salesforce
     * @param serverUrl the server url provided by salesforce
     * @return a connector config to create api connection
     */
    private ConnectorConfig createConnectorConfig(String sessionId, String serverUrl) {
        ConnectorConfig connectorConfig = new ConnectorConfig()
        if (sessionId) {
            connectorConfig.setSessionId(sessionId)
        }
        connectorConfig.setAuthEndpoint(serverUrl)
        connectorConfig.setServiceEndpoint(serverUrl)
        connectorConfig.setManualLogin(true)
        return connectorConfig
    }
}
