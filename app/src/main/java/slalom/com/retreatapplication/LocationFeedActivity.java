package slalom.com.retreatapplication;

import android.app.Activity;
import android.content.Intent;
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

import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.util.PostObject;

public class LocationFeedActivity extends Activity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    //private static final int MIN_DISTANCE = 175;
    private final String LOC_ID_EXTRA = "locationId";
    private final int OMNI_ID = 1;
    private Integer currentLocId;
    private float x1,x2;
    private TextView test_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        currentLocId = intent.getIntExtra(LOC_ID_EXTRA, OMNI_ID);

        //Need an object that stores location > image mappings
        int imageRsrc = -1;
        switch(currentLocId) {
            case 0: imageRsrc = R.mipmap.omni; break;
            case 1: imageRsrc = R.mipmap.golf; break;

            default: imageRsrc = R.mipmap.omni; break;
        }

        setContentView(R.layout.location_feed_activity);


        ImageView test_image = (ImageView)findViewById(R.id.test_image);

        test_image.setImageResource(imageRsrc);

        test_text = (TextView)findViewById(R.id.test_text);

        getPostsAsync getPostsRunner = new getPostsAsync();
        getPostsRunner.execute(currentLocId);
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

        String response;

        @Override
        protected String doInBackground(Integer... locationId) {
            publishProgress("Getting latest post...");
            String location = locationId[0].toString();
            try {
                savePosts(getPosts(location));
            } catch (IOException ioE) {
                ;
            } catch (JSONException jE) {
                ;
            }
            return "success!";
        }

        @Override
        protected void onProgressUpdate(String... update) {
            test_text.setText(update[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            test_text.setText(result);
        }

        private JSONArray getPosts(String locationId) throws IOException, JSONException {
            try {

                Thread.sleep(3000);

                String postCall = "http://tpartyservice-dev.elasticbeanstalk.com/home/getpostsforlocation?locationid=" + locationId;
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

            } catch (IOException ioE) {
                ;
            } catch (InterruptedException iE) {
                ;
            }

            Log.d(TAG, response);

            JSONArray resp_jArray = new JSONArray(response);

            return resp_jArray;
        }


        private void savePosts(JSONArray checkInsArray) throws JSONException {

            ArrayList<PostObject> posts = new ArrayList<PostObject>();
            String locName;

            for (int i = 0; i < checkInsArray.length(); i++) {
                JSONObject jObj = checkInsArray.getJSONObject(i);

                Integer postId = jObj.getInt("PostId");
                Integer userId = jObj.getInt("UserId");
                String userName = jObj.getString("UserName");
                Integer locationId = jObj.getInt("LocationId");
                String image = jObj.getString("S3ImageUrl");
                String text = jObj.getString("PostText");
                String timestampStr = jObj.getString("PostTS");
                Integer timestamp = Integer.parseInt(timestampStr.substring(6, 19));

                PostObject post = new PostObject(postId, userId, userName, locationId, image, text, timestamp);

                posts.add(post);
            }

            TPartyDBHelper dbHelper = new TPartyDBHelper(LocationFeedActivity.this);
            dbHelper.savePosts(posts);

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
