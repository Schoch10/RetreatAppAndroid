package slalom.com.retreatapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CheckedInUsersActivity extends AppCompatActivity {

    private CustomListAdapter checkInsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_in_users);

        // temporary list so this class doesn't have errors
        List<?> checkIns = new ArrayList<Object> ();

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
        private List<?> checkIns;

        public CustomListAdapter(Context c, List<?> checkIns) {
            mContext = c;
            this.checkIns = checkIns;
        }

        @Override
        public int getCount() {
            return 0;
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

            ImageView userImageView = (ImageView) convertView.findViewById(R.id.user_profile_pic);
            TextView userNameTextView = (TextView)convertView.findViewById(R.id.user_name);
            TextView checkInTimestampTextView = (TextView)convertView.findViewById(R.id.check_in_timestamp);

            userImageView.setImageResource(R.drawable.ic_launcher);
            userNameTextView.setText("user_name");
            checkInTimestampTextView.setText(" ");

            return convertView;
        }
    }
}
