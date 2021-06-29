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

public class TheMarkers extends Marker {

    private Activity activity;
	private Context ctx;
	private MapView mapView;
	private String descp;
    EditText descrp;
    AssetManager am;
    Typeface roboto_light;
    DBFavorito database;


	public TheMarkers(Activity activity,Context ctx, String descp, MapView mapView,
			LatLong latLong, Bitmap bitmap, int horizontalOffset,
			int verticalOffset) {
		super(latLong, bitmap, horizontalOffset, verticalOffset);

        this.activity = activity;
		this.ctx = ctx;
		this.mapView = mapView;
		this.descp = descp;

        am = this.ctx.getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");

	}

	@Override
	public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
		if (this.contains(layerXY, tapXY)) {
			StringBuilder text = new StringBuilder();
			text.append(this.descp + ":" + "\n");
			text.append("Lat " + tapLatLong.latitude + "\n");
			text.append("Lon " + tapLatLong.longitude + "\n");

            int image = getImageResource();

            LayoutInflater inflater = activity.getLayoutInflater();
            View layout = inflater.inflate(
                    R.layout.mapa_toast_1
                    ,(ViewGroup) activity.findViewById(R.id.mapa_toast_layout));

            TextView t = (TextView) layout.findViewById(R.id.tv_mapa_toast);
            t.setTypeface(roboto_light);
            t.setText(text.toString());

            ImageView i = (ImageView) layout.findViewById(R.id.iv_mapa_toast);
            i.setImageDrawable(ctx.getResources().getDrawable(image));

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
        if (this.contains(layerXY, tapXY)) {
            showSaveDialog(latlong);
            return true;
        }
        return super.onLongPress(tapLatLong, layerXY, tapXY);
    }

    private int getImageResource(){
        int image = 0;

        if(this.descp.contains("Docente")){
            image = R.drawable.ic_docente;
        }else if(this.descp.contains("Cafetería")){
            image = R.drawable.ic_cafeteria;
        }else if(this.descp.contains("Plaza")){
            image = R.drawable.ic_plaza;
        }else if(this.descp.contains("Complejo Comedor")){
            image = R.drawable.ic_comedor;
        }else if(this.descp.contains("Rectorado")){
            image = R.drawable.ic_rectorado;
        }else if(this.descp.contains("Discoteca")){
            image = R.drawable.ic_discoteca;
        }else if(this.descp.contains("Panadería")){
            image = R.drawable.ic_panaderia;
        }else if(this.descp.contains("Banco")){
            image = R.drawable.ic_banco;
        }else if(this.descp.contains("Centro cultural")){
            image = R.drawable.ic_centro_cultural;
        }else if(this.descp.contains("IP")){
            image = R.drawable.ic_ip;
        }else if(this.descp.contains("Pista")){
            image = R.drawable.ic_pista;
        }else if(this.descp.contains("Área deportiva")){
            image = R.drawable.ic_deporte;
        }else if(this.descp.contains("Primera Entrada")){
            image = R.drawable.ic_entrada;
        }else if(this.descp.contains("Segunda Entrada")){
            image = R.drawable.ic_entrada;
        }else if(this.descp.contains("Piscina")){
            image = R.drawable.ic_piscina;
        }else if(this.descp.contains("Pizzería")){
            image = R.drawable.ic_pizzeria;
        }else if(this.descp.contains("Hospital")){
            image = R.drawable.ic_hospital;
        }

        return image;
    }

    private void showSaveDialog(final LatLong latlong) {

        final LatLong latlong1 = latlong;
        final String positive = ctx.getResources().getString(R.string.salvar);
        final String negative = ctx.getResources().getString(R.string.hide_);
        final String title = ctx.getResources().getString(R.string.salvar_fav_);
        MaterialDialog dialog = new MaterialDialog.Builder(this.ctx)
                .title(title)
                .icon(mapView.getResources().getDrawable(R.drawable.ic_dialog_save))
                .customView(R.layout.save_favorite_layout_dialog, true)
                .positiveText(positive)
                .theme(Theme.LIGHT)
                .cancelable(true)
                .negativeText(negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        database = new DBFavorito(ctx);
                        database.open();
                        database.saveFavorite(descrp.getText().toString(),
                                latlong.latitude, latlong.longitude);
                        database.close();

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
                        mapView.getLayerManager().getLayers().remove(TheMarkers.this);
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
        descrp.setText(descp);
        positiveAction.setEnabled(true); // disabled by default
    }

}
