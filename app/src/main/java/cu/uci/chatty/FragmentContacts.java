package cu.uci.chatty;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import cu.uci.campusuci.R;
import cu.uci.utils.FloatingButtonLib.ActionButton;

/**
 * Created by Yannier on 5/28/2015.
 */
public class FragmentContacts extends Fragment{

    int selected_user;
    int long_selected_user;

    Boolean is_connected = false;
    String user_to_save;
    String pass_to_save;
    Boolean save_to_auth = false;

    private ConnectListener connectListener;
    private AddContactListener addContactListener;
    private StateListener stateListener;
    private AvatarListener avatarListener;
    private StatusMessageListener statusMessageListener;
    private OpenChatListener openChatListener;
    private DirectorioSearchListener directorioSearchListener;

    View contacts;
    ListView lv_contacts;
    ImageView iv_avatar, iv_state;
    EditText et_status_mes;
    ActionButton fab;

    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_light, roboto_condense_bold, roboto_condensed_light_italic;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        contacts = inflater.inflate(R.layout.vp_chat_list,container,false);
        initFAB();
        init_List_Texts_Images();

        return contacts;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void initFAB(){

        fab = (ActionButton) contacts.findViewById(R.id.fab_add);
        fab.setType(ActionButton.Type.DEFAULT);
        fab.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fab.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fab.setImageResource(R.drawable.ic_fa_sign_in);
        fab.setShadowRadius(4.4f);
        fab.setShadowXOffset(2.8f);
        fab.setShadowYOffset(2.1f);
        fab.setStrokeColor(getResources().getColor(R.color.fab_material_yellow_900));
        fab.setStrokeWidth(0.0f);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_pop);
                fab.startAnimation(pop);

                if(!is_connected){
                    if(connectListener != null){
                        connectListener.onConnectListener();
                    }
                }else{

                }

            }
        });

    }

    private void init_List_Texts_Images(){

        am = getActivity().getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");
        roboto_condense_bold = Typeface.createFromAsset(am,"roboto_condense_bold.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");
        roboto_condensed_light_italic = Typeface.createFromAsset(am,"roboto_condensed_light_italic.ttf");

        et_status_mes = (EditText) contacts.findViewById(R.id.et_status_mes);
        et_status_mes.setTypeface(roboto_condensed_light_italic);
        et_status_mes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(statusMessageListener != null){
                    statusMessageListener.onStatusMessageListener();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lv_contacts = (ListView) contacts.findViewById(R.id.lv_chat);
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(openChatListener != null){
                    selected_user = position;
                    openChatListener.onOpenChatListener();
                }
            }
        });
        lv_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(directorioSearchListener != null){
                            long_selected_user = position;
                            directorioSearchListener.onDirectorioSearchListener();
                }
                return true;
            }
        });

        iv_avatar = (ImageView) contacts.findViewById(R.id.iv_avatar);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_pop);
                iv_avatar.startAnimation(pop);
                iv_avatar.setClickable(false);
                if(avatarListener != null){
                    avatarListener.onAvatarListener();
                }
            }
        });

        iv_state = (ImageView) contacts.findViewById(R.id.iv_state);
        iv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_pop);
                iv_state.startAnimation(pop);
                iv_state.setClickable(false);
                    if(stateListener != null){
                        stateListener.onStateListener();
                    }
            }
        });
    }

    public void setOpenChatListener(OpenChatListener listener){
        this.openChatListener = listener;
    }

    public void setConnectListener(ConnectListener listener){
        this.connectListener = listener;
    }

    public void setStateListener(StateListener listener){
        this.stateListener = listener;
    }

    public void setAvatarListener(AvatarListener listener){
        this.avatarListener = listener;
    }

    public void setStatusMessageListener(StatusMessageListener listener){
        this.statusMessageListener = listener;
    }

    public void setDirectorioSearchListener(DirectorioSearchListener listener){
        this.directorioSearchListener = listener;
    }

    public interface DirectorioSearchListener{
        void onDirectorioSearchListener();
    }

    public interface OpenChatListener{
        void onOpenChatListener();
    }

    public interface StatusMessageListener{
        void onStatusMessageListener();
    }

    public interface AvatarListener{
        void onAvatarListener();
    }

    public interface StateListener{
        void onStateListener();
    }

    public interface ConnectListener{
        void onConnectListener();
    }

    public interface AddContactListener{
        void onAddContactListener();
    }

}
