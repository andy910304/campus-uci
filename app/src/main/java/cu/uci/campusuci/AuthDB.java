package cu.uci.campusuci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yannier on 5/15/2015.
 */
public class AuthDB {

    private static final String DB_NAME = "_authUci";
    private static final String T_NAME = "_auth";
    private static final String ID = "_id";
    private static final String USER = "_user";
    private static final String PASS = "_pass";
    private static final int VERSION = 1;

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public AuthDB(Context context){

        this.context = context;

    }

    public void openAuthDatabase(){
        this.dbHelper = new DBHelper(this.context);
        this.sqLiteDatabase = this.dbHelper.getWritableDatabase();
    }

    public void closeAuthDatabase(){
        this.dbHelper.close();
    }

    public void saveAuth(String user, String pass) {
        ContentValues cv = new ContentValues();
        cv.put(ID,1);
        cv.put(USER,user);
        cv.put(PASS,pass);

        String[] columnas = new String[]{ID, USER, PASS};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

        if(cursor.getCount()>0){
            this.sqLiteDatabase.update(T_NAME, cv, ID + "=" + 1, null);
        }else{
            this.sqLiteDatabase.insert(T_NAME,null,cv);
        }

        cursor.close();

    }

    public String[] loadAuth(){

        String[] info = new String[2];
        String[] columnas = new String[]{USER,PASS};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

        int iUser = cursor.getColumnIndex(USER);
        int iPass = cursor.getColumnIndex(PASS);
        int count = cursor.getCount();

        if(count==0){
            info[0]="";
            info[1]="";
        }else{
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                info[0] = cursor.getString(iUser);
                info[1] = cursor.getString(iPass);
            }
        }

        cursor.close();
        return info;
    }

    public int deleteAuth(String user){

        return this.sqLiteDatabase.delete(T_NAME, USER + "='" + user + "'", null);
    }

    public void cleanDatabase(){
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        this.sqLiteDatabase.execSQL("CREATE TABLE " + T_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
                USER + " TEXT NOT NULL, " + PASS + " TEXT NOT NULL);");
    }

    private static class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + T_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
                    USER + " TEXT NOT NULL, " + PASS + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + T_NAME);
            onCreate(db);
        }


    }

}

