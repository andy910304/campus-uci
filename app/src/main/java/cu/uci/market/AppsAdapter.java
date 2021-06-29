package cu.uci.market;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import cu.uci.campusuci.R;

/**
 * Created by Yannier on 6/13/2015.
 */
public class AppsAdapter extends BaseAdapter{

    Activity ctx;
    ArrayList<App> repo_apps;
    Typeface roboto_light_bold, roboto_light;

    public AppsAdapter(Activity ctx, ArrayList<App> repo_apps, Typeface roboto_light_bold,
                       Typeface roboto_light){
        this.ctx = ctx;
        this.repo_apps = repo_apps;
        this.roboto_light_bold = roboto_light_bold;
        this.roboto_light = roboto_light;

    }

    @Override
    public int getCount() {
        return this.repo_apps.size();
    }

    @Override
    public Object getItem(int position) {
        return this.repo_apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if(item == null) {
            LayoutInflater inflater = this.ctx.getLayoutInflater();
            item = inflater.inflate(R.layout.app_layout, null);

            holder = new ViewHolder();
            holder.apkname = (TextView) item.findViewById(R.id.tv_title);
            holder.size = (TextView) item.findViewById(R.id.tv_size);
            holder.details = (ImageView) item.findViewById(R.id.iv_details);
            holder.app = (ImageView) item.findViewById(R.id.iv_app);

            item.setTag(holder);
        }else{
            holder = (ViewHolder)item.getTag();
        }
        holder.apkname.setText(repo_apps.get(position).apkname);
        holder.apkname.setTypeface(roboto_light_bold);
        holder.size.setText(repo_apps.get(position).size);
        holder.size.setTypeface(roboto_light);

        if(repo_apps.get(position).image != null){
            Picasso.with(ctx).load(repo_apps.get(position).image).into(holder.app);
        }

            if(repo_apps.get(position).animate){
                Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.fab_scale_up);
                item.startAnimation(animation);
                repo_apps.get(position).animate = false;
            }

        return item;
    }

    private class ViewHolder{
        TextView apkname;
        TextView size;
        ImageView app;
        ImageView details;
    }

}
