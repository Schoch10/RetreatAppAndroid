package slalom.com.retreatapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;


public class ActivitiesActivity extends ActionBarActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private float x1,x2;
    static final int MIN_DISTANCE = 175;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
    }


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

                        ImageView test_image = (ImageView)findViewById(R.id.test_image);
                        //test_image.setImageDrawable(getResources().getDrawable(R.drawable.omni));

                        TextView test_text = (TextView)findViewById(R.id.test_text);
                        test_text.setText("you just swiped right!");
                } else if (deltaX < 0) {
                        //swipe left
                        Log.d(TAG, "swipe left? x1: "+x1+", x2: "+x2);

                        ImageView test_image = (ImageView)findViewById(R.id.test_image);
                        //test_image.setImageDrawable(getResources().getDrawable(R.drawable.golf));

                        TextView test_text = (TextView)findViewById(R.id.test_text);
                        test_text.setText("you just swiped left!");
                    }
                    }
                break;
        }

        return true;

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
