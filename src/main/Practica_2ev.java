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
 * Cabe destacar que la clase cuenta con tres atributos, 'ConsultasMetodosPredeterminados', 'ConsultasHQL' y 'ConsultasXML' .
 * Utilizando los mismos para implementar el patrón DAO (Data Access Object), separando así la lógica de consultas y 
 * transacciones del flujo principal del programa, siguiendo principios de diseño modular.
 * </p>
 * <p>
 * Además, el programa incluye un menú interactivo para que el usuario seleccione diferentes operaciones de
 * gestión, tales como consultas mediante métodos predeterminados, consultas HQL, o consultas XML utilizando
 * XPath y XQuery.
 * </p>
 * @author Christian Alejandro Quinone
 * @version 1.0
 * @since 2024
 */
public class Practica_2ev {
	
	private static final ConsultasMetodosPredeterminados consultasPredeterminadas = new ConsultasMetodosPredeterminados();
	private static final ConsultasHQL consultasHQL = new ConsultasHQL();
	private static final ConsultasXML consultasXML = new ConsultasXML();
	
    /**
     * Método principal de la aplicación.
     * <p>
     * Presenta un menú interactivo que permite al usuario realizar operaciones de gestión y consulta en la base de datos.
     * Las opciones disponibles son:
     * <ul>
     *   <li><b>Opción 1:</b> Gestión mediante métodos predeterminados de Hibernate.</li>
     *   <li><b>Opción 2:</b> Consultas utilizando el lenguaje HQL.</li>
     *   <li><b>Opción 3:</b> Consultas XML utilizando XPath y XQuery.</li>
     *   <li><b>Opción 4:</b> Salir del programa.</li>
     * </ul>
     * </p>
     * <p>
     * @param args Argumentos de línea de comandos (no utilizados en esta aplicación).
     * @throws InputMismatchException Si el usuario proporciona una entrada no válida durante la selección del menú.
     */
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
					usoConsultasXML();
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
			System.out.println("Elige entre las siguientes opciones:"
					+ "\n1- Buscar cualquier tipo de registro por su clave primaria."
					+ "\n2- A partir de un determinado ID de jugador, obtener sus datos y todas sus estadísticas."
					+ "\n3- A partir del ID de un equipo, obtener sus datos y un listado de todos los jugadores que forman parte de él, ordenados por la altura (de mayor a menor)."
					+ "\n4- Insertar cualquier tipo de registro en la base de datos."
					+ "\n5- Salir ");
			
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
			System.out.println("Elige entre las siguientes opciones:"
					+ "\n1- Imprimir por pantalla las estadísticas de un partido bien tabuladas."
					+ "\n2- Imprimir por pantalla los datos de todos los jugadores que pertenecen a equipos de la localidad “Palencia”, y para cada jugador su media de estadísticas."
					+ "\n3- A partir del ID de un equipo, mostrar los datos de los jugadores que han obtenido las tres mejores valoraciones."
					+ "\n4- A partir del ID de un jugador, se mostrarán todas las estadísticas separadas en dos grupos, las de los partidos en los que fue titular y las de los partidos en los que fue suplente. Además se mostrará la media para cada uno de esos grupos."
					+ "\n5- A partir del ID de un equipo, mostrar sus estadísticas como local y como visitante (partidos jugados, ganados, perdidos, puntos a favor y puntos en contra)."
					+ "\n6- Mostrar clasificación final de la liga."
					+ "\n7- Salir.  ");
			
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
	
    /**
     * Maneja el menú de opciones relacionadas con consultas XML utilizando XPath y XQuery.
     * <p>
     * Este método permite realizar diversas operaciones sobre una base de datos representada como colección XML,
     * tales como ejecutar consultas personalizadas, imprimir información detallada de jugadores y partidos, 
     * y obtener datos específicos basados en criterios predefinidos.
     * </p>
     * <p>
     * Opciones del menú:
     * <ul>
     *   <li><b>1:</b> Permite al usuario realizar consultas personalizadas XPath o XQuery.</li>
     *   <li><b>2:</b> Imprime información detallada de todos los jugadores.</li>
     *   <li><b>3:</b> Imprime información de todos los partidos jugados por un equipo determinado.</li>
     *   <li><b>4:</b> Obtiene una lista de jugadores que pertenecen a un equipo específico.</li>
     *   <li><b>5:</b> Obtiene los datos del jugador con la valoración más alta en la liga.</li>
     *   <li><b>6:</b> Salir del menú.</li>
     * </ul>
     * </p>
     * <p>
     * Antes de mostrar el menú, el método verifica si la colección XML necesaria está disponible,
     * mediante la función <b>posibilidadDeGenerarColeccionXML</b>.
     * </p>
     * 
     * @throws InputMismatchException Si el usuario proporciona una opción no válida.
     * @see #posibilidadDeGenerarColeccionXML(Scanner)
     * @see ConsultasXML#consultaXPathXQueryDelUsuario(String, String)
     * @see ConsultasXML#imprimirJugadores(String)
     * @see ConsultasXML#imprimirPartidosDeEquipo(String)
     * @see ConsultasXML#obtenerJugadoresDeEquipo(String)
     * @see ConsultasXML#obtenerJugadoresConValoracionMasAlta(String)
     */
	private static void usoConsultasXML() {
		
		int opcion = 0 , id;
		Scanner sc = new Scanner (System.in);
		String consulta = "";
		
		posibilidadDeGenerarColeccionXML(sc);
		
		do {
			
			System.out.println("Elige entre las siguientes opciones:"
					+ "\n1- Hacer consultas XPath y XQuery a la base de datos."
					+ "\n2- Imprimir por pantalla la información detallada de todos los jugadores."
					+ "\n3- Imprimir por pantalla la información de todos los partidos jugados por un determinado equipo."
					+ "\n4- Obtener una lista de jugadores que pertenecen a un equipo."
					+ "\n5- Obtener los datos del jugador que ha sacado la valoración más alta en toda la liga."
					+ "\n6- Salir.  ");
			
			try {
				String nombreColeccion = "/ColeccionBaloncestoHibernate";
				opcion = sc.nextInt();
				sc.nextLine();
				switch(opcion) {
				case 1 :
					System.out.println("A continuación introduce la consulta a realizar: ");
					consulta = sc.nextLine();	
					consultasXML.consultaXPathXQueryDelUsuario(nombreColeccion, consulta);
					break;
				case 2 :
					consultasXML.imprimirJugadores(nombreColeccion);
					break;
				case 3 :
					consultasXML.imprimirPartidosDeEquipo(nombreColeccion);
					break;
				case 4 :
					consultasXML.obtenerJugadoresDeEquipo(nombreColeccion);
					break;
				case 5 :
					consultasXML.obtenerJugadoresConValoracionMasAlta(nombreColeccion);
					break;
				case 6 :
					
					break;
					
				default :
					System.out.println("Ingresa una opción válida.\n");
					break;
				
				}
					
			}catch (InputMismatchException ex) {
				System.out.println("Ingresa una opción válida.\n");
				sc.nextLine();
			}
			
		}while (opcion!=6);
		
	}

    /**
     * Gestiona la posibilidad de generar una colección XML para consultas mediante 'eXide'.
     * <p>
     * Este método utiliza la clase <b>consultasXML</b> para intentar construir una colección XML a partir de la base de datos.
     * Dependiendo del resultado de la operación, el usuario es informado sobre la ubicación de la colección
     * y se le solicita que la añada al entorno de 'eXide' para la gestión de consultas.
     * </p>
     * <p>
     * Comportamiento según el resultado devuelto por <b>consultasXML.consultarABaseYConstruirColeccion()</b>:
     * <ul>
     *   <li><b>1:</b> La colección XML fue generada exitosamente. Se solicita al usuario que la añada a 'eXide'.</li>
     *   <li><b>0:</b> No fue posible actualizar la colección completamente, pero se informa al usuario de su ubicación interna.</li>
     *   <li><b>2:</b> La colección ya existe en el proyecto y se informa al usuario de su ubicación.</li>
     * </ul>
     * </p>
     * <p>
     * En todos los casos, se solicita al usuario que escriba 'Listo' para confirmar que la colección ha sido añadida a 'eXide'.
     * </p>
     * 
     * @param sc Un objeto {@code Scanner} utilizado para leer la entrada del usuario.
     * @see ConsultasXML#consultarABaseYConstruirColeccion()
     */
	private static void posibilidadDeGenerarColeccionXML(Scanner sc) {
		int seGeneraColeccion;
		String respuesta;
		seGeneraColeccion = consultasXML.consultarABaseYConstruirColeccion();
		
		if (seGeneraColeccion == 1) {
			System.out.println("Añade la colección a 'eXide' para gestionar las consultas.");
			do {
				System.out.println("Cuando la hayas añadido, escribe 'Listo' para continuar.");
				respuesta = sc.nextLine();
			}while (!respuesta.equalsIgnoreCase("Listo"));
		}else if(seGeneraColeccion == 0){
			System.out.println("No fué posible actualizar la colección XML.");
			System.out.println("Cuentas con una colección XML en la ruta interna del proyecto:\n  Practica_2ev_AD\\ColeccionBaloncestoHibernate .");
			System.out.println("Añade la colección a 'eXide' para gestionar las consultas.");
			do {
				System.out.println("Cuando la hayas añadido, escribe 'Listo' para continuar.");
				respuesta = sc.nextLine();
			}while (!respuesta.equalsIgnoreCase("Listo"));
		}else if(seGeneraColeccion == 2){
			System.out.println("Cuentas con una colección XML en la ruta interna del proyecto:\n  Practica_2ev_AD\\ColeccionBaloncestoHibernate .");
			System.out.println("Añade la colección a 'eXide' para gestionar las consultas.");
			do {
				System.out.println("Cuando la hayas añadido, escribe 'Listo' para continuar.");
				respuesta = sc.nextLine();
			}while (!respuesta.equalsIgnoreCase("Listo"));
		}
	}

	

}
