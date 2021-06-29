package cu.uci.mapa.markers;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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


public class FavoriteMarker extends Marker{

    private Activity activity;
	private Context ctx;
	private String descp;
	private MapView mapView;
    DBFavorito database;
    EditText des, lat, lon;
    Double la, lo;
    Boolean v1, v2, v3;

    AssetManager am;
    Typeface roboto_light;
	
	public FavoriteMarker(Activity activity,Context ctx, String descp, MapView mapView,
                          LatLong latLong, Bitmap bitmap, int horizontalOffset,
			int verticalOffset) {
		super(latLong, bitmap, horizontalOffset, verticalOffset);
        this.activity = activity;
		this.ctx = ctx;
		this.descp = descp;
		this.mapView = mapView;

        am = this.ctx.getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");
	}

	@Override
	public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
		// TODO Auto-generated method stub
		if (this.contains(layerXY, tapXY)) {
			StringBuilder text = new StringBuilder();
			text.append(this.descp + ":" + "\n");
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
            i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_dialog_favorite));

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
		// TODO Auto-generated method stub
		
		final LatLong latlong = tapLatLong;
		final String d = this.descp;
		if (this.contains(layerXY, tapXY)) {

			showOptionDialog(latlong, d);
			return true;
		}
		return super.onLongPress(tapLatLong, layerXY, tapXY);
	}

    private void showOptionDialog(final LatLong latlong, String d) {
        final LatLong latlong1 = latlong;
        final String d1 = d;
        StringBuilder text = new StringBuilder();
        text.append(d + ":" + "\n");
        text.append("Lat " + latlong1.latitude + "\n");
        text.append("Lon " + latlong1.longitude + "\n");
        final String positive = ctx.getResources().getString(R.string.edit);
        final String negative = ctx.getResources().getString(R.string.del);
        MaterialDialog dialog = new MaterialDialog.Builder(this.ctx)
                .title(ctx.getResources().getString(R.string.opc_fav))
                .icon(mapView.getResources().getDrawable(R.drawable.ic_dialog_favorite))
                .content(text.toString())
                .theme(Theme.LIGHT)
                .cancelable(true)
                .positiveText(positive)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        showOptionDialogEdit(latlong1);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        showOptionDialogDel(latlong1, d1);
                    }

                }).build();
        dialog.show();
    }

    private void showOptionDialogDel(final LatLong latlong, String d) {
        final LatLong latlong1 = latlong;
        final String d1 = d;
        StringBuilder text = new StringBuilder();
        text.append(d + ":" + "\n");
        text.append("Lat " + latlong1.latitude + "\n");
        text.append("Lon " + latlong1.longitude + "\n");
        final String positive = ctx.getResources().getString(R.string.accept);
        final String negative = ctx.getResources().getString(R.string.cancel);
        MaterialDialog dialog = new MaterialDialog.Builder(this.ctx)
                .title(ctx.getResources().getString(R.string.del_fav))
                .content(text.toString())
                .icon(mapView.getResources().getDrawable(R.drawable.ic_dialog_delete))
                .cancelable(true)
                .theme(Theme.LIGHT)
                .positiveText(positive)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        database = new DBFavorito(ctx);
                        database.open();
                        database.delete(d1);
                        database.close();
                        mapView.getLayerManager().getLayers().remove(FavoriteMarker.this);

                        LayoutInflater inflater = activity.getLayoutInflater();
                        View layout = inflater.inflate(
                                R.layout.mapa_toast_1
                                ,(ViewGroup) activity.findViewById(R.id.mapa_toast_layout));

                        TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                        t.setTypeface(roboto_light);
                        t.setText(ctx.getResources().getString(R.string.del_fav_1));

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
        dialog.show();
    }

    private void showOptionDialogEdit(final LatLong latlong) {

        v1 = true;
        v2 = true;
        v3 = true;
        final LatLong latlong1 = latlong;

        final String positive = ctx.getResources().getString(R.string.edit);
        final String negative = ctx.getResources().getString(R.string.cancel);
        final String title = ctx.getResources().getString(R.string.edit_fav);
        MaterialDialog dialog = new MaterialDialog.Builder(this.ctx)
                .title(title)
                .icon(mapView.getResources().getDrawable(R.drawable.ic_dialog_edit))
                .customView(R.layout.edit_favorite_layout, true)
                .positiveText(positive)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        try {
                            la = Double.parseDouble(lat.getEditableText()
                                    .toString());
                            lo = Double.parseDouble(lon.getEditableText()
                                    .toString());
                            database = new DBFavorito(ctx);
                            database.open();
                            database.edit(descp, des.getEditableText().toString(),
                                    la, lo);
                            database.close();

                            LayoutInflater inflater = activity.getLayoutInflater();
                            View layout = inflater.inflate(
                                    R.layout.mapa_toast_1
                                    ,(ViewGroup) activity.findViewById(R.id.mapa_toast_layout));

                            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                            t.setTypeface(roboto_light);
                            t.setText(ctx.getResources().getString(R.string.edit_fav_1));

                            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                            i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_favorito));

                            Toast toast = new Toast(ctx);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setView(layout);
                            toast.show();

                            FavoriteMarker.this.descp = des.getEditableText().toString();

                        } catch (Exception e) {
                            // TODO: handle exception
                            LayoutInflater inflater = activity.getLayoutInflater();
                            View layout = inflater.inflate(
                                    R.layout.mapa_toast_1
                                    ,(ViewGroup) activity.findViewById(R.id.mapa_toast_layout));

                            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
                            t.setTypeface(roboto_light);
                            t.setText(ctx.getResources().getString(R.string.correct_data));

                            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
                            i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_nav_stat_error));

                            Toast toast = new Toast(ctx);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setView(layout);
                            toast.show();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                }).build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        des = (EditText) dialog.getCustomView().findViewById(R.id.etEDES);
        des.setText(descp);
        lat = (EditText) dialog.getCustomView().findViewById(R.id.etELAT);
        lat.setText(latlong1.latitude + "");
        lon = (EditText) dialog.getCustomView().findViewById(R.id.etELON);
        lon.setText(latlong1.longitude + "");

        des.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() > 0){
                    v1 = true;
                }else{
                    v1 = false;
                }
                    positiveAction.setEnabled(v1 && v2 && v3);


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() > 0){
                    v2 = true;
                }else{
                    v2 = false;
                }
                positiveAction.setEnabled(v1 && v2 && v3);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length() > 0){
                    v3 = true;
                }else{
                    v3 = false;
                }
                positiveAction.setEnabled(v1 && v2 && v3);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.show();
        positiveAction.setEnabled(true); // disabled by default
    }
}
