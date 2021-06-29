package cu.uci.cuota;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import cu.uci.campusuci.AboutActivity;
import cu.uci.campusuci.CampusNotification;
import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.LoginUser;
import cu.uci.campusuci.MainActivity;
import cu.uci.campusuci.NavigationDrawer;
import cu.uci.campusuci.R;
import cu.uci.chatty.ChatActivity;
import cu.uci.directorio.DirectorioActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.mapa.MapaActivity;
import cu.uci.mapa.UciMapa;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.Item;
import cu.uci.rss.MyListAdapter;
import cu.uci.rss.MyListAdapter2;
import cu.uci.rss.RssActivity;
import cu.uci.rss.RssDataBaseAttr;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;
import cu.uci.utils.SSL._FakeX509TrustManager;

public class CuotaActivity extends ActionBarActivity implements SensorEventListener{

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

    Menu menu;
    CuotaDataBase cuotaDataBase;
    Usuario user_saved;

    Usuario usuario;
    ArrayList<String> xVals;
    ArrayList<String> x1Vals;
    ArrayList<Entry> entries;
    ArrayList<BarEntry> entries_bar;
    ArrayList<PieDataSet> pieDataSets;
    ArrayList<BarDataSet> barDataSets;

    PieChart piechart;
    PieData pieData;
    PieDataSet pieDataSet;
    Entry cuota_restante;
    Entry cuota_usada;

    BarChart barchart;
    BarData barData;
    BarDataSet barDataSet;
    BarEntry cuota_restante_bar;
    BarEntry cuota_usada_bar;

    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_light;
    EditText et_cuota_user, et_cuota_pass;
    Toolbar mToolbar;
    ActionButton fab, fab_2;

    View shadow;

    public static LoginUser me;
    public static Bundle bundle;

    Boolean drawerIn;
    public static Boolean isInFront;
    _obtenetCuota o;

    Handler handler = new Handler();
    MyRunnable runnable;
    private int refresh_actual;
    private int refresh_final;
    private int refresh_real;
    Boolean cuota_active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuota);

        ctx = this;

        initToolBar_FAB();
        initTextViews();
        initOtherValues();
        initBundle();
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
    protected void onStop() {
        //finish();
        isInFront = false;
        drawerLayout.closeDrawers();
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
        notif = false;
        notifType = 0;
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_CUOTA);

        verifyRefreshTime();

        super.onStart();
    }

    private void verifyRefreshTime() {

        if (sharedPreferences.contains(CampusPreference.CUOTA_REFRESH_TIME)) {
            refresh_final = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
        } else {
            editor.putInt(CampusPreference.CUOTA_REFRESH_TIME, 0);
            editor.commit();
            refresh_final = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
        }

        if(refresh_actual != refresh_final){

            switch (refresh_final){
                case 0:
                    refresh_real = 0;
                    break;

                case 1:
                    refresh_real = 2;
                    break;

                case 2:
                    refresh_real = 5;
                    break;

                case 3:
                    refresh_real = 10;
                    break;

                case 4:
                    refresh_real = 30;
                    break;

                case 5:
                    refresh_real = 60;
                    break;
            }

            refresh_actual = refresh_final;
            runnable = new MyRunnable(refresh_real);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        }

    }

    @Override
    protected void onRestart() {
        //drawer.nav_cuota_stat.setVisibility(View.INVISIBLE);
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else{
            Intent i1 = new Intent(CuotaActivity.this,
                    MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
            //super.onBackPressed();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        //if(me.nombre != null){

        //}
    }

    private void initBundle(){
        me = new LoginUser();
        bundle = getIntent().getExtras();
        if(bundle != null){
            me.setUser(bundle.getString("NOMBRE"),bundle.getString("APELLIDOS"),bundle.getString("FOTO"),
                    bundle.getString("EDIFICIO"),bundle.getString("AREA"),bundle.getString("DNI"),bundle.getString("APTO"),
                    bundle.getString("EXP"),bundle.getString("CRED"),bundle.getString("USER"),bundle.getString("CORREO"),
                    bundle.getString("TEL"),bundle.getString("CAT"),bundle.getString("PROV"),bundle.getString("MUN"),
                    bundle.getString("SEXO"), bundle.getString("PASS"));

            if(me.nombre != null){

                this.et_cuota_user.setText(me.usuario);
                this.et_cuota_pass.setText(bundle.getString("PASS"));

                Boolean cuota_load;

                if (sharedPreferences.contains(CampusPreference.CUOTA_LOAD)) {
                    cuota_load = sharedPreferences.getBoolean(CampusPreference.CUOTA_LOAD, true);
                } else {
                    editor.putBoolean(CampusPreference.CUOTA_LOAD, false);
                    editor.commit();
                    cuota_load = sharedPreferences.getBoolean(CampusPreference.CUOTA_LOAD,
                            false);
                }

                if(cuota_load){
                    thread_running = true;
                    Animation animation_fab = AnimationUtils.loadAnimation(CuotaActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation_fab);
                    fab.setClickable(false);
                    o = new _obtenetCuota();
                    o.execute();
                }

            }
        }


    }

    @Override
    protected void onDestroy() {
        o.cancel(true);
        cuota_active = false;
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_CUOTA);
        super.onDestroy();
    }

    private void initToolBar_FAB(){

        notif = false;
        notifType = 0;
        drawerIn = false;
        isInFront = true;
        drawer = new NavigationDrawer();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        o = new _obtenetCuota();

        cuotaDataBase = new CuotaDataBase(this);

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        notificationCompatBuilder = new NotificationCompat.Builder(CuotaActivity.this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        shadow = findViewById(R.id.shadow_toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            shadow.setMinimumHeight(4);
        }

        am = getAssets();
        roboto_condense_light = Typeface.createFromAsset(am, "roboto_condense_light.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_cuota);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        menu = mToolbar.getMenu();
        menu.getItem(0).setEnabled(false);
        menu.getItem(1).setEnabled(false);

        fab_2 = (ActionButton) findViewById(R.id.fab_login_2);
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
                thread_running = true;
                Animation animation_fab = AnimationUtils.loadAnimation(CuotaActivity.this,
                        R.anim.fab_rotate);
                fab_2.startAnimation(animation_fab);
                fab_2.setClickable(false);
                o = new _obtenetCuota();
                o.execute();
            }
        });

        fab = (ActionButton) findViewById(R.id.fab_login);
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
                Animation animation_fab = AnimationUtils.loadAnimation(CuotaActivity.this,
                        R.anim.fab_rotate);
                fab.startAnimation(animation_fab);
                fab.setClickable(false);
                o = new _obtenetCuota();
                o.execute();
            }
        });

        setSupportActionBar(mToolbar);
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

                    case R.id.vista_val:
                        piechart.setUsePercentValues(false);
                        piechart.invalidate();
                        break;

                    case R.id.vitsa_por:
                        piechart.setUsePercentValues(true);
                        piechart.invalidate();
                        break;

                    case R.id.bchart:
                        piechart.setVisibility(View.INVISIBLE);
                        barchart.setVisibility(View.VISIBLE);
                        //barchart.invalidate();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && isAnimationActive()){
                            //piechart.animateX(1800);
                            barchart.animateY(1500);
                        }else{
                            barchart.invalidate();
                        }
                        break;

                    case R.id.pchart:
                        barchart.setVisibility(View.INVISIBLE);
                        piechart.setVisibility(View.VISIBLE);
                        //piechart.invalidate();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && isAnimationActive()){
                            piechart.animateY(1500, Easing.EasingOption.EaseInCirc);
                            //barchart.animateY(1800);
                        }else{
                            piechart.invalidate();
                        }
                        break;

                    case R.id.action_last_search:
                        cuotaDataBase.openCuotaDatabase();
                        String[] users = cuotaDataBase.getUsersSaved();
                        if(users.length == 0){
                            sendToast();
                        }else{
                            showUsersSaved(users);
                        }
                        cuotaDataBase.closeCuotaDatabase();
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
                            cuotaDataBase.openCuotaDatabase();
                            cuotaDataBase.cleanDatabase();
                            cuotaDataBase.closeCuotaDatabase();
                            Toast toast = Toast.makeText(CuotaActivity.this,
                                    getResources().getString(R.string._delete_alerts_), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                        break;
                }
                return true;
            }
        });

        if (sharedPreferences.contains(CampusPreference.CUOTA_REFRESH_TIME)) {
            refresh_actual = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
            refresh_final = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
        } else {
            editor.putInt(CampusPreference.CUOTA_REFRESH_TIME, 0);
            editor.commit();
            refresh_actual = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
            refresh_actual = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
        }

        switch (refresh_actual){
            case 0:
                refresh_real = 0;
                break;

            case 1:
                refresh_real = 2;
                break;

            case 2:
                refresh_real = 5;
                break;

            case 3:
                refresh_real = 10;
                break;

            case 4:
                refresh_real = 30;
                break;

            case 5:
                refresh_real = 60;
                break;
        }

        runnable = new MyRunnable(refresh_real);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);

    }

    private void initTextViews(){

        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");

        et_cuota_user = (EditText) findViewById(R.id.et_cuota_user);
        et_cuota_user.setTypeface(roboto_light_italic);
        et_cuota_pass = (EditText) findViewById(R.id.et_cuota_pass);
        et_cuota_pass.setTypeface(roboto_light_italic);
    }

    private void initOtherValues(){

        this.usuario = new Usuario();

        this.xVals = new ArrayList<>();
        this.xVals.add("Usada");
        this.xVals.add("Restante");

        this.x1Vals = new ArrayList<>();
        this.x1Vals.add("Total");
        this.x1Vals.add("Usada");

        this.entries = new ArrayList<>();
        pieDataSets = new ArrayList<>();

        this.entries_bar = new ArrayList<>();
        barDataSets = new ArrayList<>();

        this.piechart = (PieChart) findViewById(R.id.piechart);
        this.piechart.setDescriptionTypeface(roboto_light_italic);
        //this.piechart.setValueTypeface(roboto_condense_light);
        //this.piechart.setValueTextColor(Color.BLACK);
        this.piechart.setCenterTextTypeface(roboto_light_italic);
        this.piechart.setTouchEnabled(true);
        this.piechart.setHighlightEnabled(true);

        this.barchart = (BarChart) findViewById(R.id.barchart);
        this.barchart.setDescriptionTypeface(roboto_light_italic);
        //this.barchart.setValueTypeface(roboto_condense_light);
        //this.barchart.setValueTextColor(Color.BLACK);
        this.barchart.setTouchEnabled(true);
        this.barchart.setHighlightEnabled(true);

    }

    private void entry(){
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_tv = AnimationUtils.loadAnimation(this, R.anim.tv_alpha);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        mToolbar.startAnimation(animation_toolbar_in);
        fab.startAnimation(animation_fab);
        et_cuota_pass.startAnimation(animation_toolbar_in);
        et_cuota_user.startAnimation(animation_toolbar_in);
        piechart.startAnimation(animation_tv);
        shadow.startAnimation(animation_shadow_in);

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
                    Animation animation_fab = AnimationUtils.loadAnimation(CuotaActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation_fab);
                    fab.setClickable(false);
                    o = new _obtenetCuota();
                    o.execute();
                }

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class _obtenetCuota extends AsyncTask<String, Integer, Boolean> {

        private final String NAMESPACE = "urn:InetCuotasWS";
        private final String URL = "https://cuotas.uci.cu/servicios/v1/InetCuotasWS.php";
        private final String METHOD_NAME = "ObtenerCuota";
        private final String SOAP_ACTION = "urn:InetCuotasWSAction";
        private String error = "";

        private float c_u, c_r, c_t;

        private final String DOMINIO = "uci.cu";


        @Override
        protected Boolean doInBackground(String... params) {

            _FakeX509TrustManager.allowAllSSL();

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("usuario", et_cuota_user.getText().toString());
            request.addProperty("clave", et_cuota_pass.getText().toString());
            request.addProperty("dominio", DOMINIO);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                usuario.cuota = Float.valueOf(resSoap.getProperty(0).toString());
                usuario.cuota_usada = Float.valueOf(resSoap.getProperty(1).toString());
                usuario.nivel_nav = String.valueOf(resSoap.getProperty(2).toString());
                usuario.usuario = String.valueOf(resSoap.getProperty(3).toString());
                Log.d("!!!!!!!!!", usuario.cuota + "");
                Log.d("!!!!!!!!!", usuario.cuota_usada + "");
                Log.d("!!!!!!!!!", usuario.usuario + "");
                if(usuario.usuario.equalsIgnoreCase("null")){
                    this.error = CuotaActivity.this.getResources().getString(R.string.error_user_pass);
                    return false;
                }else{
                    c_t = usuario.cuota;
                    c_u = usuario.cuota_usada;
                    c_r = (float) 0.0;
                    if(c_u < c_t){
                        c_r = c_t - c_u;
                    }

                    cuota_restante = new Entry(c_r,0);
                    cuota_usada = new Entry(c_u,1);

                    cuota_restante_bar = new BarEntry(c_t, 0);
                    cuota_usada_bar = new BarEntry(c_u, 1);

                    Log.d("!!!!!!!!!", c_t + "");
                    Log.d("!!!!!!!!!", c_u + "");
                    Log.d("!!!!!!!!!", c_r + "");

                    entries = new ArrayList<>();
                    entries.add(cuota_usada);
                    entries.add(cuota_restante);

                    entries_bar = new ArrayList<>();
                    entries_bar.add(cuota_usada_bar);
                    entries_bar.add(cuota_restante_bar);

                    pieDataSet = new PieDataSet(entries,CuotaActivity.this.getResources()
                            .getString(R.string.data_set));
                    pieDataSet.setSliceSpace(3f);
                    pieDataSet.setSelectionShift(5f);
                    barDataSet = new BarDataSet(entries_bar,CuotaActivity.this.getResources()
                            .getString(R.string.data_set));

                    if(c_r == 0){
                        pieDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,74)});
                        barDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,
                                74)});
                    }else if(c_r <= c_t/2){
                        pieDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(255,235,59)});
                        barDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,
                        74)});
                    }else if(c_r >= c_t/2){
                        pieDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,74)});
                        barDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,
                                74)});
                    }


                    pieDataSets = new ArrayList<>();
                    pieDataSets.add(pieDataSet);
                    Log.d("!!!!!!!!!1", "JAJAJAJAJAJA");

                    barDataSets = new ArrayList<>();
                    barDataSets.add(barDataSet);
                    Log.d("!!!!!!!!!2", "JAJAJAJAJAJA");

                    pieData = new PieData(xVals,pieDataSet);
                    pieData.setValueTextSize(12f);
                    pieData.setValueTypeface(roboto_condense_light);
                    pieData.setValueTextColor(Color.BLACK);
                    Log.d("!!!!!!!!!3", "JAJAJAJAJAJA");
                    barData = new BarData(x1Vals, barDataSets);
                    barData.setValueTextSize(12f);
                    barData.setValueTypeface(roboto_condense_light);
                    barData.setValueTextColor(Color.BLACK);
                    Log.d("!!!!!!!!!4", "JAJAJAJAJAJA");

                    piechart.setData(pieData);
                    Log.d("!!!!!!!!!5", "JAJAJAJAJAJA");
                    barchart.setData(barData);
                    Log.d("!!!!!!!!!6", "JAJAJAJAJAJA");
                }
            }
            catch (Exception e)
            {
                this.error = CuotaActivity.this.getResources().getString(R.string.error_con);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //this.tv.setText(usuario.toString());
            thread_running = false;
            fab.clearAnimation();
            Animation pop = AnimationUtils.loadAnimation(CuotaActivity.this, R.anim.fab_pop);
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

            int c_us = (int) c_u;
            int c_res = (int) c_r;
            int c_tot = (int) c_t;
            String contentText = c_us + "/" + c_tot + "\n" + getResources().getString(R.string.rest) +
                    c_res;

            if(aBoolean){
                cuota_active = true;
                verifyRefreshTime();
                piechart.setCenterText(usuario.usuario + "\n" + usuario.nivel_nav + "\n" + usuario
                        .cuota + "MB");
                piechart.setDescription("uci.cu");
                barchart.setDescription("uci.cu");
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(true);
                //piechart.invalidate();
                //barchart.invalidate();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && isAnimationActive()){
                    piechart.animateY(1500);
                    barchart.animateY(1500);
                }else{
                    piechart.invalidate();
                    barchart.invalidate();
                }

                cuotaDataBase.openCuotaDatabase();
                cuotaDataBase.saveCuota(usuario.usuario, c_us, c_tot,
                        usuario.nivel_nav);
                cuotaDataBase.closeCuotaDatabase();

                if(activate){
                    sendNotification(aBoolean, c_u, c_t, c_r);
                }else{
                    if(!isInFront) {
                        sendSuccessToast(contentText);
                    }
                }

            }else{
                cuota_active = false;
                if(activate){
                    sendNotification(aBoolean, c_u, c_t, c_r);
                }else{
                    sendErrorToast();
                }
            }

            super.onPostExecute(aBoolean);
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
                .cuota_uci_title) + "\n" + getResources().getString(R.string
                .error_con));

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_cuota));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendSuccessToast(String texto){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string.cuotas) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageBitmap(piechart.getChartBitmap());

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cuota, menu);
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

    private boolean isInHome(){
        if(
                (MenuActivity.isInFront==null || MenuActivity.isInFront==false) &&
                        (RssActivity.isInFront==null || RssActivity.isInFront==false) &&
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

    private void sendNotification(Boolean aBoolean, float c_u, float c_t, float c_r){
        int c_us = (int) c_u;
        int c_res = (int) c_r;
        int c_tot = (int) c_t;
        String contentText = c_us + "/" + c_tot + "\n" + getResources().getString(R.string.rest) +
                c_res;


        Boolean cuota_always;

        if (sharedPreferences.contains(CampusPreference.CUOTA_ALWAYS_IN_NOTIFICATION_BAR)) {
            cuota_always = sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ALWAYS_IN_NOTIFICATION_BAR,
                    false);
        } else {
            editor.putBoolean(CampusPreference.CUOTA_ALWAYS_IN_NOTIFICATION_BAR, false);
            editor.commit();
            cuota_always = sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ALWAYS_IN_NOTIFICATION_BAR,
                    false);
        }


        if(cuota_always){

            if(aBoolean){
                notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_cuota);
                notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                        .ic_cuota_nav)).getBitmap());
                notificationCompatBuilder.setContentTitle(getResources().getString(R.string.cuotas));

                notificationCompatBuilder.setContentText(contentText);
                notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
                notificationCompatBuilder.setTicker(getResources().getString(R.string
                        .cuota_uci_title));
                notificationCompatBuilder.setAutoCancel(true);
                setNotificationsDefaults();

                Intent notIntent =
                        new Intent(CuotaActivity.this, CuotaActivity.class);
                PendingIntent contIntent = PendingIntent.getActivity(
                        CuotaActivity.this, 0, notIntent, 0);
                notificationCompatBuilder.setContentIntent(contIntent);

                notificationManager.notify(CampusNotification.NOTIFICATION_ID_CUOTA,
                        notificationCompatBuilder.build());

            }else if(!aBoolean){

                notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_cuota);
                notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                                .ic_cuota_nav)).getBitmap()
                );
                notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                        .cuota_uci_title));

                notificationCompatBuilder.setContentText(getResources().getString(R.string
                        .error_con));
                notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
                notificationCompatBuilder.setTicker(getResources().getString(R.string
                        .cuota_uci_title));
                notificationCompatBuilder.setAutoCancel(true);
                setNotificationsDefaults();

                Intent notIntent =
                        new Intent(CuotaActivity.this, CuotaActivity.class);
                PendingIntent contIntent = PendingIntent.getActivity(
                        CuotaActivity.this, 0, notIntent, 0);
                notificationCompatBuilder.setContentIntent(contIntent);

                notificationManager.notify(CampusNotification.NOTIFICATION_ID_CUOTA,
                        notificationCompatBuilder.build());
            }

        }else{

            if(aBoolean && isInHome()){
                notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_cuota);
                notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                        .ic_cuota_nav)).getBitmap());
                notificationCompatBuilder.setContentTitle(getResources().getString(R.string.cuotas));

                notificationCompatBuilder.setContentText(contentText);
                notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
                notificationCompatBuilder.setTicker(getResources().getString(R.string
                        .cuota_uci_title));
                notificationCompatBuilder.setAutoCancel(true);
                setNotificationsDefaults();

                Intent notIntent =
                        new Intent(CuotaActivity.this, CuotaActivity.class);
                PendingIntent contIntent = PendingIntent.getActivity(
                        CuotaActivity.this, 0, notIntent, 0);
                notificationCompatBuilder.setContentIntent(contIntent);

                notificationManager.notify(CampusNotification.NOTIFICATION_ID_CUOTA,
                        notificationCompatBuilder.build());

            }else if(!aBoolean && isInHome()){

                notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_cuota);
                notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                                .ic_cuota_nav)).getBitmap()
                );
                notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                        .cuota_uci_title));

                notificationCompatBuilder.setContentText(getResources().getString(R.string
                        .error_con));
                notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
                notificationCompatBuilder.setTicker(getResources().getString(R.string
                        .cuota_uci_title));
                notificationCompatBuilder.setAutoCancel(true);
                setNotificationsDefaults();

                Intent notIntent =
                        new Intent(CuotaActivity.this, CuotaActivity.class);
                PendingIntent contIntent = PendingIntent.getActivity(
                        CuotaActivity.this, 0, notIntent, 0);
                notificationCompatBuilder.setContentIntent(contIntent);

                notificationManager.notify(CampusNotification.NOTIFICATION_ID_CUOTA,
                        notificationCompatBuilder.build());
            }else{
                if(aBoolean && !isInFront){
                    sendSuccessToast(contentText);
                }else if(!aBoolean && !isInHome()){
                    sendErrorToast();
                }
            }

        }

    }

    private void setNotificationsDefaults(){
        boolean vibrate;
        boolean sound;
        boolean led;
        int defaults = 0;

            if(sharedPreferences.contains(CampusPreference.CUOTA_VIBRATE)){
                vibrate = sharedPreferences.getBoolean(CampusPreference.CUOTA_VIBRATE,
                        false);
            }else{
                vibrate = true;
            }

            if(sharedPreferences.contains(CampusPreference.CUOTA_SOUND)){
                sound = sharedPreferences.getBoolean(CampusPreference.CUOTA_SOUND,true);
            }else{
                sound = false;
            }

            if(sharedPreferences.contains(CampusPreference.CUOTA_LED)){
                led = sharedPreferences.getBoolean(CampusPreference.CUOTA_LED,true);
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

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }

    private void sendToast(){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.rss_toast
                ,(ViewGroup) findViewById(R.id.rss_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_rss_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string.cuota_uci_title) + "\n" + getResources().getString(R
                .string.data_save_not));

        ImageView i = (ImageView) layout.findViewById(R.id.iv_rss_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_cuota));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void showUsersSaved(final String[] users) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.last_searchs)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_cuota_nav))
                .items(users)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        cuotaDataBase.openCuotaDatabase();
                        user_saved = cuotaDataBase.loadCuota(users[which]);
                        Log.d("!!!!!!!!!!!", user_saved.cuota + ",,,,," + user_saved.cuota_usada);
                        showOfflineChart(user_saved);
                        cuotaDataBase.closeCuotaDatabase();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showOfflineChart(Usuario u_){

        piechart.setCenterText(u_.usuario + "\n" + u_.nivel_nav + "\n" + u_
                .cuota + "MB");
        piechart.setDescription("uci.cu");
        barchart.setDescription("uci.cu");

        float c_t = u_.cuota;
        float c_u = u_.cuota_usada;
        float c_r = (float) 0.0;
        if(c_u < c_t){
            c_r = c_t - c_u;
        }

        cuota_restante = new Entry(c_r,0);
        cuota_usada = new Entry(c_u,1);

        cuota_restante_bar = new BarEntry(c_t, 0);
        cuota_usada_bar = new BarEntry(c_u, 1);

        Log.d("!!!!!!!!!", c_t + "");
        Log.d("!!!!!!!!!", c_u + "");
        Log.d("!!!!!!!!!", c_r + "");

        entries = new ArrayList<>();
        entries.add(cuota_usada);
        entries.add(cuota_restante);

        entries_bar = new ArrayList<>();
        entries_bar.add(cuota_usada_bar);
        entries_bar.add(cuota_restante_bar);

        pieDataSet = new PieDataSet(entries,CuotaActivity.this.getResources()
                .getString(R.string.data_set));
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        barDataSet = new BarDataSet(entries_bar,CuotaActivity.this.getResources()
                .getString(R.string.data_set));

        if(c_r == 0){
            pieDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,74)});
            barDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,
                    74)});
        }else if(c_r <= c_t/2){
            pieDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(255,235,59)});
            barDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,
                    74)});
        }else if(c_r >= c_t/2){
            pieDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,74)});
            barDataSet.setColors(new int[]{Color.rgb(244,67,54),Color.rgb(139,195,
                    74)});
        }


        pieDataSets = new ArrayList<>();
        pieDataSets.add(pieDataSet);
        Log.d("!!!!!!!!!1", "JAJAJAJAJAJA");

        barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);
        Log.d("!!!!!!!!!2", "JAJAJAJAJAJA");

        pieData = new PieData(xVals,pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTypeface(roboto_condense_light);
        pieData.setValueTextColor(Color.BLACK);
        Log.d("!!!!!!!!!3", "JAJAJAJAJAJA");
        barData = new BarData(x1Vals, barDataSets);
        barData.setValueTextSize(12f);
        barData.setValueTypeface(roboto_condense_light);
        barData.setValueTextColor(Color.BLACK);
        Log.d("!!!!!!!!!4", "JAJAJAJAJAJA");

        piechart.setData(pieData);
        Log.d("!!!!!!!!!5", "JAJAJAJAJAJA");
        barchart.setData(barData);
        Log.d("!!!!!!!!!6", "JAJAJAJAJAJA");

        //barchart.invalidate();
        //piechart.invalidate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && isAnimationActive()){
            piechart.animateY(1500);
            barchart.animateY(1500);
        }else{
            barchart.invalidate();
            piechart.invalidate();
        }

    }

    private CheckBox check;

    private void showDeleteDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(CuotaActivity.this)
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
                        if (check.isChecked()) {
                            editor.putBoolean(CampusPreference.DELETE_ALERTS, false);
                            editor.commit();
                        }
                        cuotaDataBase.openCuotaDatabase();
                        cuotaDataBase.cleanDatabase();
                        cuotaDataBase.closeCuotaDatabase();
                        Toast toast = Toast.makeText(CuotaActivity.this,
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

    private class MyRunnable implements Runnable {

        int time;

        public MyRunnable(int time_) {
        this.time = time_;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(time == 0){

            }else{
                if(!thread_running && cuota_active){
                    thread_running = true;
                    Animation animation_fab = AnimationUtils.loadAnimation(CuotaActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation_fab);
                    fab.setClickable(false);
                    o = new _obtenetCuota();
                    o.execute();
                }
                handler.postDelayed(this, 60000 * time);
            }

        }

    }

    private boolean isAnimationActive(){

        boolean cuota_animations;

        if (sharedPreferences.contains(CampusPreference.CUOTA_ANIMATIONS)) {
            cuota_animations = sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ANIMATIONS,
                    false);
        } else {
            editor.putBoolean(CampusPreference.CUOTA_ANIMATIONS, true);
            editor.commit();
            cuota_animations = sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ANIMATIONS,
                    false);
        }

        return  cuota_animations;
    }


}
