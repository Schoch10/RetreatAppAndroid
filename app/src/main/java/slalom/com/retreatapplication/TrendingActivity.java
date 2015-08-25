package slalom.com.retreatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.model.CheckIn;
import slalom.com.retreatapplication.model.Location;
import slalom.com.retreatapplication.util.CustomArrayAdapter;
import slalom.com.retreatapplication.util.TPartyTask;


public class TrendingActivity extends AppCompatActivity {
    private Activity activityContext;
    private Intent activityIntent;
    private TPartyDBHelper dbHelper;
    private ArrayList<Location> locations;
    private Location location;
    private CustomArrayAdapter customArrayAdapter;
    private Bundle bundle;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);
        activityContext = this;

        //Get latest CheckIns from REST service and update local store
        new LocationsAsyncTask().execute(this);

        //Get latest CheckIns from local store
        dbHelper = new TPartyDBHelper(this);
        locations = dbHelper.getLocations();

        //Using adapter class for rendering
        customArrayAdapter = new CustomArrayAdapter(this, R.layout.activity_trending, locations);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(customArrayAdapter);

        // OnItem click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // When Button is clicked
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                location = locations.get(position);
                bundle = new Bundle();
                bundle.putString("locationName", location.getLocationName());
                bundle.putLong("locationId", location.getLocationId());

                activityIntent = new Intent(activityContext, LocationFeedActivity.class);
                activityIntent.putExtras(bundle);
                activityContext.startActivity(activityIntent);
            }
        });

        dbHelper.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LocationsAsyncTask().execute(this);

    }
    public void refreshCheckInsSelected(View view) {
        // Trigger Async Task
        new LocationsAsyncTask().execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agenda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private class LocationsAsyncTask extends AsyncTask<Object, Object, Object>  {
        private final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
        private final String CHECK_INS = TPARTY_ENDPOINT + "pollparticipantlocations";

        protected String doInBackground(Object... args) {
            // all activities that call our service must include a "call" extra so we know what to do
            try {
                activityContext = (Activity)args[0];
                dbHelper = new TPartyDBHelper(activityContext);

                saveCheckIns(getResp(CHECK_INS));

                dbHelper.close();

            } catch (Exception e) {
                //What should we do here?;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object arg) {
            //Get latest CheckIns from local store
            dbHelper = new TPartyDBHelper(TrendingActivity.this);
            locations = dbHelper.getLocations();

            //Using adapter class for rendering
            customArrayAdapter = new CustomArrayAdapter(TrendingActivity.this, R.layout.activity_trending, locations);
            listView = (ListView) findViewById(R.id.listView1);
            listView.setAdapter(customArrayAdapter);

            dbHelper.close();
        }
    }
}
