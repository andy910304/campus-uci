package cu.uci.mapa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cu.uci.mapa.lugares.Favorito;

public class DBFavorito {

	static final String ID_FILA = "_id";
	static final String DESCRIP = "_descrip";
	static final String LATITUDE = "_latitude";
	static final String LONGITUDE = "_longitude";

	private static final String N_BD = "Favoritos";
	private static final String N_TABLA = "Tabla_Favoritos";
	private static final int VERSION = 1;

	private BDHelper helper;
	private final Context ctx;
	private SQLiteDatabase db;

	public DBFavorito(Context ctx) {
		this.ctx = ctx;
	}

	public DBFavorito open() {

		this.helper = new BDHelper(this.ctx);
		this.db = this.helper.getReadableDatabase();
		return this;
	}

	public void close() {

		this.helper.close();
	}

	public long saveFavorite(String d, double lat, double lon) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(DESCRIP, d);
		cv.put(LATITUDE, lat);
		cv.put(LONGITUDE, lon);
		return this.db.insert(N_TABLA, null, cv);
	}

	public ArrayList<Favorito> getData() {
		// TODO Auto-generated method stub
		ArrayList<Favorito> aux = new ArrayList<Favorito>();
		String[] columnas = { ID_FILA, DESCRIP, LATITUDE, LONGITUDE };
		Cursor c = this.db.query(N_TABLA, columnas, null, null, null, null,
				null);

		int iFila = c.getColumnIndex(ID_FILA);
		int iDescrip = c.getColumnIndex(DESCRIP);
		int iLat = c.getColumnIndex(LATITUDE);
		int iLon = c.getColumnIndex(LONGITUDE);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			Favorito fav = new Favorito(c.getString(iDescrip),
					c.getDouble(iLat), c.getDouble(iLon));
			aux.add(fav);
		}
		return aux;
	}
	
	public void delete(String descrip2) {
		// TODO Auto-generated method stub
		this.db.delete(N_TABLA, DESCRIP + "=" + "'" + descrip2 + "'", null);
	}

	
	public void edit(String desc, String string, double la, double lo) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(DESCRIP, string);
		cv.put(LATITUDE, la);
		cv.put(LONGITUDE, lo);
		this.db.update(N_TABLA, cv, DESCRIP + "=" + "'" + desc + "'", null);
	}

	private static class BDHelper extends SQLiteOpenHelper {

		public BDHelper(Context context) {
			super(context, N_BD, null, VERSION);
			// TODO Auto-generated constructor stub

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + N_TABLA + "(" + ID_FILA
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DESCRIP
					+ " TEXT NOT NULL, " + LATITUDE + " DOUBLE, " + LONGITUDE
					+ " DOUBLE);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + N_TABLA);
			onCreate(db);
		}

	}


	

	
}
