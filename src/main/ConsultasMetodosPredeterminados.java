package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.Query;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import conexion.HibernateUtil;
import orm.*;
import interfaces.*;

public class ConsultasMetodosPredeterminados {

	public ConsultasMetodosPredeterminados() {
		super();
	}

	/**
	 * Busca y muestra información específica de una entidad en la base de datos según su ID.
	 * Admite la búsqueda de jugadores, equipos, partidos y datos de jugador por partido (con claves compuestas).
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>La transacción se inicia antes de realizar las búsquedas y se finaliza al concluirlas.</li>
	 *   <li>Las claves compuestas en entidades como "DatosJugadorPartido" requieren ambos parámetros, `id` y `id2`.</li>
	 *   <li>Si no se encuentra un registro correspondiente, se notifica en consola.</li>
	 *   <li>El método no realiza validaciones específicas del contenido de las entidades, solo verifica su existencia.</li>
	 * </ul>
	 * 
	 * @param entidad El tipo de entidad a buscar. Puede ser: "Jugador", "Equipo", "Partido", "DatosJugadorPartido".
	 * @param id El ID principal de la entidad a buscar.
	 * @param id2 (Opcional) Un segundo ID necesario para buscar entidades con claves compuestas (como "DatosJugadorPartido").
	 * 
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a datos nulos.
	 * @throws IllegalArgumentException Si se proporciona un argumento inválido.
	 * @throws Exception Para otros errores no previstos.
	 * 
	 * @see Jugador
	 * @see Equipo
	 * @see Partido
	 * @see Datosjugadorpartido
	 * @since 2024
	 */

	public void buscarRegistrosPorID(String entidad ,int id , int id2) {
		Session s = null;
		Transaction trans = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			if(entidad.equalsIgnoreCase("Jugador")) {
	
				Jugador jugador = new Jugador();
				jugador=s.get(Jugador.class, id);
				if (jugador!=null) {
					System.out.println("La información solicitada es:");
					System.out.println("ID de jugador: "+jugador.getIdJugador()+", ID equipo: "+jugador.getEquipo().getIdEquipo()+" , nombre: "+jugador.getNombre()+" , apellidos: "+jugador.getApellidos()+" ,altura (cm): "+jugador.getAlturaCm()+" ,edad :"+jugador.getEdad()+" , nacionalidad: "+jugador.getNacionalidad()+".\n");
				}else {
					System.out.println("No hay resultado para el id brindado.");
				}
			}
			if(entidad.equalsIgnoreCase("Equipo")) {
	
				Equipo equipo = new Equipo();
				equipo=s.get(Equipo.class, id);
				if (equipo!=null) {
					System.out.println("La información solicitada es:");
					System.out.println("ID de equipo: "+equipo.getIdEquipo()+", nombre: "+equipo.getNombre()+" ,localidad: "+equipo.getLocalidad()+" ,país: "+equipo.getPais()+" ,nombre corto :"+equipo.getNombreCorto()+".\n");
				}else {
					System.out.println("No hay resultado para el id brindado.");
				}
			}
			if(entidad.equalsIgnoreCase("Partido")) {
	
				Partido partido = new Partido();
				partido=s.get(Partido.class, id);
				if (partido!=null) {
					System.out.println("La información solicitada es:");
					System.out.println("ID de partido: "+partido.getIdPartido()+", ID equipo local: "+partido.getEquipoByIdEquipoLocal().getIdEquipo()+" ("+partido.getEquipoByIdEquipoLocal().getNombre()+")"+" , ID equipo visitante: "+partido.getEquipoByIdEquipoVisitante().getIdEquipo()+" ("+partido.getEquipoByIdEquipoVisitante().getNombre()+")"+" , puntos local: "+partido.getPuntosLocal()+" , puntos visitante :"+partido.getPuntosVisitante()+".\n");
				}else {
					System.out.println("No hay resultado para el id brindado.");
				}
			}
			if(entidad.equalsIgnoreCase("DatosJugadorPartido")) {
	
				DatosjugadorpartidoId datosId = new DatosjugadorpartidoId(); // Aquí se le pasa la clave compuesta
				datosId.setIdP(id);  // 'id' es el id del partido
				datosId.setIdJ(id2);  //'id2' es el id del jugador
			    
	
			    Datosjugadorpartido datosPartido = s.get(Datosjugadorpartido.class, datosId); // y la brindamos aquí a la clave compuesta
			    if (datosPartido != null) {
			        System.out.println("La información solicitada es:");
			        System.out.println("ID de partido: " + datosPartido.getId().getIdP()+" ,ID de jugador: " + datosPartido.getId().getIdJ()+" ,valoración: " + datosPartido.getValoracion()+" ,puntos: " + datosPartido.getPuntos()+" ,asistencias: " + datosPartido.getAsistencias()+" ,rebotes: " + datosPartido.getRebotes()+" ,tapones: " + datosPartido.getTapones()+" ,titular: " + datosPartido.getTitular()+".\n");
			        
				}else {
					System.out.println("No hay resultado para el id brindado.");
				}
			}
			
			trans.commit();
			
	    } catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
	}
	
	/**
	 * Busca la información de un jugador por su ID y muestra sus datos personales junto con las estadísticas de los partidos que ha jugado.
	 * Las estadísticas incluyen todos los partidos jugados como local o visitante, y se ordenan según valoración, puntos, asistencias y rebotes.
	 * 
	 * <h3>Descripción general</h3>
	 * <ul>
	 *   <li>Primero, se busca el jugador por su ID.</li>
	 *   <li>Luego, se obtienen los partidos asociados al equipo del jugador (como local y visitante).</li>
	 *   <li>Para cada partido, se consulta el desempeño del jugador en ese partido mediante la clave compuesta (ID de jugador e ID de partido).</li>
	 *   <li>Las estadísticas se ordenan por valoración y luego por puntos, asistencias y rebotes.</li>
	 *   <li>Finalmente, se imprimen los datos personales del jugador y sus estadísticas ordenadas.</li>
	 * </ul>
	 * 
	 * <h3>Salida</h3>
	 * <ul>
	 *   <li><strong>Datos personales del jugador:</strong> ID, ID del equipo, nombre del equipo, nombre completo, altura, edad y nacionalidad.</li>
	 *   <li><strong>Estadísticas por partido:</strong> ID del partido, valoración, puntos, asistencias, rebotes, tapones y si fue titular.</li>
	 * </ul>
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>Se utiliza una lista para almacenar las estadísticas del jugador en cada partido.</li>
	 *   <li>Los partidos del equipo se dividen entre local y visitante para garantizar que se recopilen todos los datos relevantes.</li>
	 *   <li>Si el jugador o sus estadísticas no se encuentran, se notifica en consola.</li>
	 *   <li>Las claves compuestas (ID de jugador y ID de partido) se reinician en cada iteración.</li>
	 *   <li>Las estadísticas se ordenan utilizando comparadores personalizados.</li>
	 * </ul>
	 * 
	 * <h3>Ordenamiento</h3>
	 * Las estadísticas del jugador se ordenan con los siguientes criterios en cascada:
	 * <ol>
	 *   <li>Por valoración (de mayor a menor).</li>
	 *   <li>En caso de empate, por puntos (de mayor a menor).</li>
	 *   <li>En caso de empate, por asistencias (de mayor a menor).</li>
	 *   <li>En caso de empate, por rebotes (de mayor a menor).</li>
	 * </ol>
	 * 
	 * @param id El ID del jugador que se desea buscar.
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a datos nulos.
	 * @throws IllegalArgumentException Si se proporciona un argumento inválido.
	 * @throws Exception Para otros errores no previstos.
	 * 
	 * @see Jugador
	 * @see Partido
	 * @see Datosjugadorpartido
	 * @see DatosjugadorpartidoId
	 * @since 2024
	 */
	public void buscarJugadorConEstadisticas (int id) {
		
		Session s= null;
		Transaction trans = null;
		
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
					
			Jugador jugador = new Jugador();
			jugador = s.get(Jugador.class, id);
			
			DatosjugadorpartidoId datosClaveCompuesta =null;
			
			if(jugador!=null) {
				System.out.println("  Datos del jugador:");
				System.out.println("ID jugador: "+jugador.getIdJugador()+" , ID equipo: "+jugador.getEquipo().getIdEquipo()+"("+jugador.getEquipo().getNombre()+") , nombre: "+jugador.getNombre()+" , apellidos: "+jugador.getApellidos()+" , altura(cm): "+jugador.getAlturaCm()+" , edad: "+jugador.getEdad()+" , nacionalidad: "+jugador.getNacionalidad()+".");
				
				List<Datosjugadorpartido> listaDeDatosEnPartidos = new ArrayList<Datosjugadorpartido>();//En ella introducimos todos los partidos para ordenarles luego.
				
				// Obtener los partidos como equipo local
		        Set<Partido> partidosLocal = jugador.getEquipo().getPartidosForIdEquipoLocal();

		        for (Partido partido : partidosLocal) {
		            //System.out.println("ID Partido: " + partido.getIdPartido());// Con este dato puedo armar la clave compuesta
		        	datosClaveCompuesta = new DatosjugadorpartidoId();// Importante reiniciar la clave en cada iteración para no tener problemas
		        	
		        	datosClaveCompuesta.setIdJ(jugador.getIdJugador());
		            datosClaveCompuesta.setIdP(partido.getIdPartido());
				    Datosjugadorpartido datosPartido = s.get(Datosjugadorpartido.class, datosClaveCompuesta); // brindamos aquí a la clave compuesta
				    if (datosPartido != null) {
				        
				        listaDeDatosEnPartidos.add(datosPartido);
				    }else {
						System.out.println("No hay resultado para el id brindado.");
					}
		            
		        }  
		        // Obtener los partidos como equipo visitante
		        Set<Partido> partidosVisitante = jugador.getEquipo().getPartidosForIdEquipoVisitante();

		        for (Partido partido : partidosVisitante) {
		            //System.out.println("ID Partido: " + partido.getIdPartido());
		        	datosClaveCompuesta = new DatosjugadorpartidoId();
		        	
		            datosClaveCompuesta.setIdJ(jugador.getIdJugador());
		            datosClaveCompuesta.setIdP(partido.getIdPartido());
				    Datosjugadorpartido datosPartido = s.get(Datosjugadorpartido.class, datosClaveCompuesta); // brindamos aquí a la clave compuesta
				    if (datosPartido != null) {
			
				        listaDeDatosEnPartidos.add(datosPartido);
				    }else {
						System.out.println("No hay resultado para el id brindado.");
					}
				}

				System.out.println("\n  Estadísticas del jugador:");
				//Se ordena según lo solicitado.
				listaDeDatosEnPartidos.sort(new ComparadorDatosJugadorPorValoracion().thenComparing(new ComparadorDatosJugadorPorPuntos().thenComparing(new ComparadorDatosJugadorPorAsistencias().thenComparing(new ComparadorDatosJugadorPorRebotes()))));
				for (Datosjugadorpartido datosPartido : listaDeDatosEnPartidos) {
					System.out.println("ID de partido: " + datosPartido.getId().getIdP()+" ,ID de jugador: " + datosPartido.getId().getIdJ()+" ,valoración: " + datosPartido.getValoracion()+" ,puntos: " + datosPartido.getPuntos()+" ,asistencias: " + datosPartido.getAsistencias()+" ,rebotes: " + datosPartido.getRebotes()+" ,tapones: " + datosPartido.getTapones()+" ,titular: " + datosPartido.getTitular()+".");
				}
			}else {
				System.out.println("No hay resultado para el id brindado.");
			}
			
			trans.commit();
			
	    } catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
	}
	
	/**
	 * Busca la información de un equipo por su ID y muestra los datos del equipo junto con los jugadores asociados, ordenados por altura.
	 * 
	 * <h3>Descripción general</h3>
	 * <ul>
	 *   <li>Primero, se busca el equipo por su ID.</li>
	 *   <li>Luego, se obtienen todos los jugadores asociados al equipo.</li>
	 *   <li>Los jugadores se convierten en una lista, se ordenan por altura (de mayor a menor o según el comparador utilizado) y se imprimen.</li>
	 *   <li>Si no se encuentra el equipo, se notifica al usuario.</li>
	 * </ul>
	 * 
	 * <h3>Salida</h3>
	 * <ul>
	 *   <li><strong>Datos del equipo:</strong> ID, nombre, localidad, país y nombre corto.</li>
	 *   <li><strong>Jugadores del equipo:</strong> ID del jugador, ID del equipo, nombre, apellidos, altura, edad y nacionalidad.</li>
	 * </ul>
	 * 
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>Se utiliza un 'Set' para almacenar los jugadores asociados al equipo y luego se convierte en una 'List' para ordenarlos.</li>
	 *   <li>El orden de los jugadores se basa en un comparador personalizado ("ComparadorJugadoresPorAltura").</li>
	 *   <li>Si el equipo o los jugadores no se encuentran, se notifica al usuario en la consola.</li>
	 *   <li>El método utiliza transacciones para garantizar la consistencia de la base de datos y realiza un cierre adecuado de la sesión.</li>
	 * </ul>
	 * 
	 * <h3>Ordenamiento</h3>
	 * Los jugadores se ordenan utilizando un comparador personalizado que organiza los datos por altura.
	 * 
	 * @param id El ID del equipo que se desea buscar.
	 * @throws HibernateException Si ocurre un error durante la interacción con la base de datos.
	 * @throws NullPointerException Si se intenta acceder a datos nulos.
	 * @throws IllegalArgumentException Si se proporciona un argumento inválido.
	 * @throws Exception Para otros errores no previstos.
	 * 
	 * @see Equipo
	 * @see Jugador
	 * @see ComparadorJugadoresPorAltura
	 * @since 2024
	 */
	public void buscarEquipoConSusJugadores(int id) {
		
		Session s = null;
		Transaction trans = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Equipo equipo= new Equipo();
			equipo=s.get(Equipo.class, id);
			if(equipo!=null) {
				System.out.println("  Datos del equipo:");
				System.out.println("ID equipo: "+equipo.getIdEquipo()+" , nombre: "+equipo.getNombre()+" , localidad: "+equipo.getLocalidad()+" , país: "+equipo.getPais()+" , nombre corto: "+equipo.getNombreCorto());
				System.out.println("  Jugadores del equipo ordenados por altura:");
				Set<Jugador>jugadoresSet = equipo.getJugadors();
				
				List<Jugador> jugadores = new ArrayList<>(jugadoresSet);
				jugadores.sort(new ComparadorJugadoresPorAltura());
				
				for(Jugador jugador : jugadores) {
					System.out.println("ID jugador: "+jugador.getIdJugador()+" , ID equipo: "+jugador.getEquipo().getIdEquipo()+" , nombre: "+jugador.getNombre()+" , apellidos: "+jugador.getApellidos()+" , altura(cm): "+jugador.getAlturaCm()+" , edad: "+jugador.getEdad()+" , nacionalidad: "+jugador.getNacionalidad()+".");
				}
				System.out.println("");
			}else {
				System.out.println("No hay resultado para el id brindado.");
			}
			
			trans.commit();
	    
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
	}
	
	/**
	 * Inserta un nuevo equipo en la base de datos y permite asociar jugadores al equipo recién creado.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método realiza las siguientes operaciones:
	 * <ol>
	 *   <li>Solicita al usuario que proporcione los datos para crear un nuevo equipo mediante el método ("insertarEquipoNuevo()").</li>
	 *   <li>Si la creación del equipo es exitosa, permite agregar jugadores al equipo recién creado mediante el método ("insertarJugadoresEnNuevoEquipo(Equipo equipoNuevo)").</li>
	 *   <li>En caso de que la inserción del equipo falle (por ejemplo, por datos inválidos o errores en la base de datos), vuelve a solicitar los datos.</li>
	 * </ol>
	 * 
	 * <h3>Comportamiento en bucle</h3>
	 * El método emplea un bucle 'do-while' para garantizar que se intente insertar el equipo hasta que la operación sea exitosa.
	 * 
	 * <h3>Manejo de errores</h3>
	 * Se espera que los métodos auxiliares "insertarEquipoNuevo()" y "insertarJugadoresEnNuevoEquipo()" manejen adecuadamente excepciones como:
	 * <ul>
	 *   <li><b>HibernateException:</b> Si ocurre un problema al interactuar con la base de datos.</li>
	 *   <li><b>IllegalArgumentException:</b> Si se proporcionan datos inválidos al insertar el equipo o los jugadores.</li>
	 *   <li><b>NullPointerException:</b> Si algún objeto requerido no está inicializado correctamente.</li>
	 * </ul>
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>El método depende de "insertarEquipoNuevo()" para crear el equipo y verificar su validez.</li>
	 *   <li>Si "insertarEquipoNuevo()" devuelve "null", significa que hubo un error o que los datos proporcionados son inválidos.</li>
	 *   <li>El método "insertarJugadoresEnNuevoEquipo()" debe encargarse de agregar los jugadores al equipo de manera interactiva o automatizada.</li>
	 *   <li>La inserción de jugadores solo se realiza si la creación del equipo fue exitosa.</li>
	 * </ul>
	 * 
	 * <h3>Requisitos previos</h3>
	 * Asegúrate de implementar correctamente los métodos auxiliares:
	 * <ul>
	 *   <li><b>insertarEquipoNuevo()</b>: Solicita los datos del equipo y devuelve una instancia válida de <b>"Equipo"</b> o <b>null</b> si falló.</li>
	 *   <li><b>insertarJugadoresEnNuevoEquipo(Equipo equipoNuevo)</b>: Maneja la lógica para asociar jugadores al equipo.</li>
	 * </ul>
	 * 
	 * <h3>Dependencias</h3>
	 * Este método asume la existencia de las siguientes clases y métodos:
	 * <ul>
	 *   <li><b>Equipo</b>: Clase que representa un equipo en la base de datos.</li>
	 *   <li><b>insertarEquipoNuevo()</b>: Método auxiliar que gestiona la creación de un equipo.</li>
	 *   <li><b>insertarJugadoresEnNuevoEquipo(Equipo equipoNuevo)</b>: Método auxiliar para agregar jugadores al equipo.</li>
	 * </ul>
	 * 
	 * @see Equipo
	 * @see #insertarEquipoNuevo()
	 * @see #insertarJugadoresEnNuevoEquipo(Equipo)
	 * @since 2024
	 */
	public void insertarEquipoConPosiblesJugadores() {

		Equipo equipoNuevo = new Equipo() ;
		do {
			equipoNuevo = insertarEquipoNuevo();
			if(equipoNuevo!=null) {
				insertarJugadoresEnNuevoEquipo(equipoNuevo);
			}else {
				System.out.println("");
			}			
		}while(equipoNuevo==null);
		
		
	}

	/**
	 * Inserta un nuevo equipo en la base de datos tras solicitar los datos al usuario.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método interactúa con el usuario para recopilar los datos necesarios para crear un equipo. 
	 * Luego, verifica si el equipo con el ID especificado ya existe en la base de datos. Si el equipo 
	 * no existe, lo inserta en la base de datos y confirma la transacción.
	 * 
	 * <h3>Pasos principales</h3>
	 * <ol>
	 *   <li>Solicita al usuario el ID, nombre, localidad, país y nombre corto del equipo.</li>
	 *   <li>Valida si el equipo con el ID proporcionado ya existe en la base de datos.</li>
	 *   <li>Si el ID del equipo ya está registrado, informa al usuario y permite reintentar la inserción.</li>
	 *   <li>Si los datos son válidos y el equipo no existe, guarda el nuevo equipo en la base de datos y 
	 *       confirma la transacción.</li>
	 * </ol>
	 * 
	 * En todos los casos en que exista error, se asegura de revertir la transacción para mantener la integridad de los datos.
	 * 
	 * <h3>Bucle de inserción</h3>
	 * El método utiliza un bucle 'do-while' para garantizar que el proceso se repita hasta que 
	 * la inserción sea exitosa. Esto permite manejar entradas inválidas o duplicadas de manera interactiva.
	 * 
	 * @return Una instancia de <b>"Equipo"</b> que representa el equipo recién insertado.
	 * @throws InputMismatchException Si el usuario proporciona un tipo de dato incorrecto.
	 * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
	 * @throws IllegalStateException Si ocurre un problema con el estado de la transacción.
	 * @throws Exception Para manejar cualquier error inesperado.
	 * @see Equipo
	 * @see HibernateUtil
	 * @since 2024
	 */

	private Equipo insertarEquipoNuevo() {
		
		Scanner sc = new Scanner(System.in);
		boolean insercion=false;
		int idEquipo;
		String nombreEquipo, localidad, pais, nombreCorto;
		Equipo equipoNuevo = null;
		
		Session s = null;
		
		do {
			Transaction trans = null; // Una transacción por ciclo
			try {
				s = HibernateUtil.getSessionFactory().openSession();
				trans = s.beginTransaction();
				
				System.out.println("Inserta el ID del nuevo equipo, correspondiente a un número entero: ");
				idEquipo=sc.nextInt();
				sc.nextLine();// Limpiar el buffer
				System.out.println("A continuación inserta el nombre del equipo: ");
				nombreEquipo=sc.nextLine();
				System.out.println("Ingresa la localidad del equipo: ");
				localidad=sc.nextLine();
				System.out.println("Ingresa el país al que pertenece el equipo: ");
				pais=sc.nextLine();
				System.out.println("Finalmente ingresa un nombre corto para el equipo: ");
				nombreCorto=sc.nextLine();
				
				// Crear el objeto equipo
				equipoNuevo = new Equipo(idEquipo,nombreEquipo, localidad, pais, nombreCorto);
				
				//Verificamos si el quipo ya existe.
				Equipo equiposEnBase = new Equipo();
				equiposEnBase=s.get(Equipo.class,idEquipo);

				if (equiposEnBase!=null) {
					System.out.println("Ya existe un equipo con id "+idEquipo+". Inténtalo nuevamente.");
	                trans.rollback(); // Revertir la transacción si ya existe
	                insercion = false; // Permitir repetir el bucle
				}else {
					s.save(equipoNuevo);
					trans.commit();
					System.out.println("Equipo insertado con éxito.\n");
					insercion = true; // finalizar el bucle
					
				}
				
			} catch (InputMismatchException e) {
	            System.out.println("Entrada inválida. Por favor, intente nuevamente.\n");
	            sc.nextLine(); // Limpiar entrada
	            if (trans != null) trans.rollback(); // Revertir transacción por seguridad

	        } catch (HibernateException he) {
	            System.err.println("Error de Hibernate: " + he.getMessage());
	            if (trans != null) trans.rollback(); // Revertir transacción

	        } catch (IllegalStateException ise) {
	            System.err.println("Error en la transacción: " + ise.getMessage());
	            if (trans != null) trans.rollback();

	        }catch(Exception e) {
	            System.err.println("Error inesperado: " + e.getMessage());
	            if (trans != null) trans.rollback();
	        } finally {
	            if (s != null) s.close(); 
	        }
		}while (!insercion);
		
		return equipoNuevo;
		
	}
	
	/**
	 * Permite al usuario asignar jugadores sin equipo a un nuevo equipo recién creado.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método interactúa con el usuario para mostrar una lista de jugadores que actualmente 
	 * no tienen un equipo asignado. El usuario puede seleccionar los jugadores a agregar al 
	 * equipo recién creado, validando las entradas y actualizando la base de datos.
	 * 
	 * <h3>Pasos principales</h3>
	 * <ol>
	 *   <li>Pregunta al usuario si desea asignar jugadores al nuevo equipo.</li>
	 *   <li>Obtiene y muestra la lista de jugadores sin equipo desde la base de datos.</li>
	 *   <li>Solicita al usuario los IDs de los jugadores que desea asignar al equipo.</li>
	 *   <li>Valida los IDs proporcionados por el usuario para garantizar que coincidan con jugadores disponibles.</li>
	 *   <li>Confirma la asignación de los jugadores seleccionados al nuevo equipo.</li>
	 *   <li>Actualiza la base de datos para reflejar los cambios y confirma la transacción.</li>
	 * </ol>
	 * 
	 * <h3>Detalles técnicos</h3>
	 * <ul>
	 *   <li>Utiliza HQL (<b>from Jugador where idEquipo = null</b>) para obtener jugadores sin equipo.</li>
	 *   <li>Valida las entradas del usuario utilizando un conjunto (<b>Set</b>) para garantizar la unicidad de los IDs seleccionados.</li>
	 *   <li>Incluye múltiples confirmaciones del usuario para evitar cambios accidentales en los datos.</li>
	 *   <li>Utiliza <b>saveOrUpdate</b> para actualizar los registros de jugadores en la base de datos.</li>
	 * </ul>
	 * 
	 * @param equipoNuevo El equipo recién creado al cual se asignarán los jugadores seleccionados.
	 * @throws HibernateException Si ocurre un error relacionado con la interacción de Hibernate con la base de datos.
	 * @throws IllegalStateException Si ocurre un problema con el estado de la transacción.
	 * @throws Exception Para manejar cualquier error inesperado.
	 * @see Equipo
	 * @see Jugador
	 * @see HibernateUtil
	 * @since 2024
	 */
	private void insertarJugadoresEnNuevoEquipo(Equipo equipoNuevo) {
		
		Scanner sc = new Scanner(System.in);
		String respuesta="",cadenaAuxiliar="",respuestaSegunda="";
		int idSc;
		Session s = null;
		Transaction trans = null;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans=s.beginTransaction();
			do {
				System.out.println("¿Deseas insertar jugadores en el nuevo equipo? : (Si / No).");
				respuesta=sc.nextLine();
				if(respuesta.equalsIgnoreCase("Si")) {
					System.out.println("Los jugadores que actualmente se encuentran sin equipo son :");
					Query q = s.createQuery("from Jugador where idEquipo = null"); // s.createCriteria(getClass()) para obtener los jugadores con equipo null sin HQL se encuentra deprecado
					List<Jugador> jugadoresSinEquipo =  q.getResultList();
	               
					// Mostrar los jugadores encontrados que no tienen equipo asignado
	                if (jugadoresSinEquipo.isEmpty()) {
	                    System.out.println("No hay jugadores sin equipo.");
	                } else {
	                	List<Integer> idsExistentes = new ArrayList<Integer>();
	                	for (Jugador jugador : jugadoresSinEquipo) {
	                        System.out.println("ID: " + jugador.getIdJugador() + ", Nombre: " + jugador.getNombre());
	                        idsExistentes.add(jugador.getIdJugador()); // Creamos una lista con los ids existentes
	                    }
	                    Set<Integer> setIds =new HashSet<Integer>(); ;
	                    System.out.println("A continuación inserta los IDs de estos jugadores que deseas añadir al nuevo equipo, para finalizar escribe 'fin'");
	                    do {
	                    	cadenaAuxiliar="";//reinicio la variable
	                    	if(sc.hasNextInt()) {
	                    		idSc = sc.nextInt();
	                    		for (Integer idAVerificar : idsExistentes) {
	                    			if(idSc == idAVerificar) {
	                    				setIds.add(idSc);
	                    			}
	                    		}
	                    	}else {
	                    		cadenaAuxiliar = sc.nextLine();
	                    	}
	                    }while(!cadenaAuxiliar.equalsIgnoreCase("fin"));
	                    
	                    do {
	                    	if(setIds.size()==0) {
	                    		break;// Si los ids ingresados por el usuario no coninciden con ninguno existente no continuamos.
	                    		
	                    	}
		                    System.out.println("Los IDs válidos ingresados son:");
		                    for (Integer iD : setIds) {
		                    	System.out.print(iD+" ");
		                    }
		    				System.out.println("\n¿Deseas insertar jugadores con estos ids en el nuevo equipo? : (Si / No).");
		    				respuestaSegunda=sc.nextLine();
		    				if(respuestaSegunda.equalsIgnoreCase("Si")) {
	                            // Insertar los jugadores en el equipo nuevo
	                            for (Integer iD : setIds) {
	                                Jugador j = s.get(Jugador.class, iD);
	                                if (j != null) { // Verificar que el jugador exista
	                                    j.setEquipo(equipoNuevo); // Asignar el equipo
	                                    s.saveOrUpdate(j); // Actualizar en la base de datos
	                                    System.out.println("Jugador con id:"+j.getIdJugador()+" insertado correctamente.");
	                                    
	                                }else {
	                                	System.out.println("Jugador con ID: "+iD+" no existe.");
	                                }
	                            }
	                            respuesta="No";//Para salir del bucle principal
	                            break; // Para salir del bucle actual
		    				}
	                    }while (!respuestaSegunda.equalsIgnoreCase("No"));
	             
	                }
				}
				
			}while(!respuesta.equalsIgnoreCase("No"));
			
			trans.commit();
			
        } catch (HibernateException he) {
            System.err.println("Error de Hibernate: " + he.getMessage());
            if (trans != null) trans.rollback(); // Revertir transacción

        } catch (IllegalStateException ise) {
            System.err.println("Error en la transacción: " + ise.getMessage());
            if (trans != null) trans.rollback();

        }catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            if (trans != null) trans.rollback();
        } finally {
            if (s != null) s.close();
            
        }
	}
	
	/**
	 * Permite insertar un nuevo jugador en la base de datos, con la posibilidad de asignarlo a un equipo existente.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método combina dos acciones: 
	 * <ol>
	 *   <li>Crear un nuevo jugador sin asignar un equipo inicialmente.</li>
	 *   <li>Permitir al usuario asignar el jugador creado a un equipo existente, si lo desea.</li>
	 * </ol>
	 * El método utiliza un bucle para asegurarse de que el proceso se repite hasta que el jugador 
	 * sea creado con éxito y procesado correctamente.
	 * 
	 * <h3>Flujo del método</h3>
	 * <ol>
	 *   <li>Llama al método <b>"insertarJugadorSinEquipo"</b> para que el usuario proporcione los datos del nuevo jugador.</li>
	 *   <li>Si el jugador se crea con éxito, llama al método <b>"insertarJugadorEnEquiposExistentes"</b> 
	 *       para gestionar su posible asignación a un equipo.</li>
	 *   <li>Si no se logra crear el jugador, solicita al usuario intentarlo nuevamente.</li>
	 *   <li>Repite el proceso hasta que el jugador sea creado con éxito.</li>
	 * </ol>
	 * 
	 * <h3>Manejo de errores</h3>
	 * Este método no gestiona errores directamente. La validación y manejo de excepciones se delegan 
	 * a los métodos llamados (<b>insertarJugadorSinEquipo</b> y <b>insertarJugadorEnEquiposExistentes</b>).
	 * 
	 * <h3>Dependencias</h3>
	 * Este método depende de:
	 * <ul>
	 *   <li><b>Jugador</b>: Clase que representa un jugador en la base de datos.</li>
	 *   <li><b>insertarJugadorSinEquipo</b>: Método que crea un nuevo jugador sin asignarlo a un equipo.</li>
	 *   <li><b>insertarJugadorEnEquiposExistentes</b>: Método que asigna un jugador existente a un equipo.</li>
	 * </ul>
	 * 
	 * @throws Exception Las excepciones específicas se manejan en los métodos llamados.
	 * @see Jugador
	 * @see insertarJugadorSinEquipo
	 * @see insertarJugadorEnEquiposExistentes
	 * @since 2024
	 */
	public void insertarJugadorConPosiblesEquipo() {
		
		Jugador jugadorNuevo = new Jugador();
		do {
			jugadorNuevo = insertarJugadorSinEquipo();
			if(jugadorNuevo!=null) {
				insertarJugadorEnEquiposExistentes(jugadorNuevo);
			}else {
				System.out.println("Inténtalo nuevamente.");
			}			
		}while(jugadorNuevo == null);

		
		
	}
	
	/**
	 * Inserta un nuevo jugador sin asignarle un equipo inicial en la base de datos.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método permite al usuario ingresar los datos de un nuevo jugador, valida que el ID no 
	 * esté duplicado en la base de datos, y lo almacena sin asociarlo a un equipo. Si el ID 
	 * ingresado ya existe, se notifica al usuario y el proceso finaliza sin insertar un nuevo jugador.
	 * 
	 * <h3>Flujo del método</h3>
	 * <ol>
	 *   <li>Solicita al usuario los datos necesarios para crear un jugador: ID, nombre, apellidos, altura, edad y nacionalidad.</li>
	 *   <li>Verifica si ya existe un jugador en la base de datos con el mismo ID.</li>
	 *   <li>Si el ID no está duplicado, crea el jugador y lo guarda en la base de datos.</li>
	 *   <li>Si ocurre un error, se revierte la transacción y se informa al usuario.</li>
	 * </ol>
	 * 
	 * @return El objeto <b>"Jugador"</b> creado, o <b>"null"</b> si no se realizó la inserción.
	 * @throws InputMismatchException Si el usuario proporciona datos no válidos en los campos numéricos.
	 * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
	 * @throws IllegalStateException Si la transacción falla por un estado incorrecto.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
	 * @since 2024
	 */

	private Jugador insertarJugadorSinEquipo() {
		
		Session s = null;
		Transaction trans = null;
		Scanner sc = new Scanner(System.in);
		int id, alturaCm, edad;
		String nombre, apellidos, nacionalidad;
		Jugador jugadorNuevo= null;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans=s.beginTransaction();
			
			System.out.println("Ingresa el id del nuevo jugador:  ");
			id=sc.nextInt();
			sc.nextLine();//Limpiar el buffer
			System.out.println("Ingresa nombre del jugador: ");
			nombre = sc.nextLine();
			System.out.println("A continuación ingresa sus apellidos: ");
			apellidos = sc.nextLine();
			System.out.println("Ingresa su altura en centímetros: ");
			alturaCm = sc.nextInt();
			sc.nextLine();//Limpiar el buffer
			System.out.println("Ingresa su edad: ");
			edad = sc.nextInt();
			sc.nextLine();//Limpiar el buffer
			System.out.println("Finalmente ingresa su nacionalidad: ");
			nacionalidad = sc.nextLine();
			
			Jugador jugadorEnBase = null;
			jugadorEnBase= s.get(Jugador.class, id);// Verificamos existencia del ID en base de datos
			if(jugadorEnBase!=null) {
				System.out.println("Actualmente existe un jugador en la base de datos con el id "+id+".\n");
			}else {
				jugadorNuevo = new Jugador(id,null,nombre,apellidos,alturaCm,edad,nacionalidad);
				s.save(jugadorNuevo);
				System.out.println("Jugador con id "+id+",insertado exitosamente.\n");
			}
			
			trans.commit();
		
		
		} catch (InputMismatchException e) {
		    System.out.println("Ingreso no válido. Por favor, intente nuevamente.\n");
		    sc.nextLine(); // Limpiar entrada
		    if (trans != null) trans.rollback(); // Revertir transacción por seguridad

		} catch (HibernateException he) {
            System.err.println("Error de Hibernate: " + he.getMessage());
            if (trans != null) trans.rollback(); // Revertir transacción

        } catch (IllegalStateException ise) {
            System.err.println("Error en la transacción: " + ise.getMessage());
            if (trans != null) trans.rollback();

        }catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            if (trans != null) trans.rollback();
        } finally {
            if (s != null) s.close();
            
        }
		
		return jugadorNuevo;
		
	}
	
	
	/**
	 * Asigna un jugador existente a un equipo actual de la base de datos.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método permite al usuario asignar un jugador previamente creado a un equipo existente en la base de datos. 
	 * Si el usuario decide no asignarlo, el proceso puede finalizar sin cambios adicionales en el jugador.
	 * 
	 * <h3>Flujo del método</h3>
	 * <ol>
	 *   <li>Se solicita al usuario si desea asignar el jugador a un equipo.</li>
	 *   <li>Si el usuario acepta, se muestran los equipos disponibles, incluyendo sus IDs, nombres y detalles básicos.</li>
	 *   <li>El usuario selecciona un equipo ingresando su ID, validado contra los IDs de equipos existentes.</li>
	 *   <li>El jugador se asocia al equipo seleccionado y se actualiza en la base de datos.</li>
	 *   <li>Si el usuario decide no asignar un equipo, el proceso se termina sin realizar cambios.</li>
	 * </ol>
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>El método utiliza HQL para listar los equipos actuales en la base de datos y orientar al usuario en su selección.</li>
	 *   <li>El jugador debe haberse creado previamente y no debe tener un equipo asignado al momento de ejecutar este método.</li>
	 *   <li>Los datos de entrada del usuario se validan para asegurar que el ID del equipo seleccionado es válido.</li>
	 * </ul>
	 * 
	 * @param jugadorNuevo El jugador que será asignado a un equipo existente.
	 * @throws InputMismatchException Si el usuario proporciona datos no válidos, como caracteres en lugar de números.
	 * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
	 * @throws IllegalStateException Si la transacción falla debido a un estado incorrecto.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
	 * @since 2024
	 */
	private void insertarJugadorEnEquiposExistentes(Jugador jugadorNuevo) {
		
		String respuesta="",respuestaSegunda="";
		int idSc;
		boolean insercion = false;
		Scanner sc= new Scanner(System.in);
		Session s = null;
		Transaction trans = null;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans=s.beginTransaction();
			do {
				System.out.println("¿Deseas insertar al nuevo jugador en un equipo? : (Si / No).");
				respuesta=sc.nextLine();
				if(respuesta.equalsIgnoreCase("Si")) {
					System.out.println("Los ids de los equipos actuales son los siguientes:");
					Query q = s.createQuery("from Equipo ").setReadOnly(true); // Consulta HQL creada sólo para orientar al usuario, el resto se realiza con consultas predeterminadas.
					List<Equipo> equiposActuales =  q.getResultList();
					List<Integer> idsValidos = new ArrayList<Integer>();
					for(Equipo equipo : equiposActuales) {
						System.out.println("ID : "+equipo.getIdEquipo()+" , nombre: "+equipo.getNombre()+" , localidad: "+equipo.getLocalidad()+" , país: "+equipo.getPais());
						idsValidos.add(equipo.getIdEquipo());
					}
					do {
						System.out.println("Selecciona un id de equipo al cual asignar al nuevo jugador o ingresa 'fin' si no quieres asignarle equipo: ");
						if(sc.hasNextInt()) {
							idSc=sc.nextInt();
							sc.nextLine();//Limpiamos el buffer.
							if(idsValidos.contains(idSc)) {
								Equipo equipo = s.get(Equipo.class, idSc);
								jugadorNuevo.setEquipo(equipo); // Asignamos equipo.
								s.saveOrUpdate(jugadorNuevo); // Realizamos update.
								System.out.println("Jugador con ID "+jugadorNuevo.getIdJugador()+" insertado exitosamente en el equipo con ID "+idSc+" ("+equipo.getNombre()+").\n");
								insercion = true;//Salir del bucle actual
								respuesta="No";//Salir del primer bucle también
							}
						}else {
							respuestaSegunda=sc.nextLine();
							if(respuestaSegunda.equalsIgnoreCase("fin")) {
								break;
							}
						}
					}while(!insercion );
					
				}
				
			}while(!respuesta.equalsIgnoreCase("No"));
			
			trans.commit();
		} catch (InputMismatchException e) {
		    System.out.println("Ingreso no válido. Por favor, intente nuevamente.\n");
		    sc.nextLine(); // Limpiar buffer
		    if (trans != null) trans.rollback(); // Revertir transacción por seguridad

		} catch (HibernateException he) {
            System.err.println("Error de Hibernate: " + he.getMessage());
            if (trans != null) trans.rollback(); // Revertir transacción

        } catch (IllegalStateException ise) {
            System.err.println("Error en la transacción: " + ise.getMessage());
            if (trans != null) trans.rollback();

        }catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            if (trans != null) trans.rollback();
        } finally {
            if (s != null) s.close();
            
        }
		
	}
	
	/**
	 * Gestiona la creación de un nuevo partido y, opcionalmente, la asignación de estadísticas al mismo.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método permite al usuario crear un nuevo partido en la base de datos y, si lo desea, 
	 * añadir estadísticas relacionadas con dicho partido. 
	 * Combina dos procesos: 
	 * <ol>
	 *   <li>Creación del partido sin estadísticas.</li>
	 *   <li>Asociación de estadísticas al partido creado.</li>
	 * </ol>
	 * 
	 * <h3>Flujo del método</h3>
	 * <ol>
	 *   <li>Intenta crear un nuevo partido utilizando el método auxiliar <b>insertarPartidoSinEstadisticas</b>.</li>
	 *   <li>Si el partido se crea con éxito, llama al método <b>insertarEstadisticasEnPartido</b> para gestionar la asignación de estadísticas.</li>
	 *   <li>Si el proceso de creación del partido falla, solicita al usuario que lo intente nuevamente.</li>
	 *   <li>El bucle continúa hasta que se crea un partido válido.</li>
	 * </ol>
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>Este método depende de métodos auxiliares para dividir el flujo de trabajo en pasos manejables.</li>
	 *   <li>El partido debe ser creado antes de que se puedan asociar estadísticas.</li>
	 *   <li>El proceso es interactivo, solicitando entradas del usuario para ambos pasos.</li>
	 * </ul>
	 * 
	 * <h3>Dependencias</h3>
	 * Este método depende de:
	 * <ul>
	 *   <li><b>Partido</b>: Clase que representa un partido en la base de datos.</li>
	 *   <li>Métodos auxiliares: <b>insertarPartidoSinEstadisticas</b> y <b>insertarEstadisticasEnPartido</b>.</li>
	 *   <li><b>HibernateUtil</b>: Clase que gestiona la fábrica de sesiones de Hibernate.</li>
	 * </ul>
	 * 
	 * 
	 * @throws HibernateException Si ocurre un error al interactuar con la base de datos en los métodos auxiliares.
	 * @throws IllegalStateException Si falla la lógica de transacciones en los métodos auxiliares.
	 * @since 2024
	 */
	public void insertarPartidoConPosiblesEstadisticas() {
		
		Partido partidoNuevo = new Partido();
		
		do {
			partidoNuevo = insertarPartidoSinEstadisticas();
			if(partidoNuevo!=null) {
				insertarEstadisticasEnPartido(partidoNuevo);
			}else {
				System.out.println("Inténtalo nuevamente.");
			}			
		}while(partidoNuevo == null);
		
		
	}
	
	/**
	 * Crea un nuevo partido sin estadísticas asociadas y lo almacena en la base de datos.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método permite al usuario registrar un nuevo partido en la base de datos especificando 
	 * los equipos local y visitante, así como los puntos obtenidos por cada equipo.
	 * 
	 * <h3>Flujo del método</h3>
	 * <ol>
	 *   <li>Obtiene una lista de los equipos disponibles en la base de datos y muestra su información al usuario.</li>
	 *   <li>Solicita al usuario seleccionar dos equipos, uno como local y otro como visitante.</li>
	 *   <li>Solicita al usuario ingresar los puntos obtenidos por cada equipo.</li>
	 *   <li>Genera un nuevo objeto <b>Partido</b> con los datos ingresados.</li>
	 *   <li>Asigna un ID único al nuevo partido basándose en el ID del último partido registrado.</li>
	 *   <li>Guarda el nuevo partido en la base de datos utilizando Hibernate.</li>
	 * </ol>
	 * 
	 * <h3>Validaciones</h3>
	 * <ul>
	 *   <li>Valida que los IDs de los equipos ingresados sean válidos y no estén repetidos.</li>
	 *   <li>Valida que los puntos ingresados sean números enteros válidos.</li>
	 * </ul>
	 * 
	 * <h3>Notas importantes</h3>
	 * <ul>
	 *   <li>El partido creado no incluye estadísticas relacionadas; estas se gestionan por separado.</li>
	 *   <li>El método asume que los equipos ya están registrados en la base de datos.</li>
	 *   <li>El método es interactivo y requiere entradas del usuario.</li>
	 *   <li>Utiliza un comparador personalizado <b>ComparadorPartidosPorID</b> para obtener el último partido registrado.</li>
	 * </ul>
	 * 
	 * @return El objeto <b>"Partido"</b> recién creado o <b>"null"</b> si ocurrió un error durante la creación.
	 * @throws InputMismatchException Si se ingresan valores no válidos para IDs o puntos.
	 * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
	 * @throws IllegalStateException Si falla la lógica de transacciones.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
	 * @since 2024
	 */
	private Partido insertarPartidoSinEstadisticas() {
		
		Partido partidoNuevo = null , ultimoPartido = null;
		Session s = null;
		Transaction trans = null;
		Scanner sc= new Scanner(System.in);
		Equipo equipoLocal = null;
		Equipo equipoVisitante = null;
		Integer idSc , puntosLocal = null , puntosVisitante = null;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			System.out.println("Para ingresar un nuevo partido debes seleccionar de la lista un ID para el equipo local , y luego un ID para el equipo visitante:");
			Query q = s.createQuery("from Equipo").setReadOnly(true);
			List<Equipo> listaEquiposActuales = q.getResultList();
			
			List<Integer> idsValidos = new ArrayList<Integer>();// Almacena IDs válidos.
			for (Equipo equipoFE : listaEquiposActuales) {
				System.out.println("ID equipo: "+equipoFE.getIdEquipo()+" , nombre: "+equipoFE.getNombre()+" , localidad: "+equipoFE.getLocalidad()+" , país:"+equipoFE.getPais()+".");
				idsValidos.add(equipoFE.getIdEquipo());
			}
			
			List<Integer> idsSeleccionados = new ArrayList<Integer>(); // Almacena IDs seleccionados por el usuario.
			System.out.println("\nSelecciona a continuación 2 IDs válidos no repetidos, el primero será el equipo local y el segundo el visitante:");
			do {
				if(sc.hasNextInt()) {
					idSc = sc.nextInt();
					sc.nextLine();
					if(idsValidos.contains(idSc) && !idsSeleccionados.contains(idSc)) {//Se verifica que el ID ingresado sea válido y no repetido.
						idsSeleccionados.add(idSc);
					}
				}else {
					sc.nextLine();//Limpio el buffer
				}
			}while(idsSeleccionados.size()!=2);
			System.out.println("Los IDs seleccionados son : ");
			for(Integer idFE : idsSeleccionados) {
				System.out.print(idFE+" ");
				for (Equipo equipoFE : listaEquiposActuales) {
					if(idFE == equipoFE.getIdEquipo()) {
						if(equipoLocal==null) {
							equipoLocal=equipoFE;
						}else {
							equipoVisitante=equipoFE;
						}
					}
				}
			}
			
			System.out.println("\nA continuación vamos a insertar los puntos que hizo el equipo local '"+equipoLocal.getNombre()+"'(ID:"+equipoLocal.getIdEquipo()+"): ");
			do {
				if(sc.hasNextInt()) {
					puntosLocal = sc.nextInt();
				}else {
					sc.nextLine();//Limpio el buffer
				}
			}while(puntosLocal == null);
			System.out.println("Finalmente vamos a insertar los puntos que hizo el equipo visitante '"+equipoVisitante.getNombre()+"'(ID:"+equipoVisitante.getIdEquipo()+"): ");
			do {
				if(sc.hasNextInt()) {
					puntosVisitante = sc.nextInt();
				}else {
					sc.nextLine();//Limpio el buffer
				}
			}while(puntosVisitante == null);
			
			//Finalmente vamos a buscar el último id partido para insertar a continuación de él
			Query q2 = s.createQuery("from Partido").setReadOnly(true);
			List<Partido> listaPartidosActuales = q2.getResultList();
			listaPartidosActuales.sort(new ComparadorPartidosPorID());
			ultimoPartido = listaPartidosActuales.get(listaPartidosActuales.size()-1); // obtenemos aquí el último partido
			
			//Construimos el Partido sin estadísticas 
			partidoNuevo = new Partido(ultimoPartido.getIdPartido()+1,equipoVisitante,equipoLocal,puntosLocal,puntosVisitante);
			
			s.save(partidoNuevo);
			System.out.println("Partido insertado exitosamente.\nCon ID :"+partidoNuevo.getIdPartido()+" , equipo local: "+partidoNuevo.getEquipoByIdEquipoLocal().getNombre()+" , equipo visitante : "+partidoNuevo.getEquipoByIdEquipoVisitante().getNombre()+" , puntos local: "+partidoNuevo.getPuntosLocal()+" , puntos visitante: "+partidoNuevo.getPuntosVisitante()+".\n");
			
			
			trans.commit();
		} catch (InputMismatchException e) {
		    System.out.println("Ingreso no válido. Por favor, intente nuevamente.\n");
		    sc.nextLine(); // Limpiar buffer
		    if (trans != null) trans.rollback(); // Revertir transacción por seguridad

		} catch (HibernateException he) {
            System.err.println("Error de Hibernate: " + he.getMessage());
            if (trans != null) trans.rollback(); // Revertir transacción

        } catch (IllegalStateException ise) {
            System.err.println("Error en la transacción: " + ise.getMessage());
            if (trans != null) trans.rollback();

        }catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            if (trans != null) trans.rollback();
        } finally {
            if (s != null) s.close();
            
        }
		
		return partidoNuevo;
		
	}
	
	/**
	 * Inserta estadísticas para los jugadores de un partido específico en la base de datos.
	 * 
	 * <h3>Descripción general</h3>
	 * Este método permite al usuario registrar estadísticas individuales para los jugadores
	 * de los equipos local y visitante en un partido recién creado. Las estadísticas se almacenan
	 * en la base de datos asociadas al partido y a los jugadores correspondientes.
	 * 
	 * <h3>Flujo del método</h3>
	 * <ol>
	 *   <li>Pregunta al usuario si desea agregar estadísticas para el partido.</li>
	 *   <li>Si el usuario responde "Sí":
	 *     <ul>
	 *       <li>Obtiene los equipos local y visitante del partido proporcionado.</li>
	 *       <li>Llama al método <b>insertarEstadisticasParaEquipo</b> para registrar las estadísticas de los jugadores del equipo local.</li>
	 *       <li>Llama al mismo método para registrar las estadísticas de los jugadores del equipo visitante.</li>
	 *       <li>Guarda todas las estadísticas recopiladas en la base de datos.</li>
	 *     </ul>
	 *   </li>
	 *   <li>Si el usuario responde "No", se finaliza el proceso sin realizar cambios.</li>
	 * </ol>
	 * 
	 * <h3>Dependencias</h3>
	 * Este método depende de:
	 * <ul>
	 *   <li><b>insertarEstadisticasParaEquipo</b>: Método para recopilar estadísticas individuales de los jugadores de un equipo.</li>
	 *   <li><b>Partido</b>: Clase que representa un partido en la base de datos.</li>
	 *   <li><b>Equipo</b>: Clase que representa un equipo en la base de datos.</li>
	 *   <li><b>Datosjugadorpartido</b>: Clase que representa las estadísticas de un jugador en un partido.</li>
	 *   <li><b>HibernateUtil</b>: Clase que gestiona la fábrica de sesiones de Hibernate.</li>
	 * </ul>
	 * 
	 * @param partidoNuevo El partido para el cual se insertarán las estadísticas.
	 * @throws InputMismatchException Si el usuario ingresa datos no válidos.
	 * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
	 * @throws IllegalStateException Si falla la lógica de las transacciones.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
	 * @since 2024
	 */
	private void insertarEstadisticasEnPartido(Partido partidoNuevo) {
		
		String respuesta="";
		int idLocal, idVisitante;
		Session s = null;
		Transaction trans = null;
		Scanner sc = new Scanner(System.in);
		Equipo equipoLocal = null;
		Equipo equipoVisitante = null;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans=s.beginTransaction();
			do {
				System.out.println("¿Deseas insertar estadísticas al nuevo partido? : (Si / No).");
				respuesta=sc.nextLine().trim();
				if(respuesta.equalsIgnoreCase("Si")) {
					idLocal = partidoNuevo.getEquipoByIdEquipoLocal().getIdEquipo();
					equipoLocal= s.get(Equipo.class, idLocal);
					idVisitante = partidoNuevo.getEquipoByIdEquipoVisitante().getIdEquipo();
					equipoVisitante = s.get(Equipo.class, idVisitante);
					List<Datosjugadorpartido> listaDeDatosJugadoresAInsertarLocal = new ArrayList<Datosjugadorpartido>();
					List<Datosjugadorpartido> listaDeDatosJugadoresAInsertarVisitante = new ArrayList<Datosjugadorpartido>();
					
	                // Insertar estadísticas para jugadores del equipo local
					System.out.println("  A continuación vamos a insertar estadísticas para los jugadores del equipo local '"+equipoLocal.getNombre()+"'(ID: "+equipoLocal.getIdEquipo()+"): ");
					listaDeDatosJugadoresAInsertarLocal=insertarEstadisticasParaEquipo(equipoLocal,partidoNuevo, sc );

	                // Insertar estadísticas para jugadores del equipo visitante
	                System.out.println("Finalmente vamos a insertar estadísticas para los jugadores del equipo visitante '"+equipoVisitante.getNombre()+"'(ID: "+equipoVisitante.getIdEquipo()+"): ");
	                listaDeDatosJugadoresAInsertarVisitante=insertarEstadisticasParaEquipo(equipoVisitante,partidoNuevo, sc);
	                
	                if (listaDeDatosJugadoresAInsertarLocal.size()>0) {
	                	for(Datosjugadorpartido datosJugadorEnPartidosLocales : listaDeDatosJugadoresAInsertarLocal) {
		                	s.save(datosJugadorEnPartidosLocales);
		                }
	                }else {
	                	System.out.println("El equipo local no posee jugadores.");
	                }
	                
	                if(listaDeDatosJugadoresAInsertarVisitante.size()>0) {
	                	for(Datosjugadorpartido datosJugadorEnPartidosVisitantes : listaDeDatosJugadoresAInsertarVisitante) {
		                	s.save(datosJugadorEnPartidosVisitantes);
		                }
	                }else {
	                	System.out.println("El equipo visitante no posee jugadores.");
	                }
	                
	                System.out.println("Estadísticas guardadas satisfactoriamente.");
	                break;
				}
				
			}while(!respuesta.equalsIgnoreCase("No"));
			
			trans.commit();
			
		} catch (InputMismatchException e) {
		    System.out.println("Ingreso no válido. Por favor, intente nuevamente.\n");
		    sc.nextLine(); // Limpiar buffer
		    if (trans != null) trans.rollback(); // Revertir transacción por seguridad

		} catch (HibernateException he) {
            System.err.println("Error de Hibernate: " + he.getMessage());
            if (trans != null) trans.rollback(); // Revertir transacción

        } catch (IllegalStateException ise) {
            System.err.println("Error en la transacción: " + ise.getMessage());
            if (trans != null) trans.rollback();

        }catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            if (trans != null) trans.rollback();
        } finally {
            if (s != null) s.close();
            
        }
	}
	
    /**
     * Inserta estadísticas individuales para los jugadores de un equipo en un partido específico.
     * 
     * <h3>Descripción general</h3>
     * Este método permite al usuario ingresar estadísticas individuales para cada jugador de un equipo 
     * en un partido específico. Se solicita al usuario que proporcione datos como la valoración del jugador, 
     * puntos, asistencias, rebotes, tapones y si el jugador fue titular. La información se almacena en un 
     * objeto de tipo <b>"Datosjugadorpartido"</b> que se añade a una lista.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>Itera sobre los jugadores del equipo proporcionado.</li>
     *   <li>Solicita al usuario que ingrese las estadísticas de cada jugador.</li>
     *   <li>Genera un objeto <b>"Datosjugadorpartido"</b> con las estadísticas y lo agrega a una lista.</li>
     *   <li>Devuelve la lista de objetos <b>"Datosjugadorpartido"</b> con las estadísticas de todos los jugadores.</li>
     * </ol>
     * 
     *<h3>Validaciones</h3>
     * Durante el proceso de inserción de estadísticas, se realizan las siguientes validaciones:
     * <ul>
     *   <li><b>Valoración:</b> Se solicita un valor decimal. Si el usuario ingresa un valor no numérico, se repite la solicitud hasta obtener un número válido.</li>
     *   <li><b>Puntos:</b> Se solicita un valor entero. Si el usuario ingresa un valor no numérico, se repite la solicitud hasta obtener un número entero válido.</li>
     *   <li><b>Asistencias:</b> Se solicita un valor entero. Si el usuario ingresa un valor no numérico, se repite la solicitud hasta obtener un número entero válido.</li>
     *   <li><b>Rebotes:</b> Se solicita un valor entero. Si el usuario ingresa un valor no numérico, se repite la solicitud hasta obtener un número entero válido.</li>
     *   <li><b>Tapones:</b> Se solicita un valor entero. Si el usuario ingresa un valor no numérico, se repite la solicitud hasta obtener un número entero válido.</li>
     *   <li><b>Titular:</b> Se solicita una respuesta booleana ("Sí" o "No"). Si el usuario ingresa un valor diferente, se repite la solicitud hasta obtener una respuesta válida.</li>
     * </ul>
     * 
     * @param equipo El equipo cuyos jugadores tendrán estadísticas insertadas en el partido.
     * @param partidoNuevo El partido en el cual se registrarán las estadísticas de los jugadores.
     * @param sc El objeto <b>"Scanner"</b> para leer las entradas del usuario.
     * @return Una lista de objetos <b>"Datosjugadorpartido"</b> que contienen las estadísticas de los jugadores del equipo.
     * @throws InputMismatchException Si el usuario ingresa datos no válidos.
     * @since 2024
     */
	private List<Datosjugadorpartido> insertarEstadisticasParaEquipo(Equipo equipo,Partido partidoNuevo, Scanner sc) {
		Set<Jugador> setJugadores = equipo.getJugadors();
		List<Datosjugadorpartido> listaDeDatosJugadoresAInsertar = new ArrayList<Datosjugadorpartido>();
		
		for (Jugador jugador : setJugadores) {
	        System.out.println("Insertar estadísticas para el jugador: " + jugador.getNombre() + " " + jugador.getApellidos() + " (ID: " + jugador.getIdJugador() + ").Equipo :"+jugador.getEquipo().getNombre());

	        double valoracion = leerValorDouble("Ingresa una valoración para el jugador en número con decimales:", sc);
	        int puntos = leerValorEntero("Ingresa los puntos que realizó el jugador en el partido:", sc);
	        int asistencias = leerValorEntero("Ingresa las asistencias que realizó el jugador en el partido:", sc);
	        int rebotes = leerValorEntero("Ingresa los rebotes que realizó el jugador en el partido:", sc);
	        int tapones = leerValorEntero("Ingresa los tapones que realizó el jugador en el partido:", sc);
	        boolean titular = leerValorBoolean("¿El jugador fue titular en el partido? (Si / No):", sc);

	        
	        DatosjugadorpartidoId datosPartidoID= new DatosjugadorpartidoId(jugador.getIdJugador(),partidoNuevo.getIdPartido());
	        Datosjugadorpartido datosJugadorEnPartido= new Datosjugadorpartido(datosPartidoID,jugador,partidoNuevo,valoracion,puntos,asistencias,rebotes,tapones,titular);
	        
	        listaDeDatosJugadoresAInsertar.add(datosJugadorEnPartido);
	        //s.save(datosJugadorEnPartido);
	    }
		return listaDeDatosJugadoresAInsertar;
	}

    /**
     * Lee un valor decimal positivo introducido por el usuario.
     * 
     * <h3>Descripción general</h3>
     * Este método solicita al usuario que ingrese un número decimal positivo, asegurándose de que
     * la entrada sea válida. Si el valor ingresado no es un número decimal positivo, el método 
     * repetirá la solicitud hasta recibir una entrada válida.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>Se muestra el mensaje proporcionado al usuario.</li>
     *   <li>El usuario debe ingresar un número decimal positivo.</li>
     *   <li>Si la entrada no es válida (no es un número decimal positivo), se solicita al usuario
     *       que lo intente de nuevo.</li>
     *   <li>El proceso se repite hasta que el valor ingresado sea válido.</li>
     *   <li>El valor válido se devuelve como resultado del método.</li>
     * </ol>
     * 
     * @param mensaje El mensaje que se mostrará al usuario solicitando la entrada del valor decimal.
     * @param sc El objeto <b>"Scanner"</b> para leer la entrada del usuario.
     * @return El valor decimal positivo ingresado por el usuario.
     * @throws InputMismatchException Si el usuario ingresa un valor no válido.
     * @since 2024
     */
	private double leerValorDouble(String mensaje, Scanner sc) {
	    double valor = -1;
	    while (valor < 0) {
	        System.out.println(mensaje);
	        if (sc.hasNextDouble()) {
	            valor = sc.nextDouble();
	            sc.nextLine(); // Limpiar buffer
	        } else {
	            System.out.println("Entrada no válida. Ingresa un número decimal positivo.");
	            sc.nextLine(); // Limpiar buffer
	        }
	    }
	    return valor;
	}

    /**
     * Lee un valor entero positivo introducido por el usuario.
     * 
     * <h3>Descripción general</h3>
     * Este método solicita al usuario que ingrese un número entero positivo, asegurándose de que
     * la entrada sea válida. Si el valor ingresado no es un número entero positivo, el método 
     * repetirá la solicitud hasta recibir una entrada válida.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>Se muestra el mensaje proporcionado al usuario.</li>
     *   <li>El usuario debe ingresar un número entero positivo.</li>
     *   <li>Si la entrada no es válida (no es un número entero positivo o "0"), se solicita al usuario que lo intente de nuevo.</li>
     *   <li>El proceso se repite hasta que el valor ingresado sea válido.</li>
     *   <li>El valor válido se devuelve como resultado del método.</li>
     * </ol>
     * 
     * @param mensaje El mensaje que se mostrará al usuario solicitando la entrada del valor entero.
     * @param sc El objeto <b>"Scanner"</b> para leer la entrada del usuario.
     * @return El valor entero positivo ingresado por el usuario.
     * @throws InputMismatchException Si el usuario ingresa un valor no válido.
     * @since 2024
     */
	public int leerValorEntero(String mensaje, Scanner sc) {
	    int valor = -1;
	    while (valor < 0) {
	        System.out.println(mensaje);
	        if (sc.hasNextInt()) {
	            valor = sc.nextInt();
	            sc.nextLine(); // Limpiar buffer
	        } else {
	            System.out.println("Entrada no válida. Ingresa un número entero positivo.");
	            sc.nextLine(); // Limpiar buffer
	        }
	    }
	    return valor;
	}

    /**
     * Lee un valor textual ingresado por el usuario, esperando las respuestas "Si" o "No".
     * 
     * <h3>Descripción general</h3>
     * Este método solicita al usuario que ingrese una respuesta "Si" o "No". Si el usuario ingresa
     * una de estas respuestas, el método devuelve un valor booleano correspondiente. Si el usuario
     * ingresa cualquier otro valor, se le solicita que ingrese una respuesta válida ("Si" o "No").
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>Se muestra el mensaje proporcionado al usuario.</li>
     *   <li>El usuario debe ingresar "Si" o "No".</li>
     *   <li>Si la respuesta es "Si", el método devuelve <b>true</b>.</li>
     *   <li>Si la respuesta es "No", el método devuelve <b>false</b>.</li>
     *   <li>Si la entrada no es válida, se solicita al usuario que ingrese una respuesta válida.</li>
     *   <li>El proceso se repite hasta que el usuario ingrese una respuesta válida.</li>
     * </ol>
     * 
     * @param mensaje El mensaje que se mostrará al usuario solicitando la entrada del valor booleano.
     * @param sc El objeto <b>"Scanner"</b> para leer la entrada del usuario.
     * @return <b>true</b> si el usuario ingresa "Si", <b>false</b> si ingresa "No".
     * @throws InputMismatchException Si el usuario ingresa un valor no válido.
     * @since 2024
     */
	private boolean leerValorBoolean(String mensaje, Scanner sc) {
	    String respuesta;
	    while (true) {
	        System.out.println(mensaje);
	        respuesta = sc.nextLine();
	        if (respuesta.equalsIgnoreCase("Si")) {
	            return true;
	        } else if (respuesta.equalsIgnoreCase("No")) {
	            return false;
	        } else {
	            System.out.println("Entrada no válida. Por favor, ingresa 'Si' o 'No'.");
	        }
	    }
	}	
	
    /**
     * Permite insertar estadísticas de un jugador en un partido específico.
     * 
     * <h3>Descripción general</h3>
     * Este método permite al usuario seleccionar un jugador que no tenga equipo asignado y luego 
     * seleccionar un partido en el que el jugador haya participado. A continuación, el usuario puede 
     * ingresar las estadísticas del jugador para ese partido.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>El método solicita al usuario que seleccione un jugador que no esté asignado a ningún equipo.</li>
     *   <li>Si el jugador seleccionado es válido (no es nulo), el proceso continúa.</li>
     *   <li>El usuario luego selecciona un partido y proporciona las estadísticas correspondientes del jugador.</li>
     *   <li>Si no se encuentra un jugador válido, el proceso se repite hasta que se seleccione uno.</li>
     * </ol>
     * 
     * <h3>Dependencias</h3>
     * Este método depende de:
     * <ul>
     *   <li><b>seleccionarJugadorSinEquipo</b>: Método que permite seleccionar un jugador sin equipo asignado.</li>
     *   <li><b>seleccionarPartidoYDarEstadisticas</b>: Método que permite seleccionar un partido y registrar las estadísticas del jugador en ese partido.</li>
     *   <li><b>Jugador</b>: Clase que representa a un jugador en la base de datos.</li>
     *   <li><b>Partido</b>: Clase que representa un partido en la base de datos.</li>
     * </ul>
     * 
     * @throws InputMismatchException Si el usuario ingresa datos no válidos.
     * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
     * @throws IllegalStateException Si falla la lógica de las transacciones.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
     * @since 2024
     */
	public void insertarEstadisticasAJugadorEnPartido(){
		
		Jugador jugador = new Jugador();
		
		do {
			jugador = seleccionarJugadorSinEquipo();
			if(jugador!=null) {
				seleccionarPartidoYDarEstadisticas(jugador);
			}else {
				System.out.println("Inténtalo nuevamente.");
			}			
		}while(jugador == null);
	}
	
    /**
     * Permite seleccionar un jugador que actualmente no está asignado a ningún equipo y asignarlo a uno.
     * 
     * <h3>Descripción general</h3>
     * Este método consulta la base de datos para obtener los jugadores que no están asignados a ningún equipo.
     * Luego, muestra los jugadores disponibles y permite al usuario seleccionar uno. Posteriormente, el usuario 
     * puede asignar el jugador seleccionado a un equipo válido de los existentes en la base de datos.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>El método consulta la base de datos para obtener una lista de jugadores que no tienen equipo asignado.</li>
     *   <li>Si existen jugadores sin equipo, se muestran sus IDs y nombres para que el usuario seleccione uno.</li>
     *   <li>Una vez que se selecciona un jugador, el método muestra los equipos disponibles y permite al usuario 
     *       asignar el jugador seleccionado a uno de estos equipos.</li>
     *   <li>Si el ID del equipo seleccionado es válido, el jugador se asigna al equipo y la transacción se guarda en la base de datos.</li>
     *   <li>Si no se encuentra un jugador sin equipo, o si el jugador no puede ser asignado a un equipo, el proceso se repite.</li>
     * </ol>
     * 
     * <h3>Validaciones</h3>
     * <p>El método realiza las siguientes validaciones:</p>
     * <ul>
     *   <li><b>Validación de jugadores sin equipo:</b> Se verifica que existan jugadores sin equipo en la base de datos. 
     *       Si no existen, se muestra un mensaje informando que no hay jugadores disponibles.</li>
     *   <li><b>Validación de ID de jugador:</b> Se asegura de que el ID ingresado por el usuario corresponda a un jugador 
     *       sin equipo. Si el ID no es válido, se solicita al usuario que ingrese un ID correcto.</li>
     *   <li><b>Validación de equipo seleccionado:</b> Se valida que el ID del equipo ingresado por el usuario corresponda 
     *       a un equipo existente. Si el ID no es válido, se solicita al usuario que ingrese un ID de equipo válido.</li>
     * </ul>
     * 
     * @return El jugador seleccionado y asignado a un equipo, o <code>null</code> si no se selecciona ningún jugador.
     * @throws InputMismatchException Si el usuario ingresa un dato no válido durante la selección de jugador o equipo.
     * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
     * @throws IllegalStateException Si falla la lógica de las transacciones durante la selección o asignación.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
     * @since 2024
     */
	private Jugador seleccionarJugadorSinEquipo() {
		Scanner sc = new Scanner(System.in);
		int idSc;
		Session s = null;
		Transaction trans = null;
		Jugador jugadorSeleccionado = null;
		Equipo equipoSeleccionado = null;
		boolean insercion = false;
		
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans=s.beginTransaction();
			
			System.out.println("Para insertar estadísticas primero vamos a seleccionar un jugador sin equipo.");
			System.out.println("Los jugadores que actualmente se encuentran sin equipo son :");
			Query q = s.createQuery("from Jugador where idEquipo = null"); 
			List<Jugador> jugadoresSinEquipo =  q.getResultList();
               
			// Mostrar los jugadores encontrados que no tienen equipo asignado
            if (jugadoresSinEquipo.isEmpty()) {
            	System.out.println("No hay jugadores sin equipo.");
            } else {
            	List<Integer> idsExistentes = new ArrayList<Integer>();
            	for (Jugador jugador : jugadoresSinEquipo) {
            		System.out.println("ID: " + jugador.getIdJugador() + ", Nombre: " + jugador.getNombre());
            		idsExistentes.add(jugador.getIdJugador()); // Creamos una lista con los ids existentes
            	}
            	Set<Integer> setIds =new HashSet<Integer>(); ;
            	System.out.println("A continuación inserta un ID válido correspondiente al jugador que deseas añadir a un equipo, para luego brindarle estadísticas:");
            	do {
                    	
            		if(sc.hasNextInt()) {
            			idSc = sc.nextInt();
            			for (Integer idAVerificar : idsExistentes) {
            				if(idSc == idAVerificar) {
            					setIds.add(idSc);
            				}
            			}
            		}else {
            			sc.nextLine();
            		}
            	}while(setIds.size()!=1);
            	
            	System.out.println("\nEl ID seleccionado corresponde con :");
            	for (Integer iD : setIds) {
            		jugadorSeleccionado=s.get(Jugador.class, iD);
            		System.out.print("Jugador ID: "+jugadorSeleccionado.getIdJugador()+" , nombre:"+jugadorSeleccionado.getNombre()+" ,apellidos: "+jugadorSeleccionado.getApellidos()+".");
            	}
            	System.out.println("\n\nA continuación lo insertaremos en un Equipo. ");
				System.out.println("Los IDs de los equipos actuales son los siguientes:");
				Query q2 = s.createQuery("from Equipo ");
				List<Equipo> equiposActuales =  q2.getResultList();
				List<Integer> idsValidos = new ArrayList<Integer>();
				for(Equipo equipo : equiposActuales) {
					System.out.println("ID : "+equipo.getIdEquipo()+" , nombre: "+equipo.getNombre()+" , localidad: "+equipo.getLocalidad()+" , país: "+equipo.getPais());
					idsValidos.add(equipo.getIdEquipo());
				}
				do {
					
					System.out.println("Selecciona un ID de equipo al cual asignar al jugador: ");
					if(sc.hasNextInt()) {
						idSc=sc.nextInt();
						sc.nextLine();//Limpiamos el buffer.
						if(idsValidos.contains(idSc)) {
							equipoSeleccionado = s.get(Equipo.class, idSc);
							jugadorSeleccionado.setEquipo(equipoSeleccionado); // Asignamos equipo
							s.saveOrUpdate(jugadorSeleccionado); // Realizamos update
							insercion = true;
							System.out.println("Jugador con ID "+jugadorSeleccionado.getIdJugador()+" insertado exitosamente en el equipo con ID "+idSc+" ("+equipoSeleccionado.getNombre()+").\n");
							
						}else {
							System.out.println("El ID ingresado no corresponde a ningún equipo válido. Intenta nuevamente.");
						}
					}else {
				        System.out.println("Entrada no válida. Por favor, ingresa un número.");
				        sc.next(); // Consume la entrada inválida
					}
					
				}while(!insercion );
            }  
            
			trans.commit();
			
        } catch (HibernateException he) {
            System.err.println("Error de Hibernate: " + he.getMessage());
            if (trans != null) trans.rollback(); // Revertir transacción

        } catch (IllegalStateException ise) {
            System.err.println("Error en la transacción: " + ise.getMessage());
            if (trans != null) trans.rollback();

        }catch(Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            if (trans != null) trans.rollback();
        } finally {
            if (s != null) s.close();
            
        }
		return jugadorSeleccionado;
	}
	
    /**
     * Permite seleccionar un partido en el que el equipo asignado al jugador haya participado y registrar sus estadísticas en dicho partido.
     * 
     * <h3>Descripción general</h3>
     * Este método muestra los partidos en los que el equipo asignado al nuevo jugador ha participado, tanto como equipo local como visitante.
     * Luego, permite al usuario seleccionar uno de esos partidos y registrar estadísticas para el jugador en ese partido.
     * Las estadísticas se guardan en la base de datos una vez completadas.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>Se obtiene el equipo al que pertenece el jugador.</li>
     *   <li>Se muestran los partidos en los que el equipo ha participado, tanto como local como visitante.</li>
     *   <li>El usuario selecciona un partido válido (por ID) en el que desea registrar estadísticas para el jugador.</li>
     *   <li>Una vez seleccionado el partido, el método solicita al usuario las estadísticas del jugador para ese partido.</li>
     *   <li>Las estadísticas del jugador se guardan en la base de datos asociadas al partido correspondiente.</li>
     * </ol>
     * 
     * 
     * @param jugador El jugador para el cual se van a registrar las estadísticas.
     * @throws InputMismatchException Si el usuario ingresa un dato no válido durante la selección de partido o estadísticas.
     * @throws HibernateException Si ocurre un error al interactuar con la base de datos.
     * @throws IllegalStateException Si falla la lógica de las transacciones.
     * @throws Exception Si ocurre un error inesperado durante el proceso.
     * @since 2024
     */
	private void seleccionarPartidoYDarEstadisticas(Jugador jugador) {
		Scanner sc = new Scanner(System.in);
		int idSc;
		Session s = null;
		Transaction trans = null;
		Partido partidoSeleccionado = null;
		Datosjugadorpartido estadisticas = null;
		boolean insercion = false;
		
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			if(jugador!=null) {
				
				Equipo equipo = s.get(Equipo.class, jugador.getEquipo().getIdEquipo());
				
				System.out.println("Para continuar vamos a insertar estadísticas del jugador con ID "+jugador.getIdJugador()+" en un partido del equipo.");
				System.out.println("Los partidos que jugó el equipo actualmente son: ");
	
				// Obtener los partidos como equipo local
		        Set<Partido> partidosLocal = equipo.getPartidosForIdEquipoLocal();
		        List<Integer> idsValidos = new ArrayList<Integer>();
		        System.out.println("  Partidos como equipo local:");
		        for (Partido partido : partidosLocal) {
		            System.out.println("ID Partido: " + partido.getIdPartido()+" , equipos que jugaron : "+partido.getEquipoByIdEquipoLocal().getNombre()+" "+partido.getPuntosLocal()+" - "+partido.getPuntosVisitante()+" "+partido.getEquipoByIdEquipoVisitante().getNombre()+".");
		            idsValidos.add(partido.getIdPartido());
		        }  
		        // Obtener los partidos como equipo visitante
		        Set<Partido> partidosVisitante = equipo.getPartidosForIdEquipoVisitante();
		        System.out.println("\n  Partidos como equipo visitante:");
		        for (Partido partido : partidosVisitante) {
		            System.out.println("ID Partido: " + partido.getIdPartido()+" , equipos que jugaron : "+partido.getEquipoByIdEquipoLocal().getNombre()+" "+partido.getPuntosLocal()+" - "+partido.getPuntosVisitante()+" "+partido.getEquipoByIdEquipoVisitante().getNombre()+".");
		            idsValidos.add(partido.getIdPartido());
				}
		        
				do {
					
					System.out.println("\n  Selecciona un ID válido de partido para introducir estadísticas en él: ");
					if(sc.hasNextInt()) {
						idSc=sc.nextInt();
						sc.nextLine();//Limpiamos el buffer.
						if(idsValidos.contains(idSc)) {
							partidoSeleccionado = s.get(Partido.class, idSc);
							//Aqui llamo a "insertarEstadisticasParaPartido" para construir las estadísticas
							estadisticas = insertarEstadisticasParaPartido(jugador,partidoSeleccionado, sc);
							s.saveOrUpdate(estadisticas); 
							insercion = true;
							System.out.println("Las estadisticas para el jugador con ID: "+jugador.getIdJugador()+" insertado exitosamente en el partido con ID: "+ partidoSeleccionado.getIdPartido()+" , equipos que jugaron : "+partidoSeleccionado.getEquipoByIdEquipoLocal().getNombre()+" "+partidoSeleccionado.getPuntosLocal()+" - "+partidoSeleccionado.getPuntosVisitante()+" "+partidoSeleccionado.getEquipoByIdEquipoVisitante().getNombre()+".");
							
						}else {
							System.out.println("El ID ingresado no corresponde a ningún partido válido. Intenta nuevamente.");
						}
					}else {
				        System.out.println("Entrada no válida. Por favor, ingresa un número.");
				        sc.next(); // Consume la entrada inválida
					}
					
				}while(!insercion ); 
				
			}else {
				System.out.println("No hay resultado para el id brindado.");
			}
			
			trans.commit();
			
	    } catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	    	if (trans != null) trans.rollback();
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }

	}
	
    /**
     * Registra las estadísticas de un jugador en un partido específico.
     * 
     * <h3>Descripción general</h3>
     * Este método solicita al usuario que ingrese diversas estadísticas relacionadas con un jugador en un partido. 
     * Las estadísticas incluyen valoración, puntos, asistencias, rebotes, tapones y si el jugador fue titular en el partido. 
     * Los datos ingresados se utilizan para crear una nueva instancia de la clase <b>"Datosjugadorpartido"</b> que representa las estadísticas de 
     * ese jugador en ese partido específico.
     * 
     * <h3>Flujo del método</h3>
     * <ol>
     *   <li>Se muestra un mensaje indicando el nombre del jugador y el equipo al que pertenece.</li>
     *   <li>Se solicitan las estadísticas del jugador para el partido específico:</li>
     *     <ul>
     *       <li>Valoración (número decimal).</li>
     *       <li>Puntos (número entero).</li>
     *       <li>Asistencias (número entero).</li>
     *       <li>Rebotes (número entero).</li>
     *       <li>Tapones (número entero).</li>
     *       <li>Si el jugador fue titular en el partido (booleano).</li>
     *     </ul>
     *   <li>Se crea una nueva instancia de la clase <b>"Datosjugadorpartido"</b> con los datos ingresados.</li>
     *   <li>Se devuelve la instancia de <b>"Datosjugadorpartido"</b> que contiene las estadísticas del jugador para el partido.</li>
     * </ol>
     * 
     * <h3>Validaciones</h3>
     * <p>El método realiza las siguientes validaciones:</p>
     * <ul>
     *   <li><b>Valoración:</b> Debe ser un número decimal mayor o igual a 0.</li>
     *   <li><b>Puntos, asistencias, rebotes, tapones:</b> Deben ser valores enteros no negativos.</li>
     *   <li><b>Jugador titular:</b> El usuario debe ingresar 'Si' o 'No' para indicar si el jugador fue titular.</li>
     * </ul>
     * 
     * <h3>Dependencias</h3>
     * Este método depende de:
     * <ul>
     *   <li><b>Jugador</b>: Clase que representa a un jugador en la base de datos.</li>
     *   <li><b>Partido</b>: Clase que representa un partido en la base de datos.</li>
     *   <li><b>Datosjugadorpartido</b>: Clase que representa las estadísticas de un jugador en un partido.</li>
     *   <li><b>DatosjugadorpartidoId</b>: Clase que representa la clave primaria compuesta de un jugador y un partido.</li>
     *   <li><b>leerValorDouble</b>, <b>leerValorEntero</b>, <b>leerValorBoolean</b>: Métodos auxiliares para la validación de los valores ingresados.</li>
     * </ul>
     * 
     * @param jugador El jugador para el cual se van a registrar las estadísticas.
     * @param partido El partido en el cual se van a registrar las estadísticas del jugador.
     * @param sc El objeto <b>"Scanner"</b> utilizado para leer los datos de entrada del usuario.
     * @return La instancia de <b>"Datosjugadorpartido"</b> con las estadísticas ingresadas.
     * @throws InputMismatchException Si el usuario ingresa datos no válidos durante la captura de estadísticas.
     * @throws IllegalStateException Si ocurre un error en la creación de las estadísticas.
     * @since 2024
     */
	private Datosjugadorpartido insertarEstadisticasParaPartido(Jugador jugador,Partido partido, Scanner sc) {
		
		System.out.println("Insertar estadísticas para el jugador: " + jugador.getNombre() + " " + jugador.getApellidos() + " (ID: " + jugador.getIdJugador() + ").Equipo :"+jugador.getEquipo().getNombre());

		double valoracion = leerValorDouble("Ingresa una valoración para el jugador en número con decimales:", sc);
		int puntos = leerValorEntero("Ingresa los puntos que realizó el jugador en el partido:", sc);
		int asistencias = leerValorEntero("Ingresa las asistencias que realizó el jugador en el partido:", sc);
		int rebotes = leerValorEntero("Ingresa los rebotes que realizó el jugador en el partido:", sc);
		int tapones = leerValorEntero("Ingresa los tapones que realizó el jugador en el partido:", sc);
		boolean titular = leerValorBoolean("¿El jugador fue titular en el partido? (Si / No):", sc);

	        
		DatosjugadorpartidoId datosPartidoID= new DatosjugadorpartidoId(jugador.getIdJugador(),partido.getIdPartido());
		Datosjugadorpartido datosJugadorEnPartido= new Datosjugadorpartido(datosPartidoID,jugador,partido,valoracion,puntos,asistencias,rebotes,tapones,titular);
		
		return datosJugadorEnPartido;
	    
	}
	
	
	
}
