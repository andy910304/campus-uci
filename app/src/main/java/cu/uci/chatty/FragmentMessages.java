package cu.uci.chatty;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import cu.uci.campusuci.R;
import cu.uci.utils.FloatingButtonLib.ActionButton;

/**
 * Created by Yannier on 5/28/2015.
 */
public class FragmentMessages extends Fragment {

    SendMessageListener sendMessageListener;

    View chats;
    ListView lv_chat_view;
    ImageView iv_emoticons, iv_send;
    EditText et_messages;

    AssetManager am;
    Typeface roboto_condense_light, roboto_light_italic, roboto_light, roboto_condense_bold, roboto_condensed_light_italic;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        chats = inflater.inflate(R.layout.vp_chat_messages,container,false);
        init_List_Texts_Images();

        return chats;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init_List_Texts_Images(){

        am = getActivity().getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
        roboto_condense_light = Typeface.createFromAsset(am,"roboto_condense_light.ttf");
        roboto_condense_bold = Typeface.createFromAsset(am,"roboto_condense_bold.ttf");
        roboto_light_italic = Typeface.createFromAsset(am,"roboto_light_italic.ttf");
        roboto_condensed_light_italic = Typeface.createFromAsset(am,"roboto_condensed_light_italic.ttf");

        et_messages = (EditText) chats.findViewById(R.id.et_message);
        et_messages.setTypeface(roboto_condensed_light_italic);

        lv_chat_view = (ListView) chats.findViewById(R.id.lv_chat_view);
        lv_chat_view.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        iv_emoticons = (ImageView) chats.findViewById(R.id.iv_emoticons);
        iv_emoticons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_pop);
                iv_emoticons.startAnimation(pop);
            }
        });

        iv_send = (ImageView) chats.findViewById(R.id.iv_send);
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation pop = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_pop);
                iv_send.startAnimation(pop);

                if(sendMessageListener != null){
                    sendMessageListener.onSendMessage();
                }
            }
        });
    }

    public void setOnSendMessageListener(SendMessageListener listener){
        this.sendMessageListener = listener;
    }

    public interface SendMessageListener{
        void onSendMessage();
    }
}
