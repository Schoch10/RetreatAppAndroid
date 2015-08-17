package slalom.com.retreatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import slalom.com.retreatapplication.LocationFeedActivity;
import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.model.Location;
import slalom.com.retreatapplication.util.CustomArrayAdapter;
import slalom.com.retreatapplication.util.TPartyTask;


public class TrendingActivity extends Activity {
    //SwipeRefreshLayout swipeLayout;
    Activity activityContext;
    Intent activityIntent;
    TPartyDBHelper dbHelper;
    TextView textDetail;
    ArrayList<Location> locations;
    Location location;

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
        setContentView(R.layout.activity_trending);
        activityContext = this;

        /*
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Trigger Async Task (onPreExecute method)
                new TPartyTask().execute("getCheckIns", this);
                swipeLayout.setRefreshing(false);
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/

        dbHelper = new TPartyDBHelper(this);
        locations = (ArrayList<Location>)dbHelper.getLocations();

        CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(this, R.layout.activity_trending, locations);
        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(customArrayAdapter);

        // OnItem click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // When Button is clicked
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                location = locations.get(position);
                Bundle b = new Bundle();
                b.putString("locationName", location.getLocationName());
                b.putLong("locationId", location.getLocationId());

                activityIntent = new Intent(activityContext, LocationFeedActivity.class);
                activityIntent.putExtras(b);
                activityContext.startActivity(activityIntent);
            }
        });
    }

    public void refreshCheckInsSelected(View view) {
        // Trigger Async Task
        new TPartyTask().execute("getCheckIns", this);
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
}
