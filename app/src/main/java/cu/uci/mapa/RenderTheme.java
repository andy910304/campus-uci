package cu.uci.mapa;


import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderThemeMenuCallback;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class RenderTheme implements XmlRenderTheme {

	InputStream file;
	
	
	
	public RenderTheme(InputStream file) {

        this.file = file;
	}

	@Override
	public XmlRenderThemeMenuCallback getMenuCallback() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRelativePathPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getRenderThemeAsStream() throws FileNotFoundException {
		// TODO Auto-generated method stub

		return this.file;
	}

}
