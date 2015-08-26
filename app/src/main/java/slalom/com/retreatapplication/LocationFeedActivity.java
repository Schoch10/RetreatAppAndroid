package slalom.com.retreatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.List;

import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.util.PostObject;
import slalom.com.retreatapplication.util.TPartyTask;

public class LocationFeedActivity extends AppCompatActivity {

    private final String TAG = LocationFeedActivity.class.getSimpleName();
    private int userId = 0;
    private int locationId = 0;
    private String location = "";
    private final String PREFS_NAME = "UserPreferences";
    private final String USER_ID = "userId";
    private final String USER_IMAGE = "userImage";
    private final String LOC_ID = "locationId";
    private final String LOC_NAME = "locationName";
    private final String POST_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/getpostsforlocation?locationid=";
    private final String PAGE_LIMIT = "&page=100";

    private Bundle bundle;

    private CustomListAdapter postListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate() called");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getInt(USER_ID, 2);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            locationId = (int) bundle.getLong(LOC_ID, 3);
            location = bundle.getString(LOC_NAME);
        }

        //update ActionBar title with location name of selected location in view
        setTitle(location.toUpperCase());

        setContentView(R.layout.location_feed_activity);

        //Add user image to check in and post button
        ((ImageView) findViewById(R.id.user_image)).setImageURI(Uri.parse(prefs.getString(USER_IMAGE, "")));
        TextView postTextView = (TextView)findViewById(R.id.post_text);


        //Check remote DB for posts and display those when finished
        getPostsAsync getPostsRunner = new getPostsAsync();
        getPostsRunner.execute(locationId);

        //Meanwhile Check local DB for posts and display those
        TPartyDBHelper dbHelper = new TPartyDBHelper(this);
        List<PostObject> localPosts = dbHelper.getLocalPosts(locationId);
        postListAdapter = new CustomListAdapter(this, localPosts);
        ListView postListView = (ListView) findViewById(R.id.postListView);
        postListView.setAdapter(postListAdapter);

        //See if user is checked into this location or not
        boolean checkedIn = dbHelper.isUserCheckedIn(userId, locationId);
        if(checkedIn){
            Button btn = (Button)findViewById(R.id.checkInButton);
            btn.setVisibility(View.GONE);
            findViewById(R.id.createPostButton).setVisibility(View.VISIBLE);
        }

        dbHelper.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "about to update posts...");
        new getPostsAsync().execute(locationId);
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    //Async task to handle querying AWS on separate thread
    private class getPostsAsync extends AsyncTask<Integer, String, String> {

        Integer locationId;
        String response;

        @Override
        protected String doInBackground(Integer... location) {
            //Parent method for getting posts from remote DB and storing them in local DB
            publishProgress("Getting latest post...");
            locationId = location[0];
            savePosts(locationId, getPosts(locationId));

            return "success!";
        }

        @Override
        protected void onProgressUpdate(String... update) {
//            postTextView.setText(update[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Once we know we've updated the local DB, we query it and update the display
            TPartyDBHelper dbHelper = new TPartyDBHelper(LocationFeedActivity.this);
            List<PostObject> localPosts = dbHelper.getLocalPosts(locationId);

            postListAdapter = new CustomListAdapter(LocationFeedActivity.this, localPosts);
            ListView postListView = (ListView) findViewById(R.id.postListView);
            postListView.setAdapter(postListAdapter);

            dbHelper.close();
        }

        private JSONArray getPosts(Integer locationId) {
            //Ask AWS for all the posts for this location
            JSONArray respJArray = new JSONArray();

            HttpURLConnection urlConnection = null;
            InputStream in = null;

            try {
                String postCall = POST_ENDPOINT + locationId.toString() + PAGE_LIMIT;
                URL url = new URL(postCall);
                urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());
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
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }

            return respJArray;

        }

        private void savePosts(Integer locationId, JSONArray respArray) {
            //Iterate through array of posts and store them in local DB
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
            dbHelper.close();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_feed_activity, menu);
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
        } else if (id == R.id.action_check_ins) {
            Intent activityIntent = new Intent(this, CheckedInUsersActivity.class);
            bundle = new Bundle();
            bundle.putLong("locationId", locationId);
            activityIntent.putExtras(bundle);
            startActivity(activityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreatePost(View view) {
        bundle = new Bundle();
        bundle.putLong("locationId", locationId);
        bundle.putString("locationName", location);
        Intent activityIntent = new Intent(this, CreatePostActivity.class);
        activityIntent.putExtras(bundle);
        startActivity(activityIntent);
    }

    public void onCheckIn(View view) {
        //Disable button
        Button btn = (Button)findViewById(R.id.checkInButton);
        btn.setVisibility(View.GONE);
        findViewById(R.id.createPostButton).setVisibility(View.VISIBLE);

        //Call checkIn API and update local DB
        //Trigger Async Task
        new TPartyTask().execute("checkInUser", this, userId, locationId);
    }

    public void refreshLocationFeed(View view) {
        bundle = new Bundle();
        bundle.putLong("locationId", locationId);
        bundle.putString("locationName", location);

        new TPartyTask().execute("refreshActivity", this, LocationFeedActivity.class, bundle);
    }

    private class CustomListAdapter extends BaseAdapter {
        private Context mContext;
        private List<PostObject> postList;
        private ImageView postImageView;

        public CustomListAdapter(Context c, List<PostObject> postList) {
            mContext = c;
            this.postList = postList;
        }

        public int getCount() {
            return postList.size();
        }

        public Object getItem(int position) {
            return postList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        // create a single post View for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.single_post, parent, false);
            }

            ImageView postUserImageView = (ImageView) convertView.findViewById(R.id.post_user_image);
            TextView postUserNameTextView = (TextView)convertView.findViewById(R.id.post_user_name);
            TextView elapsedTimestampTextView = (TextView)convertView.findViewById(R.id.post_elapsed_timestamp);
            postImageView = (ImageView) convertView.findViewById(R.id.post_image);
            TextView postTextView = (TextView)convertView.findViewById(R.id.post_text);

            PostObject post = ((PostObject) getItem(position));

            postUserImageView.setImageResource(R.drawable.ic_launcher);
            postUserNameTextView.setText(post.userName().replace("%20", " "));

            elapsedTimestampTextView.setText(formatTimeSincePostString(post.timestamp()));

            if (post.image().equals(null) || post.image().equals("")) {
                postImageView.setVisibility(View.GONE);
            } else {
                    postImageView.setVisibility(View.VISIBLE);
                    postImageView.setImageBitmap(BitmapFactory.decodeResource(LocationFeedActivity.this.getResources(), R.drawable.placeholder));
                    new makeBitmapsTask(postImageView).execute(post.image());
                    //postImageView.setImageURI(Uri.parse(imageUrl.toURI().toString()));
            }
            if (post.text().equals("null") || post.text().equals("")) {
                postTextView.setVisibility(View.GONE);
            } else {
                postTextView.setVisibility(View.VISIBLE);
                postTextView.setText(post.text().replace("%20", " "));
            }
            return convertView;
        }

        public String formatTimeSincePostString(long timeStamp) {
            String timeSincePost = "";
            Date now = new Date();
            Date postTime = new Date(timeStamp);
            int days, hours, minutes, timeSincePostInSeconds;
            long timeSincePostInMilliseconds = now.getTime() - postTime.getTime();
            timeSincePostInSeconds = (int)(timeSincePostInMilliseconds / 1000);

            days = timeSincePostInSeconds / 86400;

            if (days == 0) {
                hours = timeSincePostInSeconds / 3600;
            } else {
                hours = (timeSincePostInSeconds % 86400) / 3600;
            }
            if (hours == 0) {
                minutes = timeSincePostInSeconds / 60;
            } else {
                minutes = (timeSincePostInSeconds % 3600) / 60;
            }
            if (minutes == 0 && hours == 0 & days == 0) {
                timeSincePost = "Just Now";
            } else if (hours == 0 &&  days == 0) {
                timeSincePost = String.format("%d Minutes Ago", minutes);
            } else if (days == 0) {
                timeSincePost = String.format("%d Hours Ago", hours);
            } else {
                timeSincePost = String.format("%d Days Ago", days);
            }
            return timeSincePost;
        }


        private class makeBitmapsTask extends AsyncTask<String, String, Bitmap> {
            private ImageView postImageView;

            public makeBitmapsTask(ImageView imageView) {
                postImageView = imageView;
            }

            protected Bitmap doInBackground(String... url) {
                URL imageUrl = null;
                Bitmap imageBm = null;
                HttpURLConnection urlConn = null;
                BufferedInputStream in = null;
                try {
                    imageUrl = new URL(url[0]);
                    urlConn = (HttpURLConnection) imageUrl.openConnection();
                    in = new BufferedInputStream(urlConn.getInputStream());
                    imageBm = BitmapFactory.decodeStream(in);
                    Log.d("Image found in S3", imageUrl.toString());

                } catch (Exception e) {
                    Log.d("Image not found in S3", e.getMessage());
                    imageBm = BitmapFactory.decodeResource(LocationFeedActivity.this.getResources(), R.drawable.placeholder);
                } finally {
                    if (urlConn != null) {
                        urlConn.disconnect();
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {}
                    }
                }
                return imageBm;

            }

            protected void onPostExecute(Bitmap result) {
                        if (result != null && postImageView != null)
                        postImageView.setImageBitmap(result);
                    }
            }
        }
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