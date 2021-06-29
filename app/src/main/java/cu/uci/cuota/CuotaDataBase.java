package cu.uci.cuota;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yannier on 4/17/2015.
 */
public class CuotaDataBase {

    private static final String DB_NAME = "_cuotaUci";
    private static final String T_NAME = "_users";
    private static final String ID = "_id";
    private static final String USER = "_user";
    private static final String CUOTA_USADA = "_cu";
    private static final String CUOTA_TOTAL = "_ct";
    private static final String NAV_TYPE = "_nav";
    private static final int VERSION = 1;

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public CuotaDataBase(Context context){

        this.context = context;

    }

    public void openCuotaDatabase(){
        this.dbHelper = new DBHelper(this.context);
        this.sqLiteDatabase = this.dbHelper.getWritableDatabase();
    }

    public void closeCuotaDatabase(){
        this.dbHelper.close();
    }

    public String[] getUsersSaved() {

        String [] user;

        String[] columnas = new String[]{USER};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);
        user = new String[cursor.getCount()];

        int iUser = cursor.getColumnIndex(USER);
        int count = 0;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            user[count] = cursor.getString(iUser);
            count ++;
        }
        cursor.close();

        return user;
    }

    public void saveCuota(String user, int cu, int ct, String nav) {
        ContentValues cv = new ContentValues();
        cv.put(USER,user);
        cv.put(CUOTA_USADA,cu);
        cv.put(CUOTA_TOTAL,ct);
        cv.put(NAV_TYPE,nav);

        String[] columnas = new String[]{USER, CUOTA_USADA, CUOTA_TOTAL, NAV_TYPE};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, USER + "= '" + user + "'", null,
                null, null, null);

        if(cursor.getCount()>0){
            this.sqLiteDatabase.update(T_NAME, cv, USER + "= '" + user + "'", null);
        }else{
            this.sqLiteDatabase.insert(T_NAME,null,cv);
        }

        cursor.close();
    }

    public Usuario loadCuota(String user){

        Usuario u = null;
        String[] columnas = new String[]{USER, CUOTA_USADA, CUOTA_TOTAL, NAV_TYPE};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, USER + "= '" + user + "'", null, null, null, null);

        //int iUser = cursor.getColumnIndex(USER);
        int iCU = cursor.getColumnIndex(CUOTA_USADA);
        int iCT = cursor.getColumnIndex(CUOTA_TOTAL);
        int iNAV = cursor.getColumnIndex(NAV_TYPE);
        int count = cursor.getCount();

        if(count==0){
            return u;
        }else{
            u = new Usuario();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                u.usuario = user;
                u.cuota = cursor.getInt(iCT);
                u.cuota_usada = cursor.getInt(iCU);
                u.nivel_nav = cursor.getString(iNAV);
            }
        }

        cursor.close();
        return u;
    }

    public void cleanDatabase(){
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        this.sqLiteDatabase.execSQL("CREATE TABLE " + T_NAME +
                "(" + ID + " INTEGER PRIMARY KEY, " +
                USER + " TEXT NOT NULL, " +
                CUOTA_USADA + " INTEGER NOT NULL, " +
                CUOTA_TOTAL + " INTEGER NOT NULL, " +
                NAV_TYPE + " TEXT NOT NULL);");
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + T_NAME +
                    "(" + ID + " INTEGER PRIMARY KEY, " +
                    USER + " TEXT NOT NULL, " +
                    CUOTA_USADA + " INTEGER NOT NULL, " +
                    CUOTA_TOTAL + " INTEGER NOT NULL, " +
                    NAV_TYPE + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + T_NAME);
            onCreate(db);
        }


    }
}
