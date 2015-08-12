package slalom.com.retreatapplication.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import slalom.com.retreatapplication.TrendingActivity;
import slalom.com.retreatapplication.db.TPartyDBHelper;

public class TrendingTask extends AsyncTask<Object, Object, Object> {

    protected final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
    protected final String CHECK_INS = TPARTY_ENDPOINT + "pollparticipantlocations";
    protected final String POSTS = TPARTY_ENDPOINT + "pollposts";
    protected static final String TAG = TrendingTask.class.getSimpleName();
    protected String operationName;
    protected Context context;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Object... args) {

        // all activities that call our service must include a "call" extra so we know what to do
        try {
            operationName = (String)args[0];
            context = (Context)args[1];

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
        //TrendingActivity activity = new TrendingActivity();
        //activity.reload();
    }

    protected JSONArray getResp(String serviceCall) throws IOException, JSONException {
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

    protected void saveCheckIns(JSONArray checkInsArray) throws JSONException {

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

        //TPartyDBHelper dbHelper = new TPartyDBHelper(context);
        //ArrayList trendingValues = dbHelper.saveCheckIns(checkIns);

    }


    protected void savePosts(JSONArray postsArray) throws IOException, JSONException {
        //...
    }
}