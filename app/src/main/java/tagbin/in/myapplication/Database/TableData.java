package tagbin.in.myapplication.Database;

import android.provider.BaseColumns;

/**
 * Created by Ramandeep on 19-08-2015.
 */
public class TableData {
    public TableData() {

    }

    public static abstract class Tableinfo implements BaseColumns {
        public static final String CAB_NO = "cab_no";
        public static final String TIME = "time";
        public static final String USER_ID = "user_id";
        public static final String TIMETOSTART = "timetostart";
        public static final String PICKUP_LOCATION = "pick_location";
        public static final String STATUS = "status";
        public static final String DATABASE_NAME = "dbrides";
        public static final String TABLE_NAME = "rides_table";
        ////////////////////////////////////////////////////
        public static final String UNIQUE_ID = "unique_id";
        public static final String LAT = "lat";
        public static final String LNG = "long";
        public static final String TIMESTAMP = "timestamp";
        public static final String LOC_TABLE_NAME = "loc_table";

        public static final int database_version = 1;
    }
}