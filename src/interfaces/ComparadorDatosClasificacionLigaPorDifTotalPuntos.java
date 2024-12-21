package interfaces;

import java.util.Comparator;

public class ComparadorDatosClasificacionLigaPorDifTotalPuntos implements Comparator <DatosClasificacionLiga>{

	@Override
	public int compare(DatosClasificacionLiga dcl1, DatosClasificacionLiga dcl2) {
		Integer dPT1 = dcl1.getDiferenciaTotalPuntos();
		Integer dPT2 = dcl2.getDiferenciaTotalPuntos();
		return dPT2.compareTo(dPT1);
	}

}
