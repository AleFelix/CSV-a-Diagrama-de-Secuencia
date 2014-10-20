package controladores;

import generadores.GeneradorNormal;

public class ControlNormal {

	public ControlNormal(String[] args) {
		GeneradorNormal g;
		if (args.length == 0) {
			g = new GeneradorNormal();
		} else if (args.length == 1) {
			g = new GeneradorNormal(args[0]);
		} else if (args.length == 2) {
			g = new GeneradorNormal(args[0],Integer.valueOf(args[1]),0);
		} else {
			g = new GeneradorNormal(args[0],Integer.valueOf(args[1]),Integer.valueOf(args[2]));
		}
		g.obtenerIpsCSV();
		g.iniciarPrintWriter();
		g.generarCabecerahtml();
		g.generarCabeceras();
		g.lineaPunteada();
		g.parsearCaptura();
		g.generarCierrehtml();
		g.cerrarPrintWriter();
	}
	
}
