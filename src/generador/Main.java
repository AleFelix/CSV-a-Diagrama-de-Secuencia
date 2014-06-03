package generador;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Main {

	public static void main(String[] args) {
		Generador g = new Generador();
		List macs = new ArrayList();
		List listaIps = new ArrayList();
		g.obtenerIpsCSV(macs, listaIps);
		g.iniciarPrintWriter();
		g.generarCabecerahtml();
		g.generarCabeceras(listaIps);
		g.lineaPunteada(listaIps);
		g.parsearCaptura(macs, listaIps);
		g.generarCierrehtml();
		g.cerrarPrintWriter();
	}
	
}
