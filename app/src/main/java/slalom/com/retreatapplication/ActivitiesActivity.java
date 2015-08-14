package slalom.com.retreatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import slalom.com.retreatapplication.R;

public class ActivitiesActivity extends Activity {

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

        setContentView(R.layout.activity_activities);


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
                        Intent nextActivityIntent = new Intent(this, ActivitiesActivity.class);
                        nextActivityIntent.putExtra(LOC_ID_EXTRA, currentLocId+1);
                        startActivity(nextActivityIntent);

                } else if (deltaX < 0) {
                        //swipe left
                        Log.d(TAG, "swipe left? x1: " + x1 + ", x2: " + x2);

                        Intent nextActivityIntent = new Intent(this, ActivitiesActivity.class);
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

        StringBuilder response = new StringBuilder();

        @Override
        protected String doInBackground(Integer... locationId)  {
            publishProgress("Getting latest post...");

            try {

                Thread.sleep(3000);

                String postCall = "http://tpartyservice-dev.elasticbeanstalk.com/home/getpostsforlocation?locationid="+locationId[0].toString();
                URL url = new URL(postCall);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Since all REST responses will be arrays of JSON we can just package them up
                // as JSONArrays and send them back to the calling method
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String line;

                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

            } catch (IOException ioE) {
                ;
            } catch (InterruptedException iE) {
                ;
            }

            Log.d(TAG, response.toString());
            return response.toString();

        }

        @Override
        protected void onProgressUpdate(String... update) {
            test_text.setText(update[0]);
        }

        @Override
        protected void onPostExecute(String result){
            test_text.setText(result);
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
