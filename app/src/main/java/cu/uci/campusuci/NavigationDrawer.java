package cu.uci.campusuci;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import cu.uci.chatty.ChatActivity;
import cu.uci.cuota.CuotaActivity;
import cu.uci.directorio.DirectorioActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.mapa.MapaActivity;
import cu.uci.market.MarketActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.RssActivity;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;

/**
 * Created by Yannier on 3/24/2015.
 */
public class NavigationDrawer extends android.support.v4.app.Fragment implements View.OnClickListener{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    AssetManager am;
    Typeface roboto_condense_light;

    TextView nav_inicio;
    TextView nav_rss;
    TextView nav_directorio;
    TextView nav_cuota;
    TextView nav_horario;
    TextView nav_menu;
    TextView nav_mapa;
    TextView nav_chat;
    TextView nav_market;
    TextView nav_sobre;
    TextView nav_salir;
    TextView nav_settings;
    TextView user_name;

    LinearLayout ll_nav_inicio;
    LinearLayout ll_nav_rss;
    LinearLayout ll_nav_directorio;
    LinearLayout ll_nav_cuota;
    LinearLayout ll_nav_horario;
    LinearLayout ll_nav_menu;
    LinearLayout ll_nav_mapa;
    LinearLayout ll_nav_chat;
    LinearLayout ll_nav_market;
    LinearLayout ll_nav_sobre;
    LinearLayout ll_nav_salir;
    LinearLayout ll_nav_settings;
    LinearLayout ll_header;

    public static ImageView nav_rss_here;
    public static ImageView nav_directorio_here;
    public static ImageView nav_cuota_here;
    public static ImageView nav_horario_here;
    public static ImageView nav_menu_here;
    public static ImageView nav_sobre_here;
    public static ImageView nav_chat_here;
    public static ImageView nav_market_here;
    public static ImageView nav_user_picture;

    public static ImageView nav_rss_stat;
    public static ImageView nav_directorio_stat;
    public static ImageView nav_cuota_stat;
    public static ImageView nav_horario_stat;
    public static ImageView nav_menu_stat;
    public static ImageView nav_mapa_stat;
    public static ImageView nav_chat_stat;
    public static ImageView nav_market_stat;

    LoginUser user;
    int log_numbers_from_main = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        am = getActivity().getAssets();
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");
        this.user = MainActivity.persona;

        sharedPreferences = NavigationDrawer.this.getActivity().getSharedPreferences
                (CampusPreference.CAMPUS_PREFERENCE, NavigationDrawer.this.getActivity()
                        .getBaseContext().MODE_PRIVATE);
        editor = NavigationDrawer.this.getActivity().getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE,
                NavigationDrawer.this.getActivity()
                .getBaseContext().MODE_PRIVATE).edit();

        nav_inicio = (TextView) getView().findViewById(R.id.nav_inicio);
        nav_rss = (TextView) getView().findViewById(R.id.nav_rss);
        nav_directorio = (TextView) getView().findViewById(R.id.nav_directorio);
        nav_cuota = (TextView) getView().findViewById(R.id.nav_cuota);
        nav_horario = (TextView) getView().findViewById(R.id.nav_horario);
        nav_menu = (TextView) getView().findViewById(R.id.nav_menu);
        nav_mapa = (TextView) getView().findViewById(R.id.nav_mapa);
        nav_chat = (TextView) getView().findViewById(R.id.nav_chat);
        nav_market = (TextView) getView().findViewById(R.id.nav_market);
        nav_sobre = (TextView) getView().findViewById(R.id.nav_sobre);
        nav_salir = (TextView) getView().findViewById(R.id.nav_salir);
        nav_settings = (TextView) getView().findViewById(R.id.nav_settings);
        user_name = (TextView) getView().findViewById(R.id.nav_user_name);

        nav_inicio.setTypeface(roboto_condense_light);
        nav_rss.setTypeface(roboto_condense_light);
        nav_directorio.setTypeface(roboto_condense_light);
        nav_cuota.setTypeface(roboto_condense_light);
        nav_horario.setTypeface(roboto_condense_light);
        nav_menu.setTypeface(roboto_condense_light);
        nav_mapa.setTypeface(roboto_condense_light);
        nav_chat.setTypeface(roboto_condense_light);
        nav_market.setTypeface(roboto_condense_light);
        nav_sobre.setTypeface(roboto_condense_light);
        nav_salir.setTypeface(roboto_condense_light);
        nav_settings.setTypeface(roboto_condense_light);
        user_name.setTypeface(roboto_condense_light);


        ll_nav_inicio = (LinearLayout) getView().findViewById(R.id.ll_inicio_nav);
        ll_nav_rss = (LinearLayout) getView().findViewById(R.id.ll_rss_nav);
        ll_nav_directorio = (LinearLayout) getView().findViewById(R.id.ll_directorio_nav);
        ll_nav_cuota = (LinearLayout) getView().findViewById(R.id.ll_cuota_nav);
        ll_nav_horario = (LinearLayout) getView().findViewById(R.id.ll_horario_nav);
        ll_nav_menu = (LinearLayout) getView().findViewById(R.id.ll_menu_nav);
        ll_nav_mapa = (LinearLayout) getView().findViewById(R.id.ll_mapa_nav);
        ll_nav_chat = (LinearLayout) getView().findViewById(R.id.ll_chat_nav);
        ll_nav_market = (LinearLayout) getView().findViewById(R.id.ll_market_nav);
        ll_nav_sobre = (LinearLayout) getView().findViewById(R.id.ll_sobre_nav);
        ll_nav_salir = (LinearLayout) getView().findViewById(R.id.ll_salir_nav);
        ll_nav_settings = (LinearLayout) getView().findViewById(R.id.ll_settings_nav);
        ll_header = (LinearLayout) getView().findViewById(R.id.ll_iv_header);

        ll_nav_inicio.setOnClickListener(this);
        ll_nav_rss.setOnClickListener(this);
        ll_nav_directorio.setOnClickListener(this);
        ll_nav_cuota.setOnClickListener(this);
        ll_nav_horario.setOnClickListener(this);
        ll_nav_menu.setOnClickListener(this);
        ll_nav_mapa.setOnClickListener(this);
        ll_nav_chat.setOnClickListener(this);
        ll_nav_market.setOnClickListener(this);
        ll_nav_sobre.setOnClickListener(this);
        ll_nav_salir.setOnClickListener(this);
        ll_nav_settings.setOnClickListener(this);
        ll_header.setOnClickListener(this);

        nav_rss_here = (ImageView) getView().findViewById(R.id.nav_rss_here);
        nav_directorio_here = (ImageView) getView().findViewById(R.id.nav_directorio_here);
        nav_cuota_here = (ImageView) getView().findViewById(R.id.nav_cuota_here);
        nav_horario_here = (ImageView) getView().findViewById(R.id.nav_horario_here);
        nav_menu_here = (ImageView) getView().findViewById(R.id.nav_menu_here);
        nav_sobre_here = (ImageView) getView().findViewById(R.id.nav_sobre_here);
        nav_user_picture = (ImageView) getView().findViewById(R.id.nav_user_picture);
        nav_chat_here = (ImageView) getView().findViewById(R.id.nav_chat_here);
        nav_market_here = (ImageView) getView().findViewById(R.id.nav_market_here);

        nav_rss_stat = (ImageView) getView().findViewById(R.id.nav_rss_stat);
        nav_directorio_stat = (ImageView) getView().findViewById(R.id.nav_directorio_stat);
        nav_cuota_stat = (ImageView) getView().findViewById(R.id.nav_cuota_stat);
        nav_horario_stat = (ImageView) getView().findViewById(R.id.nav_horario_stat);
        nav_menu_stat = (ImageView) getView().findViewById(R.id.nav_menu_stat);
        nav_mapa_stat = (ImageView) getView().findViewById(R.id.nav_mapa_stat);
        nav_chat_stat = (ImageView) getView().findViewById(R.id.nav_chat_stat);
        nav_market_stat = (ImageView) getView().findViewById(R.id.nav_market_stat);

        if(NavigationDrawer.this.getActivity() instanceof RssActivity){
            nav_rss_here.setImageResource(R.drawable.here);
            nav_rss_here.setVisibility(View.VISIBLE);

        }else if(NavigationDrawer.this.getActivity() instanceof DirectorioActivity){
            nav_directorio_here.setImageResource(R.drawable.here);
            nav_directorio_here.setVisibility(View.VISIBLE);

        }else if(NavigationDrawer.this.getActivity() instanceof CuotaActivity){
            nav_cuota_here.setImageResource(R.drawable.here);
            nav_cuota_here.setVisibility(View.VISIBLE);

        }else if(NavigationDrawer.this.getActivity() instanceof HorarioActivity){
            nav_horario_here.setImageResource(R.drawable.here);
            nav_horario_here.setVisibility(View.VISIBLE);

        }else if(NavigationDrawer.this.getActivity() instanceof MenuActivity){
            nav_menu_here.setImageResource(R.drawable.here);
            nav_menu_here.setVisibility(View.VISIBLE);

        }else if(NavigationDrawer.this.getActivity() instanceof AboutActivity){
            nav_sobre_here.setImageResource(R.drawable.here);
            nav_sobre_here.setVisibility(View.VISIBLE);
        }else if(NavigationDrawer.this.getActivity() instanceof ChatActivity){
            nav_chat_here.setImageResource(R.drawable.here);
            nav_chat_here.setVisibility(View.VISIBLE);
        }else if(NavigationDrawer.this.getActivity() instanceof MarketActivity){
            nav_market_here.setImageResource(R.drawable.here);
            nav_market_here.setVisibility(View.VISIBLE);
        }
        unlockOptionsNav();

    }

    @Override
    public void onResume() {
        unlockOptionsNav();
        //nav_user_picture.invalidate();
        super.onResume();
    }

    @Override
    public void onStart() {

        if(this.log_numbers_from_main != MainActivity.log_numbers){
            Log.d("!!!!!!!!!!!", "" + log_numbers_from_main + ",,,," + MainActivity.log_numbers);
            log_numbers_from_main = MainActivity.log_numbers;
                this.user = MainActivity.persona;
                if(this.user.nombre != null){
                    Log.d("!!!!!!!!!!!!!!", this.user.nombre);
                    user_name.setText(this.user.nombre + " " + this.user.apellidos);
                    user_name.invalidate();
                    Picasso.with(NavigationDrawer.this.getActivity().getBaseContext()).load(this.user.foto)
                            .into(nav_user_picture);
                    //nav_user_picture.invalidate();
                }else{
                    user_name.setText(NavigationDrawer.this.getActivity()
                            .getResources().getString(R.string.not_log));
                    user_name.invalidate();
                    nav_user_picture.setImageResource(R.drawable.empty_user);
                    //nav_user_picture.invalidate();

                }
        }
        super.onStart();
    }

    private void lockOptionsNav(){
        ll_nav_inicio.setClickable(false);
        ll_nav_rss.setClickable(false);
        ll_nav_directorio.setClickable(false);
        ll_nav_cuota.setClickable(false);
        ll_nav_horario.setClickable(false);
        ll_nav_menu.setClickable(false);
        ll_nav_mapa.setClickable(false);
        ll_nav_chat.setClickable(false);
        ll_nav_market.setClickable(false);
        ll_nav_settings.setClickable(false);
        ll_nav_sobre.setClickable(false);
        ll_nav_salir.setClickable(false);
    }

    private void unlockOptionsNav(){
        ll_nav_inicio.setClickable(true);
        ll_nav_rss.setClickable(true);
        ll_nav_directorio.setClickable(true);
        ll_nav_cuota.setClickable(true);
        ll_nav_horario.setClickable(true);
        ll_nav_menu.setClickable(true);
        ll_nav_mapa.setClickable(true);
        ll_nav_chat.setClickable(true);
        ll_nav_market.setClickable(true);
        ll_nav_settings.setClickable(true);
        ll_nav_sobre.setClickable(true);
        ll_nav_salir.setClickable(true);
    }

    @Override
    public void onClick(View v) {
        Thread t;

        switch (v.getId()){
            case R.id.ll_inicio_nav:
                lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                Intent i1 = new Intent(NavigationDrawer.this.getActivity(),
                                        MainActivity.class);
                                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i1);
                                //NavigationDrawer.this.getActivity().overridePendingTransition(R
                                 //               .anim.activity_fade_in,
                                  //      R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();

                break;

            case R.id.ll_rss_nav:
                if(NavigationDrawer.this.getActivity() instanceof RssActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_rss_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        RssActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_directorio_nav:
                if(NavigationDrawer.this.getActivity() instanceof DirectorioActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_directorio_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        DirectorioActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_cuota_nav:
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO){
                    Toast toast = Toast.makeText(NavigationDrawer.this.getActivity().getApplicationContext(),
                            getResources().getString(R.string.sdk_min_),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }else{
                    if(NavigationDrawer.this.getActivity() instanceof CuotaActivity){
                        Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                                .getActivity().getApplicationContext(), R.anim.here_scale);
                        nav_cuota_here.startAnimation(animation_here);
                    }else{
                        lockOptionsNav();
                        t = new Thread(){
                            public void run(){
                                try {
                                    sleep(320);
                                    startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                            CuotaActivity.class).putExtras(MainActivity.bundle));
                                    NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                    .anim.activity_fade_in,
                                            R.anim.activity_fade_out);
                                    //NavigationDrawer.this.getActivity().finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                }
                break;

            case R.id.ll_horario_nav:
                if(NavigationDrawer.this.getActivity() instanceof HorarioActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_horario_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        HorarioActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_menu_nav:
                if(NavigationDrawer.this.getActivity() instanceof MenuActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_menu_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        MenuActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_mapa_nav:
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
                    Toast toast = Toast.makeText(NavigationDrawer.this.getActivity().getApplicationContext(),
                            getResources().getString(R.string.sdk_min),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }else {
                    if (NavigationDrawer.this.getActivity() instanceof MapaActivity) {

                    } else {
                        lockOptionsNav();
                        t = new Thread() {
                            public void run() {
                                try {
                                    sleep(320);
                                    startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                            MapaActivity.class));
                                    NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                    .anim.activity_fade_in,
                                            R.anim.activity_fade_out);
                                    //NavigationDrawer.this.getActivity().finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                }
                break;

            case R.id.ll_chat_nav:
                if(NavigationDrawer.this.getActivity() instanceof ChatActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_chat_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        ChatActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_market_nav:
                if(NavigationDrawer.this.getActivity() instanceof MarketActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_market_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        MarketActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_sobre_nav:
                if(NavigationDrawer.this.getActivity() instanceof AboutActivity){
                    Animation animation_here = AnimationUtils.loadAnimation(NavigationDrawer.this
                            .getActivity().getApplicationContext(), R.anim.here_scale);
                    nav_sobre_here.startAnimation(animation_here);
                }else{
                    lockOptionsNav();
                    t = new Thread(){
                        public void run(){
                            try {
                                sleep(320);
                                startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                        AboutActivity.class).putExtras(MainActivity.bundle));
                                NavigationDrawer.this.getActivity().overridePendingTransition(R
                                                .anim.activity_fade_in,
                                        R.anim.activity_fade_out);
                                //NavigationDrawer.this.getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
                break;

            case R.id.ll_salir_nav:
                lockOptionsNav();
                if(NavigationDrawer.this.getActivity() instanceof AboutActivity){
                    t = new Thread(){
                         public void run(){
                          try {
                             sleep(320);
                              Intent i1 = new Intent(NavigationDrawer.this.getActivity(),
                                      MainActivity.class);
                              i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                              startActivity(i1);
                              NavigationDrawer.this.getActivity().overridePendingTransition(R.anim
                                              .activity_fade_in,
                                      R.anim.activity_fade_out);
                              NavigationDrawer.this.getActivity().finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                         }
                         }
                        };
                        t.start();
                }else{
                    boolean close_alert;
                    if(sharedPreferences.contains(CampusPreference.CLOSE_ALERTS)){
                        close_alert = sharedPreferences.getBoolean(CampusPreference.CLOSE_ALERTS,
                                false);
                    }else{
                        close_alert = false;
                    }

                    if(close_alert){
                        showCloseDialog();
                    }else{
                        t = new Thread(){
                            public void run(){
                                try {
                                    sleep(320);
                                    Intent i1 = new Intent(NavigationDrawer.this.getActivity(),
                                            MainActivity.class);
                                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i1);
                                    NavigationDrawer.this.getActivity().overridePendingTransition(R.anim
                                                    .activity_fade_in,
                                            R.anim.activity_fade_out);
                                    NavigationDrawer.this.getActivity().finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }

                }
                break;

            case R.id.ll_settings_nav:
                lockOptionsNav();
                t = new Thread(){
                    public void run(){
                        try {
                            sleep(320);
                            startActivity(new Intent(NavigationDrawer.this.getActivity(),
                                    PreferenceActivity.class).putExtra(INTENTSENDER.INTENT_SENDER,
                                    getIntentSender()));
                            NavigationDrawer.this.getActivity().overridePendingTransition(R.anim
                                            .activity_fade_in,
                                    R.anim.activity_fade_out);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;

            case R.id.ll_iv_header:
                //lockOptionsNav();
                if(this.user.nombre != null){
                 //   t = new Thread(){
                   //     public void run(){
                   //         try {
                    //           sleep(320);
                                showMeDialog();
                     //       } catch (InterruptedException e) {
                     //           e.printStackTrace();
                     //       }
                      //  }
                    //};
                    //t.start();
                }

                break;
        }
    }

    private String getIntentSender(){

        if(NavigationDrawer.this.getActivity() instanceof AboutActivity){
            return INTENTSENDER.ABOUT_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof RssActivity){
            return INTENTSENDER.RSS_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof DirectorioActivity){
            return INTENTSENDER.DIRECTORIO_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof CuotaActivity){
            return INTENTSENDER.CUOTA_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof HorarioActivity){
            return INTENTSENDER.HORARIO_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof ChatActivity){
            return INTENTSENDER.CHAT_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof MenuActivity) {
            return INTENTSENDER.MENU_ACTIVITY;
        }else if(NavigationDrawer.this.getActivity() instanceof MarketActivity) {
            return INTENTSENDER.MARKET_ACTIVITY;
        }

        return INTENTSENDER.MAIN_ACTIVITY;
    }

    private void showMeDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(NavigationDrawer.this.getActivity())
                .title(R.string.profile)
                .icon(NavigationDrawer.this.getActivity().getResources().getDrawable(R.drawable.ic_fa_unlock))
                .customView(R.layout.me_layout, true)
                .negativeText(R.string.cancel)
                .positiveText(R.string.salvar)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        intent.putExtra(ContactsContract.Intents.Insert.NAME,
                                user.nombre + " " + user.apellidos);
                        intent.putExtra(ContactsContract.Intents.Insert.EMAIL,
                                    user.correo);
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE,
                                    user.tel);
                        intent.putExtra(ContactsContract.Intents.Insert.NOTES,
                                user.dni + ", " + user.categoria + ", " + user.provincia + ", " +
                                        "" + user.municipio + ", " + user.credencial + ", " +
                                        "" + user.exp + ", " + user.apto + ", " + user.sexo);
                        NavigationDrawer.this.getActivity().startActivity(intent);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();
                dialog.show();

        ImageView imageView = (ImageView) dialog.findViewById(R.id.login_picture_);

        TextView tvName = (TextView) dialog.findViewById(R.id.tvName_);
        TextView tvDni = (TextView) dialog.findViewById(R.id.tvDni_);
        TextView tvCategory = (TextView)dialog.findViewById(R.id.tvCategory_);
        TextView tvBuilding = (TextView)dialog.findViewById(R.id.tvBuilding_);
        TextView tvPhone = (TextView)dialog.findViewById(R.id.tvPhone_);
        TextView tvEmail = (TextView)dialog.findViewById(R.id.tvEmail_);
        TextView tvSexo = (TextView)dialog.findViewById(R.id.tvSexo_);
        TextView tvCredencial = (TextView) dialog.findViewById(R.id.tvCredencial_);
        TextView tvProv_Mun = (TextView) dialog.findViewById(R.id.tvProv_Mun_);
        TextView tvUser = (TextView)dialog.findViewById(R.id.tvUser_);
        TextView tvArea = (TextView) dialog.findViewById(R.id.tvArea_);
        TextView tvExp = (TextView) dialog.findViewById(R.id.tvExp_);

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

        Picasso.with(NavigationDrawer.this.getActivity()).load(this.user.foto).into(imageView);

        tvName.setText(this.user.nombre + " " + this.user.apellidos);
        tvDni.setText(this.user.dni);
        tvCategory.setText(this.user.categoria);
        tvCredencial.setText(this.user.credencial);
        tvProv_Mun.setText(this.user.provincia + "," + this.user.municipio);
        tvBuilding.setText(this.user.apto);
        tvPhone.setText(this.user.tel);
        tvEmail.setText(this.user.correo);
        tvSexo.setText(this.user.sexo);
        tvUser.setText(this.user.usuario);
        tvExp.setText(this.user.exp);
    }





    private CheckBox check;

    private void showCloseDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(NavigationDrawer.this
                .getActivity())
                .title(R.string.warning)
                .icon(NavigationDrawer.this.getActivity().getResources().getDrawable(R.drawable
                        .ic_dialog_sync_error))
                .content(R.string.cancel_anyway)
                .customView(R.layout.dialog_close, true)
                .positiveText(R.string.accept)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .negativeText(android.R.string.cancel)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        unlockOptionsNav();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        unlockOptionsNav();
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (check.isChecked()) {
                            editor.putBoolean(CampusPreference.CLOSE_ALERTS, false);
                            editor.commit();
                        }
                        Intent i1 = new Intent(NavigationDrawer.this.getActivity(),
                                MainActivity.class);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i1);
                        NavigationDrawer.this.getActivity().overridePendingTransition(R.anim
                                        .activity_fade_in,
                                R.anim.activity_fade_out);
                        NavigationDrawer.this.getActivity().finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        unlockOptionsNav();
                    }

                }).build();


        // Toggling the show password CheckBox will mask or unmask the password input EditText
        check = (CheckBox) dialog.getCustomView().findViewById(R.id.not_show);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        check.setChecked(false);dialog.show();
    }

    private void sendErrorToast(String texto){
        LayoutInflater inflater = NavigationDrawer.this.getActivity().getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) getView().findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_condense_light);
        t.setText(getResources().getString(R.string
                .log_in) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));

        Toast toast = new Toast(NavigationDrawer.this.getActivity());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

}
