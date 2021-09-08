package util
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.sobject.*
import com.sforce.soap.partner.*
import com.sforce.ws.ConnectorConfig
import com.sforce.ws.ConnectionException


PartnerConnection partnerConnection = null
boolean success = false
String username =  ""
String password = ""
String authEndPoint = "https://login.salesforce.com/services/Soap/u/52.0"

try {
    ConnectorConfig config = new ConnectorConfig()
    config.setUsername(username)
    config.setPassword(password)

    config.setAuthEndpoint(authEndPoint)
    config.setTraceFile("traceLogs.txt")
    config.setTraceMessage(true)
    config.setPrettyPrintXml(true)

    partnerConnection = new PartnerConnection(config)

    partnerConnection.setQueryOptions(250)

    // SOQL query to use
    String soqlQuery = "select id,ApexTestClassId,ApexTestClass.Name,ApexClassorTrigger.Name,NumLinesCovered,NumLinesUncovered from ApexCodeCoverage"
    // Make the query call and get the query results
    QueryResult qr = partnerConnection.query(soqlQuery)

    boolean done = false
    // Loop through the batches of returned results
    while (!done) {
        SObject[] records = qr.getRecords()
        // Process the query results
        for (int i = 0; i < records.length; i++) {
            SObject contact = records[i]
            println("CoverageRecord: "+ contact)
            if (qr.isDone()) {
                done = true
            } else {
                qr = partnerConnection.queryMore(qr.getQueryLocator())
            }
        }
    }
    success = true
} catch (ConnectionException ce) {
    ce.printStackTrace()
} catch (FileNotFoundException fnfe) {
    fnfe.printStackTrace()
}