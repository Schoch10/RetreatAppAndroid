package slalom.com.retreatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Base64;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Created by senthilrajav on 8/13/15.
 */
public class CreatePostActivity extends AppCompatActivity {
    private int userId = 0;
    private int locationId = 0;
    private String location = "Omni";
    private Bundle bundle;
    private String encodedString;
    private ImageView imgView;
    private Bitmap bitmap;
    private String filePath;
    //image selected to upload
    private String postImage;

    // UserPreferences file that hold local userId
    private static final String PREFS_NAME = "UserPreferences";
    //Set int for Select Picture Activity callback identify
    private static int RESULT_LOAD_IMG = 1;
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
        if (bundle != null) {
            locationId = (int) bundle.getLong("locationId", 3);
            location = bundle.getString("locationName");
        }

        //update ActionBar title with location name of selected location in view
        setTitle(location);

        setContentView(R.layout.activity_create_post);
        imgView = (ImageView) findViewById(R.id.upload_image);
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
                Uri selectedImageUri = data.getData();
                Log.d(TAG, "String: " + selectedImageUri.toString());
                Log.d(TAG, "Path: " + selectedImageUri.getPath());
                //Uri uriPath = Uri.parse(imageUri.getPath());
                //InputStream uriStream = getContentResolver().openInputStream(imageUri);
                //Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
                //Bitmap bitmap = BitmapFactory.decodeFile(imageUri.toString());
                //Log.d(TAG, bitmap.toString());
                //((ImageView) findViewById(R.id.imageView3)).setImageBitmap(bitmap);
                imgView.setImageURI(selectedImageUri);

                /*
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();*/

                /** Get File Path and Decode File **/
                try {
                    // OI FILE Manager
                    String filemanagerstring = selectedImageUri.getPath();

                    // MEDIA GALLERY
                    String selectedImagePath = getPath(selectedImageUri);

                    if (selectedImagePath != null) {
                        filePath = selectedImagePath;
                    } else if (filemanagerstring != null) {
                        filePath = filemanagerstring;
                    } else {
                        //Toast.makeText(getApplicationContext(), "Unknown path", Toast.LENGTH_LONG).show();
                        Log.e("Bitmap", "Unknown path");
                    }

                    if (filePath != null) {
                        decodeFile(filePath);
                    } else {
                        bitmap = null;
                    }
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_LONG).show();
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }

            } else {
                Log.d(TAG, "Result not OK");
            }
        }
    }

    public void createPostSelected(View view) {
        EditText textValue = (EditText) findViewById(R.id.editText);
        String postText = textValue.getText().toString();

        //Call checkIn API and update local DB
        //Trigger Async Task
        new createPostAsync().execute(String.valueOf(userId), String.valueOf(locationId), postText);

        returnToLocationFeed();
    }

    public void returnToLocationFeed() {
        //return to locationFeedActivity
        bundle = new Bundle();
        bundle.putLong("locationId", locationId);
        bundle.putString("locationName", location);

        Intent locationIndent = new Intent(this, LocationFeedActivity.class);
        locationIndent.putExtras(bundle);
        startActivity(locationIndent);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    /**
     * Simple Reads the image file and converts them to Bytes
     *
     * @param filePath of the file
     * @return byte array which is converted from the image
     * @throws IOException
     */
    public byte[] getBytesFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream is = new FileInputStream(file);
        // Get the size of the file
        long length = file.length();
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public String getBase64EncodeToString(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // Must compress the Image to reduce image size to make upload easy
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();

        // Encode Image to String
        encodedString = Base64.encodeToString(byte_arr, 0);

        return encodedString;
    }

    public String getPath(Uri uri) {
        String imageUriId = uri.getPathSegments().get(1).split(":")[1];
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, "_id = ?", new String[]{imageUriId}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        imgView.setImageBitmap(bitmap);
    }

    private class createPostAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            publishProgress("Creating post...");

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                String serviceURL = "http://tpartyservice-dev.elasticbeanstalk.com/api/post/SubmitPost?userid=" + args[0] + "&locationid=" + args[1] + "&posttext=" + URLEncoder.encode(args[2], "utf-8");
                HttpPost httpPost = new HttpPost(serviceURL);

                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(CompressFormat.JPEG, 100, bos);
                }

                byte[] data = bos.toByteArray();
                entity.addPart("uploaded", new ByteArrayBody(data, "myImage.jpg"));
                httpPost.setEntity(entity);

                /*MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("upfile", getBytesFromFile(filePath), ContentType.DEFAULT_BINARY, "myImage.jpg");
                //builder.addTextBody("userid", args[0], ContentType.TEXT_PLAIN);
                //builder.addTextBody("locationid", args[1], ContentType.TEXT_PLAIN);
                builder.addTextBody("posttext", args[2], ContentType.TEXT_PLAIN);

                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);*/

                HttpResponse response = httpClient.execute(httpPost, localContext);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));

                /*
                String sResponse;
                StringBuilder sBuilder = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    sBuilder = sBuilder.append(sResponse);
                }
                sResponse = sBuilder.toString();*/
                Log.d("This post succeeded", args[2]);
                return "Success:"+args[2];

            } catch (Exception e) {
                Log.d("This post failed: ", e.toString());
                return "Failed: "+args[2];
            }
        }

        protected void onPostExecute(String result) {
            Log.d("Post outcome:", result);
        }
    }
}
