package generadores;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import graficos.TextoGrafico;

import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVReader;

public class GeneradorAdyacentes {

	// Defino las variables estaticas
	// Tamaño por defecto de las cabeceras
	public final static int TAM_DEF_CAB = 19;
	// Tamaño minimo que pueden tener las cabeceras
	public final static int MIN_TAM_CAB = 13;
	// Maximo tamaño que se le puede restar a la distancia entre cabeceras
	public final static int MAX_TAM_RESTA = 9;
	// Rotulo de broadcast
	public final static String BROADCAST = "Broadcast";
	// Rotulo de ARP
	public final static String ARP = "ARP";
	// Rotulo de puntos suspensivos (Se añaden al rotulo cuando no entra en la
	// cabecera)
	public final static String PUNTOS = "...";
	// Nombre por defecto de la captura
	public final static String NOM_CAP_DEF = "./captura.csv";

	// Defino las variables de la clase
	// El tamaño que van a tener las cabeceras
	private int tn;
	// El valor que se restara a la distancia entre cabeceras
	private int restar;
	// El tamaño maximo del titulo de cada cabecera
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
	// dinamica mediante metodos con el tamaño como parametro
	private TextoGrafico tg;
	// La lista de ips que participan en la captura
	private List<String> listaIps;
	// Lista de arreglos de Strings, el primer elemento de cada arreglo es la
	// mac y el segundo la ip asociada a esa mac
	private List<String[]> macs;

	// Funcion que inicializa los valores de las variables de la clase, crea
	// todos los graficos metiante la clase TextoGrafico y a partir de los
	// valores de las variables tn (Tamaño de cabecera) y resta (Espacio a
	// restar entre cabeceras) determina el tamaño proporcional del grafico
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
	}

	// Esta funcion valida los valores de tamaño y resta ingresados como
	// parametros del constructor, el tamaño no puede ser menor al tamaño minimo
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
	// establece el tamaño por defecto de cabecera, determina que la resta de
	// espacio entre cabeceras es 0 (No se resta espacio), establece que la
	// ejecucion no es local (Ya que se recibieron parametros, lo que significa
	// que se ejecuto por consola, probablemente desde el servidor web) y se
	// inicializan las variables con la funcion correspondiente
	public GeneradorAdyacentes(String archivo) {
		tn = TAM_DEF_CAB;
		restar = 0;
		local = false;
		nombreCaptura = archivo;
		inicializarVariables();
	}

	// Este constructor ademas recibe como parametros el tamaño de cabecera y la
	// resta a realizar en el espacio entre cabeceras, por lo tanto usa la
	// funcion de validacion cambiarProporcion para establecerlos
	public GeneradorAdyacentes(String archivo, int tam, int rest) {
		cambiarProporcion(tam, rest);
		local = false;
		nombreCaptura = archivo;
		inicializarVariables();
	}

	// Este constructor no recibe nada, por lo tanto asume ejecucion local (Que
	// no se uso consola, posiblemente se hizo doble click sobre el archivo
	// .jar)
	public GeneradorAdyacentes() {
		tn = TAM_DEF_CAB;
		restar = 0;
		local = true;
		nombreCaptura = NOM_CAP_DEF;
		inicializarVariables();
	}

	// Este constructor no se usa actualmente, seria para el caso de una
	// ejecucion local usando la consola para agregar los parametros de tamaño y
	// resta
	public GeneradorAdyacentes(int tam, int rest) {
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
			JOptionPane.showMessageDialog(null, "Diagrama de secuencia creado con éxito en el directorio:\n" + System.getProperty("user.dir"),
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

	// Solo genera 2 cabeceras, las cuales le son pasadas como parametros en un
	// arreglo de Strings
	public void generarCabecerasAdyacentes(String[] titulos) {
		int tamTitulo;
		String titulo;
		for (int i = 0; i <= 1; i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
		for (int i = 0; i <= 1; i++) {
			titulo = titulos[i];
			tamTitulo = titulo.length();
			if (tamTitulo > longTitulo) {
				titulo = titulo.substring(0, longTitulo - PUNTOS.length());
				titulo = titulo + PUNTOS;
				tamTitulo = titulo.length();
			}
			imprimir(paddingCabecera + "| " + titulo + paddingTitulos.substring(tamTitulo + 1) + "|" + paddingCabecera);
		}
		nLinea();
		for (int i = 0; i <= 1; i++) {
			imprimir(paddingCabecera + tapa + paddingCabecera);
		}
		nLinea();
	}

	// [FUNCION NO USADA EN ESTA VERSION]
	// Genera las cabeceras imprimiendo primero las tapas usando la cantidad de
	// ips en listaIps, despues imprime cada ip usando el padding para que haya
	// la separacion exacta, ademas si el tamaño de la ip es mayor al maximo
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

	// [MODIFICADA PARA IMPRIMIR SOLO 2 LINEAS]
	// Imprime las lineas punteadas sin flechas ni mensajes
	public void lineaPunteada() {
		for (int i = 0; i <= 1; i++) {
			imprimir(paddingLineas + "|" + paddingLineas);
		}
		nLinea();
	}

	// [MODIFICADA PARA IMPRIMIR SOLO UNA LINEA LUEGO DE LA FLECHA]
	// Determina si la flecha es derecha o izquierda verificando si el origen es
	// menor o mayor al destino, luego imprime lineas hasta llegar al punto
	// extremo izquierdo de la flecha, al llegar en caso de que sea flecha
	// izquierda, imprime la flecha, en caso de que sea flecha derecha y la
	// longitud sea 1, imprime la derecha, si la longitud es mayor, solo imprime
	// el cuerpo de la flecha.
	// Luego imprime el cuerpo de la flecha hasta alcanzar el punto extremo
	// derecho, alli determina si cierra con flecha derecha o con cuerpo
	// Luego sigue imprimiendo lineas hasta alcanzar el tamaño de listaIps
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
		while (posFinal < 1) {
			imprimir(paddingLineas + paddingLineas + "|");
			posFinal++;
		}
		nLinea();
	}

	// [MODIFICADA PARA IMPRIMIR SOLO 2 LINEAS]
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
		int tamTotal = longInicial + (longEspacios + 1) * 1;
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

	// [MODIFICACION: Almacena el destino y origen anterior para solo volver a
	// imprimir la cabeceras cuando alguno de los dos ha cambiado, ademas usa la
	// funcion generarcabecerasAdyacentes]
	// Se lee linea a linea la captura de la lista listaLineas, si el protocolo
	// es ARP, se busca en la lista macs cual es la ip correspondiente
	// Se crea el mensaje con los campos numero, protocolo y mensaje
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
		int posOrigen = 0;
		int posDestino = 1;
		int posListaMacs = 0;
		String[] titulos;
		String origenAnterior = "";
		String destinoAnterior = "";
		boolean intercambiados = false;
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
			if (origenAnterior.equals(destino) && destinoAnterior.equals(origen)) {
				int aux = posOrigen;
				posOrigen = posDestino;
				posDestino = aux;
				intercambiados = true;
			} else if (!origenAnterior.equals(origen) || !destinoAnterior.equals(destino)) {
				posOrigen = 0;
				posDestino = 1;
			}
			titulos = new String[] { origen, destino };
			if ((!origenAnterior.equals(origen) || !destinoAnterior.equals(destino)) && !intercambiados) {
				generarCabecerasAdyacentes(titulos);
			}
			origenAnterior = origen;
			destinoAnterior = destino;
			intercambiados = false;
			lineaPunteada();
			agregarMensaje(mensaje, posOrigen, posDestino);
			formarFlecha(posOrigen, posDestino);
			lineaPunteada();
		}
	}
}
