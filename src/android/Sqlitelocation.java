package com.oby.cordova.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.location.Location;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONObject;
import org.json.JSONArray;

public class Sqlitelocation extends SQLiteOpenHelper {
    private static final String TAG = "Sqlitelocation";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final int DATABASE_VERSION = 1;
    private static final String DICTIONARY_TABLE_NAME = "locations";
    private static final String CONFIGURATION_TABLE_NAME = "configurations";
    private static final String DATABASE_NAME = "locationsManager.sql";
    private static final String KEY_ID = "_id";

    private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + DICTIONARY_TABLE_NAME + 
    " ("+KEY_ID+" INTEGER primary key autoincrement, latitude TEXT, longitude TEXT, time TEXT);";
    private static final String CONFIGURATION_TABLE_CREATE = "CREATE TABLE " + CONFIGURATION_TABLE_NAME + 
    " ("+KEY_ID+" INTEGER primary key autoincrement, id TEXT, token TEXT, urlTo TEXT, delay TEXT);";
    
    Sqlitelocation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
        db.execSQL(CONFIGURATION_TABLE_CREATE);
        Log.v (TAG,"oncreate");
    }

    // Upgrading database for a newer version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DICTIONARY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONFIGURATION_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }
    public Customlocation getLastLocationRecorded(){
        Log.v(TAG, "getLastLocationRecorded entry");

        // SELECT * FROM tablename ORDER BY column DESC LIMIT 1;
        //db.rawQuery(selectQuery, null);
        Customlocation customlocation = new Customlocation();

        String query = "SELECT * from "+DICTIONARY_TABLE_NAME+" order by "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db = this.getReadableDatabase(); // get for read
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            
            //construct the customlocation to return
            customlocation = hydrate(c);

            Log.v (TAG, "get from sql the last location");
            Log.v (TAG, customlocation.toString());

            return customlocation; //The 0 is the column index, we only have 1 column, so the index is 0
        }
        // never occur of one case : init !
        return customlocation;
    }
    // Adding new
    public void addLocation(Location location) {
        Log.v(TAG, "addLocation entry");
        Customlocation lastLocation = getLastLocationRecorded();
        
        Log.v(TAG, "last location from sql :");
        Log.v(TAG, lastLocation.getLongitude() +" , "+ lastLocation.getLatitude());
        
        // si la locationtion n'a pas bougé.. on va pas surcharger le process et on oublie pour cette fois
        Log.v(TAG, "1 : "+lastLocation.getLongitude());
        Log.v(TAG, "2 : "+Double.toString(location.getLongitude()));
        Log.v(TAG, "3 : "+lastLocation.getLatitude());
        Log.v(TAG, "4 : "+Double.toString(location.getLatitude()));

        // check if location is null for first time or has changed
        boolean equalLng;
        boolean equalLat;
        String lastLng = lastLocation.getLongitude();
        String lastLat = lastLocation.getLatitude();
        if (lastLng == null || lastLng.equals("null")){ // first time
            equalLng = false;
        } else {
            // location changed ?
            equalLng = new String(lastLocation.getLongitude().substring(0,6)).equals(Double.toString(location.getLongitude()).substring(0,6)); // --> true 
        }
        if (lastLat == null || lastLat.equals("null")){ // first time
            equalLat = false;
        } else {
            // location changed ?
            equalLat = new String(lastLocation.getLatitude().substring(0,6)).equals(Double.toString(location.getLatitude()).substring(0,6)); // --> true 
        }

        // compare on 6 digit including the dot
        if (equalLng && equalLat){
            //The third decimal place is worth up to 110 m: it can identify a large agricultural field or institutional campus.
            //-> The fourth decimal place is worth up to 11 m: it can identify a parcel of land. It is comparable to the typical accuracy of an uncorrected GPS unit with no interference.
            //The fifth decimal place is worth up to 1.1 m: it distinguish trees from each other. Accuracy to this level with commercial GPS units can only be achieved with differential correction.
            Log.v(TAG, "Location not changed on 6 digits - do nothing..");
            return;
        }

        // create the entry in database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", location.getLatitude()); // Contact Name
        values.put("longitude", location.getLongitude()); // Contact Phone Number
        values.put("time", location.getTime()); // Contact Phone Number
     
        Log.v (TAG, "Value for sql is : "+values.toString());
        // Inserting Row
        db.insert(DICTIONARY_TABLE_NAME, null, values);
        db.close(); // Closing database connection

        Log.v(TAG, "addLocation end function");
    }
    public ContentValues getConfiguration(){
        ContentValues contentValues = new ContentValues();

        String query = "SELECT * from "+CONFIGURATION_TABLE_NAME+" order by "+KEY_ID+" DESC limit 1";
        SQLiteDatabase db = this.getReadableDatabase(); // get for read
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            
            //construct the customlocation to return
            contentValues.put("_id", c.getString(0)); // Contact Name

            contentValues.put("id", c.getString(1)); // Contact Name
            contentValues.put("token", c.getString(2)); // Contact Name
            contentValues.put("urlTo", c.getString(3)); // Contact Name
            contentValues.put("delay", c.getString(4)); // Contact Phone Number

            Log.v (TAG, "getConfiguration end :");
            Log.v (TAG, contentValues.toString());

            return contentValues; //The 0 is the column index, we only have 1 column, so the index is 0
        }
        // never occur of one case : init !
        return contentValues;
    }
    public void addConfiguration(JSONArray configuration) {
        Log.v(TAG, "addConfiguration entry");

        // create the entry in database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.v (TAG, "addConfiguration entry Jsonarray toString :"+ configuration.toString());
        JSONObject json_data = new JSONObject();
        try {
            json_data = configuration.getJSONObject(0);    
        }
        catch (Exception e)
        {
            // More about HTTP exception handling in another tutorial.
            // For now we just print the stack trace.
            e.printStackTrace();
        }
        Log.v (TAG, "addConfiguration entry JsonObject toString : "+ json_data.toString());
        try {
            values.put("token", json_data.getString("token")); // Contact Name
            values.put("id", json_data.getString("id")); // Contact Phone Number
            values.put("urlTo", json_data.getString("urlTo")); // Contact Phone Number
            values.put("delay", json_data.getString("delay")); // Contact Phone Number
        } catch (Exception e)
        {
            // More about HTTP exception handling in another tutorial.
            // For now we just print the stack trace.
            Log.v (TAG, "addConfiguration CATCH EXCEPTION : "+ json_data.toString());

            e.printStackTrace();
        }
        Log.v (TAG, "final Value for config just before insert sql is : "+values.toString());
        //delete all previous config
        db.execSQL("delete from "+ CONFIGURATION_TABLE_NAME);
        // Inserting new Row
        db.insert(CONFIGURATION_TABLE_NAME, null, values);
        db.close(); // Closing database connection

        Log.v(TAG, "addConfiguration end function");
    }
    /* hydratation of the cursor to return an object */
    private Customlocation hydrate(Cursor c) {
        Customlocation customlocation = new Customlocation();
        customlocation.setId(Long.valueOf(Integer.parseInt(c.getString(0))));
        customlocation.setLatitude(c.getString(1));
        customlocation.setLongitude(c.getString(2));
        // format the timestamp to date
        Log.v (TAG, "date ONE is : "+c.getString(3));
        Date date = new Date(Long.parseLong(c.getString(3))); // parse to long the string date
        Log.v (TAG, "date transformed is : "+date.toString());
        // set the date
        customlocation.setRecordedAt(date);
        
        Log.v (TAG, "End hydrate : "+customlocation.toString());

        return customlocation;
    }

    /* Get All location in array list */
    public List getAllLocations(){
        List<Customlocation> all = new ArrayList<Customlocation>();
        //Customlocation[] all = new Customlocation();
        String query = "SELECT * from "+DICTIONARY_TABLE_NAME+" order by "+KEY_ID+" DESC ";
        SQLiteDatabase db = this.getReadableDatabase(); // get for read
        Cursor c = db.rawQuery(query, null);
        if (c .moveToFirst()) {
            while (c.isAfterLast() == false) {
                all.add(hydrate(c));
                c.moveToNext();
            }
        }
        c.close();
        db.close();
        return all;
    }
    public void eraseAllLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        //delete all previous location
        db.execSQL("delete from "+ DICTIONARY_TABLE_NAME);
    }
    public Date stringToDate(String dateTime) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_FORMAT);
        Date date = null;
        try {
            date = iso8601Format.parse(dateTime);
            
        } catch (ParseException e) {
            Log.e("DBUtil", "Parsing ISO8601 datetime ("+ dateTime +") failed", e);
        }
        return date;
    }
    public String dateToString(Date date) {
        SimpleDateFormat iso8601Format = new SimpleDateFormat(DATE_FORMAT);
        return iso8601Format.format(date);
    }
}
