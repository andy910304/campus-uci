package cu.uci.horario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

public class Connect {

	LinkedList<String> listafacultad = new LinkedList<>();
	LinkedList<String> listasemanas = new LinkedList<>();
	LinkedList<String> listabrigadas = new LinkedList<>();

	String url = "http://horario.uci.cu/Default.aspx";
	Element __VIEWSTATE;
	Element __EVENTVALIDATION;
	Element __EVENTTARGET;
	Element __EVENTARGUMENT;
	Element __LASTFOCUS;

	Element ctlHeader_cmbListaFacultades;
	Element ctlHeader_cmbSemanas;
	Element ctlToolBar_AñoLB;
	Element ctlToolBar_BrigadaLB;
	Element ctlToolBar_ActividadLB;
	Element ctlToolBar_ProfesorLB;
	Element ctlToolBar_LocalLB;
	Document doc;

	public Connect() throws IOException {
		// TODO Auto-generated constructor stub
		doc = Jsoup.connect(url).get();
		loadData();
	}

	private void loadData() {
		// TODO Auto-generated method stub
		__VIEWSTATE = doc.select("#__VIEWSTATE").first();
		__EVENTVALIDATION = doc.select("#__EVENTVALIDATION").first();
		__EVENTTARGET = doc.select("#__EVENTTARGET").first();
		__EVENTARGUMENT = doc.select("#__EVENTARGUMENT").first();
		__LASTFOCUS = doc.select("#__LASTFOCUS").first();

		ctlHeader_cmbListaFacultades = doc.select(
				"#ctlHeader_cmbListaFacultades").first();
		ctlHeader_cmbSemanas = doc.select("#ctlHeader_cmbSemanas").first();
		ctlToolBar_AñoLB = doc.select("#ctlToolBar_AñoLB").first();
		ctlToolBar_BrigadaLB = doc.select("#ctlToolBar_BrigadaLB").first();
		ctlToolBar_ActividadLB = doc.select("#ctlToolBar_ActividadLB").first();
		ctlToolBar_ProfesorLB = doc.select("#ctlToolBar_ProfesorLB").first();
		ctlToolBar_LocalLB = doc.select("#ctlToolBar_LocalLB").first();
	}

	public String[] cargarSemanas(String facultad) throws IOException {
		doc = Jsoup.connect(url)
				.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
				.data("__VIEWSTATE", __VIEWSTATE.val())
				.data("ctlHeader$cmbListaFacultades", facultad).post();
		loadData();

		Elements semanasSelectOptions = doc
				.select("#ctlHeader_cmbSemanas option");
		int cantSemanas = semanasSelectOptions.size();
		String[] semanas = new String[cantSemanas];
		for (int i = 0; i < cantSemanas; i++) {
			semanas[i] = semanasSelectOptions.get(i).val();
		}

		return semanas;
	}

	// necesariamente ejecutar despues de ejecutar insertarFacultad
	// devuelve un arreglo de String (brigadas existentes para la facultad
	// fijada)
	public String[] cargarBrigadas(String facultad, String semana)
			throws IOException {
		doc = Jsoup.connect(url)
				.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
				.data("__VIEWSTATE", __VIEWSTATE.val())
				.data("ctlHeader$cmbListaFacultades", facultad).post();
		loadData();
		doc = Jsoup.connect(url)
				.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
				.data("__VIEWSTATE", __VIEWSTATE.val())
				.data("ctlHeader$cmbListaFacultades", facultad)
				.data("ctlHeader$cmbSemanas", semana)
				.data("ctlToolBar$AñoLB", "seleccione")
				.data("ctlToolBar$BrigadaLB", "seleccione")
				.data("ctlToolBar$ActividadLB", "seleccione")
				.data("ctlToolBar$ProfesorLB", "seleccione")
				.data("ctlToolBar$LocalLB", "seleccione").post();
		loadData();

		Elements brigadasSelectOptions = doc
				.select("#ctlToolBar_BrigadaLB option");
		int cantBrigadas = brigadasSelectOptions.size();
		String[] brigadas = new String[cantBrigadas-1];
        int x = 0;
		for (int i = 1; i < cantBrigadas; i++) {
			brigadas[x] = brigadasSelectOptions.get(i).val();
            x++;
		}

		return brigadas;
	}
	

	
	public String[] MostrarHorario(String facultad, String semana,
			String brigada) throws IOException {
		
		String nome;
		
		doc = Jsoup.connect(url)
				.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
				.data("__VIEWSTATE", __VIEWSTATE.val())
				.data("ctlHeader$cmbListaFacultades", facultad)
				.post();
		loadData();
		
		doc = Jsoup.connect(url)
				.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
				.data("__VIEWSTATE", __VIEWSTATE.val())
				.data("ctlHeader$cmbListaFacultades", facultad)
				.data("ctlHeader$cmbSemanas", semana)
				.data("ctlToolBar$AñoLB", "seleccione")
				.data("ctlToolBar$BrigadaLB", "seleccione")
				.data("ctlToolBar$ActividadLB", "seleccione")
				.data("ctlToolBar$ProfesorLB", "seleccione")
				.data("ctlToolBar$LocalLB", "seleccione").post();
		loadData();
		
		doc = Jsoup.connect(url)
				.data("__EVENTVALIDATION", __EVENTVALIDATION.val())
				.data("__VIEWSTATE", __VIEWSTATE.val())
				.data("ctlHeader$cmbListaFacultades", facultad)
				.data("ctlHeader$cmbSemanas", semana)
				.data("ctlToolBar$AñoLB", "seleccione")
				.data("ctlToolBar$BrigadaLB", brigada)
				.data("ctlToolBar$ActividadLB", "seleccione")
				.data("ctlToolBar$ProfesorLB", "seleccione")
				.data("ctlToolBar$LocalLB", "seleccione").post();

		Element tabla = doc.select("#TABLE1").first();
		Elements name = tabla.getElementsByTag("span");
		int tamano = name.size();
		String[] horario = new String[tamano];

		for (int i = 0, x=0; i < tamano; i++) {
			nome = name.get(i).text();
			horario[x++]=nome;
		}

		return horario;
	}

}
