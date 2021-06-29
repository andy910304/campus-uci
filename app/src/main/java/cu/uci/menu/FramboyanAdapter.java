package cu.uci.menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cu.uci.campusuci.R;
import cu.uci.rss.Item;
import cu.uci.rss.RssDataBaseAttr;

/**
 * Created by Yannier on 5/19/2015.
 */
public class FramboyanAdapter extends ArrayAdapter<Item> {

    Typeface typeface;
    Typeface typeface1;
    Activity ctx;
    ArrayList<Plato> it;
    int layout;

    public FramboyanAdapter(Context context, int resource, Activity ctx,
                            ArrayList<Plato> items, Typeface t, Typeface t1, int l) {
        super(context, resource);
        this.it = items;
        this.ctx = ctx;
        this.typeface = t;
        this.typeface1 = t1;
        this.layout = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if (item == null) {
            LayoutInflater inflater = this.ctx.getLayoutInflater();
            item = inflater.inflate(layout, null);

            holder = new ViewHolder();
            holder.nombre = (TextView) item.findViewById(R.id.tv_nombre);
            holder.precio = (TextView) item.findViewById(R.id.tv_precio);
            holder.img = (ImageView) item.findViewById(R.id.iv_plato);

            item.setTag(holder);
        } else {
            holder = (ViewHolder) item.getTag();
        }
        holder.nombre.setText(it.get(position).nombre);
        holder.nombre.setTypeface(typeface);
        holder.precio.setText(it.get(position).precio);
        holder.precio.setTypeface(typeface1);

        if (it.get(position).img != null) {
            Picasso.with(ctx).load(it.get(position).img).into(holder.img);
            holder.img.setVisibility(View.VISIBLE);
        } else {
            holder.img.setVisibility(View.GONE);
        }

        return item;
    }

    @Override
    public int getCount() {
        return this.it.size();
    }

    private class ViewHolder {
        TextView nombre;
        TextView precio;
        ImageView img;
    }

}