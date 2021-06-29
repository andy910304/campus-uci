package cu.uci.campusuci;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import cu.uci.chatty.ChatActivity;
import cu.uci.cuota.CuotaActivity;
import cu.uci.directorio.DirectorioActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.mapa.MapaActivity;
import cu.uci.market.MarketActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.RssActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.DialogAction;
import cu.uci.utils.MaterialDialog.base.GravityEnum;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;
import cu.uci.utils.SSL._FakeX509TrustManager;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    AuthDB authDB;
    boolean save_to_auth;
    String user_to_save;
    String pass_to_save;

    RssActivity rss = new RssActivity();
    DirectorioActivity dir = new DirectorioActivity();
    CuotaActivity cuota = new CuotaActivity();
    MenuActivity menu = new MenuActivity();
    HorarioActivity horario = new HorarioActivity();
    MapaActivity mapa = new MapaActivity();
    ChatActivity chat = new ChatActivity();
    MarketActivity market = new MarketActivity();
    AboutActivity about = new AboutActivity();

    public static LoginUser persona;

    AssetManager am;
    Typeface roboto_light, roboto_light_italic, roboto_condense_light;
    Toolbar mToolbar;
    ActionButton fab, fab_exit, fab_stay;
    LinearLayout llRss, llDirectorio, llCuota, llHorario, llMenu, llMapa, llChat, llMarket;
    FrameLayout fl_exit,fl_new_chat_fuction;
    TextView tvDominio, tvAppName, tvRssTitle, tvRssSubTitle , tvDirectorioTitle,
            tvDirectorioSubTitle, tvCuotaTitle, tvCuotaSubTitle, tvHorarioTitle,
            tvHorarioSubTitle, tvMenuTitle, tvMenuSubTitle, tvMapaTitle, tvMapaSubTitle,
            tvChatTitle, tvChatSubTitle, tvMarketTitle, tvMarketSubTitle, tvExit,
            tv_new_chat_function;
    ImageView ivRss, ivDirectorio, ivCuota, ivHorario, ivMenu, ivMapa, ivChat, ivMarket;
    Boolean isExitShow;
    Boolean log = false;


    View shadow;
    public static Boolean isInFront;
    public static Bundle bundle;
    public static int log_numbers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);

        overridePendingTransition(R.anim.activity_fade_in,
                R.anim.activity_fade_out);
        initToolBar_FAB();
        initExitFabs();
        initImageViews();
        intitTextViews();
        initLayouts();
        logOrNotToLog();
        entry();
        setStatusBarColor();
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
        }else{

        }
    }

    public void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
            //lx.setMinimumHeight(getStatusBarHeight());
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

    private void logOrNotToLog() {

        boolean log_in_on_start;

        if (sharedPreferences.contains(CampusPreference.LOG_IN_ON_START)) {
            log_in_on_start = sharedPreferences.getBoolean(CampusPreference
                            .LOG_IN_ON_START,
                    false);
        } else {
            editor.putBoolean(CampusPreference.LOG_IN_ON_START, false);
            editor.commit();
            log_in_on_start = sharedPreferences.getBoolean(CampusPreference
                            .LOG_IN_ON_START,
                    false);
        }

        if(log_in_on_start){

            authDB.openAuthDatabase();
            String []auth = authDB.loadAuth();
            authDB.closeAuthDatabase();

            if(auth[0].length()>0){
                _autenticarUsuario autenticarUsuario = new _autenticarUsuario();
                autenticarUsuario.execute(auth[0],
                        auth[1]);
            }

        }

    }

    @Override
    protected void onStop() {
        //finish();
        isInFront = false;
        super.onStop();
    }

    private void initToolBar_FAB(){

        bundle = new Bundle();

        notificationCompatBuilder = new NotificationCompat.Builder(MainActivity.this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        persona = new LoginUser();
        fillBundle(null, null, null,null, null, null,null, null, null,null,
                null, null,null, null, null, null, null);

        isInFront = true;
        shadow = findViewById(R.id.shadow_toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            shadow.setMinimumHeight(4);
        }


         authDB = new AuthDB(this);
        isExitShow = false;

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_main);

        fab = (ActionButton) findViewById(R.id.fab_login);
        fab.setType(ActionButton.Type.DEFAULT);
        fab.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab.setImageResource(R.drawable.ic_login);
        fab.setShadowRadius(4.4f);
        fab.setShadowXOffset(2.8f);
        fab.setShadowYOffset(2.1f);
        fab.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab.setStrokeWidth(0.0f);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setClickable(false);
                Animation pop = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_pop);
                fab.startAnimation(pop);
                showLogDialog();
            }
        });

        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.ic_launcher);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.action_about:
                        startActivity(new Intent(MainActivity.this,
                                AboutActivity.class).putExtras(bundle));
                        overridePendingTransition(R.anim.activity_fade_in,
                                R.anim.activity_fade_out);
                        break;

                    case R.id.action_settings:
                        startActivity(new Intent(MainActivity.this,
                                PreferenceActivity.class).putExtra(INTENTSENDER.INTENT_SENDER,
                                INTENTSENDER.MAIN_ACTIVITY));
                        overridePendingTransition(R.anim.activity_fade_in,
                                R.anim.activity_fade_out);
                        break;

                }
                return true;
            }
        });

    }

    private void initExitFabs(){

        fab_exit = (ActionButton) findViewById(R.id.fab_exit);
        fab_exit.setType(ActionButton.Type.DEFAULT);
        fab_exit.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab_exit.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab_exit.setImageResource(R.drawable.ic_action_cancel);
        fab_exit.setShadowRadius(4.4f);
        fab_exit.setShadowXOffset(2.8f);
        fab_exit.setShadowYOffset(2.1f);
        fab_exit.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab_exit.setStrokeWidth(0.0f);

        fab_stay = (ActionButton) findViewById(R.id.fab_stay);
        fab_stay.setType(ActionButton.Type.DEFAULT);
        fab_stay.setButtonColor(getResources().getColor(R.color.fab_sms));
        fab_stay.setButtonColorPressed(getResources().getColor(R.color.fab_sms_pressed));
        fab_stay.setImageResource(R.drawable.ic_action_accept);
        fab_stay.setShadowRadius(4.4f);
        fab_stay.setShadowXOffset(2.8f);
        fab_stay.setShadowYOffset(2.1f);
        fab_stay.setStrokeColor(getResources().getColor(R.color.fab_sms));
        fab_stay.setStrokeWidth(0.0f);

        fab_stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_pop);
                fab_stay.startAnimation(pop);
                if(rss.ctx != null){
                    rss.ctx.finish();
                }
                if(dir.ctx != null){
                    dir.ctx.finish();
                }
                if(horario.ctx != null){
                    horario.ctx.finish();
                }
                if(menu.ctx != null){
                    menu.ctx.finish();
                }
                if(cuota.ctx != null){
                    cuota.ctx.finish();
                }
                if(mapa.ctx != null){
                    mapa.ctx.finish();
                }
                if(about.ctx != null){
                    about.ctx.finish();
                }
                if(chat.ctx != null){
                    chat.ctx.finish();
                }
                if(market.ctx != null){
                    market.ctx.finish();
                }
                MainActivity.this.finish();

            }
        });

        fab_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockOptions();
                Animation animation_exit = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.exit_anim_out);
                fab_exit.startAnimation(animation_exit);
                fab_stay.startAnimation(animation_exit);
                fab_exit.setVisibility(View.INVISIBLE);
                fab_stay.setVisibility(View.INVISIBLE);
                isExitShow = false;
                entry();
            }
        });

    }

    private void initImageViews(){

        ivRss = (ImageView) findViewById(R.id.ivRss);
        ivDirectorio = (ImageView) findViewById(R.id.ivDirectorio);
        ivCuota = (ImageView) findViewById(R.id.ivCuota);
        ivHorario = (ImageView) findViewById(R.id.ivHorario);
        ivMenu = (ImageView) findViewById(R.id.ivMenu);
        ivMapa = (ImageView) findViewById(R.id.ivMapa);
        ivChat = (ImageView) findViewById(R.id.ivChat);
        ivMarket = (ImageView) findViewById(R.id.ivMarket);
    }

    private void intitTextViews(){

        am = getAssets();

        roboto_light = Typeface.createFromAsset(am,"roboto_light.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");

        tv_new_chat_function = (TextView) findViewById(R.id.tv_new_chat_function);
        tv_new_chat_function.setTypeface(roboto_light);

        tvDominio = (TextView) findViewById(R.id.tvDominio);
        tvDominio.setTypeface(roboto_light_italic);
        tvAppName = (TextView) findViewById(R.id.tvAppName);
        tvAppName.setTypeface(roboto_condense_light);

        tvRssTitle = (TextView) findViewById(R.id.tvRssTitle);
        tvRssTitle.setTypeface(roboto_condense_light);
        tvRssSubTitle = (TextView) findViewById(R.id.tvRssSubtitle);
        tvRssSubTitle.setTypeface(roboto_condense_light);

        tvDirectorioTitle = (TextView) findViewById(R.id.tvDirectorioTitle);
        tvDirectorioTitle.setTypeface(roboto_condense_light);
        tvDirectorioSubTitle = (TextView) findViewById(R.id.tvDirectorioSubtitle);
        tvDirectorioSubTitle.setTypeface(roboto_condense_light);

        tvCuotaTitle = (TextView) findViewById(R.id.tvCuotaTitle);
        tvCuotaTitle.setTypeface(roboto_condense_light);
        tvCuotaSubTitle = (TextView) findViewById(R.id.tvCuotaSubtitle);
        tvCuotaSubTitle.setTypeface(roboto_condense_light);

        tvMenuTitle = (TextView) findViewById(R.id.tvMenuTitle);
        tvMenuTitle.setTypeface(roboto_condense_light);
        tvMenuSubTitle = (TextView) findViewById(R.id.tvMenuSubtitle);
        tvMenuSubTitle.setTypeface(roboto_condense_light);

        tvHorarioTitle = (TextView) findViewById(R.id.tvHorarioTitle);
        tvHorarioTitle.setTypeface(roboto_condense_light);
        tvHorarioSubTitle = (TextView) findViewById(R.id.tvHorarioSubtitle);
        tvHorarioSubTitle.setTypeface(roboto_condense_light);

        tvMapaTitle = (TextView) findViewById(R.id.tvMapaTitle);
        tvMapaTitle.setTypeface(roboto_condense_light);
        tvMapaSubTitle = (TextView) findViewById(R.id.tvMapaSubtitle);
        tvMapaSubTitle.setTypeface(roboto_condense_light);

        tvChatTitle = (TextView) findViewById(R.id.tvChatTitle);
        tvChatTitle.setTypeface(roboto_condense_light);
        tvChatSubTitle = (TextView) findViewById(R.id.tvChatSubtitle);
        tvChatSubTitle.setTypeface(roboto_condense_light);

        tvMarketTitle = (TextView) findViewById(R.id.tvMarketTitle);
        tvMarketTitle.setTypeface(roboto_condense_light);
        tvMarketSubTitle = (TextView) findViewById(R.id.tvMarketSubtitle);
        tvMarketSubTitle.setTypeface(roboto_condense_light);

        tvExit = (TextView) findViewById(R.id.tvExit);
        tvExit.setTypeface(roboto_light_italic);

    }

    private void initLayouts(){

        llRss = (LinearLayout) findViewById(R.id.llRss);
        llDirectorio = (LinearLayout) findViewById(R.id.llDirectorio);
        llCuota = (LinearLayout) findViewById(R.id.llCuota);
        llHorario = (LinearLayout) findViewById(R.id.llHorario);
        llMenu = (LinearLayout) findViewById(R.id.llMenu);
        llMapa = (LinearLayout) findViewById(R.id.llMapa);
        llChat = (LinearLayout) findViewById(R.id.llChat);
        llMarket = (LinearLayout) findViewById(R.id.llMarket);

        fl_exit = (FrameLayout) findViewById(R.id.fl_exit);
        fl_new_chat_fuction = (FrameLayout) findViewById(R.id.fl_new_chat_fuction);
        fl_new_chat_fuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fl_new_chat_fuction.getVisibility() == View.VISIBLE){
                    Animation a = AnimationUtils.loadAnimation(MainActivity.this,
                            R.anim.fab_fade_out);
                    fl_new_chat_fuction.startAnimation(a);
                    fl_new_chat_fuction.setVisibility(View.GONE);
                }
            }
        });

        llRss.setOnClickListener(this);
        llDirectorio.setOnClickListener(this);
        llCuota.setOnClickListener(this);
        llHorario.setOnClickListener(this);
        llMenu.setOnClickListener(this);
        llMapa.setOnClickListener(this);
        llChat.setOnClickListener(this);
        llMarket.setOnClickListener(this);
    }

    private void entry(){

        llRss.setVisibility(View.VISIBLE);
        llDirectorio.setVisibility(View.VISIBLE);
        llCuota.setVisibility(View.VISIBLE);
        llHorario.setVisibility(View.VISIBLE);
        llMenu.setVisibility(View.VISIBLE);
        llMapa.setVisibility(View.VISIBLE);
        llChat.setVisibility(View.VISIBLE);
        llMarket.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.VISIBLE);
        shadow.setVisibility(View.VISIBLE);

        Animation animation_rss = AnimationUtils.loadAnimation(this, R.anim.entry_rss);
        Animation animation_directorio = AnimationUtils.loadAnimation(this, R.anim.entry_directorio);
        Animation animation_cuota = AnimationUtils.loadAnimation(this, R.anim.entry_cuota);
        Animation animation_horario = AnimationUtils.loadAnimation(this, R.anim.entry_horario);
        Animation animation_menu = AnimationUtils.loadAnimation(this, R.anim.entry_menu);
        Animation animation_mapa = AnimationUtils.loadAnimation(this, R.anim.entry_mapa);
        Animation animation_chat = AnimationUtils.loadAnimation(this, R.anim.entry_chat);
        Animation animation_market = AnimationUtils.loadAnimation(this, R.anim.entry_market);
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale);
        Animation animation_fl = AnimationUtils.loadAnimation(this, R.anim.fl_fade_out);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        llRss.startAnimation(animation_rss);
        llDirectorio.startAnimation(animation_directorio);
        llCuota.startAnimation(animation_cuota);
        llHorario.startAnimation(animation_horario);
        llMenu.startAnimation(animation_menu);
        llMapa.startAnimation(animation_mapa);
        llChat.startAnimation(animation_chat);
        llMarket.startAnimation(animation_market);
        fab.startAnimation(animation_fab);
        shadow.startAnimation(animation_shadow_in);
        mToolbar.startAnimation(animation_toolbar_in);
        fl_exit.startAnimation(animation_fl);

        tvExit.setVisibility(View.INVISIBLE);
        fl_exit.setBackgroundColor(Color.TRANSPARENT);

        boolean new_function_chat;

        if (sharedPreferences.contains(CampusPreference.NEW_CHAT_FUNCIONALITY)) {
            new_function_chat = sharedPreferences.getBoolean(CampusPreference.NEW_CHAT_FUNCIONALITY,
                    false);
        } else {
            editor.putBoolean(CampusPreference.NEW_CHAT_FUNCIONALITY, true);
            editor.commit();
            new_function_chat = sharedPreferences.getBoolean(CampusPreference.NEW_CHAT_FUNCIONALITY,
                    false);
        }

        if(new_function_chat){
            fl_new_chat_fuction.setBackgroundColor(getResources().getColor(R.color.app_black_transparent));
            fl_new_chat_fuction.setVisibility(View.VISIBLE);
            Animation anim_new_chat = AnimationUtils.loadAnimation(this, R.anim.fl_fade_in);
            fl_new_chat_fuction.startAnimation(anim_new_chat);
            editor.putBoolean(CampusPreference.NEW_CHAT_FUNCIONALITY, false);
            editor.commit();
        }

    }

    private void exit(){

        fl_exit.setBackgroundColor(getResources().getColor(R.color.app_black_transparent));
        tvExit.setVisibility(View.VISIBLE);

        Animation animation_rss = AnimationUtils.loadAnimation(this, R.anim.exit_rss);
        Animation animation_directorio = AnimationUtils.loadAnimation(this, R.anim.exit_directorio);
        Animation animation_cuota = AnimationUtils.loadAnimation(this, R.anim.exit_cuota);
        Animation animation_horario = AnimationUtils.loadAnimation(this, R.anim.exit_horario);
        Animation animation_menu = AnimationUtils.loadAnimation(this, R.anim.exit_menu);
        Animation animation_mapa = AnimationUtils.loadAnimation(this, R.anim.exit_mapa);
        Animation animation_chat = AnimationUtils.loadAnimation(this, R.anim.exit_chat);
        Animation animation_market = AnimationUtils.loadAnimation(this, R.anim.exit_market);
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale_out);
        Animation animation_fl = AnimationUtils.loadAnimation(this, R.anim.fl_fade_in);
        Animation animation_toolbar_out = AnimationUtils.loadAnimation(this, R.anim.toolbar_out);
        Animation animation_shadow_out = AnimationUtils.loadAnimation(this, R.anim.shadow_out);

        llRss.startAnimation(animation_rss);
        llDirectorio.startAnimation(animation_directorio);
        llCuota.startAnimation(animation_cuota);
        llHorario.startAnimation(animation_horario);
        llMenu.startAnimation(animation_menu);
        llMapa.startAnimation(animation_mapa);
        llChat.startAnimation(animation_chat);
        llMarket.startAnimation(animation_market);
        fab.startAnimation(animation_fab);
        fl_exit.startAnimation(animation_fl);
        tvExit.startAnimation(animation_fl);
        mToolbar.startAnimation(animation_toolbar_out);
        shadow.startAnimation(animation_shadow_out);

        llRss.setVisibility(View.INVISIBLE);
        llDirectorio.setVisibility(View.INVISIBLE);
        llCuota.setVisibility(View.INVISIBLE);
        llHorario.setVisibility(View.INVISIBLE);
        llMenu.setVisibility(View.INVISIBLE);
        llMapa.setVisibility(View.INVISIBLE);
        llChat.setVisibility(View.INVISIBLE);
        llMarket.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        mToolbar.setVisibility(View.INVISIBLE);
        shadow.setVisibility(View.INVISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onClick(View v) {
        int id = v.getId();
        Thread t;
        Animation animation;
        switch (id){

            case R.id.llRss:
                lockOptions();
                 animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                 ivRss.startAnimation(animation);
                t = new Thread(){
                    public void run(){
                        try {
                            sleep(700);
                            startActivity(new Intent(MainActivity.this, RssActivity.class).putExtras(bundle));
                            overridePendingTransition(R.anim.activity_fade_in,
                                    R.anim.activity_fade_out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;

            case R.id.llDirectorio:
                lockOptions();
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.iv_rotate);
                        ivDirectorio.startAnimation(animation);
                t = new Thread(){
                  public void run(){
                      try {
                          sleep(700);
                          startActivity(new Intent(MainActivity.this, DirectorioActivity.class).putExtras(bundle));
                          overridePendingTransition(R.anim.activity_fade_in,
                                  R.anim.activity_fade_out);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  }
                };
                t.start();

                break;

            case R.id.llCuota:
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO){
                    Toast toast = Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.sdk_min_),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }else{
                    lockOptions();
                    animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                    ivCuota.startAnimation(animation);
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(700);
                                startActivity(new Intent(MainActivity.this, CuotaActivity.class).putExtras(bundle));
                                overridePendingTransition(R.anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.llHorario:
                lockOptions();
                animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                ivHorario.startAnimation(animation);
                t = new Thread(){
                    public void run(){
                        try {
                            sleep(700);
                            startActivity(new Intent(MainActivity.this, HorarioActivity.class).putExtras(bundle));
                            overridePendingTransition(R.anim.activity_fade_in,
                                    R.anim.activity_fade_out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;

            case R.id.llMenu:
                lockOptions();
                animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                ivMenu.startAnimation(animation);
                t = new Thread(){
                    public void run(){
                        try {
                            sleep(700);
                            startActivity(new Intent(MainActivity.this, MenuActivity.class).putExtras(bundle));
                            overridePendingTransition(R.anim.activity_fade_in,
                                    R.anim.activity_fade_out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;

            case R.id.llMapa:
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
                        Toast toast = Toast.makeText(this, getResources().getString(R.string.sdk_min),
                                Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }else{
                    lockOptions();
                    animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                    ivMapa.startAnimation(animation);
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(700);
                                startActivity(new Intent(MainActivity.this, MapaActivity.class));
                                overridePendingTransition(R.anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }

                break;

            case R.id.llChat:
                lockOptions();
                animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                ivChat.startAnimation(animation);
                t = new Thread(){
                    public void run(){
                        try {
                            sleep(700);
                            startActivity(new Intent(MainActivity.this,
                                    ChatActivity.class).putExtras(bundle));
                            overridePendingTransition(R.anim.activity_fade_in,
                                    R.anim.activity_fade_out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;

            case R.id.llMarket:
                lockOptions();
                animation = AnimationUtils.loadAnimation(this, R.anim.iv_rotate);
                ivMarket.startAnimation(animation);
                t = new Thread(){
                    public void run(){
                        try {
                            sleep(700);
                            startActivity(new Intent(MainActivity.this,
                                    MarketActivity.class).putExtras(bundle));
                            overridePendingTransition(R.anim.activity_fade_in,
                                    R.anim.activity_fade_out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;
        }
    }

    private void lockOptions(){
        mToolbar.setClickable(false);
        fab.setClickable(false);
        this.llMapa.setClickable(false);
        this.llDirectorio.setClickable(false);
        this.llCuota.setClickable(false);
        this.llMenu.setClickable(false);
        this.llHorario.setClickable(false);
        this.llRss.setClickable(false);
        this.llChat.setClickable(false);
        this.llMarket.setClickable(false);
    }

    private void unlockOptions(){
        mToolbar.setClickable(true);
        fab.setClickable(true);
        this.llMapa.setClickable(true);
        this.llDirectorio.setClickable(true);
        this.llCuota.setClickable(true);
        this.llMenu.setClickable(true);
        this.llHorario.setClickable(true);
        this.llRss.setClickable(true);
        this.llChat.setClickable(true);
        this.llMarket.setClickable(true);
    }

    @Override
    protected void onRestart() {
        //this.llMapa.setEnabled(true);
        //this.llDirectorio.setEnabled(true);
        //this.llCuota.setEnabled(true);
        //this.llMenu.setEnabled(true);
        //this.llHorario.setEnabled(true);
        //this.llRss.setEnabled(true);
        super.onRestart();
    }

    @Override
    protected void onStart() {
        //unlockOptions();
        overridePendingTransition(R.anim.activity_fade_in,
                R.anim.activity_fade_out);
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_MAIN);
        super.onStart();
    }

    @Override
    protected void onResume() {
        isInFront = true;
        unlockOptions();
        overridePendingTransition(R.anim.activity_fade_in,
                R.anim.activity_fade_out);
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if(fl_new_chat_fuction.getVisibility() == View.VISIBLE){
            Animation a = AnimationUtils.loadAnimation(MainActivity.this,
                    R.anim.fab_fade_out);
            fl_new_chat_fuction.startAnimation(a);
            fl_new_chat_fuction.setVisibility(View.GONE);
        }else{

            boolean exit_anim;

            if (sharedPreferences.contains(CampusPreference.ANIMATION_TO_EXIT)) {
                exit_anim = sharedPreferences.getBoolean(CampusPreference
                                .ANIMATION_TO_EXIT,
                        false) ;
            } else {
                editor.putBoolean(CampusPreference.ANIMATION_TO_EXIT, true);
                editor.commit();
                exit_anim = sharedPreferences.getBoolean(CampusPreference
                                .ANIMATION_TO_EXIT,
                        false);
            }

            if(exit_anim){

                if(!isExitShow){
                    lockOptions();
                    exit();
                    Animation animation_exit = AnimationUtils.loadAnimation(this, R.anim.exit_anim_in);

                    fab_exit.setVisibility(View.VISIBLE);
                    fab_stay.setVisibility(View.VISIBLE);
                    fab_exit.startAnimation(animation_exit);
                    fab_stay.startAnimation(animation_exit);
                    isExitShow = true;
                }else{
                    unlockOptions();
                    Animation animation_exit = AnimationUtils.loadAnimation(this, R.anim.exit_anim_out);
                    Animation animation_tv = AnimationUtils.loadAnimation(this, R.anim.tv_fade_out);

                    tvExit.startAnimation(animation_tv);
                    fab_exit.startAnimation(animation_exit);
                    fab_stay.startAnimation(animation_exit);
                    tvExit.setVisibility(View.INVISIBLE);
                    fab_exit.setVisibility(View.INVISIBLE);
                    fab_stay.setVisibility(View.INVISIBLE);
                    isExitShow = false;

                    entry();
                }

            }else{

                if(rss.ctx != null){
                    rss.ctx.finish();
                }
                if(dir.ctx != null){
                    dir.ctx.finish();
                }
                if(horario.ctx != null){
                    horario.ctx.finish();
                }
                if(menu.ctx != null){
                    menu.ctx.finish();
                }
                if(cuota.ctx != null){
                    cuota.ctx.finish();
                }
                if(mapa.ctx != null){
                    mapa.ctx.finish();
                }
                if(about.ctx != null){
                    about.ctx.finish();
                }
                if(chat.ctx != null){
                    chat.ctx.finish();
                }
                if(market.ctx != null){
                    market.ctx.finish();
                }
                MainActivity.this.finish();

            }

        }

    }

    private EditText userInput;
    private EditText passwordInput;
    private CheckBox showPass;
    private CheckBox remember_me;
    private View positiveAction;
    private boolean userText = false;
    private boolean passText = false;

    private void showLogDialog() {

        authDB.openAuthDatabase();
        String []auth = authDB.loadAuth();
        authDB.closeAuthDatabase();

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.log_in)
                .icon(getResources().getDrawable(R.drawable.ic_login))
                .customView(R.layout.dialog_auth, true)
                .positiveText(R.string.connect)
                .theme(Theme.LIGHT)
                .negativeText(android.R.string.cancel)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        fab.setClickable(true);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fab.setClickable(true);
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        fab.setClickable(true);
                        _autenticarUsuario autenticarUsuario = new _autenticarUsuario();
                        autenticarUsuario.execute(userInput.getText().toString(),
                                passwordInput.getText().toString());
                        save_to_auth = remember_me.isChecked();
                        user_to_save = userInput.getText().toString();
                        pass_to_save = passwordInput.getText().toString();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.setClickable(true);
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
                if(s.toString().trim().length() > 0){
                    userText = true;
                }else{
                    userText = false;
                }

                positiveAction.setEnabled(passText && userText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        remember_me = (CheckBox) dialog.getCustomView().findViewById(R.id.rememberMe);

        if (sharedPreferences.contains(CampusPreference.REMEMBER_ME)) {
            remember_me.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .REMEMBER_ME,
                    false));
        } else {
            editor.putBoolean(CampusPreference.REMEMBER_ME, false);
            editor.commit();
            remember_me.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .REMEMBER_ME,
                    false));
        }


        // Toggling the show password CheckBox will mask or unmask the password input EditText
        showPass = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  int selection = passwordInput.getSelectionStart();
                  passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                  passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
                  passwordInput.setSelection(selection);
                  }
                  });

        showPass.setChecked(true);

        if(auth[0].length()>0){
            userInput.setText(auth[0]);
            passwordInput.setText(auth[1]);
        }

        showPass.setChecked(false);

        dialog.show();
        if(userInput.getText().toString().length() > 0 && passwordInput.getText().toString().length
                () > 0){
            positiveAction.setEnabled(true);
        }else{
            positiveAction.setEnabled(false); // disabled by default
        }

    }

    private class _autenticarUsuario extends AsyncTask<String, Integer, Boolean>{

        private final String NAMESPACE = "https://autenticacion2.uci.cu/v6/";
        private final String URL = "https://autenticacion2.uci.cu/v6/PasarelaAutenticacionWS.php";
        private final String METHOD_NAME = "AutenticarUsuario";
        private final String SOAP_ACTION = "https://autenticacion2.uci.cu/v6/AutenticarUsuario";

        String error = MainActivity.this.getResources().getString(R.string.error_con);
        String u;
        String c;

        MaterialDialog progress;

        @Override
        protected void onPreExecute() {
            persona = new LoginUser();
            progress = new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.autenticando)
                    .content(R.string.please_wait)
                    .theme(Theme.LIGHT)
                    .icon(MainActivity.this.getResources().getDrawable(R.drawable.ic_login))
                    .cancelable(false)
                    .contentGravity(GravityEnum.CENTER)
                    .progress(true, 0)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            _FakeX509TrustManager.allowAllSSL();

            u = params[0];
            c = params[1];

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Usuario", u);
            request.addProperty("Clave", c);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);
                SoapObject resSoap =(SoapObject)envelope.getResponse();

                Log.d("!!!!!", String.valueOf(resSoap.getProperty(15)));
                log = Boolean.valueOf(resSoap.getProperty(15).toString());
                Log.d("!!!!!", String.valueOf(log));
                if(log){
                String persnona_nombre = String.valueOf(resSoap.getProperty(1).toString());
                if(persnona_nombre.equalsIgnoreCase("anyType{}")){
                    error = MainActivity.this.getResources().getString(R.string.error_user_pass);
                    return false;
                }else{

                    SoapObject aux4;
                    SoapObject aux4_2;
                    SoapObject aux8;
                    SoapObject aux11;
                    SoapObject aux12;
                    SoapObject aux13;


                    persona.apellidos = String.valueOf(resSoap.getProperty(2).toString());
                    persona.dni = String.valueOf(resSoap.getProperty(5).toString());
                    persona.categoria = String.valueOf(resSoap.getProperty(6).toString());
                    persona.exp = String.valueOf(resSoap.getProperty(0).toString());
                    persona.credencial = String.valueOf(resSoap.getProperty(3).toString());
                    persona.usuario = String.valueOf(resSoap.getProperty(9).toString());
                    persona.correo = String.valueOf(resSoap.getProperty(10).toString());
                    SoapObject aux1 = (SoapObject) resSoap.getProperty(13);
                    persona.foto = String.valueOf(aux1.getProperty(1).toString());

                    persona.nombre = persnona_nombre;
                    persona.exp = String.valueOf(resSoap.getProperty(0).toString());
                    if(persona.exp.equalsIgnoreCase("anyType{}")){
                        persona.exp = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.apellidos = String.valueOf(resSoap.getProperty(2).toString());
                    persona.credencial = String.valueOf(resSoap.getProperty(3).toString());
                    if(persona.credencial.equalsIgnoreCase("anyType{}")){
                        persona.credencial = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.correo = String.valueOf(resSoap.getProperty(10).toString());
                    if(persona.correo.equalsIgnoreCase("anyType{}")){
                        persona.correo = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.dni = String.valueOf(resSoap.getProperty(5).toString());
                    if(persona.dni.equalsIgnoreCase("anyType{}")){
                        persona.dni = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.categoria = String.valueOf(resSoap.getProperty(6).toString());
                    if(persona.categoria.equalsIgnoreCase("anyType{}")){
                        persona.categoria = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }

                    aux4 = (SoapObject) resSoap.getProperty(4);
                    aux4_2 = (SoapObject) aux4.getProperty(2);
                    persona.municipio = String.valueOf(aux4.getProperty(1).toString());
                    if(persona.municipio.equalsIgnoreCase("anyType{}")){
                        persona.municipio = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.provincia = String.valueOf(aux4_2.getProperty(1).toString());
                    if(persona.provincia.equalsIgnoreCase("anyType{}")){
                        persona.provincia = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    aux8 = (SoapObject) resSoap.getProperty(8);
                    persona.sexo = String.valueOf(aux8.getProperty(1).toString());
                    if(persona.sexo.equalsIgnoreCase("anyType{}")){
                        persona.sexo = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.usuario = String.valueOf(resSoap.getProperty(9).toString());
                    if(persona.usuario.equalsIgnoreCase("anyType{}")){
                        persona.usuario = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    aux11 = (SoapObject) resSoap.getProperty(11);
                    persona.edificio = String.valueOf(aux11.getProperty(0).toString());
                    if(persona.edificio.equalsIgnoreCase("anyType{}")){
                        persona.edificio = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.apto = String.valueOf(aux11.getProperty(1).toString());
                    if(persona.apto.equalsIgnoreCase("anyType{}")){
                        persona.apto = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    persona.tel = String.valueOf(aux11.getProperty(2).toString());
                    if(persona.tel.equalsIgnoreCase("anyType{}")){
                        persona.tel = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    aux12 = (SoapObject) resSoap.getProperty(12);
                    persona.area = String.valueOf(aux12.getProperty(1).toString());
                    if(persona.area.equalsIgnoreCase("anyType{}")){
                        persona.area = MainActivity.this.getResources().getString(R
                                .string.no_disponible);
                    }
                    aux13 = (SoapObject) resSoap.getProperty(13);
                    persona.foto = String.valueOf(aux13.getProperty(1).toString());
                }
                }else{
                    error = MainActivity.this.getResources().getString(R.string.error_user_pass);
                    return false;
                }

            }
            catch (Exception e)
            {
                Log.d("!!!!!", e.getMessage());
                error = MainActivity.this.getResources().getString(R.string.error_con);
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progress.dismiss();
            log_numbers ++;
            if(aBoolean){
                Animation pop = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fab_pop);
                fab.startAnimation(pop);
                fab.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable
                        .ic_fa_unlock));
                fillBundle(persona.nombre, persona.apellidos, persona.foto, persona.edificio,
                        persona.area, persona.dni, persona.apto,persona.exp, persona.credencial,
                        persona.usuario, persona.correo, persona.tel, persona.categoria,
                        persona.provincia,persona.municipio, persona.sexo, c
                        );
                if(save_to_auth){

                    authDB.openAuthDatabase();
                    authDB.saveAuth(user_to_save, pass_to_save);
                    authDB.closeAuthDatabase();

                    editor.putBoolean(CampusPreference.REMEMBER_ME, true);
                    editor.commit();

                }else{
                    authDB.openAuthDatabase();
                    int temp = authDB.deleteAuth(user_to_save);
                    if(temp == 0){
                        String[] test = authDB.loadAuth();
                        if(test[0].length() == 0){
                            editor.putBoolean(CampusPreference.REMEMBER_ME, false);
                            editor.commit();
                        }else{
                            editor.putBoolean(CampusPreference.REMEMBER_ME, true);
                            editor.commit();
                        }
                    }else{
                        editor.putBoolean(CampusPreference.REMEMBER_ME, false);
                        editor.commit();
                    }
                    authDB.closeAuthDatabase();

                }
            }else{
                Animation pop = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fab_pop);
                fab.startAnimation(pop);
                fab.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable
                        .ic_login));
                fillBundle(null, null, null,null, null, null,null, null, null,null,
                        null, null,null, null, null, null, null);



            }
            sendNotification(aBoolean, error);
            super.onPostExecute(aBoolean);
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
                .log_in) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendSuccessToast(){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string.log_in) + "\n" + getResources().getString(R
                .string.correct_login) + " " + persona.nombre);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();

    }

     private void fillBundle(String n, String a, String f,String e,String ar, String d, String ap
             ,String exp, String cred, String user,String corr,String tel, String cat, String prov
             ,String mun, String sex, String pass){
         bundle.putString("NOMBRE",n);
         bundle.putString("APELLIDOS",a);
         bundle.putString("FOTO",f);
         bundle.putString("EDIFICIO",e);
         bundle.putString("AREA",ar);
         bundle.putString("DNI",d);
         bundle.putString("APTO",ap);
         bundle.putString("EXP",exp);
         bundle.putString("CRED",cred);
         bundle.putString("USER",user);
         bundle.putString("CORREO",corr);
         bundle.putString("TEL",tel);
         bundle.putString("CAT",cat);
         bundle.putString("PROV",prov);
         bundle.putString("MUN",mun);
         bundle.putString("SEXO",sex);
         bundle.putString("PASS",pass);
}

    private boolean isInHome(){
        if(
                (MenuActivity.isInFront==null || MenuActivity.isInFront==false) &&
                        (RssActivity.isInFront==null || RssActivity.isInFront==false) &&
                        (HorarioActivity.isInFront==null || HorarioActivity.isInFront==false)&&
                        (MapaActivity.isInFront==null || MapaActivity.isInFront==false) &&
                        (AboutActivity.isInFront==null || AboutActivity.isInFront==false)&&
                        (CuotaActivity.isInFront==null || CuotaActivity.isInFront==false) &&
                        (DirectorioActivity.isInFront==null || DirectorioActivity.isInFront==false) &&
                        (ChatActivity.isInFront==null || ChatActivity.isInFront==false) &&
                        (isInFront==null || isInFront==false)){

            return true;
        }

        return false;
    }

    private void sendNotification(Boolean aBoolean, String texto){
        if(aBoolean && isInHome()){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_login);
            notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_login)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.log_in));

            notificationCompatBuilder.setContentText(getResources().getString(R
                    .string.correct_login) + " " + persona.nombre);
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .log_in));
            notificationCompatBuilder.setAutoCancel(true);
            notificationCompatBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

            Intent notIntent =
                    new Intent(MainActivity.this, MainActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    MainActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_MAIN,
                    notificationCompatBuilder.build());

        }else if(!aBoolean && isInHome()){

            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_login);
            notificationCompatBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_login)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                    .not_log));

            notificationCompatBuilder.setContentText(getResources().getString(R.string
                    .error_con));
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .not_log));
            notificationCompatBuilder.setAutoCancel(true);
            notificationCompatBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

            Intent notIntent =
                    new Intent(MainActivity.this, MainActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    MainActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_MAIN,
                    notificationCompatBuilder.build());
        }else{if(aBoolean && isInFront){
            sendSuccessToast();
        }else if(aBoolean && !isInFront){
                sendSuccessToast();
        }else if(!aBoolean && !isInHome()){
                sendErrorToast(texto);
            }
        }
    }


}
