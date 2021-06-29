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
 * Created by Yannier on 6/1/2015.
 */
public class ChatListAdapterOpen extends ArrayAdapter<Buddie> {

    int removed_pos = -1;

    Typeface typeface;
    Typeface typeface1;
    Activity ctx;
    ArrayList<Buddie> it;
    int layout;

    public ChatListAdapterOpen(Context context, int resource, Activity ctx, ArrayList<Buddie> items,
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
            item = inflater.inflate(R.layout.chat_user_open, null);

            holder = new ViewHolder();
            holder.name = (TextView) item.findViewById(R.id.tv_name);
            //holder.status = (TextView) item.findViewById(R.id.tv_status);
            //holder.status_mess = (TextView) item.findViewById(R.id.tv_status_mess);
            holder.img = (CircleImageView) item.findViewById(R.id.iv_user);
            //holder.state = (CircleImageView) item.findViewById(R.id.iv_state);
            holder.iv_con = (ImageView) item.findViewById(R.id.iv_con);

            item.setTag(holder);

        }else{
            holder = (ViewHolder)item.getTag();
        }
        holder.name.setTypeface(typeface1);
        holder.name.setText(it.get(position).name);
        //holder.status.setText(it.get(position).status);
        //holder.status.setTypeface(typeface1);

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

        if(it.get(position).anim){
            Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.fab_jump_from_down);
            item.startAnimation(anim);
            it.get(position).anim = false;
        }

        if(it.get(position).con){
            Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.fab_jump_from_down);
            holder.iv_con.setVisibility(View.VISIBLE);
            holder.iv_con.startAnimation(anim);
        }else{
            holder.iv_con.setVisibility(View.INVISIBLE);
        }

        return item;
    }

    @Override
    public void add(Buddie object) {
        this.it.add(object);
        //super.add(object);
    }

    public ArrayList<Buddie> getAll() {
        return this.it;
        //super.add(object);
    }

    public void set(int i){
        removed_pos = i;
    }

    @Override
    public Buddie getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return this.it.size();
    }

    private class ViewHolder{
        TextView name;
        //TextView status;
        //TextView status_mess;
        CircleImageView img;
        //CircleImageView state;
        ImageView iv_con;
    }



}
