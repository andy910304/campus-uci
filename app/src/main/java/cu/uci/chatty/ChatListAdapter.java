package cu.uci.chatty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cu.uci.campusuci.R;


/**
 * Created by madhur on 17/01/15.
 */
public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatListAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;

    }


    public void add(ChatMessage object){
        this.chatMessages.add(object);
    }

    public ArrayList<ChatMessage> getAll() {
        return this.chatMessages;
        //return super.getItem(position);

    }

    public void setAll(ArrayList<ChatMessage> objects) {
        this.chatMessages = objects;
        //return super.getItem(position);

    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ChatMessage message = chatMessages.get(position);
        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.userType == UserType.OTHER) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                holder1 = new ViewHolder1();


                holder1.form = (TextView) v.findViewById(R.id.chat_company_reply_author);
                holder1.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder1.timeTextView = (TextView) v.findViewById(R.id.time_text);

                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();

            }

            holder1.form.setText(message.user.replace("@jabber.uci.cu", ""));
            holder1.messageTextView.setText(message.messageText);
            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.messageTime));

        } else if (message.userType == UserType.SELF) {

            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);

                holder2 = new ViewHolder2();

                holder2.form = (TextView) v.findViewById(R.id.chat_company_reply_author);
                holder2.messageTextView = (TextView) v.findViewById(R.id.message_text);
                holder2.timeTextView = (TextView) v.findViewById(R.id.time_text);
                v.setTag(holder2);

            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();

            }

            holder2.form.setText(context.getResources().getString(R.string.me_));
            holder2.messageTextView.setText(message.messageText);
            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.messageTime));

        }

        return v;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        return message.userType.ordinal();
    }

    private class ViewHolder1 {
        public TextView form;
        public TextView messageTextView;
        public TextView timeTextView;
    }

    private class ViewHolder2 {
        public TextView form;
        public TextView messageTextView;
        public TextView timeTextView;

    }
}
