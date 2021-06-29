package cu.uci.campusuci;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import cu.uci.cuota.CuotaActivity;
import cu.uci.directorio.DirectorioActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.market.MarketActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.MyListAdapter2;
import cu.uci.rss.RssActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;

/**
 * Created by Yannier on 4/15/2015.
 */
public class PreferenceActivity extends ActionBarActivity {

    AssetManager am;
    SensorManager sm;
    Sensor sensor;

    Typeface roboto_light, roboto_light_italic, roboto_condense_light;
    ActionButton fab;
    TextView general_sett, cuota_sett, rss_sett, dir_sett, menu_sett, horario_sett, rss_pref,
            menu_pref, rss_what, cuota_time, chat_sett, chat_host_tv, chat_service_tv, chat_port_tv;
    CheckBox general_activate_notif, general_close_alert, log_in_on_start, exit_anim, cuota_vib,
            cuota_sound,
            cuota_led,
            rss_vib, rss_sound,
            rss_led, menu_vib, menu_sound, menu_led, dir_vib, dir_sound, dir_led,
            horario_vib, horario_sound, horario_led, rss_load, menu_load, general_delete_alert,
            general_proximity, cuota_load, rss_load_select, cuota_always, cuota_animations,
            chat_vib, chat_sound, chat_led, rss_animate_news;
    LinearLayout ll_rss_pref, ll_menu_pref, ll_rss_what, ll_cuota_time;
    ImageView iv_pref_rss, iv_pref_menu;
    EditText chat_host,chat_service,chat_port;

    int cuota_selected = 0;

    int rss_selected = 0;
    int rss_what_ = 0;
    String[] rss_url;

    int[] imgs_rss = new int[]{
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

    int[] imgs_menu = new int[]{
            R.drawable.dragon3s1,
            R.drawable.octavito,
            R.drawable.zorros,
            R.drawable.grundys
    };

    int menu_selected_value = 0;
    String[] menu_url;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_layout);

        initActionBar_Fab();
        initTextViews();
        initSettings();
        initSettingsView();

        setStatusBarColor();
    }

    public void setStatusBarColor(){
        //LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
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
            //lx.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
        //super.onBackPressed();
    }

    private void initSettings() {
        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        //Activar Notificaciones
        if (sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)) {
            general_activate_notif.setChecked(sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                    false));
        } else {
            editor.putBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS, true);
            editor.commit();
            general_activate_notif.setChecked(sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                    false));
        }

        //Alerta de cierre
        if (sharedPreferences.contains(CampusPreference.CLOSE_ALERTS)) {
            general_close_alert.setChecked(sharedPreferences.getBoolean(CampusPreference.CLOSE_ALERTS,
                    false));
        } else {
            editor.putBoolean(CampusPreference.CLOSE_ALERTS, true);
            editor.commit();
            general_close_alert.setChecked(sharedPreferences.getBoolean(CampusPreference.CLOSE_ALERTS,
                    false));
        }

        //Alerta de borrados
        if (sharedPreferences.contains(CampusPreference.DELETE_ALERTS)) {
            general_delete_alert.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .DELETE_ALERTS,
                    false));
        } else {
            editor.putBoolean(CampusPreference.DELETE_ALERTS, true);
            editor.commit();
            general_delete_alert.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .DELETE_ALERTS,
                    false));
        }

        //Proximidad

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (sharedPreferences.contains(CampusPreference.PROXIMITY_UPDATE)) {
            general_proximity.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .PROXIMITY_UPDATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.PROXIMITY_UPDATE, false);
            editor.commit();
            general_proximity.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .PROXIMITY_UPDATE,
                    false));
        }

        if (sensor == null){
            general_proximity.setVisibility(View.GONE);
        }

        if (sharedPreferences.contains(CampusPreference.LOG_IN_ON_START)) {
            log_in_on_start.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .LOG_IN_ON_START,
                    false));
        } else {
            editor.putBoolean(CampusPreference.LOG_IN_ON_START, false);
            editor.commit();
            log_in_on_start.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .LOG_IN_ON_START,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.ANIMATION_TO_EXIT)) {
            exit_anim.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .ANIMATION_TO_EXIT,
                    false));
        } else {
            editor.putBoolean(CampusPreference.ANIMATION_TO_EXIT, true);
            editor.commit();
            exit_anim.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .ANIMATION_TO_EXIT,
                    false));
        }

        //Cuota Notificaciones
        if (sharedPreferences.contains(CampusPreference.CUOTA_VIBRATE)) {
            cuota_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_VIBRATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.CUOTA_VIBRATE, true);
            editor.commit();
            cuota_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_VIBRATE,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CUOTA_SOUND)) {
            cuota_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_SOUND, true));
        } else {
            editor.putBoolean(CampusPreference.CUOTA_SOUND, false);
            editor.commit();
            cuota_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_SOUND,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CUOTA_LED)) {
            cuota_led.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_LED, true));
        } else {
            editor.putBoolean(CampusPreference.CUOTA_LED, false);
            editor.commit();
            cuota_led.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_LED,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CUOTA_LOAD)) {
            cuota_load.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_LOAD, true));
        } else {
            editor.putBoolean(CampusPreference.CUOTA_LOAD, false);
            editor.commit();
            cuota_load.setChecked(sharedPreferences.getBoolean(CampusPreference.CUOTA_LOAD,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CUOTA_REFRESH_TIME)) {
            cuota_selected = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
        } else {
            editor.putInt(CampusPreference.CUOTA_REFRESH_TIME, 0);
            editor.commit();
            cuota_selected = sharedPreferences.getInt(CampusPreference.CUOTA_REFRESH_TIME,
                    0);
        }

        if (sharedPreferences.contains(CampusPreference.CUOTA_ALWAYS_IN_NOTIFICATION_BAR)) {
            cuota_always.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ALWAYS_IN_NOTIFICATION_BAR,
                    false));
        } else {
            editor.putBoolean(CampusPreference.CUOTA_ALWAYS_IN_NOTIFICATION_BAR, false);
            editor.commit();
            cuota_always.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ALWAYS_IN_NOTIFICATION_BAR,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CUOTA_ANIMATIONS)) {
            cuota_animations.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ANIMATIONS,
                    false));
        } else {
            editor.putBoolean(CampusPreference.CUOTA_ANIMATIONS, true);
            editor.commit();
            cuota_animations.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .CUOTA_ANIMATIONS,
                    false));
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            cuota_animations.setVisibility(View.GONE);
        }

        //Rss Notificaciones
        if (sharedPreferences.contains(CampusPreference.RSS_VIBRATE)) {
            rss_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_VIBRATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.RSS_VIBRATE, true);
            editor.commit();
            rss_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_VIBRATE,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.RSS_SOUND)) {
            rss_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_SOUND, true));
        } else {
            editor.putBoolean(CampusPreference.RSS_SOUND, false);
            editor.commit();
            rss_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_SOUND,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.RSS_LED)) {
            rss_led.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_LED, true));
        } else {
            editor.putBoolean(CampusPreference.RSS_LED, false);
            editor.commit();
            rss_led.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_LED,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.RSS_LOAD)) {
            rss_load.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_LOAD, true));
        } else {
            editor.putBoolean(CampusPreference.RSS_LOAD, false);
            editor.commit();
            rss_load.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_LOAD,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.RSS_PREF)) {
            rss_selected = sharedPreferences.getInt(CampusPreference.RSS_PREF, 0);
        } else {
            editor.putInt(CampusPreference.RSS_PREF, 0);
            editor.commit();
            rss_selected = sharedPreferences.getInt(CampusPreference.RSS_PREF,
                    0);
        }

        if (sharedPreferences.contains(CampusPreference.RSS_WHAT)) {
            rss_what_ = sharedPreferences.getInt(CampusPreference.RSS_WHAT, 0);
        } else {
            editor.putInt(CampusPreference.RSS_WHAT, 0);
            editor.commit();
            rss_what_ = sharedPreferences.getInt(CampusPreference.RSS_WHAT,
                    0);
        }

        if (sharedPreferences.contains(CampusPreference.RSS_LOAD_SELECT)) {
            rss_load_select.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_LOAD_SELECT,
                    false));
        } else {
            editor.putBoolean(CampusPreference.RSS_LOAD_SELECT, false);
            editor.commit();
            rss_load_select.setChecked(sharedPreferences.getBoolean(CampusPreference.RSS_LOAD_SELECT,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.ANIMATE_NEWS_ENTRY)) {
            rss_animate_news.setChecked(sharedPreferences.getBoolean(CampusPreference.ANIMATE_NEWS_ENTRY,
                    false));
        } else {
            editor.putBoolean(CampusPreference.ANIMATE_NEWS_ENTRY, true);
            editor.commit();
            rss_animate_news.setChecked(sharedPreferences.getBoolean(CampusPreference.ANIMATE_NEWS_ENTRY,
                    false));
        }

        //Directorio Notificaciones
        if (sharedPreferences.contains(CampusPreference.DIRECTORIO_VIBRATE)) {
            dir_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_VIBRATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.DIRECTORIO_VIBRATE, true);
            editor.commit();
            dir_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_VIBRATE,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.DIRECTORIO_SOUND)) {
            dir_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_SOUND, true));
        } else {
            editor.putBoolean(CampusPreference.DIRECTORIO_SOUND, false);
            editor.commit();
            dir_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_SOUND,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.DIRECTORIO_LED)) {
            dir_led.setChecked(sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_LED, true));
        } else {
            editor.putBoolean(CampusPreference.DIRECTORIO_LED, false);
            editor.commit();
            dir_led.setChecked(sharedPreferences.getBoolean(CampusPreference.DIRECTORIO_LED,
                    false));
        }

        //Horario Notificaciones
        if (sharedPreferences.contains(CampusPreference.HORARIO_VIBRATE)) {
            horario_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.HORARIO_VIBRATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.HORARIO_VIBRATE, true);
            editor.commit();
            horario_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.HORARIO_VIBRATE,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.HORARIO_SOUND)) {
            horario_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.HORARIO_SOUND,
                    true));
        } else {
            editor.putBoolean(CampusPreference.HORARIO_SOUND, false);
            editor.commit();
            horario_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.HORARIO_SOUND,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.HORARIO_LED)) {
            horario_led.setChecked(sharedPreferences.getBoolean(CampusPreference.HORARIO_LED,
                    true));
        } else {
            editor.putBoolean(CampusPreference.HORARIO_LED, false);
            editor.commit();
            horario_led.setChecked(sharedPreferences.getBoolean(CampusPreference.HORARIO_LED,
                    false));
        }

        //Menu Notificaciones
        if (sharedPreferences.contains(CampusPreference.MENU_VIBRATE)) {
            menu_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_VIBRATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.MENU_VIBRATE, true);
            editor.commit();
            menu_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_VIBRATE,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.MENU_SOUND)) {
            menu_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_SOUND,
                    true));
        } else {
            editor.putBoolean(CampusPreference.MENU_SOUND, false);
            editor.commit();
            menu_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_SOUND,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.MENU_LED)) {
            menu_led.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_LED,
                    true));
        } else {
            editor.putBoolean(CampusPreference.MENU_LED, false);
            editor.commit();
            menu_led.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_LED,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.MENU_LOAD)) {
            menu_load.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_LOAD, true));
        } else {
            editor.putBoolean(CampusPreference.MENU_LOAD, false);
            editor.commit();
            menu_load.setChecked(sharedPreferences.getBoolean(CampusPreference.MENU_LOAD,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.MENU_PREF)) {
            menu_selected_value = sharedPreferences.getInt(CampusPreference.MENU_PREF, 0);
        } else {
            editor.putInt(CampusPreference.MENU_PREF, 0);
            editor.commit();
            menu_selected_value = sharedPreferences.getInt(CampusPreference.MENU_PREF,
                    0);
        }

        //Chat Notificaciones
        if (sharedPreferences.contains(CampusPreference.CHAT_VIBRATE)) {
            chat_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.CHAT_VIBRATE,
                    false));
        } else {
            editor.putBoolean(CampusPreference.CHAT_VIBRATE, true);
            editor.commit();
            chat_vib.setChecked(sharedPreferences.getBoolean(CampusPreference.CHAT_VIBRATE,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_SOUND)) {
            chat_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.CHAT_SOUND, true));
        } else {
            editor.putBoolean(CampusPreference.CHAT_SOUND, false);
            editor.commit();
            chat_sound.setChecked(sharedPreferences.getBoolean(CampusPreference.CHAT_SOUND,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_LED)) {
            chat_led.setChecked(sharedPreferences.getBoolean(CampusPreference.CHAT_LED, true));
        } else {
            editor.putBoolean(CampusPreference.CHAT_LED, false);
            editor.commit();
            chat_led.setChecked(sharedPreferences.getBoolean(CampusPreference.CHAT_LED,
                    false));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_HOST)) {
            chat_host.setText(sharedPreferences.getString(CampusPreference.CHAT_HOST, ""));
        } else {
            editor.putString(CampusPreference.CHAT_HOST, getResources().getString(R.string.jabber));
            editor.commit();
            chat_host.setText(sharedPreferences.getString(CampusPreference.CHAT_HOST,
                    getResources().getString(R.string.jabber)));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_SERVICE)) {
            chat_service.setText(sharedPreferences.getString(CampusPreference.CHAT_SERVICE, ""));
        } else {
            editor.putString(CampusPreference.CHAT_SERVICE, getResources().getString(R.string
                    .jabber));
            editor.commit();
            chat_service.setText(sharedPreferences.getString(CampusPreference.CHAT_SERVICE,
                    getResources().getString(R.string.jabber)));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_PORT)) {
            chat_port.setText(sharedPreferences.getString(CampusPreference.CHAT_PORT, ""));
        } else {
            editor.putString(CampusPreference.CHAT_PORT, getResources().getString(R.string
                    .jabber_port));
            editor.commit();
            chat_port.setText(sharedPreferences.getString(CampusPreference.CHAT_PORT,
                    getResources().getString(R.string.jabber_port)));
        }

    }

    private void initActionBar_Fab() {
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable
                .map_background));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (ActionButton) findViewById(R.id.fab_set);
        fab.setType(ActionButton.Type.DEFAULT);
        fab.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab.setImageResource(R.drawable.ic_action_accept);
        fab.setShadowRadius(4.4f);
        fab.setShadowXOffset(2.8f);
        fab.setShadowYOffset(2.1f);
        fab.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab.setStrokeWidth(0.0f);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(PreferenceActivity.this,
                        R.anim.fab_pop);
                fab.startAnimation(pop);
                saveAllData();
            }
        });

    }

    private void initTextViews() {
        am = getAssets();

        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_light_italic = Typeface.createFromAsset(am, "roboto_light_italic.ttf");
        roboto_condense_light = Typeface.createFromAsset(am, "roboto_condense_light.ttf");

        rss_url = getResources().getStringArray(R.array.rss);
        menu_url = getResources().getStringArray(R.array.menu_source);

        iv_pref_rss = (ImageView) findViewById(R.id.rss_pref_img);
        iv_pref_menu = (ImageView) findViewById(R.id.menu_pref_img);

        ll_cuota_time = (LinearLayout) findViewById(R.id.ll_cuota_time);
        ll_cuota_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCuotaTime();
            }
        });

        ll_rss_pref = (LinearLayout) findViewById(R.id.ll_rss_pref);
        ll_rss_pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrefRssList();
            }
        });

        ll_rss_what = (LinearLayout) findViewById(R.id.ll_rss_what);
        ll_rss_what.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTouchChoice();
            }
        });

        ll_menu_pref = (LinearLayout) findViewById(R.id.ll_menu_pref);
        ll_menu_pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrefMenuList();
            }
        });

        cuota_sett = (TextView) findViewById(R.id.cuota_sett);
        general_sett = (TextView) findViewById(R.id.general_sett);
        rss_sett = (TextView) findViewById(R.id.rss_sett);
        dir_sett = (TextView) findViewById(R.id.dir_sett);
        horario_sett = (TextView) findViewById(R.id.horario_sett);
        menu_sett = (TextView) findViewById(R.id.menu_sett);
        rss_pref = (TextView) findViewById(R.id.rss_pref);
        menu_pref = (TextView) findViewById(R.id.menu_pref);
        rss_what = (TextView) findViewById(R.id.rss_what);
        cuota_time = (TextView) findViewById(R.id.cuota_time);
        chat_sett = (TextView) findViewById(R.id.chat_sett);
        chat_host_tv = (TextView) findViewById(R.id.chat_host_tv);
        chat_service_tv = (TextView) findViewById(R.id.chat_service_tv);
        chat_port_tv = (TextView) findViewById(R.id.chat_port_tv);

        cuota_vib = (CheckBox) findViewById(R.id.cuota_vib);
        cuota_sound = (CheckBox) findViewById(R.id.cuota_sound);
        cuota_led = (CheckBox) findViewById(R.id.cuota_led);
        chat_vib = (CheckBox) findViewById(R.id.chat_vib);
        chat_sound = (CheckBox) findViewById(R.id.chat_sound);
        chat_led = (CheckBox) findViewById(R.id.chat_led);
        cuota_load = (CheckBox) findViewById(R.id.cuota_load);
        cuota_always = (CheckBox) findViewById(R.id.cuota_always);
        cuota_animations = (CheckBox) findViewById(R.id.cuota_animations);
        rss_vib = (CheckBox) findViewById(R.id.rss_vib);
        rss_sound = (CheckBox) findViewById(R.id.rss_sound);
        rss_led = (CheckBox) findViewById(R.id.rss_led);
        rss_load = (CheckBox) findViewById(R.id.rss_load);
        rss_load_select = (CheckBox) findViewById(R.id.rss_load_select);
        rss_animate_news = (CheckBox) findViewById(R.id.rss_animate_news);
        dir_vib = (CheckBox) findViewById(R.id.dir_vib);
        dir_sound = (CheckBox) findViewById(R.id.dir_sound);
        dir_led = (CheckBox) findViewById(R.id.dir_led);
        horario_vib = (CheckBox) findViewById(R.id.horario_vib);
        horario_sound = (CheckBox) findViewById(R.id.horario_sound);
        horario_led = (CheckBox) findViewById(R.id.horario_led);
        menu_vib = (CheckBox) findViewById(R.id.menu_vib);
        menu_sound = (CheckBox) findViewById(R.id.menu_sound);
        menu_led = (CheckBox) findViewById(R.id.menu_led);
        menu_load = (CheckBox) findViewById(R.id.menu_load);
        log_in_on_start = (CheckBox) findViewById(R.id.general_log_in);
        exit_anim = (CheckBox) findViewById(R.id.general_exit_anim);
        general_close_alert = (CheckBox) findViewById(R.id.general_close_alert);
        general_delete_alert = (CheckBox) findViewById(R.id.general_delete_alert);
        general_proximity = (CheckBox) findViewById(R.id.general_proximity);
        general_activate_notif = (CheckBox) findViewById(R.id.general_activate_notif);
        general_activate_notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    disableCheckBoxes();
                } else {
                    enableCheckBoxes();
                }
            }
        });


        cuota_sett.setTypeface(roboto_condense_light);
        rss_sett.setTypeface(roboto_condense_light);
        dir_sett.setTypeface(roboto_condense_light);
        horario_sett.setTypeface(roboto_condense_light);
        menu_sett.setTypeface(roboto_condense_light);
        general_sett.setTypeface(roboto_condense_light);
        chat_sett.setTypeface(roboto_condense_light);
        rss_pref.setTypeface(roboto_light);
        rss_what.setTypeface(roboto_light);
        cuota_time.setTypeface(roboto_light);
        menu_pref.setTypeface(roboto_light);

        cuota_vib.setTypeface(roboto_light);
        cuota_sound.setTypeface(roboto_light);
        cuota_led.setTypeface(roboto_light);
        chat_vib.setTypeface(roboto_light);
        chat_sound.setTypeface(roboto_light);
        chat_led.setTypeface(roboto_light);
        chat_host_tv.setTypeface(roboto_light);
        chat_service_tv.setTypeface(roboto_light);
        chat_port_tv.setTypeface(roboto_light);
        cuota_load.setTypeface(roboto_light);
        cuota_always.setTypeface(roboto_light);
        cuota_animations.setTypeface(roboto_light);
        rss_vib.setTypeface(roboto_light);
        rss_sound.setTypeface(roboto_light);
        rss_led.setTypeface(roboto_light);
        rss_load.setTypeface(roboto_light);
        rss_load_select.setTypeface(roboto_light);
        rss_animate_news.setTypeface(roboto_light);
        dir_vib.setTypeface(roboto_light);
        dir_sound.setTypeface(roboto_light);
        dir_led.setTypeface(roboto_light);
        horario_vib.setTypeface(roboto_light);
        horario_sound.setTypeface(roboto_light);
        horario_led.setTypeface(roboto_light);
        menu_vib.setTypeface(roboto_light);
        menu_sound.setTypeface(roboto_light);
        menu_led.setTypeface(roboto_light);
        menu_load.setTypeface(roboto_light);
        log_in_on_start.setTypeface(roboto_light);
        exit_anim.setTypeface(roboto_light);
        general_activate_notif.setTypeface(roboto_light);
        general_close_alert.setTypeface(roboto_light);
        general_delete_alert.setTypeface(roboto_light);
        general_proximity.setTypeface(roboto_light);

        chat_host = (EditText) findViewById(R.id.chat_host);
        chat_service = (EditText) findViewById(R.id.chat_service);
        chat_port = (EditText) findViewById(R.id.chat_port);

        chat_host.setTypeface(roboto_light_italic);
        chat_service.setTypeface(roboto_light_italic);
        chat_port.setTypeface(roboto_light_italic);
    }

    private void initSettingsView() {
        if (!general_activate_notif.isChecked()) {
            disableCheckBoxes();
        }

        iv_pref_rss.setImageDrawable(getResources().getDrawable(imgs_rss[rss_selected]));
        iv_pref_menu.setImageDrawable(getResources().getDrawable(imgs_menu[menu_selected_value]));

        if(rss_what_ == 0){
            rss_what.setText(getResources().getString(R.string.on_touch) + " " + getResources()
                    .getString(R.string.on_touch_desc));
        }else if(rss_what_ == 1){
            rss_what.setText(getResources().getString(R.string.on_touch) + " " + getResources()
                    .getString(R.string.on_touch_url));
        }

        if(cuota_selected == 0){
            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                    .getString(R.string.quota_refresh_nunca));
        }else if(cuota_selected == 1){
            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                    .getString(R.string.quota_refresh_two));
        }else if(cuota_selected == 2){
            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                    .getString(R.string.quota_refresh_five));
        }else if(cuota_selected == 3){
            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                    .getString(R.string.quota_refresh_ten));
        }else if(cuota_selected == 4){
            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                    .getString(R.string.quota_refresh_thirty));
        }else if(cuota_selected == 5){
            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                    .getString(R.string.quota_refresh_sixty));
        }


    }

    private void disableCheckBoxes() {
        cuota_vib.setEnabled(false);
        cuota_sound.setEnabled(false);
        cuota_led.setEnabled(false);
        cuota_always.setEnabled(false);

        rss_vib.setEnabled(false);
        rss_sound.setEnabled(false);
        rss_led.setEnabled(false);

        dir_vib.setEnabled(false);
        dir_sound.setEnabled(false);
        dir_led.setEnabled(false);

        horario_vib.setEnabled(false);
        horario_sound.setEnabled(false);
        horario_led.setEnabled(false);

        menu_vib.setEnabled(false);
        menu_sound.setEnabled(false);
        menu_led.setEnabled(false);

        chat_vib.setEnabled(false);
        chat_sound.setEnabled(false);
        chat_led.setEnabled(false);

    }

    private void enableCheckBoxes() {
        cuota_vib.setEnabled(true);
        cuota_sound.setEnabled(true);
        cuota_led.setEnabled(true);
        cuota_always.setEnabled(true);

        rss_vib.setEnabled(true);
        rss_sound.setEnabled(true);
        rss_led.setEnabled(true);

        dir_vib.setEnabled(true);
        dir_sound.setEnabled(true);
        dir_led.setEnabled(true);

        horario_vib.setEnabled(true);
        horario_sound.setEnabled(true);
        horario_led.setEnabled(true);

        menu_vib.setEnabled(true);
        menu_sound.setEnabled(true);
        menu_led.setEnabled(true);

        chat_vib.setEnabled(true);
        chat_sound.setEnabled(true);
        chat_led.setEnabled(true);
    }

    private void saveAllData() {
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        editor.putBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS, general_activate_notif.isChecked());
        editor.putBoolean(CampusPreference.CLOSE_ALERTS, general_close_alert.isChecked());
        editor.putBoolean(CampusPreference.DELETE_ALERTS, general_delete_alert.isChecked());
        editor.putBoolean(CampusPreference.PROXIMITY_UPDATE, general_proximity.isChecked());
        editor.putBoolean(CampusPreference.LOG_IN_ON_START, log_in_on_start.isChecked());
        editor.putBoolean(CampusPreference.ANIMATION_TO_EXIT, exit_anim.isChecked());

        editor.putBoolean(CampusPreference.CUOTA_VIBRATE, cuota_vib.isChecked());
        editor.putBoolean(CampusPreference.CUOTA_SOUND, cuota_sound.isChecked());
        editor.putBoolean(CampusPreference.CUOTA_LED, cuota_led.isChecked());
        editor.putBoolean(CampusPreference.CUOTA_LOAD, cuota_load.isChecked());
        editor.putInt(CampusPreference.CUOTA_REFRESH_TIME, cuota_selected);
        editor.putBoolean(CampusPreference.CUOTA_ALWAYS_IN_NOTIFICATION_BAR, cuota_always.isChecked());
        editor.putBoolean(CampusPreference.CUOTA_ANIMATIONS,
                cuota_animations.isChecked());

        editor.putBoolean(CampusPreference.RSS_VIBRATE, rss_vib.isChecked());
        editor.putBoolean(CampusPreference.RSS_SOUND, rss_sound.isChecked());
        editor.putBoolean(CampusPreference.RSS_LED, rss_led.isChecked());
        editor.putBoolean(CampusPreference.RSS_LOAD, rss_load.isChecked());
        editor.putInt(CampusPreference.RSS_PREF, rss_selected);
        editor.putInt(CampusPreference.RSS_WHAT, rss_what_);
        editor.putBoolean(CampusPreference.RSS_LOAD_SELECT, rss_load_select.isChecked());
        editor.putBoolean(CampusPreference.ANIMATE_NEWS_ENTRY, rss_animate_news.isChecked());

        editor.putBoolean(CampusPreference.DIRECTORIO_VIBRATE, dir_vib.isChecked());
        editor.putBoolean(CampusPreference.DIRECTORIO_SOUND, dir_sound.isChecked());
        editor.putBoolean(CampusPreference.DIRECTORIO_LED, dir_led.isChecked());

        editor.putBoolean(CampusPreference.HORARIO_VIBRATE, horario_vib.isChecked());
        editor.putBoolean(CampusPreference.HORARIO_SOUND, horario_sound.isChecked());
        editor.putBoolean(CampusPreference.HORARIO_LED, horario_led.isChecked());

        editor.putBoolean(CampusPreference.CHAT_VIBRATE, chat_vib.isChecked());
        editor.putBoolean(CampusPreference.CHAT_SOUND, chat_sound.isChecked());
        editor.putBoolean(CampusPreference.CHAT_LED, chat_led.isChecked());
        editor.putString(CampusPreference.CHAT_HOST, chat_host.getText().toString());
        editor.putString(CampusPreference.CHAT_SERVICE, chat_service.getText().toString());
        editor.putString(CampusPreference.CHAT_PORT, chat_port.getText().toString());

        editor.putBoolean(CampusPreference.MENU_VIBRATE, menu_vib.isChecked());
        editor.putBoolean(CampusPreference.MENU_SOUND, menu_sound.isChecked());
        editor.putBoolean(CampusPreference.MENU_LED, menu_led.isChecked());
        editor.putBoolean(CampusPreference.MENU_LOAD, menu_load.isChecked());
        editor.putInt(CampusPreference.MENU_PREF, menu_selected_value);

        editor.commit();
        exitPreference();
        //finish();
    }

    private void saveValuesToDefault() {
        //editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        //editor.putBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS, true);
        general_activate_notif.setChecked(true);
        //editor.putBoolean(CampusPreference.CLOSE_ALERTS, true);
        general_close_alert.setChecked(true);
        //editor.putBoolean(CampusPreference.DELETE_ALERTS, true);
        general_delete_alert.setChecked(true);
        //editor.putBoolean(CampusPreference.PROXIMITY_UPDATE, false);
        general_proximity.setChecked(false);
        //editor.putBoolean(CampusPreference.LOG_IN_ON_START, false);
        log_in_on_start.setChecked(false);
        //editor.putBoolean(CampusPreference.ANIMATION_TO_EXIT, true);
        exit_anim.setChecked(true);

        //editor.putBoolean(CampusPreference.CUOTA_VIBRATE, true);
        cuota_vib.setChecked(true);
        //editor.putBoolean(CampusPreference.CUOTA_SOUND, false);
        cuota_sound.setChecked(false);
        //editor.putBoolean(CampusPreference.CUOTA_LED, false);
        cuota_led.setChecked(false);
        //editor.putBoolean(CampusPreference.CUOTA_LOAD, false);
        cuota_load.setChecked(false);
        //editor.putInt(CampusPreference.CUOTA_REFRESH_TIME, 0);
        cuota_selected = 0;
        cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources().getString(R.string.quota_refresh_nunca));
        //editor.putBoolean(CampusPreference.CUOTA_ALWAYS_IN_NOTIFICATION_BAR, false);
        cuota_always.setChecked(false);
        //editor.putBoolean(CampusPreference.CUOTA_ANIMATIONS,true);
        cuota_animations.setChecked(true);

        //editor.putBoolean(CampusPreference.RSS_VIBRATE, true);
        rss_vib.setChecked(true);
        //editor.putBoolean(CampusPreference.RSS_SOUND, false);
        rss_sound.setChecked(false);
        //editor.putBoolean(CampusPreference.RSS_LED, false);
        rss_led.setChecked(false);
        //editor.putBoolean(CampusPreference.RSS_LOAD, false);
        rss_load.setChecked(false);
        //editor.putInt(CampusPreference.RSS_PREF, 0);
        rss_selected = 0;
        Animation i = AnimationUtils.loadAnimation(PreferenceActivity.this, R.anim.tv_alpha);
        iv_pref_rss.startAnimation(i);
        iv_pref_rss.setImageDrawable(PreferenceActivity.this.getResources().getDrawable(imgs_rss[rss_selected]));
        //editor.putInt(CampusPreference.RSS_WHAT, 0);
        rss_what_ = 0;
        Animation i1 = AnimationUtils.loadAnimation(PreferenceActivity.this, R.anim.tv_alpha);
        rss_what.setText(getResources().getString(R.string.on_touch) + " " + getResources().getString(R.string.on_touch_desc));
        rss_what.startAnimation(i1);
        //editor.putBoolean(CampusPreference.RSS_LOAD_SELECT, false);
        rss_load_select.setChecked(false);

        //editor.putBoolean(CampusPreference.DIRECTORIO_VIBRATE, true);
        dir_vib.setChecked(true);
        //editor.putBoolean(CampusPreference.DIRECTORIO_SOUND, false);
        dir_sound.setChecked(false);
        //editor.putBoolean(CampusPreference.DIRECTORIO_LED, false);
        dir_led.setChecked(false);

        //editor.putBoolean(CampusPreference.HORARIO_VIBRATE, true);
        horario_vib.setChecked(true);
        //editor.putBoolean(CampusPreference.HORARIO_SOUND, false);
        horario_sound.setChecked(false);
        //editor.putBoolean(CampusPreference.HORARIO_LED, false);
        horario_led.setChecked(false);

        //editor.putBoolean(CampusPreference.CHAT_VIBRATE, true);
        chat_vib.setChecked(true);
        //editor.putBoolean(CampusPreference.CHAT_SOUND, false);
        chat_sound.setChecked(false);
        //editor.putBoolean(CampusPreference.CHAT_LED, false);
        chat_led.setChecked(false);
        //editor.putString(CampusPreference.CHAT_HOST, getResources().getString(R.string.jabber));
        chat_host.setText(getResources().getString(R.string.jabber));
        //editor.putString(CampusPreference.CHAT_SERVICE, getResources().getString(R.stringjabber));
        chat_service.setText(getResources().getString(R.string.jabber));
        //editor.putString(CampusPreference.CHAT_PORT, getResources().getString(R.string.jabber_port));
        chat_port.setText(getResources().getString(R.string.jabber_port));

        //editor.putBoolean(CampusPreference.MENU_VIBRATE, true);
        menu_vib.setChecked(true);
        //editor.putBoolean(CampusPreference.MENU_SOUND, false);
        menu_sound.setChecked(false);
        //editor.putBoolean(CampusPreference.MENU_LED, false);
        menu_led.setChecked(false);
        //editor.putBoolean(CampusPreference.MENU_LOAD, false);
        menu_load.setChecked(false);
        //editor.putInt(CampusPreference.MENU_PREF, 0);
        menu_selected_value = 0;
        Animation i2 = AnimationUtils.loadAnimation(PreferenceActivity.this, R.anim.tv_alpha);
        iv_pref_menu.startAnimation(i2);
        iv_pref_menu.setImageDrawable(PreferenceActivity.this.getResources().getDrawable(imgs_menu[menu_selected_value]));

        //initTextViews();
        //initSettings();
        //initSettingsView();

        //finish();
    }

    private void showExitDialog() {
        final String positive = getResources().getString(R.string.yes);
        final String negative = getResources().getString(R.string.no);
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.close))
                .content(getResources().getString(R.string.to_save))
                .icon(getResources().getDrawable(R.drawable.ic_settings_nav))
                .cancelable(true)
                .theme(Theme.LIGHT)
                .positiveText(positive)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        saveAllData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        exitPreference();
                        //finish();
                    }

                }).build();
        dialog.show();

    }

    private void showPrefRssList() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.pref_rss)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                .adapter(new MyListAdapter2(this, R.array.rss, PreferenceActivity.this, rss_url,
                                imgs_rss, roboto_light),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                rss_selected = which;
                                Animation i = AnimationUtils.loadAnimation(PreferenceActivity
                                        .this, R.anim.tv_alpha);
                                iv_pref_rss.startAnimation(i);
                                iv_pref_rss.setImageDrawable(PreferenceActivity.this.getResources
                                        ().getDrawable(imgs_rss[rss_selected]));
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void showPrefMenuList() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.pref_menu)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_menu_nav))
                .adapter(new MyListAdapter2(this, R.array.menu_source, PreferenceActivity.this,
                                menu_url,
                                imgs_menu, roboto_light),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                menu_selected_value = which;
                                Animation i = AnimationUtils.loadAnimation(PreferenceActivity
                                        .this, R.anim.tv_alpha);
                                iv_pref_menu.startAnimation(i);
                                iv_pref_menu.setImageDrawable(PreferenceActivity.this.getResources
                                        ().getDrawable(imgs_menu[menu_selected_value]));
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void showTouchChoice() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.on_touch)
                .cancelable(true)
                .items(R.array.touch)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_rss_nav))
                .itemsCallbackSingleChoice(rss_what_, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        rss_what_ = which;
                        Animation i = AnimationUtils.loadAnimation(PreferenceActivity
                                .this, R.anim.tv_alpha);
                        if(rss_what_ == 0){
                            rss_what.setText(getResources().getString(R.string.on_touch) + " " + getResources()
                                    .getString(R.string.on_touch_desc));
                            rss_what.startAnimation(i);
                        }else if(rss_what_ == 1){
                            rss_what.setText(getResources().getString(R.string.on_touch) + " " + getResources()
                                    .getString(R.string.on_touch_url));
                            rss_what.startAnimation(i);
                        }

                        dialog.dismiss();
                        return true; // allow selection
                    }
                })
                .show();
    }

    private void showCuotaTime() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.quota_refresh_)
                .cancelable(true)
                .items(R.array.time)
                .theme(Theme.LIGHT)
                .icon(getResources().getDrawable(R.drawable.ic_cuota_nav))
                .itemsCallbackSingleChoice(cuota_selected, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        cuota_selected = which;
                        Animation i = AnimationUtils.loadAnimation(PreferenceActivity
                                .this, R.anim.tv_alpha);
                        if(cuota_selected == 0){
                            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                                    .getString(R.string.quota_refresh_nunca));
                            cuota_time.startAnimation(i);
                        }else if(cuota_selected == 1){
                            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                                    .getString(R.string.quota_refresh_two));
                            cuota_time.startAnimation(i);
                        }else if(cuota_selected == 2){
                            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                                    .getString(R.string.quota_refresh_five));
                            cuota_time.startAnimation(i);
                        }else if(cuota_selected == 3){
                            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                                    .getString(R.string.quota_refresh_ten));
                            cuota_time.startAnimation(i);
                        }else if(cuota_selected == 4){
                            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                                    .getString(R.string.quota_refresh_thirty));
                            cuota_time.startAnimation(i);
                        }else if(cuota_selected == 5){
                            cuota_time.setText(getResources().getString(R.string.quota_refresh) + " " + getResources()
                                    .getString(R.string.quota_refresh_sixty));
                            cuota_time.startAnimation(i);
                        }

                        dialog.dismiss();
                        return true; // allow selection
                    }
                })
                .show();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    private void exitPreference(){

        if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.MAIN_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.RSS_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, RssActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.DIRECTORIO_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, DirectorioActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.CUOTA_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, CuotaActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.HORARIO_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, HorarioActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.MENU_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, MenuActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.CHAT_ACTIVITY)){
            //startActivity(new Intent(PreferenceActivity.this, ChattyActivity.class));
            //overridePendingTransition(R.anim.activity_fade_in,
            //        R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals(INTENTSENDER.ABOUT_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }else if(getIntent().getExtras().getString(INTENTSENDER.INTENT_SENDER).equals
                (INTENTSENDER.MARKET_ACTIVITY)){
            startActivity(new Intent(PreferenceActivity.this, MarketActivity.class));
            overridePendingTransition(R.anim.activity_fade_in,
                    R.anim.activity_fade_out);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_default) {

            saveValuesToDefault();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
