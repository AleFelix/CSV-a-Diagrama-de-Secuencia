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

@SuppressWarnings("rawtypes")
public class Generador {

	final static int longTitulo = 15;
	final static String tapa = "+-----------------+";
	final static String paddingCabecera = "          ";
	final static String paddingTitulos = "                 ";
	final static String paddingLineas = "                   ";
	final static String flechaDerecha = "------------------------------------->";
	final static String flechaIzquierda = "<-------------------------------------";
	final static String cuerpoFlecha = "--------------------------------------";
	final static String Broadcast = "Broadcast";
	final static String ARP = "ARP";
	final static String puntos = "...";
	private List listaLineas;
	private PrintWriter escribir;
	  
	public void iniciarPrintWriter() {
		DateFormat formato = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy");
		Date fecha = new Date();
		try {
			escribir = new PrintWriter("Diagrama (" + formato.format(fecha) + ").html");
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,"No se pudo crear el archivo del diagrama","No se puede crear",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
	public void generarCabecerahtml() {
		escribir.println("<html>");
		escribir.println("<title>Diagrama de Secuencia</title>");
		escribir.println("<body>");
		escribir.println("<pre>");
	}
	
	public void generarCierrehtml() {
		escribir.println("</pre>");
		escribir.println("</body>");
		escribir.println("</html>");
	}

	public void cerrarPrintWriter() {
		JOptionPane.showMessageDialog(null,"Diagrama de secuencia creado con éxito en el directorio:\n"+System.getProperty("user.dir"),"Diagrama creado",JOptionPane.INFORMATION_MESSAGE);
		escribir.close();
	}

	public void nLinea() {
		escribir.println("");
	}

	@SuppressWarnings({ "unchecked" })
	public void obtenerIpsCSV(List macs, List listaIps) {
		CSVReader lector = null;
		listaLineas = null;
		String[] lineaActual;
		try {
			lector = new CSVReader(new FileReader("./captura.csv"));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null,"No se encontro el archivo captura.csv en el directorio:\n"+System.getProperty("user.dir"),"No encontrado",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		try {
			listaLineas = lector.readAll();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"No se pudo leer el archivo captura.csv (Esta corrupto)","Error en archivo",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		List destinos = new ArrayList();
		String[] ipMac;
		for (int i = 1; i < listaLineas.size(); i++) {
			lineaActual = (String[]) listaLineas.get(i);
			ipMac = new String[2];
			if (!destinos.contains(lineaActual[2]) && !lineaActual[4].equals(ARP)) {
				destinos.add(lineaActual[2]);
			}
			if (!destinos.contains(lineaActual[3]) && !lineaActual[4].equals(ARP)) {
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
			ipMac = (String[]) macs.get(i);
			if (!listaIps.contains(ipMac[1])) {
				listaIps.add(ipMac[1]);
			}
		}
	}
	
	public int modulo(int n) {
		if (n < 0) {
			return n * -1;
		} else {
			return n;
		}
	}

	public void generarCabeceras(List listaIps) {
		int tamTitulo;
		String titulo;
		for (int i = 0; i <= listaIps.size(); i++) {
			escribir.print(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
		for (int i = 0; i < listaIps.size(); i++) {
			titulo = (String) listaIps.get(i);
			tamTitulo = titulo.length();
			if(tamTitulo > longTitulo) {
				titulo = titulo.substring(0,longTitulo-puntos.length());
				titulo = titulo + puntos;
				tamTitulo = titulo.length();
			}
			escribir.print(paddingCabecera + "| " + titulo + paddingTitulos.substring(tamTitulo + 1) + "|" + paddingCabecera);
		}
		escribir.println(paddingCabecera + "| " + Broadcast + paddingTitulos.substring(Broadcast.length() + 1) + "|" + paddingCabecera);
		for (int i = 0; i <= listaIps.size(); i++) {
			escribir.print(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
	}

	public void lineaPunteada(List listaIps) {
		for (int i = 0; i <= listaIps.size(); i++) {
			escribir.print(paddingLineas + "|" + paddingLineas);
		}
		nLinea();
	}

	public void formarFlecha(int origen, int destino, List listaIps) {
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
		escribir.print(paddingLineas + "|");
		while (i < inicio) {
			escribir.print(paddingLineas + paddingLineas + "|");
			i++;
		}
		if (!derecha) {
			escribir.print(flechaIzquierda);
		} else {
			if (longitud > 1) {
				escribir.print(cuerpoFlecha);
			} else {
				escribir.print(flechaDerecha);
			}
		}
		int restante = longitud - 1;
		while (restante > 1) {
			escribir.print("-" + cuerpoFlecha);
			restante--;
		}
		if (derecha) {
			if (longitud > 1) {
				escribir.print("-" + flechaDerecha + "|");
			} else {
				escribir.print("|");
			}
		} else {
			if (longitud >= 2) {
				escribir.print("-"+cuerpoFlecha+"|");
			} else {
				escribir.print("|");
			}
		}
		while (posFinal < listaIps.size()) {
			escribir.print(paddingLineas + paddingLineas + "|");
			posFinal++;
		}
		nLinea();
	}

	public void agregarMensaje(String mensaje, int posOrigen, int posDestino, List listaIps) {
		int posInicio;
		if (posOrigen < posDestino) {
			posInicio = posOrigen;
		} else {
			posInicio = posDestino;
		}
		int i = 0;
		escribir.print(paddingLineas + "|");
		int longitud = paddingLineas.length() + 1;
		while (i < posInicio) {
			escribir.print(paddingLineas + paddingLineas + "|");
			longitud = longitud + (paddingLineas.length() * 2) + 1;
			i++;
		}
		escribir.print(mensaje);
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
						escribir.print(" ");
						i++;
					}
					escribir.print("|");
					while (medida < tamTotal) {
						escribir.print(paddingLineas + paddingLineas + "|");
						medida = medida + longEspacios + 1;
					}
				}
			}
		}
		nLinea();
	}

	public void parsearCaptura(List macs, List listaIps) {
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
				if (!destino.equals(Broadcast)) {
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
			if (!destino.equals(Broadcast)) {
				posDestino = listaIps.indexOf(destino);
			} else {
				posDestino = listaIps.size();
			}
			agregarMensaje(mensaje, posOrigen, posDestino, listaIps);
			formarFlecha(posOrigen, posDestino, listaIps);
			lineaPunteada(listaIps);
			lineaPunteada(listaIps);
			agregarCabeceras++;
			if (agregarCabeceras == 5) {
				generarCabeceras(listaIps);
				agregarCabeceras = 0;
				lineaPunteada(listaIps);
			}
		}
	}
}
