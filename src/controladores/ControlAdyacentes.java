package controladores;

import generadores.GeneradorAdyacentes;

public class ControlAdyacentes {

	public ControlAdyacentes(String[] args) {
		GeneradorAdyacentes g;
		if (args.length == 0) {
			g = new GeneradorAdyacentes();
		} else if (args.length == 1) {
			g = new GeneradorAdyacentes(args[0]);
		} else if (args.length == 2) {
			g = new GeneradorAdyacentes(args[0],Integer.valueOf(args[1]),0);
		} else {
			g = new GeneradorAdyacentes(args[0],Integer.valueOf(args[1]),Integer.valueOf(args[2]));
		}
		g.obtenerIpsCSV();
		g.iniciarPrintWriter();
		g.generarCabecerahtml();
		g.parsearCaptura();
		g.generarCierrehtml();
		g.cerrarPrintWriter();
	}
	
}
