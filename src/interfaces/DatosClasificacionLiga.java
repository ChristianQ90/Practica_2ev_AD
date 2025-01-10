package interfaces;

import orm.Equipo;

public class DatosClasificacionLiga {

	private Equipo equipo;
	private int puntos;
	
	private int partidosGanados;
	private int partidosPerdidos;
	private int puntosFavor;
	private int puntosContra;
	private int diferenciaTotalPuntos;
	
	private int puntosFavorComoLocal;
	private int puntosContraComoLocal;
	private int diferenciaTotalPuntosComoLocal;
	
	private int puntosFavorComoVisitante;
	private int puntosContraComoVisitante;
	private int diferenciaTotalPuntosComoVisitante;
	
	public DatosClasificacionLiga(Equipo equipo, int puntos, int partidosGanados, int partidosPerdidos, int puntosFavor,
			int puntosContra, int diferenciaTotalPuntos, int puntosFavorComoLocal, int puntosContraComoLocal,
			int diferenciaTotalPuntosComoLocal, int puntosFavorComoVisitante, int puntosContraComoVisitante,
			int diferenciaTotalPuntosComoVisitante) {
		super();
		this.equipo = equipo;
		this.puntos = puntos;
		this.partidosGanados = partidosGanados;
		this.partidosPerdidos = partidosPerdidos;
		this.puntosFavor = puntosFavor;
		this.puntosContra = puntosContra;
		this.diferenciaTotalPuntos = diferenciaTotalPuntos;
		this.puntosFavorComoLocal = puntosFavorComoLocal;
		this.puntosContraComoLocal = puntosContraComoLocal;
		this.diferenciaTotalPuntosComoLocal = diferenciaTotalPuntosComoLocal;
		this.puntosFavorComoVisitante = puntosFavorComoVisitante;
		this.puntosContraComoVisitante = puntosContraComoVisitante;
		this.diferenciaTotalPuntosComoVisitante = diferenciaTotalPuntosComoVisitante;
	}
	@Override
	public String toString() {

	    String formato =" %-32s |     %-6s |   %-5s |  %-3s |   %-6s |   %-6s |   %-6s |   %-5s |    %-4s |   %-5s |   %-5s |    %-4s |    %-3s";
	        
	        return String.format(formato,equipo.getNombre()+" (ID: "+equipo.getIdEquipo()+")",
	            puntos,
	            partidosGanados,
	            partidosPerdidos,
	            puntosFavor,
	            puntosContra,
	            diferenciaTotalPuntos,
	            puntosFavorComoLocal,
	            puntosContraComoLocal,
	            diferenciaTotalPuntosComoLocal,
	            puntosFavorComoVisitante,
	            puntosContraComoVisitante,
	            diferenciaTotalPuntosComoVisitante);
	}
	public Equipo getEquipo() {
		return equipo;
	}
	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}
	public int getPuntos() {
		return puntos;
	}
	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}
	public int getPartidosGanados() {
		return partidosGanados;
	}
	public void setPartidosGanados(int partidosGanados) {
		this.partidosGanados = partidosGanados;
	}
	public int getPartidosPerdidos() {
		return partidosPerdidos;
	}
	public void setPartidosPerdidos(int partidosPerdidos) {
		this.partidosPerdidos = partidosPerdidos;
	}
	public int getPuntosFavor() {
		return puntosFavor;
	}
	public void setPuntosFavor(int puntosFavor) {
		this.puntosFavor = puntosFavor;
	}
	public int getPuntosContra() {
		return puntosContra;
	}
	public void setPuntosContra(int puntosContra) {
		this.puntosContra = puntosContra;
	}
	public int getDiferenciaTotalPuntos() {
		return diferenciaTotalPuntos;
	}
	public void setDiferenciaTotalPuntos(int diferenciaTotalPuntos) {
		this.diferenciaTotalPuntos = diferenciaTotalPuntos;
	}
	public int getPuntosFavorComoLocal() {
		return puntosFavorComoLocal;
	}
	public void setPuntosFavorComoLocal(int puntosFavorComoLocal) {
		this.puntosFavorComoLocal = puntosFavorComoLocal;
	}
	public int getPuntosContraComoLocal() {
		return puntosContraComoLocal;
	}
	public void setPuntosContraComoLocal(int puntosContraComoLocal) {
		this.puntosContraComoLocal = puntosContraComoLocal;
	}
	public int getDiferenciaTotalPuntosComoLocal() {
		return diferenciaTotalPuntosComoLocal;
	}
	public void setDiferenciaTotalPuntosComoLocal(int diferenciaTotalPuntosComoLocal) {
		this.diferenciaTotalPuntosComoLocal = diferenciaTotalPuntosComoLocal;
	}
	public int getPuntosFavorComoVisitante() {
		return puntosFavorComoVisitante;
	}
	public void setPuntosFavorComoVisitante(int puntosFavorComoVisitante) {
		this.puntosFavorComoVisitante = puntosFavorComoVisitante;
	}
	public int getPuntosContraComoVisitante() {
		return puntosContraComoVisitante;
	}
	public void setPuntosContraComoVisitante(int puntosContraComoVisitante) {
		this.puntosContraComoVisitante = puntosContraComoVisitante;
	}
	public int getDiferenciaTotalPuntosComoVisitante() {
		return diferenciaTotalPuntosComoVisitante;
	}
	public void setDiferenciaTotalPuntosComoVisitante(int diferenciaTotalPuntosComoVisitante) {
		this.diferenciaTotalPuntosComoVisitante = diferenciaTotalPuntosComoVisitante;
	}

	/*
Las siguientes columnas: equipo, puntos, PG, PP, pF, pC, dPT, pFL, pCL, dPTL, pFV, pCV, dPTV.

PG: Partidos Ganados = La cantidad de partidos ganados por el equipo en total (local y visitante).
PP: Partidos Perdidos = La cantidad de partidos perdidos por el equipo en total (local y visitante).
pF: Puntos a Favor = La sumatoria de los puntos anotados por el equipo en todos los partidos jugados.
pC: Puntos en Contra = La sumatoria de los puntos recibidos por el equipo en todos los partidos jugados.
dPT: Diferencia Total de Puntos = La diferencia entre los puntos a favor y los puntos en contra:

dPT = pF−pC

pFL: Puntos a Favor como Local = La sumatoria de los puntos anotados por el equipo en partidos jugados como local.
pCL: Puntos en Contra como Local = La sumatoria de los puntos recibidos por el equipo en partidos jugados como local.
dPTL: Diferencia de Puntos como Local = La diferencia de puntos en partidos jugados como local:

dPTL = pFL−pCL

pFV: Puntos a Favor como Visitante = La sumatoria de los puntos anotados por el equipo en partidos jugados como visitante.
pCV: Puntos en Contra como Visitante = La sumatoria de los puntos recibidos por el equipo en partidos jugados como visitante.
dPTV: Diferencia de Puntos como Visitante = La diferencia de puntos en partidos jugados como visitante:

dPTV = pFV−pCV

	 */
}
