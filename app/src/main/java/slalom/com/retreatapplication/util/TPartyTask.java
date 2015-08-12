package slalom.com.retreatapplication.util;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import slalom.com.retreatapplication.TrendingActivity;
import slalom.com.retreatapplication.db.TPartyDBHelper;

public class TPartyTask extends AsyncTask<Object, Object, Object> {

    private final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
    private final String CHECK_INS = TPARTY_ENDPOINT + "pollparticipantlocations";
    private final String POSTS = TPARTY_ENDPOINT + "pollposts";
    private static final String TAG = TPartyTask.class.getSimpleName();
    private String operationName;
    private Activity activityContext;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Object... args) {
        // all activities that call our service must include a "call" extra so we know what to do
        try {
            operationName = (String)args[0];
            activityContext = (Activity)args[1];

            if ("getCheckIns".equals(operationName)) {
                saveCheckIns(getResp(CHECK_INS));

            } else if ("getPosts".equals(operationName)) {
                savePosts(getResp(POSTS));

            }

        } catch (Exception e) {
            //What should we do here?;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object arg) {
        //
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
            locName = jObj.getString("Location");
            if (checkIns.containsKey(locName)) {
                checkIns.put(locName, checkIns.get(locName) + 1);
            } else {
                checkIns.put(locName, 1);
            }
            Log.d(TAG, checkIns.toString());
        }

        TPartyDBHelper dbHelper = new TPartyDBHelper(activityContext);
        dbHelper.saveCheckIns(checkIns);

        Intent refresh = new Intent(activityContext, TrendingActivity.class);
        activityContext.startActivity(refresh);
        activityContext.finish();
    }


    private void savePosts(JSONArray postsArray) throws IOException, JSONException {
        //...
    }
}
