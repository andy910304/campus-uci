package cu.uci.mapa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.cache.TileCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.MainActivity;
import cu.uci.campusuci.R;
import cu.uci.mapa.database.DBFavorito;
import cu.uci.mapa.database.DBLocation;
import cu.uci.mapa.lugares.Cafeteria;
import cu.uci.mapa.lugares.Comedor;
import cu.uci.mapa.lugares.Docente;
import cu.uci.mapa.lugares.Favorito;
import cu.uci.mapa.lugares.Hospital;
import cu.uci.mapa.lugares.Lugar;
import cu.uci.mapa.lugares.Otro;
import cu.uci.mapa.lugares.Plaza;
import cu.uci.mapa.markers.FavoriteMarker;
import cu.uci.mapa.markers.MeMarker;
import cu.uci.mapa.markers.MeOldMarker;
import cu.uci.mapa.markers.MyMarker;
import cu.uci.mapa.markers.TheMarkers;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;

public class MapaActivity extends ActionBarActivity implements LocationListener, SensorEventListener {

    public static Activity ctx;
    public static Boolean notif;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar mToolbar;
    ActionButton fab;

    MyRunnable update;
    Handler handler = new Handler();
    LocationManager lm;
    UciMapa mapView;
    TileCache tileCache;
    Layer tileRendererLayer;
    MyMarker marker;
    MeMarker marker1;
    MeOldMarker marker2;
    ArrayList<TheMarkers> markers;
    ArrayList<Lugar> lugares;
    ArrayList<Docente> docentes;
    ArrayList<Plaza> plazas;
    ArrayList<Cafeteria> cafeterias;
    ArrayList<Comedor> comedores;
    ArrayList<Favorito> favoritos;
    ArrayList<Otro> otros;
    FrameLayout ll;
    LinearLayout l;
    requestLocationUpdates rlu;
    int temp = 0;
    int temp1 = 0;
    boolean seguir = false;
    boolean seguir2 = false;
    InputStream is;
    AssetManager as;
    Menu menu;

    Typeface roboto_light;
    AssetManager am;

    public static Boolean isInFront;

    SensorManager sm;
    Sensor sensor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean thread_running = false;

    DBLocation dbLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.activity_fade_in,
                R.anim.activity_fade_out);

        ctx = this;
        am = getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");

        isInFront = true;
        notif = false;

        as = getAssets();
        try {
            is = as.open("renderthemev4_1.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .map_background));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciarVistaMapa();
        setContentView(ll);
        iniciarLocalizador();
        //initToolBar();
        //update = new MyRunnable(this.mapView);
        //handler.removeCallbacks(update);
        //handler.postDelayed(update, 500);

        //setStatusBarColor();

    }

    public void setStatusBarColor(){
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
        }
    }

    @Override
    public void onBackPressed() {
        Intent i1 = new Intent(MapaActivity.this,
                MainActivity.class);
        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i1);
    }

    private void initToolBar(){

        mToolbar = new Toolbar(this);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.primary_color));
        mToolbar.inflateMenu(R.menu.menu_mapa);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,mToolbar,
                R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    protected void onStop() {
        //finish();
        isInFront = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        lm.removeUpdates(this);
        if(sensor != null){
            sm.unregisterListener(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isInFront = true;
        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, MapaActivity.this,
                null);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStart() {
        notif = false;
        super.onStart();
    }

    private void iniciarLocalizador() {

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(
                    R.layout.mapa_toast_1
                    ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
            t.setTypeface(roboto_light);
            t.setText(getResources().getString(R.string.gps_on));

            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
            i.setImageDrawable(getResources().getDrawable(R.drawable.ic_gps_on));

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();
        }

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void iniciarVistaMapa() {
        AndroidGraphicFactory.createInstance(getApplication());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ll = new FrameLayout(this);
        l = new LinearLayout(this);
        l.setVerticalGravity(Gravity.BOTTOM);
        l.setHorizontalGravity(Gravity.RIGHT);
        l.setPadding(0,0,20,20);

        dbLocation = new DBLocation(this);

        mapView = new UciMapa(this);
        mapView.setMinimumHeight(ll.getHeight());
        mapView.setMinimumWidth(ll.getWidth());
        mapView.setClickable(true);
        // create a tile cache of suitable size
        tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        mapView.getModel().mapViewPosition.setZoomLevel((byte) 15);
        mapView.getMapZoomControls().setZoomLevelMin((byte) 15);
        mapView.getMapZoomControls().setZoomLevelMax((byte) 20);
        mapView.getMapZoomControls().setZoomControlsGravity(Gravity.LEFT|Gravity.BOTTOM);
        BoundingBox mapLimit = new BoundingBox(22.9748, -82.4851, 22.9998,
                -82.4452);
        mapView.getModel().mapViewPosition.setMapLimit(mapLimit);
        String filepath = Environment.getExternalStorageDirectory().getPath()
                + "/uci.map";
        File f = new File(filepath);
        if(!f.exists()){
            copyAssets();
        }
        // tile renderer layer using internal render theme
        tileRendererLayer = new Layer(this ,this, mapView, tileCache,
                mapView.getModel().mapViewPosition, false,
                AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setMapFile(new File(filepath));
        RenderTheme render = new RenderTheme(this.is);
        tileRendererLayer.setXmlRenderTheme(render);

        // only once a layer is associated with a mapView the rendering starts
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        // mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getMapScaleBar().setVisible(false);

        mapView.getModel().mapViewPosition.setCenter(new LatLong(22.9880,
                -82.4650));

        fab = new ActionButton(this);
        fab.setType(ActionButton.Type.DEFAULT);
        fab.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab.setImageResource(R.drawable.ic_menu_mylocation);
        fab.setShadowRadius(4.4f);
        fab.setShadowXOffset(2.8f);
        fab.setShadowYOffset(2.1f);
        fab.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab.setStrokeWidth(0.0f);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(MapaActivity.this, R.anim.fab_pop);
                fab.startAnimation(pop);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    thread_running = true;
                    fab.setClickable(false);
                    showGpsDialog();

                } else {
                    lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, MapaActivity.this, null);
                    try {
                        double latitude = lm.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER).getLatitude();
                        double longitude = lm.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER).getLatitude();
                    } catch (Exception e) {
                        // TODO: handle exception
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(
                                R.layout.mapa_toast_1
                                ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

                        TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                        t.setTypeface(roboto_light);
                        t.setText(getResources().getString(R.string.unknow_position));

                        ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknow_position));

                        Toast toast = new Toast(MapaActivity.this);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.setView(layout);
                        toast.show();
                    }

                }
            }
        });

        l.addView(fab);
        ll.addView(mapView);
        ll.addView(l);
    }

    private void mostrarDocentes() {

        this.docentes = new ArrayList<Docente>();
        // Docentes
        Docente docente1 = new Docente("Docente 1(Verde)", 22.987828738672633,
                -82.46589100809419);
        Docente docente2 = new Docente("Docente 2(Multicolor)",
                22.98889543274403, -82.46687806101167);
        Docente docente3 = new Docente("Docente 3(Chino)", 22.987377758690045,
                -82.46746226787567);
        Docente docente4 = new Docente("Docente 4(Rojo)", 22.98759011120535,
                -82.4687443637848);
        Docente docente5 = new Docente("Docente 5(Azul)", 22.986370314300828,
                -82.46846809625626);
        Docente docente6 = new Docente("Docente 6(Albet)", 22.985572748830222,
                -82.4693746829033);

        this.docentes.add(docente1);
        this.docentes.add(docente2);
        this.docentes.add(docente3);
        this.docentes.add(docente4);
        this.docentes.add(docente5);
        this.docentes.add(docente6);

        for (int i = 0; i < this.docentes.size(); i++) {

            String descp = this.docentes.get(i).getDescription();
            double lon = this.docentes.get(i).getLongitude();
            double lat = this.docentes.get(i).getLatitude();
            this.insertMarker(descp, lat, lon, R.drawable.ic_docente);
        }
    }

    private void mostrarCafeterias() {

        this.cafeterias = new ArrayList<Cafeteria>();
        // Cafeteria
        Cafeteria pinos = new Cafeteria("Cafetería(Los Pinos)",
                22.986969247677152, -82.46442411747498);
        Cafeteria manticot = new Cafeteria("Cafetería(Manticot)",
                22.988297641761136, -82.4614714181624);
        Cafeteria mosca = new Cafeteria("Cafetería(La Mosca)",
                22.98425985668956, -82.46430038214643);

        this.cafeterias.add(pinos);
        this.cafeterias.add(manticot);
        this.cafeterias.add(mosca);

        for (int i = 0; i < this.cafeterias.size(); i++) {

            String descp = this.cafeterias.get(i).getDescription();
            double lon = this.cafeterias.get(i).getLongitude();
            double lat = this.cafeterias.get(i).getLatitude();
            this.insertMarker(descp, lat, lon, R.drawable.ic_cafeteria);
        }

    }

    private void mostrarPlazas() {

        this.plazas = new ArrayList<Plaza>();
        // Plazas
        Plaza niemeyer = new Plaza("Plaza Niemeyer", 22.989854360869742,
                -82.46474250793457);
        Plaza mella = new Plaza("Plaza Mella", 22.988140744660612,
                -82.46727539326261);
        Plaza lam = new Plaza("Plaza W.Lam", 22.983979466571824,
                -82.46557131052018);

        this.plazas.add(niemeyer);
        this.plazas.add(mella);
        this.plazas.add(lam);

        for (int i = 0; i < this.plazas.size(); i++) {

            String descp = this.plazas.get(i).getDescription();
            double lon = this.plazas.get(i).getLongitude();
            double lat = this.plazas.get(i).getLatitude();
            this.insertMarker(descp, lat, lon, R.drawable.ic_plaza);
        }

    }

    private void mostrarComedores() {

        this.comedores = new ArrayList<Comedor>();
        // Comedores
        Comedor com1 = new Comedor("Complejo Comedor 1", 22.98679502216541,
                -82.46608361244202);
        Comedor com2 = new Comedor("Complejo Comedor 2", 22.987336623248794,
                -82.4622065506814);
        Comedor com3 = new Comedor("Complejo Comedor 3", 22.991387897083357,
                -82.46374163240748);

        this.comedores.add(com1);
        this.comedores.add(com2);
        this.comedores.add(com3);

        for (int i = 0; i < this.comedores.size(); i++) {

            String descp = this.comedores.get(i).getDescription();
            double lon = this.comedores.get(i).getLongitude();
            double lat = this.comedores.get(i).getLatitude();
            this.insertMarker(descp, lat, lon, R.drawable.ic_comedor);
        }

    }

    private void mostrarOtros() {

        this.otros = new ArrayList<Otro>();
        // Otros
        Otro rec = new Otro("Rectorado", 22.992262103744267, -82.46686365396076);
        Otro disco = new Otro("Discoteca", 22.985303600286656,
                -82.46958389520645);
        Otro pan = new Otro("Panadería", 22.984619397097788, -82.46558192066718);
        Otro banco = new Otro("Banco", 22.983680984751473, -82.46504555501448);
        Otro cul = new Otro("Centro cultural", 22.983590429722398,
                -82.46564906658736);
        Otro ip = new Otro("IP", 22.99384201470052, -82.46457084655762);
        Otro deporte = new Otro("Área deportiva", 22.985254797949736,
                -82.46777442615698);
        Otro pista = new Otro("Pista", 22.98343672511716, -82.46971349449072);
        Otro entrada1 = new Otro("Primera Entrada", 22.9841874923446365,
                -82.47130855560303);
        Otro entrada2 = new Otro("Segunda Entrada", 22.990161514419754,
                -82.47216411962458);
        Otro pizzeria = new Otro("Pizzería", 22.984565224363294,
                -82.46352075059608);
        Otro piscina = new Otro("Piscina", 22.986281421788064,
                -82.46524676322937);

        this.otros.add(rec);
        this.otros.add(pan);
        this.otros.add(banco);
        this.otros.add(cul);
        this.otros.add(disco);
        this.otros.add(entrada1);
        this.otros.add(entrada2);
        this.otros.add(deporte);
        this.otros.add(ip);
        this.otros.add(pista);
        this.otros.add(pizzeria);
        this.otros.add(piscina);

        for (int i = 0; i < this.otros.size(); i++) {

            String descp = this.otros.get(i).getDescription();
            double lon = this.otros.get(i).getLongitude();
            double lat = this.otros.get(i).getLatitude();

            if (descp.equalsIgnoreCase("Rectorado")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_rectorado);
            } else if (descp.equalsIgnoreCase("Discoteca")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_discoteca);
            } else if (descp.equalsIgnoreCase("Panadería")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_panaderia);
            } else if (descp.equalsIgnoreCase("Banco")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_banco);
            } else if (descp.equalsIgnoreCase("Centro cultural")) {
                this.insertMarker(descp, lat, lon,
                        R.drawable.ic_centro_cultural);
            } else if (descp.equalsIgnoreCase("IP")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_ip);
            } else if (descp.equalsIgnoreCase("Pista")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_pista);
            } else if (descp.equalsIgnoreCase("Área deportiva")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_deporte);
            } else if (descp.equalsIgnoreCase("Primera Entrada")
                    || descp.equalsIgnoreCase("Segunda Entrada")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_entrada);
            } else if (descp.equalsIgnoreCase("Piscina")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_piscina);
            } else if (descp.equalsIgnoreCase("Pizzería")) {
                this.insertMarker(descp, lat, lon, R.drawable.ic_pizzeria);
            }
        }

    }

    private void mostrarLugares() {
        this.mapView.getLayerManager().getLayers().clear();
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
        this.lugares = new ArrayList<Lugar>();

        // Docentes
        Docente docente1 = new Docente("Docente 1(Verde)", 22.987828738672633,
                -82.46589100809419);
        Docente docente2 = new Docente("Docente 2(Multicolor)",
                22.98889543274403, -82.46687806101167);
        Docente docente3 = new Docente("Docente 3(Chino)", 22.987377758690045,
                -82.46746226787567);
        Docente docente4 = new Docente("Docente 4(Rojo)", 22.98759011120535,
                -82.4687443637848);
        Docente docente5 = new Docente("Docente 5(Azul)", 22.986370314300828,
                -82.46846809625626);
        Docente docente6 = new Docente("Docente 6(Albet)", 22.985572748830222,
                -82.4693746829033);

        // Plazas
        Plaza niemeyer = new Plaza("Plaza Niemeyer", 22.989854360869742,
                -82.46474250793457);
        Plaza mella = new Plaza("Plaza Mella", 22.988140744660612,
                -82.46727539326261);
        Plaza lam = new Plaza("Plaza W.Lam", 22.983979466571824,
                -82.46557131052018);

        // Hospital
        Hospital hosp = new Hospital("Hospital UCI", 22.989416082136373,
                -82.46169954180718);

        // Cafeteria
        Cafeteria pinos = new Cafeteria("Cafetería(Los Pinos)",
                22.986969247677152, -82.46442411747498);
        Cafeteria manticot = new Cafeteria("Cafetería(Manticot)",
                22.988297641761136, -82.4614714181624);
        Cafeteria mosca = new Cafeteria("Cafetería(La Mosca)",
                22.98425985668956, -82.46430038214643);

        // Comedores
        Comedor com1 = new Comedor("Complejo Comedor 1", 22.98679502216541,
                -82.46608361244202);
        Comedor com2 = new Comedor("Complejo Comedor 2", 22.987336623248794,
                -82.4622065506814);
        Comedor com3 = new Comedor("Complejo Comedor 3", 22.991387897083357,
                -82.46374163240748);

        // Otros
        Otro rec = new Otro("Rectorado", 22.992262103744267, -82.46686365396076);
        Otro disco = new Otro("Discoteca", 22.985303600286656,
                -82.46958389520645);
        Otro pan = new Otro("Panadería", 22.984619397097788, -82.46558192066718);
        Otro banco = new Otro("Banco", 22.983680984751473, -82.46504555501448);
        Otro cul = new Otro("Centro cultural", 22.983590429722398,
                -82.46564906658736);
        Otro ip = new Otro("IP", 22.99384201470052, -82.46457084655762);
        Otro deporte = new Otro("Área deportiva", 22.985254797949736,
                -82.46777442615698);
        Otro pista = new Otro("Pista", 22.98343672511716, -82.46971349449072);
        Otro entrada1 = new Otro("Primera Entrada", 22.9841874923446365,
                -82.47130855560303);
        Otro entrada2 = new Otro("Segunda Entrada", 22.990161514419754,
                -82.47216411962458);
        Otro pizzeria = new Otro("Pizzería", 22.984565224363294,
                -82.46352075059608);
        Otro piscina = new Otro("Piscina", 22.986281421788064,
                -82.46524676322937);

        // Favoritos
        DBFavorito db = new DBFavorito(this);
        db.open();
        this.favoritos = db.getData();
        db.close();

        this.lugares.add(docente1);
        this.lugares.add(docente2);
        this.lugares.add(docente3);
        this.lugares.add(docente4);
        this.lugares.add(docente5);
        this.lugares.add(docente6);
        this.lugares.add(niemeyer);
        this.lugares.add(mella);
        this.lugares.add(lam);
        this.lugares.add(hosp);
        this.lugares.add(pinos);
        this.lugares.add(manticot);
        this.lugares.add(mosca);
        this.lugares.add(com1);
        this.lugares.add(com2);
        this.lugares.add(com3);
        this.lugares.add(pan);
        this.lugares.add(banco);
        this.lugares.add(cul);
        this.lugares.add(rec);
        this.lugares.add(disco);
        this.lugares.add(ip);
        this.lugares.add(pista);
        this.lugares.add(deporte);
        this.lugares.add(entrada1);
        this.lugares.add(entrada2);
        this.lugares.add(pizzeria);
        this.lugares.add(piscina);

        if (!this.favoritos.isEmpty()) {

            for (int i = 0; i < this.favoritos.size(); i++) {

                this.lugares.add(this.favoritos.get(i));

            }
        }

        for (int i = 0; i < this.lugares.size(); i++) {

            String descp = this.lugares.get(i).getDescription();
            double lon = this.lugares.get(i).getLongitude();
            double lat = this.lugares.get(i).getLatitude();

            if (this.lugares.get(i) instanceof Docente) {

                this.insertMarker(descp, lat, lon, R.drawable.ic_docente);

            } else if (this.lugares.get(i) instanceof Plaza) {

                this.insertMarker(descp, lat, lon, R.drawable.ic_plaza);

            } else if (this.lugares.get(i) instanceof Cafeteria) {

                this.insertMarker(descp, lat, lon, R.drawable.ic_cafeteria);

            } else if (this.lugares.get(i) instanceof Hospital) {

                this.insertMarker(descp, lat, lon, R.drawable.ic_hospital);

            } else if (this.lugares.get(i) instanceof Comedor) {

                this.insertMarker(descp, lat, lon, R.drawable.ic_comedor);

            } else if (this.lugares.get(i) instanceof Otro) {

                if (descp.equalsIgnoreCase("Rectorado")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_rectorado);

                } else if (descp.equalsIgnoreCase("Discoteca")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_discoteca);

                } else if (descp.equalsIgnoreCase("Panadería")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_panaderia);

                } else if (descp.equalsIgnoreCase("Banco")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_banco);

                } else if (descp.equalsIgnoreCase("Centro cultural")) {

                    this.insertMarker(descp, lat, lon,
                            R.drawable.ic_centro_cultural);

                } else if (descp.equalsIgnoreCase("IP")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_ip);

                } else if (descp.equalsIgnoreCase("Pista")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_pista);

                } else if (descp.equalsIgnoreCase("Área deportiva")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_deporte);

                } else if (descp.equalsIgnoreCase("Primera Entrada")
                        || descp.equalsIgnoreCase("Segunda Entrada")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_entrada);

                } else if (descp.equalsIgnoreCase("Piscina")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_piscina);

                } else if (descp.equalsIgnoreCase("Pizzería")) {

                    this.insertMarker(descp, lat, lon, R.drawable.ic_pizzeria);

                }
            } else if (this.lugares.get(i) instanceof Favorito) {

                this.mapView
                        .getLayerManager()
                        .getLayers()
                        .add(new FavoriteMarker(this,
                                this,
                                descp,
                                mapView,
                                new LatLong(lat, lon),
                                AndroidGraphicFactory
                                        .convertToBitmap(getResources()
                                                .getDrawable(
                                                        R.drawable.ic_favorito)),
                                0, 0));

            }

        }

    }

    private void MostrarFavoritos(){
        DBFavorito db = new DBFavorito(this);
        db.open();
        this.favoritos = db.getData();
        db.close();

        for (int i = 0; i < this.favoritos.size(); i++) {
            String descp = this.favoritos.get(i).getDescription();
            double lat = this.favoritos.get(i).getLatitude();
            double lon = this.favoritos.get(i).getLongitude();
            Log.d("!!!!!!", descp + " " + lat + " " + lon);

            this.mapView
                    .getLayerManager()
                    .getLayers()
                    .add(new FavoriteMarker(this,
                            this,
                            descp,
                            mapView,
                            new LatLong(lat, lon),
                            AndroidGraphicFactory
                                    .convertToBitmap(getResources()
                                            .getDrawable(
                                                    R.drawable.ic_favorito)),
                            0, 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        this.menu = menu;
        return true;
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // action_search

            case R.id.action_docente_1:
                Docente docente1 = new Docente("Docente 1(Verde)",
                        22.987828738672633, -82.46589100809419);
                this.insertMarker(docente1.getDescription(),
                        docente1.getLatitude(), docente1.getLongitude(),
                        R.drawable.ic_docente);
                break;

            case R.id.action_docente_2:
                Docente docente2 = new Docente("Docente 2(Multicolor)",
                        22.98889543274403, -82.46687806101167);
                this.insertMarker(docente2.getDescription(),
                        docente2.getLatitude(), docente2.getLongitude(),
                        R.drawable.ic_docente);
                break;

            case R.id.action_docente_3:
                Docente docente3 = new Docente("Docente 3(Chino)",
                        22.987377758690045, -82.46746226787567);
                this.insertMarker(docente3.getDescription(),
                        docente3.getLatitude(), docente3.getLongitude(),
                        R.drawable.ic_docente);
                break;

            case R.id.action_docente_4:
                Docente docente4 = new Docente("Docente 4(Rojo)",
                        22.98759011120535, -82.4687443637848);
                this.insertMarker(docente4.getDescription(),
                        docente4.getLatitude(), docente4.getLongitude(),
                        R.drawable.ic_docente);
                break;

            case R.id.action_docente_5:
                Docente docente5 = new Docente("Docente 5(Azul)",
                        22.986370314300828, -82.46846809625626);
                this.insertMarker(docente5.getDescription(),
                        docente5.getLatitude(), docente5.getLongitude(),
                        R.drawable.ic_docente);
                break;

            case R.id.action_docente_6:
                Docente docente6 = new Docente("Docente 6(Albet)",
                        22.985572748830222, -82.4693746829033);
                this.insertMarker(docente6.getDescription(),
                        docente6.getLatitude(), docente6.getLongitude(),
                        R.drawable.ic_docente);
                break;

            case R.id.action_docente_todos:
                this.mostrarDocentes();
                break;

            // Hospital
            case R.id.action_hospital:
                Hospital hosp = new Hospital("Hospital UCI", 22.989416082136373,
                        -82.46169954180718);
                this.insertMarker(hosp.getDescription(), hosp.getLatitude(),
                        hosp.getLongitude(), R.drawable.ic_hospital);
                break;

            // Cafeterias

            case R.id.action_cafeteria_mosca:

                Cafeteria mosca = new Cafeteria("Cafetería(La Mosca)",
                        22.98425985668956, -82.46430038214643);
                this.insertMarker(mosca.getDescription(), mosca.getLatitude(),
                        mosca.getLongitude(), R.drawable.ic_cafeteria);
                break;

            case R.id.action_cafeteria_manticot:
                Cafeteria manticot = new Cafeteria("Cafetería(Manticot)",
                        22.988297641761136, -82.4614714181624);
                this.insertMarker(manticot.getDescription(),
                        manticot.getLatitude(), manticot.getLongitude(),
                        R.drawable.ic_cafeteria);
                break;

            case R.id.action_cafeteria_pinos:
                Cafeteria pinos = new Cafeteria("Cafetería(Los Pinos)",
                        22.986969247677152, -82.46442411747498);
                this.insertMarker(pinos.getDescription(), pinos.getLatitude(),
                        pinos.getLongitude(), R.drawable.ic_cafeteria);
                break;

            case R.id.action_cafeteria_todos:
                this.mostrarCafeterias();

                break;

            case R.id.action_todos:
                this.mostrarLugares();
                break;

            case R.id.action_limpiar:
                this.mapView.getLayerManager().getLayers().clear();
                this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

                break;

            case R.id.action_plaza_lam:

                Plaza lam = new Plaza("Plaza W.Lam", 22.983979466571824,
                        -82.46557131052018);
                this.insertMarker(lam.getDescription(), lam.getLatitude(),
                        lam.getLongitude(), R.drawable.ic_plaza);
                break;

            case R.id.action_plaza_mella:
                Plaza mella = new Plaza("Plaza Mella", 22.988140744660612,
                        -82.46727539326261);
                this.insertMarker(mella.getDescription(), mella.getLatitude(),
                        mella.getLongitude(), R.drawable.ic_plaza);
                break;

            case R.id.action_plaza_niemeyer:
                Plaza niemeyer = new Plaza("Plaza Niemeyer", 22.989854360869742,
                        -82.46474250793457);
                this.insertMarker(niemeyer.getDescription(),
                        niemeyer.getLatitude(), niemeyer.getLongitude(),
                        R.drawable.ic_plaza);
                break;

            case R.id.action_plaza_todos:
                this.mostrarPlazas();
                break;

            case R.id.action_comedor_todos:
                this.mostrarComedores();
                break;

            case R.id.action_comedor_1:
                Comedor com1 = new Comedor("Complejo Comedor 1", 22.98679502216541,
                        -82.46608361244202);
                this.insertMarker(com1.getDescription(), com1.getLatitude(),
                        com1.getLongitude(), R.drawable.ic_comedor);
                break;

            case R.id.action_comedor_2:
                Comedor com2 = new Comedor("Complejo Comedor 2", 22.987336623248794,
                        -82.4622065506814);
                this.insertMarker(com2.getDescription(), com2.getLatitude(),
                        com2.getLongitude(), R.drawable.ic_comedor);
                break;

            case R.id.action_comedor_3:

                Comedor com3 = new Comedor("Complejo Comedor 3", 22.991387897083357,
                        -82.46374163240748);
                this.insertMarker(com3.getDescription(), com3.getLatitude(),
                        com3.getLongitude(), R.drawable.ic_comedor);
                break;

            case R.id.action_otros_rectorado:
                Otro rec = new Otro("Rectorado", 22.992262103744267,
                        -82.46686365396076);
                this.insertMarker(rec.getDescription(), rec.getLatitude(),
                        rec.getLongitude(), R.drawable.ic_rectorado);
                break;

            case R.id.action_otros_discoteca:
                Otro disco = new Otro("Discoteca", 22.985303600286656,
                        -82.46958389520645);
                this.insertMarker(disco.getDescription(), disco.getLatitude(),
                        disco.getLongitude(), R.drawable.ic_discoteca);
                break;

            case R.id.action_otros_panaderia:
                Otro pan = new Otro("Panadería", 22.984619397097788,
                        -82.46558192066718);
                this.insertMarker(pan.getDescription(), pan.getLatitude(),
                        pan.getLongitude(), R.drawable.ic_panaderia);
                break;

            case R.id.action_otros_banco:
                Otro banco = new Otro("Banco", 22.983680984751473,
                        -82.46504555501448);
                this.insertMarker(banco.getDescription(), banco.getLatitude(),
                        banco.getLongitude(), R.drawable.ic_banco);
                break;

            case R.id.action_otros_centro_cultural:
                Otro cul = new Otro("Centro cultural", 22.983590429722398,
                        -82.46564906658736);
                this.insertMarker(cul.getDescription(), cul.getLatitude(),
                        cul.getLongitude(), R.drawable.ic_centro_cultural);
                break;

            case R.id.action_otros_ip:
                Otro ip = new Otro("IP", 22.99384201470052, -82.46457084655762);
                this.insertMarker(ip.getDescription(), ip.getLatitude(),
                        ip.getLongitude(), R.drawable.ic_ip);
                break;

            case R.id.action_otros_pizza:
                Otro pizzeria = new Otro("Pizzería", 22.984565224363294,
                        -82.46352075059608);
                this.insertMarker(pizzeria.getDescription(), pizzeria.getLatitude(),
                        pizzeria.getLongitude(), R.drawable.ic_pizzeria);
                break;

            case R.id.action_otros_piscina:
                Otro piscina = new Otro("Piscina", 22.986281421788064,
                        -82.46524676322937);
                this.insertMarker(piscina.getDescription(), piscina.getLatitude(),
                        piscina.getLongitude(), R.drawable.ic_piscina);
                break;

            case R.id.action_otros_todos:
                this.mostrarOtros();
                break;

            case R.id.action_center:

                mapView.getModel().mapViewPosition.setZoomLevel((byte) 15);
                mapView.getModel().mapViewPosition.setCenter(new LatLong(22.9880,
                        -82.4650));
                break;

            case R.id.action_gps:
                if(this.menu.findItem(R.id.action_follow).isChecked()){
                    Toast toast = Toast.makeText(this,getResources().getString(R.string.follow_off),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }else{
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

                break;

            //case R.id.action_my_location:
             //   if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
              //      AlertDialog.Builder builder = new AlertDialog.Builder(this);
               //     builder.setTitle(getResources().getString(R.string.gps_off) + ".");
               //     builder.setMessage(getResources().getString(R.string.activate));
                //    builder.setPositiveButton(getResources().getString(R.string.yes),
                 //           new DialogInterface.OnClickListener() {

                 //       @Override
                  //      public void onClick(DialogInterface dialog, int which) {
                  //          // TODO Auto-generated method stub
                  //          startActivity(new Intent(
                  //                  android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   //     }
                   // });
                   // builder.setNegativeButton(getResources().getString(R.string.no),
                    //        new DialogInterface.OnClickListener() {

                   //     @Override
                    //    public void onClick(DialogInterface dialog, int which) {
                    //        // TODO Auto-generated method stub

                    //    }
                   // });
                   // AlertDialog alert = builder.create();
                   // alert.show();
                //} else {
                //    lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                 //   try {
                 //       double latitude = lm.getLastKnownLocation(
                 //               LocationManager.GPS_PROVIDER).getLatitude();
                  //      double longitude = lm.getLastKnownLocation(
                  //              LocationManager.GPS_PROVIDER).getLatitude();
                   // } catch (Exception e) {
                   //     // TODO: handle exception
                   //     Toast.makeText(this, getResources().getString(R.string.unknow_position),
                    //            Toast.LENGTH_SHORT).show();
                   // }

                //}

               // break;

            case R.id.action_follow:

                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(
                            R.layout.mapa_toast_1
                            ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

                    TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                    t.setTypeface(roboto_light);
                    t.setText(getResources().getString(R.string.gps_off));

                    ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                    i.setImageDrawable(getResources().getDrawable(R.drawable.ic_dialog_gps));

                    Toast toast = new Toast(this);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setView(layout);
                    toast.show();
                } else {

                    seguir = !seguir;
                    item.setChecked(seguir);
                    if (item.isChecked()) {
                        rlu = new requestLocationUpdates();
                        rlu.execute();
                    } else {
                        this.rlu = null;
                        this.lm.removeUpdates(this);

                    }

                }

                break;

            case R.id.action_otros_deporte:

                Otro deporte = new Otro("Área deportiva", 22.985254797949736,
                        -82.46777442615698);
                this.insertMarker(deporte.getDescription(), deporte.getLatitude(),
                        deporte.getLongitude(), R.drawable.ic_deporte);

                break;

            case R.id.action_otros_pista:

                Otro pista = new Otro("Pista", 22.98343672511716,
                        -82.46971349449072);
                this.insertMarker(pista.getDescription(), pista.getLatitude(),
                        pista.getLongitude(), R.drawable.ic_pista);

                break;

            case R.id.action_otros_entradas:

                Otro entrada1 = new Otro("Primera Entrada", 22.9841874923446365,
                        -82.47130855560303);
                Otro entrada2 = new Otro("Segunda Entrada", 22.990161514419754,
                        -82.47216411962458);
                this.insertMarker(entrada1.getDescription(),
                        entrada1.getLatitude(), entrada1.getLongitude(),
                        R.drawable.ic_entrada);
                this.insertMarker(entrada2.getDescription(),
                        entrada2.getLatitude(), entrada2.getLongitude(),
                        R.drawable.ic_entrada);

                break;

            case R.id.action_favoritos:

                MostrarFavoritos();

                break;

            case R.id.action_last_position:

                dbLocation.openMapaDatabase();
                double[] latlong = dbLocation.loadLastLocation();
                dbLocation.closeMapaDatabase();
                if(latlong[0] == 0){

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(
                            R.layout.mapa_toast_1
                            ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

                    TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                    t.setTypeface(roboto_light);
                    t.setText(getResources().getString(R.string.data_save_not));

                    ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                    i.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknow_position));

                    Toast toast = new Toast(MapaActivity.this);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setView(layout);
                    toast.show();

                }else{

                    if (temp1 == 0) {
                        LatLong aux = new LatLong(latlong[0], latlong[1]);
                        marker2 = new MeOldMarker(this,
                                this,
                                mapView,
                                aux,
                                AndroidGraphicFactory
                                        .convertToBitmap(getResources()
                                                .getDrawable(
                                                        R.drawable
                                                                .ic_maps_indicator_current_position_anim2)),
                                0, 0);
                        mapView.getLayerManager().getLayers().add(marker2);
                        temp1++;
                    } else {
                        mapView.getLayerManager().getLayers().remove(marker2);
                        LatLong aux = new LatLong(latlong[0], latlong[1]);
                        marker2 = new MeOldMarker(this,
                                this,
                                mapView,
                                aux,
                                AndroidGraphicFactory
                                        .convertToBitmap(getResources()
                                                .getDrawable(
                                                        R.drawable
                                                                .ic_maps_indicator_current_position_anim2)),
                                0, 0);
                        mapView.getLayerManager().getLayers().add(marker2);
                        int i = mapView.getLayerManager().getLayers().indexOf(marker2);
                        mapView.getLayerManager().getLayers().get(i).requestRedraw();
                    }

                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertMarker(String descp, double lat, double lon, int icon) {

        this.mapView
                .getLayerManager()
                .getLayers()
                .add(new TheMarkers(this,this, descp, mapView,
                        new LatLong(lat, lon), AndroidGraphicFactory
                        .convertToBitmap(getResources().getDrawable(
                                icon)), 0, 0));
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
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

                    Animation pop = AnimationUtils.loadAnimation(MapaActivity.this, R.anim.fab_pop);
                    fab.startAnimation(pop);
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        thread_running = true;
                        fab.setClickable(false);
                        showGpsDialog();

                    } else {
                        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, MapaActivity.this, null);
                        try {
                            double latitude = lm.getLastKnownLocation(
                                    LocationManager.GPS_PROVIDER).getLatitude();
                            double longitude = lm.getLastKnownLocation(
                                    LocationManager.GPS_PROVIDER).getLatitude();
                        } catch (Exception e) {
                            // TODO: handle exception
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(
                                    R.layout.mapa_toast_1
                                    ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

                            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                            t.setTypeface(roboto_light);
                            t.setText(getResources().getString(R.string.unknow_position));

                            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                            i.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknow_position));

                            Toast toast = new Toast(MapaActivity.this);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setView(layout);
                            toast.show();
                        }

                    }

                }

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class requestLocationUpdates extends
            AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            seguir2 = true;

        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            // loop.prepare();
            while (seguir2) {
                try {
                    lm.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                            MapaActivity.this, null);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
                    return true;
                }

            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub

            lm.removeUpdates(MapaActivity.this);

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        dbLocation.openMapaDatabase();
        dbLocation.saveLocation(latitude,longitude);
        dbLocation.closeMapaDatabase();
       //Toast toast =  Toast.makeText(this, latitude + " " + longitude, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.BOTTOM, 0, 0);
         //       toast.show();
        if (temp == 0) {
            LatLong aux = new LatLong(latitude, longitude);
            marker1 = new MeMarker(this,
                    this,
                    mapView,
                    aux,
                    AndroidGraphicFactory
                            .convertToBitmap(getResources()
                                    .getDrawable(
                                            R.drawable.ic_maps_indicator_current_position_anim3)),
                    0, 0);
            mapView.getLayerManager().getLayers().add(marker1);
            temp++;
        } else {
            mapView.getLayerManager().getLayers().remove(marker1);
            LatLong aux = new LatLong(latitude, longitude);
            marker1 = new MeMarker(this,
                    this,
                    mapView,
                    aux,
                    AndroidGraphicFactory
                            .convertToBitmap(getResources()
                                    .getDrawable(
                                            R.drawable.ic_maps_indicator_current_position_anim3)),
                    0, 0);
            mapView.getLayerManager().getLayers().add(marker1);
            int i = mapView.getLayerManager().getLayers().indexOf(marker1);
            mapView.getLayerManager().getLayers().get(i).requestRedraw();
        }

        if(temp1 > 0){
            mapView.getLayerManager().getLayers().remove(marker2);
            temp1=0;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(
                    R.layout.mapa_toast_1
                    ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
            t.setTypeface(roboto_light);
            t.setText(getResources().getString(R.string.gps_off));

            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
            i.setImageDrawable(getResources().getDrawable(R.drawable.ic_dialog_gps));

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(
                    R.layout.mapa_toast_1
                    ,(ViewGroup) findViewById(R.id.mapa_toast_layout));

            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
            t.setTypeface(roboto_light);
            t.setText(getResources().getString(R.string.gps_on));

            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
            i.setImageDrawable(getResources().getDrawable(R.drawable.ic_gps_on));

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    private class MyRunnable implements Runnable {

        UciMapa mv;

        public MyRunnable(UciMapa mv) {
            this.mv = mv;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            this.mv.verifyZoom();
            handler.postDelayed(this, 1000);
        }

    }

    private void copyAssets() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = as.open("uci.map");
            File outFile = new File(Environment.getExternalStorageDirectory() + File
                    .separator + "uci.map");
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + "uci.map", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }

    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void showGpsDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.gps_off)
                .content(R.string.activate)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_dialog_gps))
                .cancelable(false)
                .positiveText(R.string.accept)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        thread_running = false;
                        fab.setClickable(true);
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.setClickable(true);
                        thread_running = false;
                    }

                }).build();
        dialog.show();

    }
}
