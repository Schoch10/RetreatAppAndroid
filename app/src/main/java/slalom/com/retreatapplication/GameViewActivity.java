package slalom.com.retreatapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;


public class GameViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

//        SharedPreferences prefs = getPreferences(MODE_PRIVATE);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GridViewAdapter(this));

//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Toast.makeText(GameViewActivity.this, "" + position,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
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

    public class GridViewAdapter extends BaseAdapter {
        private Context mContext;

        public GridViewAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return questionsArray.length;
        }

        public Object getItem(int position) {
            return questionsArray[position];
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.card_view, parent, false);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.question);
            textView.setText(questionsArray[position]);
            return convertView;
        }

        private String[] questionsArray = {
                "How old is Russell?",
                "What object does Mikey B look like when he stands?",
                "Who Broke the Elevator?",
                "Name an IM&A SSIS Architect?",
                "How does Todd Richman get to work in the summer?",
                "Who are Slalom's founders?",
                "What is a fun fact about someone else?",
                "What is Todd Christy's favorite snack?",
                "What consultant was mounted by a cow?",
                "What is the Dude's favorite hobby?",
                "What is a fun fact about Barry?",
                "Who has more than 3 siblings?"
        };
    }
}
