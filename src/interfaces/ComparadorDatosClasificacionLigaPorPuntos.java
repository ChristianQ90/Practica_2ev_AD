package interfaces;

import java.util.Comparator;

public class ComparadorDatosClasificacionLigaPorPuntos implements Comparator<DatosClasificacionLiga>{

	@Override
	public int compare(DatosClasificacionLiga dcl1, DatosClasificacionLiga dcl2) {
		Integer puntos1 = dcl1.getPuntos();
		Integer puntos2 = dcl2.getPuntos();
		return puntos2.compareTo(puntos1);
	}

}
