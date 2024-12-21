package main;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Clase principal del proyecto para la práctica de la segunda evaluación del módulo "Acceso a datos".
 * <p>
 * Este proyecto implementa una aplicación para gestionar registros y realizar consultas sobre una base de datos
 * de una liga de baloncesto, utilizando Hibernate para la persistencia de datos.
 * </p>
 * <p>
 * Cabe destacar que la clase cuenta con dos atributos, 'ConsultasMetodosPredeterminados' y 'ConsultasHQL'.
 * Utilizando los mismos para implementar el patrón DAO (Data Access Object), separando así la lógica de consultas y 
 * transacciones del flujo principal del programa, siguiendo principios de diseño modular.
 * </p>
 * <p>
 * Además, el programa incluye un menú interactivo para que el usuario seleccione diferentes operaciones de
 * gestión, tales como consultas mediante métodos predeterminados, consultas HQL, o consultas XML utilizando
 * XPath y XQuery.
 * </p>
 * @author Christian Alejandro Quinone
 * @throws InputMismatchException Si el usuario proporciona un valor no válido para la opción o cualquier entrada requerida durante el proceso.
 * @version 1.0
 * @since 2024
 */
public class Practica_2ev {
	
	private static final ConsultasMetodosPredeterminados consultasPredeterminadas = new ConsultasMetodosPredeterminados();
	private static final ConsultasHQL consultasHQL = new ConsultasHQL();

	public static void main(String[] args) {
		
		int opcion = 0 ;
		Scanner sc = new Scanner (System.in);

		do {
			System.out.println("Bienvenido al menú para la gestión de la base de datos 'baloncestohibernate', las opciones son las siguientes:\n1- Realizar gestión mediante Hibernate (métodos predeterminados).\n2- Realizar gestión mediante consultas HQL.\n3- Realizar gestión mediante consultas xPath y xQuery.\n4- Salir ");
			
			try {
				
				opcion = sc.nextInt();
				switch(opcion) {
				case 1 :
					usoMetodosPredeterminadosHibernate();
					break;
				case 2 :
					usoConsultasHQL();
					break;
				case 3 :
					//Consultas xPath
					
					break;
				case 4 :

					break;
					
				default :
					System.out.println("Ingresa una opción válida.\n");
					break;
				
				}
				
			}catch (InputMismatchException ex) {
				System.out.println("Ingresa una opción válida.\n");
				sc.nextLine();
			}		
		}while (opcion!=4);
		System.out.println("Hasta luego.");

	}
	
	/**
	 * Método para gestionar operaciones sobre la base de datos utilizando los métodos predeterminados de Hibernate.
	 * <p>
	 * Este método presenta un submenú interactivo que permite al usuario realizar diversas operaciones relacionadas con la gestión
	 * de registros en la base de datos. El menú se ejecuta en un bucle 'do-while' hasta que el usuario selecciona la opción de salir.
	 * Las operaciones disponibles son:
	 * </p>
	 * 
	 * <h3>Opciones del submenú</h3>
	 * <ul>
	 *   <li><b>Opción 1:</b> Buscar cualquier tipo de registro por su clave primaria.</li>
	 *   <li><b>Opción 2:</b> Obtener los datos de un jugador y todas sus estadísticas a partir de su ID.</li>
	 *   <li><b>Opción 3:</b> Obtener los datos de un equipo y un listado de sus jugadores ordenados por altura (de mayor a menor) a partir de su ID.</li>
	 *   <li><b>Opción 4:</b> Insertar cualquier tipo de registro en la base de datos.</li>
	 *   <li><b>Opción 5:</b> Salir del submenú.</li>
	 * </ul>
	 * 
	 * @throws InputMismatchException Si el usuario introduce un valor no válido, como un texto en lugar de un número.
	 * @see ConsultasMetodosPredeterminados#buscarJugadorConEstadisticas(int)
	 * @see ConsultasMetodosPredeterminados#buscarEquipoConSusJugadores(int)
	 * @see #insertarRegistros()
	 * @since 2024
	 */
	private static void usoMetodosPredeterminadosHibernate() {
		
		int opcion = 0 , id;
		
		Scanner sc = new Scanner (System.in);
		do {
			System.out.println("Elige entre las siguientes opciones:\n1- Buscar cualquier tipo de registro por su clave primaria.\n2- A partir de un determinado ID de jugador, obtener sus datos y todas sus estadísticas.\n3- A partir del ID de un equipo, obtener sus datos y un listado de todos los jugadores que forman parte de él, ordenados por la altura (de mayor a menor).\n4- Insertar cualquier tipo de registro en la base de datos.\n5- Salir ");
			
			try {
				
				opcion = sc.nextInt();
				switch(opcion) {
				case 1 :
					buscarRegistro();
					break;
				case 2 :
					System.out.println("Ingresa el ID del jugador a buscar: ");
					id=sc.nextInt();
					consultasPredeterminadas.buscarJugadorConEstadisticas(id);
					break;
				case 3 :
					System.out.println("Ingresa el ID del equipo a buscar: ");
					id=sc.nextInt();
					consultasPredeterminadas.buscarEquipoConSusJugadores(id);
					break;
				case 4 :
					insertarRegistros();
					break;
				case 5 :
					
					break;

				default :
					System.out.println("Ingresa una opción válida.\n");
					break;
				
				}
					
				
			}catch (InputMismatchException ex) {
				System.out.println("Ingresa una opción válida.\n");
				sc.nextLine();
			}
			
			
		}while (opcion!=5);
		
		
		
	}
	
	/**
	 * Método para buscar registros específicos en la base de datos según el tipo de entidad seleccionada por el usuario.
	 * <p>
	 * Este método presenta un submenú interactivo que permite al usuario buscar información detallada sobre diversas entidades
	 * de la base de datos. El usuario puede seleccionar el tipo de registro que desea buscar (equipo, jugador, partido, datos de un jugador en un partido.)
	 * y proporcionar los identificadores necesarios para realizar la búsqueda.
	 * </p>
	 * 
	 * <h3>Opciones del submenú</h3>
	 * <ul>
	 *   <li><b>Opción 1:</b> Buscar información sobre un equipo ingresando su ID.</li>
	 *   <li><b>Opción 2:</b> Buscar información sobre un jugador ingresando su ID.</li>
	 *   <li><b>Opción 3:</b> Buscar información sobre un partido ingresando su ID.</li>
	 *   <li><b>Opción 4:</b> Buscar información sobre los datos de un jugador específico en un partido,
	 *       ingresando el ID del partido y del jugador.</li>
	 *   <li><b>Opción 5:</b> Salir del submenú.</li>
	 * </ul>
	 * 
	 * @throws InputMismatchException Si el usuario introduce un valor no válido, como un texto en lugar de un número.
	 * @see ConsultasMetodosPredeterminados#buscarRegistrosPorID(String, int, int)
	 * @since 2024
	 */
	private static void buscarRegistro() {
		
		int opcion = 0 , id, id2;
		
		Scanner sc = new Scanner (System.in);
		do {
			System.out.println("¿Cuál registro te gustaría buscar?:\n1- Sobre equipo.\n2- Sobre jugador.\n3- Sobre partido.\n4- Sobre datos de jugadores en un determinado partido.\n5- Salir ");
			
			try {
				
				opcion = sc.nextInt();
				switch(opcion) {
				case 1 :
					System.out.println("Inserta el ID del equipo: ");
					id = sc.nextInt();	
					consultasPredeterminadas.buscarRegistrosPorID("Equipo", id, 0);
					break;
				case 2 :
					System.out.println("Inserta el ID del jugador: ");
					id = sc.nextInt();	
					consultasPredeterminadas.buscarRegistrosPorID("Jugador", id, 0);
					break;
				case 3 :
					System.out.println("Inserta el ID del partido: ");
					id = sc.nextInt();	
					consultasPredeterminadas.buscarRegistrosPorID("Partido", id, 0 );
					break;
				case 4 :
					System.out.println("Inserta el ID del partido: ");
					id = sc.nextInt();
					System.out.println("Inserta el ID del jugador: ");
					id2 = sc.nextInt();
					consultasPredeterminadas.buscarRegistrosPorID("DatosJugadorPartido", id,id2 );
					break;
				case 5 :
					
					break;

				default :
					System.out.println("Ingresa una opción válida.\n");
					break;
				
				}
				
			}catch (InputMismatchException ex) {
				System.out.println("Ingresa una opción válida.\n");
				sc.nextLine();
			}		
		}while (opcion!=5);
		
	}
	
	/**
	 * Método para insertar nuevos registros en la base de datos según el tipo de entidad seleccionada por el usuario.
	 * <p>
	 * Este método presenta un submenú interactivo que permite al usuario insertar diferentes tipos de datos en la base
	 * de datos de una liga de baloncesto. El usuario puede elegir entre insertar equipos, jugadores, partidos o
	 * estadísticas de jugadores en partidos específicos.
	 * </p>
	 * 
	 * <h3>Opciones del submenú</h3>
	 * <ul>
	 *   <li><b>Opción 1:</b> Insertar un nuevo equipo con la posibilidad de asignarle jugadores que aún no tengan equipo.</li>
	 *   <li><b>Opción 2:</b> Insertar un nuevo jugador con la posibilidad de asignarle un equipo existente.</li>
	 *   <li><b>Opción 3:</b> Insertar un nuevo partido con la opción de añadir estadísticas de los jugadores que participaron.</li>
	 *   <li><b>Opción 4:</b> Insertar estadísticas para un jugador en un partido específico. Incluye la posibilidad de
	 *       asignar un equipo al jugador si no tiene uno, ya que sin equipo no será posible asignar estadísticas.</li>
	 *   <li><b>Opción 5:</b> Salir del submenú.</li>
	 * </ul>
	 * 
	 * @throws InputMismatchException Si el usuario introduce un valor no válido, como un texto en lugar de un número.
	 * @see ConsultasMetodosPredeterminados#insertarEquipoConPosiblesJugadores()
	 * @see ConsultasMetodosPredeterminados#insertarJugadorConPosiblesEquipo()
	 * @see ConsultasMetodosPredeterminados#insertarPartidoConPosiblesEstadisticas()
	 * @see ConsultasMetodosPredeterminados#insertarEstadisticasAJugadorEnPartido()
	 * @since 2024
	 */
	private static void insertarRegistros() {
		int opcion = 0 ;
		
		Scanner sc = new Scanner (System.in);
		do {
			System.out.println("¿Cuál registro te gustaría insertar?:\n1- Sobre equipo.\n2- Sobre jugador.\n3- Sobre partido.\n4- Sobre datos de un jugador en un determinado partido.\n5- Salir ");
			
			try {
				
				opcion = sc.nextInt();
				switch(opcion) {
				case 1 :
					// Insertamos equipo y posibles jugadores sin equipo dentro del mismo.
					consultasPredeterminadas.insertarEquipoConPosiblesJugadores();
					break;
				case 2 :
					// Insertamos jugador y con posibilidad de asignarle un equipo de los ya existentes.
					consultasPredeterminadas.insertarJugadorConPosiblesEquipo();
					break;
				case 3 :
					//Insertamos un partido y ofrecemos introducir estadísticas.
					consultasPredeterminadas.insertarPartidoConPosiblesEstadisticas();
					break;
				case 4 :
					//Para insertar estadísticas, tomamos un jugador sin equipo, se lo asignamos, que el usuario elija un partido existente y añadimos estadísticas.
					consultasPredeterminadas.insertarEstadisticasAJugadorEnPartido();
					break;
				case 5 :
					
					break;

				default :
					System.out.println("Ingresa una opción válida.\n");
					break;
				
				}
				
			}catch (InputMismatchException ex) {
				System.out.println("Ingresa una opción válida.\n");
				sc.nextLine();
			}		
		}while (opcion!=5);
		
	}

	/**
	 * Método que gestiona un submenú para realizar consultas avanzadas a la base de datos utilizando HQL (Hibernate Query Language).
	 * <p>
	 * Este método presenta un menú interactivo que permite al usuario realizar varias consultas predefinidas sobre
	 * la base de datos de una liga de baloncesto. Las consultas están optimizadas para mostrar información relevante
	 * sobre estadísticas de partidos, jugadores y equipos utilizando HQL.
	 * </p>
	 * 
	 * <h3>Opciones del submenú</h3>
	 * <ul>
	 *   <li><b>Opción 1:</b> Mostrar las estadísticas de un partido bien tabuladas, a partir del ID del partido.</li>
	 *   <li><b>Opción 2:</b> Mostrar los datos de todos los jugadores que pertenecen a equipos de la localidad “Palencia”, junto con su media de estadísticas.</li>
	 *   <li><b>Opción 3:</b> A partir del ID de un equipo, mostrar los datos de los tres jugadores con las mejores valoraciones.</li>
	 *   <li><b>Opción 4:</b> A partir del ID de un jugador, mostrar sus estadísticas separadas en dos grupos: partidos como titular y como suplente, junto con las medias de ambos grupos.</li>
	 *   <li><b>Opción 5:</b> A partir del ID de un equipo, mostrar estadísticas como local y visitante (partidos jugados, ganados, perdidos, puntos a favor y en contra).</li>
	 *   <li><b>Opción 6:</b> Mostrar la clasificación final de la liga, basada en el desempeño de los equipos durante la temporada.</li>
	 *   <li><b>Opción 7:</b> Salir del submenú.</li>
	 * </ul>
	 * 
	 * <h3>Detalles de implementación</h3>
	 * <p>
	 * Cada opción utiliza métodos específicos de la clase {@code ConsultasHQL} para realizar las consultas, encapsulando
	 * la lógica necesaria para interactuar con la base de datos mediante HQL. Estos métodos garantizan un acceso eficiente
	 * y seguro a los datos.
	 * </p>
	 * 
	 * @throws InputMismatchException Si el usuario introduce un valor no válido, como un texto en lugar de un número.
	 * @see ConsultasHQL#imprimirEstadisticasDeUnPartido(int)
	 * @see ConsultasHQL#mostrarJugadoresDePalenciaConMediasEstadisticas()
	 * @see ConsultasHQL#mostrarTresMejorValoradosDeUnEquipo(int)
	 * @see ConsultasHQL#mostrarEstadisticasDeTitularYSuplente(int)
	 * @see ConsultasHQL#mostrarEstadisticasEquipoDeLocalYVisitante(int)
	 * @see ConsultasHQL#mostrarClasificacionLiga()
	 * @since 2024
	 */
	private static void usoConsultasHQL() {
		
		int opcion = 0 , id;
		
		Scanner sc = new Scanner (System.in);
		do {
			System.out.println("Elige entre las siguientes opciones:\n1- Imprimir por pantalla las estadísticas de un partido bien tabuladas.\n2- Imprimir por pantalla los datos de todos los jugadores que pertenecen a equipos de la localidad “Palencia”, y para\r\n"
					+ "   cada jugador su media de estadísticas.\n3- A partir del ID de un equipo, mostrar los datos de los jugadores que han obtenido las tres mejores valoraciones.\n4- A partir del ID de un jugador, se mostrarán todas las estadísticas separadas en dos grupos, las de los partidos en los que fue titular y\r\n"
					+ "   las de los partidos en los que fue suplente. Además se mostrará la media para cada uno de esos grupos.\n5- A partir del ID de un equipo, mostrar sus estadísticas como local y como visitante (partidos jugados, ganados, perdidos, puntos a favor y puntos en contra).\n6- Mostrar clasificación final de la liga.\n7- Salir.  ");
			
			try {
				
				opcion = sc.nextInt();
				switch(opcion) {
				case 1 :
					System.out.println("Inserta el ID del partido: ");
					id = sc.nextInt();	
					consultasHQL.imprimirEstadisticasDeUnPartido(id);
					break;
				case 2 :
					
					consultasHQL.mostrarJugadoresDePalenciaConMediasEstadisticas();
					break;
				case 3 :
					System.out.println("Inserta el ID del equipo: ");
					id = sc.nextInt();	
					consultasHQL.mostrarTresMejorValoradosDeUnEquipo(id);
					break;
				case 4 :
					System.out.println("Inserta el ID del jugador: ");
					id = sc.nextInt();	
					consultasHQL.mostrarEstadisticasDeTitularYSuplente(id);
					break;
				case 5 :
					System.out.println("Inserta el ID del equipo: ");
					id = sc.nextInt();	
					consultasHQL.mostrarEstadisticasEquipoDeLocalYVisitante(id);
					break;
				case 6 :
					consultasHQL.mostrarClasificacionLiga();
					break;
				case 7 :
					
					break;
					
				default :
					System.out.println("Ingresa una opción válida.\n");
					break;
				
				}
					
			}catch (InputMismatchException ex) {
				System.out.println("Ingresa una opción válida.\n");
				sc.nextLine();
			}
			
		}while (opcion!=7);
		
	}
	

	

}
