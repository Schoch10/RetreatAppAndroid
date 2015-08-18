package slalom.com.retreatapplication.util;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

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
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import slalom.com.retreatapplication.LocationFeedActivity;
import slalom.com.retreatapplication.TrendingActivity;
import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.model.CheckIn;

public class TPartyTask extends AsyncTask<Object, Object, Object> {
    private static final String TAG = TPartyTask.class.getSimpleName();
    private final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
    private final String CHECK_INS = TPARTY_ENDPOINT + "pollparticipantlocations";
    private final String POSTS = TPARTY_ENDPOINT + "pollposts";
    private final String CHECK_IN_USER = TPARTY_ENDPOINT + "checkin";
    private final String SAVE_POST = TPARTY_ENDPOINT + "DoPost";
    private TPartyDBHelper dbHelper;
    private Class activityClass;
    private String operationName;
    private Intent activityIntent;
    private Activity activityContext;
    private Bundle bundle;
    private int userId; private int locationId;
    private String postText; private String serviceURL;

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
                refreshActivity(TrendingActivity.class, null);

            } else if ("checkInUser".equals(operationName)) {
                //Construct Service url
                userId = (int)args[2];
                locationId = (int)args[3];
                serviceURL = CHECK_IN_USER +"?userId=" + userId +"&locationId="+locationId;

                //check In
                checkInUser(serviceURL);

                //fetch CheckIns
                saveCheckIns(getResp(CHECK_INS));

            } else if ("getPosts".equals(operationName)) {
                //savePosts(getResp(POSTS));

            }else if ("refreshActivity".equals(operationName)) {
                activityClass = (Class)args[2];
                if(args[3]!=null) bundle = (Bundle)args[3];
                refreshActivity(activityClass, bundle);

            } else if ("getCheckIns".equals(operationName)) {
                saveCheckIns(getResp(CHECK_INS));

            } else if ("savePost".equals(operationName)) {
                //Construct Service url
                userId = (int)args[2];
                locationId = (int)args[3];
                postText = (String)args[4];
                serviceURL = SAVE_POST +"?userId=" + userId +"&locationId="+locationId +"&postText="+postText;

                //Save post
                //savePost(serviceURL);
                savePost(SAVE_POST, userId, locationId, postText);
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

    private void refreshActivity(Class activityClass, Bundle bundle){
        //Refresh View
        activityIntent = new Intent(activityContext, activityClass);
        if(bundle!=null) activityIntent.putExtras(bundle);
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
    }

    private void updateLocations(JSONArray checkInsArray) throws JSONException {
        int locID;
        int checkInCount;
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

    private void savePost(String serviceCall, int userId, int locationId, String postText) throws IOException, JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(serviceCall);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("UserId", String.valueOf(userId)));
            nameValuePairs.add(new BasicNameValuePair("LocationId", String.valueOf(locationId)));
            nameValuePairs.add(new BasicNameValuePair("postText", postText));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }

        //Refresh View
        bundle = new Bundle();
        bundle.putLong("locationId", locationId);
        refreshActivity(LocationFeedActivity.class, bundle);
    }
}
