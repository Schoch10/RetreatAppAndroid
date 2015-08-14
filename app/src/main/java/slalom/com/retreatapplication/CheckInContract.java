package slalom.com.retreatapplication;

import android.provider.BaseColumns;

public final class CheckInContract {
    public CheckInContract() {}

    public static final String TABLE_NAME = "CheckIn";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + CheckInContract.TABLE_NAME + " ("
            + CheckInContract.RowEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CheckInContract.RowEntry.COLUMN_NAME_LOCATION + " TEXT,"
            + CheckInContract.RowEntry.COLUMN_NAME_CHECKINS + " NUMERIC);";

    public static abstract class RowEntry implements BaseColumns {
        public static final String COLUMN_NAME_LOCATION = "Location";
        public static final String COLUMN_NAME_CHECKINS = "CheckIns";
    }
}