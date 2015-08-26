package slalom.com.retreatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class CreateUserActivity extends AppCompatActivity {

    //Set int for Select Picture Activity callback identify
    private static final int SELECT_PICTURE = 1;

    //Set string to easily identify debug data
    private static final String TAG = CreateUserActivity.class.getSimpleName();

    //Don't allow users to create user unless we get explicit confirmation from service
    private boolean canProceed = false;

    //Set username shared preferences variable name
    private static final String PREFS_NAME = "UserPreferences";
    private final String USER_NAME = "userName";
    private final String USER_ID = "userId";
    private final String USER_IMAGE = "userImage";

    private String userName;
    private Integer userId;
    private String userImage;
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_user, menu);
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

    public void uploadPictureSelected(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_PICK);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                ((ImageView) findViewById(R.id.imageView3)).setImageURI(imageUri);

                String imageUriId = imageUri.getPathSegments().get(1).split(":")[1];
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, "_id = ?", new String[]{imageUriId}, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                userImage = cursor.getString(columnIndex);
                cursor.close();
            }
        }
    }

    public void createUserSelected(View view) {

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        userName = nameEditText.getText().toString();

        sendPostsAsync sendPostRunner = new sendPostsAsync();
        sendPostRunner.execute(userName);

    }

    private class sendPostsAsync extends AsyncTask<String, String, String> {

        String response;

        @Override
        protected String doInBackground(String... userName)  {

            try {

                String postCall = "http://tpartyservice-dev.elasticbeanstalk.com/Home/CreateUser";
                String params = "username="+URLEncoder.encode(userName[0], "utf-8");

                URL url = new URL(postCall);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                OutputStreamWriter outputWriter = new OutputStreamWriter(urlConnection.getOutputStream());

                outputWriter.write(params);
                outputWriter.flush();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    responseBuilder.append(line);
                    response = responseBuilder.toString();
                }

                if (!response.equals("-1")) {
                    userId = Integer.parseInt(response);
                    canProceed=true;
                } else {
                    response = "Name already taken. Please choose a new name.";
                }

                in.close();

            } catch (IOException ioE) {
                response = "Something went wrong. Please try again.";
            }

            return response;

        }

        @Override
        protected void onPostExecute(String response){

            if (canProceed) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(USER_ID, userId);
                editor.putString(USER_NAME, userName);
                editor.putString(USER_IMAGE, userImage);
                editor.commit();

                Intent mainViewIntent = new Intent(getApplicationContext(), RetreatAppMainView.class);
                startActivity(mainViewIntent);
            } else {
                CharSequence text = response;
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(CreateUserActivity.this, text, duration);
                toast.show();
            }
        }
    }

}
