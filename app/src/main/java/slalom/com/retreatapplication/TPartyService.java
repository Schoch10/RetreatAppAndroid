package slalom.com.retreatapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * Created by alexanderp on 8/9/2015.
 */
public class TPartyService extends IntentService {

    private final String TPARTY_ENDPOINT = "http://tpartyservice-dev.elasticbeanstalk.com/home/";
    private final String CHECKINS = TPARTY_ENDPOINT+"pollparticipantlocations";
    private static final String TAG = TPartyService.class.getSimpleName();

    public TPartyService() {
        super("TpartyDataService");
    }

    @Override


    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            getData(CHECKINS);
        } catch (IOException e) {
            //What should we do here?;
        }

        stopSelf();

    }

    private void getData(String serviceCall) throws IOException {

        HttpURLConnection urlConnection;

        URL url = new URL(serviceCall);
        urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder resp = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            resp.append(line);
        }

        Log.d(TAG, resp.toString());

    }


}
