package cu.uci.horario;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;

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
import cu.uci.mapa.MapaActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.MySpinnerAdapter;
import cu.uci.rss.RssActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.GravityEnum;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;

public class HorarioActivity extends ActionBarActivity {

    DBHorario dbHorario;

    SharedPreferences sharedPreferences;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;

    public static NavigationDrawer drawer;
    public static Boolean notif;
    public static int notifType;

    public static Activity ctx;
    public static Boolean isInFront;
    Boolean drawerIn;

    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_condense_bold, roboto_light;
    Toolbar mToolbar;
    ActionButton fab;

    View shadow;

    RelativeLayout ll_help;
    TextView tv_help_faculty,tv_help_weeks,tv_help_group,tv_help_refresh;

    MaterialDialog progress;

    int posicion = 0;
    int posicion2 = 0;
    int posicion3 = 0;

    boolean up2 = false;
    boolean up3 = false;

    LinkedList<String> stringfacultades;
    //LinkedList<String> stringfacultades1;
    LinkedList<String> stringsemanas;
    LinkedList<String> stringbrigadas;
    LinkedList<String> stringhorario;

    ArrayAdapter<String> arrayfacultades;
    ArrayAdapter<String> arraysemanas;
    ArrayAdapter<String> arraybrigadas;

    String[] horario;

    GridView dias;
    GridView gridview;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapterDias;
    LinkedList<String> lista;
    LinkedList<String> diaslista;

    public static LoginUser me;
    public static Bundle bundle;

    LoadSchedule loadSchedule_;
    LoadWeeks loadWeeks_;
    LoadGroups loadGroups_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        ctx = this;
        initToolBar_FAB();
        initMenuItems();
        initGridView();
        //initBundle();

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

    private void initGridView(){

        lista = new LinkedList<>();
        diaslista = new LinkedList<>();

        diaslista.add(getResources().getString(R.string.lunes));
        diaslista.add(getResources().getString(R.string.martes));
        diaslista.add(getResources().getString(R.string.miercoles));
        diaslista.add(getResources().getString(R.string.jueves));
        diaslista.add(getResources().getString(R.string.viernes));
        diaslista.add(getResources().getString(R.string.sabado));
        diaslista.add(getResources().getString(R.string.domingo));

        gridview = (GridView) findViewById(R.id.idgridview);
        dias = (GridView) findViewById(R.id.idgridviewdias);

        arrayAdapterDias = new ArrayAdapter<>(this,
                R.layout.list_item_dias, diaslista);
        dias.setAdapter(arrayAdapterDias);

    }

    private void initMenuItems(){
        stringfacultades = new LinkedList<>();
        //stringfacultades1 = new LinkedList<>();
        stringsemanas = new LinkedList<>();
        stringbrigadas = new LinkedList<>();
        stringhorario = new LinkedList<>();

        //stringfacultades1.add(getResources().getString(R.string.faculty_1));
        //stringfacultades1.add(getResources().getString(R.string.faculty_2));
        //stringfacultades1.add(getResources().getString(R.string.faculty_3));
        //stringfacultades1.add(getResources().getString(R.string.faculty_4));
        //stringfacultades1.add(getResources().getString(R.string.faculty_5));
        //stringfacultades1.add(getResources().getString(R.string.faculty_6));
        //stringfacultades1.add(getResources().getString(R.string.faculty_7));

        stringfacultades.add("Facultad 1");
        stringfacultades.add("Facultad 2");
        stringfacultades.add("Facultad 3");
        stringfacultades.add("Facultad 4");
        stringfacultades.add("Facultad 5");
        stringfacultades.add("Facultad 6");
        stringfacultades.add("Facultad 7");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_horario, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
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
    protected void onStop() {
        //finish();
        drawerLayout.closeDrawers();
        isInFront = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        isInFront = true;
        drawerLayout.invalidate();
        super.onResume();
    }

    @Override
    protected void onStart() {
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_HORARIO);
        notif = false;
        notifType = 0;
        super.onStart();
    }

    @Override
    protected void onRestart() {
        //drawer.nav_horario_stat.setVisibility(View.INVISIBLE);
        drawerLayout.invalidate();
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else if(ll_help.isShown()){
            Animation animation1 = AnimationUtils.loadAnimation(HorarioActivity.this,
                    R.anim.fab_fade_out);
            ll_help.startAnimation(animation1);
            ll_help.setVisibility(View.INVISIBLE);
        }else{
            Intent i1 = new Intent(HorarioActivity.this,
                    MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
        }
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        loadSchedule_.cancel(true);
        loadGroups_.cancel(true);
        loadWeeks_.cancel(true);
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_HORARIO);
        super.onDestroy();
    }

    private void initToolBar_FAB(){

        drawerIn = false;
        isInFront = true;
        notif = false;
        notifType = 0;
        drawer = new NavigationDrawer();

        dbHorario = new DBHorario(this);

        ll_help = (RelativeLayout) findViewById(R.id.ll_help);
        ll_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_help.isShown()){
                    Animation animation1 = AnimationUtils.loadAnimation(HorarioActivity.this,
                            R.anim.fab_fade_out);
                    ll_help.startAnimation(animation1);
                    ll_help.setVisibility(View.INVISIBLE);
                }
            }
        });

        tv_help_faculty = (TextView) findViewById(R.id.tv_help_faculty);
        tv_help_weeks = (TextView) findViewById(R.id.tv_help_weeks);
        tv_help_group = (TextView) findViewById(R.id.tv_help_group);
        tv_help_refresh = (TextView) findViewById(R.id.tv_help_refresh);


        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);

        loadSchedule_ = new LoadSchedule();
        loadWeeks_ = new LoadWeeks();
        loadGroups_ = new LoadGroups();

        notificationCompatBuilder = new NotificationCompat.Builder(HorarioActivity.this);
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

        tv_help_faculty.setTypeface(roboto_light);
        tv_help_weeks.setTypeface(roboto_light);
        tv_help_group.setTypeface(roboto_light);
        tv_help_refresh.setTypeface(roboto_light);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_horario);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

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
                if(up2 && up3) {
                    Animation animation_fab = AnimationUtils.loadAnimation(HorarioActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation_fab);
                    fab.setClickable(false);
                    loadSchedule_ = new LoadSchedule();
                    loadSchedule_.execute();
                }else{
                    sendErrorToast(getResources().getString(R.string.verify));
                }
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

                    case R.id.action_help:

                        if(!ll_help.isShown()){
                            Animation animation = AnimationUtils.loadAnimation(HorarioActivity.this,
                                    R.anim.tv_alpha);
                            ll_help.setVisibility(View.VISIBLE);
                            ll_help.startAnimation(animation);
                        }


                        break;

                    case R.id.action_last_search:

                        dbHorario.openHorarioDatabase();
                        horario = dbHorario.loadHorario();
                        dbHorario.closeHorarioDatabase();

                        if(horario == null){
                            Toast toast5 = Toast.makeText(HorarioActivity.this,
                                    getResources().getString(R.string
                                            .data_save_not), Toast.LENGTH_SHORT);
                            toast5.setGravity(Gravity.BOTTOM, 0, 0);
                            toast5.show();
                        }else{
                            arrayAdapter = new ArrayAdapter<>(HorarioActivity.this, R.layout.list_item,horario);
                            gridview.setAdapter(arrayAdapter);
                            Animation anim = AnimationUtils.loadAnimation(HorarioActivity.this,
                                    R.anim.tv_alpha);
                            arrayAdapter.notifyDataSetChanged();
                            gridview.startAnimation(anim);
                        }

                        break;

                    case R.id.faculty:
                        new MaterialDialog.Builder(HorarioActivity.this)
                                .title(R.string.faculty_choose)
                                .items(R.array.faculties)
                                .theme(Theme.LIGHT)
                                .icon(getResources().getDrawable(R.drawable.ic_faculty_color))
                                .itemsCallbackSingleChoice(posicion,
                                        new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                if (posicion != which) {
                                                    up2 = false;
                                                    up3 = false;
                                                    posicion = which;
                                                } else {
                                                    posicion = which;
                                                    Log.d("!!!!!!!!!", "Same selected");
                                                }
                                                return true; // allow selection
                                            }
                                        })
                                .positiveText(R.string.accept)
                                .show();
                        break;

                    case R.id.week:
                       if(!up2){
                           progress = new MaterialDialog.Builder(HorarioActivity.this)
                                   .title(R.string.load_weeks)
                                   .content(R.string.please_wait)
                                   .cancelable(true)
                                   .theme(Theme.LIGHT)
                                   .icon(HorarioActivity.this.getResources().getDrawable(R
                                           .drawable.ic_week_color))
                                   .contentGravity(GravityEnum.CENTER)
                                   .progress(true, 0)
                                   .show();
                           posicion2 = 0;
                           loadWeeks_ = new LoadWeeks();
                           loadWeeks_.execute();
                       }else{

                           int count = stringsemanas.size();
                           CharSequence[] weeks = new CharSequence[count];
                           for(int i =0; i<count; i++){
                               weeks[i]= stringsemanas.get(i);
                           }

                           new MaterialDialog.Builder(HorarioActivity.this)
                                   .title(R.string.week_choose)
                                   .items(weeks)
                                   .theme(Theme.LIGHT)
                                   .icon(getResources().getDrawable(R.drawable.ic_week_color))
                                   .itemsCallbackSingleChoice(posicion2,
                                           new MaterialDialog.ListCallbackSingleChoice() {
                                       @Override
                                       public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                           posicion2 = which;
                                           return true; // allow selection
                                       }
                                   })
                                   .positiveText(R.string.accept)
                                   .show();
                       }
                        break;

                    case R.id.group:
                        if(!up2){
                            sendErrorToast(getResources().getString(R.string.week_first));
                        }else if(!up3) {
                            progress = new MaterialDialog.Builder(HorarioActivity.this)
                                    .title(R.string.load_groups)
                                    .content(R.string.please_wait)
                                    .theme(Theme.LIGHT)
                                    .cancelable(true)
                                    .icon(HorarioActivity.this.getResources().getDrawable(R
                                            .drawable.ic_group_color))
                                    .contentGravity(GravityEnum.CENTER)
                                    .progress(true, 0)
                                    .show();
                            posicion3 = 0;
                            loadGroups_ = new LoadGroups();
                            loadGroups_.execute();
                        }else{
                            int count = stringbrigadas.size();
                            CharSequence[] groups = new CharSequence[count];
                            for(int i =0; i<count; i++){
                                groups[i]= stringbrigadas.get(i);
                            }

                            new MaterialDialog.Builder(HorarioActivity.this)
                                    .title(R.string.group_choose)
                                    .items(groups)
                                    .theme(Theme.LIGHT)
                                    .icon(getResources().getDrawable(R.drawable.ic_group_color))
                                    .itemsCallbackSingleChoice(posicion3,
                                            new MaterialDialog.ListCallbackSingleChoice() {
                                                @Override
                                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                    posicion3 = which;
                                                    return true; // allow selection
                                                }
                                            })
                                    .positiveText(R.string.accept)
                                    .show();
                        }
                        break;

                }
                return true;
            }
        });

    }

    private void entry(){

        Animation animation_tv = AnimationUtils.loadAnimation(this, R.anim.tv_alpha);
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        mToolbar.startAnimation(animation_toolbar_in);
        shadow.startAnimation(animation_shadow_in);
        fab.startAnimation(animation_fab);

    }


    private boolean isInHome(){
        if(
                (MenuActivity.isInFront==null || MenuActivity.isInFront==false) &&
                        (CuotaActivity.isInFront==null || CuotaActivity.isInFront==false) &&
                        (RssActivity.isInFront==null || RssActivity.isInFront==false)&&
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


    public class LoadWeeks extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            stringsemanas = new LinkedList<>();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                Connect conexion = new Connect();
                String facultad = stringfacultades.get(posicion);

                int cant = conexion.cargarSemanas(facultad).length;
                for (int i = 0; i < cant; i++) {
                    stringsemanas.add(conexion.cargarSemanas(facultad)[i]);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            progress.dismiss();
            if(result){
                up2 = true;
                sendSuccessToast(getResources().getString(R.string.load_weeks_s));
                int count = stringsemanas.size();
                CharSequence[] weeks = new CharSequence[count];
                for(int i =0; i<count; i++){
                    weeks[i]= stringsemanas.get(i);
                }

                new MaterialDialog.Builder(HorarioActivity.this)
                        .title(R.string.week_choose)
                        .items(weeks)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_week_color))
                        .itemsCallbackSingleChoice(posicion2, new MaterialDialog.ListCallbackSingleChoice
                                () {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                posicion2 = which;
                                return true; // allow selection
                            }
                        })
                        .positiveText(R.string.accept)
                        .show();

            }else{
                sendErrorToast(getResources().getString(R.string.load_weeks));
            }
            //semanas.setAdapter(arraysemanas);

        }

    }

    public class LoadGroups extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            stringbrigadas = new LinkedList<>();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                Connect conexion = new Connect();
                String facultad = stringfacultades.get(posicion);

                // int cant = conexion.cargarSemanas(facultad).length;
                int cant2 = conexion.cargarBrigadas(facultad,
                        stringsemanas.get(posicion2)).length;

                for (int i = 0; i < cant2; i++) {
                    stringbrigadas.add(conexion.cargarBrigadas(facultad,
                            stringsemanas.get(posicion2))[i]);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            progress.dismiss();
            if(result){
                up3 = true;
                sendSuccessToast(getResources().getString(R.string.load_groups_s));
                int count = stringbrigadas.size();
                CharSequence[] groups = new CharSequence[count];
                for(int i =0; i<count; i++){
                    groups[i]= stringbrigadas.get(i);
                }

                new MaterialDialog.Builder(HorarioActivity.this)
                        .title(R.string.group_choose)
                        .items(groups)
                        .theme(Theme.LIGHT)
                        .icon(getResources().getDrawable(R.drawable.ic_group_color))
                        .itemsCallbackSingleChoice(posicion3, new MaterialDialog.ListCallbackSingleChoice
                                () {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                posicion3 = which;
                                return true; // allow selection
                            }
                        })
                        .positiveText(R.string.accept)
                        .show();
            }else{
                sendErrorToast(getResources().getString(R.string.load_groups));
            }
            //brigadas.setAdapter(arraybrigadas);
        }

    }

    public class LoadSchedule extends AsyncTask<Void, Void, Boolean> {

        MaterialDialog progress;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            stringhorario = new LinkedList<>();
            progress = new MaterialDialog.Builder(HorarioActivity.this)
                    .title(R.string.load_schedule)
                    .content(R.string.load_schedule_)
                    .theme(Theme.LIGHT)
                    .icon(HorarioActivity.this.getResources().getDrawable(R.drawable.ic_horario_nav))
                    .cancelable(true)
                    .contentGravity(GravityEnum.CENTER)
                    .progress(true, 0)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                Connect conexion = new Connect();

                String facultad = stringfacultades.get(posicion);
                String semana = stringsemanas.get(posicion2);
                String brigada = stringbrigadas.get(posicion3);

                int cant3 = conexion.MostrarHorario(facultad, semana, brigada).length;

                for (int i = 1; i < cant3; i++) {
                    stringhorario.add(conexion.MostrarHorario(facultad, semana,
                            brigada)[i]);
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            fab.clearAnimation();
            fab.setClickable(true);
            progress.dismiss();

            boolean activate;
            if(sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)){
                activate = sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                        false);
            }else{
                activate = true;
            }
            //sendNotification(result);
            if(result){
                horario = new String[stringhorario.size()];
                for (int i = 0; i < stringhorario.size(); i++) {
                    horario[i] = stringhorario.get(i);
                }
                arrayAdapter = new ArrayAdapter<>(HorarioActivity.this, R.layout.list_item,horario);
                gridview.setAdapter(arrayAdapter);
                Animation anim = AnimationUtils.loadAnimation(HorarioActivity.this,
                        R.anim.tv_alpha);
                arrayAdapter.notifyDataSetChanged();
                gridview.startAnimation(anim);
                Log.d("!!!!!","!!!!!DESCARGADO");

                dbHorario.openHorarioDatabase();
                dbHorario.saveHorario(horario);
                dbHorario.closeHorarioDatabase();

                if(activate){
                    sendNotification(result);
                }else{
                    if(!isInFront) {
                        sendSuccessToast(stringfacultades.get(posicion) + "," +
                                "" + stringbrigadas.get(posicion2));
                    }
                }

            }else{
                if(activate){
                    sendNotification(result);
                }else{
                    sendErrorToast(getResources().getString(R.string.horario_uci_title));
                }
            }
        }

    }

    private void sendErrorToast(String texto){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string
                .error_con) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_horario));

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
        t.setText(getResources().getString(R.string.download_ho ) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_horario));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();

    }

    private void sendNotification(Boolean aBoolean){
        String contentText ="";

        if(aBoolean && isInHome()){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_horario);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_horario_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.horarios));

            notificationCompatBuilder.setContentText(stringfacultades.get(posicion) + "," +
                    "" + stringbrigadas.get(posicion2));
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .horario_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(HorarioActivity.this, HorarioActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    HorarioActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_HORARIO,
                    notificationCompatBuilder.build());

        }else if(!aBoolean && isInHome()){

            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_horario);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_horario_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                    .horario_uci_title));

            notificationCompatBuilder.setContentText(getResources().getString(R.string
                    .error_con));
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .horario_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(HorarioActivity.this, HorarioActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    HorarioActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_HORARIO,
                    notificationCompatBuilder.build());
        }else{
            if(aBoolean && !isInFront){
                sendSuccessToast(stringfacultades.get(posicion) + "," +
                        "" + stringbrigadas.get(posicion2));
            }else if(!aBoolean && !isInHome()){
                sendErrorToast(getResources().getString(R.string.horario_uci_title));
            }
        }
    }

    private void setNotificationsDefaults(){
        boolean vibrate;
        boolean sound;
        boolean led;
        int defaults = 0;

        if(sharedPreferences.contains(CampusPreference.HORARIO_VIBRATE)){
            vibrate = sharedPreferences.getBoolean(CampusPreference.HORARIO_VIBRATE,
                    false);
        }else{
            vibrate = true;
        }

        if(sharedPreferences.contains(CampusPreference.HORARIO_SOUND)){
            sound = sharedPreferences.getBoolean(CampusPreference.HORARIO_SOUND,true);
        }else{
            sound = false;
        }

        if(sharedPreferences.contains(CampusPreference.HORARIO_LED)){
            led = sharedPreferences.getBoolean(CampusPreference.HORARIO_LED,true);
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

}
