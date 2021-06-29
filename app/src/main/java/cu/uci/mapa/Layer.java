package cu.uci.mapa;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController.OnZoomListener;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;

import cu.uci.campusuci.R;
import cu.uci.mapa.database.DBFavorito;
import cu.uci.mapa.markers.MyMarker;

public class Layer extends TileRendererLayer implements OnZoomListener{

	Context ctx;
	MapView mv;
	MyMarker marker;
    Activity activity;

    AssetManager am;
    Typeface roboto_light;


	public Layer(Activity activity,Context ctx, MapView mv, TileCache tileCache,
			MapViewPosition mapViewPosition, boolean isTransparent,
			GraphicFactory graphicFactory) {
		super(tileCache, mapViewPosition, isTransparent, isTransparent,
				graphicFactory);

        this.activity = activity;
		this.ctx = ctx;
		this.mv = mv;
		this.marker = null;
        am = this.ctx.getAssets();
        roboto_light = Typeface.createFromAsset(am, "roboto_light.ttf");

		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
		// TODO Auto-generated method stub

		if (this.marker == null) {
			marker = new MyMarker(this.activity,this.ctx, mv, tapLatLong,
					AndroidGraphicFactory
							.convertToBitmap(this.ctx.getResources()
									.getDrawable(R.drawable.ic_maps_pin)), 0, 0);
			this.mv.getLayerManager().getLayers().add(marker);
            StringBuilder text = new StringBuilder();
            text.append(ctx.getResources().getString(R.string.new_pos) + "\n");
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
            i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_mapa_nav));

            Toast toast = new Toast(ctx);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();

            //Toast toast = Toast.makeText(ctx, text.toString(), Toast.LENGTH_SHORT);
            //toast.setGravity(Gravity.BOTTOM, 0, 0);
            //toast.show();
			return true;
		} else if (this.marker != null) {
			this.mv.getLayerManager().getLayers().remove(marker);
			marker = new MyMarker(this.activity,this.ctx, mv, tapLatLong,
					AndroidGraphicFactory
							.convertToBitmap(this.ctx.getResources()
									.getDrawable(R.drawable.ic_maps_pin)), 0, 0);
			this.mv.getLayerManager().getLayers().add(marker);
            StringBuilder text = new StringBuilder();
            text.append(ctx.getResources().getString(R.string.new_pos) + "\n");
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
            i.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_mapa_nav));

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

		return super.onLongPress(tapLatLong, layerXY, tapXY);
	}

	@Override
	public void onVisibilityChanged(boolean visible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onZoom(boolean zoomIn) {
		// TODO Auto-generated method stub

		BoundingBox mapLimit = mv.getModel().mapViewPosition.getMapLimit();

		if (zoomIn == true) {

			if (mv.getModel().mapViewPosition.getZoomLevel() == mv.getModel().mapViewPosition
					.getZoomLevelMax()) {
				mapLimit = new BoundingBox(22.9748, -82.4851, 22.9998, -82.4452);
				mv.getModel().mapViewPosition.setMapLimit(mapLimit);
			} else {
				BoundingBox mapLimitAux = new BoundingBox(
						mapLimit.minLatitude + 40, mapLimit.minLongitude,
						mapLimit.maxLatitude - 30, mapLimit.maxLongitude);
				mv.getModel().mapViewPosition.setMapLimit(mapLimitAux);
			}

		} else if (zoomIn == false) {

			if (mv.getModel().mapViewPosition.getZoomLevel() == mv.getModel().mapViewPosition
					.getZoomLevelMin()) {
				mapLimit = new BoundingBox(22.9870 - 40, -82.4728, 22.9908,
						-82.4575);
				mv.getModel().mapViewPosition.setMapLimit(mapLimit);
			} else {
				BoundingBox mapLimitAux = new BoundingBox(mapLimit.minLatitude,
						mapLimit.minLongitude, mapLimit.maxLatitude + 30,
						mapLimit.maxLongitude);
				mv.getModel().mapViewPosition.setMapLimit(mapLimitAux);
			}

		}

	}

}
