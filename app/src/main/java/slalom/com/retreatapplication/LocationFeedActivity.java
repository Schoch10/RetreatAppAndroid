package slalom.com.retreatapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
import java.util.List;


import slalom.com.retreatapplication.db.CheckInContract;
import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.util.PostObject;

public class LocationFeedActivity extends Activity {

    private static final String TAG = LocationFeedActivity.class.getSimpleName();
    //private static final int MIN_DISTANCE = 175;
    private TextView test_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Integer locationId = intent.getIntExtra("locationId", 3);

        //Need an object that stores location > image mappings
        int imageRsrc = -1;
        switch(locationId) {
            case 3: imageRsrc = R.mipmap.omni; break;
            case 4: imageRsrc = R.mipmap.golf; break;

            default: imageRsrc = R.mipmap.omni; break;
        }

        setContentView(R.layout.location_feed_activity);


        ImageView test_image = (ImageView)findViewById(R.id.test_image);

        test_image.setImageResource(imageRsrc);

        test_text = (TextView)findViewById(R.id.test_text);


        TPartyDBHelper dbHelper = new TPartyDBHelper(this);

        List<PostObject> localPosts = dbHelper.getLocalPosts(locationId);

        for (PostObject post: localPosts) {
            Log.d(TAG, post.postId().toString());
            Log.d(TAG, post.userId().toString());
            Log.d(TAG, post.userName().toString());
            Log.d(TAG, post.locationId().toString());
            Log.d(TAG, post.image().toString());
            Log.d(TAG, post.text().toString());
            Log.d(TAG, post.timestamp().toString());
        }

        getPostsAsync getPostsRunner = new getPostsAsync();
        getPostsRunner.execute(locationId);
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX > 0) {
                        //swipe right
                        Log.d(TAG, "swipe right? x1: " + x1 + ", x2: " + x2);

                        // Need an object that stores location id > rank mapping
                        // so when user swipes we increment or decrement rank, and
                        // and get loc id associated with that rank
                        Intent nextActivityIntent = new Intent(this, LocationFeedActivity.class);
                        nextActivityIntent.putExtra(LOC_ID_EXTRA, currentLocId+1);
                        startActivity(nextActivityIntent);

                } else if (deltaX < 0) {
                        //swipe left
                        Log.d(TAG, "swipe left? x1: " + x1 + ", x2: " + x2);

                        Intent nextActivityIntent = new Intent(this, LocationFeedActivity.class);
                        nextActivityIntent.putExtra(LOC_ID_EXTRA, currentLocId-1);
                        startActivity(nextActivityIntent);

                    }
                }

                break;
        }

        return true;

    }
    */

    private class getPostsAsync extends AsyncTask<Integer, String, String> {

        Integer locationId;
        String response;

        @Override
        protected String doInBackground(Integer... location) {
            publishProgress("Getting latest post...");
            locationId = location[0];
            savePosts(locationId, getPosts(locationId));

            return "success!";
        }

        @Override
        protected void onProgressUpdate(String... update) {
            test_text.setText(update[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            TPartyDBHelper dbHelper = new TPartyDBHelper(LocationFeedActivity.this);
            List<PostObject> localPosts = dbHelper.getLocalPosts(locationId);

            test_text.setText(localPosts.toString());
        }

        private JSONArray getPosts(Integer locationId) {

            JSONArray respJArray = new JSONArray();

            try {

                String postCall = "http://tpartyservice-dev.elasticbeanstalk.com/home/getpostsforlocation?locationid=" + locationId.toString();
                URL url = new URL(postCall);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Since all REST responses will be arrays of JSON we can just package them up
                // as JSONArrays and send them back to the calling method
                StringBuilder responseBuilder = new StringBuilder();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = r.readLine()) != null) {
                    responseBuilder.append(line);
                }

                response = responseBuilder.toString();

                Log.d(TAG, response);

                respJArray = new JSONArray(response);


            } catch (IOException|JSONException e) {
                Log.d(TAG, e.toString());
            }

            return respJArray;

        }


        private void savePosts(Integer locationId, JSONArray respArray) {

            List<PostObject> posts = new ArrayList<PostObject>();

            Integer postId;
            Integer userId;
            String userName;
            Integer currentLocationId;
            String image;
            String text;
            String timestampStr;
            Long timestamp;

            for (int i = 0; i < respArray.length(); i++) {
                try {
                    JSONObject jObj = respArray.getJSONObject(i);

                    postId = jObj.getInt("PostId");
                    userId = jObj.getInt("UserId");
                    userName = jObj.getString("UserName");
                    currentLocationId = jObj.getInt("LocationId");
                    image = jObj.getString("S3ImageUrl");
                    text = jObj.getString("PostText");
                    timestampStr = jObj.getString("PostTS").substring(6, 19);
                    timestamp = Long.parseLong(timestampStr);

                    PostObject post = new PostObject(postId, userId, userName, currentLocationId, image, text, timestamp);
                    posts.add(post);

                } catch (JSONException jE) {
                    Log.d(TAG, jE.toString());
                }

            }

            TPartyDBHelper dbHelper = new TPartyDBHelper(LocationFeedActivity.this);
            dbHelper.savePosts(locationId, posts);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activities, menu);
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
}
