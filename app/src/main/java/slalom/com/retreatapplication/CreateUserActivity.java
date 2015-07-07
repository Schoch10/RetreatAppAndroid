package slalom.com.retreatapplication;

import android.content.ContentResolver;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class CreateUserActivity extends ActionBarActivity {

    //Set int for Select Picture Activity callback identify
    static final int SELECT_PICTURE = 1;

    //Set string to easily identify debug data
    private static final String TAG = CreateUserActivity.class.getSimpleName();

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
        if (id == R.id.action_settings) {
            return true;
        }

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
            Log.d(TAG, "Result OK");
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                Log.d(TAG, "String: "+imageUri.toString());
                Log.d(TAG, "Path: "+imageUri.getPath());
                //Uri uriPath = Uri.parse(imageUri.getPath());
                //InputStream uriStream = getContentResolver().openInputStream(imageUri);
                //Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
                //Bitmap bitmap = BitmapFactory.decodeFile(imageUri.toString());
                //Log.d(TAG, bitmap.toString());
                //((ImageView) findViewById(R.id.imageView3)).setImageBitmap(bitmap);
                ((ImageView) findViewById(R.id.imageView3)).setImageURI(imageUri);
            }
        } else {
            Log.d(TAG, "Result not OK");
        }
    }
    /*
    private String getRealPathFromUri(Uri uri) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    */
    public void createUserSelected(View view) {
        Intent mainViewIntent = new Intent(this, RetreatAppMainView.class);
        startActivity(mainViewIntent);
    }
}
