package cu.uci.horario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yannier on 5/7/2015.
 */
public class DBHorario {

    private static final String DB_NAME = "_horarioUci";
    private static final String T_NAME = "_horario";

    private static final String ID = "_id";

    private static final String LUNES_PRIMERA = "_lunes_primera";
    private static final String LUNES_SEGUNDA = "_lunes_segunda";
    private static final String LUNES_TERCERA = "_lunes_tercera";
    private static final String LUNES_CUARTA = "_lunes_cuarta";
    private static final String LUNES_QUINTA = "_lunes_quinta";
    private static final String LUNES_SEXTA = "_lunes_sexta";

    private static final String MARTES_PRIMERA = "_martes_primera";
    private static final String MARTES_SEGUNDA = "_martes_segunda";
    private static final String MARTES_TERCERA = "_martes_tercera";
    private static final String MARTES_CUARTA = "_martes_cuarta";
    private static final String MARTES_QUINTA = "_martes_quinta";
    private static final String MARTES_SEXTA = "_martes_sexta";

    private static final String MIERCOLES_PRIMERA = "_miercoles_primera";
    private static final String MIERCOLES_SEGUNDA = "_miercoles_segunda";
    private static final String MIERCOLES_TERCERA = "_miercoles_tercera";
    private static final String MIERCOLES_CUARTA = "_miercoles_cuarta";
    private static final String MIERCOLES_QUINTA = "_miercoles_quinta";
    private static final String MIERCOLES_SEXTA = "_miercoles_sexta";

    private static final String JUEVES_PRIMERA = "_jueves_primera";
    private static final String JUEVES_SEGUNDA = "_jueves_segunda";
    private static final String JUEVES_TERCERA = "_jueves_tercera";
    private static final String JUEVES_CUARTA = "_jueves_cuarta";
    private static final String JUEVES_QUINTA = "_jueves_quinta";
    private static final String JUEVES_SEXTA = "_jueves_sexta";

    private static final String VIERNES_PRIMERA = "_viernes_primera";
    private static final String VIERNES_SEGUNDA = "_viernes_segunda";
    private static final String VIERNES_TERCERA = "_viernes_tercera";
    private static final String VIERNES_CUARTA = "_viernes_cuarta";
    private static final String VIERNES_QUINTA = "_viernes_quinta";
    private static final String VIERNES_SEXTA = "_viernes_sexta";

    private static final String SABADO_PRIMERA = "_sabado_primera";
    private static final String SABADO_SEGUNDA = "_sabado_segunda";
    private static final String SABADO_TERCERA = "_sabado_tercera";
    private static final String SABADO_CUARTA = "_sabado_cuarta";
    private static final String SABADO_QUINTA = "_sabado_quinta";
    private static final String SABADO_SEXTA = "_sabado_sexta";

    private static final String DOMINGO_PRIMERA = "_domingo_primera";
    private static final String DOMINGO_SEGUNDA = "_domingo_segunda";
    private static final String DOMINGO_TERCERA = "_domingo_tercera";
    private static final String DOMINGO_CUARTA = "_domingo_cuarta";
    private static final String DOMINGO_QUINTA = "_domingo_quinta";
    private static final String DOMINGO_SEXTA = "_domingo_sexta";

    private static final int VERSION = 1;

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DBHorario(Context context){

        this.context = context;

    }

    public void openHorarioDatabase(){
        this.dbHelper = new DBHelper(this.context);
        this.sqLiteDatabase = this.dbHelper.getWritableDatabase();
    }

    public void closeHorarioDatabase(){
        this.dbHelper.close();
    }

    public void saveHorario(String[] horario) {
        String[] h = horario;
        ContentValues cv = new ContentValues();
        cv.put(ID,1);

        cv.put(LUNES_PRIMERA,h[0]);
        cv.put(MARTES_PRIMERA,h[1]);
        cv.put(MIERCOLES_PRIMERA,h[2]);
        cv.put(JUEVES_PRIMERA,h[3]);
        cv.put(VIERNES_PRIMERA,h[4]);
        cv.put(SABADO_PRIMERA,h[5]);
        cv.put(DOMINGO_PRIMERA,h[6]);

        cv.put(LUNES_SEGUNDA,h[7]);
        cv.put(MARTES_SEGUNDA,h[8]);
        cv.put(MIERCOLES_SEGUNDA,h[9]);
        cv.put(JUEVES_SEGUNDA,h[10]);
        cv.put(VIERNES_SEGUNDA,h[11]);
        cv.put(SABADO_SEGUNDA,h[12]);
        cv.put(DOMINGO_SEGUNDA,h[13]);

        cv.put(LUNES_TERCERA,h[14]);
        cv.put(MARTES_TERCERA,h[15]);
        cv.put(MIERCOLES_TERCERA,h[16]);
        cv.put(JUEVES_TERCERA,h[17]);
        cv.put(VIERNES_TERCERA,h[18]);
        cv.put(SABADO_TERCERA,h[19]);
        cv.put(DOMINGO_TERCERA,h[20]);

        cv.put(LUNES_CUARTA,h[21]);
        cv.put(MARTES_CUARTA,h[22]);
        cv.put(MIERCOLES_CUARTA,h[23]);
        cv.put(JUEVES_CUARTA,h[24]);
        cv.put(VIERNES_CUARTA,h[25]);
        cv.put(SABADO_CUARTA,h[26]);
        cv.put(DOMINGO_CUARTA,h[27]);

        cv.put(LUNES_QUINTA,h[28]);
        cv.put(MARTES_QUINTA,h[29]);
        cv.put(MIERCOLES_QUINTA,h[30]);
        cv.put(JUEVES_QUINTA,h[31]);
        cv.put(VIERNES_QUINTA,h[32]);
        cv.put(SABADO_QUINTA,h[33]);
        cv.put(DOMINGO_QUINTA,h[34]);

        cv.put(LUNES_SEXTA,h[35]);
        cv.put(MARTES_SEXTA,h[36]);
        cv.put(MIERCOLES_SEXTA,h[37]);
        cv.put(JUEVES_SEXTA,h[38]);
        cv.put(VIERNES_SEXTA,h[39]);
        cv.put(SABADO_SEXTA,h[40]);
        cv.put(DOMINGO_SEXTA,h[41]);

        String[] columnas = new String[]{ID};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

        if(cursor.getCount()>0){
            this.sqLiteDatabase.update(T_NAME, cv, ID + "=" + 1, null);
        }else{
            this.sqLiteDatabase.insert(T_NAME,null,cv);
        }

        cursor.close();

    }


    public String[] loadHorario(){

        String[] info = new String[42];
        String[] columnas = new String[]{ID,
        LUNES_PRIMERA,MARTES_PRIMERA,MIERCOLES_PRIMERA,JUEVES_PRIMERA,VIERNES_PRIMERA,SABADO_PRIMERA, DOMINGO_PRIMERA,
        LUNES_SEGUNDA,MARTES_SEGUNDA,MIERCOLES_SEGUNDA,JUEVES_SEGUNDA,VIERNES_SEGUNDA,SABADO_SEGUNDA,DOMINGO_SEGUNDA,
        LUNES_TERCERA,MARTES_TERCERA,MIERCOLES_TERCERA,JUEVES_TERCERA,VIERNES_TERCERA,SABADO_TERCERA,DOMINGO_TERCERA,
        LUNES_CUARTA,MARTES_CUARTA,MIERCOLES_CUARTA,JUEVES_CUARTA,VIERNES_CUARTA,SABADO_CUARTA,DOMINGO_CUARTA,
        LUNES_QUINTA,MARTES_QUINTA,MIERCOLES_QUINTA,JUEVES_QUINTA,VIERNES_QUINTA,SABADO_QUINTA,DOMINGO_QUINTA,
        LUNES_SEXTA,MARTES_SEXTA,MIERCOLES_SEXTA,JUEVES_SEXTA,VIERNES_SEXTA,SABADO_SEXTA,DOMINGO_SEXTA,
        };
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, null, null, null, null, null);

        int count = cursor.getCount();

        if(count==0){
            info = null;
        }else{

            int iLunes_Primera = cursor.getColumnIndex(LUNES_PRIMERA);
            int iMartes_Primera = cursor.getColumnIndex(MARTES_PRIMERA);
            int iMiercoles_Primera = cursor.getColumnIndex(MIERCOLES_PRIMERA);
            int iJueves_Primera = cursor.getColumnIndex(JUEVES_PRIMERA);
            int iViernes_Primera = cursor.getColumnIndex(VIERNES_PRIMERA);
            int iSabado_Primera = cursor.getColumnIndex(SABADO_PRIMERA);
            int iDomingo_Primera = cursor.getColumnIndex(DOMINGO_PRIMERA);

            int iLunes_Segunda = cursor.getColumnIndex(LUNES_SEGUNDA);
            int iMartes_Segunda = cursor.getColumnIndex(MARTES_SEGUNDA);
            int iMiercoles_Segunda = cursor.getColumnIndex(MIERCOLES_SEGUNDA);
            int iJueves_Segunda = cursor.getColumnIndex(JUEVES_SEGUNDA);
            int iViernes_Segunda = cursor.getColumnIndex(VIERNES_SEGUNDA);
            int iSabado_Segunda = cursor.getColumnIndex(SABADO_SEGUNDA);
            int iDomingo_Segunda = cursor.getColumnIndex(DOMINGO_SEGUNDA);

            int iLunes_Tercera = cursor.getColumnIndex(LUNES_TERCERA);
            int iMartes_Tercera = cursor.getColumnIndex(MARTES_TERCERA);
            int iMiercoles_Tercera = cursor.getColumnIndex(MIERCOLES_TERCERA);
            int iJueves_Tercera = cursor.getColumnIndex(JUEVES_TERCERA);
            int iViernes_Tercera = cursor.getColumnIndex(VIERNES_TERCERA);
            int iSabado_Tercera = cursor.getColumnIndex(SABADO_TERCERA);
            int iDomingo_Tercera = cursor.getColumnIndex(DOMINGO_TERCERA);

            int iLunes_Cuarta = cursor.getColumnIndex(LUNES_CUARTA);
            int iMartes_Cuarta = cursor.getColumnIndex(MARTES_CUARTA);
            int iMiercoles_Cuarta = cursor.getColumnIndex(MIERCOLES_CUARTA);
            int iJueves_Cuarta = cursor.getColumnIndex(JUEVES_CUARTA);
            int iViernes_Cuarta = cursor.getColumnIndex(VIERNES_CUARTA);
            int iSabado_Cuarta = cursor.getColumnIndex(SABADO_CUARTA);
            int iDomingo_Cuarta = cursor.getColumnIndex(DOMINGO_CUARTA);

            int iLunes_Quinta = cursor.getColumnIndex(LUNES_QUINTA);
            int iMartes_Quinta = cursor.getColumnIndex(MARTES_QUINTA);
            int iMiercoles_Quinta = cursor.getColumnIndex(MIERCOLES_QUINTA);
            int iJueves_Quinta = cursor.getColumnIndex(JUEVES_QUINTA);
            int iViernes_Quinta = cursor.getColumnIndex(VIERNES_QUINTA);
            int iSabado_Quinta = cursor.getColumnIndex(SABADO_QUINTA);
            int iDomingo_Quinta = cursor.getColumnIndex(DOMINGO_QUINTA);

            int iLunes_Sexta = cursor.getColumnIndex(LUNES_SEXTA);
            int iMartes_Sexta = cursor.getColumnIndex(MARTES_SEXTA);
            int iMiercoles_Sexta = cursor.getColumnIndex(MIERCOLES_SEXTA);
            int iJueves_Sexta = cursor.getColumnIndex(JUEVES_SEXTA);
            int iViernes_Sexta = cursor.getColumnIndex(VIERNES_SEXTA);
            int iSabado_Sexta = cursor.getColumnIndex(SABADO_SEXTA);
            int iDomingo_Sexta = cursor.getColumnIndex(DOMINGO_SEXTA);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

                info[0] = cursor.getString(iLunes_Primera);
                info[1] = cursor.getString(iMartes_Primera);
                info[2] = cursor.getString(iMiercoles_Primera);
                info[3] = cursor.getString(iJueves_Primera);
                info[4] = cursor.getString(iViernes_Primera);
                info[5] = cursor.getString(iSabado_Primera);
                info[6] = cursor.getString(iDomingo_Primera);

                info[7] = cursor.getString(iLunes_Segunda);
                info[8] = cursor.getString(iMartes_Segunda);
                info[9] = cursor.getString(iMiercoles_Segunda);
                info[10] = cursor.getString(iJueves_Segunda);
                info[11] = cursor.getString(iViernes_Segunda);
                info[12] = cursor.getString(iSabado_Segunda);
                info[13] = cursor.getString(iDomingo_Segunda);

                info[14] = cursor.getString(iLunes_Tercera);
                info[15] = cursor.getString(iMartes_Tercera);
                info[16] = cursor.getString(iMiercoles_Tercera);
                info[17] = cursor.getString(iJueves_Tercera);
                info[18] = cursor.getString(iViernes_Tercera);
                info[19] = cursor.getString(iSabado_Tercera);
                info[20] = cursor.getString(iDomingo_Tercera);

                info[21] = cursor.getString(iLunes_Cuarta);
                info[22] = cursor.getString(iMartes_Cuarta);
                info[23] = cursor.getString(iMiercoles_Cuarta);
                info[24] = cursor.getString(iJueves_Cuarta);
                info[25] = cursor.getString(iViernes_Cuarta);
                info[26] = cursor.getString(iSabado_Cuarta);
                info[27] = cursor.getString(iDomingo_Cuarta);

                info[28] = cursor.getString(iLunes_Quinta);
                info[29] = cursor.getString(iMartes_Quinta);
                info[30] = cursor.getString(iMiercoles_Quinta);
                info[31] = cursor.getString(iJueves_Quinta);
                info[32] = cursor.getString(iViernes_Quinta);
                info[33] = cursor.getString(iSabado_Quinta);
                info[34] = cursor.getString(iDomingo_Quinta);

                info[35] = cursor.getString(iLunes_Sexta);
                info[36] = cursor.getString(iMartes_Sexta);
                info[37] = cursor.getString(iMiercoles_Sexta);
                info[38] = cursor.getString(iJueves_Sexta);
                info[39] = cursor.getString(iViernes_Sexta);
                info[40] = cursor.getString(iSabado_Sexta);
                info[41] = cursor.getString(iDomingo_Sexta);
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
            db.execSQL("CREATE TABLE " + T_NAME + "(" +
                    ID + " INTEGER PRIMARY KEY, " +

                    LUNES_PRIMERA + " TEXT NOT NULL, " +
                    MARTES_PRIMERA + " TEXT NOT NULL, " +
                    MIERCOLES_PRIMERA + " TEXT NOT NULL, " +
                    JUEVES_PRIMERA + " TEXT NOT NULL, " +
                    VIERNES_PRIMERA + " TEXT NOT NULL, " +
                    SABADO_PRIMERA + " TEXT NOT NULL, " +
                    DOMINGO_PRIMERA + " TEXT NOT NULL, " +

                    LUNES_SEGUNDA + " TEXT NOT NULL, " +
                    MARTES_SEGUNDA + " TEXT NOT NULL, " +
                    MIERCOLES_SEGUNDA + " TEXT NOT NULL, " +
                    JUEVES_SEGUNDA + " TEXT NOT NULL, " +
                    VIERNES_SEGUNDA + " TEXT NOT NULL, " +
                    SABADO_SEGUNDA + " TEXT NOT NULL, " +
                    DOMINGO_SEGUNDA + " TEXT NOT NULL, " +

                    LUNES_TERCERA + " TEXT NOT NULL, " +
                    MARTES_TERCERA + " TEXT NOT NULL, " +
                    MIERCOLES_TERCERA + " TEXT NOT NULL, " +
                    JUEVES_TERCERA + " TEXT NOT NULL, " +
                    VIERNES_TERCERA + " TEXT NOT NULL, " +
                    SABADO_TERCERA + " TEXT NOT NULL, " +
                    DOMINGO_TERCERA + " TEXT NOT NULL, " +

                    LUNES_CUARTA + " TEXT NOT NULL, " +
                    MARTES_CUARTA + " TEXT NOT NULL, " +
                    MIERCOLES_CUARTA + " TEXT NOT NULL, " +
                    JUEVES_CUARTA + " TEXT NOT NULL, " +
                    VIERNES_CUARTA + " TEXT NOT NULL, " +
                    SABADO_CUARTA + " TEXT NOT NULL, " +
                    DOMINGO_CUARTA + " TEXT NOT NULL, " +

                    LUNES_QUINTA + " TEXT NOT NULL, " +
                    MARTES_QUINTA + " TEXT NOT NULL, " +
                    MIERCOLES_QUINTA + " TEXT NOT NULL, " +
                    JUEVES_QUINTA + " TEXT NOT NULL, " +
                    VIERNES_QUINTA + " TEXT NOT NULL, " +
                    SABADO_QUINTA + " TEXT NOT NULL, " +
                    DOMINGO_QUINTA + " TEXT NOT NULL, " +

                    LUNES_SEXTA + " TEXT NOT NULL, " +
                    MARTES_SEXTA + " TEXT NOT NULL, " +
                    MIERCOLES_SEXTA + " TEXT NOT NULL, " +
                    JUEVES_SEXTA + " TEXT NOT NULL, " +
                    VIERNES_SEXTA + " TEXT NOT NULL, " +
                    SABADO_SEXTA + " TEXT NOT NULL, " +
                    DOMINGO_SEXTA + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + T_NAME);
            onCreate(db);
        }


    }

}
