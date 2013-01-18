import java.io.*;
 
import com.fitbit.api.client.*;
import com.fitbit.api.client.http.*;
import com.fitbit.api.client.service.*;
 
import com.fitbit.api.model.*;
import com.fitbit.api.common.model.user.*;
 
public class FitbitOAuthAccessTokenGetterPinBased {
 
  public static void main(String[] args) throws Exception {
 
    String apiBaseUrl = "api.fitbit.com";
    String webBaseUrl = "https://www.fitbit.com";
 
    // https://dev.fitbit.com/apps/new
    // your application%#39; Consumer key and Consumer secret
    String consumerKey = "0123456789abcde0123456789abcde01";
    String consumerSecret = "aabbccddee00112233445566778899aa";
 
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
 
    TempCredentials credentials = apiClientAgent.getOAuthTempToken();
    String authorizationURL = credentials.getAuthorizationURL();
 
    System.out.println("access by web browser: " + authorizationURL);
    System.out.println("Your web browser shows a PIN.");
    System.out.println("Input the PIN and push Enter key.");

    // input
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    String pin = r.readLine();
 
    // AccessToken
    AccessToken accessToken = apiClientAgent.getOAuthAccessToken(credentials, pin);
 
    // printing token and token secret
    String userId = accessToken.getEncodedUserId();
    String token = accessToken.getToken();
    String tokenSecret = accessToken.getTokenSecret();
    System.out.println("UserId=" + userId);
    System.out.println("Token=" + token);
    System.out.println("TokenSecret=" + tokenSecret);
 
    System.out.println("TempCredentials#getToken=" + credentials.getToken());
    System.out.println("TempCredentials#getTokenSecret=" + credentials.getTokenSecret());
 
    APIResourceCredentials resourceCredentials = new APIResourceCredentials(userId, token, tokenSecret);
    resourceCredentials.setAccessToken(token);
    resourceCredentials.setAccessTokenSecret(tokenSecret);
    resourceCredentials.setResourceId(userId);
 
    LocalUserDetail user = new LocalUserDetail(userId);
 
    apiClientService.saveResourceCredentials(user, resourceCredentials);
 
    FitbitApiClientAgent agent = apiClientService.getClient();
    UserInfo userInfo = agent.getUserInfo(user);
    System.out.println(userInfo.getFullName());
  }
}

