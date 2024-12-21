package interfaces;

import java.util.Comparator;

import orm.Datosjugadorpartido;

public class ComparadorDatosJugadorPorPuntos implements Comparator<Datosjugadorpartido> {

	@Override
	public int compare(Datosjugadorpartido djp1, Datosjugadorpartido djp2) {
		Integer puntos1 = djp1.getPuntos();
		Integer puntos2 = djp2.getPuntos();
		return puntos2.compareTo(puntos1);
	}

}
