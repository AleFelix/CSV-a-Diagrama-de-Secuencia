package generador;

public class Main {

	public static void main(String[] args) {
		Generador g;
		if (args.length == 0) {
			g = new Generador();
		} else if (args.length == 1) {
			g = new Generador(args[0]);
		} else if (args.length == 2) {
			g = new Generador(args[0],Integer.valueOf(args[1]),0);
		} else {
			g = new Generador(args[0],Integer.valueOf(args[1]),Integer.valueOf(args[2]));
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
