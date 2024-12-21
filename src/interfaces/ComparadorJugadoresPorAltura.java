package interfaces;

import java.util.Comparator;

import orm.Jugador;

public class ComparadorJugadoresPorAltura implements Comparator<Jugador> {

	@Override
	public int compare(Jugador j1, Jugador j2) {
		
		return j2.getAlturaCm().compareTo(j1.getAlturaCm()); 
	}

}
