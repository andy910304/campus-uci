package cu.uci.campusuci;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cu.uci.utils.FloatingButtonLib.ActionButton;


public class AboutActivity extends ActionBarActivity implements DrawerLayout.DrawerListener{

    Activity ctx;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    public static NavigationDrawer drawer;

    AssetManager am;
    Typeface roboto_bold_italic, roboto_condense_light;
    Toolbar mToolbar;
    ActionButton fab_contactus, fab_sms, fab_email;
    ImageView logo_id;
    TextView tvDB, tvYannier, tvAndy, tvVersion;
    Boolean areIn;
    Boolean drawerIn;
    View shadow;
    public static Boolean isInFront;

    public static LoginUser me;
    public static Bundle bundle;

    static final String[] email = { "ygpedroso@estudiantes.uci.cu", "alromero@estudiantes.uci.cu" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ctx = this;

        initAboutView();
        initRestViews();
        initFabs();
        //initBundle();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            entry();
        }

        //setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(R.color
        //        .primary_dark_color));

        setStatusBarColor();

    }

    public void setStatusBarColor() {
        LinearLayout lx = (LinearLayout) findViewById(R.id.ll_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build
                .VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            //int actionBarHeight = getActionBarHeight();
            //int statusBarHeight = getStatusBarHeight();
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
        finish();
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
        super.onStart();
    }

    @Override
    protected void onRestart() {
        //drawer.nav_rss_stat.setVisibility(View.INVISIBLE);
        super.onRestart();
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

    private void initAboutView(){
        isInFront = true;
        drawer = new NavigationDrawer();

        shadow = findViewById(R.id.shadow_toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            shadow.setMinimumHeight(4);
        }

        drawerIn = false;
        areIn = false;

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
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
    }

    private void initRestViews(){

        am = getAssets();
        roboto_bold_italic = Typeface.createFromAsset(am,"roboto_bold_italic.ttf");
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");

        logo_id = (ImageView) findViewById(R.id.logo_id);

        tvDB = (TextView) findViewById(R.id.tvDB);
        tvDB.setTypeface(roboto_condense_light);
        tvYannier = (TextView) findViewById(R.id.tvyannier);
        tvYannier.setTypeface(roboto_bold_italic);
        tvAndy = (TextView) findViewById(R.id.tvandy);
        tvAndy.setTypeface(roboto_bold_italic);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setTypeface(roboto_bold_italic);

    }

    private void initFabs(){

        fab_email = (ActionButton) findViewById(R.id.fab_email);
        fab_email.setType(ActionButton.Type.DEFAULT);
        fab_email.setButtonColor(getResources().getColor(R.color.fab_email));
        fab_email.setButtonColorPressed(getResources().getColor(R.color.fab_email_pressed));
        fab_email.setImageResource(R.drawable.ic_action_email);
        fab_email.setShadowRadius(4.2f);
        fab_email.setShadowXOffset(2.8f);
        fab_email.setShadowYOffset(2.1f);
        fab_email.setStrokeColor(getResources().getColor(R.color.fab_email_pressed));
        fab_email.setStrokeWidth(0.0f);

        fab_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(AboutActivity.this, R.anim.fab_pop);
                fab_email.startAnimation(pop);

                Intent intent = new Intent(
                        android.content.Intent.ACTION_SEND);
                intent.putExtra(
                        android.content.Intent.EXTRA_SUBJECT,
                        AboutActivity.this.getResources().getString(R.string.dudas_sugerencias));
                intent.putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        email);
                intent.setType("plain/text");
                startActivity(intent);
            }
        });

        fab_sms = (ActionButton) findViewById(R.id.fab_sms);
        fab_sms.setType(ActionButton.Type.DEFAULT);
        fab_sms.setButtonColor(getResources().getColor(R.color.fab_sms));
        fab_sms.setButtonColorPressed(getResources().getColor(R.color.fab_sms_pressed));
        fab_sms.setImageResource(R.drawable.ic_sms);
        fab_sms.setShadowRadius(4.4f);
        fab_sms.setShadowXOffset(2.8f);
        fab_sms.setShadowYOffset(2.1f);
        fab_sms.setStrokeColor(getResources().getColor(R.color.fab_sms));
        fab_sms.setStrokeWidth(0.0f);

        fab_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(AboutActivity.this, R.anim.fab_pop);
                fab_sms.startAnimation(pop);

                Uri uri = Uri.parse("smsto:" + "52542984; 53571620");
                Intent intent = new Intent(
                        Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", AboutActivity.this.getResources().getString(R.string
                        .dudas_sugerencias) + ":");
                try{
                    startActivity(intent);
                }catch (Exception e){

                }

            }
        });

        fab_contactus = (ActionButton) findViewById(R.id.fab_contactus);
        fab_contactus.setType(ActionButton.Type.DEFAULT);
        fab_contactus.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab_contactus.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab_contactus.setImageResource(R.drawable.ic_action_send_now);
        fab_contactus.setShadowRadius(4.4f);
        fab_contactus.setShadowXOffset(2.8f);
        fab_contactus.setShadowYOffset(2.1f);
        fab_contactus.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab_contactus.setStrokeWidth(0.0f);

        fab_contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(AboutActivity.this, R.anim.fab_pop);
                fab_contactus.startAnimation(pop);

                if(!areIn){

                    Animation animation_fab_email = AnimationUtils.loadAnimation(AboutActivity.this,
                            R.anim.fab_roll_from_down);
                    Animation animation_fab_sms = AnimationUtils.loadAnimation(AboutActivity.this,
                            R.anim.fab_roll_from_down);

                    fab_email.setVisibility(View.VISIBLE);
                    fab_sms.setVisibility(View.VISIBLE);

                    fab_email.startAnimation(animation_fab_email);
                    fab_sms.startAnimation(animation_fab_sms);

                    fab_contactus.setImageResource(R.drawable.ic_action_undo);
                    areIn = true;

                }else{

                    Animation animation_fab_email = AnimationUtils.loadAnimation(AboutActivity.this,
                            R.anim.fab_roll_to_down);
                    Animation animation_fab_sms = AnimationUtils.loadAnimation(AboutActivity.this,
                            R.anim.fab_roll_to_down);

                    fab_email.startAnimation(animation_fab_email);
                    fab_sms.startAnimation(animation_fab_sms);

                    fab_email.setVisibility(View.INVISIBLE);
                    fab_sms.setVisibility(View.INVISIBLE);

                    fab_contactus.setImageResource(R.drawable.ic_action_send_now);
                    areIn = false;

                }


            }
        });

    }

    private void entry(){

        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.iv_logo);
        Animation animation_fab = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
        Animation animation_toolbar_in = AnimationUtils.loadAnimation(this, R.anim.toolbar_in);
        Animation animation_shadow_in = AnimationUtils.loadAnimation(this, R.anim.shadow_in);

        logo_id.startAnimation(animation_logo);
        fab_contactus.startAnimation(animation_fab);
        tvDB.startAnimation(animation_logo);
        tvYannier.startAnimation(animation_logo);
        tvAndy.startAnimation(animation_logo);
        tvVersion.startAnimation(animation_logo);
        mToolbar.startAnimation(animation_toolbar_in);
        shadow.startAnimation(animation_shadow_in);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
        }else if(areIn){
            Animation animation_fab_email = AnimationUtils.loadAnimation(AboutActivity.this,
                    R.anim.fab_roll_to_down);
            Animation animation_fab_sms = AnimationUtils.loadAnimation(AboutActivity.this,
                    R.anim.fab_roll_to_down);

            fab_email.startAnimation(animation_fab_email);
            fab_sms.startAnimation(animation_fab_sms);

            fab_email.setVisibility(View.INVISIBLE);
            fab_sms.setVisibility(View.INVISIBLE);

            fab_contactus.setImageResource(R.drawable.ic_action_send_now);
            areIn = false;
        }else{
            Intent i1 = new Intent(AboutActivity.this,
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
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
