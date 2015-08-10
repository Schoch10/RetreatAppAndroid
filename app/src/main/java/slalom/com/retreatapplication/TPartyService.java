package slalom.com.retreatapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by alexanderp on 8/9/2015.
 */
public class TPartyService extends IntentService {

    private final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
    private final String CHECK_INS = TPARTY_ENDPOINT + "pollparticipantlocations";
    private final String POSTS = TPARTY_ENDPOINT + "pollposts";
    private static final String TAG = TPartyService.class.getSimpleName();


    public TPartyService() {
        super("TpartyDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // all activities that call our service must include a "call" extra so we know what to do
        try {
            switch (intent.getStringExtra("call")) {
                case "getCheckIns":
                    saveCheckIns(getResp(CHECK_INS));
                    break;

                case "getPosts":
                    savePosts(getResp(POSTS));
                    break;
            }
        } catch (Exception e) {
            //What should we do here?;
        }

    }

    private JSONArray getResp(String serviceCall) throws IOException, JSONException {
        HttpURLConnection urlConnection;

        URL url = new URL(serviceCall);
        urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder resp = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            resp.append(line);
        }

        JSONArray resp_jArray = new JSONArray(resp.toString());

        return resp_jArray;
    }


    private void saveCheckIns(JSONArray checkInsArray) throws JSONException {

        String locName;
        Map<String, Integer> checkIns = new HashMap<String, Integer>();

        for (int i = 0; i < checkInsArray.length(); i++) {
            JSONObject jObj = checkInsArray.getJSONObject(i);
            locName = jObj.getString("LocationName");
            if (checkIns.containsKey(locName)) {
                checkIns.put(locName, checkIns.get(locName) + 1);
            } else {
                checkIns.put(locName, 1);
            }
            Log.d(TAG, checkIns.toString());
        }

        //...

    }


    private void savePosts(JSONArray postsArray) throws IOException, JSONException {
        //...
    }
}
