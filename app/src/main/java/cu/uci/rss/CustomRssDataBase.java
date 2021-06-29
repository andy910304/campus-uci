package cu.uci.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Yannier on 4/28/2015.
 */

public class CustomRssDataBase {

    private static final String DB_NAME = "_rssCustomUci";

    private static final String T_NAME = "_newsCustom";

    private static final String ID = "_id";
    private static final String SOURCE = "_source";
    private static final String TITLE = "_title";
    private static final String URL = "_url";
    private static final String DESC = "_desc";
    private static final String IMG = "_img";
    private static final String PUBDATE = "_pubdate";
    private static final String AUTHOR = "_author";
    private static final String COMMENTS = "_comments";


    private static final String T_NAME_ = "_savedSource";
    private static final String ID_ = "_id";
    private static final String SOURCE_ = "_source";
    private static final String URL_ = "_url";

    private static final int VERSION = 1;



    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public CustomRssDataBase(Context context){

        this.context = context;

    }

    public void openCustomRssDatabase(){
        this.dbHelper = new DBHelper(this.context);
        this.sqLiteDatabase = this.dbHelper.getWritableDatabase();
    }

    public void closeCustomRssDatabase(){
        this.dbHelper.close();
    }

    public void saveRss(ArrayList<Item> items, String source) {

        //int count = items.size();
        ArrayList<Item> _items = items;

        String[] columnas = new String[]{SOURCE, TITLE, URL, DESC, IMG, PUBDATE, AUTHOR, COMMENTS};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, SOURCE + "= '" + source + "'",
                null,
                null, null, null);
        int cursor_count = cursor.getCount();

        if(cursor_count > 0){
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                this.sqLiteDatabase.delete(T_NAME, SOURCE + "= '" + source + "'", null);
            }
        }

        for(Item item:_items){

            ContentValues cv = new ContentValues();

            cv.put(SOURCE,source);
            cv.put(TITLE,item.title);
            cv.put(URL,item.url);
            cv.put(DESC,item.desc);

            if(item.img != null){
                cv.put(IMG,item.img);
            }else{
                cv.put(IMG,RssDataBaseAttr.NOT_IMAGE);
            }

            if(item.pubDate != null){
                cv.put(PUBDATE,item.pubDate);
            }else{
                cv.put(PUBDATE,RssDataBaseAttr.NOT_PUBDATE);
            }

            if(item.author != null){
                cv.put(AUTHOR,item.author);
            }else{
                cv.put(AUTHOR,RssDataBaseAttr.NOT_AUTHOR);
            }

            if(item.comments != null){
                cv.put(COMMENTS,item.comments);
            }else{
                cv.put(COMMENTS,RssDataBaseAttr.NOT_COMMENTS);
            }

            this.sqLiteDatabase.insert(T_NAME,null,cv);

        }

        cursor.close();

    }

    public int saveRss_(String source, String url) {

        String[] columnas = new String[]{SOURCE_,URL_};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME_, columnas,
                SOURCE_ + "= '" + source + "' or " + URL_ + "= '" + url + "'",
                null,
                null, null, null);
        int cursor_count = cursor.getCount();
        cursor.close();

        if(cursor_count > 0){
            return 0;
        }else{
            ContentValues cv = new ContentValues();
            cv.put(SOURCE_,source);
            cv.put(URL_,url);
            this.sqLiteDatabase.insert(T_NAME_,null,cv);
            return 1;
        }


    }

    public Item[] loadCustomRss(String source) {

        Item[] items = null;
        Item item;
        String[] columnas = new String[]{TITLE, URL, DESC, IMG, PUBDATE, AUTHOR, COMMENTS};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME, columnas, SOURCE + "= '" + source + "'", null, null, null, null);
        items = new Item[cursor.getCount()];
        int cursor_count = 0;

        //int iSource = cursor.getColumnIndex(SOURCE);
        if(cursor.getCount() == 0){
            return items;
        }else{

            int iTitle = cursor.getColumnIndex(TITLE);
            int iUrl = cursor.getColumnIndex(URL);
            int iDesc = cursor.getColumnIndex(DESC);
            int iImg = cursor.getColumnIndex(IMG);
            int iPubdate = cursor.getColumnIndex(PUBDATE);
            int iAuthor = cursor.getColumnIndex(AUTHOR);
            int iComments = cursor.getColumnIndex(COMMENTS);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

                item = new Item();

                item.title = cursor.getString(iTitle);
                item.url = cursor.getString(iUrl);
                item.desc = cursor.getString(iDesc);
                item.img = RssDataBaseAttr.NOT_IMAGE;
                item.pubDate = cursor.getString(iPubdate);
                item.author = cursor.getString(iAuthor);
                item.comments = cursor.getString(iComments);

                items[cursor_count] = item;

                cursor_count++;

            }
        }

        return  items;
    }

    public CustomRss[] loadCustomRss_() {

        CustomRss[] items = null;
        CustomRss item;
        String[] columnas = new String[]{SOURCE_, URL_};
        Cursor cursor = this.sqLiteDatabase.query(T_NAME_, columnas, null, null, null, null, null);
        items = new CustomRss[cursor.getCount()];
        int cursor_count = 0;

        //int iSource = cursor.getColumnIndex(SOURCE);
        if(cursor.getCount() == 0){
            return items;
        }else{

            int iSource_ = cursor.getColumnIndex(SOURCE_);
            int iUrl_ = cursor.getColumnIndex(URL_);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){

                item = new CustomRss();

                item.source_ = cursor.getString(iSource_);
                item.url_ = cursor.getString(iUrl_);

                items[cursor_count] = item;

                cursor_count++;

            }
        }

        return  items;
    }

    public void deleteCustomRss(String source){
        this.sqLiteDatabase.delete(T_NAME_,SOURCE_ + "='" + source + "'", null);
        this.sqLiteDatabase.delete(T_NAME,SOURCE_ + "='" + source + "'", null);
    }

    public void cleanCustomDatabase(){
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        this.sqLiteDatabase.execSQL("CREATE TABLE " + T_NAME +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SOURCE + " TEXT NOT NULL, "
                + TITLE + " TEXT NOT NULL, "
                + URL + " TEXT NOT NULL, "
                + DESC + " TEXT NOT NULL, "
                + IMG + " TEXT NOT NULL, "
                + PUBDATE + " TEXT NOT NULL, "
                + AUTHOR + " TEXT NOT NULL, "
                + COMMENTS + " TEXT NOT NULL);");
    }

    public void cleanCustomDatabase_(){
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME);
        this.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + T_NAME_);

        this.sqLiteDatabase.execSQL("CREATE TABLE " + T_NAME_ +
                "(" + ID_ + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SOURCE_ + " TEXT NOT NULL, "
                + URL_ + " TEXT NOT NULL);");
        this.sqLiteDatabase.execSQL("CREATE TABLE " + T_NAME +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SOURCE + " TEXT NOT NULL, "
                + TITLE + " TEXT NOT NULL, "
                + URL + " TEXT NOT NULL, "
                + DESC + " TEXT NOT NULL, "
                + IMG + " TEXT NOT NULL, "
                + PUBDATE + " TEXT NOT NULL, "
                + AUTHOR + " TEXT NOT NULL, "
                + COMMENTS + " TEXT NOT NULL);");
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + T_NAME_ +
                    "(" + ID_ + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SOURCE_ + " TEXT NOT NULL, "
                    + URL_ + " TEXT NOT NULL);");

            db.execSQL("CREATE TABLE " + T_NAME +
                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SOURCE + " TEXT NOT NULL, "
                    + TITLE + " TEXT NOT NULL, "
                    + URL + " TEXT NOT NULL, "
                    + DESC + " TEXT NOT NULL, "
                    + IMG + " TEXT NOT NULL, "
                    + PUBDATE + " TEXT NOT NULL, "
                    + AUTHOR + " TEXT NOT NULL, "
                    + COMMENTS + " TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + T_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + T_NAME_);
            onCreate(db);
        }


    }

}