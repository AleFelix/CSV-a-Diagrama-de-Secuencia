package main;

import controladores.ControlAdyacentes;
import controladores.ControlAdyacentesNombres;
import controladores.ControlNombresPorMac;
import controladores.ControlNormal;
import controladores.ControlNormalNombres;

public class Main {
	
	public static final int MODO_NORMAL = 0;
	public static final int MODO_ADYACENTES = 1;
	public static final int MODO_NOMBRES = 2;
	public static final int MODO_ADYACENTES_NOMBRES = 3;
	public static final int MODO_NOMBRES_POR_MAC = 4;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		int modo;
		boolean hasArgs = false;
		if (args.length == 0) {
			modo = MODO_NORMAL;
		} else {
			if (args.length > 1)
				hasArgs = true;
			modo = Integer.valueOf(args[0]);
			if (modo < MODO_NORMAL || modo > MODO_NOMBRES_POR_MAC)
				modo = MODO_NORMAL;
		}
		String[] args2;
		if (hasArgs) {
			args2 = new String[args.length-1];
			for (int i = 0; i < args2.length; i++)
				args2[i] = args[i+1];
		} else {
			args2 = new String[0];
		}
		switch (modo) {
		case MODO_NORMAL:
			ControlNormal cn = new ControlNormal(args2);
			break;
		case MODO_ADYACENTES:
			ControlAdyacentes ca = new ControlAdyacentes(args2);
			break;
		case MODO_NOMBRES:
			ControlNormalNombres cnn = new ControlNormalNombres(args2);
			break;
		case MODO_ADYACENTES_NOMBRES:
			ControlAdyacentesNombres can = new ControlAdyacentesNombres(args2);
			break;
		case MODO_NOMBRES_POR_MAC:
			ControlNombresPorMac cnm = new ControlNombresPorMac(args2);
			break;
		}
	}

}
