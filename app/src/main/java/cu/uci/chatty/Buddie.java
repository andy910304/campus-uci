package cu.uci.chatty;

import android.graphics.Bitmap;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by Yannier on 5/20/2015.
 */
public class Buddie {

    String user = null;
    String name = null;
    Presence.Mode mode = null;
    String status = null;
    String status_mess = null;
    String state = null;
    byte[] img = null;
    Bitmap bm = null;
    Boolean anim = true;
    Boolean con = false;
    Boolean remove = false;
    Boolean con_update = false;

}
