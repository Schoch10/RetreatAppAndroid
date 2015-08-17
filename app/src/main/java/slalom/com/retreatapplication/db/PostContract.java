package slalom.com.retreatapplication.db;

import android.provider.BaseColumns;

/**
 * Created by alexanderp on 8/14/2015.
 */
public final class PostContract {

    public PostContract() {}

    public static final String TABLE_NAME = "posts";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + PostContract.TABLE_NAME + " ("
            + PostContract.RowEntry.POST_ID + " INTEGER PRIMARY KEY,"
            + PostContract.RowEntry.USER_ID + " NUMERIC,"
            + PostContract.RowEntry.USER_NAME + " TEXT,"
            + PostContract.RowEntry.LOCATION_ID + " NUMERIC,"
            + PostContract.RowEntry.IMAGE + " TEXT,"
            + PostContract.RowEntry.TEXT + " TEXT,"
            + PostContract.RowEntry.TIMESTAMP + " NUMERIC);";

    public static abstract class RowEntry implements BaseColumns {
        public static final String POST_ID = "id";
        public static final String USER_ID = "user_id";
        public static final String USER_NAME = "user_name";
        public static final String LOCATION_ID = "location_id";
        public static final String IMAGE = "image";
        public static final String TEXT = "text";
        public static final String TIMESTAMP = "timestamp";
    }

}