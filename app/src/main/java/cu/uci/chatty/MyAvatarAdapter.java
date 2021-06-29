package cu.uci.chatty;

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
import cu.uci.rss.Item;

/**
 * Created by Yannier on 5/22/2015.
 */
public class MyAvatarAdapter extends ArrayAdapter<Item> {

    Typeface typeface;
    Activity ctx;
    String[] rss_url;
    int[] rss_img;
    int layout;

    public MyAvatarAdapter(Context context, int resource, Activity ctx, String[] rss_u, int[] rss_i,
                           Typeface t, int l) {
        super(context, resource);
        this.ctx = ctx;
        this.typeface = t;
        this.rss_url = rss_u;
        this.rss_img = rss_i;
        this.layout = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if (item == null) {
            LayoutInflater inflater = this.ctx.getLayoutInflater();
            item = inflater.inflate(this.layout, null);

            holder = new ViewHolder();
            holder.title = (TextView) item.findViewById(R.id.avatar_tv);
            holder.img = (ImageView) item.findViewById(R.id.avatar_iv);

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
