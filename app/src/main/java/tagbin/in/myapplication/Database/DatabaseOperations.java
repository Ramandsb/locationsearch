package tagbin.in.myapplication.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import tagbin.in.myapplication.UpcomingRides.DataItems;


public class DatabaseOperations extends SQLiteOpenHelper {


    public DatabaseOperations(Context context) {
        super(context, TableData.Tableinfo.DATABASE_NAME, null, database_version);
    }

    public static final int database_version = 2;
    public String CREATE_QUERY = "CREATE TABLE " + TableData.Tableinfo.TABLE_NAME + "(" + TableData.Tableinfo.CAB_NO + " TEXT," +TableData.Tableinfo.TIME + " TEXT,"+TableData.Tableinfo.USER_ID + " TEXT," + TableData.Tableinfo.PICKUP_LOCATION + " TEXT," +TableData.Tableinfo.TIMETOSTART+" TEXT,"+ TableData.Tableinfo.STATUS+" TEXT);";
    public String CREATE_LOC_QUERY = "CREATE TABLE " + TableData.Tableinfo.LOC_TABLE_NAME + "(" + TableData.Tableinfo.UNIQUE_ID + " TEXT," +TableData.Tableinfo.LAT + " TEXT,"+TableData.Tableinfo.LNG + " TEXT," + TableData.Tableinfo.TIMESTAMP + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        sdb.execSQL(CREATE_LOC_QUERY);
        Log.d("Database operations", "Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableData.Tableinfo.TABLE_NAME);
        onCreate(db);
    }

    public void putInformation(DatabaseOperations dop, String cab_no, String time,String user_id,String pick,String timeto,String status)

    {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.CAB_NO, cab_no);
        cv.put(TableData.Tableinfo.TIME, time);
        cv.put(TableData.Tableinfo.USER_ID, user_id);
        cv.put(TableData.Tableinfo.TIMETOSTART, timeto);
        cv.put(TableData.Tableinfo.PICKUP_LOCATION, pick);
        cv.put(TableData.Tableinfo.STATUS, status);
        long k = SQ.insert(TableData.Tableinfo.TABLE_NAME, null, cv);
        Log.d("Database Created", "true");

    }
    public void putLatLong(DatabaseOperations dop, String unique_id, String lat,String lng,String timestamp)

    {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.UNIQUE_ID, unique_id);
        cv.put(TableData.Tableinfo.LAT, lat);
        cv.put(TableData.Tableinfo.LNG, lng);
        cv.put(TableData.Tableinfo.TIMESTAMP, timestamp);
        long k = SQ.insert(TableData.Tableinfo.LOC_TABLE_NAME, null, cv);
        Log.d("Database putLatLon", "true");

    }
    public void putStatus(DatabaseOperations dop,String status,String user_id){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.STATUS, status);
        SQ.update(TableData.Tableinfo.TABLE_NAME,cv, TableData.Tableinfo.USER_ID+"="+user_id,null);
    }


//    public Cursor getInformation(DatabaseOperations dop) {
//        SQLiteDatabase SQ = dop.getReadableDatabase();
//        String[] coloumns = {TableData.Tableinfo.CAB_NO, TableData.Tableinfo.TIME,TableData.Tableinfo.USER_ID,TableData.Tableinfo.PICKUP_LOCATION,};
//        Cursor CR = SQ.query(TableData.Tableinfo.TABLE_NAME, coloumns, null, null, null, null, null);
//
//        return CR;
//
//
//    }

    public ArrayList<DataItems> readData(DatabaseOperations dop) {
        ArrayList<DataItems> listData = new ArrayList<>();
        SQLiteDatabase SQ = dop.getReadableDatabase();

        Log.d("DatabasRead","");
        String[] coloumns = {TableData.Tableinfo.CAB_NO, TableData.Tableinfo.TIME,TableData.Tableinfo.USER_ID,TableData.Tableinfo.PICKUP_LOCATION,TableData.Tableinfo.TIMETOSTART,};
//        Cursor cursor = SQ.query(TableData.Tableinfo.TABLE_NAME, coloumns, null, null, null, null, null);
        Cursor cursor = SQ.rawQuery("SELECT * from " + TableData.Tableinfo.TABLE_NAME + " ORDER BY " + TableData.Tableinfo.TIMETOSTART + " ASC", null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //create a new movie object
                // and retrieve the data from the cursor to be stored in this movie object
                DataItems item = new DataItems();
                item.setCab_no(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.CAB_NO)));
                item.setTime(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.TIME)));
                item.setTo_loc(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.USER_ID)));
                item.setPick(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.PICKUP_LOCATION)));
                item.setTimetostart(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.TIMETOSTART)));
                item.SetStatus(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.STATUS)));
                listData.add(item);
                Log.d("Database read", "true");
            }
            while (cursor.moveToNext());
        }
        return listData;
    }

    public void deleteRow(DatabaseOperations dop,String id)
    {

        SQLiteDatabase SQ = dop.getWritableDatabase();
            SQ.delete(TableData.Tableinfo.TABLE_NAME, TableData.Tableinfo.USER_ID+"="+id, null);


    }

    public  void eraseData(DatabaseOperations dop){
        SQLiteDatabase db = dop.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TableData.Tableinfo.TABLE_NAME, null, null);
        Log.d("Database Erased", "true");
    }

    public String  getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TableData.Tableinfo.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        String count = Integer.toString(cnt);
        return count;
    }
    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.

    }


}