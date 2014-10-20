package controladores;

import generadores.GeneradorNormalNombres;

public class ControlNormalNombres {

	public ControlNormalNombres(String[] args) {
		GeneradorNormalNombres g;
		if (args.length == 0) {
			g = new GeneradorNormalNombres();
		} else if (args.length == 1) {
			g = new GeneradorNormalNombres(args[0]);
		} else if (args.length == 2) {
			g = new GeneradorNormalNombres(args[0],Integer.valueOf(args[1]),0);
		} else {
			g = new GeneradorNormalNombres(args[0],Integer.valueOf(args[1]),Integer.valueOf(args[2]));
		}
		g.obtenerIpsCSV();
		g.iniciarPrintWriter();
		g.generarCabecerahtml();
		g.cambiarIpPorAlias();
		g.generarCabeceras();
		g.lineaPunteada();
		g.parsearCaptura();
		g.generarCierrehtml();
		g.cerrarPrintWriter();
	}
	
}
