package cu.uci.chatty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;

import cu.uci.campusuci.R;
import cu.uci.utils.views.CircleImageView;

/**
 * Created by Yannier on 5/20/2015.
 */
public class ChatContactAdapter extends ArrayAdapter<Buddie> {

    int removed_pos = -1;
    Typeface typeface;
    Typeface typeface1;
    Activity ctx;
    ArrayList<Buddie> it;
    int layout;

    public ChatContactAdapter(Context context, int resource, Activity ctx, ArrayList<Buddie> items,
                              Typeface t, Typeface t1, int l) {
        super(context, resource);
        this.it = items;
        this.ctx = ctx;
        this.typeface = t;
        this.typeface1 = t1;
        this.layout = l;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if(item == null) {
            LayoutInflater inflater = this.ctx.getLayoutInflater();
            item = inflater.inflate(R.layout.chat_user, null);

            holder = new ViewHolder();
            holder.name = (TextView) item.findViewById(R.id.tv_name);
            holder.status = (TextView) item.findViewById(R.id.tv_status);
            holder.status_mess = (TextView) item.findViewById(R.id.tv_status_mess);
            holder.img = (CircleImageView) item.findViewById(R.id.iv_user);
            holder.state = (CircleImageView) item.findViewById(R.id.iv_state);
            holder.iv_con = (ImageView) item.findViewById(R.id.iv_con);

            item.setTag(holder);

        }else{
            holder = (ViewHolder)item.getTag();
        }
        holder.name.setTypeface(typeface);
        holder.name.setText(it.get(position).name);
        holder.status.setText(it.get(position).status);
        holder.status.setTypeface(typeface1);

        if(it.get(position).status_mess != null){
            holder.status_mess.setTypeface(typeface1);
            holder.status_mess.setVisibility(View.VISIBLE);
            holder.status_mess.setText(it.get(position).status_mess);
        }else{
            holder.status_mess.setVisibility(View.GONE);
        }

        if(it.get(position).img != null){
            if(it.get(position).bm == null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(it.get(position).img, 0,
                        it.get(position).img.length);
                it.get(position).bm = bitmap;
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", it.get(position).img + "");
                holder.img.setImageBitmap(bitmap);
                holder.img.invalidate();
            }else{
                holder.img.setImageBitmap(it.get(position).bm);
                holder.img.invalidate();
            }
        }else{
            Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "NUUUUUUUUUUUUULLLLLLLLLLLLLLLLLLLLLLL");
            holder.img.setImageDrawable(ctx.getResources().getDrawable(R.drawable.empty_user));
        }

        if(it.get(position).mode == Presence.Mode.away){
            holder.state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_away));
        }else if(it.get(position).mode == Presence.Mode.dnd){
            holder.state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_dnd));
        }else if(it.get(position).mode == Presence.Mode.chat){
            holder.state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_chatty));
        }else if(it.get(position).mode == Presence.Mode.xa){
            holder.state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_xa));
        }else{
            holder.state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_available));
        }

        if(it.get(position).anim){
            Animation anim = AnimationUtils.loadAnimation(ctx,R.anim.fab_jump_from_down);
            item.startAnimation(anim);
            it.get(position).anim = false;
        }

        //if(it.get(position).con){
        //    Animation anim = AnimationUtils.loadAnimation(ctx,R.anim.fab_scale_up);
        //    holder.iv_con.setVisibility(View.VISIBLE);
        //    holder.iv_con.startAnimation(anim);
        //    it.get(position).con = false;
        //    //it.get(position).con = false;
        //}

        //if(it.get(position).remove){
            //final View view1 = item;
            //Thread t = new Thread(){
             // public void run(){
                  //try {
                    //  Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.exit_menu);
                    //  view1.startAnimation(animation);
                    //  sleep(720);
                    //  remove(it.get(removed_pos));
                 // } catch (InterruptedException e) {
                  //    e.printStackTrace();
                 // }
              //}
            //};
            //t.start();
        //}

        return item;
    }

    public void set(int i){
        removed_pos = i;
    }

    public void remove(int position) {
        this.it.remove(position);
        //return super.getItem(position);
    }

    @Override
    public Buddie getItem(int position) {
        return this.it.get(position);
        //return super.getItem(position);
    }

    public ArrayList<Buddie> getAll() {
        return this.it;
        //return super.getItem(position);

    }
        @Override
    public void add(Buddie object) {
        this.it.add(object);
        //super.add(object);
    }

    @Override
    public int getCount() {
        return this.it.size();
    }

    private class ViewHolder{
        TextView name;
        TextView status;
        TextView status_mess;
        CircleImageView img;
        CircleImageView state;
        ImageView iv_con;
    }



}
