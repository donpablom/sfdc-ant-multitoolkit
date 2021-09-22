package info

import util.Session
/**
 * Salesforce connection information
 */
public class ConnectionInfo {
    private static final String URL_COLON = ":"
    private static final String URL_DOUBLE_SLASH = "//"

    private static final String PATH_QUERY = "/services/data/v%s/tooling/query/"

    public String userName
    public String password
    public String serverUrl
    public String apiVersion

    public ConnectionInfo(String userName,
                          String password,
                          String serverUrl,
                          String apiVersion) {
        this.userName = userName
        this.password = password
        this.serverUrl = serverUrl
        this.apiVersion = apiVersion
    }

    /**
     * Gets the endpoint from server url.
     * https://test.salesforce.com -> https://cs88...
     * @param session server url from login result
     * @return
     */
    public static String getEndPoint(Session session) {
        URL soapEndpoint = new URL(session.serverUrl)
        StringBuilder endpoint = new StringBuilder()
                .append(soapEndpoint.getProtocol())
                .append("${URL_COLON}${URL_DOUBLE_SLASH}")
                .append(soapEndpoint.getHost())

        if (soapEndpoint.getPort() > 0) {
            endpoint.append("${URL_COLON}").append(soapEndpoint.getPort())
        }

        return endpoint.toString()
    }

    /**
     * @param session server url from login result
     * @return
     */
    public String getSoapURl() {
        String url = serverUrl + "/services/Soap/u/" + apiVersion
        return url
    }

    /**
     * @param session server url from login result
     * @return
     */
    public String getPathQuery(Session session) {
        String url = String.format(PATH_QUERY, apiVersion)
        return url
    }
}
