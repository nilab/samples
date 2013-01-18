import java.util.*;
 
import org.joda.time.LocalDate;
 
import com.fitbit.api.client.*;
import com.fitbit.api.client.service.*;
 
import com.fitbit.api.model.*;
import com.fitbit.api.common.model.activities.*;
import com.fitbit.api.common.model.body.*;
import com.fitbit.api.common.model.sleep.*;
import com.fitbit.api.common.model.user.*;
import com.fitbit.api.common.service.*;
 
public class FitbitUserInfoPrinter {
 
  public static void main(String[] args) throws Exception {
 
    String apiBaseUrl = "api.fitbit.com";
    String webBaseUrl = "https://www.fitbit.com";
 
    // https://dev.fitbit.com/apps/new
    // your application%#39; Consumer key and Consumer secret
    String consumerKey = "0123456789abcde0123456789abcde01";
    String consumerSecret = "aabbccddee00112233445566778899aa";
 
    // user' Token and TokenSecret
    String userId = "-";
    String token = "1234512345abcdeabcde6789067890fg";
    String tokenSecret = "abcabcdefdef123123456456ghighijk";
 
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
 
    LocalUserDetail user = new LocalUserDetail(userId);
 
    APIResourceCredentials resourceCredentials = new APIResourceCredentials(userId, token, tokenSecret);
    resourceCredentials.setAccessToken(token);
    resourceCredentials.setAccessTokenSecret(tokenSecret);
 
    apiClientService.saveResourceCredentials(user, resourceCredentials);
 
    FitbitApiClientAgent agent = apiClientService.getClient();
 
    UserInfo userInfo = agent.getUserInfo(user);
    System.out.println(userInfo.getNickname());
 
    LocalDate date = FitbitApiService.getValidLocalDateOrNull("2013-01-11");
 
    // activity
    System.out.println("***** Activity *****");
    Activities activities = agent.getActivities(user, FitbitUser.CURRENT_AUTHORIZED_USER, date);
    ActivitiesSummary activitiesSummary = activities.getSummary();
    System.out.println(activitiesSummary.getCaloriesOut() + " calories burned");
    System.out.println("Elevation: " + activitiesSummary.getElevation());
    System.out.println(activitiesSummary.getFloors() + " floors climbed");
    System.out.println("Sedentary Minutes: " + activitiesSummary.getSedentaryMinutes() + "min");
    System.out.println("Very Active Minutes: " + activitiesSummary.getVeryActiveMinutes() + "min");
    System.out.println(activitiesSummary.getSteps() + " steps taken");
    for(ActivityDistance activityDistance : activitiesSummary.getDistances()){
      System.out.println("Distance(" + activityDistance.getActivity() + "): " + activityDistance.getDistance() + " km");
    }
 
    // sleep
    System.out.println("***** Sleep *****");
    Sleep sleep = agent.getSleep(user, FitbitUser.CURRENT_AUTHORIZED_USER, date);
    List<SleepLog> sleepLogList= sleep.getSleepLogs();
    for(SleepLog sleepLog : sleepLogList){
      if(sleepLog.isMainSleep()){
        System.out.println("Actual sleep time: " + (sleepLog.getMinutesAsleep() / 60) + "hrs " + (sleepLog.getMinutesAsleep() % 60) + "min");
        System.out.println("Bed time: " + sleepLog.getStartTime());
        System.out.println("Fell asleep in: " + sleepLog.getMinutesToFallAsleep() + "min");
        System.out.println("Awakened: " + sleepLog.getAwakeningsCount() + " times");
        System.out.println("In bed time: " + (sleepLog.getTimeInBed() / 60) + "hrs " + (sleepLog.getTimeInBed() % 60) + "min");
        System.out.println("Sleep efficiency: " + sleepLog.getEfficiency() + "%");
        System.out.println("Duration: " + sleepLog.getDuration());
        System.out.println("After Wakeup: " + sleepLog.getMinutesAfterWakeup() + "min");
        System.out.println("Awake: " + sleepLog.getMinutesAwake() + "min");
      }
    }
 
    // weight
    System.out.println("***** Weight *****");
    List<WeightLog> wwightLogLst = agent.getLoggedWeight(user, FitbitUser.CURRENT_AUTHORIZED_USER, date);
    for(WeightLog weightLog : wwightLogLst){
      System.out.println("Date: " + weightLog.getDate());
      System.out.println("Time: " + weightLog.getTime());
      System.out.println("Weight: " + weightLog.getWeight());
      System.out.println("BMI: " + weightLog.getBmi());
    }
  }
}

