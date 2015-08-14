package slalom.com.retreatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import slalom.com.retreatapplication.util.CustomListAdapter;

/**
 * Created by senthilrajav on 8/13/15.
 */
public class ViewPostsActivity extends Activity {
    private String[][] agenda = {
            { "Arrive & Checkin", "Mt. Omni Lobby" },
            { "Cocktail Hour", "Mt. Omni Pool" },
            { "Breakfast", "Mt. Omni Restaurant" },
            { "Activities Freetime", "Check Activities Schedule" },
            { "20s Gala", "Mt. Omni Ballroom" },
            { "Breakfast", "Mt. Omni Restaurant" },
            { "Checkout", "Mt. Omni Lobby" }
    };
    private Integer[] imgId = {
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);

        CustomListAdapter customAdapter = new CustomListAdapter(this, agenda, imgId);
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(customAdapter);
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


    public void createPostSelected(View view) {
        Intent createPostActivity = new Intent(this, CreatePostActivity.class);
        startActivity(createPostActivity);
    }
}
