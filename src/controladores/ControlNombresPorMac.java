package controladores;

import generadores.GeneradorNombresPorMac;

public class ControlNombresPorMac {

	public ControlNombresPorMac(String[] args) {
		GeneradorNombresPorMac g;
		if (args.length == 0) {
			g = new GeneradorNombresPorMac();
		} else if (args.length == 1) {
			g = new GeneradorNombresPorMac(args[0]);
		} else if (args.length == 2) {
			g = new GeneradorNombresPorMac(args[0],Integer.valueOf(args[1]),0);
		} else {
			g = new GeneradorNombresPorMac(args[0],Integer.valueOf(args[1]),Integer.valueOf(args[2]));
		}
		g.obtenerIpsCSV();
		g.iniciarPrintWriter();
		g.generarCabecerahtml();
		g.cambiarIpPorAlias();
		g.generarCabecerasMac();
		g.lineaPunteada();
		g.parsearCapturaMacs();
		g.generarCierrehtml();
		g.cerrarPrintWriter();
	}
	
}
