package util

import com.sforce.soap.partner.GetUserInfoResult
import com.sforce.soap.partner.LoginResult
import groovy.transform.ToString

/**
 * A wrapper for login results
 */
@ToString(includeNames = true)
public class Session {
    public String sessionId
    public String serverUrl
    public String userId
    public String userFullName
    public String userEmail
    public String metadataServerUrl

    /**
     * Create session infos from login results
     * @param loginResult
     */
    public Session(LoginResult loginResult) {
        sessionId = loginResult.sessionId
        serverUrl = loginResult.serverUrl
        metadataServerUrl = loginResult.metadataServerUrl
        GetUserInfoResult userInfo = loginResult.getUserInfo()
        userId = userInfo.userId
        userFullName = userInfo.userFullName
        userEmail = userInfo.userEmail
    }
}
