package slalom.com.retreatapplication.db;

import android.provider.BaseColumns;

/**
 * Created by senthilrajav on 8/13/15.
 */
public class LocationContract {
    public LocationContract() {}

    public static final String TABLE_NAME = "Location";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+LocationContract.TABLE_NAME+";";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + LocationContract.TABLE_NAME + " ("
            + LocationContract.RowEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LocationContract.RowEntry.COLUMN_NAME_LOCATION_ID + " NUMERIC,"
            + LocationContract.RowEntry.COLUMN_NAME_LOCATION_NAME + " TEXT,"
            + LocationContract.RowEntry.COLUMN_NAME_LOCATION_IMAGE + " TEXT,"
            + LocationContract.RowEntry.COLUMN_NAME_CHECKIN + " NUMERIC);";

    public static abstract class RowEntry implements BaseColumns {
        public static final String COLUMN_NAME_LOCATION_ID = "LocationId";
        public static final String COLUMN_NAME_LOCATION_NAME = "LocationName";
        public static final String COLUMN_NAME_LOCATION_IMAGE = "LocationImage";
        public static final String COLUMN_NAME_CHECKIN = "CheckIn";
    }
}
