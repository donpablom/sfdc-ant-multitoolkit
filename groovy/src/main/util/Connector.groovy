package util

import info.ConnectionInfo
import com.sforce.soap.partner.LoginResult
import com.sforce.soap.partner.PartnerConnection
import com.sforce.ws.ConnectorConfig

/**
 * Provides a connection to Salesforce
 */
public class Connector {

    private static final String EXCEPTION_PROPERTY_NAME = 'exceptionMessage'

    private PartnerConnection partnerConnection
    private ConnectionInfo connectionInfo
    private Session session

    /**
     * Constructs a connection
     * @param connectionInfo
     */
    public Connector(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo
        ConnectorConfig connectorConfig = createConnectorConfig(null, connectionInfo.getSoapURl())
        partnerConnection = new PartnerConnection(connectorConfig)
    }

    /**
     * Login to access web services of salesforce with a user credential
     * @return a session with server url, session id and the user info
     */
    public Session login() {
        try {
            LoginResult loginResult = partnerConnection.login(connectionInfo.userName,
                                                              connectionInfo.password)
            session = new Session(loginResult)
            return session
        } catch (Exception exception) {
            String message = exception.message
            throw new Exception(message, exception)
        }
    }

    /**
     * Logout if exist an open session
     */
    public void logout() {
        if (session) {
            ConnectorConfig connectorConfig = createConnectorConfig(session.sessionId, session.serverUrl)
            partnerConnection = new PartnerConnection(connectorConfig)
            partnerConnection.logout()
        } else {
            println 'No session is open'
        }
    }

    /**
     * Gets the partner server url if exist an open session
     * @return a partner server url
     */
    public String getPartnerServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl.replaceAll("/m/", "/u/")
        }
        return url
    }

    /**
     * Gets the apex server url if exist an open session
     * @return a apex server url
     */
    public String getApexServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl.replaceAll("/m/", "/s/")
        }
        return url
    }

    /**
     * Gets the tooling server url if exist an open session
     * @return a tooling server url
     */
    public String getToolingServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl.replaceAll("/m/", "/T/")
        }
        return url
    }

    /**
     * Gets the metadata server url if exist an open session
     * @return a partner server url
     */
    public String getMetadataServerUrl() {
        String url
        if (session) {
            url = session.metadataServerUrl
        }
        return url
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
