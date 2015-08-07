package slalom.com.retreatapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class AgendaActivity extends Activity {

    private SimpleAdapter sa;
    private String[][] agenda = {
            { "Arrive & Checkin", "Mt. Omni Lobby" },
            { "Cocktail Hour", "Mt. Omni Pool" },
            { "Breakfast", "Mt. Omni Restaurant" },
            { "Activities Freetime", "Check Activities Schedule" },
            { "20s Gala", "Mt. Omni Ballroom" },
            { "Breakfast", "Mt. Omni Restaurant" },
            { "Checkout", "Mt. Omni Lobby" }
    };
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        HashMap<String,String> item;
        for(int i=0; i<agenda.length; i++){
            item = new HashMap<String,String>();
            item.put( "line1", agenda[i][0]);
            item.put( "line2", agenda[i][1]);
            list.add( item );
        }
        sa = new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2,
                new String[] { "line1","line2" },
                new int[] {android.R.id.text1, android.R.id.text2});
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(sa);
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
