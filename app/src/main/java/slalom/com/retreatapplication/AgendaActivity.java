package slalom.com.retreatapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import slalom.com.retreatapplication.util.CustomListAdapter;


public class AgendaActivity extends AppCompatActivity {

    private String[][] agenda = {
            { "Arrive & Check-in", "Omni Mt. Washington Lobby", "4:00 PM" },
            { "Cocktails & Dinner", "Jewel Terrace \nWeather Back Up: Grand Ballroom", "6:00 PM - 10:00 PM" },
            { "After Party", "The Cave and Stickney’s", "10:00 PM - Midnight" },
            { "Breakfast", "Main Dining Room", "7:00 AM - 10:00 AM" },
            { "Golf", "Omni Mt. Washington Golf Course", "9:00 AM" },
            { "Lawn Games", "South Veranda Lawn", "10:00 AM" },
            { "Zipline", "Omni Canopy Tours - Slopes", "11:00 AM" },
            { "Outdoor Activities", "Check Activities Emails", "All Day" },
            { "1920s Speakeasy", "Presidential Ballroom, Presidential Foyer, Presidential Garden", "6:00 PM - 10:00 PM" },
            { "After Party", "The Cave and Stickney’s", "10:00 PM - Midnight" },
            { "Breakfast", "Main Dining Room", "7:00 AM - 10:00 AM" },
            { "Checkout", "Omni Mt. Washington Lobby", "11:00 AM" }
    };
    private Integer[] imgId = {
            R.drawable.checkin,
            R.drawable.cocktails,
            R.drawable.cocktails,
            R.drawable.breakfast,
            R.drawable.golf,
            R.drawable.lawngames,
            R.drawable.zipline,
            R.drawable.outdoors,
            R.drawable.banquet,
            R.drawable.cocktails,
            R.drawable.breakfast,
            R.drawable.checkout
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}