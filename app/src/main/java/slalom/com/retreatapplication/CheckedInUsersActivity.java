package slalom.com.retreatapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import slalom.com.retreatapplication.db.TPartyDBHelper;
import slalom.com.retreatapplication.util.CheckInObject;

public class CheckedInUsersActivity extends AppCompatActivity {
    private TPartyDBHelper dbHelper;
    private Bundle bundle;
    private CustomListAdapter checkInsListAdapter;
    private int locationId;
    private final String LOC_ID = "locationId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_in_users);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            locationId = (int) bundle.getLong(LOC_ID, 3);
        }

        // temporary list so this class doesn't have errors
        dbHelper = new TPartyDBHelper(this);
        List<CheckInObject> checkIns = dbHelper.getLocalCheckIns(locationId);

        for (CheckInObject aCheckIn: checkIns) {
            Log.d("CheckInActivity", aCheckIn.toString());
        }

        checkInsListAdapter = new CustomListAdapter(this, checkIns);
        ListView checkInsListView = (ListView) findViewById(R.id.checked_in_users_list_view);
        checkInsListView.setAdapter(checkInsListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checked_in_users, menu);
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

    private class CustomListAdapter extends BaseAdapter {
        private Context mContext;
        private List<CheckInObject> checkIns;

        public CustomListAdapter(Context c, List<CheckInObject> checkIns) {
            mContext = c;
            this.checkIns = checkIns;
        }

        @Override
        public int getCount() {
            return checkIns.size();
        }

        @Override
        public Object getItem(int position) {
            return checkIns.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checked_in_user, parent, false);
            }

            ImageView userImageView = (ImageView) convertView.findViewById(R.id.check_in_user_profile_pic);
            TextView userNameTextView = (TextView)convertView.findViewById(R.id.check_in_user_name);
            TextView checkInTimestampTextView = (TextView)convertView.findViewById(R.id.check_in_timestamp);

            CheckInObject checkIn = ((CheckInObject) getItem(position));

            userImageView.setVisibility(View.GONE);
            userNameTextView.setText(checkIn.userName().replace("%20", " "));
            checkInTimestampTextView.setVisibility(View.GONE);

            // only uncomment code below if the service eventually returns these values
//            userImageView.setImageResource(R.drawable.ic_launcher);
//            checkInTimestampTextView.setText(formatTimeSinceCheckInString(checkIn.getTimestamp()));

            return convertView;
        }

        public String formatTimeSinceCheckInString(long timeStamp) {
            String timeSinceCheckIn = "";
            Date now = new Date();
            Date checkInTime = new Date(timeStamp);
            int days, hours, minutes, timeSinceCheckInInSeconds;
            long timeSinceCheckInInMilliseconds = now.getTime() - checkInTime.getTime();
            timeSinceCheckInInSeconds = (int)(timeSinceCheckInInMilliseconds / 1000);

            days = timeSinceCheckInInSeconds / 86400;

            if (days == 0) {
                hours = timeSinceCheckInInSeconds / 3600;
            } else {
                hours = (timeSinceCheckInInSeconds % 86400) / 3600;
            }
            if (hours == 0) {
                minutes = timeSinceCheckInInSeconds / 60;
            } else {
                minutes = (timeSinceCheckInInSeconds % 3600) / 60;
            }
            if (minutes == 0 && hours == 0 & days == 0) {
                timeSinceCheckIn = "Just Now";
            } else if (hours == 0 &&  days == 0) {
                timeSinceCheckIn = String.format("%d Minutes Ago", minutes);
            } else if (days == 0) {
                timeSinceCheckIn = String.format("%d Hours Ago", hours);
            } else {
                timeSinceCheckIn = String.format("%d Days Ago", days);
            }
            return timeSinceCheckIn;
        }
    }
}
