package cu.uci.rss;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cu.uci.campusuci.R;

/**
 * Created by Yannier on 3/10/2015.
 */
public class MySpinnerAdapter extends ArrayAdapter {

    Typeface roboto_condense_light;
    RssActivity rssActivity;
    Context ctx;
    String[] datos;
    int[] imgs;

    public MySpinnerAdapter(RssActivity rssActivity,Context ctx, String[] datos, int[] images,
                            int spinner_layout,
                            Typeface roboto_condense_light) {
        super(ctx, spinner_layout, datos);
        this.ctx = ctx;
        this.rssActivity = rssActivity;
        this.datos = datos;
        this.imgs = images;
        this.roboto_condense_light = roboto_condense_light;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder holder;

        if(item == null){

            LayoutInflater inflater = this.rssActivity.getLayoutInflater();
            item = inflater.inflate(R.layout.spinner_layout, null);

            item.setBackgroundColor(ctx.getResources().getColor(R.color.app_background));

            holder = new ViewHolder();

            holder.rss = (TextView) item.findViewById(R.id.rss_id);
            holder.logo = (ImageView) item.findViewById(R.id.rss_img_id);

            item.setTag(holder);
        }else{
            holder = (ViewHolder) item.getTag();
        }

        holder.rss.setText(datos[position]);
        holder.rss.setTypeface(roboto_condense_light);
        holder.rss.setTextColor(ctx.getResources().getColor(R.color.text_color));
        holder.logo.setImageResource(imgs[position]);

        return (item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if(item == null){

            LayoutInflater inflater = this.rssActivity.getLayoutInflater();
            item = inflater.inflate(R.layout.spinner_layout, null);

            holder = new ViewHolder();

            holder.rss = (TextView) item.findViewById(R.id.rss_id);
            holder.logo = (ImageView) item.findViewById(R.id.rss_img_id);

            item.setTag(holder);
        }else{
            holder = (ViewHolder) item.getTag();
        }

        holder.rss.setText(datos[position]);
        holder.rss.setTypeface(roboto_condense_light);
        holder.logo.setImageResource(imgs[position]);

        return (item);
    }

    private class ViewHolder{

        ImageView logo;
        TextView rss;

    }


}
