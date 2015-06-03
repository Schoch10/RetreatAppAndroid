package slalom.com.retreatapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;


public class RetreatAppMainView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retreat_app_main_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_retreat_app_main_view, menu);
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

    public void gameViewSelected(View view) {
        Intent gameViewIntent = new Intent(this, GameViewActivity.class);
        startActivity(gameViewIntent);
    }

    public void infoButtonSelected(View view) {
        Intent informationIntent = new Intent(this, InformationActivity.class);
        startActivity(informationIntent);
    }

    public void agendaButtonSelected(View view) {
        Intent agendaIntent = new Intent(this, AgendaActivity.class);
        startActivity(agendaIntent);
    }

    public void activitiesButtonSelected(View view) {
        Intent activitiesIntent = new Intent(this, ActivitiesActivity.class);
        startActivity(activitiesIntent);
    }

    public void trendingButtonSelected(View view) {
        Intent trendingIntent = new Intent(this, TrendingActivity.class);
        startActivity(trendingIntent);
    }
}
