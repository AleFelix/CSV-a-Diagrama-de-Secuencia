package generadores;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import graficos.TextoGrafico;

import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVReader;

public class GeneradorNombresPorMac {

	// Defino las variables estaticas
	// Tama�o por defecto de las cabeceras
	public final static int TAM_DEF_CAB = 19;
	// Tama�o minimo que pueden tener las cabeceras
	public final static int MIN_TAM_CAB = 13;
	// Maximo tama�o que se le puede restar a la distancia entre cabeceras
	public final static int MAX_TAM_RESTA = 9;
	// Rotulo de broadcast
	public final static String BROADCAST = "Broadcast";
	// Rotulo de ARP
	public final static String ARP = "ARP";
	// Rotulo de puntos suspensivos (Se a�aden al rotulo cuando no entra en la
	// cabecera)
	public final static String PUNTOS = "...";
	// Nombre por defecto de la captura
	public final static String NOM_CAP_DEF = "./captura.csv";

	// Defino las variables de la clase
	// El tama�o que van a tener las cabeceras
	private int tn;
	// El valor que se restara a la distancia entre cabeceras
	private int restar;
	// El tama�o maximo del titulo de cada cabecera
	private int longTitulo;
	// El texto que dibuja la parte superior e inferior de la cabecera
	private String tapa;
	// El padding que hay entre cabeceras
	private String paddingCabecera;
	// El padding del titulo con respecto al borde derecho de la cabecera
	private String paddingTitulos;
	// El paddding entre las lineas punteadas
	private String paddingLineas;
	// El texto que dibuja la flecha hacia la derecha
	private String flechaDerecha;
	// El texto que dibuja la flecha hacia la izquierda
	private String flechaIzquierda;
	// El texto que dibuja el cuerpo de la flecha sin cabeza (Usado de manera
	// concatenada para alargar las flechas)
	private String cuerpoFlecha;
	// El nombre del archivo .csv
	private String nombreCaptura;
	// Booleano que determina si la ejecucion es local o en el servidor (En
	// realidad, asumo que no es local cuando se le pasan argumentos al main, ya
	// que se ha ejecutado desde la consola de comandos)
	private boolean local;

	// En esta lista se almacenan todas las lineas que conforman el .csv, cada
	// linea esta representada como un arreglo de Strings
	private List<String[]> listaLineas;
	// El objeto que escribe el texto en el archivo
	private PrintWriter escribir;
	// El objeto que cree para generar las cabeceras y flechas de manera
	// dinamica mediante metodos con el tama�o como parametro
	private TextoGrafico tg;
	// La lista de ips que participan en la captura
	private List<String> listaIps;
	// Lista de arreglos de Strings, el primer elemento de cada arreglo es la
	// mac y el segundo la ip asociada a esa mac
	private List<String[]> macs;

	private List<String[]> ipNombre;

	private List<String[]> macNombre;
	private String[] nombres;

	void cargarListaMacNombre() {
		macNombre = new ArrayList<String[]>();
		nombres = new String[] { "HOST", "ROUTER C", "PROXY", "ROUTER B", "ROUTER A", "SERVER HTTP", "SERVER IMG1", "SERVER IMG2" };
		String[] misMacs = { "Giga-Byt_67:42:3d", "Giga-Byt_68:a0:74", "EdimaxTe_c6:b3:41", "Giga-Byt_6e:73:62", "Giga-Byt_69:65:54",
				"Netronix_aa:35:6f", "Giga-Byt_63:54:91", "EdimaxTe_c6:b5:18", "Giga-Byt_68:68:9c", "Giga-Byt_8d:52:d4", "Giga-Byt_5e:3b:c9" };
		int p = 0;
		for (int i = 0; i < nombres.length; i++) {
			String[] nombreMacAux = new String[2];
			if (i == 0 || i == 2 || i >= 5) {
				nombreMacAux[0] = nombres[i];
				nombreMacAux[1] = misMacs[p];
				macNombre.add(nombreMacAux);
				p++;
			} else {
				nombreMacAux[0] = nombres[i];
				nombreMacAux[1] = misMacs[p];
				macNombre.add(nombreMacAux);
				p++;
				nombreMacAux = new String[2];
				nombreMacAux[0] = nombres[i];
				nombreMacAux[1] = misMacs[p];
				macNombre.add(nombreMacAux);
				p++;
			}
		}
	}

	// Funcion llamada desde parsearCaptura para reemplazar las ips encontradas
	// en el archivo .csv por su rotulo en la lista ipNombres
	String reemplazarPosicion(String ip) {
		String ipAux = ip;
		for (int i = 0; i < ipNombre.size(); i++) {
			if (ipNombre.get(i)[1].equals(ip)) {
				ipAux = ipNombre.get(i)[0];
			}
		}
		ip = ipAux;
		return ip;
	}

	// Funcion que carga la lista ipNombres con los rotulos y sus arreglos de
	// ips asociados, sirve de indice
	void cargarListaIpNombre(String nombre, String[] ips) {
		String[] ipAux;
		for (int i = 0; i < ips.length; i++) {
			ipAux = new String[2];
			ipAux[0] = nombre;
			ipAux[1] = ips[i];
			ipNombre.add(ipAux);
		}
	}

	// En esta funcion se cambian las ips por los correspondientes alias, creo
	// una lista con todos los nombres, luego creo arreglos para cada nombre con
	// las ips asociadas dicho nombre, despues lleno una lista auxiliar con
	// todas las ips juntas a la cual luego le agrego las ips que no tienen un
	// nombre asociado.
	// Finalmente intercambio listaIps por la lista auxiliar que contiene los
	// rotulos del proyecto integrador y las ips que no esten asociadas a ningun
	// rotulo y relleno la lista ipNombre con una funcion que rellena el indice
	// que asocia el rotulo al arreglo
	public void cambiarIpPorAlias() {
		List<String> listaIpsAux = new ArrayList<String>();
		List<String> listaconNombres = new ArrayList<String>();
		ipNombre = new ArrayList<String[]>();
		listaconNombres.add("ROUTER A");
		listaconNombres.add("ROUTER B");
		listaconNombres.add("ROUTER C");
		listaconNombres.add("PROXY");
		listaconNombres.add("HOST");
		listaconNombres.add("SERVER HTTP");
		listaconNombres.add("SERVER IMG 1");
		listaconNombres.add("SERVER IMG 2");
		listaconNombres.add("DNS");
		String[] ipsRouterA = new String[] { "200.40.0.100", "144.22.3.1" };
		String[] ipsRouterB = new String[] { "220.210.12.100", "144.22.3.2" };
		String[] ipsRouterC = new String[] { "220.210.12.101", "10.10.0.100" };
		String[] ipsProxy = new String[] { "220.210.12.10" };
		String[] ipHost = new String[] { "10.10.0.11" };
		String[] ipServerHTTP = new String[] { "200.40.0.4" };
		String[] ipServerIMG1 = new String[] { "200.40.0.5" };
		String[] ipServerIMG2 = new String[] { "200.40.0.6" };
		String[] ipDNS = new String[] { "200.40.0.2" };
		listaIpsAux.addAll(Arrays.asList(ipsRouterA));
		listaIpsAux.addAll(Arrays.asList(ipsRouterB));
		listaIpsAux.addAll(Arrays.asList(ipsRouterC));
		listaIpsAux.addAll(Arrays.asList(ipsProxy));
		listaIpsAux.addAll(Arrays.asList(ipHost));
		listaIpsAux.addAll(Arrays.asList(ipServerHTTP));
		listaIpsAux.addAll(Arrays.asList(ipServerIMG1));
		listaIpsAux.addAll(Arrays.asList(ipServerIMG2));
		listaIpsAux.addAll(Arrays.asList(ipDNS));
		for (int i = 0; i < listaIps.size(); i++) {
			if (!listaIpsAux.contains(listaIps.get(i))) {
				listaconNombres.add(listaIps.get(i));
			}
		}
		listaIps = listaconNombres;
		cargarListaIpNombre("ROUTER A", ipsRouterA);
		cargarListaIpNombre("ROUTER B", ipsRouterB);
		cargarListaIpNombre("ROUTER C", ipsRouterC);
		cargarListaIpNombre("PROXY", ipsProxy);
		cargarListaIpNombre("HOST", ipHost);
		cargarListaIpNombre("SERVER HTTP", ipServerHTTP);
		cargarListaIpNombre("SERVER IMG 1", ipServerIMG1);
		cargarListaIpNombre("SERVER IMG 2", ipServerIMG2);
		cargarListaIpNombre("DNS", ipDNS);
	}

	// Funcion que inicializa los valores de las variables de la clase, crea
	// todos los graficos metiante la clase TextoGrafico y a partir de los
	// valores de las variables tn (Tama�o de cabecera) y resta (Espacio a
	// restar entre cabeceras) determina el tama�o proporcional del grafico
	// Tambien inicializa las listas mac y listaIps
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
		cargarListaMacNombre();
	}

	// Esta funcion valida los valores de tama�o y resta ingresados como
	// parametros del constructor, el tama�o no puede ser menor al tama�o minimo
	// posible y la resta no puede ser mayor a la resta maxima posible
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

	// Constructor que recibe solo la direccion del archivo como parametro,
	// establece el tama�o por defecto de cabecera, determina que la resta de
	// espacio entre cabeceras es 0 (No se resta espacio), establece que la
	// ejecucion no es local (Ya que se recibieron parametros, lo que significa
	// que se ejecuto por consola, probablemente desde el servidor web) y se
	// inicializan las variables con la funcion correspondiente
	public GeneradorNombresPorMac(String archivo) {
		tn = TAM_DEF_CAB;
		restar = 0;
		local = false;
		nombreCaptura = archivo;
		inicializarVariables();
	}

	// Este constructor ademas recibe como parametros el tama�o de cabecera y la
	// resta a realizar en el espacio entre cabeceras, por lo tanto usa la
	// funcion de validacion cambiarProporcion para establecerlos
	public GeneradorNombresPorMac(String archivo, int tam, int rest) {
		cambiarProporcion(tam, rest);
		local = false;
		nombreCaptura = archivo;
		inicializarVariables();
	}

	// Este constructor no recibe nada, por lo tanto asume ejecucion local (Que
	// no se uso consola, posiblemente se hizo doble click sobre el archivo
	// .jar)
	public GeneradorNombresPorMac() {
		tn = TAM_DEF_CAB;
		restar = 0;
		local = true;
		nombreCaptura = NOM_CAP_DEF;
		inicializarVariables();
	}

	// Este constructor no se usa actualmente, seria para el caso de una
	// ejecucion local usando la consola para agregar los parametros de tama�o y
	// resta
	public GeneradorNombresPorMac(int tam, int rest) {
		cambiarProporcion(tam, rest);
		tn = tam;
		restar = rest;
		local = true;
		nombreCaptura = NOM_CAP_DEF;
		inicializarVariables();
	}

	// Inicializo el escritor para guardar el archivo html, le asigno la fecha y
	// hora actual en el nombre
	public void iniciarPrintWriter() {
		if (local) {
			DateFormat formato = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy");
			Date fecha = new Date();
			try {
				escribir = new PrintWriter("Diagrama (" + formato.format(fecha) + ").html");
			} catch (FileNotFoundException e) {
				if (local) {
					JOptionPane.showMessageDialog(null, "No se pudo crear el archivo del diagrama", "No se puede crear",
							JOptionPane.ERROR_MESSAGE);
				}
				System.exit(0);
			}
		}
	}

	// Esta funcion sirve que la escritura se haga en un archivo de texto o por
	// salida estandar dependiendo de si la ejecucion es local o no.
	// Cada vez que la aplicacion necesita escribir algo con salto de linea,
	// llama a esta funcion
	public void imprimirln(String texto) {
		if (local) {
			escribir.println(texto);
		} else {
			System.out.println(texto);
		}
	}

	// Igual a la anterior pero sin salto de linea
	public void imprimir(String texto) {
		if (local) {
			escribir.print(texto);
		} else {
			System.out.print(texto);
		}
	}

	// Esta funcion solo escribe las etiquetas html iniciales (La etiqueta <pre>
	// me asegura que el texto no va a ser modificado, ya que html lo asume como
	// preformateado
	public void generarCabecerahtml() {
		imprimirln("<html>");
		imprimirln("<title>Diagrama de Secuencia</title>");
		imprimirln("<body>");
		imprimirln("<pre>");
	}

	// Esta funcion escribe las etiquetas html finales
	public void generarCierrehtml() {
		imprimirln("</pre>");
		imprimirln("</body>");
		imprimirln("</html>");
	}

	// Cierra la clase que escribe el archivo si la ejecucion es local
	public void cerrarPrintWriter() {
		if (local) {
			JOptionPane.showMessageDialog(null, "Diagrama de secuencia creado con �xito en el directorio:\n" + System.getProperty("user.dir"),
					"Diagrama creado", JOptionPane.INFORMATION_MESSAGE);
			escribir.close();
		}
	}

	// Genera un salto de linea
	public void nLinea() {
		imprimirln("");
	}

	// Creo el lector de .csv con la libreria, luego leo todas las lineas y las
	// almaceno en la lista listaLineas, despues linea a linea tomo el campo
	// origen y destino y si no fueron almacenados previamente en la lista
	// auxiliar destinos, los almaceno en ella
	// Como caso aparte, si el protocolo es ARP uso el arreglo auxiliar ipMac
	// para diseccionar los mensajes y obtener la ip asociada a la mac, almaceno
	// mac e ip en el arreglo y luego lo inserto en la lista macs
	// Despues cierro el lector y paso las ips de la lista auxiliar destino a
	// listaIps y las ips de la lista mac tambien a listaIps
	public void obtenerIpsCSV() {
		CSVReader lector = null;
		listaLineas = null;
		String[] lineaActual;
		try {
			lector = new CSVReader(new FileReader(nombreCaptura));
		} catch (FileNotFoundException e1) {
			if (local) {
				JOptionPane.showMessageDialog(null,
						"No se encontro el archivo captura.csv en el directorio:\n" + System.getProperty("user.dir"), "No encontrado",
						JOptionPane.WARNING_MESSAGE);
			}
			System.exit(0);
		}
		try {
			listaLineas = lector.readAll();
		} catch (IOException e) {
			if (local) {
				JOptionPane.showMessageDialog(null, "No se pudo leer el archivo captura.csv (Esta corrupto)", "Error en archivo",
						JOptionPane.ERROR_MESSAGE);
			}
			System.exit(0);
		}
		List<String> destinos = new ArrayList<String>();
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
			ipMac = macs.get(i);
			if (!listaIps.contains(ipMac[1])) {
				listaIps.add(ipMac[1]);
			}
		}
	}

	// Genera las cabeceras imprimiendo primero las tapas usando la cantidad de
	// ips en listaIps, despues imprime cada ip usando el padding para que haya
	// la separacion exacta, ademas si el tama�o de la ip es mayor al maximo
	// permitido, se le resta el excedente y se le agregan puntos suspensivos
	// Siempre el ultimo titulo que se agrega es el de Broadcast, el cual no
	// esta almacenado en listaIps, por lo tanto se inserta manualmente
	// Luego se imprimen las tapas inferiores y se hace salto de linea
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
			imprimir(paddingCabecera + "| " + titulo + paddingTitulos.substring(tamTitulo + 1) + "|" + paddingCabecera);
		}
		imprimirln(paddingCabecera + "| " + BROADCAST + paddingTitulos.substring(BROADCAST.length() + 1) + "|" + paddingCabecera);
		for (int i = 0; i <= listaIps.size(); i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
	}
	
	public void generarCabecerasMac() {
		int tamTitulo;
		String titulo;
		for (int i = 0; i <= nombres.length; i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
		for (int i = 0; i < nombres.length; i++) {
			titulo = (String) nombres[i];
			tamTitulo = titulo.length();
			if (tamTitulo > longTitulo) {
				titulo = titulo.substring(0, longTitulo - PUNTOS.length());
				titulo = titulo + PUNTOS;
				tamTitulo = titulo.length();
			}
			imprimir(paddingCabecera + "| " + titulo + paddingTitulos.substring(tamTitulo + 1) + "|" + paddingCabecera);
		}
		imprimirln(paddingCabecera + "| " + BROADCAST + paddingTitulos.substring(BROADCAST.length() + 1) + "|" + paddingCabecera);
		for (int i = 0; i <= nombres.length; i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
	}

	// Imprime las lineas punteadas sin flechas ni mensajes
	public void lineaPunteada() {
		for (int i = 0; i <= nombres.length; i++) {
			imprimir(paddingLineas + "|" + paddingLineas);
		}
		nLinea();
	}

	// Determina si la flecha es derecha o izquierda verificando si el origen es
	// menor o mayor al destino, luego imprime lineas hasta llegar al punto
	// extremo izquierdo de la flecha, al llegar en caso de que sea flecha
	// izquierda, imprime la flecha, en caso de que sea flecha derecha y la
	// longitud sea 1, imprime la derecha, si la longitud es mayor, solo imprime
	// el cuerpo de la flecha.
	// Luego imprime el cuerpo de la flecha hasta alcanzar el punto extremo
	// derecho, alli determina si cierra con flecha derecha o con cuerpo
	// Luego sigue imprimiendo lineas hasta alcanzar el tama�o de listaIps
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
		while (posFinal < nombres.length) {
			imprimir(paddingLineas + paddingLineas + "|");
			posFinal++;
		}
		nLinea();
	}

	// Imprime lineas hasta llegar a la posicion de la ip mas a la izquierda que
	// participa en la transmision del mensaje, luego imprime el mensaje y
	// finalmente sigue imprimiento lineas hasta llegar al final
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
		int tamTotal = longInicial + (longEspacios + 1) * nombres.length;
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

	// Se lee linea a linea la captura de la lista listaLineas, si el protocolo
	// es ARP, se busca en la lista macs cual es la ip correspondiente
	// Se crea el mensaje con los campos numero, protocolo y mensaje
	// Se reemplaza la ip por su rotulo con la funcion reemplazarPosicion
	// Se establece la posicion origen y destino mediante el indice de listaIps
	// Con estos campos se llama a agregarMensaje y formarFlecha y luego se
	// agregan las lineas punteadas
	// Cada 5 iteraciones de mensajes se agregan las cabeceras nuevamente
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
			origen = reemplazarPosicion(origen);
			destino = reemplazarPosicion(destino);
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
	
	String obtenerNombreMac(String mac) {
		String nombre = null;
		for (int i = 0; i < macNombre.size(); i++) {
			if (mac.equals(macNombre.get(i)[1])) {
				nombre = macNombre.get(i)[0];
			}
		}
		return nombre;
	}
	
	public void parsearCapturaMacs() {
		String[] datos = new String[5];
		String numero;
		String protocolo;
		String origen;
		String destino;
		String mensaje;
		int posOrigen;
		int posDestino;
		int agregarCabeceras = 0;
		for (int i = 1; i < listaLineas.size(); i++) {
			datos = (String[]) listaLineas.get(i);
			numero = datos[0];
			protocolo = datos[6];
			origen = datos[2];
			destino = datos[3];
			mensaje = datos[8];
			mensaje = numero + "  " + protocolo + "   " + mensaje;
			origen = obtenerNombreMac(origen);
			posOrigen = Arrays.asList(nombres).indexOf(origen);
			if (!destino.equals("Broadcast")) {
				destino = obtenerNombreMac(destino);
				posDestino = Arrays.asList(nombres).indexOf(destino);
			} else {
				posDestino = nombres.length;
			}
			agregarMensaje(mensaje, posOrigen, posDestino);
			formarFlecha(posOrigen, posDestino);
			lineaPunteada();
			lineaPunteada();
			agregarCabeceras++;
			if (agregarCabeceras == 5) {
				generarCabecerasMac();
				agregarCabeceras = 0;
				lineaPunteada();
			}
		}
	}
}
