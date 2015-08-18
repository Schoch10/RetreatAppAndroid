package slalom.com.retreatapplication;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RetreatAppMainView extends ActionBarActivity {

    private static final String PREFS_NAME = "UserPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retreat_app_main_view);
        setCountdowntimer(findViewById(R.id.textView3));

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ((TextView)findViewById(R.id.textView4)).setText(prefs.getString("userName", ""));

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
        Intent activitiesIntent = new Intent(this, LocationFeedActivity.class);
        startActivity(activitiesIntent);
    }

    public void trendingButtonSelected(View view) {
        Intent trendingIntent = new Intent(this, TrendingActivity.class);
        startActivity(trendingIntent);
    }

    public void setCountdowntimer(View view) {
        final TextView textView = (TextView)view;
        Date now = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = dformat.parse("28.08.2015");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millisecondsLeft = date.getTime() - now.getTime();

        CountDownTimer timer = new CountDownTimer(millisecondsLeft, 1000) {
            int days, hours, minutes, seconds, secondsLeft;

            public void onTick(long millisecondsLeft) {
                secondsLeft = (int)(millisecondsLeft / 1000);
                days = secondsLeft / 86400;
                hours = (secondsLeft % 86400) / 3600;
                minutes = (secondsLeft % 3600) / 60;
                seconds = (secondsLeft % 3600) % 60;

                textView.setText(String.format("%d:%02d:%02d:%02d",days,hours,minutes,seconds));
            }

            public void onFinish() {
                textView.setText("Time to party!!");
            }
        }.start();

    }
}
