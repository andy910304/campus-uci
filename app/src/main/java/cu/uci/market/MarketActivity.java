package cu.uci.market;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import cu.uci.campusuci.CampusNotification;
import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.MainActivity;
import cu.uci.campusuci.NavigationDrawer;
import cu.uci.campusuci.R;
import cu.uci.rss.CustomRss;
import cu.uci.rss.CustomRssDataBase;
import cu.uci.rss.MySpinnerAdapter;
import cu.uci.rss.RssDataBase;
import cu.uci.utils.FloatingButtonLib.ActionButton;

/**
 * Created by Yannier on 6/8/2015.
 */
public class MarketActivity extends ActionBarActivity implements SensorEventListener{

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

    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_condense_bold, roboto_light, roboto_condensed_light_italic;
    Toolbar mToolbar;
    ActionButton fab;
    ViewPager vp;

    View shadow;

    Boolean drawerIn;
    public static Boolean isInFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        this.ctx = this;
        initToolBar_FAB();
        initViewPager();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_MARKET);
        super.onStart();
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
    protected void onStop() {
        isInFront = false;
        drawerLayout.closeDrawers();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_MARKET);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else{
            Intent i1 = new Intent(MarketActivity.this,
                    MainActivity.class);
            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i1);
            //super.onBackPressed();
        }
    }

    private void initViewPager(){

        vp = (ViewPager) findViewById(R.id.vp_categories);
        //vp.setAdapter(MyVpAdapter);
    }

    private void initToolBar_FAB(){

        drawerIn = false;
        isInFront = true;
        drawer = new NavigationDrawer();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor != null){
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();

        notificationCompatBuilder = new NotificationCompat.Builder(MarketActivity.this);
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
        mToolbar.inflateMenu(R.menu.menu_market);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);

        //setSupportActionBar(mToolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.ic_launcher);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
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


                }
                return true;
            }
        });

        fab = (ActionButton) findViewById(R.id.fab_market);
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
                Animation animation_fab = AnimationUtils.loadAnimation(MarketActivity.this,
                        R.anim.fab_rotate);
                fab.startAnimation(animation_fab);
                fab.setClickable(false);

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class MyVpAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 16;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = ((LayoutInflater)container.getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE));
            View v = null;
            switch(position){

            }

            return 1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((LinearLayout)object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {

        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
