package cu.uci.menu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yannier on 25/02/2015.
 */
public class MenuDataBase {

	private static final String DB_NAME = "_menuUci";
	private static final String T_NAME = "_menu";
	private static final String ID = "_id";
	private static final String LUNCH = "_lunch";
	private static final String DINNER = "_dinner";
	private static final int VERSION = 1;

	private DBHelper dbHelper;
	private Context context;
	private SQLiteDatabase sqLiteDatabase;

public MenuDataBase(Context context){

	this.context = context;

}

	public void openMenuDatabase(){
		this.dbHelper = new DBHelper(this.context);
		this.sqLiteDatabase = this.dbHelper.getWritableDatabase();
	}

	public void closeMenuDatabase(){
		this.dbHelper.close();
	}

	public void saveMenu(String lunch, String dinner) {
		ContentValues cv = new ContentValues();
		cv.put(ID,1);
		cv.put(LUNCH,lunch);
		cv.put(DINNER,dinner);

		String[] columnas = new String[]{ID, LUNCH,DINNER};
		Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

		if(cursor.getCount()>0){
           this.sqLiteDatabase.update(T_NAME, cv, ID + "=" + 1, null);
		}else{
			this.sqLiteDatabase.insert(T_NAME,null,cv);
		}

		cursor.close();

	}

	public String[] loadLastMenu(){

		String[] info = new String[2];
 		String[] columnas = new String[]{ID, LUNCH,DINNER};
		Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

		int iFila = cursor.getColumnIndex(ID);
		int iLunch = cursor.getColumnIndex(LUNCH);
		int iDinner = cursor.getColumnIndex(DINNER);
		int count = cursor.getCount();

        if(count==0){
			info[0]="";
			info[1]="";
		}else{
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                info[0] = cursor.getString(iLunch);
				info[1] = cursor.getString(iDinner);
			}
		}

		cursor.close();
		return info;
	}

    public void cleanDatabase(){
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        this.sqLiteDatabase.execSQL("CREATE TABLE " + T_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
                LUNCH + " TEXT NOT NULL, " + DINNER + " TEXT NOT NULL);");
    }

	private static class DBHelper extends SQLiteOpenHelper{

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + T_NAME + "(" + ID + " INTEGER PRIMARY KEY, " +
		LUNCH + " TEXT NOT NULL, " + DINNER + " TEXT NOT NULL);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + T_NAME);
    	onCreate(db);
	}


}

}
