import java.io.*;
import java.util.*;
 
import com.fitbit.api.client.*;
import com.fitbit.api.client.http.*;
import com.fitbit.api.client.service.*;
 
import com.fitbit.api.model.*;
import com.fitbit.api.common.model.user.*;
 
public class FitbitOAuthAccessTokenGetter {
 
  public static void main(String[] args) throws Exception {
 
    String apiBaseUrl = "api.fitbit.com";
    String webBaseUrl = "https://www.fitbit.com";
 
    // https://dev.fitbit.com/apps/new
    // your application's Consumer key and Consumer secret
    String consumerKey = "0123456789abcde0123456789abcde01";
    String consumerSecret = "aabbccddee00112233445566778899aa";
 
    // for receiveing oauth_token and oauth_verifier
    String callbackUrl = "http://www.nilab.info/lab/nilogger/";
 
    FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
    FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
    FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();
    FitbitApiClientAgent apiClientAgent = new FitbitApiClientAgent(apiBaseUrl, webBaseUrl, credentialsCache);
 
    FitbitAPIClientService<FitbitApiClientAgent> apiClientService
      = new FitbitAPIClientService<FitbitApiClientAgent>(
        apiClientAgent,
        consumerKey,
        consumerSecret,
        credentialsCache,
        entityCache,
        subscriptionStore
    );
 
    LocalUserDetail userDetail = new LocalUserDetail("-");
    String authorizationURL = apiClientService.getResourceOwnerAuthorizationURL(userDetail, callbackUrl);
 
    System.out.println("access by web browser: " + authorizationURL);
    System.out.println("Your web browser shows redirected URL.");
    System.out.println("Input the redirected URL and push Enter key.");
 
    // input
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    String redirectUrl = r.readLine();
 
    // deviding query string
    Properties params = getParameters(redirectUrl);
 
    String oauth_token = params.getProperty("oauth_token");
    String oauth_verifier = params.getProperty("oauth_verifier");
    APIResourceCredentials resourceCredentials = apiClientService.getResourceCredentialsByTempToken(oauth_token);
 
    if (resourceCredentials == null) {
      throw new Exception("Unrecognized temporary token when attempting to complete authorization: " + oauth_token);
    }
    if (!resourceCredentials.isAuthorized()) {
      resourceCredentials.setTempTokenVerifier(oauth_verifier);
      apiClientService.getTokenCredentials(new LocalUserDetail(resourceCredentials.getLocalUserId()));
    }
 
    // printing token and token secret
    String userId = resourceCredentials.getLocalUserId();
    String token = resourceCredentials.getAccessToken();
    String tokenSecret = resourceCredentials.getAccessTokenSecret();
    System.out.println("UserId=" + userId);
    System.out.println("Token=" + token);
    System.out.println("TokenSecret=" + tokenSecret);
 
    LocalUserDetail user = new LocalUserDetail(userId);
    FitbitApiClientAgent agent = apiClientService.getClient();
    //agent.setOAuthAccessToken(accessToken);
    UserInfo userInfo = agent.getUserInfo(user);
    System.out.println(userInfo.getNickname());
  }
 
  // dividing query string
  private static Properties getParameters(String url){
    Properties params = new Properties();
    String query_string = url.substring(url.indexOf('?') + 1);
    String[] pairs = query_string.split("&");
    for(String pair : pairs){
      String[] kv = pair.split("=");
      params.setProperty(kv[0], kv[1]);
    }
    return params;
  }
}

