package slalom.com.retreatapplication;

import android.database.sqlite.*;
import android.content.Context;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class helps open, create, and upgrade the database file containing the
 * projects and their row counters.
 */
public class TPartyDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TParty.db";

    public TPartyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the underlying database with the SQL_CREATE_TABLE queries from
     * the contract classes to create the tables and initialize the data.
     * The onCreate is triggered the first time someone tries to access
     * the database with the getReadableDatabase or
     * getWritableDatabase methods.
     *
     * @param db the database being accessed and that should be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CheckInContract.SQL_CREATE_TABLE);
        db.execSQL(PostContract.SQL_CREATE_TABLE);
        initializeData(db);
    }

    /**
     * This method must be implemented if your application is upgraded and must
     * include the SQL query to upgrade the database from your old to your new
     * schema.
     *
     * @param db the database being upgraded.
     * @param oldVersion the current version of the database before the upgrade.
     * @param newVersion the version of the database after the upgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TPartyDBHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion);
    }


    /**
     * Initialize example data to show when the application is first installed.
     *
     * @param db the database being initialized.
     */
    private void initializeData(SQLiteDatabase db) {
        //Call TParty Service to add initial values
        ContentValues values = new ContentValues();
        values.put(CheckInContract.RowEntry.COLUMN_NAME_LOCATION, "Golf");
        values.put(CheckInContract.RowEntry.COLUMN_NAME_CHECKINS, "2");
        long rowId = db.insert(CheckInContract.TABLE_NAME, null, values);
    }

    public void saveCheckIns(Map<String, Integer> checkIns){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + CheckInContract.TABLE_NAME);

        ContentValues values;
        for(Map.Entry<String, Integer> entry : checkIns.entrySet()) {
            values = new ContentValues();
            values.put(CheckInContract.RowEntry.COLUMN_NAME_LOCATION, entry.getKey());
            values.put(CheckInContract.RowEntry.COLUMN_NAME_CHECKINS, entry.getValue());
            db.insert(CheckInContract.TABLE_NAME, null, values);
        }
    }

    /**
     * Gets the list of projects from the database.
     *
     * @return the current projects from the database.
     */
    public Map<String, Integer> getCheckIns() {
        Map<String, Integer> checkIns = new HashMap<String, Integer>();

        SQLiteDatabase db = getReadableDatabase();

        // After the query, the cursor points to the first database row
        // returned by the request.
        Cursor cursor = db.query(CheckInContract.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int locationIndex = cursor.getColumnIndex(CheckInContract.RowEntry.COLUMN_NAME_LOCATION);
            int checkInsIndex = cursor.getColumnIndex(CheckInContract.RowEntry.COLUMN_NAME_CHECKINS);

            checkIns.put(cursor.getString(locationIndex), cursor.getInt(checkInsIndex));
        }
        cursor.close();

        return (checkIns);
    }

    public void savePosts(ArrayList<PostObject> posts) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + CheckInContract.TABLE_NAME);

        ContentValues values;
        for (PostObject post : posts) {
            values = new ContentValues();
            values.put(PostContract.RowEntry.POST_ID, post.postId());
            values.put(PostContract.RowEntry.USER_ID, post.userId());
            values.put(PostContract.RowEntry.USER_NAME, post.userName());
            values.put(PostContract.RowEntry.LOCATION_ID, post.locationId());
            values.put(PostContract.RowEntry.IMAGE, post.image());
            values.put(PostContract.RowEntry.TEXT, post.text());
            values.put(PostContract.RowEntry.TIMESTAMP, post.timestamp());

            db.insert(PostContract.TABLE_NAME, null, values);
        }

    }

}
