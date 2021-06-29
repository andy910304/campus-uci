package cu.uci.chatty;

import org.jivesoftware.smack.Chat;

import java.util.ArrayList;


/**
 * Created by Yannier on 5/26/2015.
 */
public class MyChat {

    String user;
    ArrayList<ChatMessage> messages;
    Chat chat;


    public MyChat(String user, Chat chat) {
        this.user = user;
        this.chat = chat;
        messages = new ArrayList<>();
    }

}
