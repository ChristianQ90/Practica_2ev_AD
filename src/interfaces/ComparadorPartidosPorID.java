package interfaces;

import java.util.Comparator;

import orm.Partido;

public class ComparadorPartidosPorID implements Comparator<Partido> {

	@Override
	public int compare(Partido p1, Partido p2) {
		Integer idP1 = p1.getIdPartido();
		Integer idP2 = p2.getIdPartido();
		return idP1.compareTo(idP2);
	}

}
