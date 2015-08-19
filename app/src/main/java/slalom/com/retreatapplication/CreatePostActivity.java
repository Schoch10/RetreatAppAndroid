package slalom.com.retreatapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import slalom.com.retreatapplication.util.TPartyTask;

/**
 * Created by senthilrajav on 8/13/15.
 */
public class CreatePostActivity extends AppCompatActivity {
    private int userId = 0;
    private int locationId = 0;

    // UserPreferences file that hold local userId
    private static final String PREFS_NAME = "UserPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getInt("userId", 2);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            locationId = (int) b.getLong("locationId", 3);
        }

        setContentView(R.layout.activity_create_post);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_view, menu);
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

    public void createPostSelected(View view) {
        EditText postText = (EditText)findViewById(R.id.editText);
        String newPost = postText.getText().toString();

        //Call checkIn API and update local DB
        //Trigger Async Task
        new TPartyTask().execute("savePost", this, userId, locationId, newPost);
    }

}
