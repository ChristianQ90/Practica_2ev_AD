package interfaces;

import java.util.Comparator;

import orm.Datosjugadorpartido;

public class ComparadorDatosJugadorPorValoracion implements Comparator<Datosjugadorpartido> {

	@Override
	public int compare(Datosjugadorpartido djp1, Datosjugadorpartido djp2) {
		Double valoracion1 = djp1.getValoracion();
		Double valoracion2 = djp2.getValoracion();
		return valoracion2.compareTo(valoracion1);
	}

}
