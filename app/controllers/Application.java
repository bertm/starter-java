package controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.Configuration;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

@Singleton
public class Application extends Controller {

    // Load configuration from [project]/conf/application.conf
    private final String twilioAccountSid;
    private final String twilioAuthToken;
    private final String twilioNumber;

    // create an authenticated REST client
    private final TwilioRestClient client;
    private final Account mainAccount;

    @Inject public Application(Configuration configuration) {
        twilioAccountSid = configuration.getString("twilio.accountSid");
        twilioAuthToken = configuration.getString("twilio.authToken");
        twilioNumber = configuration.getString("twilio.number");

        client = new TwilioRestClient(twilioAccountSid, twilioAuthToken);
        mainAccount = client.getAccount();
    }
  
    // Render the home page for our application
    public Result index() {
        return ok(index.render());
    }

    // Handle a POST request to make an outbound call
    public Result call() throws TwilioRestException {
        // Get POST data
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        String to = params.get("to")[0];

        // Make a call
        CallFactory callFactory = mainAccount.getCallFactory();
        Map<String, String> callParams = new HashMap<String, String>();
        callParams.put("To", to);
        callParams.put("From", twilioNumber);
        callParams.put("Url", "http://twilio-elearning.herokuapp.com/starter/voice.php");
        callFactory.create(callParams);
        
        return ok("Call is inbound!");
    }

    // Handle a POST request to send a text message
    public Result message() throws TwilioRestException {
    	// Get POST data
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        String to = params.get("to")[0];
    	
    	SmsFactory smsFactory = mainAccount.getSmsFactory();
        Map<String, String> smsParams = new HashMap<String, String>();
        smsParams.put("To", to);
        smsParams.put("From", twilioNumber);
        smsParams.put("Body", "Have fun with your Twilio development!");
        smsFactory.create(smsParams);
    	
        return ok("Message incoming!");
    }

    // Render a TwiML document to give instructions for an outbound call
    public Result hello() throws TwiMLException {
        TwiMLResponse response = new TwiMLResponse();
        Say one = new Say("Hello there! You have successfully configured a web hook.");
        Say two = new Say("Have fun with your Twilio development!");
        two.setVoice("woman");

        response.append(one);
        response.append(two);

        response().setContentType("text/xml");
        return ok(response.toXML());
    }
  
}
