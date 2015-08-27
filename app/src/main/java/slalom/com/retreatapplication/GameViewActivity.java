package slalom.com.retreatapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;


public class GameViewActivity extends AppCompatActivity {

    private String[] strArray;
    private GridViewAdapter gridViewAdapter = new GridViewAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);
        GridView gridview = (GridView) findViewById(R.id.gridview);

        // Restore preferences
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        strArray = getStoredAnswers(prefs, gridViewAdapter.getCount());

        gridview.setAdapter(gridViewAdapter);
    }

    private String[] getStoredAnswers(SharedPreferences prefs, int cardCount) {
        String[] answers = new String[cardCount];
        for (int i = 0; i < cardCount; i++) {
            answers[i] = prefs.getString("question_" + i, "");
        }
        return answers;
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        GridView gridview = (GridView) findViewById(R.id.gridview);
        int cardCount = gridViewAdapter.getCount();
        String answerText;
        for (int i = 0; i < cardCount; i++) {
            answerText = ((EditText)gridview.getChildAt(i).findViewById(R.id.answer_field))
                    .getText().toString();
            if (!answerText.equals("")) {
                editor.putString("question_" + i, answerText);
            }
        }
        // Commit the edits!
        editor.commit();
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

        // create a new Card View for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.card_view, parent, false);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.question);
            EditText editView = (EditText)convertView.findViewById(R.id.answer_field);

            textView.setText(questionsArray[position]);
            if (strArray != null && strArray[position] != "") {
                editView.setText(strArray[position]);
            }

            return convertView;
        }

        private String[] questionsArray = {
                "...Attended the BBJ Best Places to Work Award Ceremony?",
                "...Can Name 5 of Slalom's Core Values?",
                "...Volunteered at the Greater Boston Food Bank?",
                "...Has Run the Boston Marathon?",
                "...Participated in Last Year's Shuffleboard Tournament",
                "...Part of the Slalom Boston First 20?",
                "...Won a Mogul Award?",
                "...Has Hiked Mt. Washington?",
                "...Started at Slalom After Jan 1, 2015?",
                "...is Wearing their Slalom Fitbit?",
                "...Plays a Musical Instrument?",
                "...Developed this app!"
        };
    }
}
