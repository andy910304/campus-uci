package cu.uci.mapa;

import android.content.Context;
import android.graphics.Canvas;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.view.MapView;

public class UciMapa extends MapView {

	boolean v;
	boolean v1;
    int zoomLevel;
    int oldZoomLevel;

	public UciMapa(Context context) {
		super(context);
		v = false;
		v1 = false;
        this.zoomLevel = this.getModel().mapViewPosition.getZoomLevel();
        this.oldZoomLevel = this.getModel().mapViewPosition.getZoomLevel();
		// TODO Auto-generated constructor stub
	}

    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.zoomLevel = this.getModel().mapViewPosition.getZoomLevel();
        if (this.zoomLevel != oldZoomLevel) {
            //do your thing
            oldZoomLevel = this.zoomLevel;
            if(this.zoomLevel < 15){

                this.getModel().mapViewPosition.setZoomLevel((byte) 15);
                this.getModel().mapViewPosition.setCenter(new LatLong(22.9880,
                        -82.4650));
                BoundingBox mapLimitAux = new BoundingBox(22.9870, -82.4728,
                        22.9908, -82.4575);
                this.getModel().mapViewPosition.setMapLimit(mapLimitAux);

            }else if (this.getModel().mapViewPosition.getZoomLevel() == 15) {

                BoundingBox mapLimitAux = new BoundingBox(22.9870, -82.4728,
                        22.9908, -82.4575);
                this.getModel().mapViewPosition.setMapLimit(mapLimitAux);

            } else if (this.getModel().mapViewPosition.getZoomLevel() > 15
                    && this.getModel().mapViewPosition.getZoomLevel() <= 20) {

                BoundingBox mapLimitAux = new BoundingBox(22.9748, -82.4851,
                        22.9998, -82.4452);
                this.getModel().mapViewPosition.setMapLimit(mapLimitAux);

            } else if (this.getModel().mapViewPosition.getZoomLevel() > 20) {

                this.getModel().mapViewPosition.setZoomLevel((byte) 20);

            }
        }
    }



    public void verifyZoom() {
		if (this.getModel().mapViewPosition.getZoomLevel() < 15) {

			this.getModel().mapViewPosition.setZoomLevel((byte) 15);
			this.getModel().mapViewPosition.setCenter(new LatLong(22.9880,
					-82.4650));
			BoundingBox mapLimitAux = new BoundingBox(22.9870, -82.4728,
					22.9908, -82.4575);
			this.getModel().mapViewPosition.setMapLimit(mapLimitAux);

		} else if (this.getModel().mapViewPosition.getZoomLevel() == 15) {

			BoundingBox mapLimitAux = new BoundingBox(22.9870, -82.4728,
					22.9908, -82.4575);
			this.getModel().mapViewPosition.setMapLimit(mapLimitAux);

		} else if (this.getModel().mapViewPosition.getZoomLevel() > 15
				&& this.getModel().mapViewPosition.getZoomLevel() <= 20) {

			BoundingBox mapLimitAux = new BoundingBox(22.9748, -82.4851,
					22.9998, -82.4452);
			this.getModel().mapViewPosition.setMapLimit(mapLimitAux);

		} else if (this.getModel().mapViewPosition.getZoomLevel() > 20) {

			this.getModel().mapViewPosition.setZoomLevel((byte) 18);

		}

	}

	public void verifyLimits() {

		if (this.getModel().mapViewPosition.getZoomLevel() > 15) {
			v = true;
		} else {
			v = false;
		}

		if (v == true) {
			BoundingBox mapLimitAux = new BoundingBox(22.9748, -82.4851,
					22.9998, -82.4452);
			this.getModel().mapViewPosition.setMapLimit(mapLimitAux);
		} else {
			BoundingBox mapLimitAux = new BoundingBox(22.9870, -82.4728,
					22.9908, -82.4575);
			this.getModel().mapViewPosition.setMapLimit(mapLimitAux);
		}

	}
}
