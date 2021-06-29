package cu.uci.rss;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cu.uci.campusuci.AboutActivity;
import cu.uci.campusuci.CampusNotification;
import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.LoginUser;
import cu.uci.campusuci.MainActivity;
import cu.uci.campusuci.NavigationDrawer;
import cu.uci.campusuci.R;
import cu.uci.chatty.ChatActivity;
import cu.uci.cuota.CuotaActivity;
import cu.uci.directorio.DirectorioActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.mapa.MapaActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.DialogAction;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;
import cu.uci.utils.SSL._FakeX509TrustManager;

public class RssActivity extends ActionBarActivity implements ActionBar.OnNavigationListener,
        AdapterView.OnItemClickListener, SensorEventListener{

    public static Activity ctx;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SensorManager sm;
    Sensor sensor;
    Boolean thread_running = false;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    public static NavigationDrawer drawer;
    public static Boolean notif;
    public static int notifType;

    AssetManager am;
    TextView tv_init;
    Typeface roboto_condense_light, roboto_light_italic, roboto_condense_bold, roboto_light, roboto_condensed_light_italic;
    Toolbar mToolbar;
    ActionButton fab;
    MySpinnerAdapter mSpinnerAdapter;

    RssDataBase rssDataBase;
    String rss_source;

    CustomRssDataBase customRssDataBase;
    Item[] item_custom_database;


    boolean img_octavitos;
    boolean img_seguridad;
    boolean img_mindpro;
    boolean img_dragones;
    boolean img_internos;
    boolean img_iblog;
    boolean img_academia;
    boolean img_gladiadores;
    boolean img_venus;
    boolean img_java;
    boolean img_firefox;
    boolean img_rice;
    boolean img_calisoft;
    boolean img_custom;

    int rss_selected;

    ImageView iv_up;

    String [] rss;
    String rss_custom;
    int pos_ = 0;

    String[] urls = new String[]{
            "http://firefoxmania.uci.cu/feed/",
            "http://humanos.uci.cu/feed/",
            "http://internos.uci.cu/rss",
            "https://dragones.uci.cu/index.php/secciones?format=feed",
            "https://octavitos.uci.cu/?q=rss.xml",
            "http://www.uci.cu/rss-uci",
            "https://iblog.uci.cu/feed/",
            "http://seguridad.uci.cu/?q=rss.xml",
            "http://android.uci.cu/feed/",
            "https://php.uci.cu/infusions/rss_panel/rss/rss_n.php",
            "http://academia.uci.cu/?feed=rss2",
            "https://dprog.facultad6.uci.cu/?feed=rss2",
            "https://venusit.uci.cu/feed/",
            "http://drupaleros.uci.cu/?q=rss.xml",
            "http://solucionesjava.blog.uci.cu/feed/",
            "http://periodico.uci.cu/feed/",
            "http://mindpro.uci.cu/servicios/feed",
            "https://rice.uci.cu/?feed=rss2",
            "https://blackhat.uci.cu/index.php/feed/",
            "https://soporte.uci.cu/comunidades/index.php?qa=feed&qa_1=qa.rss",
            "https://rcci.uci.cu/index.php?journal=rcci&page=gateway&op=plugin&path[]=WebFeedGatewayPlugin&path[]=rss2",
            "https://calisoft.uci.cu/index.php?format=feed&type=rss",
            "https://fenix.uci.cu/?feed=rss2",
            "https://none",
            "https://none"};

            //https://facultad6.uci.cu/index.php?format=feed&type=rss

    int[] imgs = new int[]{
            R.drawable.firefox,
            R.drawable.humanos,
            R.drawable.internos,
            R.drawable.dragon3s1,
            R.drawable.octavito,
            R.drawable.uci,
            R.drawable.apple,
            R.drawable.seguridad,
            R.drawable.android,
            R.drawable.ic_php,
            R.drawable.ic_academia,
            R.drawable.ic_gladiadores,
            R.drawable.ic_venus,
            R.drawable.ic_drupal,
            R.drawable.ic_java,
            R.drawable.ic_mella,
            R.drawable.ic_mindpro,
            R.drawable.ic_rice,
            R.drawable.ic_blackhat,
            R.drawable.ic_soporte,
            R.drawable.ic_rcci,
            R.drawable.ic_calisoft,
            R.drawable.ic_fenix,
            R.drawable.ic_rss_nav
    };

    int[] imgs_2 = new int[]{
            R.drawable.firefox,
            R.drawable.humanos,
            R.drawable.internos,
            R.drawable.dragon3s1,
            R.drawable.octavito,
            R.drawable.uci,
            R.drawable.apple,
            R.drawable.seguridad,
            R.drawable.android,
            R.drawable.ic_php,
            R.drawable.ic_academia,
            R.drawable.ic_gladiadores,
            R.drawable.ic_venus,
            R.drawable.ic_drupal,
            R.drawable.ic_java,
            R.drawable.ic_mella,
            R.drawable.ic_mindpro,
            R.drawable.ic_rice,
            R.drawable.ic_blackhat,
            R.drawable.ic_soporte,
            R.drawable.ic_rcci,
            R.drawable.ic_calisoft,
            R.drawable.ic_fenix
    };

    ListView lv_rss;
    ImageView iv_source;
    TextView tv_source;
    CardView cv_source;

    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    org.w3c.dom.Document dom;
    Element root;
    NodeList items;

    ArrayList<String> firefoxmania;
    ArrayList<String> humanos;
    ArrayList<String> internos;
    ArrayList<String> dragones;
    ArrayList<String> octavitos;
    ArrayList<String> uci;
    ArrayList<String> iblog;
    ArrayList<String> seguridad;
    ArrayList<String> androiduci;
    ArrayList<String> php;
    ArrayList<String> academia;
    ArrayList<String> gladiadores;
    ArrayList<String> venus;
    ArrayList<String> drupal;
    ArrayList<String> java;
    ArrayList<String> mella;
    ArrayList<String> mindpro;
    ArrayList<String> rice;
    ArrayList<String> blackhat;
    ArrayList<String> soporte;
    ArrayList<String> rcci;
    ArrayList<String> calisoft;
    ArrayList<String> custom;

    ArrayList<Item> firefoxmaniaI;
    ArrayList<Item> humanosI;
    ArrayList<Item> internosI;
    ArrayList<Item> dragonesI;
    ArrayList<Item> octavitosI;
    ArrayList<Item> uciI;
    ArrayList<Item> iblogI;
    ArrayList<Item> seguridadI;
    ArrayList<Item> androiduciI;
    ArrayList<Item> phpI;
    ArrayList<Item> academiaI;
    ArrayList<Item> gladiadoresI;
    ArrayList<Item> venusI;
    ArrayList<Item> drupalI;
    ArrayList<Item> javaI;
    ArrayList<Item> mellaI;
    ArrayList<Item> mindproI;
    ArrayList<Item> riceI;
    ArrayList<Item> blackhatI;
    ArrayList<Item> soporteI;
    ArrayList<Item> rcciI;
    ArrayList<Item> calisoftI;
    ArrayList<Item> fenixI;
    ArrayList<Item> customI;
    ArrayList<Item> customIaux;
    ArrayList<AllItem> allItems;

    ImageView big;
    FrameLayout dark;
    Boolean big_in = false;

    //String[] datos;
    String[] datos_2;

    ArrayAdapter<String> aas;
    //ArrayAdapter<String> aal;
    MyListAdapter aal;
    MyAllListAdapter myAllListAdapter;

    int which = 0;
    int last = 0;
    int last_wrong = 0;

    View shadow;

    Boolean drawerIn;
    public static Boolean isInFront;
    refreshRss refreshrss;
    refresAllhRss refresAllhRss;

    public static LoginUser me;
    public static Bundle bundle;

    Boolean on_create = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        ctx = this;

        initToolBar_FAB();
        initTextView();
        initOtherViews();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            entry();
        }
        //setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(R.color
        //        .primary_dark_color));
        setStatusBarColor();
    }

    public void setStatusBarColor(){
        LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build
                .VERSION_CODES.LOLLIPOP) {
            //lx.getLayoutParams().height = getStatusBarHeight();
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            //int actionBarHeight = getActionBarHeight();
            //int statusBarHeight = getStatusBarHeight();
            //action bar height
            //statusBar.getLayoutParams().height = mToolbar.getHeight();
            //statusBar.setBackgroundColor(color);
        }else{
            lx.setVisibility(View.GONE);
        }
    }

    public void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build
                .VERSION_CODES.LOLLIPOP) {
            //LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
            //lx.getLayoutParams().height = getStatusBarHeight();
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            //int actionBarHeight = getActionBarHeight();
            //int statusBarHeight = getStatusBarHeight() + mToolbar.getHeight();
            //action bar height
            statusBar.getLayoutParams().height = mToolbar.getHeight();
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStop() {
        isInFront = false;
        drawerLayout.closeDrawers();
        //finish();
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(sensor != null){
            sm.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        isInFront = true;
        drawerLayout.invalidate();
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        //initBundle();
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_RSS);
        notif = false;
        notifType = 0;
        super.onStart();
    }

    @Override
    protected void onRestart() {
        //drawer.nav_rss_stat.setVisibility(View.INVISIBLE);
        drawerLayout.invalidate();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else if(big_in){
            Animation anim = AnimationUtils.loadAnimation(RssActivity.this,
                    R.anim.fab_fade_out);
            big.startAnimation(anim);
            dark.startAnimation(anim);
            big.setVisibility(View.INVISIBLE);
            dark.setVisibility(View.INVISIBLE);
            big_in = false;
        }else{
            Intent i1 = new Intent(RssActivity.this,
                    MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
            //super.onBackPressed();
        }

    }

    private int getSelectedNews(){
        int rss_selected;
        if (sharedPreferences.contains(CampusPreference.RSS_PREF)) {
            rss_selected = sharedPreferences.getInt(CampusPreference.RSS_PREF, 0);
        } else {
            editor.putInt(CampusPreference.RSS_PREF, 0);
            editor.commit();
            rss_selected = sharedPreferences.getInt(CampusPreference.RSS_PREF,
                    0);
        }

        return  rss_selected;
    }

    private void initBundle(){
        me = new LoginUser();
        bundle = getIntent().getExtras();
        me.setUser(bundle.getString("NOMBRE"),bundle.getString("APELLIDOS"),bundle.getString("FOTO"),
                bundle.getString("EDIFICIO"),bundle.getString("AREA"),bundle.getString("DNI"),bundle.getString("APTO"),
                bundle.getString("EXP"),bundle.getString("CRED"),bundle.getString("USER"),bundle.getString("CORREO"),
                bundle.getString("TEL"),bundle.getString("CAT"),bundle.getString("PROV"),bundle.getString("MUN"),
                bundle.getString("SEXO"), bundle.getString("PASS"));
        //if(!me.nombre.equalsIgnoreCase("none")){
        //Log.d("!!!!!!!!!", me.nombre + " " + me.apellidos );
        //Log.d("!!!!!!!!!", me.foto );
        //drawer.setTextName(me.nombre + " " + me.apellidos);
        //Picasso.with(this).load(me.foto).into(drawer.nav_user_picture);
        drawerLayout.invalidate();

        //}
    }

    @Override
    protected void onDestroy() {
        refreshrss.cancel(true);
        refresAllhRss.cancel(true);
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_RSS);
        super.onDestroy();
    }

    private void initToolBar_FAB(){

        drawerIn = false;
        isInFront = true;
        notif = false;
        notifType = 0;
        drawer = new NavigationDrawer();

        dark = (FrameLayout) findViewById(R.id.fl_dark);

        rss = getResources().getStringArray(R.array.rss);
        big = (ImageView) findViewById(R.id.iv_big);
        big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(RssActivity.this,
                        R.anim.fab_fade_out);
                big.startAnimation(anim);
                dark.startAnimation(anim);
                big.setVisibility(View.INVISIBLE);
                dark.setVisibility(View.INVISIBLE);
                big_in = false;
            }
        });

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


        refreshrss = new refreshRss();
        refresAllhRss = new refresAllhRss();

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        rssDataBase = new RssDataBase(RssActivity.this);
        customRssDataBase = new CustomRssDataBase(RssActivity.this);

        notificationCompatBuilder = new NotificationCompat.Builder(RssActivity.this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        shadow = findViewById(R.id.shadow_toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            shadow.setMinimumHeight(4);
        }

        am = getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");
        roboto_condense_bold = Typeface.createFromAsset(am,"roboto_condense_bold.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");
        roboto_condensed_light_italic= Typeface.createFromAsset(am,"roboto_condensed_light_italic.ttf");

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_rss);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        setSupportActionBar(mToolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,mToolbar,
                R.string.app_name,R.string.app_name){

            public void onDrawerClosed(View view) {
                drawerIn = false;
                Log.d("!!!!!!!!", "closed");
            }

            public void onDrawerOpened(View drawerView) {
                //Acciones que se ejecutan cuando se despliega el drawer
                drawerIn = true;
                Log.d("!!!!!!!!", "opened");
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){

                    case R.id.wifi_off:
                        showPrefRssList();
                        break;

                    case R.id.new_rss:
                           showNewRssDialog();
                        break;

                    case R.id.action_custom_rss:
                        CustomRss[] customRsses;
                        customRssDataBase.openCustomRssDatabase();
                        customRsses = customRssDataBase.loadCustomRss_();
                        customRssDataBase.closeCustomRssDatabase();
                        showCustomRssSaved(customRsses);
                        break;


                    case R.id.action_custom_rss_show:
                        CustomRss[] customRss1;
                        customRssDataBase.openCustomRssDatabase();
                        customRss1 = customRssDataBase.loadCustomRss_();
                        customRssDataBase.closeCustomRssDatabase();
                        showCustomRssRecent(customRss1);
                        break;


                    case R.id.action_custom_rss_del:
                        CustomRss[] customRss;
                        customRssDataBase.openCustomRssDatabase();
                        customRss = customRssDataBase.loadCustomRss_();
                        customRssDataBase.closeCustomRssDatabase();
                        showCustomRssSavedDel(customRss);
                        break;

                    case R.id.action_last_delete:
                        Boolean general_delete_alert;
                        if (sharedPreferences.contains(CampusPreference.DELETE_ALERTS)) {
                            general_delete_alert = sharedPreferences.getBoolean(CampusPreference
                                            .DELETE_ALERTS,
                                    false);
                        } else {
                            editor.putBoolean(CampusPreference.DELETE_ALERTS, true);
                            editor.commit();
                            general_delete_alert = sharedPreferences.getBoolean(CampusPreference
                                            .DELETE_ALERTS,
                                    false);
                        }

                        if(general_delete_alert){
                            showDeleteDialog();
                        }else{
                            rssDataBase.openRssDatabase();
                            rssDataBase.cleanDatabase();
                            rssDataBase.closeRssDatabase();
                            Toast toast = Toast.makeText(RssActivity.this,
                                    getResources().getString(R.string._delete_alerts_), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                        break;

                }
                return true;
            }
        });

        //datos = getResources().getStringArray(R.array.rss);
        datos_2 = getResources().getStringArray(R.array.rss_2);
        mSpinnerAdapter = new MySpinnerAdapter(this, getSupportActionBar().getThemedContext(),
                rss, imgs,
                R.layout.spinner_layout,roboto_condense_light);
        getSupportActionBar().setListNavigationCallbacks(mSpinnerAdapter, this);

        fab = (ActionButton) findViewById(R.id.fab_loading);
        fab.setType(ActionButton.Type.DEFAULT);
        fab.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab.setImageResource(R.drawable.ic_fa_refresh);
        fab.setShadowRadius(4.4f);
        fab.setShadowXOffset(2.8f);
        fab.setShadowYOffset(2.1f);
        fab.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab.setStrokeWidth(0.0f);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread_running = true;
                Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                        R.anim.fab_rotate);
                fab.startAnimation(animation_fab);
                fab.setClickable(false);

                if(which != 23){
                    refreshrss = new refreshRss();
                    refreshrss.execute(which);
                }else{
                    refresAllhRss = new refresAllhRss();
                    refresAllhRss .execute(which);
                }

            }
        });

        if (sharedPreferences.contains(CampusPreference.RSS_PREF)) {
            rss_selected = sharedPreferences.getInt(CampusPreference.RSS_PREF, 0);
        } else {
            editor.putInt(CampusPreference.RSS_PREF, 0);
            editor.commit();
            rss_selected = sharedPreferences.getInt(CampusPreference.RSS_PREF,
                    0);
        }

        getSupportActionBar().setSelectedNavigationItem(rss_selected);
        which = rss_selected;

        boolean load;

        if (sharedPreferences.contains(CampusPreference.RSS_LOAD)) {
            load = sharedPreferences.getBoolean(CampusPreference.RSS_LOAD, true);
        } else {
            editor.putBoolean(CampusPreference.RSS_LOAD, false);
            editor.commit();
            load = sharedPreferences.getBoolean(CampusPreference.RSS_LOAD,
                    false);
        }

        if(load){
            Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                    R.anim.fab_rotate);
            fab.startAnimation(animation_fab);
            fab.setClickable(false);
            if(which != 23){
                refreshrss = new refreshRss();
                refreshrss.execute(which);
            }else{
                refresAllhRss = new refresAllhRss();
                refresAllhRss .execute(which);
            }
        }



    }

    private void initTextView(){

        tv_init = (TextView) findViewById(R.id.tv_init_text);
        tv_init.setTypeface(roboto_light_italic);

        tv_source = (TextView) findViewById(R.id.tv_source);
        tv_source.setTypeface(roboto_light_italic);

    }

    private void initOtherViews(){

        iv_up = (ImageView) findViewById(R.id.iv_up);
        iv_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(RssActivity.this, R.anim.fab_fade_out);
                iv_up.startAnimation(pop);
                lv_rss.setSelection(0);

                iv_up.setVisibility(View.INVISIBLE);
            }
        });

        iv_source = (ImageView) findViewById(R.id.iv_source);
        cv_source = (CardView) findViewById(R.id.cv_source);

        lv_rss = (ListView) findViewById(R.id.lv_rss);
        lv_rss.setOnItemClickListener(this);
        lv_rss.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentFirstVisibleItem;
            private int currentVisibleItemCount;
            private int currentScrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                this.currentScrollState = scrollState;
                    isNotInBegin();
            }

            private void isNotInBegin(){

                if (this.currentFirstVisibleItem == 0) {
                    if(iv_up.getVisibility() == View.VISIBLE){

                        Animation hide = AnimationUtils.loadAnimation(RssActivity.this,
                                R.anim.fab_fade_out);
                        iv_up.startAnimation(hide);
                        iv_up.setVisibility(View.INVISIBLE);

                    }
                    Log.d("&&&&&&&&&&&&&&&&&&&&&&", "00000000000000000000000000000");
                    /*** In this way I detect if there's been a scroll which has completed ***/
                    /*** do the work! ***/
                }else if (this.currentVisibleItemCount > 0) {
                    if(iv_up.getVisibility() == View.INVISIBLE){
                        Animation show = AnimationUtils.loadAnimation(RssActivity.this,
                                R.anim.fab_fade_in);
                        iv_up.setVisibility(View.VISIBLE);
                        iv_up.startAnimation(show);
                    }
                    Log.d("&&&&&&&&&&&&&&&&&&&&&&", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    /*** In this way I detect if there's been a scroll which has completed ***/
                    /*** do the work! ***/
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
            }
        });
        //lv_rss.setOnItemLongClickListener(this);

    }

    private void entry(){

        Animation animation_tv = AnimationUtils.loadAnimation(this, R.anim.tv_alpha);
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        mToolbar.startAnimation(animation_toolbar_in);
        shadow.startAnimation(animation_shadow_in);
        fab.startAnimation(animation_fab);
        tv_init.startAnimation(animation_tv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rss, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        this.which = i;

        boolean rss_load_select;

        if (sharedPreferences.contains(CampusPreference.RSS_LOAD_SELECT)) {
            rss_load_select = sharedPreferences.getBoolean(CampusPreference.RSS_LOAD_SELECT,
                    false);
        } else {
            editor.putBoolean(CampusPreference.RSS_LOAD_SELECT, false);
            editor.commit();
            rss_load_select = sharedPreferences.getBoolean(CampusPreference.RSS_LOAD_SELECT,
                    false);
        }

        if(rss_load_select && on_create){

            if(!thread_running){

                if(which != 23) {

                    thread_running = true;
                    Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation_fab);
                    fab.setClickable(false);
                    refreshRss refreshrss = new refreshRss();
                    refreshrss.execute(which);
                }else{
                    thread_running = true;
                    Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation_fab);
                    fab.setClickable(false);
                    refresAllhRss = new refresAllhRss();
                    refresAllhRss .execute(which);
                }
            }else{

                Toast toast = Toast.makeText(RssActivity.this,
                        RssActivity.this.getString(R.string
                                .mate), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            }

        }

        on_create = true;

        return true;
    }

    private void showAndHideInitText(){

        if(tv_init.getVisibility() == View.VISIBLE){
            Animation animation_tv = AnimationUtils.loadAnimation(RssActivity.this, R.anim.tv_fade_out);
            Animation animation_lv = AnimationUtils.loadAnimation(RssActivity.this,
                    R.anim.tv_alpha);
            tv_init.startAnimation(animation_tv);
            tv_init.setVisibility(View.INVISIBLE);
            lv_rss.startAnimation(animation_lv);
            tv_source.setVisibility(View.VISIBLE);
            tv_source.startAnimation(animation_lv);
            iv_source.setVisibility(View.VISIBLE);
            iv_source.startAnimation(animation_lv);
            cv_source.setVisibility(View.VISIBLE);
            cv_source.startAnimation(animation_lv);
        }else{
            Animation animation_lv = AnimationUtils.loadAnimation(RssActivity.this,
                    R.anim.tv_alpha);
            lv_rss.startAnimation(animation_lv);
            tv_source.setVisibility(View.VISIBLE);
            tv_source.startAnimation(animation_lv);
            iv_source.setVisibility(View.VISIBLE);
            iv_source.startAnimation(animation_lv);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int what_;

        if (sharedPreferences.contains(CampusPreference.RSS_WHAT)) {
            what_ = sharedPreferences.getInt(CampusPreference.RSS_WHAT, 0);
        } else {
            editor.putInt(CampusPreference.RSS_WHAT, 0);
            editor.commit();
            what_ = sharedPreferences.getInt(CampusPreference.RSS_WHAT,
                    0);
        }

        final int pos = position;
        Thread t;

        switch (last){

            case 23:
                if(what_ == 0){
                    showAllNewsDialog(allItems.get(pos).img_drawable,allItems.get(pos).source,
                            allItems.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(allItems.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }

                break;

            case 24:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_favorite_rss,rss_custom,customI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(customI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }

                break;

            case 0:
                if(what_ == 0){
                    showNewsDialog(R.drawable.firefox,rss[last],firefoxmaniaI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(firefoxmaniaI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }

                break;

            case 1:
                if(what_ == 0){
                    showNewsDialog(R.drawable.humanos,rss[last],humanosI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(humanosI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 2:
                if(what_ == 0){
                    showNewsDialog(R.drawable.internos,rss[last],internosI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(internosI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 3:
                if(what_ == 0){
                    showNewsDialog(R.drawable.dragon3s1,rss[last],dragonesI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(dragonesI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 4:
                if(what_ == 0){
                    showNewsDialog(R.drawable.octavito,rss[last],octavitosI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(octavitosI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 5:
                if(what_ == 0){
                    showNewsDialog(R.drawable.uci,rss[last],uciI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(uciI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 6:
                if(what_ == 0){
                    showNewsDialog(R.drawable.apple,rss[last],iblogI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(iblogI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 7:
                if(what_ == 0){
                    showNewsDialog(R.drawable.seguridad,rss[last],seguridadI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(seguridadI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 8:
                if(what_ == 0){
                    showNewsDialog(R.drawable.android,rss[last],androiduciI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(androiduciI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 9:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_php,rss[last],phpI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(phpI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 10:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_academia,rss[last],academiaI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(academiaI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 11:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_gladiadores,rss[last],gladiadoresI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(gladiadoresI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 12:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_venus,rss[last],venusI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(venusI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 13:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_drupal,rss[last],drupalI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(drupalI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 14:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_java,rss[last],javaI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(javaI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 15:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_mella,rss[last],mellaI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(mellaI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 16:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_mindpro,rss[last],mindproI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(mindproI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;
            case 17:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_rice,rss[last],riceI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(riceI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 18:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_blackhat,rss[last],blackhatI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(blackhatI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 19:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_soporte,rss[last],soporteI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(soporteI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 20:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_rcci,rss[last],rcciI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(rcciI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 21:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_calisoft,rss[last],calisoftI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(calisoftI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case 22:
                if(what_ == 0){
                    showNewsDialog(R.drawable.ic_fenix,rss[last],fenixI.get(pos));
                }else{
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(400);
                                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                i.setData(Uri.parse(fenixI.get(pos).url));
                                startActivity(i);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }

                break;

            default:

                break;


        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

      if(event.values[0] == 0){

        if (!thread_running) {

            Boolean general_proximity;

            if (sharedPreferences.contains(CampusPreference.PROXIMITY_UPDATE)) {
                general_proximity = sharedPreferences.getBoolean(CampusPreference
                                .PROXIMITY_UPDATE,
                        false);
            } else {
                editor.putBoolean(CampusPreference.PROXIMITY_UPDATE, false);
                editor.commit();
                general_proximity = sharedPreferences.getBoolean(CampusPreference
                                .PROXIMITY_UPDATE,
                        false);
            }

            if (general_proximity) {
                thread_running = true;
                Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                        R.anim.fab_rotate);
                fab.startAnimation(animation_fab);
                fab.setClickable(false);
                if(which != 23) {

                    fab.setClickable(false);
                    refreshRss refreshrss = new refreshRss();
                    refreshrss.execute(which);
                }else{
                    refresAllhRss = new refresAllhRss();
                    refresAllhRss .execute(which);
                }
            }

        }

    }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class refreshRss extends AsyncTask<Integer, Integer, Boolean> {

        int value;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            _FakeX509TrustManager.allowAllSSL();

            value = params[0];
            switch (value){
                case 24:
                    //custom = new ArrayList<>();
                    customIaux = new ArrayList<>();
                    img_custom = true;
                    break;

                case 0:
                    //firefoxmania = new ArrayList<>();
                    firefoxmaniaI = new ArrayList<>();
                    img_firefox = true;
                    rss_source = RssDataBaseAttr.SOURCE_FIREFOX;
                    break;

                case 1:
                    //humanos = new ArrayList<>();
                    humanosI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_HUMANOS;
                    break;
                case 2:
                    //internos = new ArrayList<>();
                    internosI = new ArrayList<>();
                    img_internos = true;
                    rss_source = RssDataBaseAttr.SOURCE_INTERNOS;
                    break;
                case 3:
                    //dragones = new ArrayList<>();
                    dragonesI = new ArrayList<>();
                    img_dragones = true;
                    rss_source = RssDataBaseAttr.SOURCE_DRAGONES;
                    break;
                case 4:
                    //octavitos = new ArrayList<>();
                    octavitosI = new ArrayList<>();
                    img_octavitos = true;
                    rss_source = RssDataBaseAttr.SOURCE_OCTAVITOS;
                    break;
                case 5:
                    //uci = new ArrayList<>();
                    uciI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_UCI;
                    break;
                case 6:
                    //iblog = new ArrayList<>();
                    iblogI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_APPLE;
                    img_iblog = true;
                    break;
                case 7:
                    //seguridad = new ArrayList<>();
                    seguridadI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_SEGURIDAD;
                    img_seguridad = true;
                    break;

                case 8:
                    //androiduci = new ArrayList<>();
                    androiduciI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_ANDROID;
                    break;

                case 9:
                    //php = new ArrayList<>();
                    phpI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_PHP;
                    break;

                case 10:
                    //academia = new ArrayList<>();
                    academiaI = new ArrayList<>();
                    img_academia = true;
                    rss_source = RssDataBaseAttr.SOURCE_ACADEMIA;
                    break;

                case 11:
                    //gladiadores = new ArrayList<>();
                    gladiadoresI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_GLADIADORES;
                    img_gladiadores = true;
                    break;

                case 12:
                    //venus = new ArrayList<>();
                    venusI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_VENUS;
                    img_venus = true;
                    break;

                case 13:
                    //drupal = new ArrayList<>();
                    drupalI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_DRUPAL;
                    break;

                case 14:
                    //java = new ArrayList<>();
                    javaI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_JAVA;
                    img_java = true;
                    break;

                case 15:
                    //mella = new ArrayList<>();
                    mellaI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_MELLA;
                    break;

                case 16:
                    //mindpro = new ArrayList<>();
                    mindproI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_MINDPRO;
                    img_mindpro = true;
                    break;

                case 17:
                    //rice = new ArrayList<>();
                    riceI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_RICE;
                    img_rice = true;
                    break;

                case 18:
                    //blackhat = new ArrayList<>();
                    blackhatI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_BLACKHAT;
                    break;

                case 19:
                    //soporte = new ArrayList<>();
                    soporteI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_SOPORTE;
                    break;

                case 20:
                    //rcci = new ArrayList<>();
                    rcciI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_RCCI;
                    break;

                case 21:
                    //calisoft = new ArrayList<>();
                    calisoftI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_CALISOFT;
                    img_calisoft = true;
                    break;

                case 22:
                    //calisoft = new ArrayList<>();
                    fenixI = new ArrayList<>();
                    rss_source = RssDataBaseAttr.SOURCE_FENIX;
                    break;
            }

            URL rssUrl;

            try {

                rssUrl = new URL(urls[value]);
                InputStream is;
                URLConnection urlConnection = rssUrl.openConnection();
                urlConnection.setUseCaches(false);
                is = urlConnection.getInputStream();
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                dom = builder.parse(is);
                root = dom.getDocumentElement();
                items = root.getElementsByTagName("item");

                if(items.getLength() == 0){

                    items = root.getElementsByTagName("entry");

                }

                for (int i = 0; i < items.getLength(); i++) {
                    Item aux = new Item();
                    Node item = items.item(i);
                    NodeList itemChilds = item.getChildNodes();

                    for (int j = 0; j < itemChilds.getLength(); j++) {
                        Node dato = itemChilds.item(j);
                        String label = dato.getNodeName();

                        if (label.equals("title")) {
                            String texto = getTextFromNode(dato);
                            aux.title = texto;
                            //InsertOnStringList(value, texto);
                        } else if (label.equals("link")) {
                            String texto = getTextFromNode(dato);
                            aux.url = texto;
                        }else if (label.equals("description")){
                            String texto = getTextFromNode(dato);
                            aux.desc = texto;

                            if(img_custom){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        aux.img = mostrar;
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                            if(img_calisoft){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "https://calisoft.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                            if(img_rice){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "https://rice.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                            if(img_dragones){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "https://dragones.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                            if(img_octavitos){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "https://octavitos.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                            if(img_seguridad){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    if(!mostrar.contains("http")){
                                        aux.img = "http://seguridad.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);

                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                            if(img_internos){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "http://internos.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }

                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }

                            }

                        }else if (label.equals("content:encoded")){

                            if(img_custom){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        aux.img = mostrar;
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_mindpro){
                                String texto = getTextFromNode(dato);
                                aux.desc = texto;
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("'");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "http://mindpro.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_iblog){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "https://iblog.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_academia){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "http://academia.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_java){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "http://solucionesjava.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_gladiadores){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "http://dprog.facultad6.uci.cu" + mostrar;
                                    }else{
                                        aux.img = mostrar;
                                    }
                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_firefox){
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://firefoxmania.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                            if(img_venus){
                                try{
                                Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                        .indexOf("src=");
                                if(startdelimiterImage!=-1){
                                    String urlpart = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                    int enddelimiterImage = urlpart.indexOf("\"");
                                    String mostrar = dato.getFirstChild().getNodeValue().substring
                                            (startdelimiterImage+5,
                                                    startdelimiterImage+5+enddelimiterImage);
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                    if(!mostrar.contains("http")){
                                        aux.img = "https://venusit.uci.cu" + mostrar;
                                    }else{
                                        String mostrar1 = mostrar.replace("http","https");
                                        aux.img = mostrar1;
                                    }

                                }
                                }catch (Exception e){
                                    aux.img = null;
                                }
                            }

                        }else if(label.equals("pubDate")){
                            String texto = getTextFromNode(dato);
                            if(texto.contains("+")){
                                int from = texto.indexOf("+");
                                texto = texto.substring(0,from);
                            }
                            aux.pubDate = texto;
                        }else if(label.equals("author")){
                            String texto = getTextFromNode(dato);
                            aux.author = texto;
                        }else if(label.equals("dc:creator")){
                            String texto = getTextFromNode(dato);
                            aux.author = texto;
                        }else if(label.equals("slash:comments")){
                            String texto = getTextFromNode(dato);
                            aux.comments = texto;
                        }

                    }
                    InsertOnItemList(value, aux);
                }

            } catch (MalformedURLException e) {
                return false;
            } catch (ParserConfigurationException e) {
                return false;
            } catch (SAXException e) {
                return false;
            } catch (IOException e) {
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            Animation hide = AnimationUtils.loadAnimation(RssActivity.this, R.anim.fab_fade_out);
            iv_up.startAnimation(hide);
            lv_rss.setSelection(0);

            iv_up.setVisibility(View.INVISIBLE);

            fab.clearAnimation();
            Animation pop = AnimationUtils.loadAnimation(RssActivity.this, R.anim.fab_pop);
            fab.startAnimation(pop);
            fab.setClickable(true);

            img_seguridad = false;
            img_dragones = false;
            img_octavitos = false;
            img_mindpro = false;
            img_internos = false;
            img_iblog = false;
            img_academia = false;
            img_gladiadores = false;
            img_venus = false;
            img_java = false;
            img_firefox = false;
            img_rice = false;
            img_calisoft = false;
            img_custom = false;

            thread_running = false;

            String contentText = "";
            String contentText1 = "";
            int draw = 0;
            int draw_wrong = 0;

            boolean activate;
            if(sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)){
                activate = sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                        false);
            }else{
                activate = true;
            }

            if(aBoolean){

                showAndHideInitText();

                last = value;

                rssDataBase.openRssDatabase();

                switch (last){

                    case 24:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, firefoxmania);
                        customI = customIaux;
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                customI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_seguridad);
                        customRssDataBase.openCustomRssDatabase();
                        customRssDataBase.saveRss(customI, rss_custom);
                        customRssDataBase.closeCustomRssDatabase();
                        tv_source.setText(rss_custom);
                        iv_source.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_rss));
                        break;

                    case 0:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, firefoxmania);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                firefoxmaniaI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_firefoxmania);
                        rssDataBase.saveRss(firefoxmaniaI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 1:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, humanos);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                humanosI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_humanos);
                        rssDataBase.saveRss(humanosI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                    case 2:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, internos);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                internosI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_internos);
                        rssDataBase.saveRss(internosI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                    case 3:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, dragones);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                dragonesI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_dragones);
                        rssDataBase.saveRss(dragonesI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                    case 4:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, octavitos);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                octavitosI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_octavitos);
                        rssDataBase.saveRss(octavitosI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                    case 5:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, uci);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                               uciI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_uci);
                        rssDataBase.saveRss(uciI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                    case 6:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //       android.R.layout.simple_list_item_1, iblog);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                iblogI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_iblog);
                        rssDataBase.saveRss(iblogI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                    case 7:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                seguridadI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_seguridad);
                        rssDataBase.saveRss(seguridadI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 8:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, androiduci);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                androiduciI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_android);
                        rssDataBase.saveRss(androiduciI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 9:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, androiduci);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                phpI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_internos);
                        rssDataBase.saveRss(phpI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 10:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, androiduci);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                academiaI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_academia);
                        rssDataBase.saveRss(academiaI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 11:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, androiduci);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                gladiadoresI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_gladiadores);
                        rssDataBase.saveRss(gladiadoresI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 12:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                venusI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticias_layout_venus);
                        rssDataBase.saveRss(venusI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 13:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                drupalI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_drupal);
                        rssDataBase.saveRss(drupalI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 14:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                javaI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_java);
                        rssDataBase.saveRss(javaI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 15:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                mellaI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_drupal);
                        rssDataBase.saveRss(mellaI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 16:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                mindproI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_mindpro);
                        rssDataBase.saveRss(mindproI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 17:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                riceI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_iblog);
                        rssDataBase.saveRss(riceI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 18:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                blackhatI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_mindpro);
                        rssDataBase.saveRss(blackhatI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 19:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                soporteI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_internos);
                        rssDataBase.saveRss(soporteI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 20:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                rcciI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_seguridad);
                        rssDataBase.saveRss(rcciI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 21:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                calisoftI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_iblog);
                        rssDataBase.saveRss(calisoftI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;

                    case 22:
                        //aal = new ArrayAdapter<>(RssActivity.this,
                        //        android.R.layout.simple_list_item_1, seguridad);
                        aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                R.layout.noticia_layout, RssActivity.this,
                                fenixI, roboto_condense_light, roboto_condense_bold,
                                R.layout.noticia_layout_iblog);
                        rssDataBase.saveRss(fenixI, rss_source);
                        tv_source.setText(rss[last]);
                        iv_source.setImageDrawable(getResources().getDrawable(imgs[last]));
                        break;
                }

                rssDataBase.closeRssDatabase();

                lv_rss.setAdapter(aal);
                aal.notifyDataSetChanged();

                if(activate){
                    sendNotification(aBoolean);
                }else{
                    if(!isInFront){
                        switch (last){
                            case 24:
                                contentText = urls[24];
                                draw = R.drawable.ic_favorite_rss;
                                break;

                            //case 22:
                                //contentText = rss[rss.length-1];
                                //draw = R.drawable.ic_favorite_rss;
                                //break;

                            case 0:
                                contentText = "http://firefoxmania.uci.cu/feed/";
                                draw = R.drawable.firefox;
                                break;

                            case 1:
                                contentText = "http://humanos.uci.cu/feed/";
                                draw = R.drawable.humanos;
                                break;

                            case 2:
                                contentText = "http://internos.uci.cu/rss";
                                draw = R.drawable.internos;
                                break;

                            case 3:
                                contentText = "https://dragones.uci.cu/index.php/secciones?format=feed";
                                draw = R.drawable.dragon3s1;
                                break;

                            case 4:
                                contentText = "https://octavitos.uci.cu/?q=rss.xml";
                                draw = R.drawable.octavito;
                                break;

                            case 5:
                                contentText = "http://www.uci.cu/rss-uci";
                                draw = R.drawable.uci;
                                break;

                            case 6:
                                contentText = "https://iblog.uci.cu/feed/";
                                draw = R.drawable.apple;
                                break;

                            case 7:
                                contentText = "http://seguridad.uci.cu/?q=rss.xml";
                                draw = R.drawable.seguridad;
                                break;

                            case 8:
                                contentText = "http://android.uci.cu/feed/";
                                draw = R.drawable.android;
                                break;

                            case 9:
                                contentText = "https://php.uci.cu/infusions/rss_panel/rss/rss_n.php";
                                draw = R.drawable.ic_php;
                                break;

                            case 10:
                                contentText = "http://academia.uci.cu/?feed=rss2";
                                draw = R.drawable.ic_academia;
                                break;

                            case 11:
                                contentText = "https://dprog.facultad6.uci.cu/?feed=rss2";
                                draw = R.drawable.ic_gladiadores;
                                break;

                            case 12:
                                contentText = "https://venusit.uci.cu/feed/";
                                draw = R.drawable.ic_venus;
                                break;

                            case 13:
                                contentText = "http://drupaleros.uci.cu/?q=rss.xml";
                                draw = R.drawable.ic_drupal;
                                break;

                            case 14:
                                contentText = "http://solucionesjava.blog.uci.cu/feed/";
                                draw = R.drawable.ic_java;
                                break;

                            case 15:
                                contentText = "http://periodico.uci.cu/feed/";
                                draw = R.drawable.ic_mella;
                                break;

                            case 16:
                                contentText = "http://mindpro.uci.cu/servicios/feed";
                                draw = R.drawable.ic_mindpro;
                                break;

                            case 17:
                                contentText = "https://rice.uci.cu/?feed=rss2";
                                draw = R.drawable.ic_rice;
                                break;

                            case 18:
                                contentText = "https://blackhat.uci.cu/index.php/feed/";
                                draw = R.drawable.ic_blackhat;
                                break;

                            case 19:
                                contentText = "https://soporte.uci.cu/comunidades/index.php?qa=feed&qa_1=qa.rss";
                                draw = R.drawable.ic_soporte;
                                break;

                            case 20:
                                contentText = "http://rcci.uci.cu/index.php?journal=rcci&page=gateway&op=plugin&path[]=WebFeedGatewayPlugin&path[]=rss2";
                                draw = R.drawable.ic_rcci;
                                break;

                            case 21:
                                contentText = "https://calisoft.uci.cu/index.php?format=feed&type=rss";
                                draw = R.drawable.ic_calisoft;
                                break;

                            case 22:
                                contentText = "https://fenix.uci.cu/?feed=rss2";
                                draw = R.drawable.ic_fenix;
                                break;
                        }
                        sendSuccessToast(contentText, draw);
                    }
                }

            }else{
                last_wrong = value;
                if(activate){
                    sendNotification(aBoolean);
                }else{
                    switch (last_wrong){
                        case 24:
                            contentText1 = urls[24];
                            draw_wrong = R.drawable.ic_favorite_rss;
                            break;

                        case 0:
                            contentText1 = "http://firefoxmania.uci.cu/feed/";
                            draw_wrong = R.drawable.firefox;
                            break;

                        case 1:
                            contentText1 = "http://humanos.uci.cu/feed/";
                            draw_wrong = R.drawable.humanos;
                            break;

                        case 2:
                            contentText1 = "http://internos.uci.cu/rss";
                            draw_wrong = R.drawable.internos;
                            break;

                        case 3:
                            contentText1 = "https://dragones.uci.cu/index.php/secciones?format=feed";
                            draw_wrong = R.drawable.dragon3s1;
                            break;

                        case 4:
                            contentText1 = "https://octavitos.uci.cu/?q=rss.xml";
                            draw_wrong = R.drawable.octavito;
                            break;

                        case 5:
                            contentText1 = "http://www.uci.cu/rss-uci";
                            draw_wrong = R.drawable.uci;
                            break;

                        case 6:
                            contentText1 = "https://iblog.uci.cu/feed/";
                            draw_wrong = R.drawable.apple;
                            break;

                        case 7:
                            contentText1 = "http://seguridad.uci.cu/?q=rss.xml";
                            draw_wrong = R.drawable.seguridad;
                            break;

                        case 8:
                            contentText1 = "http://android.uci.cu/feed/";
                            draw_wrong = R.drawable.android;
                            break;

                        case 9:
                            contentText1 = "https://php.uci.cu/infusions/rss_panel/rss/rss_n.php";
                            draw_wrong = R.drawable.ic_php;
                            break;

                        case 10:
                            contentText1 = "http://academia.uci.cu/?feed=rss2";
                            draw_wrong = R.drawable.ic_academia;
                            break;

                        case 11:
                            contentText1 = "https://dprog.facultad6.uci.cu/?feed=rss2";
                            draw_wrong = R.drawable.ic_gladiadores;
                            break;

                        case 12:
                            contentText1 = "https://venusit.uci.cu/feed/";
                            draw_wrong = R.drawable.ic_venus;
                            break;

                        case 13:
                            contentText1 = "http://drupaleros.uci.cu/?q=rss.xml";
                            draw_wrong = R.drawable.ic_drupal;
                            break;

                        case 14:
                            contentText1 = "http://solucionesjava.blog.uci.cu/feed/";
                            draw_wrong = R.drawable.ic_java;
                            break;

                        case 15:
                            contentText1 = "http://periodico.uci.cu/feed/";
                            draw_wrong= R.drawable.ic_mella;
                            break;

                        case 16:
                            contentText1 = "http://mindpro.uci.cu/servicios/feed";
                            draw_wrong = R.drawable.ic_mindpro;
                            break;

                        case 17:
                            contentText1 = "https://rice.uci.cu/?feed=rss2";
                            draw_wrong = R.drawable.ic_rice;
                            break;

                        case 18:
                            contentText1 = "https://blackhat.uci.cu/index.php/feed/";
                            draw_wrong = R.drawable.ic_blackhat;
                            break;

                        case 19:
                            contentText1 = "https://soporte.uci.cu/comunidades/index.php?qa=feed&qa_1=qa.rss";
                            draw_wrong = R.drawable.ic_soporte;
                            break;

                        case 20:
                            contentText1 = "http://rcci.uci.cu/index.php?journal=rcci&page=gateway&op=plugin&path[]=WebFeedGatewayPlugin&path[]=rss2";
                            draw_wrong = R.drawable.ic_rcci;
                            break;

                        case 21:
                            contentText1 = "https://calisoft.uci.cu/index.php?format=feed&type=rss";
                            draw_wrong = R.drawable.ic_calisoft;
                            break;

                        case 22:
                            contentText1 = "https://fenix.uci.cu/?feed=rss2";
                            draw_wrong = R.drawable.ic_fenix;
                            break;
                    }
                    sendErrorToast(contentText1, draw_wrong);
                }
            }
            super.onPostExecute(aBoolean);
        }

        private void InsertOnStringList(int url, String texto) {
            // TODO Auto-generated method stub
            switch (url){

                case 24:
                    custom.add(texto);
                    break;

                case 0:
                    firefoxmania.add(texto);
                    break;

                case 1:
                    humanos.add(texto);
                    break;
                case 2:
                    internos.add(texto);
                    break;
                case 3:
                    dragones.add(texto);
                    break;
                case 4:
                    octavitos.add(texto);
                    break;
                case 5:
                    uci.add(texto);
                    break;
                case 6:
                    iblog.add(texto);
                    break;
                case 7:
                    seguridad.add(texto);
                    break;

                case 8:
                    androiduci.add(texto);
                    break;

                case 9:
                    php.add(texto);
                    break;

                case 10:
                    academia.add(texto);
                    break;

                case 11:
                    gladiadores.add(texto);
                    break;

                case 12:
                    venus.add(texto);
                    break;

                case 13:
                    drupal.add(texto);
                    break;

                case 14:
                    java.add(texto);
                    break;

                case 15:
                    mella.add(texto);
                    break;

                case 16:
                    mindpro.add(texto);
                    break;

                case 17:
                    rice.add(texto);
                    break;

                case 18:
                    blackhat.add(texto);
                    break;

                case 19:
                    soporte.add(texto);
                    break;

                case 20:
                    rcci.add(texto);
                    break;

                case 21:
                    calisoft.add(texto);
                    break;
            }
        }

        private void InsertOnItemList(int url, Item aux) {
            // TODO Auto-generated method stub
            switch (url){

                case 24:
                    customIaux.add(aux);
                    break;

                case 0:
                    firefoxmaniaI.add(aux);
                    break;

                case 1:
                    humanosI.add(aux);
                    break;
                case 2:
                    internosI.add(aux);
                    break;
                case 3:
                    dragonesI.add(aux);
                    break;
                case 4:
                    octavitosI.add(aux);
                    break;
                case 5:
                    uciI.add(aux);
                    break;
                case 6:
                    iblogI.add(aux);
                    break;
                case 7:
                    seguridadI.add(aux);
                    break;

                case 8:
                    androiduciI.add(aux);
                    break;

                case 9:
                    phpI.add(aux);
                    break;

                case 10:
                    academiaI.add(aux);
                    break;

                case 11:
                    gladiadoresI.add(aux);
                    break;

                case 12:
                    venusI.add(aux);
                    break;

                case 13:
                    drupalI.add(aux);
                    break;

                case 14:
                    javaI.add(aux);
                    break;

                case 15:
                    mellaI.add(aux);
                    break;

                case 16:
                    mindproI.add(aux);
                    break;

                case 17:
                    riceI.add(aux);
                    break;

                case 18:
                    blackhatI.add(aux);
                    break;

                case 19:
                    soporteI.add(aux);
                    break;

                case 20:
                    rcciI.add(aux);
                    break;

                case 21:
                    calisoftI.add(aux);
                    break;

                case 22:
                    fenixI.add(aux);
                    break;
            }
        }

        private String getTextFromNode(Node dato) {
            StringBuilder texto = new StringBuilder();
            NodeList fragmentos = dato.getChildNodes();

            for (int k = 0; k < fragmentos.getLength(); k++) {
                texto.append(fragmentos.item(k).getNodeValue());
            }

            return texto.toString();
        }
    }

    private boolean isInHome(){
            if(
                    (MenuActivity.isInFront==null || MenuActivity.isInFront==false) &&
                    (CuotaActivity.isInFront==null || CuotaActivity.isInFront==false) &&
                    (HorarioActivity.isInFront==null || HorarioActivity.isInFront==false)&&
                    (MapaActivity.isInFront==null || MapaActivity.isInFront==false) &&
                    (AboutActivity.isInFront==null || AboutActivity.isInFront==false)&&
                    (MainActivity.isInFront==null || MainActivity.isInFront==false) &&
                    (DirectorioActivity.isInFront==null || DirectorioActivity.isInFront==false) &&
                    (ChatActivity.isInFront==null || ChatActivity.isInFront==false) &&
                    (isInFront==null || isInFront==false)){

                return true;
            }

        return false;
    }


    private void sendErrorToast(String texto, int draw){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.rss_toast
                ,(ViewGroup) findViewById(R.id.rss_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_rss_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R
                .string.error_con_rss) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_rss_toast);
        i.setImageDrawable(getResources().getDrawable(draw));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendSuccessToast(String texto, int draw){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.rss_toast
                ,(ViewGroup) findViewById(R.id.rss_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_rss_toast);
        t.setTypeface(roboto_light);
        t.setText(t.getText().toString() + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_rss_toast);
        i.setImageDrawable(getResources().getDrawable(draw));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendNotification(Boolean aBoolean){
        String contentText = "";
        String contentText1 = "";
        int draw = 0;
        int draw_wrong = 0;

        switch (last){
            case 24:
                contentText = urls[24];
                draw = R.drawable.ic_favorite_rss;
                break;

            case 23:
                contentText = getResources().getString(R.string.all);
                draw = R.drawable.ic_rss_nav;
                break;

            case 0:
                contentText = "http://firefoxmania.uci.cu/feed/";
                draw = R.drawable.firefox;
                break;

            case 1:
                contentText = "http://humanos.uci.cu/feed/";
                draw = R.drawable.humanos;
                break;

            case 2:
                contentText = "http://internos.uci.cu/rss";
                draw = R.drawable.internos;
                break;

            case 3:
                contentText = "https://dragones.uci.cu/index.php/secciones?format=feed";
                draw = R.drawable.dragon3s1;
                break;

            case 4:
                contentText = "https://octavitos.uci.cu/?q=rss.xml";
                draw = R.drawable.octavito;
                break;

            case 5:
                contentText = "http://www.uci.cu/rss-uci";
                draw = R.drawable.uci;
                break;

            case 6:
                contentText = "https://iblog.uci.cu/feed/";
                draw = R.drawable.apple;
                break;

            case 7:
                contentText = "http://seguridad.uci.cu/?q=rss.xml";
                draw = R.drawable.seguridad;
                break;

            case 8:
                contentText = "http://android.uci.cu/feed/";
                draw = R.drawable.android;
                break;

            case 9:
                contentText = "https://php.uci.cu/infusions/rss_panel/rss/rss_n.php";
                draw = R.drawable.ic_php;
                break;

            case 10:
                contentText = "http://academia.uci.cu/?feed=rss2";
                draw = R.drawable.ic_academia;
                break;

            case 11:
                contentText = "https://dprog.facultad6.uci.cu/?feed=rss2";
                draw = R.drawable.ic_gladiadores;
                break;

            case 12:
                contentText = "https://venusit.uci.cu/feed/";
                draw = R.drawable.ic_venus;
                break;

            case 13:
                contentText = "http://drupaleros.uci.cu/?q=rss.xml";
                draw = R.drawable.ic_drupal;
                break;

            case 14:
                contentText = "http://solucionesjava.blog.uci.cu/feed/";
                draw = R.drawable.ic_java;
                break;

            case 15:
                contentText = "http://periodico.uci.cu/feed/";
                draw= R.drawable.ic_mella;
                break;

            case 16:
                contentText = "http://mindpro.uci.cu/servicios/feed";
                draw = R.drawable.ic_mindpro;
                break;

            case 17:
                contentText = "https://rice.uci.cu/?feed=rss2";
                draw = R.drawable.ic_rice;
                break;

            case 18:
                contentText = "https://blackhat.uci.cu/index.php/feed/";
                draw = R.drawable.ic_blackhat;
                break;

            case 19:
                contentText = "https://soporte.uci.cu/comunidades/index.php?qa=feed&qa_1=qa.rss";
                draw = R.drawable.ic_soporte;
                break;

            case 20:
                contentText = "http://rcci.uci.cu/index.php?journal=rcci&page=gateway&op=plugin&path[]=WebFeedGatewayPlugin&path[]=rss2";
                draw = R.drawable.ic_rcci;
                break;

            case 21:
                contentText = "https://calisoft.uci.cu/index.php?format=feed&type=rss";
                draw = R.drawable.ic_calisoft;
                break;

            case 22:
                contentText = "https://fenix.uci.cu/?feed=rss2";
                draw = R.drawable.ic_fenix;
                break;
        }

        switch (last_wrong){
            case 24:
                contentText1 = urls[24];
                draw_wrong = R.drawable.ic_favorite_rss;
                break;

            case 23:
                contentText1 = getResources().getString(R.string.all);
                draw_wrong = R.drawable.ic_rss_nav;
                break;

            case 0:
                contentText1 = "http://firefoxmania.uci.cu/feed/";
                draw_wrong = R.drawable.firefox;
                break;

            case 1:
                contentText1 = "http://humanos.uci.cu/feed/";
                draw_wrong = R.drawable.humanos;
                break;

            case 2:
                contentText1 = "http://internos.uci.cu/rss";
                draw_wrong = R.drawable.internos;
                break;

            case 3:
                contentText1 = "https://dragones.uci.cu/index.php/secciones?format=feed";
                draw_wrong = R.drawable.dragon3s1;
                break;

            case 4:
                contentText1 = "https://octavitos.uci.cu/?q=rss.xml";
                draw_wrong = R.drawable.octavito;
                break;

            case 5:
                contentText1 = "http://www.uci.cu/rss-uci";
                draw_wrong = R.drawable.uci;
                break;

            case 6:
                contentText1 = "https://iblog.uci.cu/feed/";
                draw_wrong = R.drawable.apple;
                break;

            case 7:
                contentText1 = "http://seguridad.uci.cu/?q=rss.xml";
                draw_wrong = R.drawable.seguridad;
                break;

            case 8:
                contentText1 = "http://android.uci.cu/feed/";
                draw_wrong = R.drawable.android;
                break;

            case 9:
                contentText1 = "https://php.uci.cu/infusions/rss_panel/rss/rss_n.php";
                draw_wrong = R.drawable.ic_php;
                break;

            case 10:
                contentText1 = "http://academia.uci.cu/?feed=rss2";
                draw_wrong = R.drawable.ic_academia;
                break;

            case 11:
                contentText1 = "https://dprog.facultad6.uci.cu/?feed=rss2";
                draw_wrong = R.drawable.ic_gladiadores;
                break;

            case 12:
                contentText1 = "https://venusit.uci.cu/feed/";
                draw_wrong = R.drawable.ic_venus;
                break;

            case 13:
                contentText1 = "http://drupaleros.uci.cu/?q=rss.xml";
                draw_wrong = R.drawable.ic_drupal;
                break;

            case 14:
                contentText1 = "http://solucionesjava.blog.uci.cu/feed/";
                draw_wrong = R.drawable.ic_java;
                break;

            case 15:
                contentText1 = "http://periodico.uci.cu/feed/";
                draw_wrong= R.drawable.ic_mella;
                break;

            case 16:
                contentText1 = "http://mindpro.uci.cu/servicios/feed";
                draw_wrong = R.drawable.ic_mindpro;
                break;

            case 17:
                contentText1 = "https://rice.uci.cu/?feed=rss2";
                draw_wrong = R.drawable.ic_rice;
                break;

            case 18:
                contentText1 = "https://blackhat.uci.cu/index.php/feed/";
                draw_wrong = R.drawable.ic_blackhat;
                break;

            case 19:
                contentText1 = "https://soporte.uci.cu/comunidades/index.php?qa=feed&qa_1=qa.rss";
                draw_wrong = R.drawable.ic_soporte;
                break;

            case 20:
                contentText1 = "http://rcci.uci.cu/index.php?journal=rcci&page=gateway&op=plugin&path[]=WebFeedGatewayPlugin&path[]=rss2";
                draw_wrong = R.drawable.ic_rcci;
                break;

            case 21:
                contentText1 = "https://calisoft.uci.cu/index.php?format=feed&type=rss";
                draw_wrong = R.drawable.ic_calisoft;
                break;

            case 22:
                contentText1 = "https://fenix.uci.cu/?feed=rss2";
                draw_wrong = R.drawable.ic_fenix;
                break;
        }

        if(aBoolean && isInHome()){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_rss);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(draw)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.news));
            notificationCompatBuilder.setContentText(contentText);
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .rss_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(RssActivity.this, RssActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    RssActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_RSS,notificationCompatBuilder.build());

        }else if(!aBoolean && isInHome()){

            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_rss);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(draw_wrong)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.rss_uci_title));

            notificationCompatBuilder.setContentText(getResources().getString(R.string
                    .error_con) + "\n" + contentText1);
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .rss_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(RssActivity.this, RssActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    RssActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_RSS,notificationCompatBuilder.build());
        }else{
            if(aBoolean && !isInFront){
                sendSuccessToast(contentText,draw);
            }else if(!aBoolean && !isInHome()){
                sendErrorToast(contentText1 ,draw_wrong);
            }
        }
    }

    private void setNotificationsDefaults(){
        boolean vibrate;
        boolean sound;
        boolean led;
        int defaults = 0;

        if(sharedPreferences.contains(CampusPreference.RSS_VIBRATE)){
            vibrate = sharedPreferences.getBoolean(CampusPreference.RSS_VIBRATE,
                    false);
        }else{
            vibrate = true;
        }

        if(sharedPreferences.contains(CampusPreference.RSS_SOUND)){
            sound = sharedPreferences.getBoolean(CampusPreference.RSS_SOUND,true);
        }else{
            sound = false;
        }

        if(sharedPreferences.contains(CampusPreference.RSS_LED)){
            led = sharedPreferences.getBoolean(CampusPreference.RSS_LED,true);
        }else{
            led = false;
        }

        if (led) {
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            notificationCompatBuilder.setLights(Color.BLUE, 3000, 3000);
        }
        if (sound) {
            defaults = defaults | Notification.DEFAULT_SOUND;
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationCompatBuilder.setSound(uri);
        }
        if (vibrate) {
            defaults = defaults | Notification.DEFAULT_VIBRATE;
        }

        notificationCompatBuilder.setDefaults(defaults);
    }

    private void showNewsDialog(int drawable, String source, Item item_) {

        final Item _item_ = item_;
        String texto = Jsoup.parse(_item_.desc).text();

        //if(_item_.desc.contains("&#8230;")){
        //    _item_.desc = _item_.desc.replace("&#8230;","...");
        //}
        //if(_item_.desc.contains("&#8220;")){
        //    _item_.desc = _item_.desc.replace("&#8220;","\"");
        //}
        //if(_item_.desc.contains("&#8221;")){
        //    _item_.desc = _item_.desc.replace("&#8221;","\"");
        //}

        //if(_item_.desc.contains("&#60;")){
        //    _item_.desc = _item_.desc.replace("&#60;","<");
        //}

        //if(_item_.desc.contains("&#62;")){
        //    _item_.desc = _item_.desc.replace("&#62;",">");
        //}
        final MaterialDialog dialog = new MaterialDialog.Builder(RssActivity.this)
                .title(source)
                .icon(getResources().getDrawable(drawable))
                .customView(R.layout.layout_noticia_descrip, true)
                .negativeText(R.string.close)
                .positiveText(R.string.go)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.setData(Uri.parse(_item_.url));
                        startActivity(i);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();
        dialog.show();

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.iv_rss);

        TextView tvUrl = (TextView) dialog.findViewById(R.id.tv_url);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvDate = (TextView) dialog.findViewById(R.id.tv_date);
        TextView tvDescription = (TextView)dialog.findViewById(R.id.tv_descrip);
        TextView tvAuthor = (TextView)dialog.findViewById(R.id.tv_author);
        TextView tvComments = (TextView)dialog.findViewById(R.id.tv_comments);

        ImageView ivComments = (ImageView)dialog.findViewById(R.id.iv_comments);

        tvUrl.setText(_item_.url);
        tvTitle.setText(_item_.title);
        tvDescription.setText(texto);

        tvUrl.setTypeface(roboto_condense_light);
        tvTitle.setTypeface(roboto_condense_bold);
        tvDate.setTypeface(roboto_condense_light);
        tvDescription.setTypeface(roboto_condense_light);
        tvAuthor.setTypeface(roboto_condense_bold);
        tvComments.setTypeface(roboto_condensed_light_italic);

        if(_item_.img != null && !_item_.img.equals(RssDataBaseAttr.NOT_IMAGE)){
            Picasso.with(RssActivity.this).load(_item_.img).into(imageView);
            imageView.setVisibility(View.VISIBLE);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation anim = AnimationUtils.loadAnimation(RssActivity.this, R.anim.tv_alpha);
                    Picasso.with(RssActivity.this).load(_item_.img).into(big);
                    dialog.dismiss();
                    big.setVisibility(View.VISIBLE);
                    dark.setVisibility(View.VISIBLE);
                    big.startAnimation(anim);
                    dark.startAnimation(anim);
                    big_in = true;
                }
            });
        }

        if(_item_.pubDate != null && !_item_.pubDate.equals(RssDataBaseAttr.NOT_PUBDATE)){
            String texto1 = _item_.pubDate;
            tvDate.setText(texto1);
            tvDate.setVisibility(View.VISIBLE);
        }

        if(_item_.author != null && !_item_.author.equals(RssDataBaseAttr.NOT_AUTHOR)){
            tvAuthor.setText(getResources().getString(R.string.by) + " " +_item_.author);
            tvAuthor.setVisibility(View.VISIBLE);
        }

        if(_item_.comments != null && !_item_.comments.equals(RssDataBaseAttr.NOT_COMMENTS)){
            tvComments.setText(_item_.comments);
            tvComments.setVisibility(View.VISIBLE);
            ivComments.setVisibility(View.VISIBLE);
        }
        Log.d("!!!!!!!!", _item_.desc);

    }

    private void showAllNewsDialog(int drawable, String source, AllItem item_) {

        final AllItem _item_ = item_;
        //String texto = Jsoup.parse(_item_.desc).text();

        //if(_item_.desc.contains("&#8230;")){
        //    _item_.desc = _item_.desc.replace("&#8230;","...");
        //}
        //if(_item_.desc.contains("&#8220;")){
        //    _item_.desc = _item_.desc.replace("&#8220;","\"");
        //}
        //if(_item_.desc.contains("&#8221;")){
        //    _item_.desc = _item_.desc.replace("&#8221;","\"");
        //}

        //if(_item_.desc.contains("&#60;")){
        //    _item_.desc = _item_.desc.replace("&#60;","<");
        //}

        //if(_item_.desc.contains("&#62;")){
        //    _item_.desc = _item_.desc.replace("&#62;",">");
        //}
        final MaterialDialog dialog = new MaterialDialog.Builder(RssActivity.this)
                .title(source)
                .icon(getResources().getDrawable(drawable))
                .customView(R.layout.layout_noticia_descrip, true)
                .negativeText(R.string.close)
                .positiveText(R.string.go)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.setData(Uri.parse(_item_.url));
                        startActivity(i);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();
        dialog.show();

        final ImageView imageView = (ImageView) dialog.findViewById(R.id.iv_rss);

        TextView tvUrl = (TextView) dialog.findViewById(R.id.tv_url);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvDate = (TextView) dialog.findViewById(R.id.tv_date);
        TextView tvDescription = (TextView)dialog.findViewById(R.id.tv_descrip);
        TextView tvAuthor = (TextView)dialog.findViewById(R.id.tv_author);
        TextView tvComments = (TextView)dialog.findViewById(R.id.tv_comments);

        ImageView ivComments = (ImageView)dialog.findViewById(R.id.iv_comments);

        tvUrl.setText(_item_.url);
        tvTitle.setText(_item_.title);
        //String texto = Jsoup.parse(_item_.desc).text();
        tvDescription.setText(_item_.desc);

        tvUrl.setTypeface(roboto_condense_light);
        tvTitle.setTypeface(roboto_condense_bold);
        tvDate.setTypeface(roboto_condense_light);
        tvDescription.setTypeface(roboto_condense_light);
        tvAuthor.setTypeface(roboto_condense_bold);
        tvComments.setTypeface(roboto_condensed_light_italic);

        if(_item_.img != null && !_item_.img.equals(RssDataBaseAttr.NOT_IMAGE)){
            Picasso.with(RssActivity.this).load(_item_.img).into(imageView);
            imageView.setVisibility(View.VISIBLE);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation anim = AnimationUtils.loadAnimation(RssActivity.this, R.anim.tv_alpha);
                    Picasso.with(RssActivity.this).load(_item_.img).into(big);
                    dialog.dismiss();
                    big.setVisibility(View.VISIBLE);
                    dark.setVisibility(View.VISIBLE);
                    big.startAnimation(anim);
                    dark.startAnimation(anim);
                    big_in = true;
                }
            });
        }

        if(_item_.pubDate != null && !_item_.pubDate.equals(RssDataBaseAttr.NOT_PUBDATE)){
            String texto1 = _item_.pubDate;
            tvDate.setText(texto1);
            tvDate.setVisibility(View.VISIBLE);
        }

        if(_item_.author != null && !_item_.author.equals(RssDataBaseAttr.NOT_AUTHOR)){
            tvAuthor.setText(getResources().getString(R.string.by) + " " +_item_.author);
            tvAuthor.setVisibility(View.VISIBLE);
        }

        if(_item_.comments != null && !_item_.comments.equals(RssDataBaseAttr.NOT_COMMENTS)){
            tvComments.setText(_item_.comments);
            tvComments.setVisibility(View.VISIBLE);
            ivComments.setVisibility(View.VISIBLE);
        }
        Log.d("!!!!!!!!", _item_.desc);

    }

    private void sendToast(String texto,int draw){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.rss_toast
                ,(ViewGroup) findViewById(R.id.rss_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_rss_toast);
        t.setTypeface(roboto_light);
        t.setText(texto + "\n" + getResources().getString(R.string.data_save_not));

        ImageView i = (ImageView) layout.findViewById(R.id.iv_rss_toast);
        i.setImageDrawable(getResources().getDrawable(draw));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void saveToArrayList(Item[] items, ArrayList<Item> rss_items){
        int count = items.length;
        for(int i = 0; i < count; i++){
            //items[i].img = RssDataBaseAttr.NOT_IMAGE;
            rss_items.add(items[i]);
        }
    }

    private void showPrefRssList() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.last_searchs)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                .adapter(new MyListAdapter2(this, R.array.rss, RssActivity.this,
                                datos_2,
                                imgs_2, roboto_light),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                                Item[] items_database = null;
                                rssDataBase.openRssDatabase();

                                switch (which) {
                                    case 0:
                                        rss_source = RssDataBaseAttr.SOURCE_FIREFOX;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //firefoxmaniaI = items_database;
                                            firefoxmaniaI = new ArrayList<Item>();
                                            saveToArrayList(items_database,firefoxmaniaI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    firefoxmaniaI, roboto_condense_light, roboto_condense_bold,
                                                    R.layout.noticia_layout_firefoxmania);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 0;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 1:
                                        rss_source = RssDataBaseAttr.SOURCE_HUMANOS;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //humanosI = items_database;
                                            humanosI = new ArrayList<Item>();
                                            saveToArrayList(items_database,humanosI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    humanosI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_humanos);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 1;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                    case 2:
                                        rss_source = RssDataBaseAttr.SOURCE_INTERNOS;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //internosI = items_database;
                                            internosI = new ArrayList<Item>();
                                            saveToArrayList(items_database,internosI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    internosI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_internos);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 2;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                    case 3:
                                        rss_source = RssDataBaseAttr.SOURCE_DRAGONES;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //dragonesI = items_database;
                                            dragonesI = new ArrayList<Item>();
                                            saveToArrayList(items_database,dragonesI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    dragonesI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_dragones);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 3;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                    case 4:
                                        rss_source = RssDataBaseAttr.SOURCE_OCTAVITOS;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //octavitosI = items_database;
                                            octavitosI = new ArrayList<Item>();
                                            saveToArrayList(items_database,octavitosI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    octavitosI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_octavitos);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 4;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                    case 5:
                                        rss_source = RssDataBaseAttr.SOURCE_UCI;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //uciI = items_database;
                                            uciI = new ArrayList<Item>();
                                            saveToArrayList(items_database,uciI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    uciI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_uci);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 5;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                    case 6:
                                        rss_source = RssDataBaseAttr.SOURCE_APPLE;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //iblogI = items_database;
                                            iblogI = new ArrayList<Item>();
                                            saveToArrayList(items_database,iblogI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    iblogI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_iblog);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 6;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                    case 7:
                                        rss_source = RssDataBaseAttr.SOURCE_SEGURIDAD;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //seguridadI = items_database;
                                            seguridadI = new ArrayList<Item>();
                                            saveToArrayList(items_database,seguridadI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    seguridadI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_seguridad);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 7;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 8:
                                        rss_source = RssDataBaseAttr.SOURCE_ANDROID;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //androiduciI = items_database;
                                            androiduciI = new ArrayList<Item>();
                                            saveToArrayList(items_database,androiduciI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    androiduciI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_android);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 8;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 9:
                                        rss_source = RssDataBaseAttr.SOURCE_PHP;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //phpI = items_database;
                                            phpI = new ArrayList<Item>();
                                            saveToArrayList(items_database,phpI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    phpI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_internos);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 9;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 10:
                                        rss_source = RssDataBaseAttr.SOURCE_ACADEMIA;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //academiaI = items_database;
                                            academiaI = new ArrayList<Item>();
                                            saveToArrayList(items_database,academiaI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    academiaI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_academia);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 10;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 11:
                                        rss_source = RssDataBaseAttr.SOURCE_GLADIADORES;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //gladiadoresI = items_database;
                                            gladiadoresI = new ArrayList<Item>();
                                            saveToArrayList(items_database,gladiadoresI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    gladiadoresI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_gladiadores);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 11;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 12:
                                        rss_source = RssDataBaseAttr.SOURCE_VENUS;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //venusI = items_database;
                                            venusI = new ArrayList<Item>();
                                            saveToArrayList(items_database,venusI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    venusI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticias_layout_venus);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 12;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 13:
                                        rss_source = RssDataBaseAttr.SOURCE_DRUPAL;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //drupalI = items_database;
                                            drupalI = new ArrayList<Item>();
                                            saveToArrayList(items_database,drupalI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    drupalI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_drupal);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 13;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 14:
                                        rss_source = RssDataBaseAttr.SOURCE_JAVA;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //javaI = items_database;
                                            javaI = new ArrayList<Item>();
                                            saveToArrayList(items_database,javaI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    javaI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_java);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 14;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 15:
                                        rss_source = RssDataBaseAttr.SOURCE_MELLA;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //mellaI = items_database;
                                            mellaI = new ArrayList<Item>();
                                            saveToArrayList(items_database,mellaI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    mellaI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_drupal);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 15;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 16:
                                        rss_source = RssDataBaseAttr.SOURCE_MINDPRO;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                           // mindproI = items_database;
                                            mindproI = new ArrayList<Item>();
                                            saveToArrayList(items_database,mindproI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    mindproI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_mindpro);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 16;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 17:
                                        rss_source = RssDataBaseAttr.SOURCE_RICE;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //riceI = items_database;
                                            riceI = new ArrayList<Item>();
                                            saveToArrayList(items_database,riceI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    riceI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_iblog);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 17;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 18:
                                        rss_source = RssDataBaseAttr.SOURCE_BLACKHAT;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //blackhatI = items_database;
                                            blackhatI = new ArrayList<Item>();
                                            saveToArrayList(items_database,blackhatI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    blackhatI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_mindpro);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 18;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 19:
                                        rss_source = RssDataBaseAttr.SOURCE_SOPORTE;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //soporteI = items_database;
                                            soporteI = new ArrayList<Item>();
                                            saveToArrayList(items_database,soporteI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    soporteI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_internos);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 19;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 20:
                                        rss_source = RssDataBaseAttr.SOURCE_RCCI;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //rcciI = items_database;
                                            rcciI = new ArrayList<Item>();
                                            saveToArrayList(items_database,rcciI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    rcciI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_seguridad);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 20;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 21:
                                        rss_source = RssDataBaseAttr.SOURCE_CALISOFT;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //calisoftI = items_database;
                                            calisoftI = new ArrayList<Item>();
                                            saveToArrayList(items_database,calisoftI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    calisoftI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_iblog);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 21;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;

                                    case 22:
                                        rss_source = RssDataBaseAttr.SOURCE_FENIX;
                                        items_database = rssDataBase.loadRss(rss_source);
                                        if (items_database.length == 0) {
                                            sendToast(rss[which], imgs[which]);
                                        } else {
                                            //calisoftI = items_database;
                                            fenixI = new ArrayList<Item>();
                                            saveToArrayList(items_database,fenixI);
                                            aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                                    R.layout.noticia_layout, RssActivity.this,
                                                    fenixI, roboto_condense_light,
                                                    roboto_condense_bold,
                                                    R.layout.noticia_layout_iblog);
                                            tv_source.setText(rss[which]);
                                            iv_source.setImageDrawable(getResources().getDrawable
                                                    (imgs[which]));
                                            showAndHideInitText();
                                            last = 22;
                                            lv_rss.setAdapter(aal);
                                            aal.notifyDataSetChanged();
                                        }
                                        break;
                                }

                                rssDataBase.closeRssDatabase();

                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private CheckBox check;

    private void showDeleteDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(RssActivity.this)
                .title(R.string.warning)
                .icon(getResources().getDrawable(R.drawable
                        .ic_dialog_sync_error))
                .content(R.string.delete_alerts_)
                .customView(R.layout.dialog_close, true)
                .positiveText(R.string.accept)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if(check.isChecked()){
                            editor.putBoolean(CampusPreference.DELETE_ALERTS,false);
                            editor.commit();
                        }
                        rssDataBase.openRssDatabase();
                        rssDataBase.cleanDatabase();
                        rssDataBase.closeRssDatabase();
                        Toast toast = Toast.makeText(RssActivity.this,
                                getResources().getString(R.string._delete_alerts_), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();

        TextView tv = (TextView) dialog.getCustomView().findViewById(R.id.user);
        tv.setText(getResources().getString(R.string.delete_alerts_));


        // Toggling the show password CheckBox will mask or unmask the password input EditText
        check = (CheckBox) dialog.getCustomView().findViewById(R.id.not_show);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        check.setChecked(false);
        dialog.show();
    }

    private EditText userInput;
    private EditText passwordInput;
    private View positiveAction;
    private boolean userText = false;
    private boolean passText = true;

    private void showNewRssDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.new_rss)
                .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                .customView(R.layout.dialog_new_rss, true)
                .positiveText(R.string.accept)
                .theme(Theme.LIGHT)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        customRssDataBase.openCustomRssDatabase();
                        int i = customRssDataBase.saveRss_(userInput.getText().toString(),
                                passwordInput.getText().toString());
                        customRssDataBase.closeCustomRssDatabase();
                        Toast toast;
                        if (i == 0) {
                            toast = Toast.makeText(RssActivity.this,
                                    RssActivity.this.getString(R.string
                                            .ya), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        } else {
                            toast = Toast.makeText(RssActivity.this,
                                    RssActivity.this.getString(R.string
                                            .ya_no), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                            if (!thread_running) {
                                thread_running = true;
                                Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                                        R.anim.fab_rotate);
                                fab.startAnimation(animation_fab);
                                fab.setClickable(false);
                                refreshrss = new refreshRss();
                                //RssActivity.this.which = 22;
                                rss_custom = userInput.getText().toString();
                                urls[24] = passwordInput.getText().toString();
                                refreshrss.execute(24);
                            }

                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }

                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        userInput = (EditText) dialog.getCustomView().findViewById(R.id.user);
        passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() > 0){
                    passText = true;
                }else{
                    passText = false;
                }

                positiveAction.setEnabled(passText && userText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    userText = true;
                } else {
                    userText = false;
                }

                positiveAction.setEnabled(passText && userText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void showCustomRssSaved(final CustomRss[] c_rss) {
        final CustomRss[] c_rss_ = c_rss;
        String[] rss_names;
        pos_ = 0;
        int count = c_rss_.length;
        if(count == 0){
            Toast toast = Toast.makeText(RssActivity.this,
                    RssActivity.this.getString(R.string
                            .no_rss), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }else{
            rss_names = new String[count];
            for(int i = 0; i < count; i++){
                rss_names[i] = c_rss_[i].source_;
            }
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.custom_rss)
                    .theme(Theme.LIGHT)
                    .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                    .items(rss_names)
                    .positiveText(R.string.go)
                    .itemsCallbackSingleChoice(pos_, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            pos_ = which;
                            if (!thread_running) {
                                thread_running = true;
                                Animation animation_fab = AnimationUtils.loadAnimation(RssActivity.this,
                                        R.anim.fab_rotate);
                                fab.startAnimation(animation_fab);
                                fab.setClickable(false);
                                refreshrss = new refreshRss();
                                //RssActivity.this.which = 22;
                                rss_custom = c_rss_[pos_].source_;
                                urls[24] = c_rss_[pos_].url_;
                                refreshrss.execute(24);
                                Log.d("!!!!!!!!!!!!!!", pos_ + "");
                            } else {
                                Toast toast = Toast.makeText(RssActivity.this,
                                        RssActivity.this.getString(R.string
                                                .mate), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.show();
                            }

                            return true;
                        }
                    })
                    .show();
        }

    }

    private void showCustomRssSavedDel(final CustomRss[] c_rss) {
        final CustomRss[] c_rss_ = c_rss;
        final String[] rss_names;
        pos_ = 0;
        int count = c_rss_.length;
        if(count == 0){
            Toast toast = Toast.makeText(RssActivity.this,
                    RssActivity.this.getString(R.string
                            .no_rss), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }else{
            rss_names = new String[count];
            for(int i = 0; i < count; i++){
                rss_names[i] = c_rss_[i].source_;
            }
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.custom_rss_del)
                    .theme(Theme.LIGHT)
                    .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                    .items(rss_names)
                    .positiveText(R.string.del)
                    .itemsCallbackSingleChoice(pos_, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            pos_ = which;
                            customRssDataBase.openCustomRssDatabase();
                            customRssDataBase.deleteCustomRss(rss_names[pos_]);
                            customRssDataBase.closeCustomRssDatabase();
                            Toast toast = Toast.makeText(RssActivity.this,
                                    RssActivity.this.getString(R.string
                                            .rss_del), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                            Log.d("!!!!!!!!!!!!!!", pos_ + "");
                            return true;
                        }
                    })
                    .show();
        }

    }

    private void showCustomRssRecent(final CustomRss[] c_rss) {
        final CustomRss[] c_rss_ = c_rss;
        final String[] rss_names;
        pos_ = 0;
        int count = c_rss_.length;
        if(count == 0){
            Toast toast = Toast.makeText(RssActivity.this,
                    RssActivity.this.getString(R.string
                            .no_rss), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }else{
            rss_names = new String[count];
            for(int i = 0; i < count; i++){
                rss_names[i] = c_rss_[i].source_;
            }
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.custom_rss)
                    .theme(Theme.LIGHT)
                    .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                    .items(rss_names)
                    .positiveText(R.string.go)
                    .itemsCallbackSingleChoice(pos_, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            pos_ = which;
                            rss_custom = rss_names[pos_];
                            customRssDataBase.openCustomRssDatabase();
                            item_custom_database = customRssDataBase.loadCustomRss(rss_custom);
                            customRssDataBase.closeCustomRssDatabase();

                            if (item_custom_database.length == 0) {
                                sendToast(rss_names[pos_], R.drawable.ic_favorite_rss);
                            } else {
                                customI = new ArrayList<Item>();
                                saveToArrayList(item_custom_database,customI);
                                aal = new MyListAdapter(RssActivity.this.getApplicationContext(),
                                        R.layout.noticia_layout, RssActivity.this,
                                        customI, roboto_condense_light, roboto_condense_bold,
                                        R.layout.noticia_layout_seguridad);
                                tv_source.setText(rss_custom);
                                iv_source.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_rss));
                                showAndHideInitText();
                                last = 24;
                                lv_rss.setAdapter(aal);
                                aal.notifyDataSetChanged();
                            }

                            return true;
                        }
                    })
                    .show();
        }

    }

    private class refresAllhRss extends AsyncTask<Integer, Integer, Boolean> {

        int value;
        String[] temporal_urls = new String[]{
                "http://firefoxmania.uci.cu/feed/",
                "http://humanos.uci.cu/feed/",
                "http://internos.uci.cu/rss",
                "https://dragones.uci.cu/index.php/secciones?format=feed",
                "https://octavitos.uci.cu/?q=rss.xml",
                "http://www.uci.cu/rss-uci",
                "https://iblog.uci.cu/feed/",
                "http://seguridad.uci.cu/?q=rss.xml",
                "http://android.uci.cu/feed/",
                "https://php.uci.cu/infusions/rss_panel/rss/rss_n.php",
                "http://academia.uci.cu/?feed=rss2",
                "https://dprog.facultad6.uci.cu/?feed=rss2",
                "https://venusit.uci.cu/feed/",
                "http://drupaleros.uci.cu/?q=rss.xml",
                "http://solucionesjava.blog.uci.cu/feed/",
                "http://periodico.uci.cu/feed/",
                "http://mindpro.uci.cu/servicios/feed",
                "https://rice.uci.cu/?feed=rss2",
                "https://blackhat.uci.cu/index.php/feed/",
                "https://soporte.uci.cu/comunidades/index.php?qa=feed&qa_1=qa.rss",
                "https://rcci.uci.cu/index.php?journal=rcci&page=gateway&op=plugin&path[]=WebFeedGatewayPlugin&path[]=rss2",
                "https://calisoft.uci.cu/index.php?format=feed&type=rss",
                "https://fenix.uci.cu/?feed=rss2"};
        @Override
        protected void onPreExecute() {

            Toast toast = Toast.makeText(RssActivity.this,
                    getResources().getString(R.string.load_schedule_), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            _FakeX509TrustManager.allowAllSSL();

            allItems = new ArrayList<>();
            ArrayList<Item> items_aux = new ArrayList<>();

            URL rssUrl;

            try {

                for(int p = 0; p < temporal_urls.length; p ++){

                    items_aux = new ArrayList<>();

                    switch (p){

                        case 0:;
                            img_firefox = true;
                            rss_source = RssDataBaseAttr.SOURCE_FIREFOX;
                            break;

                        case 1:
                            rss_source = RssDataBaseAttr.SOURCE_HUMANOS;
                            break;
                        case 2:
                            img_internos = true;
                            rss_source = RssDataBaseAttr.SOURCE_INTERNOS;
                            break;
                        case 3:
                            img_dragones = true;
                            rss_source = RssDataBaseAttr.SOURCE_DRAGONES;
                            break;
                        case 4:
                            img_octavitos = true;
                            rss_source = RssDataBaseAttr.SOURCE_OCTAVITOS;
                            break;
                        case 5:
                            uciI = new ArrayList<>();
                            rss_source = RssDataBaseAttr.SOURCE_UCI;
                            break;
                        case 6:
                            rss_source = RssDataBaseAttr.SOURCE_APPLE;
                            img_iblog = true;
                            break;
                        case 7:
                            rss_source = RssDataBaseAttr.SOURCE_SEGURIDAD;
                            img_seguridad = true;
                            break;

                        case 8:
                            rss_source = RssDataBaseAttr.SOURCE_ANDROID;
                            break;

                        case 9:
                            rss_source = RssDataBaseAttr.SOURCE_PHP;
                            break;

                        case 10:
                            img_academia = true;
                            rss_source = RssDataBaseAttr.SOURCE_ACADEMIA;
                            break;

                        case 11:
                            rss_source = RssDataBaseAttr.SOURCE_GLADIADORES;
                            img_gladiadores = true;
                            break;

                        case 12:
                            rss_source = RssDataBaseAttr.SOURCE_VENUS;
                            img_venus = true;
                            break;

                        case 13:
                            rss_source = RssDataBaseAttr.SOURCE_DRUPAL;
                            break;

                        case 14:
                            rss_source = RssDataBaseAttr.SOURCE_JAVA;
                            img_java = true;
                            break;

                        case 15:
                            rss_source = RssDataBaseAttr.SOURCE_MELLA;
                            break;

                        case 16:
                            rss_source = RssDataBaseAttr.SOURCE_MINDPRO;
                            img_mindpro = true;
                            break;

                        case 17:
                            rss_source = RssDataBaseAttr.SOURCE_RICE;
                            img_rice = true;
                            break;

                        case 18:
                            rss_source = RssDataBaseAttr.SOURCE_BLACKHAT;
                            break;

                        case 19:
                            rss_source = RssDataBaseAttr.SOURCE_SOPORTE;
                            break;

                        case 20:
                            rss_source = RssDataBaseAttr.SOURCE_RCCI;
                            break;

                        case 21:
                            rss_source = RssDataBaseAttr.SOURCE_CALISOFT;
                            img_calisoft = true;
                            break;

                        case 22:
                            rss_source = RssDataBaseAttr.SOURCE_FENIX;
                            break;

                    }

                rssUrl = new URL(temporal_urls[p]);
                InputStream is;
                URLConnection urlConnection = rssUrl.openConnection();
                urlConnection.setReadTimeout(180000);
                urlConnection.setUseCaches(false);
                is = urlConnection.getInputStream();
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                dom = builder.parse(is);
                root = dom.getDocumentElement();
                items = root.getElementsByTagName("item");

                for (int i = 0; i < items.getLength(); i++) {
                    AllItem aux = new AllItem();
                    Item aux_1 = new Item();
                    Node item = items.item(i);
                    NodeList itemChilds = item.getChildNodes();

                    aux.img_drawable = imgs[p];
                    aux.source = rss[p];

                    for (int j = 0; j < itemChilds.getLength(); j++) {
                        Node dato = itemChilds.item(j);
                        String label = dato.getNodeName();

                        if (label.equals("title")) {
                            String texto = getTextFromNode(dato);
                            aux.title = texto;
                            aux_1.title = texto;
                            //InsertOnStringList(value, texto);
                        } else if (label.equals("link")) {
                            String texto = getTextFromNode(dato);
                            aux.url = texto;
                            aux_1.url = texto;
                        } else if (label.equals("description")) {
                            String texto = getTextFromNode(dato);
                            aux.desc = texto;
                            aux_1.desc = texto;
                            if (img_custom) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        aux.img = mostrar;
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                            if (img_calisoft) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "https://calisoft.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                            if (img_rice) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "https://rice.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                            if (img_dragones) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "https://dragones.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                            if (img_octavitos) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "https://octavitos.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                            if (img_seguridad) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://seguridad.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);

                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                            if (img_internos) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://internos.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }

                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }

                            }

                        } else if (label.equals("content:encoded")) {

                            if (img_custom) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        aux.img = mostrar;
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_mindpro) {
                                String texto = getTextFromNode(dato);
                                aux.desc = texto;
                                aux_1.desc = texto;
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("'");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://mindpro.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_iblog) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "https://iblog.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_academia) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://academia.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_java) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://solucionesjava.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_gladiadores) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://dprog.facultad6.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_firefox) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "http://firefoxmania.uci.cu" + mostrar;
                                        } else {
                                            aux.img = mostrar;
                                        }
                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                            if (img_venus) {
                                try {
                                    Log.d("!!!!!!!!!!!!!!!!!!!", "Hola0: Empieza");
                                    int startdelimiterImage = dato.getFirstChild().getNodeValue()
                                            .indexOf("src=");
                                    if (startdelimiterImage != -1) {
                                        String urlpart = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola1: " + urlpart);
                                        int enddelimiterImage = urlpart.indexOf("\"");
                                        String mostrar = dato.getFirstChild().getNodeValue().substring
                                                (startdelimiterImage + 5,
                                                        startdelimiterImage + 5 + enddelimiterImage);
                                        Log.d("!!!!!!!!!!!!!!!!!!!", "Hola2: " + mostrar);
                                        if (!mostrar.contains("http")) {
                                            aux.img = "https://venusit.uci.cu" + mostrar;
                                        } else {
                                            String mostrar1 = mostrar.replace("http", "https");
                                            aux.img = mostrar1;
                                        }

                                    }
                                } catch (Exception e) {
                                    aux.img = null;
                                }
                            }

                        } else if (label.equals("pubDate")) {
                            String texto = getTextFromNode(dato);
                            if (texto.contains("+")) {
                                int from = texto.indexOf("+");
                                texto = texto.substring(0, from);
                            }
                            aux.pubDate = texto;
                            aux_1.pubDate = texto;
                        } else if (label.equals("author")) {
                            String texto = getTextFromNode(dato);
                            aux.author = texto;
                            aux_1.author = texto;
                        } else if (label.equals("dc:creator")) {
                            String texto = getTextFromNode(dato);
                            aux.author = texto;
                            aux_1.author = texto;
                        } else if (label.equals("slash:comments")) {
                            String texto = getTextFromNode(dato);
                            aux.comments = texto;
                            aux_1.comments = texto;
                        }

                    }
                    items_aux.add(aux_1);
                    InsertOnItemList(aux);
                }

                    rssDataBase.openRssDatabase();
                    rssDataBase.saveRss(items_aux, rss_source);
                    rssDataBase.closeRssDatabase();

            }

            } catch (MalformedURLException e) {
                //return false;
            } catch (ParserConfigurationException e) {
                //return false;
            } catch (SAXException e) {
                //return false;
            } catch (IOException e) {
                //return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            Animation hide = AnimationUtils.loadAnimation(RssActivity.this, R.anim.fab_fade_out);
            iv_up.startAnimation(hide);
            lv_rss.setSelection(0);

            iv_up.setVisibility(View.INVISIBLE);

            fab.clearAnimation();
            Animation pop = AnimationUtils.loadAnimation(RssActivity.this, R.anim.fab_pop);
            fab.startAnimation(pop);
            fab.setClickable(true);

            img_seguridad = false;
            img_dragones = false;
            img_octavitos = false;
            img_mindpro = false;
            img_internos = false;
            img_iblog = false;
            img_academia = false;
            img_gladiadores = false;
            img_venus = false;
            img_java = false;
            img_firefox = false;
            img_rice = false;
            img_calisoft = false;
            img_custom = false;

            thread_running = false;

            String contentText = "";
            String contentText1 = "";
            int draw = 0;
            int draw_wrong = 0;

            boolean activate;
            if(sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)){
                activate = sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                        false);
            }else{
                activate = true;
            }

            contentText = getResources().getString(R.string.all);
            draw = R.drawable.ic_rss_nav;

            if(allItems.size() > 0){

                last = 23;

                showAndHideInitText();

                rssDataBase.openRssDatabase();

                customI = customIaux;
                myAllListAdapter = new MyAllListAdapter(RssActivity.this.getApplicationContext(),
                        R.layout.noticias_todas, RssActivity.this,
                        allItems, roboto_condense_light, roboto_condense_bold,
                        R.layout.noticias_todas);
                tv_source.setText(getResources().getString(R.string.all));
                iv_source.setImageDrawable(getResources().getDrawable(R.drawable.ic_rss_nav));

                lv_rss.setAdapter(myAllListAdapter);
                myAllListAdapter.notifyDataSetChanged();

                if(activate){
                    sendNotification(aBoolean);
                }else{
                    if(!isInFront){

                                sendSuccessToast(contentText, draw);
                    }
                }

            }else{
                last_wrong = 23;
                if(activate){
                    sendNotification(aBoolean);
                }else{
                            sendErrorToast(contentText, draw);
                }
            }
            super.onPostExecute(aBoolean);
        }

        private void InsertOnItemList(AllItem aux) {
            // TODO Auto-generated method stub
            allItems.add(aux);
        }

        private String getTextFromNode(Node dato) {
            StringBuilder texto = new StringBuilder();
            NodeList fragmentos = dato.getChildNodes();

            for (int k = 0; k < fragmentos.getLength(); k++) {
                texto.append(fragmentos.item(k).getNodeValue());
            }

            return texto.toString();
        }
    }


}
