package slalom.com.retreatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RetreatAppMainView extends AppCompatActivity {
    private Date today; private Date tPartyDate;
    private boolean timeToParty = false;
    private static final String PREFS_NAME = "UserPreferences";
    private static final int TPARTY_DATE = 28; //August 28, 2015
    private static final int TPARTY_TIME = 18; //6 PM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retreat_app_main_view);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.white_slalom_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        today = new Date();
        tPartyDate = new Date(today.getYear(),today.getMonth(),TPARTY_DATE);
        if(today.after(tPartyDate) || today.equals(tPartyDate)){
            // Get calendar set to the current date and time
            Calendar cal = Calendar.getInstance();

            // Set time of calendar to 18:00
            cal.set(Calendar.HOUR_OF_DAY, TPARTY_TIME);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // Check if current time is after 18:00 today
            if(Calendar.getInstance().after(cal) || Calendar.getInstance().after(cal))
                timeToParty = true;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ((TextView)findViewById(R.id.retreat_user_name)).setText(prefs.getString("userName", ""));

        //((ImageView)findViewById(R.id.retreat_user_image)).setImageURI(Uri.parse(prefs.getString("userImage", "")));

        ImageView userImageView = (ImageView)findViewById(R.id.retreat_user_image);
        // start with the ImageView
        Ion.with(userImageView)
                // use a placeholder google_image if it needs to load from the network
                .placeholder(R.drawable.placeholder)
                // load the url
                .load(prefs.getString("userImage", ""));

        Button btn = (Button)findViewById(R.id.game_view_button);
        if(timeToParty){
            TextView countdownText = (TextView)findViewById(R.id.countdown_text_view);
            countdownText.setVisibility(View.GONE);

            TextView timerText = (TextView)findViewById(R.id.timer_text_view);
            timerText.setVisibility(View.GONE);

            RelativeLayout layout = (RelativeLayout)findViewById(R.id.main_layout);

            RelativeLayout.LayoutParams imageparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            imageparams.addRule(RelativeLayout.ABOVE, R.id.select_location_button);
            layout.updateViewLayout(userImageView, imageparams);
            btn.setEnabled(true);
        }
        else{
            setCountdownTimer(findViewById(R.id.timer_text_view));
            btn.setEnabled(false);
        }
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

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

    public void trendingButtonSelected(View view) {
        Intent trendingIntent = new Intent(this, TrendingActivity.class);
        startActivity(trendingIntent);
    }

    public void setCountdownTimer(View view) {
        final TextView textView = (TextView)view;
        today = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = dformat.parse("28.08.2015");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millisecondsLeft = date.getTime() - today.getTime();

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
