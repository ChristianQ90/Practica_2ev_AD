package interfaces;

import java.util.Comparator;

import orm.Datosjugadorpartido;

public class ComparadorDatosJugadorPorAsistencias implements Comparator<Datosjugadorpartido> {

	@Override
	public int compare(Datosjugadorpartido djp1, Datosjugadorpartido djp2) {
		Integer asistencias1=djp1.getAsistencias();
		Integer asistencias2=djp2.getAsistencias();
		return asistencias2.compareTo(asistencias1);
	}

}
