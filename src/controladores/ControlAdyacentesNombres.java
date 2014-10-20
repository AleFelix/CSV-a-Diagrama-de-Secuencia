package controladores;

import generadores.GeneradorAdyacentesNombres;

public class ControlAdyacentesNombres {

	public ControlAdyacentesNombres(String[] args) {
		GeneradorAdyacentesNombres g;
		if (args.length == 0) {
			g = new GeneradorAdyacentesNombres();
		} else if (args.length == 1) {
			g = new GeneradorAdyacentesNombres(args[0]);
		} else if (args.length == 2) {
			g = new GeneradorAdyacentesNombres(args[0],Integer.valueOf(args[1]),0);
		} else {
			g = new GeneradorAdyacentesNombres(args[0],Integer.valueOf(args[1]),Integer.valueOf(args[2]));
		}
		g.obtenerIpsCSV();
		g.iniciarPrintWriter();
		g.generarCabecerahtml();
		g.cambiarIpPorAlias();
		g.parsearCaptura();
		g.generarCierrehtml();
		g.cerrarPrintWriter();
	}
	
}
