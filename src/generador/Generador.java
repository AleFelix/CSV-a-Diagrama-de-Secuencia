package generador;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVReader;

public class Generador {

	public final static int TAM_DEF_CAB = 19;
	public final static int MIN_TAM_CAB = 13;
	public final static int MAX_TAM_RESTA = 9;
	public final static String BROADCAST = "Broadcast";
	public final static String ARP = "ARP";
	public final static String PUNTOS = "...";
	public final static String NOM_CAP_DEF = "./captura.csv";

	private int tn;
	private int restar;
	private int longTitulo;
	private String tapa;
	private String paddingCabecera;
	private String paddingTitulos;
	private String paddingLineas;
	private String flechaDerecha;
	private String flechaIzquierda;
	private String cuerpoFlecha;
	private String nombreCaptura;
	private boolean local;

	private List<String[]> listaLineas;
	private PrintWriter escribir;
	private TextoGrafico tg;
	private List<String> listaIps;
	private List<String[]> macs;

	void inicializarVariables() {
		tg = new TextoGrafico();
		longTitulo = tn - 4;
		tapa = tg.crearTapa(tn);
		paddingCabecera = tg.crearPadding(tn / 2 + 1 - restar);
		paddingTitulos = tg.crearPadding(tn - 2);
		paddingLineas = tg.crearPadding(tn - restar);
		flechaDerecha = tg.crearFlecha((tn - restar) * 2, true);
		flechaIzquierda = tg.crearFlecha((tn - restar) * 2, false);
		cuerpoFlecha = tg.crearFlecha((tn - restar) * 2);
		macs = new ArrayList<String[]>();
		listaIps = new ArrayList<String>();
	}

	void cambiarProporcion(int tam, int rest) {
		if (tam < MIN_TAM_CAB) {
			tam = MIN_TAM_CAB;
		} else if (tam % 2 == 0) {
			tam = tam + 1;
		}
		if (rest > MAX_TAM_RESTA) {
			rest = MAX_TAM_RESTA;
		}
		tn = tam;
		restar = rest;
	}

	public Generador(String archivo) {
		tn = TAM_DEF_CAB;
		restar = 0;
		local = false;
		nombreCaptura = archivo;
		inicializarVariables();
	}

	public Generador(String archivo, int tam, int rest) {
		cambiarProporcion(tam, rest);
		local = false;
		nombreCaptura = archivo;
		inicializarVariables();
	}

	public Generador() {
		tn = TAM_DEF_CAB;
		restar = 0;
		local = true;
		nombreCaptura = NOM_CAP_DEF;
		inicializarVariables();
	}

	public Generador(int tam, int rest) {
		cambiarProporcion(tam, rest);
		tn = tam;
		restar = rest;
		local = true;
		nombreCaptura = NOM_CAP_DEF;
		inicializarVariables();
	}

	public void iniciarPrintWriter() {
		if (local) {
			DateFormat formato = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy");
			Date fecha = new Date();
			try {
				escribir = new PrintWriter("Diagrama (" + formato.format(fecha)
						+ ").html");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,
						"No se pudo crear el archivo del diagrama",
						"No se puede crear", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
	}

	public void imprimirln(String texto) {
		if (local) {
			escribir.println(texto);
		} else {
			System.out.println(texto);
		}
	}

	public void imprimir(String texto) {
		if (local) {
			escribir.print(texto);
		} else {
			System.out.print(texto);
		}
	}

	public void generarCabecerahtml() {
		imprimirln("<html>");
		imprimirln("<title>Diagrama de Secuencia</title>");
		imprimirln("<body>");
		imprimirln("<pre>");
	}

	public void generarCierrehtml() {
		imprimirln("</pre>");
		imprimirln("</body>");
		imprimirln("</html>");
	}

	public void cerrarPrintWriter() {
		if (local) {
			JOptionPane.showMessageDialog(null,
					"Diagrama de secuencia creado con éxito en el directorio:\n"
							+ System.getProperty("user.dir"),
					"Diagrama creado", JOptionPane.INFORMATION_MESSAGE);
			escribir.close();
		}
	}

	public void nLinea() {
		imprimirln("");
	}

	public void obtenerIpsCSV() {
		CSVReader lector = null;
		listaLineas = null;
		String[] lineaActual;
		try {
			lector = new CSVReader(new FileReader(nombreCaptura));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null,
					"No se encontro el archivo captura.csv en el directorio:\n"
							+ System.getProperty("user.dir"), "No encontrado",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		try {
			listaLineas = lector.readAll();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"No se pudo leer el archivo captura.csv (Esta corrupto)",
					"Error en archivo", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		List<String> destinos = new ArrayList<String>();
		String[] ipMac;
		for (int i = 1; i < listaLineas.size(); i++) {
			lineaActual = (String[]) listaLineas.get(i);
			ipMac = new String[2];
			if (!destinos.contains(lineaActual[2])
					&& !lineaActual[4].equals(ARP)) {
				destinos.add(lineaActual[2]);
			}
			if (!destinos.contains(lineaActual[3])
					&& !lineaActual[4].equals(ARP)) {
				destinos.add(lineaActual[3]);
			}
			if (lineaActual[4].equals(ARP)) {
				if (lineaActual[6].substring(0, 3).equals("Who")) {
					ipMac[0] = lineaActual[2];
					ipMac[1] = lineaActual[6].split(" ")[5];
					macs.add(ipMac);
				} else {
					ipMac[0] = lineaActual[2];
					ipMac[1] = lineaActual[6].split(" ")[0];
					macs.add(ipMac);
				}
			}
		}
		try {
			lector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < destinos.size(); i++) {
			if (!listaIps.contains(destinos.get(i))) {
				listaIps.add(destinos.get(i));
			}
		}
		for (int i = 0; i < macs.size(); i++) {
			ipMac = macs.get(i);
			if (!listaIps.contains(ipMac[1])) {
				listaIps.add(ipMac[1]);
			}
		}
	}

	public void generarCabeceras() {
		int tamTitulo;
		String titulo;
		for (int i = 0; i <= listaIps.size(); i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
		for (int i = 0; i < listaIps.size(); i++) {
			titulo = (String) listaIps.get(i);
			tamTitulo = titulo.length();
			if (tamTitulo > longTitulo) {
				titulo = titulo.substring(0, longTitulo - PUNTOS.length());
				titulo = titulo + PUNTOS;
				tamTitulo = titulo.length();
			}
			imprimir(paddingCabecera + "| " + titulo
					+ paddingTitulos.substring(tamTitulo + 1) + "|"
					+ paddingCabecera);
		}
		imprimirln(paddingCabecera + "| " + BROADCAST
				+ paddingTitulos.substring(BROADCAST.length() + 1) + "|"
				+ paddingCabecera);
		for (int i = 0; i <= listaIps.size(); i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
	}

	public void lineaPunteada() {
		for (int i = 0; i <= listaIps.size(); i++) {
			imprimir(paddingLineas + "|" + paddingLineas);
		}
		nLinea();
	}

	public void formarFlecha(int origen, int destino) {
		boolean derecha;
		int longitud;
		int inicio;
		int posFinal;
		if (origen < destino) {
			derecha = true;
			longitud = destino - origen;
			inicio = origen;
		} else {
			derecha = false;
			longitud = origen - destino;
			inicio = destino;
		}
		posFinal = inicio + longitud;
		int i = 0;
		imprimir(paddingLineas + "|");
		while (i < inicio) {
			imprimir(paddingLineas + paddingLineas + "|");
			i++;
		}
		if (!derecha) {
			imprimir(flechaIzquierda);
		} else {
			if (longitud > 1) {
				imprimir(cuerpoFlecha);
			} else {
				imprimir(flechaDerecha);
			}
		}
		int restante = longitud - 1;
		while (restante > 1) {
			imprimir("-" + cuerpoFlecha);
			restante--;
		}
		if (derecha) {
			if (longitud > 1) {
				imprimir("-" + flechaDerecha + "|");
			} else {
				imprimir("|");
			}
		} else {
			if (longitud >= 2) {
				imprimir("-" + cuerpoFlecha + "|");
			} else {
				imprimir("|");
			}
		}
		while (posFinal < listaIps.size()) {
			imprimir(paddingLineas + paddingLineas + "|");
			posFinal++;
		}
		nLinea();
	}

	public void agregarMensaje(String mensaje, int posOrigen, int posDestino) {
		int posInicio;
		if (posOrigen < posDestino) {
			posInicio = posOrigen;
		} else {
			posInicio = posDestino;
		}
		int i = 0;
		imprimir(paddingLineas + "|");
		int longitud = paddingLineas.length() + 1;
		while (i < posInicio) {
			imprimir(paddingLineas + paddingLineas + "|");
			longitud = longitud + (paddingLineas.length() * 2) + 1;
			i++;
		}
		imprimir(mensaje);
		int longEspacios = paddingLineas.length() * 2;
		int longInicial = paddingLineas.length() + 1;
		int tamTotal = longInicial + (longEspacios + 1) * listaIps.size();
		longitud = longitud + mensaje.length();
		int medida = longInicial;
		if (longitud < tamTotal) {
			while (medida <= longitud) {
				if (medida <= longitud) {
					medida = medida + longEspacios + 1;
				}
				if (medida > longitud) {
					i = 0;
					while (i + longitud < medida - 1) {
						imprimir(" ");
						i++;
					}
					imprimir("|");
					while (medida < tamTotal) {
						imprimir(paddingLineas + paddingLineas + "|");
						medida = medida + longEspacios + 1;
					}
				}
			}
		}
		nLinea();
	}

	public void parsearCaptura() {
		String[] datos = new String[5];
		String numero;
		String protocolo;
		String origen;
		String destino;
		String mensaje;
		int posOrigen;
		int posDestino;
		int posListaMacs = 0;
		int agregarCabeceras = 0;
		for (int i = 1; i < listaLineas.size(); i++) {
			datos = (String[]) listaLineas.get(i);
			numero = datos[0];
			protocolo = datos[4];
			origen = datos[2];
			destino = datos[3];
			mensaje = datos[6];
			if (protocolo.equals(ARP)) {
				String[] macIP = (String[]) macs.get(posListaMacs);
				origen = macIP[1];
				if (!destino.equals(BROADCAST)) {
					boolean encontrado = false;
					String[] macIPActual;
					int p = 0;
					while (!encontrado && p < macs.size()) {
						macIPActual = (String[]) macs.get(p);
						if (macIPActual[0].equals(destino)) {
							destino = macIPActual[1];
							encontrado = true;
						}
						p++;
					}
				}
				posListaMacs++;
			}
			mensaje = numero + "  " + protocolo + "   " + mensaje;
			posOrigen = listaIps.indexOf(origen);
			if (!destino.equals(BROADCAST)) {
				posDestino = listaIps.indexOf(destino);
			} else {
				posDestino = listaIps.size();
			}
			agregarMensaje(mensaje, posOrigen, posDestino);
			formarFlecha(posOrigen, posDestino);
			lineaPunteada();
			lineaPunteada();
			agregarCabeceras++;
			if (agregarCabeceras == 5) {
				generarCabeceras();
				agregarCabeceras = 0;
				lineaPunteada();
			}
		}
	}
}
