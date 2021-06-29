package cu.uci.chatty;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import cu.uci.campusuci.AboutActivity;
import cu.uci.campusuci.CampusNotification;
import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.LoginUser;
import cu.uci.campusuci.MainActivity;
import cu.uci.campusuci.NavigationDrawer;
import cu.uci.campusuci.R;
import cu.uci.cuota.CuotaActivity;
import cu.uci.directorio.DirectorioActivity;
import cu.uci.horario.HorarioActivity;
import cu.uci.mapa.MapaActivity;
import cu.uci.menu.MenuActivity;
import cu.uci.rss.RssActivity;
import cu.uci.utils.FloatingButtonLib.ActionButton;
import cu.uci.utils.MaterialDialog.base.DialogAction;
import cu.uci.utils.MaterialDialog.base.GravityEnum;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;

/**
 * Created by Yannier on 5/28/2015.
 */
public class ChatActivity extends ActionBarActivity implements FragmentContacts.ConnectListener,
        FragmentContacts.StateListener, FragmentContacts.AvatarListener,
        FragmentContacts.StatusMessageListener, FragmentContacts.OpenChatListener,
        FragmentContacts.DirectorioSearchListener, FragmentMessages.SendMessageListener,
        RosterListener{

    public static Activity ctx;

    //SoundPool sp;
    //Uri _u_;
    int notification;
    //MediaPlayer mp;
    Buddie b;
    ChatManager chatManager;
    ArrayList<MyChat> chats;
    Chat chat_active;
    MyChat myChat;
    private XMPPConnection myconnection;
    private VCard myVcard;
    Roster roster;
    PacketFilter filter;
    ArrayList<Buddie> buddies;
    ChatContactAdapter chatContactAdapter;
    ChatListAdapter chatListAdapter;
    ChatListAdapterOpen chatListAdapterOpen;
    ArrayList<Buddie> buddies_open;
    ArrayList<ChatMessage> chatMessages = new ArrayList<>();
    private Handler mHandler = new Handler();
    byte[] my_avatar_byte_array;
    Bitmap mybitmap;
    String s_m;
    Boolean thread_running = false;
    public static LoginUser me;
    public static Bundle bundle;
    chatDB chatDB;

    private static final int CAMERA_AVATAR = 012;
    private static final int PHONE_AVATAR = 013;

    private String HOST = "jabber.uci.cu";
    private int PORT = 5222;
    private String SERVICE = "jabber.uci.cu";
    private String USERNAME = null;
    private String PASSWORD = null;
    private String REAL_USERNAME = null;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SensorManager sm;
    Sensor sensor;
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompatBuilder;

    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    public static NavigationDrawer drawer;
    Boolean drawerIn = false;
    public static Boolean isInFront;

    FragmentManager fragmentManager;
    FragmentContacts fragment_contacts;
    FragmentMessages fragment_messages;
    FragmentTransaction fragmentTransaction;

    Toolbar mToolbar;
    ListView open_chats;
    TextView tv_connected_user_avatar, tv_init_text;
    ImageView iv_connected_user_avatar;
    RelativeLayout rl_myVcard;
    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_light, roboto_condense_bold, roboto_condensed_light_italic;

    _connect connect;

    int which = 5;
    int[] imgs = new int[]{
            R.drawable.ic_chatty,
            R.drawable.ic_available,
            R.drawable.ic_away,
            R.drawable.ic_xa,
            R.drawable.ic_dnd,
            R.drawable.ic_offline
    };

    int[] img_avatars = new int[]{
            R.drawable.ic_folder,
            R.drawable.ic_camera
    };

    int[] img_avatars_no_camera = new int[]{
            R.drawable.ic_folder,
    };

    int[] img_actions = new int[]{
            R.drawable.ic_clear
    };

    String[] l_avatar;
    String[] l_avatar_no_camera;
    String[] l_state;
    String[] l_actions;
    Boolean has_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ctx = this;

        initToolBar();
        initFragments();
        initOtherViews();
        initBundle();

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
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_CHAT);
        super.onStart();
    }

    @Override
    protected void onResume() {
        isInFront = true;
        drawerLayout.invalidate();
        super.onResume();
    }

    @Override
    protected void onPause() {
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

        String mensaje_de_estado = fragment_contacts.et_status_mes.getText().toString();

        if (sharedPreferences.contains(CampusPreference.STATUS_MESSAGE_)) {
            s_m = sharedPreferences.getString(CampusPreference
                    .STATUS_MESSAGE_,"");
        } else {
            editor.putString(CampusPreference.STATUS_MESSAGE_,mensaje_de_estado);
            editor.commit();
            s_m = sharedPreferences.getString(CampusPreference
                    .STATUS_MESSAGE_, "");
        }

        Log.d("@@@@@@@@@@@@@@@@@@#################", fragment_contacts.et_status_mes.getText()
                .toString());
        Log.d("@@@@@@@@@@@@@@@@@@#################", s_m);

        notificationManager.cancel(CampusNotification.NOTIFICATION_ID_CHAT);

        if(roster != null){
            roster.removeRosterListener(this);
        }

        try {
            if (myconnection != null) {
                myconnection.disconnect();
            }
        } catch (Exception e) {

        }

        try {
            if (connect != null) {
                connect.cancel(true);
            }
        } catch (Exception e) {

        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(drawerIn){
            drawerLayout.closeDrawers();
        }else if(fragment_contacts.isHidden()){
                ShowAndHideFragments(fragment_contacts,fragment_messages);
        }else{
                Intent i1 = new Intent(ChatActivity.this,
                        MainActivity.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);
        }
    }

    private void initFragments(){

        fragmentManager = getSupportFragmentManager();

        fragment_contacts = (FragmentContacts) fragmentManager.findFragmentById(R.id.fragment_contacts);
        fragment_messages = (FragmentMessages) fragmentManager.findFragmentById(R.id.fragment_messages);

        ShowAndHideFragments(fragment_contacts, fragment_messages);

        fragment_contacts.setConnectListener(this);
        fragment_contacts.setStateListener(this);
        fragment_contacts.setAvatarListener(this);
        fragment_contacts.setStatusMessageListener(this);
        fragment_contacts.setOpenChatListener(this);
        fragment_contacts.setDirectorioSearchListener(this);

        fragment_messages.setOnSendMessageListener(this);

    }

    private void initOtherViews(){

        am = getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_condense_light = Typeface.createFromAsset(am, "roboto_condense_light.ttf");
        roboto_condense_bold = Typeface.createFromAsset(am, "roboto_condense_bold.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");
        roboto_condensed_light_italic = Typeface.createFromAsset(am,"roboto_condensed_light_italic.ttf");

        iv_connected_user_avatar = (ImageView) findViewById(R.id.iv_connected_user_avatar);

        tv_init_text = (TextView) findViewById(R.id.tv_init_text);
        tv_init_text.setTypeface(roboto_light_italic);

        tv_connected_user_avatar = (TextView) findViewById(R.id.tv_connected_user_avatar);
        tv_connected_user_avatar.setTypeface(roboto_light);

        rl_myVcard = (RelativeLayout) findViewById(R.id.myVcard);
        rl_myVcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.fab_pop);
                rl_myVcard.startAnimation(pop);
                if(!thread_running){
                    if(myconnection != null){

                    }
                }else{
                    Toast toast = Toast.makeText(ChatActivity.this, getResources().getString(R.string.connecting) + "...",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }
        });



    }

    private void initBundle(){

        if (sharedPreferences.contains(CampusPreference.CHAT_HOST)) {
            HOST = sharedPreferences.getString(CampusPreference.CHAT_HOST, "");
        } else {
            editor.putString(CampusPreference.CHAT_HOST, getResources().getString(R.string.jabber));
            editor.commit();
            HOST = sharedPreferences.getString(CampusPreference.CHAT_HOST,
                    getResources().getString(R.string.jabber));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_SERVICE)) {
            SERVICE = sharedPreferences.getString(CampusPreference.CHAT_SERVICE, "");
        } else {
            editor.putString(CampusPreference.CHAT_SERVICE, getResources().getString(R.string
                    .jabber));
            editor.commit();
            SERVICE = sharedPreferences.getString(CampusPreference.CHAT_SERVICE,
                    getResources().getString(R.string.jabber));
        }

        if (sharedPreferences.contains(CampusPreference.CHAT_PORT)) {
            PORT = Integer.parseInt(sharedPreferences.getString(CampusPreference.CHAT_PORT, ""));
        } else {
            editor.putString(CampusPreference.CHAT_PORT, getResources().getString(R.string
                    .jabber_port));
            editor.commit();
            PORT = Integer.parseInt(sharedPreferences.getString(CampusPreference.CHAT_PORT,
                    getResources().getString(R.string.jabber_port)));
        }

        Log.d("#######################%%%%%%%%%%%%", HOST);
        Log.d("#######################%%%%%%%%%%%%", SERVICE);
        Log.d("#######################%%%%%%%%%%%%", PORT + "");

        me = new LoginUser();
        bundle = getIntent().getExtras();
        me.setUser(bundle.getString("NOMBRE"),bundle.getString("APELLIDOS"),bundle.getString("FOTO"),
                bundle.getString("EDIFICIO"),bundle.getString("AREA"),bundle.getString("DNI"),bundle.getString("APTO"),
                bundle.getString("EXP"),bundle.getString("CRED"),bundle.getString("USER"),bundle.getString("CORREO"),
                bundle.getString("TEL"),bundle.getString("CAT"),bundle.getString("PROV"),bundle.getString("MUN"),
                bundle.getString("SEXO"), bundle.getString("PASS"));
        drawerLayout.invalidate();

        if(me.usuario != null){
            USERNAME = me.usuario;
            PASSWORD = me.password;
            initConnection();
        }
    }

    private void initToolBar(){

        //_u_ = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //mp = MediaPlayer.create(this, uri);
        //sp = new SoundPool(2, AudioManager.STREAM_NOTIFICATION,0);
        //notification = sp.load(_u_.getPath(),1);
        connect = new _connect();
        chatDB = new chatDB(this);
        sharedPreferences = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE);
        editor = getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE, MODE_PRIVATE).edit();
        notificationCompatBuilder = new NotificationCompat.Builder(ChatActivity.this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        filter = new MessageTypeFilter(org.jivesoftware.smack.packet.Message.Type.chat);
        PackageManager packageManager = getPackageManager();
        has_camera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        open_chats = (ListView) findViewById(R.id.open_chats);
        View v = getLayoutInflater().inflate(R.layout.chats_header,(ViewGroup) findViewById(R.id
                .lv_header));
        open_chats.addHeaderView(v);
        buddies_open = new ArrayList<>();
        chatListAdapterOpen = new ChatListAdapterOpen(ChatActivity.this,
                R.layout.chat_user_open, ChatActivity.this, buddies_open, roboto_condense_bold,
                roboto_condense_light, R.layout.chat_user_open);
        open_chats.setAdapter(chatListAdapterOpen);
        open_chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("*****************************", position + "");
                if(position == 0){
                    ChatActivity.this.onBackPressed();
                }else{
                    buddies_open.get(position - 1).con = false;
                    //chatListAdapterOpen.getItem(position - 1).con = false;
                    chatListAdapterOpen.notifyDataSetChanged();
                    if(chat_active.getParticipant().equals(buddies_open.get(position - 1).user) &&
                            !fragment_messages.isHidden()){
                        ChatActivity.this.onBackPressed();
                    }else if(chat_active.getParticipant().equals(buddies_open.get(position -1)
                            .user) &&
                            fragment_messages.isHidden()){
                        ShowAndHideFragments(fragment_messages, fragment_contacts);
                        ChatActivity.this.onBackPressed();
                    }else{
                        Chat aux = null;
                        int pos = -1;

                        for(int i = 0; i < chats.size(); i++){
                            if(chats.get(i).user.equals(buddies_open.get(position - 1).user)){
                                aux = chats.get(i).chat;
                                pos = i;
                            }
                        }
                        chat_active = aux;
                        //chatMessages = chats.get(pos).messages;
                        chatListAdapter = new ChatListAdapter(chats.get(pos).messages,ChatActivity.this);
                        //fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                        //ShowAndHideFragments(fragment_messages, fragment_contacts);
                        mHandler.post(new Runnable() {
                            public void run() {
                                fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                                chatListAdapter.notifyDataSetChanged();
                                ShowAndHideFragments(fragment_messages,fragment_contacts);
                                fragment_messages.lv_chat_view.setSelection(chatListAdapter.getAll()
                                        .size()-1);
                            }
                        });
                        ChatActivity.this.onBackPressed();
                    }
                }
            }
        });

        l_avatar_no_camera = new String[]{getResources().getString(R.string.sd)};
        l_avatar = getResources().getStringArray(R.array.avatar);
        l_state = getResources().getStringArray(R.array.states);
        l_actions = getResources().getStringArray(R.array.actions);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.inflateMenu(R.menu.menu_chat);
        //setSupportActionBar(mToolbar);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();
                switch (id) {

                    case R.id.action_log_out:
                        fragment_contacts.is_connected = false;
                        fragment_contacts.fab.setImageResource(R.drawable.ic_fa_sign_in);
                        try {
                            if (myconnection != null) {
                                myconnection.disconnect();
                                myconnection = null;
                                chat_active = null;
                                chatMessages = null;
                                sendOfflieToast();
                                ShowAndHideFragments(fragment_contacts,fragment_messages);
                                chats = null;
                                chat_active = null;
                                chatMessages = null;
                                ShowAndHideInitText(false);
                            }
                        } catch (Exception e) {

                        }
                        break;

                }

                return true;
            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConnectListener() {

        if(thread_running){
            Toast toast = Toast.makeText(ChatActivity.this, getResources().getString(R.string.connecting) + "...",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }else{
            if(myconnection == null){
                fragment_contacts.fab.setClickable(false);
                showChatDialog();
            }else{
                //Cuando esta conectado "Añadir Grupo"
            }
        }
    }

    private void ShowAndHideInitText(Boolean aBoolean){

        if(aBoolean && tv_init_text.getVisibility() == View.VISIBLE){
            Animation animation_tv = AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.tv_fade_out);
            Animation animation_lv = AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.tv_alpha);
            tv_init_text.startAnimation(animation_tv);
            tv_init_text.setVisibility(View.INVISIBLE);
            //tv_init_text.setText(ChatActivity.this.getResources().getString(R.string.not_connected_yet));
            fragment_contacts.lv_contacts.setVisibility(View.VISIBLE);
            fragment_contacts.lv_contacts.startAnimation(animation_lv);
            //lv_chat.setEnabled(true);
        }else if(aBoolean && tv_init_text.getVisibility() == View.INVISIBLE){
            Animation animation_lv = AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.tv_alpha);
            tv_init_text.setText(ChatActivity.this.getResources().getString(R.string.not_connected_yet));
            fragment_contacts.lv_contacts.setVisibility(View.VISIBLE);
            fragment_contacts.lv_contacts.startAnimation(animation_lv);
            //lv_chat.setEnabled(true);
        }else if(!aBoolean && tv_init_text.getVisibility() == View.VISIBLE){
            Animation animation_tv = AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.tv_fade_out);
            tv_init_text.setText(ChatActivity.this.getResources().getString(R.string.not_connected_yet));
            fragment_contacts.lv_contacts.startAnimation(animation_tv);
            fragment_contacts.lv_contacts.setVisibility(View.INVISIBLE);
            //lv_chat.setEnabled(false);
        }else if(!aBoolean && tv_init_text.getVisibility() == View.INVISIBLE){
            Animation animation_tv = AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.tv_fade_out);
            fragment_contacts.lv_contacts.startAnimation(animation_tv);
            fragment_contacts.lv_contacts.setVisibility(View.INVISIBLE);
            //lv_chat.setEnabled(false);
            Animation animation_lv = AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.fab_fade_in);
            tv_init_text.setText(ChatActivity.this.getResources().getString(R.string.not_connected_yet));
            tv_init_text.setVisibility(View.VISIBLE);
            tv_init_text.startAnimation(animation_lv);
        }

    }

    private void sendSuccessToast(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(USERNAME + "\n" + getResources().getString(R
                .string.co) + " " + HOST);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendErrorToast(String texto){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(getResources().getString(R.string
                .chat_uci_title) + "\n" + texto);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    private void sendOfflieToast(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(
                R.layout.directorio_toast
                ,(ViewGroup) findViewById(R.id.dir_toast_layout));

        TextView t = (TextView) layout.findViewById(R.id.tv_dir_toast);
        t.setTypeface(roboto_light);
        t.setText(USERNAME + "\n" + getResources().getString(R
                .string.dis) + " " + HOST);

        ImageView i = (ImageView) layout.findViewById(R.id.iv_dir_toast);
        i.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setView(layout);
        toast.show();

        Animation pop = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.fab_pop);
        tv_connected_user_avatar.setText(getResources().getString(R.string.state_offline));
        iv_connected_user_avatar.setImageDrawable(getResources().getDrawable(R.drawable.empty_user));
        rl_myVcard.startAnimation(pop);
    }

    @Override
    public void entriesAdded(Collection<String> strings) {

    }

    @Override
    public void entriesUpdated(Collection<String> strings) {

    }

    @Override
    public void entriesDeleted(Collection<String> strings) {

    }

    @Override
    public void presenceChanged(Presence presence) {

        String from = presence.getFrom();
        int slash_pos = from.indexOf("/");
        from = from.substring(0,slash_pos);
        Presence.Type type = presence.getType();
        Presence.Mode mode = presence.getMode();
        String status = presence.getStatus();

        Buddie who = null;
        int pos = -1;
        for(int i = 0; i < chatContactAdapter.getAll().size(); i++){
            if(chatContactAdapter.getItem(i).user.equals(from)){
                who = chatContactAdapter.getItem(i);
                pos = i;
            }
        }

        if(type == Presence.Type.unavailable && who != null){
            //chatContactAdapter.set(pos);
            //buddies.get(pos).remove = true;
            //chatContactAdapter.notifyDataSetChanged();
            chatContactAdapter.remove(pos);
            chatContactAdapter.notifyDataSetChanged();
        }else if(type == Presence.Type.available){
            if(who == null){
                Log.d("********************","UNO NUEVO" + ":" + from);
                RosterEntry rosterEntry = roster.getEntry(from);
                Presence presence1 = roster.getPresence(from);
                VCard vCard1 = new VCard();
                SmackConfiguration.setPacketReplyTimeout(300000);
                ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",new VCardProvider());
                try {
                    vCard1.load(myconnection,from);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                Buddie b = new Buddie();
                b.name = rosterEntry.getName();
                b.user = from;
                b.mode = presence1.getMode();
                b.status_mess = presence1.getStatus();
                b.status = presence1.getType().toString();
                b.state = presence1 + "";
                b.img = vCard1.getAvatar();

                //buddies.add(b);
                chatContactAdapter.add(b);
                chatContactAdapter.notifyDataSetChanged();
            }else{
                //buddies.get(pos).anim = true;
                //buddies.get(pos).status_mess = status;
                //buddies.get(pos).mode = mode;
                chatContactAdapter.getItem(pos).anim = true;
                chatContactAdapter.getItem(pos).status_mess = status;
                chatContactAdapter.getItem(pos).mode = mode;
                chatContactAdapter.notifyDataSetChanged();
            }
        }

        Log.d("@@@@@@@@@@@@@@@@@@@@@", from + ":" + status);



    }

    @Override
    public void onStateListener() {
        showStateAdapter();
    }

    @Override
    public void onAvatarListener() {
        if(has_camera){
            showAvatarAdapter(img_avatars,l_avatar);
        }else{
            showAvatarAdapter(img_avatars_no_camera,l_avatar_no_camera);
        }
    }

    @Override
    public void onStatusMessageListener() {
        Log.d("!!!!!!!!!!!!!!!!##################", fragment_contacts.et_status_mes.getText()
                .toString());
        if(!thread_running){

            if(myconnection != null){
                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus(fragment_contacts.et_status_mes.getText().toString());
                myconnection.sendPacket(presence);
            }

        }
    }

    private void ShowAndHideFragments(Fragment toShow, Fragment toHide){

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fab_fade_in, R.anim.fab_fade_out)
                .show(toShow)
                .hide(toHide)
                .commit();

    }

    @Override
    public void onOpenChatListener() {
        ShowAndHideFragments(fragment_messages, fragment_contacts);
        showMyMessages();
    }

    private void showMyMessages() {

        int position = fragment_contacts.selected_user;

        int pos_2 = -1;

        Chat aux = null;
        int pos = -1;

        for(int i = 0; i < buddies_open.size(); i++){
            if(buddies_open.get(i).user.equals(chatContactAdapter.getItem(position)
                    .user)){
                pos_2 = i;
            }
        }

        for(int i = 0; i < chats.size(); i++){
            if(chats.get(i).user.equals(chatContactAdapter.getItem(position).user)){
                aux = chats.get(i).chat;
                pos = i;
            }
        }

        if(chat_active == null){
            Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@","CHAT ACTIVE NULL");

                Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@", chatContactAdapter.getItem(position).user);
                Chat chat = chatManager.createChat(chatContactAdapter.getItem(position).user,new MessageListener() {
                          @Override
                         public void processMessage(Chat chat,Message message) {
                         Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",message.getBody());
                         Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());
                    }
                });

                Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());

                myChat = new MyChat(chatContactAdapter.getItem(position).user, chat);
                chats.add(myChat);
                //chatMessages = chats.get(chats.size()-1).messages;
                chatListAdapter = new ChatListAdapter(chats.get(chats.size()-1).messages,ChatActivity.this);
                fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                chat_active = chat;
                //ShowAndHideFragments(fragment_messages,fragment_contacts);
                mHandler.post(new Runnable() {
                    public void run() {
                        fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                        chatListAdapter.notifyDataSetChanged();
                        ShowAndHideFragments(fragment_messages,fragment_contacts);
                        fragment_messages.lv_chat_view.setSelection(chatListAdapter.getAll()
                                .size()-1);
                    }
                });

            buddies_open.add(buddies.get(position));
            //Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chatListAdapterOpen.getAll().size() + "");
            //chatListAdapterOpen.add(chatContactAdapter.getItem(position));
            Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chatListAdapterOpen.getAll().size() + "");
            chatListAdapterOpen.notifyDataSetChanged();

        }else{
            Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@","CHAT ACTIVE NOT NULL");
            if(chat_active.getParticipant().equals(chatContactAdapter.getItem(position).user)){
                ShowAndHideFragments(fragment_messages,fragment_contacts);
                buddies_open.get(pos_2).con = false;
                chatListAdapterOpen.notifyDataSetChanged();
            }else{

                if(aux == null){
                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chatContactAdapter.getItem(position).user);
                    Chat chat = chatManager.createChat(chatContactAdapter.getItem(position).user,
                            new MessageListener() {
                                @Override
                                public void processMessage(Chat chat, Message message) {
                                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",message.getBody());
                                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());
                                }
                            });
                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());
                    myChat = new MyChat(chatContactAdapter.getItem(position).user, chat);
                    chats.add(myChat);
                    //chatMessages = chats.get(chats.size()-1).messages;
                    chatListAdapter = new ChatListAdapter(chats.get(chats.size()-1).messages,ChatActivity.this);
                    //fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                    chat_active = chat;
                    //ShowAndHideFragments(fragment_messages,fragment_contacts);
                    mHandler.post(new Runnable() {
                        public void run() {
                            fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                            chatListAdapter.notifyDataSetChanged();
                            ShowAndHideFragments(fragment_messages, fragment_contacts);
                            fragment_messages.lv_chat_view.setSelection(chatListAdapter.getAll()
                                    .size() - 1);
                        }
                    });
                    buddies_open.add(buddies.get(position));
                    //chatListAdapterOpen.add(chatContactAdapter.getItem(position));
                    chatListAdapterOpen.notifyDataSetChanged();
                }else{
                    chat_active = aux;
                    //chatMessages = chats.get(pos).messages;
                    chatListAdapter = new ChatListAdapter(chats.get(pos).messages,ChatActivity.this);
                    //fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                    //ShowAndHideFragments(fragment_messages, fragment_contacts);
                    mHandler.post(new Runnable() {
                        public void run() {
                            fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                            chatListAdapter.notifyDataSetChanged();
                            ShowAndHideFragments(fragment_messages,fragment_contacts);
                            fragment_messages.lv_chat_view.setSelection(chatListAdapter.getAll()
                                    .size()-1);
                        }
                    });
                    buddies_open.get(pos_2).con = false;
                    //chatListAdapterOpen.getItem(pos_2 - 1).con = false;
                    chatListAdapterOpen.notifyDataSetChanged();
                }
            }
        }

        fragment_messages.lv_chat_view.setSelection(chatListAdapter.getAll().size()-1);
    }

    @Override
    public void onDirectorioSearchListener() {
        showContactsActionsDialog();
    }

    private void showContactsActionsDialog() {
        String user = buddies.get(fragment_contacts.long_selected_user).user;
        user = user.replace("@jabber.uci.cu","");
        user = user.replace(HOST,"");

        Log.d("#################", user);

        Log.d("#################", user);
        Log.d("#################", fragment_contacts.long_selected_user + "");
        Log.d("#################", fragment_contacts.long_selected_user + "");

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.chat_uci_title)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .icon(getResources().getDrawable(R.drawable.ic_chat_nav))
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        fragment_contacts.iv_state.setClickable(true);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fragment_contacts.iv_state.setClickable(true);
                    }
                })
                .adapter(new MyAvatarAdapter(this, R.array.avatar, ChatActivity.this,l_actions,
                                img_actions, roboto_light, R.layout.new_state_layout),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int i,
                                                    CharSequence text) {
                                    switch (i) {
                                        case 0:


                                            break;
                                    }
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    @Override
    public void onSendMessage() {

        if(chat_active != null && fragment_messages.et_messages.getText().length() > 0){

            try {
                String texto = fragment_messages.et_messages.getText().toString();
                chat_active.sendMessage(texto);

                ChatMessage t = new ChatMessage();
                t.userType = UserType.SELF;
                t.user = "";
                t.messageText = fragment_messages.et_messages.getText().toString();
                t.messageTime = new Date().getTime();

                //chatMessages.add(t);
                chatListAdapter.add(t);
                fragment_messages.et_messages.setText("");
                chatListAdapter.notifyDataSetChanged();
            } catch (XMPPException e) {
                e.printStackTrace();
            }

        }

    }

    private class _connect extends AsyncTask<String, Integer, Boolean> {

        String USER_NAME;

        XMPPConnection connection;
        String error = ChatActivity.this.getResources().getString(R.string.error_con);
        ArrayList<Buddie> aux_buddies;
        Buddie buddie;
        Bitmap bm;
        VCard vCard_user;
        VCard vCard;
        Roster roster_aux;
        int value;

        MaterialDialog progress;

        @Override
        protected void onPreExecute() {

            thread_running = true;

            tv_init_text.setText(getResources().getString(R.string.connecting) + "...");

            vCard_user = new VCard();

            chatContactAdapter = null;

            if (sharedPreferences.contains(CampusPreference.CHAT_HOST)) {
                HOST = sharedPreferences.getString(CampusPreference.CHAT_HOST, "");
            } else {
                editor.putString(CampusPreference.CHAT_HOST, getResources().getString(R.string.jabber));
                editor.commit();
                HOST = sharedPreferences.getString(CampusPreference.CHAT_HOST,
                        getResources().getString(R.string.jabber));
            }

            if (sharedPreferences.contains(CampusPreference.CHAT_SERVICE)) {
                SERVICE = sharedPreferences.getString(CampusPreference.CHAT_SERVICE, "");
            } else {
                editor.putString(CampusPreference.CHAT_SERVICE, getResources().getString(R.string
                        .jabber));
                editor.commit();
                SERVICE = sharedPreferences.getString(CampusPreference.CHAT_SERVICE,
                        getResources().getString(R.string.jabber));
            }

            if (sharedPreferences.contains(CampusPreference.CHAT_PORT)) {
                PORT = Integer.parseInt(sharedPreferences.getString(CampusPreference.CHAT_PORT, ""));
            } else {
                editor.putString(CampusPreference.CHAT_PORT, getResources().getString(R.string
                        .jabber_port));
                editor.commit();
                PORT = Integer.parseInt(sharedPreferences.getString(CampusPreference.CHAT_PORT,
                        getResources().getString(R.string.jabber_port)));
            }

            Log.d("#######################%%%%%%%%%%%%", HOST);
            Log.d("#######################%%%%%%%%%%%%", SERVICE);
            Log.d("#######################%%%%%%%%%%%%", PORT + "");

            //value = which;
            //tv_init.setText(ChattyActivity.this.getResources().getString(R.string.connecting) +
            //"" +"...");
            progress = new MaterialDialog.Builder(ChatActivity.this)
                    .title(R.string.connecting)
                    .content(R.string.please_wait)
                    .theme(Theme.LIGHT)
                    .icon(ChatActivity.this.getResources().getDrawable(R.drawable.ic_chat_nav))
                    .cancelable(true)
                    .contentGravity(GravityEnum.CENTER)
                    .progress(true, 0)
                    .show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
            connection = new XMPPConnection(connConfig);
            aux_buddies = new ArrayList<>();

            try {
                connection.connect();
                Log.i("CAMPUSUCIDEBUG","Connected to " + connection.getHost());
            } catch (XMPPException ex) {
                Log.e("CAMPUSUCIDEBUG", "Failed to connect to "+ connection.getHost());
                Log.e("CAMPUSUCIDEBUG", ex.toString());
                setConnection(null);
            }
            try {
                // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                connection.login(USERNAME, PASSWORD);
                //vCard_user = new VCard();
                //vCard_user.load(connection, USERNAME + "@jabber.uci.cu");
                //byte[] byte_aux = vCard.getAvatar();
                //bm = BitmapFactory.decodeByteArray(byte_aux, 0, byte_aux.length);
                USER_NAME = connection.getUser().replace("/Smack","");
                Log.i("CAMPUSUCIDEBUG","Logged in as " + USER_NAME);

                // Set the status to available
                Presence presence = new Presence(Presence.Type.available);
                connection.sendPacket(presence);
                setConnection(connection);

                roster_aux = connection.getRoster();
                Collection<RosterEntry> entries = roster_aux.getEntries();

                int k = 0;

                for (RosterEntry entry : entries) {
                    buddie = new Buddie();

                    vCard = new VCard();
                    SmackConfiguration.setPacketReplyTimeout(300000);
                    ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",new VCardProvider());

                    String user = entry.getUser();
                    buddie.user = user;
                    String name = entry.getName();
                    buddie.name = name;

                    Log.d("CAMPUSUCIDEBUG", "--------------------------------------");
                    Log.d("CAMPUSUCIDEBUG", "RosterEntry " + entry);
                    Log.d("CAMPUSUCIDEBUG","User: " + user);
                    Log.d("CAMPUSUCIDEBUG","Name: " + name);
                    Log.d("CAMPUSUCIDEBUG","Status: " + entry.getStatus());
                    Log.d("CAMPUSUCIDEBUG","Type: " + entry.getType());

                    Presence entryPresence = roster_aux.getPresence(entry.getUser());
                    String status = entryPresence.getStatus();

                    Log.d("CAMPUSUCIDEBUG", "Presence Status: "+ status);

                    if(status == null){
                        buddie.status_mess = "";
                    }else{
                        buddie.status_mess = status;
                    }
                    Presence.Type type = entryPresence.getType();
                    Log.d("CAMPUSUCIDEBUG", "Presence Type: "+ type);

                    if (type == Presence.Type.available) {

                        buddie.status = entryPresence.getType().toString();
                        buddie.mode = entryPresence.getMode();

                        Log.d("CAMPUSUCIDEBUG", "Presence AVIALABLE");
                        Log.d("CAMPUSUCIDEBUG", "Presence : "+ entryPresence);

                        if(k == 0){
                            vCard.load(connection,USER_NAME);
                            bm = BitmapFactory.decodeByteArray(vCard.getAvatar(), 0,
                                    vCard.getAvatar().length);
                            k++;
                        }

                        buddie.state = entryPresence + "";
                        vCard.load(connection, user);
                        buddie.img = vCard.getAvatar();

                        Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1",buddie.img + "");

                        aux_buddies.add(buddie);

                    }
                }

            } catch (XMPPException ex) {
                Log.e("CAMPUSUCIDEBUG", "Failed to log in as "+ USERNAME);
                Log.e("CAMPUSUCIDEBUG", ex.toString());
                //(null);
                return false;
            }catch (Exception ex) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            Animation pop = AnimationUtils.loadAnimation(ChatActivity.this,R.anim.fab_pop);

            myconnection = connection;
            thread_running = false;

            boolean activate;
            if(sharedPreferences.contains(CampusPreference.ACTIVATE_NOTIFICATIONS)){
                activate = sharedPreferences.getBoolean(CampusPreference.ACTIVATE_NOTIFICATIONS,
                        false);
            }else{
                activate = true;
            }

            if(aBoolean) {
                buddies = aux_buddies;
                //Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", buddies.size() + "");
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "conecto perfecto");

                chatContactAdapter = new ChatContactAdapter(ChatActivity.this,
                        R.layout.chat_user, ChatActivity.this, buddies, roboto_condense_bold,
                        roboto_condense_light, R.layout.chat_user);
                fragment_contacts.lv_contacts.setAdapter(chatContactAdapter);
                chatContactAdapter.notifyDataSetChanged();

                chats = new ArrayList<>();

                chatManager = myconnection.getChatManager();

                REAL_USERNAME = USER_NAME;

                if(my_avatar_byte_array != null){
                    iv_connected_user_avatar.setImageBitmap(BitmapFactory.decodeByteArray
                            (my_avatar_byte_array, 0, my_avatar_byte_array.length));
                    try {
                        myVcard = new VCard();
                        SmackConfiguration.setPacketReplyTimeout(300000);
                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                        myVcard.load(myconnection, REAL_USERNAME);
                        Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", myVcard.getFirstName());
                        myVcard.setAvatar(my_avatar_byte_array);
                        myVcard.save(myconnection);
                        my_avatar_byte_array = null;
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }else{
                    iv_connected_user_avatar.setImageBitmap(bm);
                }
                tv_connected_user_avatar.setText(myconnection.getUser().replace("/Smack", ""));
                rl_myVcard.startAnimation(pop);

                if(which == 5){
                    which = 1;
                    fragment_contacts.iv_state.setImageDrawable(getResources().getDrawable
                            (imgs[1]));
                    fragment_contacts.iv_state.startAnimation(pop);
                }
                //setAvatar();

                Presence presence = new Presence(Presence.Type.available);

                switch (which){
                    case 0:
                        presence.setMode(Presence.Mode.chat);
                        presence.setStatus(fragment_contacts.et_status_mes.getText().toString());
                        myconnection.sendPacket(presence);
                        //sendSuccessToast();
                        fragment_contacts.fab.setImageResource(R.drawable.ic_action_new);
                        //ShowAndHideInitText(true);
                        break;

                    case 1:
                        presence.setMode(Presence.Mode.available);
                        presence.setStatus(fragment_contacts.et_status_mes.getText().toString());
                        myconnection.sendPacket(presence);
                        //sendSuccessToast();
                        fragment_contacts.fab.setImageResource(R.drawable.ic_action_new);
                        //ShowAndHideInitText(true);
                        break;

                    case 2:
                        presence.setMode(Presence.Mode.away);
                        presence.setStatus(fragment_contacts.et_status_mes.getText().toString());
                        myconnection.sendPacket(presence);
                        //sendSuccessToast();
                        fragment_contacts.fab.setImageResource(R.drawable.ic_action_new);
                        //ShowAndHideInitText(true);
                        break;

                    case 3:
                        presence.setMode(Presence.Mode.xa);
                        presence.setStatus(fragment_contacts.et_status_mes.getText().toString());
                        myconnection.sendPacket(presence);
                        //sendSuccessToast();
                        fragment_contacts.fab.setImageResource(R.drawable.ic_action_new);
                        //ShowAndHideInitText(true);
                        break;

                    case 4:
                        presence.setMode(Presence.Mode.dnd);
                        presence.setStatus(fragment_contacts.et_status_mes.getText().toString());
                        myconnection.sendPacket(presence);
                        //sendSuccessToast();
                        fragment_contacts.fab.setImageResource(R.drawable.ic_action_new);

                        break;

                    case 5:
                        try {
                            if (myconnection != null) {
                                myconnection.disconnect();
                                myconnection = null;
                                chat_active = null;
                                chatMessages = null;
                                sendOfflieToast();
                                ShowAndHideInitText(false);
                            }
                        } catch (Exception e) {

                        }
                        break;


                }

                roster = roster_aux;
                roster.addRosterListener(ChatActivity.this);

                if(fragment_contacts.save_to_auth){

                    chatDB.openChatDatabase();
                    chatDB.saveChat(fragment_contacts.user_to_save, fragment_contacts.pass_to_save);
                    chatDB.closeChatDatabase();

                    editor.putBoolean(CampusPreference.REMEMBER_ME_CHAT, true);
                    editor.commit();

                }else{
                    chatDB.openChatDatabase();
                    int temp = chatDB.deleteChat(fragment_contacts.user_to_save);
                    if(temp == 0){
                        String[] test = chatDB.loadChat();
                        if(test[0].length() == 0){
                            editor.putBoolean(CampusPreference.REMEMBER_ME_CHAT, false);
                            editor.commit();
                        }else{
                            editor.putBoolean(CampusPreference.REMEMBER_ME_CHAT, true);
                            editor.commit();
                        }
                    }else{
                        editor.putBoolean(CampusPreference.REMEMBER_ME_CHAT, false);
                        editor.commit();
                    }
                    chatDB.closeChatDatabase();

                }


                fragment_contacts.lv_contacts.invalidate();

                if(activate){
                    sendConnectNotification(aBoolean);
                }else{
                        sendSuccessToast();
                }

                progress.dismiss();
                ShowAndHideInitText(true);
            }else{
                progress.dismiss();
                which = 5;
                fragment_contacts.fab.setImageResource(R.drawable.ic_fa_sign_in);
                fragment_contacts.iv_state.setImageDrawable(getResources().getDrawable(imgs[5]));
                fragment_contacts.iv_state.startAnimation(pop);
                fragment_contacts.fab.startAnimation(pop);
                ShowAndHideInitText(false);
                if(myconnection != null){
                    try {
                        myconnection.disconnect();
                        myconnection = null;
                    } catch (Exception e) {

                    }
                }

                if(activate){
                    sendConnectNotification(aBoolean);
                }else{
                    sendErrorToast(ChatActivity.this.getResources().getString(R.string.error_con));
                }
                //sendErrorToast(ChatActivity.this.getResources().getString(R.string.error_con));
            }
            super.onPostExecute(aBoolean);
        }
    }

    private void showStateAdapter() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.state)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .icon(getResources().getDrawable(R.drawable.ic_chat_nav))
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        fragment_contacts.iv_state.setClickable(true);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fragment_contacts.iv_state.setClickable(true);
                    }
                })
                .adapter(new MyAvatarAdapter(this, R.array.avatar, ChatActivity.this,l_state,
                                imgs, roboto_light, R.layout.new_state_layout),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int i,
                                                    CharSequence text) {

                                which = i;
                                fragment_contacts.iv_state.setImageDrawable(getResources().getDrawable(imgs[i]));
                                Animation pop = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.fab_pop);
                                fragment_contacts.iv_state.startAnimation(pop);

                                if(!thread_running){

                                    Presence presence = new Presence(Presence.Type.available);
                                    switch (i) {
                                        case 0:

                                            if (myconnection != null) {
                                                presence.setMode(Presence.Mode.chat);
                                            } else {
                                                showChatDialog();
                                            }
                                            break;

                                        case 1:
                                            if (myconnection != null) {
                                                presence.setMode(Presence.Mode.available);
                                            } else {
                                                showChatDialog();
                                            }
                                            break;

                                        case 2:
                                            if (myconnection != null) {
                                                presence.setMode(Presence.Mode.away);
                                            } else {
                                                showChatDialog();
                                            }
                                            break;

                                        case 3:
                                            if (myconnection != null) {
                                                presence.setMode(Presence.Mode.xa);
                                            } else {
                                                showChatDialog();
                                            }
                                            break;

                                        case 4:
                                            if (myconnection != null) {
                                                presence.setMode(Presence.Mode.dnd);
                                            } else {
                                                showChatDialog();
                                            }
                                            break;

                                        case 5:
                                            fragment_contacts.is_connected = false;
                                            fragment_contacts.fab.setImageResource(R.drawable.ic_fa_sign_in);
                                            try {
                                                if (myconnection != null) {
                                                    myconnection.disconnect();
                                                    myconnection = null;
                                                    sendOfflieToast();
                                                    ShowAndHideInitText(false);
                                                    chats = null;
                                                    chat_active = null;
                                                    chatMessages = null;
                                                    ShowAndHideFragments(fragment_contacts,fragment_messages);
                                                }
                                            } catch (Exception e) {

                                            }
                                            break;
                                    }

                                    if (myconnection != null) {

                                        myconnection.sendPacket(presence);

                                    }

                                }


                                fragment_contacts.iv_state.setClickable(true);
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private EditText userInput;
    private EditText passwordInput;
    private CheckBox showPass;
    private CheckBox remember_me;
    private View positiveAction;
    private boolean userText = false;
    private boolean passText = false;

    private void showChatDialog() {

        chatDB.openChatDatabase();
        String []chat = chatDB.loadChat();
        chatDB.closeChatDatabase();

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.chat_uci_title)
                .icon(getResources().getDrawable(R.drawable.ic_chat_nav))
                .customView(R.layout.dialog_auth, true)
                .positiveText(R.string.connect)
                .theme(Theme.LIGHT)
                .negativeText(android.R.string.cancel)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        fragment_contacts.fab.setClickable(true);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fragment_contacts.fab.setClickable(true);
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        fragment_contacts.fab.setClickable(true);
                        USERNAME = userInput.getText().toString();
                        PASSWORD = passwordInput.getText().toString();
                        fragment_contacts.save_to_auth = remember_me.isChecked();
                        fragment_contacts.user_to_save = userInput.getText().toString();
                        fragment_contacts.pass_to_save = passwordInput.getText().toString();
                        initConnection();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fragment_contacts.fab.setClickable(true);
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

        if (sharedPreferences.contains(CampusPreference.REMEMBER_ME_CHAT)) {
            remember_me.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .REMEMBER_ME_CHAT,
                    false));
        } else {
            editor.putBoolean(CampusPreference.REMEMBER_ME_CHAT, false);
            editor.commit();
            remember_me.setChecked(sharedPreferences.getBoolean(CampusPreference
                            .REMEMBER_ME_CHAT,
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

        if(chat[0].length()>0){
            userInput.setText(chat[0]);
            passwordInput.setText(chat[1]);
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

    private void showAvatarAdapter(int[] list1, String[] list2) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.avatar)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .icon(getResources().getDrawable(R.drawable.ic_avatar))
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        fragment_contacts.iv_avatar.setClickable(true);
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fragment_contacts.iv_avatar.setClickable(true);
                    }
                })
                .adapter(new MyAvatarAdapter(this, R.array.avatar, ChatActivity.this,
                                list2,
                                list1, roboto_light, R.layout.new_avatar_layout),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int pos,
                                                    CharSequence text) {

                                switch (pos) {

                                    case 0:
                                        Intent intent = new Intent();
                                        intent.setType("image/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        startActivityForResult(intent, PHONE_AVATAR);
                                        break;

                                    case 1:
                                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(i, CAMERA_AVATAR);
                                        break;

                                }
                                fragment_contacts.iv_avatar.setClickable(true);
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    private void initConnection(){
        connect = new _connect();
        connect.execute();
    }

    public void setConnection(XMPPConnection connection) {
        this.myconnection = connection;
        if (myconnection != null) {
            // Add a packet listener to get messages sent to us
            filter = new MessageTypeFilter(org.jivesoftware.smack.packet.Message.Type.chat);
            myconnection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!", message.getFrom() + ":" + message.getBody());

                    String from_ = message.getFrom();
                    int slash_pos = from_.indexOf("/");
                    final String from = from_.substring(0,slash_pos);
                    String body = message.getBody();

                    b = null;
                    int b_pos;

                    int pos = -1;
                    Chat aux = null;

                    if(body != null){

                        //sendNewMessageNotification(from);

                        //if(!isInFront){
                        //    sendNewMessageNotification(from);
                        //}

                        for(Buddie buddie:chatContactAdapter.getAll()){
                            if(buddie.user.equals(from)){
                                b = buddie;
                            }
                        }

                        for(int i = 0; i < chats.size(); i++){
                            if(chats.get(i).user.equals(from)){
                                aux = chats.get(i).chat;
                                pos = i;
                            }
                        }

                        if(chat_active == null){

                                Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",from);
                                Chat chat = chatManager.createChat(from,
                                        new MessageListener() {
                                        @Override
                                        public void processMessage(Chat chat, Message message) {
                                                Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",message.getBody());
                                                Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());
                                            }
                                        });
                                Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());

                                chat_active = chat;
                                myChat = new MyChat(from, chat);
                                chats.add(myChat);

                                ChatMessage t = new ChatMessage();
                                t.userType = UserType.OTHER;
                                t.user = from;
                                t.messageText = message.getBody();
                                t.messageTime = new Date().getTime();

                                chats.get(chats.size()-1).messages.add(t);
                                //chatMessages = chats.get(chats.size()-1).messages;
                                chatListAdapter = new ChatListAdapter(chats.get(chats.size()-1).messages,ChatActivity.this);
                                chat_active = chat;
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                                        chatListAdapter.notifyDataSetChanged();
                                        fragment_messages.lv_chat_view.setSelection
                                                (chatListAdapter.getAll()
                                                .size() - 1);
                                        ShowAndHideFragments(fragment_messages, fragment_contacts);
                                        buddies_open.add(b);
                                        //chatListAdapterOpen.add(b);
                                        chatListAdapterOpen.notifyDataSetChanged();
                                    }
                                });

                            if(!isInFront){
                                sendNewMessageNotification(from);
                            }

                            //sp.play(notification,1,1,0,0,1);
                            //mp.start();
                        }else{

                            Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@","CHAT ACTIVE NOT NULL");
                            if(chat_active.getParticipant().equals(from)){

                                ChatMessage t = new ChatMessage();
                                t.userType = UserType.OTHER;
                                t.user = from;
                                t.messageText = message.getBody();
                                t.messageTime = new Date().getTime();

                                //chats.get(pos).messages.add(t);
                                //chatListAdapter.setAll(chats.get(pos).messages);
                                chatListAdapter.add(t);
                                if(fragment_messages.isHidden()){
                                    buddies_open.get(pos).con = true;
                                    //chatListAdapterOpen.getItem(pos).con = true;
                                }
                                mHandler.post(new Runnable() {
                                    public void run() {
                                        //fragment_messages.lv_chat_view.setAdapter(chatListAdapter);
                                        chatListAdapter.notifyDataSetChanged();
                                        fragment_messages.lv_chat_view.setSelection(chatListAdapter.getAll()
                                                .size()-1);
                                        chatListAdapterOpen.notifyDataSetChanged();
                                    }
                                });
                                if(!isInFront){
                                  sendNewMessageNotification(from);
                                }else{
                                    if(fragment_messages.isHidden()){
                                        sendNewMessageNotification(from);
                                    }
                                }
                                //AVISO

                                //sp.play(notification,1,1,0,0,1);
                                //mp.start();
                            }else{
                                if(aux == null){

                                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",from);
                                    Chat chat = chatManager.createChat(from,
                                            new MessageListener() {
                                                @Override
                                                public void processMessage(Chat chat, Message message) {
                                                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",message.getBody());
                                                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());
                                                }
                                            });
                                    Log.d("@@@@@@@@@@@@@@@@@@@@@@@@@@@",chat.getThreadID());

                                    myChat = new MyChat(from, chat);
                                    chats.add(myChat);

                                    ChatMessage t = new ChatMessage();
                                    t.userType = UserType.OTHER;
                                    t.user = from;
                                    t.messageText = message.getBody();
                                    t.messageTime = new Date().getTime();

                                    chats.get(chats.size()-1).messages.add(t);

                                    sendNewMessageNotification(from);
                                    buddies_open.add(b);
                                    b.con = true;
                                    //chatListAdapterOpen.add(b);
                                    chatListAdapterOpen.notifyDataSetChanged();
                                    //AVISO
                                    //sp.play(notification,1,1,0,0,1);
                                }else{

                                    ChatMessage t = new ChatMessage();
                                    t.userType = UserType.OTHER;
                                    t.user = from;
                                    t.messageText = message.getBody();
                                    t.messageTime = new Date().getTime();

                                    chats.get(pos).messages.add(t);
                                    buddies_open.get(pos).con = true;
                                    //chatListAdapterOpen.getItem(pos).con = true;
                                    chatListAdapterOpen.notifyDataSetChanged();
                                    sendNewMessageNotification(from);
                                    //AVISO
                                    //sp.play(notification,1,1,0,0,1);
                                }
                            }

                        }

                        Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!", from + ":" + body);
                    }
                }
            }, filter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@","EMPEZANDO ACTIVITY FOR RESULT 0");

        if(resultCode == ChatActivity.this.RESULT_OK) {
            Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@","RESULT_OK FULLLLLLLLLLLLLLLLLLLLLL");
            switch (requestCode) {

                case CAMERA_AVATAR:
                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK CAMERA 1");
                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK CAMERA 2");
                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK CAMERA 3");

                    final Bundle avatar = data.getExtras();
                    mybitmap = (Bitmap) avatar.get("data");

                    Thread thread_camera = new Thread(){

                        public void run(){

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            mybitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            my_avatar_byte_array = stream.toByteArray();

                            if (myconnection != null) {
                                Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK CAMERA " +
                                        "4");
                                myVcard = new VCard();
                                SmackConfiguration.setPacketReplyTimeout(300000);
                                ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                                try {
                                    myVcard.load(myconnection, REAL_USERNAME);
                                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", myVcard.getFirstName());
                                    myVcard.setAvatar(my_avatar_byte_array);
                                    myVcard.save(myconnection);
                                } catch (XMPPException e) {
                                    myVcard = null;
                                    e.printStackTrace();
                                }

                            }
                        }


                    };
                    thread_camera.start();
                    iv_connected_user_avatar.setImageBitmap(mybitmap);
                    //my_avatar = (Bitmap) avatar.get("data");
                    break;

                case PHONE_AVATAR:
                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK PHONE 1");
                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK PHONE 2");
                    Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@", "ACTIVITY FOR RESULT OK PHONE 3");

                    final InputStream is;

                    try {
                        is = getContentResolver().openInputStream(data.getData());
                        mybitmap = BitmapFactory.decodeStream(is);
                        Thread thread_pone = new Thread(){

                            public void run(){

                                try {
                                    // We need to recyle unused bitmaps;
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    mybitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                                    my_avatar_byte_array = stream.toByteArray();
                                    stream.close();

                                    if (myconnection != null) {
                                        Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@",
                                                "ACTIVITY FOR RESULT OK PHONE 4");
                                        myVcard = new VCard();
                                        SmackConfiguration.setPacketReplyTimeout(300000);
                                        ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
                                        try {
                                            myVcard.load(myconnection, REAL_USERNAME);
                                            Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@",myVcard.getFirstName());
                                            myVcard.setAvatar(my_avatar_byte_array);
                                            myVcard.save(myconnection);
                                        } catch (XMPPException e) {
                                            myVcard = null;
                                            e.printStackTrace();
                                        }

                                    }

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        };
                        thread_pone.start();
                        iv_connected_user_avatar.setImageBitmap(mybitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

            }

        }else if(resultCode == RESULT_CANCELED){

            Log.d("!!!!!!!!!!!!!@@@@@@@@@@@@@@@@","RESULT_CANCELED NOOOOOOOOOOOOOOOOOOOOOOOOO");
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                        (CuotaActivity.isInFront==null || CuotaActivity.isInFront==false) &&
                        (isInFront==null || isInFront==false)){

            return true;
        }

        return false;
    }

    private void sendConnectNotification(Boolean aBoolean){

        if(aBoolean && isInHome()){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_chat);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_chat_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.chat_uci_title));

            notificationCompatBuilder.setContentText(USERNAME + "\n" + getResources().getString(R
                    .string.co) + " " + HOST);
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .chat_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(ChatActivity.this, ChatActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    ChatActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_CHAT,
                    notificationCompatBuilder.build());

        }else if(!aBoolean && isInHome()){

            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_chat);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_chat_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string
                    .chat_uci_title));

            notificationCompatBuilder.setContentText(getResources().getString(R.string
                    .error_con));
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .chat_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(ChatActivity.this, ChatActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    ChatActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_CHAT,
                    notificationCompatBuilder.build());
        }else{
            if(aBoolean && !isInFront){
                sendSuccessToast();
            }else if(!aBoolean && !isInHome()){
                sendErrorToast(ChatActivity.this.getResources().getString(R.string.error_con));
            }
        }
    }

    private void sendNewMessageNotification(String from){
            notificationCompatBuilder.setSmallIcon(R.drawable.ic_stat_chat);
            notificationCompatBuilder.setLargeIcon(
                    ((BitmapDrawable)getResources().getDrawable(R.drawable
                            .ic_chat_nav)).getBitmap()
            );
            notificationCompatBuilder.setContentTitle(getResources().getString(R.string.new_mess));

            notificationCompatBuilder.setContentText(from);
            notificationCompatBuilder.setContentInfo(getResources().getString(R.string.uno));
            notificationCompatBuilder.setTicker(getResources().getString(R.string
                    .chat_uci_title));
            notificationCompatBuilder.setAutoCancel(true);
            setNotificationsDefaults();

            Intent notIntent =
                    new Intent(ChatActivity.this, ChatActivity.class);
            PendingIntent contIntent = PendingIntent.getActivity(
                    ChatActivity.this, 0, notIntent, 0);
            notificationCompatBuilder.setContentIntent(contIntent);

            notificationManager.notify(CampusNotification.NOTIFICATION_ID_CHAT,
                    notificationCompatBuilder.build());
    }

    private void setNotificationsDefaults(){
        boolean vibrate;
        boolean sound;
        boolean led;
        int defaults = 0;

        if(sharedPreferences.contains(CampusPreference.CHAT_VIBRATE)){
            vibrate = sharedPreferences.getBoolean(CampusPreference.CHAT_VIBRATE,
                    false);
        }else{
            vibrate = true;
        }

        if(sharedPreferences.contains(CampusPreference.CHAT_SOUND)){
            sound = sharedPreferences.getBoolean(CampusPreference.CHAT_SOUND,true);
        }else{
            sound = false;
        }

        if(sharedPreferences.contains(CampusPreference.CHAT_LED)){
            led = sharedPreferences.getBoolean(CampusPreference.CHAT_LED,true);
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
