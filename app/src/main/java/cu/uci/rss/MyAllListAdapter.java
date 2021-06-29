package cu.uci.rss;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.util.ArrayList;

import cu.uci.campusuci.CampusPreference;
import cu.uci.campusuci.R;

/**
 * Created by Yannier on 5/27/2015.
 */
public class MyAllListAdapter extends ArrayAdapter<Item> {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Typeface typeface;
    Typeface typeface1;
    Activity ctx;
    ArrayList<AllItem> it;
    int layout;

    public MyAllListAdapter(Context context, int resource, Activity ctx,ArrayList<AllItem> items,
                         Typeface t, Typeface t1, int l) {
        super(context, resource);
        this.it = items;
        this.ctx = ctx;
        this.typeface = t;
        this.typeface1 = t1;
        this.layout = l;

        sharedPreferences = this.ctx.getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE,
                this.ctx.MODE_PRIVATE);
        editor = this.ctx.getSharedPreferences(CampusPreference.CAMPUS_PREFERENCE,
                this.ctx.MODE_PRIVATE).edit();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if(item == null) {
            LayoutInflater inflater = this.ctx.getLayoutInflater();
            item = inflater.inflate(layout, null);

            holder = new ViewHolder();
            holder.link = (TextView) item.findViewById(R.id.tv_link);
            holder.iv_rss = (ImageView) item.findViewById(R.id.iv_rss);
            holder.author = (TextView) item.findViewById(R.id.tv_author);
            holder.date = (TextView) item.findViewById(R.id.tv_date);
            holder.title = (TextView) item.findViewById(R.id.tv_title);
            holder.decript = (TextView) item.findViewById(R.id.tv_descrpt);
            holder.iv_rss_picture = (ImageView) item.findViewById(R.id.iv_rss_picture);
            holder.comments = (TextView) item.findViewById(R.id.tv_comments);
            holder.shadow = item.findViewById(R.id.shadow_third);

            item.setTag(holder);
        }else{
            holder = (ViewHolder)item.getTag();
        }
        holder.link.setText(it.get(position).url);
        holder.link.setTypeface(typeface);
        holder.iv_rss.setImageDrawable(ctx.getResources().getDrawable(it.get(position).img_drawable));
        holder.title.setText(it.get(position).title);
        holder.title.setTypeface(typeface1);
        String texto = Jsoup.parse(it.get(position).desc).text();
        it.get(position).desc = texto;
        holder.decript.setText(texto);
        holder.decript.setTypeface(typeface);

        if(it.get(position).author != null){
            holder.author.setText(it.get(position).author);
            holder.author.setTypeface(typeface1);
            holder.author.setVisibility(View.VISIBLE);
        }else{
            holder.author.setVisibility(View.GONE);
        }

        if(it.get(position).pubDate != null){
            holder.date.setText(it.get(position).pubDate);
            holder.date.setTypeface(typeface);
            holder.date.setVisibility(View.VISIBLE);
        }else{
            holder.date.setVisibility(View.GONE);
        }

        if(it.get(position).comments != null){
            holder.comments.setText(it.get(position).comments);
            holder.comments.setTypeface(typeface);
        }else{
            holder.comments.setText("0");
            holder.comments.setTypeface(typeface);
        }

        if(it.get(position).img != null && !it.get(position).img.equals(RssDataBaseAttr.NOT_IMAGE)){
            Picasso.with(ctx).load(it.get(position).img).into(holder.iv_rss_picture);
            holder.iv_rss_picture.setVisibility(View.VISIBLE);
            holder.shadow.setVisibility(View.VISIBLE);
        }else{
            holder.iv_rss_picture.setVisibility(View.GONE);
            holder.shadow.setVisibility(View.GONE);
        }

        boolean rss_animate_news;

        if (sharedPreferences.contains(CampusPreference.ANIMATE_NEWS_ENTRY)) {
            rss_animate_news = sharedPreferences.getBoolean(CampusPreference.ANIMATE_NEWS_ENTRY,
                    false);
        } else {
            editor.putBoolean(CampusPreference.ANIMATE_NEWS_ENTRY, true);
            editor.commit();
            rss_animate_news = sharedPreferences.getBoolean(CampusPreference.ANIMATE_NEWS_ENTRY,
                    false);
        }

        if(rss_animate_news){
            if(it.get(position).anim){
                Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.fab_jump_from_down);
                item.startAnimation(animation);
                it.get(position).anim = false;
            }
        }else{
            it.get(position).anim = false;
        }

        return item;
    }

    @Override
    public int getCount() {
        return this.it.size();
    }

    private class ViewHolder{
        TextView title;
        TextView author;
        TextView link;
        TextView decript;
        TextView date;
        TextView comments;
        ImageView iv_rss;
        ImageView iv_rss_picture;
        View shadow;
    }

}
