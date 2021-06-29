package cu.uci.rss;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cu.uci.campusuci.R;

public class MyListAdapter2 extends ArrayAdapter<Item> {

    Typeface typeface;
    Activity ctx;
    String[] rss_url;
    int[] rss_img;

    public MyListAdapter2(Context context, int resource, Activity ctx, String[] rss_u, int[] rss_i,
                          Typeface t) {
        super(context, resource);
        this.ctx = ctx;
        this.typeface = t;
        this.rss_url = rss_u;
        this.rss_img = rss_i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if (item == null) {
            LayoutInflater inflater = this.ctx.getLayoutInflater();
            item = inflater.inflate(R.layout.layout_rss_select, null);

            holder = new ViewHolder();
            holder.title = (TextView) item.findViewById(R.id.tv_rss_sel);
            holder.img = (ImageView) item.findViewById(R.id.iv_rss_sel);

            item.setTag(holder);
        } else {
            holder = (ViewHolder) item.getTag();
        }
        holder.title.setText(this.rss_url[position]);
        holder.title.setTypeface(typeface);
        holder.img.setImageDrawable(ctx.getResources().getDrawable(this.rss_img[position]));

        return item;
    }

    @Override
    public int getCount() {
        return this.rss_url.length;
    }

    private class ViewHolder {
        TextView title;
        ImageView img;
    }

}