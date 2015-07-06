package slalom.com.retreatapplication;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.util.Log;


public class HomeActivity extends ActionBarActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Log.d(TAG, prefs.toString());
        if (prefs.getBoolean("firstrun", true)) {
            Log.d(TAG, "firstrun: true");
            setContentView(R.layout.activity_home);
            prefs.edit().putBoolean("firstrun", false).commit();
        } else if (!prefs.getBoolean("firstrun", false)) {
            Log.d(TAG, "firstrun: false");
            setContentView(R.layout.activity_retreat_app_main_view);
            prefs.edit().putBoolean("firstrun", true).commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void nextSelected (View view) {
        Intent createUserIntent = new Intent(this, CreateUserActivity.class);
        startActivity(createUserIntent);
    }
}


