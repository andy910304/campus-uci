package cu.uci.mapa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cu.uci.rss.Item;
import cu.uci.rss.RssDataBaseAttr;

/**
 * Created by Yannier on 4/17/2015.
 */
public class DBLocation {

    private static final String DB_NAME = "_locationUci";

    private static final String T_NAME = "_location";

    private static final String ID = "_id";
    private static final int VERSION = 1;

    private static final String LATITUDE = "_latitude";
    private static final String LONGITUDE = "_longitude";

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DBLocation (Context context){

        this.context = context;

    }

    public void openMapaDatabase(){
        this.dbHelper = new DBHelper(this.context);
        this.sqLiteDatabase = this.dbHelper.getWritableDatabase();
    }

    public void closeMapaDatabase(){
        this.dbHelper.close();
    }

    public void saveLocation(double lat, double lon) {
        ContentValues cv = new ContentValues();
        cv.put(ID,1);
        cv.put(LATITUDE,lat);
        cv.put(LONGITUDE,lon);

        String[] columnas = new String[]{ID, LATITUDE,LONGITUDE};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

        if(cursor.getCount()>0){
            this.sqLiteDatabase.update(T_NAME, cv, ID + "=" + 1, null);
        }else{
            this.sqLiteDatabase.insert(T_NAME,null,cv);
        }

        cursor.close();

    }

    public double[] loadLastLocation(){

        double[] info = new double[2];
        String[] columnas = new String[]{ID, LATITUDE,LONGITUDE};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

        int iFila = cursor.getColumnIndex(ID);
        int iLatitude = cursor.getColumnIndex(LATITUDE);
        int iLongitude = cursor.getColumnIndex(LONGITUDE);
        int count = cursor.getCount();

        if(count==0){
            info[0]=0;
            info[1]=0;
        }else{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                info[0] = cursor.getDouble(iLatitude);
                info[1] = cursor.getDouble(iLongitude);
            }
        }

        cursor.close();
        return info;
    }


    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + T_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
                    LATITUDE + " DOUBLE NOT NULL, " + LONGITUDE + " DOUBLE NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + T_NAME);
            onCreate(db);
        }


    }

}
