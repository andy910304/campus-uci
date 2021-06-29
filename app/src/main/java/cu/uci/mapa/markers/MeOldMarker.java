package cu.uci.mapa.markers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;

import cu.uci.campusuci.R;
import cu.uci.mapa.database.DBFavorito;
import cu.uci.utils.MaterialDialog.base.DialogAction;
import cu.uci.utils.MaterialDialog.base.MaterialDialog;
import cu.uci.utils.MaterialDialog.base.Theme;

public class MeOldMarker extends Marker{

    private Activity activity;
    private Context ctx;
    private MapView mapView;
    DBFavorito database;
    EditText descrp;

    AssetManager am;
    Typeface roboto_light;

    public MeOldMarker(Activity activity,Context ctx, MapView mapView,LatLong latLong,
                       Bitmap bitmap,
                    int horizontalOffset,
                    int verticalOffset) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);

        this.activity = activity;
        this.ctx = ctx;
        this.mapView = mapView;
        am = this.ctx.getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
    }

    @Override
    public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
        if (this.contains(layerXY, tapXY)) {
            StringBuilder text = new StringBuilder();
            text.append(ctx.getResources().getString(R.string.me_old) + "\n");
            text.append("Lat " + tapLatLong.latitude + "\n");
            text.append("Lon " + tapLatLong.longitude + "\n");

            LayoutInflater inflater = activity.getLayoutInflater();
            View layout = inflater.inflate(
                    R.layout.mapa_toast_1
                    ,(ViewGroup) activity.findViewById(R.id.mapa_toast_layout));

            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
            t.setTypeface(roboto_light);
            t.setText(text.toString());

            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
            i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_dialog_my_location));

            Toast toast = new Toast(ctx);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();


            //Toast toast = Toast.makeText(ctx, text.toString(), Toast.LENGTH_SHORT);
            //toast.setGravity(Gravity.BOTTOM, 0, 0);
            //toast.show();
            return true;
        }
        return super.onTap(tapLatLong, layerXY, tapXY);
    }

    @Override
    public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
        final LatLong latlong = tapLatLong;
        // TODO Auto-generated method stub
        if (this.contains(layerXY, tapXY)) {
            showOptionDialog(latlong);
            return true;
        }
        return super.onLongPress(tapLatLong, layerXY, tapXY);
    }


    private void showOptionDialog(final LatLong latlong) {
        final LatLong latlong1 = latlong;
        StringBuilder text = new StringBuilder();
        text.append(mapView.getResources().getString(R.string.me) + "\n");
        text.append("Lat " + latlong1.latitude + "\n");
        text.append("Lon " + latlong1.longitude + "\n");
        final String positive = ctx.getResources().getString(R.string.salvar);
        final String negative = ctx.getResources().getString(R.string.del);
        MaterialDialog dialog = new MaterialDialog.Builder(this.ctx)
                .title(ctx.getResources().getString(R.string.marker))
                .content(text.toString())
                .icon(mapView.getResources().getDrawable(R.drawable.ic_dialog_my_location))
                .cancelable(true)
                .theme(Theme.LIGHT)
                .positiveText(positive)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        showSaveDialog(latlong);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        mapView.getLayerManager().getLayers().remove(MeOldMarker.this);
                    }

                }).build();
        dialog.show();
    }


    private void showSaveDialog(final LatLong latlong) {

        final LatLong latlong1 = latlong;
        final String positive = ctx.getResources().getString(R.string.salvar);
        final String negative = ctx.getResources().getString(R.string.cancel);
        final String title = ctx.getResources().getString(R.string.salvar_fav);
        MaterialDialog dialog = new MaterialDialog.Builder(this.ctx)
                .title(title)
                .icon(mapView.getResources().getDrawable(R.drawable.ic_dialog_save))
                .customView(R.layout.save_favorite_layout_dialog, true)
                .positiveText(positive)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        database = new DBFavorito(ctx);
                        database.open();
                        database.saveFavorite(descrp.getText().toString(),
                                latlong.latitude, latlong.longitude);
                        database.close();
                        mapView.getLayerManager().getLayers().remove(MeOldMarker.this);

                        LayoutInflater inflater = activity.getLayoutInflater();
                        View layout = inflater.inflate(
                                R.layout.mapa_toast_1
                                ,(ViewGroup) activity.findViewById(R.id.mapa_toast_layout));

                        TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                        t.setTypeface(roboto_light);
                        t.setText(ctx.getResources().getString(R.string.salvar_fav_1));

                        ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                        i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_favorito));

                        Toast toast = new Toast(ctx);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.setView(layout);
                        toast.show();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }

                }).build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        descrp = (EditText) dialog.getCustomView().findViewById(R.id._descrip_);
        descrp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }
}
