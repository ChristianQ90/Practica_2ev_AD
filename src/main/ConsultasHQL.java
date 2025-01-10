package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import conexion.HibernateUtil;
import orm.Datosjugadorpartido;
import orm.DatosjugadorpartidoId;
import orm.Equipo;
import orm.Jugador;
import orm.Partido;
import interfaces.*;

public class ConsultasHQL {

	public ConsultasHQL() {
		super();
	}
	/**
	 * Imprime en consola las estadísticas detalladas de un partido específico, identificándolo mediante su ID.
	 * <p>
	 * Este método realiza una consulta a la base de datos utilizando Hibernate para obtener los detalles de un partido,
	 * incluyendo las estadísticas de los jugadores de ambos equipos (local y visitante).
	 * Los datos se muestran tabulados, proporcionando una visión clara y organizada de la información.
	 * </p>
	 * 
	 * <h3>Formato de salida</h3>
	 * <p>
	 * Para cada equipo (local y visitante), se imprime una tabla con las siguientes columnas:
	 * </p>
	 * <ul>
	 *   <li>ID del jugador</li>
	 *   <li>ID del partido</li>
	 *   <li>Valoración</li>
	 *   <li>Puntos</li>
	 *   <li>Asistencias</li>
	 *   <li>Rebotes</li>
	 *   <li>Tapones</li>
	 *   <li>Condición de titularidad</li>
	 * </ul>
	 * 
	 * <p>
	 * Este método abre una sesión de Hibernate, la utiliza para realizar las consultas, y finalmente la cierra,
	 * asegurando el uso adecuado de los recursos.
	 * En caso de error, se realiza un rollback de la transacción para preservar la integridad de los datos.
	 * </p>
	 * 
	 * @param id ID del partido a consultar. Debe ser un número entero válido.
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a un objeto nulo durante el procesamiento.
	 * @throws IllegalArgumentException Si se pasa un argumento inválido en la consulta.
	 * @throws Exception Para otros errores inesperados.
	 * @see Partido
	 * @see Datosjugadorpartido
	 * @since 2024
	 */

	public void imprimirEstadisticasDeUnPartido (int id) {
		
		Session s = null;
		Transaction trans = null;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Partido where idPartido = ?0").setReadOnly(true);
			q.setParameter(0, id);
			
			List<Partido>partidoSeleccionado = q.getResultList();
			Set <Jugador> jugadoresLocales = new LinkedHashSet<Jugador>();
			Set <Jugador> jugadoresVisitantes = new LinkedHashSet<Jugador>();
			DatosjugadorpartidoId datosClaveCompuesta;
			Integer idJ , idP,puntos,asistencias, rebotes,tapones;
			Double valoracion;
			boolean titular;
			
			if(partidoSeleccionado.size()>0) {
				for(Partido partido : partidoSeleccionado) {
					
					System.out.println("Partido con ID: "+partido.getIdPartido()+" ( "+partido.getEquipoByIdEquipoLocal().getNombre()+" "+partido.getPuntosLocal()+" - "+partido.getPuntosVisitante()+" "+partido.getEquipoByIdEquipoVisitante().getNombre()+" ).\n");
					
					Equipo equipoLocal = partido.getEquipoByIdEquipoLocal();
					Equipo equipoVisitante = partido.getEquipoByIdEquipoVisitante();
					jugadoresLocales= equipoLocal.getJugadors();
					jugadoresVisitantes= equipoVisitante.getJugadors();
					
					System.out.println("  Jugadores del equipo local: "+partido.getEquipoByIdEquipoLocal().getNombre());
					System.out.println("|  idJ  |  idP  |  valoración  | puntos |  asistencias  |  rebotes  |  tapones  | titular  |");
					System.out.println("--------------------------------------------------------------------------------------------");
					for (Jugador jugador : jugadoresLocales) {
						datosClaveCompuesta = new DatosjugadorpartidoId(jugador.getIdJugador(),partido.getIdPartido());
						//Datosjugadorpartido datosJugadorEnPartido  = new Datosjugadorpartido(datosClaveCompuesta,jugador,partido);
						Datosjugadorpartido datosJugadorEnPartido  = s.get(Datosjugadorpartido.class, datosClaveCompuesta);
						idJ = jugador.getIdJugador();
						idP = partido.getIdPartido();
						valoracion = datosJugadorEnPartido.getValoracion();
						puntos = datosJugadorEnPartido.getPuntos();
						asistencias = datosJugadorEnPartido.getAsistencias();
						rebotes=datosJugadorEnPartido.getRebotes();
						tapones = datosJugadorEnPartido.getTapones();
						titular= datosJugadorEnPartido.getTitular();
						//System.out.println("|  "+idJ+"   |  "+idP+"   |     "+valoracion+"     |   "+puntos+"    |       "+asistencias+"       |     "+rebotes+"     |      "+tapones+"     |    "+titular+"   |");
					    System.out.println(String.format(
					            "|  %-4s |  %-4s |     %-8s |   %-4s |      %-8s |     %-5s |     %-5s |  %-6s  |",
					            idJ, idP, valoracion, puntos, asistencias, rebotes, tapones, titular
					        ));
					}
				
					System.out.println("\n  Jugadores del equipo visitante: "+partido.getEquipoByIdEquipoVisitante().getNombre());
					System.out.println("|  idJ  |  idP  |  valoración  | puntos |  asistencias  |  rebotes  |  tapones  | titular  |");
					System.out.println("--------------------------------------------------------------------------------------------");
					for (Jugador jugador : jugadoresVisitantes) {
						datosClaveCompuesta = new DatosjugadorpartidoId(jugador.getIdJugador(),partido.getIdPartido());
						//Datosjugadorpartido datosJugadorEnPartido  = new Datosjugadorpartido(datosClaveCompuesta,jugador,partido);
						Datosjugadorpartido datosJugadorEnPartido  = s.get(Datosjugadorpartido.class, datosClaveCompuesta);
						idJ = jugador.getIdJugador();
						idP = partido.getIdPartido();
						valoracion = datosJugadorEnPartido.getValoracion();
						puntos = datosJugadorEnPartido.getPuntos();
						asistencias = datosJugadorEnPartido.getAsistencias();
						rebotes=datosJugadorEnPartido.getRebotes();
						tapones = datosJugadorEnPartido.getTapones();
						titular= datosJugadorEnPartido.getTitular();
						//System.out.println("|  "+idJ+"   |  "+idP+"   |     "+valoracion+"     |   "+puntos+"    |       "+asistencias+"       |     "+rebotes+"     |      "+tapones+"     |    "+titular+"   |");
					    System.out.println(String.format(
					            "|  %-4s |  %-4s |     %-8s |   %-4s |      %-8s |     %-5s |     %-5s |  %-6s  |",
					            idJ, idP, valoracion, puntos, asistencias, rebotes, tapones, titular
					        ));
					}
					System.out.println("");
				}
				
			}else {
				System.out.println("No hay resultados para el ID brindado.\n");
			}
			
			trans.commit();
		
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
	}
	
	/**
	 * Muestra por consola los datos de todos los jugadores pertenecientes a equipos de la localidad "Palencia" 
	 * junto con sus estadísticas promedio.
	 * <p>
	 * Este método realiza consultas a la base de datos para obtener todos los equipos registrados en la localidad
	 * "Palencia". Luego, para cada jugador de estos equipos, calcula las medias de las estadísticas (valoración, 
	 * puntos, asistencias, rebotes y tapones) basándose en sus registros históricos.
	 * </p>
	 * 
	 * <h3>Detalles del cálculo</h3>
	 * <ul>
	 *   <li>Para cada jugador, se obtienen todas las estadísticas disponibles desde la entidad "Datosjugadorpartido".</li>
	 *   <li>Las estadísticas promedio se calculan sumando todas las entradas y dividiéndolas por el número de registros encontrados.</li>
	 * </ul>
	 * 
	 * <p>
	 * Este método abre una sesión de Hibernate, la utiliza para realizar las consultas, y finalmente la cierra,
	 * asegurando el uso adecuado de los recursos.
	 * En caso de error, se realiza un rollback de la transacción para preservar la integridad de los datos.
	 * </p>
	 * 
	 * <h3>Restricciones</h3>
	 * <ul>
	 *   <li>Si no se encuentran equipos en la localidad "Palencia", el método imprime un mensaje indicando la ausencia de datos.</li>
	 * </ul>
	 * 
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a un objeto nulo.
	 * @throws IllegalArgumentException Si un argumento es inválido.
	 * @throws Exception Para otros errores no previstos.
	 * @see Equipo
	 * @see Jugador
	 * @see Datosjugadorpartido
	 * @since 2024
	 */

	public void mostrarJugadoresDePalenciaConMediasEstadisticas() {
		
		Session s = null; 
		Transaction trans = null;
		DecimalFormat formato =new DecimalFormat("0.00");
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Equipo where localidad = 'Palencia'").setReadOnly(true);
			List<Equipo> equiposDePalencia =q.getResultList();
			
			Query q2 = s.createQuery("from Datosjugadorpartido where idJ = ?0");
			
			
			if(equiposDePalencia.size()>0) {
				
				for (Equipo equipo : equiposDePalencia) {//Itera en equipos encontrados.
					
					Set<Jugador>setDeJugadores = equipo.getJugadors();
					for(Jugador jugador : setDeJugadores) { // Itera en jugadores de esos equipos.
						
						int contador = 0 ; //Reinicio variables de datos
						Double valoracionM = 0.0 ;
						Double puntosM = 0.0 ,asistenciasM = 0.0, rebotesM=0.0, taponesM = 0.0;
						
						q2.setParameter(0, jugador.getIdJugador());//Seteamos 
						List<Datosjugadorpartido> datosJugador = q2.getResultList();
						for(Datosjugadorpartido datos : datosJugador) { // Itera en datos de cada jugador.
							contador++;
							valoracionM+=datos.getValoracion();
							puntosM += datos.getPuntos();
							asistenciasM+=datos.getAsistencias();
							rebotesM+=datos.getRebotes();
							taponesM+=datos.getTapones();
						}
						valoracionM = valoracionM/contador;
						puntosM= puntosM/contador;
						asistenciasM = asistenciasM/contador;
						rebotesM = rebotesM/contador;
						taponesM = taponesM/contador;
						
						System.out.println("ID jugador: "+ jugador.getIdJugador()+", valoración: "+formato.format(valoracionM)+" , puntos: "+formato.format(puntosM)+" , asistencias: "+formato.format(asistenciasM)+" , rebotes: "+formato.format(rebotesM)+" , tapones: "+formato.format(taponesM));
						//select idJ,avg(valoracion) as valoracion,avg(puntos) as puntos, avg(asistencias) as asistencias, avg(rebotes) as rebotes, avg(tapones) as tapones from datosjugadorpartido where idJ=1 group by idJ;
					}
				}
			}else {
				System.out.println("Actualmente no existe ningún equipo de Palencia en la base de datos.");
			}

			trans.commit();
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
		
	}
	
	/**
	 * Muestra los tres jugadores con las mejores valoraciones acumuladas en todos los partidos jugados de un equipo,
	 * basado en su ID de equipo. El cálculo de las valoraciones se realiza sumando las estadísticas individuales de 
	 * cada jugador en cada partido en el que participó.
	 * 
	 * <h3>Proceso de cálculo</h3>
	 * <ul>
	 *   <li>El método obtiene el equipo utilizando su ID.</li>
	 *   <li>Itera sobre todos los jugadores del equipo y acumula sus estadísticas (valoración, puntos, asistencias, rebotes, tapones).</li>
	 *   <li>Las estadísticas se acumulan por cada jugador en todos los partidos.</li>
	 *   <li>Finalmente, los jugadores se ordenan por su valoración acumulada y se muestran los tres mejores.</li>
	 * </ul>
	 * 
	 * <h3>Ordenación</h3>
	 * La lista de jugadores se ordena en base a la valoración acumulada utilizando un comparador personalizado,
	 * y se seleccionan solo los tres jugadores con las mejores valoraciones.
	 * 
	 * <p>
	 * Este método abre una sesión de Hibernate, la utiliza para realizar las consultas, y finalmente la cierra,
	 * asegurando el uso adecuado de los recursos.
	 * En caso de error, se realiza un rollback de la transacción para preservar la integridad de los datos.
	 * </p>
	 * 
	 * <h3>Restricciones</h3>
	 * <ul>
	 *   <li>Si no se encuentra el equipo con el ID proporcionado, se imprime un mensaje de error.</li>
	 *   <li>Si el equipo no tiene jugadores o los datos de jugador están vacíos, no se mostrará ningún resultado.</li>
	 * </ul>
	 * 
	 * @param id El ID del equipo para el que se desean mostrar los tres jugadores con mejor valoración acumulada.
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a un objeto nulo.
	 * @throws IllegalArgumentException Si un argumento es inválido.
	 * @throws Exception Para otros errores no previstos.
	 * @see Equipo
	 * @see Jugador
	 * @see Datosjugadorpartido
	 * @since 2024
	 */

	public void mostrarTresMejorValoradosDeUnEquipo(int id) {
		
		Session s = null;
		Transaction trans = null;
		DecimalFormat formato =new DecimalFormat("0.00");
		List<Jugador> jugadoresConValoracionAcumulada = new ArrayList<Jugador>();
		List<Datosjugadorpartido> listaConDatosAcumulados = new ArrayList<Datosjugadorpartido>();
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Equipo where idEquipo = ?0").setReadOnly(true);
			q.setParameter(0, id);
			
			Query q2 = s.createQuery("from Datosjugadorpartido where idJ = ?0");
			
			List<Equipo> listaEquipos = q.getResultList();
			if(listaEquipos.size()>0) {
				for (Equipo equipo : listaEquipos) {//Itera en equipos encontrados, debería ser sólo uno.
					
					Set<Jugador>setDeJugadores = equipo.getJugadors();
					for(Jugador jugador : setDeJugadores) { // Itera en jugadores de esos equipos.
						
						int contador = 0 ; //Reinicio variables de datos
						Double valoracionAcumulada = 0.0 ;
						Integer puntosAcumulados = 0 ,asistenciasAcumuladas = 0, rebotesAcumulados=0, taponesAcumulados = 0;
						
						q2.setParameter(0, jugador.getIdJugador());//Seteamos 
						List<Datosjugadorpartido> datosJugador = q2.getResultList();
						for(Datosjugadorpartido datos : datosJugador) { // Itera en datos de cada jugador.
							contador++;
							valoracionAcumulada+=datos.getValoracion();
							puntosAcumulados += datos.getPuntos();
							asistenciasAcumuladas+=datos.getAsistencias();
							rebotesAcumulados+=datos.getRebotes();
							taponesAcumulados+=datos.getTapones();
						}
						//Construimos estadísticas con acumulación para cada jugador.
						DatosjugadorpartidoId datosId = new DatosjugadorpartidoId(jugador.getIdJugador(),0);
						Partido partido = new Partido(0);
						Datosjugadorpartido datosAcumulados = new Datosjugadorpartido(datosId,jugador,partido,valoracionAcumulada,puntosAcumulados,asistenciasAcumuladas,rebotesAcumulados,taponesAcumulados,true);
						listaConDatosAcumulados.add(datosAcumulados);
						
						//System.out.println("ID jugador: "+ jugador.getIdJugador()+", valoración: "+formato.format(valoracionAcumulada)+" , puntos: "+puntosAcumulados+" , asistencias: "+asistenciasAcumuladas+" , rebotes: "+rebotesAcumulados+" , tapones: "+taponesAcumulados);
						
					}
					int contador = 0;
					listaConDatosAcumulados.sort(new ComparadorDatosJugadorPorValoracion());// Ordenamos la lista por valoración. 
					System.out.println("\nPara el equipo "+equipo.getNombre()+" (ID: "+equipo.getIdEquipo()+").");
					System.out.println("Los jugadores con las tres mejores valoraciones acumuladas en todos los partidos jugados son: ");
					for (Datosjugadorpartido datos : listaConDatosAcumulados) {
						contador++;
						//System.out.println(datos.getId().getIdJ()+" "+datos.getId().getIdP()+" "+ datos.getValoracion()+" "+datos.getPuntos()+" "+datos.getAsistencias()+" "+datos.getRebotes()+" "+datos.getTapones()+" "+datos.getTitular());
						for(Jugador jugador: setDeJugadores) {
							if(datos.getId().getIdJ()==jugador.getIdJugador()) {
								System.out.println("Jugador ID: "+jugador.getIdJugador()+" , ID equipo: "+jugador.getEquipo().getIdEquipo()+" , valoración acumulada: "+formato.format(datos.getValoracion())+" , nombre: "+jugador.getNombre()+" , apellidos: "+jugador.getApellidos()+" , puntos acumulados: "+datos.getPuntos()+" ,asistencias acumuladas: "+datos.getAsistencias()+" , rebotes acumulados: "+datos.getRebotes()+" , tapones acumulados: "+datos.getTapones()+"." );
							}
						}
						if(contador ==3) { 
							break;
						}
					}
					System.out.println(" ");
				}
				
			}else {
				System.out.println("No existe actualmente un equipo con el ID brindado.");
			}
						
			trans.commit();
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
	}
	
	/**
	 * Muestra las estadísticas acumuladas y la media por partido de un jugador en sus roles como titular y suplente.
	 * Los datos incluyen puntos, asistencias, rebotes, tapones y valoración en los partidos en los que el jugador participó,
	 * distinguiendo entre partidos como titular y como suplente.
	 * 
	 * <h3>Proceso de cálculo</h3>
	 * <ul>
	 *   <li>Se busca el jugador en la base de datos utilizando su ID.</li>
	 *   <li>Se verifican las estadísticas del jugador en los partidos donde su equipo jugó como local y visitante.</li>
	 *   <li>Los datos se separan y acumulan en dos categorías: titular y suplente.</li>
	 *   <li>Se calcula la media de cada métrica para ambos roles.</li>
	 *   <li>Los resultados se muestran en consola, indicando estadísticas individuales y promedios para cada rol.</li>
	 * </ul>
	 * 
	 * <h3>Validaciones</h3>
	 * <ul>
	 *   <li>Si no existe un jugador con el ID proporcionado, se imprime un mensaje de error y se termina el método.</li>
	 *   <li>Si el jugador no está asociado a un equipo, también se informa en la consola.</li>
	 *   <li>Si no hay estadísticas disponibles para un rol, se muestra un mensaje indicando la ausencia de datos.</li>
	 * </ul>
	 * 
	 * <p>
	 * Este método abre una sesión de Hibernate, la utiliza para realizar las consultas, y finalmente la cierra,
	 * asegurando el uso adecuado de los recursos.
	 * En caso de error, se realiza un rollback de la transacción para preservar la integridad de los datos.
	 * </p>
	 * 
	 * <h3>Restricciones</h3>
	 * <ul>
	 *   <li>El ID proporcionado debe ser válido y corresponder a un jugador existente en la base de datos.</li>
	 *   <li>El jugador debe estar asociado a un equipo para que se puedan consultar sus estadísticas.</li>
	 * </ul>
	 * 
	 * @param id El ID del jugador cuyas estadísticas se desean consultar.
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a datos nulos.
	 * @throws IllegalArgumentException Si un argumento es inválido.
	 * @throws Exception Para otros errores no previstos.
	 * @see Jugador
	 * @see Equipo
	 * @see Partido
	 * @see Datosjugadorpartido
	 * @since 2024
	 */
	public void mostrarEstadisticasDeTitularYSuplente(int id) {
		
		Session s = null ; 
		Transaction trans = null; 
		Equipo equipo= null;
		DecimalFormat formato =new DecimalFormat("0.00");

		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			Query q1 = s.createQuery("from Jugador where idJugador = ?0" ).setReadOnly(true);
			q1.setParameter(0, id);
			List<Jugador> jugadores = q1.getResultList();//SingleResult lanza catch si no encuentra ningún resultado (ID fuera de rango)

			if (jugadores.isEmpty()) {
			    System.out.println("Actualmente no existe un jugador con el ID brindado.\n");
			    return; // Salimos del método si no hay jugador.
			}else {
				for(Jugador jugador : jugadores) {
					
					equipo= jugador.getEquipo();
					if(equipo!=null) {
						
						Query q2 = s.createQuery("from Partido where idEquipoLocal = ?0").setReadOnly(true);
						q2.setParameter(0,equipo.getIdEquipo());
						List<Partido> listaPartidosLocal = q2.getResultList();
						Query q3 = s.createQuery("from Partido where idEquipoVisitante = ?0").setReadOnly(true);
						q3.setParameter(0,equipo.getIdEquipo());
						List<Partido> listaPartidosVisitante = q3.getResultList();
						
						int contadorTitular = 0 ; //Variables para recopilar datos en partidos de titular.
						Double valoracionTitular = 0.0 ;
						Double puntosTitular = 0.0 ,asistenciasTitular = 0.0, rebotesTitular=0.0, taponesTitular = 0.0;
						List<Datosjugadorpartido> datosJugadorTitular = new ArrayList<Datosjugadorpartido>();
						
						int contadorSuplente = 0 ; //Variables para recopilar datos en partidos de suplente.
						Double valoracionSuplente = 0.0 ;
						Double puntosSuplente = 0.0 ,asistenciasSuplente = 0.0, rebotesSuplente=0.0, taponesSuplente = 0.0;
						List<Datosjugadorpartido> datosJugadorSuplente = new ArrayList<Datosjugadorpartido>();
						
						for(Partido partido : listaPartidosLocal) {
							DatosjugadorpartidoId datosId = new DatosjugadorpartidoId();
							datosId = new DatosjugadorpartidoId(jugador.getIdJugador(),partido.getIdPartido());
							Datosjugadorpartido datosPartido = s.get(Datosjugadorpartido.class, datosId);
							if( datosPartido!=null) {
								
								if (datosPartido.getTitular()) { // Titular en partido de local.
									contadorTitular++;
									valoracionTitular+=datosPartido.getValoracion();
									puntosTitular += datosPartido.getPuntos();
									asistenciasTitular+=datosPartido.getAsistencias();
									rebotesTitular+=datosPartido.getRebotes();
									taponesTitular+=datosPartido.getTapones();
									datosJugadorTitular.add(datosPartido);
									
								}else { //Suplente en partido de local
									contadorSuplente++;
									valoracionSuplente+=datosPartido.getValoracion();
									puntosSuplente += datosPartido.getPuntos();
									asistenciasSuplente+=datosPartido.getAsistencias();
									rebotesSuplente+=datosPartido.getRebotes();
									taponesSuplente+=datosPartido.getTapones();
									datosJugadorSuplente.add(datosPartido);
								}
								
							}
						}
						for(Partido partido : listaPartidosVisitante) {
							DatosjugadorpartidoId datosId = new DatosjugadorpartidoId();
							datosId = new DatosjugadorpartidoId(jugador.getIdJugador(),partido.getIdPartido());
							Datosjugadorpartido datosPartido = s.get(Datosjugadorpartido.class, datosId);
							if( datosPartido!=null) {
								
								if (datosPartido.getTitular()) { // Titular en partido de visitante.
									contadorTitular++;
									valoracionTitular+=datosPartido.getValoracion();
									puntosTitular += datosPartido.getPuntos();
									asistenciasTitular+=datosPartido.getAsistencias();
									rebotesTitular+=datosPartido.getRebotes();
									taponesTitular+=datosPartido.getTapones();
									datosJugadorTitular.add(datosPartido);
									
								}else { //Suplente en partido de visitante.
									contadorSuplente++;
									valoracionSuplente+=datosPartido.getValoracion();
									puntosSuplente += datosPartido.getPuntos();
									asistenciasSuplente+=datosPartido.getAsistencias();
									rebotesSuplente+=datosPartido.getRebotes();
									taponesSuplente+=datosPartido.getTapones();
									datosJugadorSuplente.add(datosPartido);
								}
								
							}
						}//Obtenemos la media de los valores
						valoracionTitular = valoracionTitular/contadorTitular;
						puntosTitular= puntosTitular/contadorTitular;
						asistenciasTitular = asistenciasTitular/contadorTitular;
						rebotesTitular = rebotesTitular/contadorTitular;
						taponesTitular = taponesTitular/contadorTitular;
						
						valoracionSuplente = valoracionSuplente/contadorSuplente;
						puntosSuplente= puntosSuplente/contadorSuplente;
						asistenciasSuplente = asistenciasSuplente/contadorSuplente;
						rebotesSuplente = rebotesSuplente/contadorSuplente;
						taponesSuplente = taponesSuplente/contadorSuplente;
						
						System.out.println("Estadísticas para el jugador "+jugador.getNombre()+" "+jugador.getApellidos()+" (ID: "+jugador.getIdJugador()+"), en partidos de titular:");
						if(datosJugadorTitular.size()>0) {
							for (Datosjugadorpartido datosTitular : datosJugadorTitular) {
								System.out.println("  ID partido: "+datosTitular.getPartido().getIdPartido()+" , valoración: "+datosTitular.getValoracion()+" , puntos: "+datosTitular.getPuntos()+" , asistencias: "+datosTitular.getAsistencias()+" , rebotes:"+datosTitular.getRebotes()+" , tapones: "+datosTitular.getTapones());
							}
						}else {
							System.out.println("  El jugador no posee estadísticas como titular.");
						}
						if (contadorTitular>0){
							System.out.println("  Media como titular [ Valoración :"+formato.format(valoracionTitular)+" , puntos: "+formato.format(puntosTitular)+" , asistencias : "+formato.format(asistenciasTitular)+" , rebotes: "+formato.format(rebotesTitular)+" , tapones: "+formato.format(taponesTitular)+" , partidos jugados como titular: "+contadorTitular+"].");	
						}else {
							System.out.println("  No es posible hacer media porque el jugador tiene 0 partidos como titular.");
						}
						
						System.out.println("\nEstadísticas para el jugador "+jugador.getNombre()+" "+jugador.getApellidos()+" (ID: "+jugador.getIdJugador()+"), en partidos de suplente:");
						if(datosJugadorSuplente.size()>0) {
							for (Datosjugadorpartido datosSuplente : datosJugadorSuplente) {
								System.out.println("  ID partido: "+datosSuplente.getPartido().getIdPartido()+" , valoración: "+datosSuplente.getValoracion()+" , puntos: "+datosSuplente.getPuntos()+" , asistencias: "+datosSuplente.getAsistencias()+" , rebotes:"+datosSuplente.getRebotes()+" , tapones: "+datosSuplente.getTapones());
							}
						}else {
							System.out.println("  El jugador no posee estadísticas como suplente.");
						}
						if(contadorSuplente>0) {
							System.out.println("  Media como suplente [ Valoración :"+formato.format(valoracionSuplente)+" , puntos: "+formato.format(puntosSuplente)+" , asistencias : "+formato.format(asistenciasSuplente)+" , rebotes: "+formato.format(rebotesSuplente)+" , tapones: "+formato.format(taponesSuplente)+" , partidos jugados como suplente: "+contadorSuplente+"].\n");	
						}else {
							System.out.println("  No es posible hacer media porque el jugador tiene 0 partidos como suplente.\n");
						}
				
					
					}else {
						 System.out.println("El jugador con el ID brindado no está asignado a un equipo.\n");
					}
				
				}
			}
			trans.commit();
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
	}
	
	/**
	 * Muestra las estadísticas acumuladas de un equipo en partidos jugados como local y como visitante.
	 * Los datos incluyen la cantidad de partidos jugados, partidos ganados, partidos perdidos, 
	 * puntos a favor y puntos en contra en cada condición (local y visitante).
	 * 
	 * <h3>Proceso de cálculo</h3>
	 * <ul>
	 *   <li>Se busca el equipo en la base de datos utilizando su ID.</li>
	 *   <li>Se obtienen todos los partidos en los que el equipo jugó como local y como visitante.</li>
	 *   <li>Se calculan las métricas requeridas para cada caso: partidos jugados, ganados, perdidos, puntos a favor y puntos en contra.</li>
	 *   <li>Se imprime la información para cada rol (local y visitante) en la consola.</li>
	 * </ul>
	 * 
	 * <h3>Validaciones </h3>
	 * <ul>
	 *   <li>Si no existe un equipo con el ID proporcionado, se informa en la consola y se finaliza el método.</li>
	 *   <li>Si el equipo no tiene partidos jugados como local o visitante, también se informa.</li>
	 * </ul>
	 * 
	 * <p>
	 * Este método abre una sesión de Hibernate, la utiliza para realizar las consultas, y finalmente la cierra,
	 * asegurando el uso adecuado de los recursos.
	 * En caso de error, se realiza un rollback de la transacción para preservar la integridad de los datos.
	 * </p>
	 * 
	 * <h3>Restricciones</h3>
	 * <ul>
	 *   <li>El ID proporcionado debe ser válido y corresponder a un equipo existente en la base de datos.</li>
	 *   <li>El equipo debe tener al menos un partido registrado como local o visitante para mostrar estadísticas.</li>
	 * </ul>
	 * 
	 * @param id El ID del equipo cuyas estadísticas se desean consultar.
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a datos nulos.
	 * @throws IllegalArgumentException Si un argumento es inválido.
	 * @throws Exception Para otros errores no previstos.
	 * @see Equipo
	 * @see Partido
	 * @since 2024
	 */

	public void mostrarEstadisticasEquipoDeLocalYVisitante(int id) {
		Session s = null ; 
		Transaction trans = null; 

		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			Query q1 = s.createQuery("from Equipo where idEquipo = ?0" ).setReadOnly(true);
			q1.setParameter(0, id);
			List<Equipo> equipos = q1.getResultList();

			if (equipos.isEmpty()) {
			    System.out.println("Actualmente no existe un equipo con el ID brindado.\n");
			    return; // Salimos del método si no hay equipo.
			}else {
				for(Equipo equipo : equipos) {
					
					Query q2 = s.createQuery("from Partido where idEquipoLocal = ?0").setReadOnly(true);
					q2.setParameter(0,equipo.getIdEquipo());
					List<Partido> listaPartidosLocal = q2.getResultList();
					Query q3 = s.createQuery("from Partido where idEquipoVisitante = ?0").setReadOnly(true);
					q3.setParameter(0,equipo.getIdEquipo());
					List<Partido> listaPartidosVisitante = q3.getResultList();
					
					int contadorLocal = 0 ; //Variables para recopilar datos en partidos de Local.
					int partidosGanadosLocal = 0 ,partidosPerdidosLocal = 0, puntosFavorLocal=0, puntosContraLocal = 0;
					
					int contadorVisitante = 0 ; //Variables para recopilar datos en partidos de Visitante.
					int partidosGanadosVisitante = 0 ,partidosPerdidosVisitante = 0, puntosFavorVisitante=0, puntosContraVisitante = 0;
					
					for(Partido partido : listaPartidosLocal) {
						contadorLocal ++;
						if(partido.getPuntosLocal()>partido.getPuntosVisitante()) {//Gana de local.
							partidosGanadosLocal++;
							//puntosFavorLocal += partido.getPuntosLocal()-partido.getPuntosVisitante();
							puntosFavorLocal += partido.getPuntosLocal();
							puntosContraLocal += partido.getPuntosVisitante();
	
						}else if (partido.getPuntosLocal()<partido.getPuntosVisitante()) {//Pierde de local.
							partidosPerdidosLocal++;
							//puntosContraLocal+= partido.getPuntosVisitante()-partido.getPuntosLocal();
							puntosFavorLocal += partido.getPuntosLocal();
							puntosContraLocal += partido.getPuntosVisitante();
						}
					}
					
					for(Partido partido : listaPartidosVisitante) {
						contadorVisitante ++;
						if(partido.getPuntosVisitante()>partido.getPuntosLocal()) {//Gana de visitante.
							partidosGanadosVisitante++;
							//puntosFavorVisitante += partido.getPuntosVisitante()-partido.getPuntosLocal();
							puntosFavorVisitante += partido.getPuntosVisitante();
							puntosContraVisitante += partido.getPuntosLocal();
							
						}else if (partido.getPuntosVisitante()<partido.getPuntosLocal()) {//Pierde de visitante.
							partidosPerdidosVisitante++;
							//puntosContraVisitante+= partido.getPuntosLocal()-partido.getPuntosVisitante();
							puntosFavorVisitante += partido.getPuntosVisitante();
							puntosContraVisitante += partido.getPuntosLocal();
						}
					}
					
					if (contadorLocal>0 ) {
						System.out.println("Estadísticas en partidos jugados como local para el equipo "+equipo.getNombre()+" (ID:"+equipo.getIdEquipo()+"):");
						System.out.println("  Partidos jugados: "+contadorLocal+", ganados: "+partidosGanadosLocal+" , perdidos: "+partidosPerdidosLocal+" , puntos a favor de local: "+puntosFavorLocal+" , puntos en contra de local: "+puntosContraLocal+".");
					}else {
						System.out.println("El equipo no cuenta actualmente con partidos jugados como local.");
					}
					if (contadorVisitante>0 ) {
						System.out.println("\nEstadísticas en partidos jugados como visitante para el equipo "+equipo.getNombre()+"(ID:"+equipo.getIdEquipo()+"):");
						System.out.println("  Partidos jugados: "+contadorVisitante+", ganados: "+partidosGanadosVisitante+" , perdidos: "+partidosPerdidosVisitante+" , puntos en contra de visitante: "+puntosContraVisitante+" , puntos a favor de visitante "+puntosFavorVisitante+".");
					}else {
						System.out.println("\nEl equipo no cuenta actualmente con partidos jugados como local.");
					}
					System.out.println(" ");

				}
		
			}
			trans.commit();
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
	}
	/**
	 * Muestra la clasificación actual de la liga, calculando estadísticas acumuladas para cada equipo 
	 * y ordenando los resultados según puntos obtenidos y diferencia de puntos total.
	 * 
	 * <h3>Proceso de cálculo</h3>
	 * <ul>
	 *   <li>Obtiene la lista de equipos desde la base de datos.</li>
	 *   <li>Para cada equipo, recupera los partidos jugados como local y como visitante.</li>
	 *   <li>Calcula estadísticas como:
	 *       <ul>
	 *           <li>Puntos totales obtenidos.</li>
	 *           <li>Partidos ganados y perdidos.</li>
	 *           <li>Puntos a favor y en contra globales, como local y como visitante.</li>
	 *           <li>Diferencia de puntos global, como local y como visitante.</li>
	 *       </ul>
	 *   </li>
	 *   <li>Crea objetos 'DatosClasificacionLiga' con los datos calculados para cada equipo.</li>
	 *   <li>Ordena los equipos por puntos (primera prioridad) y diferencia de puntos (segunda prioridad).</li>
	 *   <li>Muestra los datos en formato tabular en la consola.</li>
	 * </ul>
	 * 
	 * <h3>Formato de salida</h3>
	 * Se genera una tabla con las siguientes columnas:
	 * <ul>
	 *   <li><strong>Equipo(ID):</strong> Nombre del equipo seguido de su ID.</li>
	 *   <li><strong>Puntos:</strong> Total de puntos obtenidos en la liga.</li>
	 *   <li><strong>PG:</strong> Partidos ganados.</li>
	 *   <li><strong>PP:</strong> Partidos perdidos.</li>
	 *   <li><strong>pF:</strong> Puntos a favor totales.</li>
	 *   <li><strong>pC:</strong> Puntos en contra totales.</li>
	 *   <li><strong>dPT:</strong> Diferencia total de puntos.</li>
	 *   <li><strong>pFL:</strong> Puntos a favor como local.</li>
	 *   <li><strong>pCL:</strong> Puntos en contra como local.</li>
	 *   <li><strong>dPTL:</strong> Diferencia de puntos total como local.</li>
	 *   <li><strong>pFV:</strong> Puntos a favor como visitante.</li>
	 *   <li><strong>pCV:</strong> Puntos en contra como visitante.</li>
	 *   <li><strong>dPTV:</strong> Diferencia de puntos total como visitante.</li>
	 * </ul>
	 * 
	 * <h3>Validaciones</h3>
	 * <ul>
	 *   <li>Si no hay equipos en la base de datos, informa en consola y finaliza el método.</li>
	 * </ul>
	 * 
	 * <p>
	 * Este método abre una sesión de Hibernate, la utiliza para realizar las consultas, y finalmente la cierra,
	 * asegurando el uso adecuado de los recursos.
	 * En caso de error, se realiza un rollback de la transacción para preservar la integridad de los datos.
	 * </p>
	 * 
	 * <h3>Ordenamiento</h3>
	 * Los equipos se ordenan de forma descendente por:
	 * <ol>
	 *   <li>Puntos totales.</li>
	 *   <li>Diferencia total de puntos (en caso de empate en puntos).</li>
	 * </ol>
	 * 
	 * <h3>Restricciones</h3>
	 * <ul>
	 *   <li>Se asume que los partidos contienen datos válidos y consistentes.</li>
	 * </ul>
	 * 
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a datos nulos.
	 * @throws IllegalArgumentException Si un argumento es inválido.
	 * @throws Exception Para otros errores no previstos.
	 * @see Equipo
	 * @see Partido
	 * @see DatosClasificacionLiga
	 * @since 2024
	 */

	public void mostrarClasificacionLiga() {
		Session s = null ; 
		Transaction trans = null; 
		List<DatosClasificacionLiga> listaDatosCLiga = new ArrayList<DatosClasificacionLiga>();
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			Query q1 = s.createQuery("from Equipo" ).setReadOnly(true);
			List<Equipo> equipos = q1.getResultList();

			if (equipos.isEmpty()) {
			    System.out.println("Actualmente no existen equipos en la base de datos.\n");
			    return; // Salimos del método si no hay equipos.
			}else {
				for(Equipo equipo : equipos) {
					
					Query q2 = s.createQuery("from Partido where idEquipoLocal = ?0").setReadOnly(true);
					q2.setParameter(0,equipo.getIdEquipo());
					List<Partido> listaPartidosLocal = q2.getResultList();
					Query q3 = s.createQuery("from Partido where idEquipoVisitante = ?0").setReadOnly(true);
					q3.setParameter(0,equipo.getIdEquipo());
					List<Partido> listaPartidosVisitante = q3.getResultList();
					
					int puntos = 0 ; //Variables para recopilar datos globales.
					int partidosGanados = 0, partidosPerdidos = 0 , puntosFavor=0, puntosContra = 0 , diferenciaTotalPuntos = 0;
					
					int puntosFavorComoLocal=0; //Variables para recopilar datos en partidos de Local.
					int puntosContraComoLocal = 0 ,diferenciaTotalPuntosComoLocal = 0;
					
					int puntosFavorComoVisitante=0 ; //Variables para recopilar datos en partidos de Visitante.
					int puntosContraComoVisitante = 0, diferenciaTotalPuntosComoVisitante=0;
					
					for(Partido partido : listaPartidosLocal) {
						
						if(partido.getPuntosLocal()>partido.getPuntosVisitante()) {//Gana de local.
							puntos+=2;
							partidosGanados++;
							puntosFavor += partido.getPuntosLocal();
							puntosContra += partido.getPuntosVisitante();
							
							puntosFavorComoLocal+=partido.getPuntosLocal();
							puntosContraComoLocal += partido.getPuntosVisitante();
	
						}else if (partido.getPuntosLocal()<partido.getPuntosVisitante()) {//Pierde de local.
							puntos+=0;
							partidosPerdidos++;
							puntosFavor += partido.getPuntosLocal();
							puntosContra += partido.getPuntosVisitante();
							
							puntosFavorComoLocal+=partido.getPuntosLocal();
							puntosContraComoLocal += partido.getPuntosVisitante();
						}
					}
					
					for(Partido partido : listaPartidosVisitante) {
						
						if(partido.getPuntosVisitante()>partido.getPuntosLocal()) {//Gana de visitante.
							puntos+=2;
							partidosGanados++;
							puntosFavor+= partido.getPuntosVisitante();
							puntosContra+= partido.getPuntosLocal();
							
							puntosFavorComoVisitante+=partido.getPuntosVisitante();
							puntosContraComoVisitante += partido.getPuntosLocal();
							
						}else if (partido.getPuntosVisitante()<partido.getPuntosLocal()) {//Pierde de visitante.
							puntos+=0;
							partidosPerdidos++;
							puntosFavor += partido.getPuntosVisitante();
							puntosContra += partido.getPuntosLocal();
							
							puntosFavorComoVisitante+=partido.getPuntosVisitante();
							puntosContraComoVisitante += partido.getPuntosLocal();
						}
					}
					diferenciaTotalPuntos = puntosFavor - puntosContra;
					diferenciaTotalPuntosComoLocal = puntosFavorComoLocal - puntosContraComoLocal;
					diferenciaTotalPuntosComoVisitante = puntosFavorComoVisitante - puntosContraComoVisitante;
					
					DatosClasificacionLiga datosCLiga = new DatosClasificacionLiga(equipo, puntos, partidosGanados, partidosPerdidos, puntosFavor, puntosContra, diferenciaTotalPuntos, puntosFavorComoLocal, puntosContraComoLocal, diferenciaTotalPuntosComoLocal, puntosFavorComoVisitante, puntosContraComoVisitante, diferenciaTotalPuntosComoVisitante);
					listaDatosCLiga.add(datosCLiga);
					

				}
				listaDatosCLiga.sort(new ComparadorDatosClasificacionLigaPorPuntos().thenComparing(new ComparadorDatosClasificacionLigaPorDifTotalPuntos()));
				System.out.println("\n\tEquipo(ID)\t\t  |   Puntos   |   PG    |  PP  |    pF    |    pC    |    dPT   |   pFL   |   pCL   |   dPTL  |   pFV   |   pCV   |   dPTV  ");
				System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------");
				for(DatosClasificacionLiga datos : listaDatosCLiga) {
					System.out.println(datos.toString());
				}
				System.out.println("");
			}
			trans.commit();
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
	}
	
	
	
	

}
