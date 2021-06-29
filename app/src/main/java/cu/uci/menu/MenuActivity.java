package cu.uci.menu;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import cu.uci.rss.RssActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.FloatingButtonLib.FloatingActionButton;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;
import cu.uci.utils.SSL._FakeX509TrustManager;

public class MenuActivity extends ActionBarActivity implements SensorEventListener{

    public static Activity ctx;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SensorManager sm;
    Sensor sensor;
    Boolean thread_running = false;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    public static NavigationDrawer drawer;
    public static Boolean notif;
    public static int notifType;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    int menu_selected_value;

    final static int dragones = 1111;
    final static int octavitos = 1211;
    final static int zorros = 1311;
    final static int fac5 = 1411;
    final static int gladiadores = 1511;
    final static int none = 0000;

    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_light;
    TextView tvAlmuerzo1, tvAlmuerzo, tvComida1, tvComida;
    CardView cv_lunch, cv_dinner;
    Toolbar mToolbar;
    ActionButton fab, fab_2, fab_oct, fab_drag, fab_zorros, fab_fac5;

    Boolean areIn;
    Boolean drawerIn;
    public static Boolean isInFront;

    View shadow;

    Boolean first_time = true;

    private int which = dragones;
    private int last_correct = none;
    private int last_wrong = none;
    private ShareActionProvider mShareActionProvider;
    private MenuDataBase menuDataBase;
    private FrameLayout fl_loading;

    public static LoginUser me;
    public static Bundle bundle;
    UpdateMenu updateMenu;

    FramboyanPlatosRefresh framboyanPlatosRefresh;
    FramboyanAdapter framboyanAdapter;
    ArrayList<Plato> platos;

    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    org.w3c.dom.Document dom;
    org.w3c.dom.Element root;
    NodeList items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ctx = this;

        initToolBar_FAB();
        initTextView();
        initOthers();
        //initBundle();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            entry();
        }

        setStatusBarColor();
    }

    public void setStatusBarColor(){
        LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build
                .VERSION_CODES.LOLLIPOP) {
            //LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
            //lx.getLayoutParams().height = getStatusBarHeight();
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            //int actionBarHeight = getActionBarHeight();
            //int statusBarHeight = getStatusBarHeight();
            //action bar height
            //statusBar.getLayoutParams().height = mToolbar.getHeight();
            //statusBar.setBackgroundColor(color);
            fab.setVisibility(View.GONE);
            fab_2.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.VISIBLE);
            fab_2.setVisibility(View.GONE);
            lx.setVisibility(View.GONE);
        }
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
    protected void onResume() {
        isInFront = true;
        drawerLayout.invalidate();
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(sensor != null){
            sm.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        notif = false;
        notifType = 0;
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_MENU);
        super.onStart();
    }

    @Override
    protected void onRestart() {
        //drawer.nav_rss_stat.setVisibility(View.INVISIBLE);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        updateMenu.cancel(true);
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_MENU);
        super.onDestroy();
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

    private void initToolBar_FAB() {

        notif = false;
        notifType = 0;
        isInFront = true;
        drawerIn = false;
        isInFront = true;

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        updateMenu = new UpdateMenu(this);

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        notificationCompatBuilder = new NotificationCompat.Builder(MenuActivity.this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        shadow = findViewById(R.id.shadow_toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            shadow.setMinimumHeight(4);
        }

        areIn = false;

        am = getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_condense_light = Typeface.createFromAsset(am, "roboto_condense_light.ttf");
        roboto_light_italic = Typeface.createFromAsset(am, "roboto_light_italic.ttf");

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_menu);

        if (sharedPreferences.contains(CampusPreference.MENU_PREF)) {
            menu_selected_value = sharedPreferences.getInt(CampusPreference.MENU_PREF, 0);
        } else {
            editor.putInt(CampusPreference.MENU_PREF, 0);
            editor.commit();
            menu_selected_value = sharedPreferences.getInt(CampusPreference.MENU_PREF,
                    0);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        //MenuItem shareItem = mToolbar.getMenu().findItem(R.id.action_share);
        //mShareActionProvider = (ShareActionProvider)
        //        MenuItemCompat.getActionProvider(shareItem);

        fab_2 = (ActionButton) findViewById(R.id.fab_loading_2);
        fab_2.setType(ActionButton.Type.DEFAULT);
        fab_2.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab_2.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab_2.setImageResource(R.drawable.ic_fa_refresh);
        fab_2.setShadowRadius(4.4f);
        fab_2.setShadowXOffset(2.8f);
        fab_2.setShadowYOffset(2.1f);
        fab_2.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab_2.setStrokeWidth(0.0f);

        fab_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areIn){
                    thread_running = true;
                    fab_2.setClickable(false);
                    Animation animation = AnimationUtils.loadAnimation(MenuActivity.this,
                            R.anim.fab_rotate);
                    fab_2.startAnimation(animation);
                    update(which);
                }else{
                    entry2();
                }
            }
        });

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
                if(!areIn){
                    thread_running = true;
                    fab.setClickable(false);
                    Animation animation = AnimationUtils.loadAnimation(MenuActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation);
                    update(which);
                }else{
                    entry2();
                }
            }
        });

        Menu menu = mToolbar.getMenu();
        Log.d("!!!!!!!!!!!!!!!",menu.findItem(R.id.action_search_in).getTitle().toString());

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

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){

                    case R.id.action_search_in:

                        if(first_time) {

                            switch (menu_selected_value) {

                                case 0:
                                    menuItem.getSubMenu().findItem(R.id.action_dragones)
                                            .setChecked(true);
                                    which = dragones;
                                    first_time = false;
                                    break;

                                case 1:
                                    menuItem.getSubMenu().findItem(R.id.action_octavitos)
                                            .setChecked(true);
                                    which = octavitos;
                                    first_time = false;
                                    break;

                                case 2:
                                    menuItem.getSubMenu().findItem(R.id.action_zorros)
                                            .setChecked(true);
                                    which = zorros;
                                    first_time = false;
                                    break;

                                case 3:
                                    menuItem.getSubMenu().findItem(R.id.action_facultad5)
                                            .setChecked(true);
                                    which = fac5;
                                    first_time = false;
                                    break;
                            }

                        }
                        break;

                    case R.id.action_dragones:
                        menuItem.setChecked(true);
                        which = dragones;
                        Toast toast = Toast.makeText(MenuActivity.this, MenuActivity.this.getString(R.string
                                .search_drag),Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                        break;

                    case R.id.action_octavitos:
                        menuItem.setChecked(true);
                        which = octavitos;
                        Toast toast1 =  Toast.makeText(MenuActivity.this,
                                MenuActivity.this.getString(R.string
                                .search_octv),Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.BOTTOM, 0, 0);
                        toast1.show();
                        break;

                    case R.id.action_zorros:
                        menuItem.setChecked(true);
                        which = zorros;
                        Toast toast2 = Toast.makeText(MenuActivity.this,
                                MenuActivity.this.getString(R.string
                                .search_zorros),Toast.LENGTH_SHORT);
                        toast2.setGravity(Gravity.BOTTOM, 0, 0);
                        toast2.show();
                        break;

                    case R.id.action_facultad5:
                        menuItem.setChecked(true);
                        which = fac5;
                        Toast toast3 = Toast.makeText(MenuActivity.this,
                                MenuActivity.this.getString(R.string
                                .search_fac5),Toast.LENGTH_SHORT);
                        toast3.setGravity(Gravity.BOTTOM, 0, 0);
                        toast3.show();
                        break;

                    //case R.id.action_gladiadores:
                    //    menuItem.setChecked(true);
                    //    which = gladiadores;
                    //    Toast toast4 = Toast.makeText(MenuActivity.this,
                    //            MenuActivity.this.getString(R.string
                    //                    .search_glad),Toast.LENGTH_SHORT);
                    //    toast4.setGravity(Gravity.BOTTOM, 0, 0);
                    //    toast4.show();
                    //    break;

                    //case R.id.action_share:
                      //  mShareActionProvider.setShareIntent(getDefaultIntent());
                        //break;

                    case R.id.action_last_search:
                        String[] info = new String[2];
                        menuDataBase.openMenuDatabase();
                        info = menuDataBase.loadLastMenu();
                        if(info[0].toString().equals("") || info[1].toString().equals("")){
                            Toast toast5 = Toast.makeText(MenuActivity.this,
                                    getResources().getString(R.string
                                    .data_save_not), Toast.LENGTH_SHORT);
                            toast5.setGravity(Gravity.BOTTOM, 0, 0);
                            toast5.show();
                            menuDataBase.closeMenuDatabase();
                        }else{
                            tvAlmuerzo.setText(info[0]);
                            tvComida.setText(info[1]);

                            Animation animation_menu = AnimationUtils.loadAnimation(MenuActivity.this,R.anim.tv_alpha);
                            tvAlmuerzo.startAnimation(animation_menu);
                            tvComida.startAnimation(animation_menu);
                            menuDataBase.closeMenuDatabase();
                        }
                        break;

                    case R.id.action_clean:
                        tvAlmuerzo.setText("");
                        tvComida.setText("");
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
                            menuDataBase.openMenuDatabase();
                            menuDataBase.cleanDatabase();
                            menuDataBase.closeMenuDatabase();
                            Toast toast7 = Toast.makeText(MenuActivity.this,
                                    getResources().getString(R.string._delete_alerts_), Toast.LENGTH_SHORT);
                            toast7.setGravity(Gravity.BOTTOM, 0, 0);
                            toast7.show();
                        }
                        break;

                    //case R.id.action_framboyan:
                    //    framboyanPlatosRefresh = new FramboyanPlatosRefresh();
                     //   framboyanPlatosRefresh.execute();
                     //   Log.d("!!!!!!!!!!!!", "Comienzaaaaaaaaa");
                     //   //showFramboyanList();
                     //   break;

                }
                return true;
            }
        });

        if(menu_selected_value == 0){
            mToolbar.getMenu().findItem(R.id.action_dragones).setChecked(true);
        }else if(menu_selected_value == 1){
            mToolbar.getMenu().findItem(R.id.action_octavitos).setChecked(true);
        }else if(menu_selected_value == 2){
            mToolbar.getMenu().findItem(R.id.action_zorros).setChecked(true);
        }else if(menu_selected_value == 3){
            mToolbar.getMenu().findItem(R.id.action_facultad5).setChecked(true);
        }

        boolean load;

        if (sharedPreferences.contains(CampusPreference.MENU_LOAD)) {
            load = sharedPreferences.getBoolean(CampusPreference.MENU_LOAD, true);
        } else {
            editor.putBoolean(CampusPreference.MENU_LOAD, false);
            editor.commit();
            load = sharedPreferences.getBoolean(CampusPreference.MENU_LOAD,
                    false);
        }

        if(load){
            fab.setClickable(false);
            Animation animation = AnimationUtils.loadAnimation(MenuActivity.this,
                    R.anim.fab_rotate);
            fab.startAnimation(animation);
            update(which);
        }

    }


    private void initTextView(){

        tvComida1 = (TextView) findViewById(R.id.tvComida1);
        tvComida1.setTypeface(roboto_condense_light);
        tvComida1.setText(getResources().getString(R.string.dinner));
        tvComida = (TextView) findViewById(R.id.tvComida);
        tvComida.setTypeface(roboto_light_italic);

        tvAlmuerzo1 = (TextView) findViewById(R.id.tvAlmuerzo1);
        tvAlmuerzo1.setTypeface(roboto_condense_light);
        tvAlmuerzo1.setText(getResources().getString(R.string.lunch));
        tvAlmuerzo = (TextView) findViewById(R.id.tvAlmuerzo);
        tvAlmuerzo.setTypeface(roboto_light_italic);

        cv_lunch = (CardView) findViewById(R.id.cv_lunch);
        cv_dinner = (CardView) findViewById(R.id.cv_dinner);

    }

    private void initOthers(){

        menuDataBase = new MenuDataBase(this);
        fl_loading = (FrameLayout) findViewById(R.id.fl_loading);
    }

    private void entry(){

        Animation animation_cv_lunch = AnimationUtils.loadAnimation(this, R.anim.cv_lunch_entry);
        Animation animation_cv_dinner = AnimationUtils.loadAnimation(this, R.anim.cv_dinner_entry);
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        mToolbar.startAnimation(animation_toolbar_in);
        shadow.startAnimation(animation_shadow_in);
        fab.startAnimation(animation_fab);
        cv_lunch.startAnimation(animation_cv_lunch);
        cv_dinner.startAnimation(animation_cv_dinner);

    }

    private void entry2(){

        mToolbar.setVisibility(View.VISIBLE);
        cv_lunch.setVisibility(View.VISIBLE);
        cv_dinner.setVisibility(View.VISIBLE);

        Animation animation_cv_lunch = AnimationUtils.loadAnimation(this, R.anim.cv_lunch_entry);
        Animation animation_fabs = AnimationUtils.loadAnimation(this, R.anim.fab_menu_out);
        Animation animation_cv_dinner = AnimationUtils.loadAnimation(this, R.anim.cv_dinner_entry);
        Animation animation_fl = AnimationUtils.loadAnimation(MenuActivity
                .this, R.anim.fl_fade_out);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(MenuActivity
                .this, R.anim.toolbar_in);

        mToolbar.startAnimation(animation_toolbar_in);
        cv_lunch.startAnimation(animation_cv_lunch);
        cv_dinner.startAnimation(animation_cv_dinner);
        fab_drag.startAnimation(animation_fabs);
        fab_zorros.startAnimation(animation_fabs);
        fab_fac5.startAnimation(animation_fabs);
        fab_oct.startAnimation(animation_fabs);

        fab_zorros.setVisibility(View.INVISIBLE);
        fab_oct.setVisibility(View.INVISIBLE);
        fab_fac5.setVisibility(View.INVISIBLE);
        fab_drag.setVisibility(View.INVISIBLE);

        fl_loading.startAnimation(animation_fl);

        fl_loading.setBackgroundColor(Color.TRANSPARENT);

        areIn = false;
    }

    private void exit(){

        fl_loading.setBackgroundColor(getResources().getColor(R.color.app_black_transparent));

        Animation animation_cv_lunch = AnimationUtils.loadAnimation(this, R.anim.cv_lunch_exit);
        Animation animation_fabs = AnimationUtils.loadAnimation(this, R.anim.fab_menu_in);
        Animation animation_cv_dinner = AnimationUtils.loadAnimation(this, R.anim.cv_dinner_exit);
        Animation animation_fl = AnimationUtils.loadAnimation(MenuActivity
                .this, R.anim.fl_fade_in);
        Animation animation_toolbar_out = AnimationUtils.loadAnimation(MenuActivity
                .this, R.anim.toolbar_out);

        mToolbar.startAnimation(animation_toolbar_out);
        cv_lunch.startAnimation(animation_cv_lunch);
        cv_dinner.startAnimation(animation_cv_dinner);
        fab_drag.startAnimation(animation_fabs);
        fab_zorros.startAnimation(animation_fabs);
        fab_fac5.startAnimation(animation_fabs);
        fab_oct.startAnimation(animation_fabs);

        mToolbar.setVisibility(View.INVISIBLE);
        cv_lunch.setVisibility(View.INVISIBLE);
        cv_dinner.setVisibility(View.INVISIBLE);

        fab_zorros.setVisibility(View.VISIBLE);
        fab_oct.setVisibility(View.VISIBLE);
        fab_fac5.setVisibility(View.VISIBLE);
        fab_drag.setVisibility(View.VISIBLE);
        fl_loading.startAnimation(animation_fl);

        areIn = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
    public void onBackPressed() {
       //if(areIn){
       //    entry2();
       //}else{
       //    super.onBackPressed();
       //}
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else{
            Intent i1 = new Intent(MenuActivity.this,
                    MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
            //super.onBackPressed();
        }

    }

    private void update(int which) {
        updateMenu = new UpdateMenu(this);
        updateMenu.execute(which);
    }

    public void insertAlmuerzoyComida(String almuerzo, String comida){
        tvAlmuerzo.setText(almuerzo);
        tvComida.setText(comida);

        Animation animation_menu = AnimationUtils.loadAnimation(this, R.anim.tv_alpha);
        tvAlmuerzo.startAnimation(animation_menu);
        tvComida.startAnimation(animation_menu);

        if(!tvAlmuerzo.getText().toString().equals("") || !tvComida.getText().toString().equals("")){
            this.menuDataBase.openMenuDatabase();
            this.menuDataBase.saveMenu(tvAlmuerzo.getText().toString(), tvComida.getText().toString());
            this.menuDataBase.closeMenuDatabase();
        }

    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,tvAlmuerzo1.getText().toString() + " " + tvAlmuerzo.getText().toString() + "\n"
                + tvComida1.getText().toString() + " " +tvComida.getText().toString());
        return intent;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.values[0] == 0) {

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
                    fab.setClickable(false);
                    Animation animation = AnimationUtils.loadAnimation(MenuActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation);
                    update(which);
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class UpdateMenu extends AsyncTask<Integer, Integer, Boolean> {

        private Document doc;
        private Element root;
        private Element table;

        private StringBuilder almuerzoBuilder, comidaBuilder;
        private MenuActivity menu;

        int value;

        public UpdateMenu(MenuActivity menuActivity) {
            this.menu = menuActivity;
            this.almuerzoBuilder = new StringBuilder();
            this.comidaBuilder = new StringBuilder();
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            value = params[0];
            switch (value){
                case dragones:
                    try {
                        downloadFromDragones();
                    } catch (IOException e) {
                        return false;
                    }
                    break;

                case octavitos:
                    try {
                        downloadFromOctavitos();
                    } catch (IOException e) {
                        return false;
                    }
                    break;

                case fac5:
                    try {
                        downloadFromFac5();
                    } catch (IOException e) {
                        return false;
                    }
                    break;

                case zorros:
                    try {
                        downloadFromZorros();
                    } catch (IOException e) {
                        return false;
                    }
                    break;

                case gladiadores:
                    try {
                        downloadFromGladiadores();
                    } catch (IOException e) {
                        return false;
                    }
                    break;

            }

            return true;
        }

        private void downloadFromGladiadores() throws IOException {
            Log.d("!!!!!!!",  ",,,,,,,-1");
            Log.d("!!!!!!!",  ",,,,,,,-1");
            Log.d("!!!!!!!",  ",,,,,,,-1");
            Log.d("!!!!!!!",  ",,,,,,,-1");
            Log.d("!!!!!!!", ",,,,,,,-1");
            Log.d("!!!!!!!", ",,,,,,,-1");
            Log.d("!!!!!!!",  ",,,,,,,-1");
            try {
                _FakeX509TrustManager.allowAllSSL();
                Log.d("!!!!!!!",  ",,,,,,,0");
                Log.d("!!!!!!!",  ",,,,,,,0");
                Log.d("!!!!!!!",  ",,,,,,,0");
                Log.d("!!!!!!!",  ",,,,,,,0");
                Log.d("!!!!!!!",  ",,,,,,,0");
                Log.d("!!!!!!!",  ",,,,,,,0");
                Log.d("!!!!!!!", ",,,,,,,0");
                doc = Jsoup.connect(UCI_URL.URL_GLADIADORES).get();
                Log.d("!!!!!!!", doc.title() + ",,,,,,,1");
                root = doc.getElementById("menu");
                Log.d("!!!!!!!", root.tagName() + ",,,,,,,2");
                table = root.getElementById("mmm");
                Log.d("!!!!!!!", root.tagName() + ",,,,,,,3");
                //String fecha = root.getElementsByTag("div").get(6).text();

                String almuerzo = table.getElementById("almuerzo").text();
                Log.d("!!!!!!!", root.tagName() + ",,,,,,,");
                String comida = table.getElementById("comida").text();
                Log.d("!!!!!!!", root.tagName() + ",,,,,,,");

                almuerzoBuilder.append(almuerzo);
                comidaBuilder.append(comida);

            } catch (IOException e) {
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            } catch (NullPointerException e){
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            }
        }

        private void downloadFromZorros() throws IOException {

            try {
                _FakeX509TrustManager.allowAllSSL();
                doc = Jsoup.connect(UCI_URL.URL_ZORROS).get();
                root = doc.getElementById("accordion");

                //String fecha = root.getElementsByTag("div").get(6).text();

                String almuerzo = root.getElementById("almuerzo_div").text();
                String comida = root.getElementById("comida_div").text();

                almuerzoBuilder.append(almuerzo);
                comidaBuilder.append(comida);

            } catch (IOException e) {
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            } catch (NullPointerException e){
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            }
        }

        private void downloadFromFac5() throws IOException {

            try {
                _FakeX509TrustManager.allowAllSSL();
                doc = Jsoup.connect(UCI_URL.URL_FAC5).get();
                root = doc.getElementById("mod_kmenu_container");

                //String fecha = root.getElementsByTag("div").get(6).text();

                String almuerzo = root.getElementById("menu-almuerzo").text();

                String comida = root.getElementById("menu-comida").text();

                almuerzoBuilder.append(almuerzo);
                comidaBuilder.append(comida);

            } catch (IOException e) {
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            } catch (NullPointerException e){
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            }

        }

        private void downloadFromOctavitos() throws IOException {


            try {
                _FakeX509TrustManager.allowAllSSL();
                doc = Jsoup.connect(UCI_URL.URL_OCTAVITOS).get();
                root = doc.getElementById("block-views-menu-block");

                String fecha = root.getElementsByTag("div").get(6).text();

                String a = root.getElementsByTag("a").get(0).text();

                int al = 1;
                int co = 4;

                if(a.equalsIgnoreCase("comida")){
                    al = 4;
                    co = 1;
                }

                String almuerzo = root.getElementsByTag("p").get(al).text();

                String comida = root.getElementsByTag("p").get(co).text();

                almuerzoBuilder.append(fecha + " " + almuerzo);
                comidaBuilder.append(fecha + " " + comida);

            } catch (IOException e) {
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            } catch (NullPointerException e){
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            }

        }

        private void downloadFromDragones() throws IOException{

            try {
                _FakeX509TrustManager.allowAllSSL();
                doc = Jsoup.connect(UCI_URL.URL_DRAGONES).get();
                root = doc.getElementById("menu_conteiner");


                String h3_0 = root.getElementsByTag("h3").get(0).text();
                //String h3_1 = root.getElementsByTag("h3").get(1).text();

                int al = 0;
                int co = 1;

                if (h3_0.equalsIgnoreCase("comida")){

                    al = 1;
                    co = 0;
                }

                String almuerzo = root.getElementsByTag("p").get(al).text();
                String comida = root.getElementsByTag("p").get(co).text();

                almuerzoBuilder.append(almuerzo + "\n");
                comidaBuilder.append(comida + "\n");

            } catch (IOException e) {
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            } catch (NullPointerException e){
                throw new IOException(this.menu.getResources().getString(R.string.download_error));
            }


        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            thread_running = false;
            fab.clearAnimation();
            Animation pop = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.fab_pop);
            fab.startAnimation(pop);
            fab.setClickable(true);
            fab_2.startAnimation(pop);
            fab_2.setClickable(true);

            boolean activate;
            if(sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)){
                activate = sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                        false);
            }else{
                activate = true;
            }

            if(aBoolean){
                this.menu.insertAlmuerzoyComida(almuerzoBuilder.toString(),comidaBuilder.toString());
                last_correct = value;

                if(activate){
                    sendNotification(aBoolean);
                }else{
                    if(!isInFront) {
                        sendSuccessToast();
                    }
                }

            }else{
                last_wrong = value;

                if(activate){
                    sendNotification(aBoolean);
                }else{
                    sendErrorToast();
                }
            }
            //sendNotification(aBoolean);
        }

    }

    private void sendErrorToast(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string
                .menu_uci_title) + "\n" + getResources().getString(R.string
                .error_con));

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendSuccessToast(){
        String content = "";
        switch (last_correct){
            case dragones:
                content = getResources().getString(R.string.search_drag);
                break;

            case octavitos:
                content = getResources().getString(R.string.search_octv);
                break;

            case fac5:
                content = getResources().getString(R.string.search_fac5);
                break;

            case zorros:
                content = getResources().getString(R.string.search_zorros);
                break;
        }
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string.menus) + "\n" + content);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();

    }

    private boolean isInHome(){
        if(
                (RssActivity.isInFront==null || RssActivity.isInFront==false) &&
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

    private void sendNotification(Boolean aBoolean){
        String contentText ="";

        if(aBoolean && isInHome()){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_menu);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_menu_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.menus));

            notificationCompatBuilder.setContentText(tvAlmuerzo.getText().toString());
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .menu_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(MenuActivity.this, MenuActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    MenuActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_MENU,
                    notificationCompatBuilder.build());

        }else if(!aBoolean && isInHome()){

            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_menu);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_menu_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                    .menu_uci_title));

            notificationCompatBuilder.setContentText(getResources().getString(R.string
                    .error_con));
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .menu_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(MenuActivity.this, MenuActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    MenuActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_MENU,
                    notificationCompatBuilder.build());
        }else{
            if(aBoolean && !isInFront){
                sendSuccessToast();
            }else if(!aBoolean && !isInHome()){
                sendErrorToast();
            }
        }
    }

    private void setNotificationsDefaults(){
        boolean vibrate;
        boolean sound;
        boolean led;
        int defaults = 0;

        if(sharedPreferences.contains(CampusPreference.MENU_VIBRATE)){
            vibrate = sharedPreferences.getBoolean(CampusPreference.MENU_VIBRATE,
                    false);
        }else{
            vibrate = true;
        }

        if(sharedPreferences.contains(CampusPreference.MENU_SOUND)){
            sound = sharedPreferences.getBoolean(CampusPreference.MENU_SOUND,true);
        }else{
            sound = false;
        }

        if(sharedPreferences.contains(CampusPreference.MENU_LED)){
            led = sharedPreferences.getBoolean(CampusPreference.MENU_LED,true);
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
    private CheckBox check;

    private void showDeleteDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(MenuActivity.this)
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
                        menuDataBase.openMenuDatabase();
                        menuDataBase.cleanDatabase();
                        menuDataBase.closeMenuDatabase();
                        Toast toast = Toast.makeText(MenuActivity.this,
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


    private void showFramboyanList() {
        new MaterialDialog.Builder(this)
                .title(R.string.framboyan)
                .icon(getResources().getDrawable(R.drawable.ic_building))
                .content("Cualquier Cosa")
                //.adapter(new ButtonItemAdapter(this, R.array.socialNetworks),
                        //new MaterialDialog.ListCallback() {
                            //@Override
                           // public void onSelection(MaterialDialog dialog, View itemView,
                                                     //int which, CharSequence text) {
                              //  showToast("Clicked item " + which);
                           // }
                        //})
                .show();
    }

    private class FramboyanPlatosRefresh extends AsyncTask<Integer,Integer,Boolean>{

        String framboyan_rss = "https://facultad6.uci.cu/index.php/el-framboyan?format=feed";

        private Document doc;
        private Element root;
        private Elements elements;

        @Override
        protected void onPreExecute() {
            Log.d("!!!!!!!!!!!!!!!!", "OnPreExecute");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            _FakeX509TrustManager.allowAllSSL();
            platos = new ArrayList<>();

            try {
                Log.d("!!!!!!!!!!!!!!!!!", "doInBackground1");
                doc = Jsoup.connect(framboyan_rss).get();
                Log.d("!!!!!!!!!!!!!!!!!", "doInBackground2");
                root = doc.getElementsByTag("table").get(1);
                Log.d("!!!!!!!!!!!!!!!!!", "doInBackground3");
                elements = root.getElementsByTag("tr");
                Log.d("!!!!!!!!!!!!!!!!!", "doInBackground4");
                int count = elements.size();
                Log.d("!!!!!!!!!!!!!!!!!", "doInBackground5");
                for(int i = 0; i < count; i++){

                    Elements aux = elements.get(i).getElementsByTag("td");
                    if(aux.size() > 0) {
                        Element aux1 = aux.get(1);
                        Log.d("!!!!!!!!!!!!!!!!!", aux1.text());
                    }

                }


            } catch (MalformedURLException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d("!!!!!!!!!!!!!!!!", "OnPostExecute");
            super.onPostExecute(aBoolean);


        }
    }

}
