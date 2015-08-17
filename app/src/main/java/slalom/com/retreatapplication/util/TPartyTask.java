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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import slalom.com.retreatapplication.TrendingActivity;
import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.model.CheckIn;

public class TPartyTask extends AsyncTask<Object, Object, Object> {

    private final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
    private final String CHECK_INS = TPARTY_ENDPOINT + "pollparticipantlocations";
    private final String POSTS = TPARTY_ENDPOINT + "pollposts";
    private final String CHECK_IN_USER = TPARTY_ENDPOINT + "checkin";
    private static final String TAG = TPartyTask.class.getSimpleName();
    private String operationName;
    private Activity activityContext;
    private TPartyDBHelper dbHelper;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(Object... args) {
        // all activities that call our service must include a "call" extra so we know what to do
        try {
            operationName = (String)args[0];
            activityContext = (Activity)args[1];
            dbHelper = new TPartyDBHelper(activityContext);

            if ("getCheckIns".equals(operationName)) {
                saveCheckIns(getResp(CHECK_INS));
                refreshActivity(TrendingActivity.class);

            } else if ("checkInUser".equals(operationName)) {
                int USER_ID = (int)args[2];
                int LOCATION_ID = (int)args[3];
                String CHECK_IN_URL = CHECK_IN_USER +"?userId=" + USER_ID +"&locationId="+LOCATION_ID;
                checkInUser(CHECK_IN_URL);
                saveCheckIns(getResp(CHECK_INS));
                //Refresh
                //Intent viewPostsActivity = new Intent(this, ViewPostsActivity.class);
                //startActivity(viewPostsActivity);

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
    //JSON Array response from service call
    private boolean checkInUser(String serviceCall) throws IOException, JSONException {
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

        return (Boolean.valueOf(resp.toString())).booleanValue();
    }

    //JSON Array response from service call
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

    private void refreshActivity(Class activityClass){
        //Refresh View
        Intent activityIntent = new Intent(activityContext, activityClass);
        activityContext.startActivity(activityIntent);
        activityContext.finish();
    }

    private void saveCheckIns(JSONArray checkInsArray) throws JSONException {
        CheckIn checkIn;
        ArrayList<CheckIn> checkIns = new ArrayList<CheckIn>();

        for (int i = 0; i < checkInsArray.length(); i++) {
            JSONObject jObj = checkInsArray.getJSONObject(i);
            checkIn = new CheckIn();
            //checkIn.setCheckinDate(jObj.getString("CheckinTimeStamp"));
            checkIn.setCheckInID(jObj.getLong("CheckinId"));
            checkIn.setLocation(jObj.getString("Location"));
            checkIn.setLocationId(jObj.getLong("LocationId"));
            checkIn.setUserId(jObj.getLong("UserId"));
            checkIn.setUsername(jObj.getString("UserName"));
            checkIns.add(checkIn);
        }
        dbHelper.saveCheckIns(checkIns);
        updateLocations(checkInsArray);

        //Refresh View
        /*
        Intent refresh = new Intent(activityContext, TrendingActivity.class);
        activityContext.startActivity(refresh);
        activityContext.finish();
        */
    }

    private void updateLocations(JSONArray checkInsArray) throws JSONException {
        int locID; int checkInCount;
        Map<Integer, Integer> checkIns = new HashMap<Integer, Integer>();

        for (int i = 0; i < checkInsArray.length(); i++) {
            JSONObject jObj = checkInsArray.getJSONObject(i);
            locID = jObj.getInt("LocationId");
            if (checkIns.containsKey(locID)) {
                checkIns.put(locID, checkIns.get(locID) + 1);
            } else {
                checkIns.put(locID, 1);
            }
        }
        dbHelper.updateLocations(checkIns);
    }


    private void savePosts(JSONArray postsArray) throws IOException, JSONException {
        //...
    }
}
