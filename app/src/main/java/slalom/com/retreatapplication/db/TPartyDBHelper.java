package slalom.com.retreatapplication.db;

import android.database.sqlite.*;
import android.content.Context;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;

import java.util.Map;
import slalom.com.retreatapplication.model.Location;
import slalom.com.retreatapplication.model.CheckIn;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import slalom.com.retreatapplication.util.PostObject;


/**
 * This class helps open, create, and upgrade the database file containing the
 * projects and their row counters.
 */
public class TPartyDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TParty.db";
    private ContentValues values;
    private long rowId;
    private final String TAG = TPartyDBHelper.class.getSimpleName();

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
        db.execSQL(LocationContract.SQL_CREATE_TABLE);
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
        //Save list of locations
        ArrayList<Location> locations = new ArrayList<Location>();
        Location item;

        item = new Location(); item.setLocationId(3); item.setLocationName("Hotel Bar"); item.setLocationImage("bar.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(4); item.setLocationName("Hotel Lobby"); item.setLocationImage("lobby.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(5); item.setLocationName("Golf"); item.setLocationImage("golf.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(6); item.setLocationName("Lawn Games"); item.setLocationImage("lawn.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(7); item.setLocationName("Spa"); item.setLocationImage("spa.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(8); item.setLocationName("Zipline"); item.setLocationImage("zipline.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(9); item.setLocationName("Outdoor Activites"); item.setLocationImage("outdoor.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(10); item.setLocationName("Town"); item.setLocationImage("town.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(11); item.setLocationName("Banquet"); item.setLocationImage("banquet.png"); item.setCheckin(0); locations.add(item);
        item = new Location(); item.setLocationId(12); item.setLocationName("After Party"); item.setLocationImage("party.png"); item.setCheckin(0); locations.add(item);

        for (Location loc : locations) {
            values = new ContentValues();
            values.put(LocationContract.RowEntry.COLUMN_NAME_LOCATION_ID, loc.getLocationId());
            values.put(LocationContract.RowEntry.COLUMN_NAME_LOCATION_NAME, loc.getLocationName());
            values.put(LocationContract.RowEntry.COLUMN_NAME_LOCATION_IMAGE, loc.getLocationImage());
            values.put(LocationContract.RowEntry.COLUMN_NAME_CHECKIN, loc.getCheckin());
            db.insert(LocationContract.TABLE_NAME, null, values);
        }
    }

    public void saveCheckIns(ArrayList<CheckIn> checkIns){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + CheckInContract.TABLE_NAME);

        for (CheckIn item : checkIns) {
            values = new ContentValues();
            //values.put(CheckInContract.RowEntry.COLUMN_NAME_CHECK_IN_DATE, (Date)item.getCheckinDate());
            values.put(CheckInContract.RowEntry.COLUMN_NAME_LOCATION_ID, item.getLocationId());
            values.put(CheckInContract.RowEntry.COLUMN_NAME_LOCATION_NAME, item.getLocation());
            values.put(CheckInContract.RowEntry.COLUMN_NAME_USERNAME, item.getUsername());
            values.put(CheckInContract.RowEntry.COLUMN_NAME_USER_ID, item.getUserId());
            values.put(CheckInContract.RowEntry.COLUMN_NAME_CHECKIN_ID, item.getCheckInID());
            long rowId = db.insert(CheckInContract.TABLE_NAME, null, values);
        }
    }

    public void updateLocations(Map<Integer, Integer> checkIns){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values; int locID; int checkInCount;

        for (Map.Entry<Integer, Integer> entry : checkIns.entrySet()) {
            locID = entry.getKey();
            checkInCount = entry.getValue();

            // New value for one column
            values = new ContentValues();
            values.put(LocationContract.RowEntry.COLUMN_NAME_CHECKIN, checkInCount);

            db.update(LocationContract.TABLE_NAME, values, LocationContract.RowEntry.COLUMN_NAME_LOCATION_ID + "=" + locID, null);
        }
    }

    /**
     * Gets the list of projects from the database.
     *
     * @return the current projects from the database.
     */

    public ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();
        Location location;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(LocationContract.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int locIdIndex = cursor.getColumnIndex(LocationContract.RowEntry.COLUMN_NAME_LOCATION_ID);
            int locNameIndex = cursor.getColumnIndex(LocationContract.RowEntry.COLUMN_NAME_LOCATION_NAME);
            int locImgIndex = cursor.getColumnIndex(LocationContract.RowEntry.COLUMN_NAME_LOCATION_IMAGE);
            int chkInIndex = cursor.getColumnIndex(LocationContract.RowEntry.COLUMN_NAME_CHECKIN);

            location = new Location();
            location.setLocationId(cursor.getLong(locIdIndex));
            location.setLocationName(cursor.getString(locNameIndex));
            location.setLocationImage(cursor.getString(locImgIndex));
            location.setCheckin(cursor.getLong(chkInIndex));

            locations.add(location);
        }
        cursor.close();

        return (locations);
    }

    public boolean isUserCheckedIn(long userId, long locationId){
        boolean checkedIn = false;
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] CheckIns = {
                CheckInContract.RowEntry._ID
        };

        // Define 'where' part of query.
        String selection = CheckInContract.RowEntry.COLUMN_NAME_USER_ID + "=? AND " + CheckInContract.RowEntry.COLUMN_NAME_LOCATION_ID + "=?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(userId), String.valueOf(locationId)};

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                CheckInContract.TABLE_NAME,  // The table to query
                CheckIns,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        cursor.moveToFirst();
        if(cursor != null && cursor.getCount()>0){
            long checkInId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(CheckInContract.RowEntry._ID)
            );
            if(checkInId > 0) checkedIn = true;
        }

        return checkedIn;
    }

    public List<PostObject> getLocalPosts(Integer locationId) {
        List<PostObject> sortedPosts = new LinkedList<PostObject>();

        SQLiteDatabase db = getReadableDatabase();

        // After the query, the cursor points to the first database row
        // returned by the request.
        String[] args = {locationId.toString()};
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + PostContract.TABLE_NAME
                        + " WHERE " + PostContract.RowEntry.LOCATION_ID + "=?"
                        + " ORDER BY " + PostContract.RowEntry.TIMESTAMP + " DESC"
                , args);

        while (cursor.moveToNext()) {
            Integer postId = cursor.getInt(cursor.getColumnIndex(PostContract.RowEntry.POST_ID));
            Integer userId = cursor.getInt(cursor.getColumnIndex(PostContract.RowEntry.USER_ID));
            String userName = cursor.getString(cursor.getColumnIndex(PostContract.RowEntry.USER_NAME));
            Integer storedLocationId = cursor.getInt(cursor.getColumnIndex(PostContract.RowEntry.LOCATION_ID));
            String image = cursor.getString(cursor.getColumnIndex(PostContract.RowEntry.IMAGE));
            String text = cursor.getString(cursor.getColumnIndex(PostContract.RowEntry.TEXT));
            Long timestamp = cursor.getLong(cursor.getColumnIndex(PostContract.RowEntry.TIMESTAMP));

            PostObject post = new PostObject(postId, userId, userName, storedLocationId, image, text, timestamp);

            sortedPosts.add(post);
        }
        cursor.close();

        return sortedPosts;
    }


    public void savePosts(Integer locationId, List<PostObject> posts) {

        SQLiteDatabase db = this.getWritableDatabase();

        String[] args = {locationId.toString()};
        db.delete(PostContract.TABLE_NAME, PostContract.RowEntry.LOCATION_ID+"=?", args);

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
