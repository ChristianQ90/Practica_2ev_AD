package interfaces;

import java.util.Comparator;

import orm.Datosjugadorpartido;

public class ComparadorDatosJugadorPorRebotes implements Comparator<Datosjugadorpartido>{

	@Override
	public int compare(Datosjugadorpartido djp1, Datosjugadorpartido djp2) {
		Integer rebotes1 = djp1.getRebotes();
		Integer rebotes2 = djp2.getRebotes();
		return rebotes2.compareTo(rebotes1);
	}

}
