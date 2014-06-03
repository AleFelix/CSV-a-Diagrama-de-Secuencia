package generador;

public class TextoGrafico {

	public String crearTapa(int n) {
		String t = "+";
		for (int i = 0; i < (n - 2); i++) {
			t = t + "-";
		}
		t = t + "+";
		return t;
	}
	
	public String crearPadding(int n) {
		String p = new String();
		for (int i = 0; i < n; i++) {
			p = p + " ";
		}
		return p;
	}
	
	public String crearFlecha(int n) {
		String p = new String();
		for (int i = 0; i < n; i++) {
			p = p + "-";
		}
		return p;
	}
	
	public String crearFlecha(int n, boolean derecha) {
		String p = new String();
		if (!derecha) {
			p = p + "<";
		}
		for (int i = 0; i < (n - 1); i++) {
			p = p + "-";
		}
		if (derecha) {
			p = p + ">";
		}
		return p;
	}
	
}
