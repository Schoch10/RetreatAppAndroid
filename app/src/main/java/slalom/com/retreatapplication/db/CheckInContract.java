package slalom.com.retreatapplication.db;

import android.provider.BaseColumns;

import java.sql.Date;

public final class CheckInContract {
    public CheckInContract() {}

    public static final String TABLE_NAME = "CheckIn";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + CheckInContract.TABLE_NAME + " ("
            + CheckInContract.RowEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CheckInContract.RowEntry.COLUMN_NAME_CHECK_IN_DATE + " DATE,"
            + CheckInContract.RowEntry.COLUMN_NAME_LOCATION_ID + " NUMBERIC,"
            + CheckInContract.RowEntry.COLUMN_NAME_LOCATION_NAME + " TEXT,"
            + CheckInContract.RowEntry.COLUMN_NAME_USERNAME + " TEXT,"
            + CheckInContract.RowEntry.COLUMN_NAME_USER_ID + " NUMERIC,"
            + CheckInContract.RowEntry.COLUMN_NAME_CHECKIN_ID + " NUMERIC);";

    public static abstract class RowEntry implements BaseColumns {
        public static final String COLUMN_NAME_CHECK_IN_DATE = "CheckinDate";
        public static final String COLUMN_NAME_LOCATION_ID = "LocationId";
        public static final String COLUMN_NAME_LOCATION_NAME = "LocationName";
        public static final String COLUMN_NAME_USERNAME = "Username";
        public static final String COLUMN_NAME_USER_ID = "UserId";
        public static final String COLUMN_NAME_CHECKIN_ID = "CheckInID";
    }
}