package cu.uci.directorio;

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
import android.graphics.PorterDuff;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cu.uci.campusuci.AboutActivity;
import cu.uci.campusuci.CampusNotification;
import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.LoginUser;
import cu.uci.campusuci.MainActivity;
import cu.uci.campusuci.NavigationDrawer;
import cu.uci.campusuci.R;
import cu.uci.chatty.ChatActivity;
import cu.uci.cuota.CuotaActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.mapa.MapaActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.RssActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.SSL._FakeX509TrustManager;
import cu.uci.utils.views.CircleImageView;

public class DirectorioActivity extends ActionBarActivity implements SensorEventListener{

    public static Activity ctx;

    SensorManager sm;
    Sensor sensor;
    Boolean thread_running = false;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    public static NavigationDrawer drawer;
    public static Boolean notif;
    public static int notifType;

    Bitmap bm;

    AssetManager am;
    Typeface roboto_light, roboto_light_italic, roboto_condense_light;
    Toolbar mToolbar;
    ActionButton fab, fab_user, fab_credencial, fab_exp;
    TextView tvName, tvDni, tvCategory, tvBuilding, tvPhone, tvEmail, tvSexo, tvCredencial,
            tvProv_Mun, tvArea, tvUser, tvExp;
    ImageView ivName, ivDni, ivCategory, ivBuilding, ivPhone, ivEmail, ivSexo, ivCredencial,
            ivProv_Mun, ivArea, ivUser,ivExp, iv_fa_filter;
    CircleImageView login_picture;
    LinearLayout llName, llDni, llCategory, llBuilding, llPhone, llEmail, llSexo, llCredencial,
            llProv_Mun, llArea, llUser, llExp, llSearchView;
    EditText et_search;

    Persona persona;
    Persona temp;
    int filter;
    String who;
    Boolean my_result;
    Boolean areIn, isIn;
    Boolean drawerIn;
    public static Boolean isInFront;

    View shadow;

    public static LoginUser me;
    public static Bundle bundle;

    SearchPerson searchPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directorio);

        ctx = this;

        initToolBar_FAB();
        //initBundle();
        initTextViews();
        initImageViews();
        initLinearLayouts();
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
        }else{
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
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_DIRECTORIO);
        super.onStart();
    }

    @Override
    protected void onRestart() {
        //drawer.nav_directorio_stat.setVisibility(View.INVISIBLE);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        searchPerson.cancel(true);
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_DIRECTORIO);
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

    private void initToolBar_FAB(){

        notif = false;
        notifType = 0;
        isInFront = true;
        drawerIn = false;
        drawer = new NavigationDrawer();

        searchPerson = new SearchPerson();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        notificationCompatBuilder = new NotificationCompat.Builder(DirectorioActivity.this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        shadow = findViewById(R.id.shadow_toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            shadow.setMinimumHeight(4);
        }

        filter = 0;
        who = "";
        my_result = false;
        areIn = false;
        isIn = false;

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_directorio);
        mToolbar.setTitle(getResources().getString(R.string.directorio_uci_title));

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        fab = (ActionButton) findViewById(R.id.fab_refresh);
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
                who = et_search.getText().toString();
                if(who.length() == 0){
                  Animation pop = AnimationUtils.loadAnimation(DirectorioActivity.this,
                          R.anim.fab_pop);
                  fab.startAnimation(pop);
                  Toast toast = Toast.makeText(DirectorioActivity.this, DirectorioActivity.this.getResources
                            ().getString(R.string.no_blank), Toast.LENGTH_SHORT);
                  toast.setGravity(Gravity.BOTTOM, 0 ,0);
                  toast.show();
                }else{
                    thread_running = true;
                    Animation animation = AnimationUtils.loadAnimation(DirectorioActivity.this,
                            R.anim.fab_rotate);
                    fab.startAnimation(animation);
                    fab.setClickable(false);
                    searchPerson = new SearchPerson();
                    searchPerson.execute(filter);
                }

            }
        });

        fab_user = (ActionButton) findViewById(R.id.fab_usuario);
        fab_user.setType(ActionButton.Type.DEFAULT);
        fab_user.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab_user.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab_user.setImageResource(R.drawable.ic_user);
        fab_user.setShadowRadius(4.4f);
        fab_user.setShadowXOffset(2.8f);
        fab_user.setShadowYOffset(2.1f);
        fab_user.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab_user.setStrokeWidth(0.0f);

        fab_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = 0;
                Animation animation_scale = AnimationUtils.loadAnimation
                        (DirectorioActivity.this, R.anim.fab_scale_up);
                Animation animation_fab_out = AnimationUtils.loadAnimation
                        (DirectorioActivity.this, R.anim.fab_roll_to_right);

                fab.setVisibility(View.VISIBLE);
                fab.startAnimation(animation_scale);

                fab_exp.startAnimation(animation_fab_out);
                fab_user.startAnimation(animation_fab_out);
                fab_credencial.startAnimation(animation_fab_out);

                fab_exp.setVisibility(View.INVISIBLE);
                fab_user.setVisibility(View.INVISIBLE);
                fab_credencial.setVisibility(View.INVISIBLE);

                areIn = false;

                Toast toast = Toast.makeText(DirectorioActivity.this, DirectorioActivity.this.getResources()
                        .getString(R.string.search_user), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0 ,0);
                toast.show();
            }
        });

        fab_credencial = (ActionButton) findViewById(R.id.fab_credencial);
        fab_credencial.setType(ActionButton.Type.DEFAULT);
        fab_credencial.setButtonColor(getResources().getColor(R.color.amarillo));
        fab_credencial.setButtonColorPressed(getResources().getColor(R.color.amarillo_pressed));
        fab_credencial.setImageResource(R.drawable.ic_credencial);
        fab_credencial.setShadowRadius(4.4f);
        fab_credencial.setShadowXOffset(2.8f);
        fab_credencial.setShadowYOffset(2.1f);
        fab_credencial.setStrokeColor(getResources().getColor(R.color.amarillo));
        fab_credencial.setStrokeWidth(0.0f);

        fab_credencial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = 1;
                Animation animation_scale = AnimationUtils.loadAnimation
                        (DirectorioActivity.this, R.anim.fab_scale_up);
                Animation animation_fab_out = AnimationUtils.loadAnimation
                        (DirectorioActivity.this, R.anim.fab_roll_to_right);

                fab.setVisibility(View.VISIBLE);
                fab.startAnimation(animation_scale);

                fab_exp.startAnimation(animation_fab_out);
                fab_user.startAnimation(animation_fab_out);
                fab_credencial.startAnimation(animation_fab_out);

                fab_exp.setVisibility(View.INVISIBLE);
                fab_user.setVisibility(View.INVISIBLE);
                fab_credencial.setVisibility(View.INVISIBLE);

                areIn = false;

                Toast toast = Toast.makeText(DirectorioActivity.this, DirectorioActivity.this.getResources()
                        .getString(R.string.search_cred), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0 ,0);
                toast.show();
            }
        });

        fab_exp = (ActionButton) findViewById(R.id.fab_exp);
        fab_exp.setType(ActionButton.Type.DEFAULT);
        fab_exp.setButtonColor(getResources().getColor(R.color.primary_dark_color));
        fab_exp.setButtonColorPressed(getResources().getColor(R.color.primary_color));
        fab_exp.setImageResource(R.drawable.ic_exp);
        fab_exp.setShadowRadius(4.4f);
        fab_exp.setShadowXOffset(2.8f);
        fab_exp.setShadowYOffset(2.1f);
        fab_exp.setStrokeColor(getResources().getColor(R.color.primary_dark_color));
        fab_exp.setStrokeWidth(0.0f);

        fab_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = 2;
                Animation animation_scale = AnimationUtils.loadAnimation
                        (DirectorioActivity.this, R.anim.fab_scale_up);
                Animation animation_fab_out = AnimationUtils.loadAnimation
                        (DirectorioActivity.this, R.anim.fab_roll_to_right);

                fab.setVisibility(View.VISIBLE);
                fab.startAnimation(animation_scale);

                fab_exp.startAnimation(animation_fab_out);
                fab_user.startAnimation(animation_fab_out);
                fab_credencial.startAnimation(animation_fab_out);

                fab_exp.setVisibility(View.INVISIBLE);
                fab_user.setVisibility(View.INVISIBLE);
                fab_credencial.setVisibility(View.INVISIBLE);;

                areIn = false;

                Toast toast = Toast.makeText(DirectorioActivity.this, DirectorioActivity.this.getResources()
                        .getString(R.string.search_exp), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0 ,0);
                toast.show();

            }
        });

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
                switch (id) {

                    case R.id.action_add:
                            if(my_result){

                                Intent intent = new Intent(Intent.ACTION_INSERT);
                                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                                intent.putExtra(ContactsContract.Intents.Insert.NAME,
                                        temp.nombres_1 + " " + temp.apellidos_2);

                                if (!temp.corre_10.equalsIgnoreCase(DirectorioActivity.this
                                        .getResources().getString(R.string.no_disponible))) {

                                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL,
                                            temp.corre_10);
                                }

                                if (!temp.telefono_11_2.equalsIgnoreCase(DirectorioActivity.this
                                        .getResources().getString(R.string.no_disponible))) {

                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE,
                                            temp.telefono_11_2);
                                }

                                DirectorioActivity.this.startActivity(intent);

                            }else{
                                Toast toast = Toast.makeText(DirectorioActivity.this,
                                        DirectorioActivity.this.getResources().getString(R.string
                                                .no_result), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0 ,0);
                                toast.show();
                            }

                        break;
                }
                return true;
            }
        });

    }

    private void initTextViews(){

        am = getAssets();

        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.getBackground().setColorFilter(getResources().getColor(R.color.accent_color),
                PorterDuff.Mode.SRC_ATOP);

        tvName = (TextView) findViewById(R.id.tvName);
        tvDni = (TextView) findViewById(R.id.tvDni);
        tvCategory = (TextView)findViewById(R.id.tvCategory);
        tvBuilding = (TextView)findViewById(R.id.tvBuilding);
        tvPhone = (TextView)findViewById(R.id.tvPhone);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvSexo = (TextView)findViewById(R.id.tvSexo);
        tvCredencial = (TextView) findViewById(R.id.tvCredencial);
        tvProv_Mun = (TextView) findViewById(R.id.tvProv_Mun);
        tvUser = (TextView)findViewById(R.id.tvUser);
        tvArea = (TextView) findViewById(R.id.tvArea);
        tvExp = (TextView) findViewById(R.id.tvExp);

        tvName.setTypeface(roboto_condense_light);
        tvDni.setTypeface(roboto_condense_light);
        tvCategory.setTypeface(roboto_condense_light);
        tvBuilding.setTypeface(roboto_condense_light);
        tvPhone.setTypeface(roboto_condense_light);
        tvEmail.setTypeface(roboto_condense_light);
        tvSexo.setTypeface(roboto_condense_light);
        tvCredencial.setTypeface(roboto_condense_light);
        tvProv_Mun.setTypeface(roboto_condense_light);
        tvUser.setTypeface(roboto_condense_light);
        tvExp.setTypeface(roboto_condense_light);
        tvArea.setTypeface(roboto_condense_light);
        et_search.setTypeface(roboto_light_italic);


    }

    private void initImageViews(){

        ivName = (ImageView)findViewById(R.id.ivName);
        ivDni = (ImageView)findViewById(R.id.ivDni);
        ivCategory = (ImageView)findViewById(R.id.ivCategory);
        ivBuilding = (ImageView)findViewById(R.id.ivBuilding);
        ivPhone = (ImageView)findViewById(R.id.ivPhone);
        ivEmail = (ImageView)findViewById(R.id.ivEmail);
        ivSexo = (ImageView)findViewById(R.id.ivSexo);
        ivCredencial = (ImageView) findViewById(R.id.ivCredencial);
        ivProv_Mun = (ImageView) findViewById(R.id.ivProv_Mun);
        ivUser = (ImageView)findViewById(R.id.ivUser);
        ivArea = (ImageView) findViewById(R.id.ivArea);
        ivExp = (ImageView) findViewById(R.id.ivExp);

        login_picture = (CircleImageView) findViewById(R.id.login_picture);

        iv_fa_filter = (ImageView) findViewById(R.id.iv_fa_filter);
        iv_fa_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation pop = AnimationUtils.loadAnimation(DirectorioActivity.this, R.anim.fab_pop);
                iv_fa_filter.startAnimation(pop);

                if(!areIn){
                    Animation animation_scale_out = AnimationUtils.loadAnimation
                            (DirectorioActivity.this, R.anim.fab_fade_out);
                    Animation animation_fab_in = AnimationUtils.loadAnimation
                            (DirectorioActivity.this, R.anim.fab_roll_from_right);

                    fab.startAnimation(animation_scale_out);
                    fab.setVisibility(View.INVISIBLE);

                    fab_exp.setVisibility(View.VISIBLE);
                    fab_user.setVisibility(View.VISIBLE);
                    fab_credencial.setVisibility(View.VISIBLE);

                    fab_exp.startAnimation(animation_fab_in);
                    fab_user.startAnimation(animation_fab_in);
                    fab_credencial.startAnimation(animation_fab_in);

                    areIn = true;
                }else{

                    Animation animation_scale = AnimationUtils.loadAnimation
                            (DirectorioActivity.this, R.anim.fab_scale_up);
                    Animation animation_fab_out = AnimationUtils.loadAnimation
                            (DirectorioActivity.this, R.anim.fab_roll_to_right);

                    fab.setVisibility(View.VISIBLE);
                    fab.startAnimation(animation_scale);

                    fab_exp.startAnimation(animation_fab_out);
                    fab_user.startAnimation(animation_fab_out);
                    fab_credencial.startAnimation(animation_fab_out);

                    fab_exp.setVisibility(View.INVISIBLE);
                    fab_user.setVisibility(View.INVISIBLE);
                    fab_credencial.setVisibility(View.INVISIBLE);

                    areIn = false;

                }

            }
        });

    }

    private void initLinearLayouts(){

        llName = (LinearLayout) findViewById(R.id.llName);
        llDni = (LinearLayout) findViewById(R.id.llDni);
        llCategory = (LinearLayout) findViewById(R.id.llCategory);
        llBuilding = (LinearLayout) findViewById(R.id.llBuilding);
        llPhone = (LinearLayout) findViewById(R.id.llPhone);
        llEmail = (LinearLayout) findViewById(R.id.llEmail);
        llSexo = (LinearLayout) findViewById(R.id.llSexo);
        llCredencial = (LinearLayout) findViewById(R.id.llCredencial);
        llProv_Mun = (LinearLayout) findViewById(R.id.llProv_Mun);
        llUser = (LinearLayout) findViewById(R.id.llUser);
        llArea = (LinearLayout) findViewById(R.id.llArea);
        llExp = (LinearLayout) findViewById(R.id.llExp);

        llSearchView = (LinearLayout) findViewById(R.id.llSearchView);

    }

    private void entry(){

        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
        Animation animation_alpha = AnimationUtils.loadAnimation(this, R.anim.tv_alpha);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_search_in = AnimationUtils.loadAnimation(this, R.anim.search_in);
        Animation animation_et_in = AnimationUtils.loadAnimation(this, R.anim.et_entry);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        fab.startAnimation(animation_fab);

        tvName.startAnimation(animation_alpha);
        tvDni.startAnimation(animation_alpha);
        tvCategory.startAnimation(animation_alpha);
        tvBuilding.startAnimation(animation_alpha);
        tvPhone.startAnimation(animation_alpha);
        tvEmail.startAnimation(animation_alpha);
        tvSexo.startAnimation(animation_alpha);
        tvCredencial.startAnimation(animation_alpha);
        tvProv_Mun.startAnimation(animation_alpha);
        tvUser.startAnimation(animation_alpha);
        tvArea.startAnimation(animation_alpha);
        tvExp.startAnimation(animation_alpha);
        et_search.startAnimation(animation_et_in);

        mToolbar.startAnimation(animation_toolbar_in);
        shadow.startAnimation(animation_shadow_in);

        ivName.startAnimation(animation_alpha);
        ivDni.startAnimation(animation_alpha);
        ivCategory.startAnimation(animation_alpha);
        ivBuilding.startAnimation(animation_alpha);
        ivPhone.startAnimation(animation_alpha);
        ivEmail.startAnimation(animation_alpha);
        ivSexo.startAnimation(animation_alpha);
        ivCredencial.startAnimation(animation_alpha);
        ivProv_Mun.startAnimation(animation_alpha);
        ivUser.startAnimation(animation_alpha);
        ivArea.startAnimation(animation_alpha);
        ivExp.startAnimation(animation_alpha);

        llSearchView.startAnimation(animation_search_in);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directorio, menu);
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
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else if (areIn){
            Animation animation_scale = AnimationUtils.loadAnimation
                    (DirectorioActivity.this, R.anim.fab_scale_up);
            Animation animation_fab_out = AnimationUtils.loadAnimation
                    (DirectorioActivity.this, R.anim.fab_roll_to_right);

            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(animation_scale);

            fab_exp.startAnimation(animation_fab_out);
            fab_user.startAnimation(animation_fab_out);
            fab_credencial.startAnimation(animation_fab_out);

            fab_exp.setVisibility(View.INVISIBLE);
            fab_user.setVisibility(View.INVISIBLE);
            fab_credencial.setVisibility(View.INVISIBLE);

            areIn = false;
        }else{
            Intent i1 = new Intent(DirectorioActivity.this,
                    MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
            //super.onBackPressed();
        }

    }

    private void goBack(){
        super.onBackPressed();
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
                    who = et_search.getText().toString();
                    if (who.length() == 0) {
                        Animation pop = AnimationUtils.loadAnimation(DirectorioActivity.this,
                                R.anim.fab_pop);
                        fab.startAnimation(pop);
                        Toast toast = Toast.makeText(DirectorioActivity.this, DirectorioActivity.this.getResources
                                ().getString(R.string.no_blank), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    } else {
                        thread_running = true;
                        Animation animation = AnimationUtils.loadAnimation(DirectorioActivity.this,
                                R.anim.fab_rotate);
                        fab.startAnimation(animation);
                        fab.setClickable(false);
                        searchPerson = new SearchPerson();
                        searchPerson.execute(filter);
                    }
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class SearchPerson extends AsyncTask<Integer, Integer, Boolean>{

        private final String NAMESPACE = "https://autenticacion2.uci.cu/v6/";
        private final String URL = "https://autenticacion2.uci.cu/v6/PasarelaAutenticacionWS.php";
        private final String METHOD_NAME = "ObtenerPersonaDadoUsuario";
        private final String SOAP_ACTION = "https://autenticacion2.uci.cu/v6/ObtenerPersonaDadoUsuario";

        private final String NAMESPACE1 = "https://autenticacion2.uci.cu/v6/";
        private final String URL1 = "https://autenticacion2.uci.cu/v6/PasarelaAutenticacionWS.php";
        private final String METHOD_NAME1 = "ObtenerPersonaDadoCredencial";
        private final String SOAP_ACTION1 = "https://autenticacion2.uci.cu/v6/ObtenerPersonaDadoCredencial";

        private final String NAMESPACE2 = "https://autenticacion2.uci.cu/v6/";
        private final String URL2 = "https://autenticacion2.uci.cu/v6/PasarelaAutenticacionWS.php";
        private final String METHOD_NAME2 = "ObtenerPersonaDadoIdExpediente";
        private final String SOAP_ACTION2 = "https://autenticacion2.uci.cu/v6/ObtenerPersonaDadoIdExpediente";

        String error;
        Boolean result;

        public SearchPerson() {
            this.error = "Error de Conexión";
            this.result = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            _FakeX509TrustManager.allowAllSSL();
            int f = params[0];
            switch (f){
                case 0:
                    this.result = obtenerDadoUsuario(who);
                  break;

                case 1:
                    this.result = obtenerDadoCredencial(who);
                    break;

                case 2:
                    this.result = obtenerDadoIdExp(who);
                    break;
            }


            return this.result;

        }



        @Override
        protected void onPostExecute(Boolean aBoolean) {
            thread_running = false;
            fab.clearAnimation();
            Animation pop = AnimationUtils.loadAnimation(DirectorioActivity.this, R.anim.fab_pop);
            fab.startAnimation(pop);
            fab.setClickable(true);

            boolean activate;
            if(sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)){
                activate = sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                        false);
            }else{
                activate = true;
            }

            if(aBoolean){
                //sendNotification(aBoolean, persona.nombres_1 + " " + persona.apellidos_2);
                my_result = true;
                temp = new Persona();
                temp = persona;

                Picasso.with(DirectorioActivity.this).load(persona.foto_13_1).into(login_picture);
                Animation animation_alpha = AnimationUtils.loadAnimation(DirectorioActivity.this,
                        R.anim.tv_alpha);
                Animation animation_scale = AnimationUtils.loadAnimation(DirectorioActivity.this,
                        R.anim.fab_scale);

                tvName.setText(persona.nombres_1 + " " + persona.apellidos_2);
                tvDni.setText(persona.ci_5);
                tvCategory.setText(persona.categoria_6);
                tvCredencial.setText(persona.credencial_3);
                tvProv_Mun.setText(persona.provincia_4_2_1 + "," + persona.municipio_4_1);
                tvBuilding.setText(persona.apto_11_1);
                tvPhone.setText(persona.telefono_11_2);
                tvEmail.setText(persona.corre_10);
                tvSexo.setText(persona.sexo_8_1);
                tvUser.setText(persona.usuario_9);
                tvArea.setText(persona.area_12_1);
                tvExp.setText(persona.exp_0);

                tvName.startAnimation(animation_alpha);
                tvDni.startAnimation(animation_alpha);
                tvCategory.startAnimation(animation_alpha);
                tvBuilding.startAnimation(animation_alpha);
                tvPhone.startAnimation(animation_alpha);
                tvEmail.startAnimation(animation_alpha);
                tvSexo.startAnimation(animation_alpha);
                tvCredencial.startAnimation(animation_alpha);
                tvProv_Mun.startAnimation(animation_alpha);
                tvUser.startAnimation(animation_alpha);
                tvExp.startAnimation(animation_alpha);
                tvArea.startAnimation(animation_alpha);
                login_picture.startAnimation(animation_scale);

                if(activate){
                    sendNotification(aBoolean, persona.nombres_1 + " " + persona.apellidos_2);
                }else{
                    if(!isInFront) {
                        sendSuccessToast(persona.nombres_1 + " " + persona.apellidos_2);
                    }
                }

            }else{
                //sendNotification(aBoolean, "");
                if(activate){
                    sendNotification(aBoolean, "");
                }else{
                    if(!isInFront) {
                        sendErrorToast();
                    }
                }
            }
            super.onPostExecute(aBoolean);
        }

        private Boolean obtenerDadoCredencial(String cred){

            SoapObject request;

            request = new SoapObject(NAMESPACE1, METHOD_NAME1);
            request.addProperty("Credencial", cred.toUpperCase());

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL1);

            try {

                SoapObject aux4;
                SoapObject aux4_2;
                SoapObject aux8;
                SoapObject aux11;
                SoapObject aux12;
                SoapObject aux13;

                transporte.call(SOAP_ACTION1,envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                persona = new Persona();

                persona.nombres_1 = String.valueOf(resSoap.getProperty(1).toString());

                if(persona.nombres_1.equalsIgnoreCase("anyType{}")){
                    error = DirectorioActivity.this.getResources().getString(R.string.error_cred);
                    return false;
                }
                persona.exp_0 = String.valueOf(resSoap.getProperty(0).toString());
                if(persona.exp_0.equalsIgnoreCase("anyType{}")){
                    persona.exp_0 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.apellidos_2 = String.valueOf(resSoap.getProperty(2).toString());
                persona.credencial_3 = String.valueOf(resSoap.getProperty(3).toString());
                if(persona.credencial_3.equalsIgnoreCase("anyType{}")){
                    persona.credencial_3 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.corre_10 = String.valueOf(resSoap.getProperty(10).toString());
                if(persona.corre_10.equalsIgnoreCase("anyType{}")){
                    persona.corre_10 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.ci_5 = String.valueOf(resSoap.getProperty(5).toString());
                if(persona.ci_5.equalsIgnoreCase("anyType{}")){
                    persona.ci_5 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.categoria_6 = String.valueOf(resSoap.getProperty(6).toString());
                if(persona.categoria_6.equalsIgnoreCase("anyType{}")){
                    persona.categoria_6 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }

                aux4 = (SoapObject) resSoap.getProperty(4);
                aux4_2 = (SoapObject) aux4.getProperty(2);
                persona.municipio_4_1 = String.valueOf(aux4.getProperty(1).toString());
                if(persona.municipio_4_1.equalsIgnoreCase("anyType{}")){
                    persona.municipio_4_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.provincia_4_2_1 = String.valueOf(aux4_2.getProperty(1).toString());
                if(persona.provincia_4_2_1.equalsIgnoreCase("anyType{}")){
                    persona.provincia_4_2_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux8 = (SoapObject) resSoap.getProperty(8);
                persona.sexo_8_1 = String.valueOf(aux8.getProperty(1).toString());
                if(persona.sexo_8_1.equalsIgnoreCase("anyType{}")){
                    persona.sexo_8_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.usuario_9 = String.valueOf(resSoap.getProperty(9).toString());
                if(persona.usuario_9.equalsIgnoreCase("anyType{}")){
                    persona.usuario_9 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux11 = (SoapObject) resSoap.getProperty(11);
                persona.edificio_11_0 = String.valueOf(aux11.getProperty(0).toString());
                if(persona.edificio_11_0.equalsIgnoreCase("anyType{}")){
                    persona.edificio_11_0 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.apto_11_1 = String.valueOf(aux11.getProperty(1).toString());
                if(persona.apto_11_1.equalsIgnoreCase("anyType{}")){
                    persona.apto_11_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.telefono_11_2 = String.valueOf(aux11.getProperty(2).toString());
                if(persona.telefono_11_2.equalsIgnoreCase("anyType{}")){
                    persona.telefono_11_2 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux12 = (SoapObject) resSoap.getProperty(12);
                persona.area_12_1 = String.valueOf(aux12.getProperty(1).toString());
                if(persona.area_12_1.equalsIgnoreCase("anyType{}")){
                    persona.area_12_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux13 = (SoapObject) resSoap.getProperty(13);
                persona.foto_13_1 = String.valueOf(aux13.getProperty(1).toString());
                bm = Picasso.with(DirectorioActivity.this).load(persona.foto_13_1).get();

                Log.d("!!!!!!!!!", persona.toString());

            } catch (IOException e) {
                return false;
            } catch (XmlPullParserException e) {
                return false;
            }

            return true;
        }

        private Boolean obtenerDadoIdExp(String exp){

            SoapObject request;

            request = new SoapObject(NAMESPACE2, METHOD_NAME2);
            request.addProperty("Usuario", exp.toUpperCase());

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL2);

            try {

                SoapObject aux4;
                SoapObject aux4_2;
                SoapObject aux8;
                SoapObject aux11;
                SoapObject aux12;
                SoapObject aux13;

                transporte.call(SOAP_ACTION2,envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                persona = new Persona();

                persona.nombres_1 = String.valueOf(resSoap.getProperty(1).toString());

                if(persona.nombres_1.equalsIgnoreCase("anyType{}")){
                    error = DirectorioActivity.this.getResources().getString(R.string.error_exp);
                    return false;
                }
                persona.exp_0 = String.valueOf(resSoap.getProperty(0).toString());
                if(persona.exp_0.equalsIgnoreCase("anyType{}")){
                    persona.exp_0 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.apellidos_2 = String.valueOf(resSoap.getProperty(2).toString());
                persona.credencial_3 = String.valueOf(resSoap.getProperty(3).toString());
                if(persona.credencial_3.equalsIgnoreCase("anyType{}")){
                    persona.credencial_3 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.corre_10 = String.valueOf(resSoap.getProperty(10).toString());
                if(persona.corre_10.equalsIgnoreCase("anyType{}")){
                    persona.corre_10 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.ci_5 = String.valueOf(resSoap.getProperty(5).toString());
                if(persona.ci_5.equalsIgnoreCase("anyType{}")){
                    persona.ci_5 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.categoria_6 = String.valueOf(resSoap.getProperty(6).toString());
                if(persona.categoria_6.equalsIgnoreCase("anyType{}")){
                    persona.categoria_6 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }

                aux4 = (SoapObject) resSoap.getProperty(4);
                aux4_2 = (SoapObject) aux4.getProperty(2);
                persona.municipio_4_1 = String.valueOf(aux4.getProperty(1).toString());
                if(persona.municipio_4_1.equalsIgnoreCase("anyType{}")){
                    persona.municipio_4_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.provincia_4_2_1 = String.valueOf(aux4_2.getProperty(1).toString());
                if(persona.provincia_4_2_1.equalsIgnoreCase("anyType{}")){
                    persona.provincia_4_2_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux8 = (SoapObject) resSoap.getProperty(8);
                persona.sexo_8_1 = String.valueOf(aux8.getProperty(1).toString());
                if(persona.sexo_8_1.equalsIgnoreCase("anyType{}")){
                    persona.sexo_8_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.usuario_9 = String.valueOf(resSoap.getProperty(9).toString());
                if(persona.usuario_9.equalsIgnoreCase("anyType{}")){
                    persona.usuario_9 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux11 = (SoapObject) resSoap.getProperty(11);
                persona.edificio_11_0 = String.valueOf(aux11.getProperty(0).toString());
                if(persona.edificio_11_0.equalsIgnoreCase("anyType{}")){
                    persona.edificio_11_0 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.apto_11_1 = String.valueOf(aux11.getProperty(1).toString());
                if(persona.apto_11_1.equalsIgnoreCase("anyType{}")){
                    persona.apto_11_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.telefono_11_2 = String.valueOf(aux11.getProperty(2).toString());
                if(persona.telefono_11_2.equalsIgnoreCase("anyType{}")){
                    persona.telefono_11_2 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux12 = (SoapObject) resSoap.getProperty(12);
                persona.area_12_1 = String.valueOf(aux12.getProperty(1).toString());
                if(persona.area_12_1.equalsIgnoreCase("anyType{}")){
                    persona.area_12_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux13 = (SoapObject) resSoap.getProperty(13);
                persona.foto_13_1 = String.valueOf(aux13.getProperty(1).toString());
                bm = Picasso.with(DirectorioActivity.this).load(persona.foto_13_1).get();

                Log.d("!!!!!!!!!", persona.toString());

            } catch (IOException e) {
                return false;
            } catch (XmlPullParserException e) {
                return false;
            }

            return true;
        }

        private Boolean obtenerDadoUsuario(String user){

            SoapObject request;

            request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Usuario", user);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {

                SoapObject aux4;
                SoapObject aux4_2;
                SoapObject aux8;
                SoapObject aux11;
                SoapObject aux12;
                SoapObject aux13;

                transporte.call(SOAP_ACTION,envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                persona = new Persona();

                persona.nombres_1 = String.valueOf(resSoap.getProperty(1).toString());

                if(persona.nombres_1.equalsIgnoreCase("anyType{}")){
                    error = DirectorioActivity.this.getResources().getString(R.string.error_user);
                    return false;
                }
                persona.exp_0 = String.valueOf(resSoap.getProperty(0).toString());
                if(persona.exp_0.equalsIgnoreCase("anyType{}")){
                    persona.exp_0 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.apellidos_2 = String.valueOf(resSoap.getProperty(2).toString());
                persona.credencial_3 = String.valueOf(resSoap.getProperty(3).toString());
                if(persona.credencial_3.equalsIgnoreCase("anyType{}")){
                    persona.credencial_3 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.corre_10 = String.valueOf(resSoap.getProperty(10).toString());
                if(persona.corre_10.equalsIgnoreCase("anyType{}")){
                    persona.corre_10 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.ci_5 = String.valueOf(resSoap.getProperty(5).toString());
                if(persona.ci_5.equalsIgnoreCase("anyType{}")){
                    persona.ci_5 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.categoria_6 = String.valueOf(resSoap.getProperty(6).toString());
                if(persona.categoria_6.equalsIgnoreCase("anyType{}")){
                    persona.categoria_6 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }

                aux4 = (SoapObject) resSoap.getProperty(4);
                aux4_2 = (SoapObject) aux4.getProperty(2);
                persona.municipio_4_1 = String.valueOf(aux4.getProperty(1).toString());
                if(persona.municipio_4_1.equalsIgnoreCase("anyType{}")){
                    persona.municipio_4_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.provincia_4_2_1 = String.valueOf(aux4_2.getProperty(1).toString());
                if(persona.provincia_4_2_1.equalsIgnoreCase("anyType{}")){
                    persona.provincia_4_2_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux8 = (SoapObject) resSoap.getProperty(8);
                persona.sexo_8_1 = String.valueOf(aux8.getProperty(1).toString());
                if(persona.sexo_8_1.equalsIgnoreCase("anyType{}")){
                    persona.sexo_8_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.usuario_9 = String.valueOf(resSoap.getProperty(9).toString());
                if(persona.usuario_9.equalsIgnoreCase("anyType{}")){
                    persona.usuario_9 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux11 = (SoapObject) resSoap.getProperty(11);
                persona.edificio_11_0 = String.valueOf(aux11.getProperty(0).toString());
                if(persona.edificio_11_0.equalsIgnoreCase("anyType{}")){
                    persona.edificio_11_0 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.apto_11_1 = String.valueOf(aux11.getProperty(1).toString());
                if(persona.apto_11_1.equalsIgnoreCase("anyType{}")){
                    persona.apto_11_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                persona.telefono_11_2 = String.valueOf(aux11.getProperty(2).toString());
                if(persona.telefono_11_2.equalsIgnoreCase("anyType{}")){
                    persona.telefono_11_2 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux12 = (SoapObject) resSoap.getProperty(12);
                persona.area_12_1 = String.valueOf(aux12.getProperty(1).toString());
                if(persona.area_12_1.equalsIgnoreCase("anyType{}")){
                    persona.area_12_1 = DirectorioActivity.this.getResources().getString(R
                            .string.no_disponible);
                }
                aux13 = (SoapObject) resSoap.getProperty(13);
                persona.foto_13_1 = String.valueOf(aux13.getProperty(1).toString());
                bm = Picasso.with(DirectorioActivity.this).load(persona.foto_13_1).get();

                Log.d("!!!!!!!!!", persona.toString());

            } catch (IOException e) {
                return false;
            } catch (XmlPullParserException e) {
                return false;
            }

            return true;
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
                .directorio_uci_title) + "\n" + getResources().getString(R.string
                .error_con));

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_directorio));

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
        t.setText(t.getText().toString() + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageBitmap(bm);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();

    }

    private boolean isInHome(){
        if(
                (MenuActivity.isInFront==null || MenuActivity.isInFront==false) &&
                        (CuotaActivity.isInFront==null || CuotaActivity.isInFront==false) &&
                        (HorarioActivity.isInFront==null || HorarioActivity.isInFront==false)&&
                        (MapaActivity.isInFront==null || MapaActivity.isInFront==false) &&
                        (AboutActivity.isInFront==null || AboutActivity.isInFront==false)&&
                        (MainActivity.isInFront==null || MainActivity.isInFront==false) &&
                        (RssActivity.isInFront==null || DirectorioActivity.isInFront==false) &&
                        (ChatActivity.isInFront==null || ChatActivity.isInFront==false) &&
                        (isInFront==null || isInFront==false)){

            return true;
        }

        return false;
    }


    private void sendNotification(Boolean aBoolean, String content){
        String contentText = content;

        if(aBoolean && isInHome()){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_dir);
            notificationCompatBuilder.setLargeIcon(redimensionarImagenMaximo(bm, 72, 72));
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.users));

            notificationCompatBuilder.setContentText(contentText);
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .directorio_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(DirectorioActivity.this, DirectorioActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    DirectorioActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_DIRECTORIO,notificationCompatBuilder.build());

        }else if(!aBoolean && isInHome()){

            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_dir);
            notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_directorio_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                    .directorio_uci_title));

            notificationCompatBuilder.setContentText(getResources().getString(R.string
                    .error_con));
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .directorio_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(DirectorioActivity.this, DirectorioActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    DirectorioActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_DIRECTORIO,notificationCompatBuilder.build());
        }else{
            if(aBoolean && !isInFront){
                sendSuccessToast(persona.nombres_1 + " " + persona.apellidos_2);
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

        if(sharedPreferences.contains(CampusPreference.DIRECTORIO_VIBRATE)){
            vibrate = sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_VIBRATE,
                    false);
        }else{
            vibrate = true;
        }

        if(sharedPreferences.contains(CampusPreference.DIRECTORIO_SOUND)){
            sound = sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_SOUND,true);
        }else{
            sound = false;
        }

        if(sharedPreferences.contains(CampusPreference.DIRECTORIO_LED)){
            led = sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_LED,true);
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

}
