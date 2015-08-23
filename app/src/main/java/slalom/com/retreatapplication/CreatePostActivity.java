package slalom.com.retreatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import slalom.com.retreatapplication.util.TPartyTask;

/**
 * Created by senthilrajav on 8/13/15.
 */
public class CreatePostActivity extends AppCompatActivity {
    private int userId = 0;
    private int locationId = 0;
    private String location = "Omni";
    private Bundle bundle;

    //image selected to upload
    private String postImage;

    // UserPreferences file that hold local userId
    private static final String PREFS_NAME = "UserPreferences";

    //Set int for Select Picture Activity callback identify
    private static final int SELECT_PICTURE = 1;

    //Set string to easily identify debug data
    private static final String TAG = CreatePostActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getInt("userId", 2);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            locationId = (int) bundle.getLong("locationId", 3);
            location = (String) bundle.getString("locationName", "Omni");
        }

        //update ActionBar title with location name of selected location in view
        setTitle(location);

        setContentView(R.layout.activity_create_post);
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

    public void createPostSelected(View view) {
        EditText postText = (EditText)findViewById(R.id.editText);
        String newPost = postText.getText().toString();

        //Call checkIn API and update local DB
        //Trigger Async Task
        new TPartyTask().execute("savePost", this, userId, locationId, location, newPost);
    }

    public void uploadPostPictureSelected(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "Result OK");
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                Log.d(TAG, "String: " + imageUri.toString());
                Log.d(TAG, "Path: " + imageUri.getPath());
                ((ImageView) findViewById(R.id.imageView4)).setImageURI(imageUri);

                String imageUriId = imageUri.getPathSegments().get(1).split(":")[1];
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, "_id = ?", new String[]{imageUriId}, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                postImage = cursor.getString(columnIndex);
                cursor.close();

                //TODO: DO SOMETHING WITH THIS IMAGE WHEN CREATING THE POST OBJECT/SERVICE CALL!!
            } else {
                Log.d(TAG, "Result not OK");
            }
        }
    }
}
