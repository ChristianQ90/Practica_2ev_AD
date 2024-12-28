package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import conexion.HibernateUtil;
import orm.Datosjugadorpartido;
import orm.Equipo;
import orm.Jugador;
import orm.Partido;

/**
 * Clase que encapsula la lógica para realizar consultas XML utilizando las APIs de XML:DB.
 * <p>
 * Esta clase utiliza la conexión a una base de datos eXist-db para realizar consultas XPath y XQuery 
 * sobre colecciones XML almacenadas en el servidor. Proporciona métodos para ejecutar consultas 
 * específicas relacionadas con jugadores, equipos y partidos de baloncesto.
 * </p>
 * <p>
 * La conexión al servidor está configurada con los siguientes parámetros por defecto: uri , userName y password. 
 * Los cuales deberán ser cambiados por el usuario según corresponda.
 * </p>
 * 
 * @author Christian Alejandro Quinone
 * @version 1.0
 * @since 2024
 */
public class ConsultasXML {
	//Cambiar según corresponda, para realizar las consultas con APIs XML:DB.
	private static final String uri = "xmldb:exist://localhost:8080/exist/xmlrpc/db";
	private static final String userName = "admin";
	private static final String password = "2222";

	public ConsultasXML() {
		super();
	}
	
	/**
	 * Método que consulta la base de datos y genera una colección XML basada en los datos actuales.
	 * <p>
	 * Este método interactúa con el usuario para decidir si se debe generar una nueva colección de archivos 
	 * XML desde la base de datos. Si el usuario confirma, se invocan métodos para construir archivos XML 
	 * representativos de entidades como equipos, jugadores, partidos y datos de jugadores en partidos. 
	 * La colección generada se guarda en un directorio denominado "ColeccionBaloncestoHibernate".
	 * </p>
	 * 
	 * @return Un entero que indica el resultado de la operación:
	 *         <ul>
	 *             <li><b>1:</b> La colección XML fue generada exitosamente.</li>
	 *             <li><b>0:</b> Se intentó generar la colección, pero ocurrió un error inesperado.</li>
	 *             <li><b>2:</b> El usuario optó por no generar la colección.</li>
	 *         </ul>
	 * 
	 * @throws SecurityException Si ocurre un error al intentar crear el directorio para la colección.
	 * @throws IOException Si ocurre un error inesperado al manipular los archivos XML.
	 */
	public int consultarABaseYConstruirColeccion() {
		
		int posible = 0;
		boolean xmlEquipo = false , xmlJugador = false ,xmlPartido = false ,xmlDatosJugadorPartido = false;
		Scanner sc = new Scanner(System.in);
		String respuesta="";
		
		File directorioColeccion = new File("ColeccionBaloncestoHibernate");
		
        if (!directorioColeccion.exists()) {
            directorioColeccion.mkdir();
        }
        do {
            System.out.println("¿Deseas generar una nueva Colección de XML a partir de la Base de datos actual?: (Si/No)");
            respuesta = sc.nextLine();
            if (respuesta.equalsIgnoreCase("Si")) {
                xmlEquipo = consultarEquipoYConstruirXML(directorioColeccion);
                xmlJugador = consultarJugadorYConstruirXML(directorioColeccion);
                xmlPartido = consultarPartidoYConstruirXML(directorioColeccion);
                xmlDatosJugadorPartido = consultarDatosJPYConstruirXML(directorioColeccion);
                
                if(xmlEquipo && xmlJugador && xmlPartido && xmlDatosJugadorPartido) {
                	System.out.println("\nColección XML generada con éxito en:\n  " + directorioColeccion.getAbsolutePath());
                	posible = 1; //Se construye correctamente la colección.
                	break;
                }else {
                	System.out.println("Ocurrió un error inesperado al generar los archivos xml para la colección");
                	posible = 0 ;//El usuario decide construirla pero ocurre un error inesperado.
                	break;
                }
            }else if(respuesta.equalsIgnoreCase("No")) {
            	posible = 2; //El usuario decide no construir la colección.
            }else {
            	System.out.println("Elige una opción válida.");
            }
        }while (!respuesta.equalsIgnoreCase("No"));

        
        return posible;
        
	}
	
	/**
	 * Consulta la base de datos para obtener información de los equipos y genera un archivo XML
	 * con los datos obtenidos. El archivo se guarda en la ruta especificada.
	 *
	 * <p>El método utiliza Hibernate para obtener los datos desde la base de datos y la API
	 * DOM para construir el archivo XML. Si la consulta no retorna resultados, se emite un mensaje
	 * informativo. En caso de errores, se manejan diferentes excepciones para garantizar la
	 * estabilidad del programa.</p>
	 *
	 * @param rutaDirectorio Directorio donde se guardará el archivo XML generado.
	 * @return <b>true</b> si el archivo XML se generó correctamente; <b>false</b> en caso contrario.
	 * @throws HibernateException Si ocurre un error durante la interacción con Hibernate.
	 * @throws NullPointerException Si se intenta acceder a datos nulos en los resultados.
	 * @throws IllegalArgumentException Si hay un argumento inválido al buscar registros.
	 * @throws ParserConfigurationException Si ocurre un error al configurar el constructor del documento XML.
	 * @throws Exception Para cualquier otro error inesperado durante la ejecución del método.
	 */
	private boolean consultarEquipoYConstruirXML(File rutaDirectorio) {
		boolean posible = false;
		String nombreArchivo = "Equipo.xml";
		Session s = null;
		Transaction trans = null;
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Equipo").setReadOnly(true);
		
			List<Equipo> listaEquipos = q.getResultList();
			
			if(listaEquipos.size()>0) {
	            // Crear el documento XML
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.newDocument();

	            // Crear el elemento raíz
	            Element elementoRaiz = doc.createElement("Equipos");
	            doc.appendChild(elementoRaiz);

	            // Agregar elementos 
	            for (Equipo equipo : listaEquipos) {
	                Element elementoEquipo = doc.createElement("Equipo");

	                Element idEquipo = doc.createElement("idEquipo");
	                idEquipo.appendChild(doc.createTextNode(String.valueOf(equipo.getIdEquipo())));
	                elementoEquipo.appendChild(idEquipo);

	                Element nombre = doc.createElement("nombre");
	                nombre.appendChild(doc.createTextNode(manejarNulo(equipo.getNombre())));
	                elementoEquipo.appendChild(nombre);
	                
	                Element localidad = doc.createElement("localidad");
	                localidad.appendChild(doc.createTextNode(manejarNulo(equipo.getLocalidad())));
	                elementoEquipo.appendChild(localidad);
	                
	                Element pais = doc.createElement("pais");
	                pais.appendChild(doc.createTextNode(manejarNulo(equipo.getPais())));
	                elementoEquipo.appendChild(pais);
	                
	                Element nombreCorto = doc.createElement("nombreCorto");
	                nombreCorto.appendChild(doc.createTextNode(manejarNulo(equipo.getNombreCorto())));
	                elementoEquipo.appendChild(nombreCorto);
   
	                
	                elementoRaiz.appendChild(elementoEquipo);
	            }

	            escribirArchivoXML(rutaDirectorio, nombreArchivo, doc);
	            
			}else {
				System.out.println("No hay resultados para consulta HQL de Equipo.\n");
			}
			
			trans.commit();
			posible = true;
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (ParserConfigurationException e) {
            System.err.println("Error al configurar el constructor del documento XML:");
            System.err.println("Detalle: " + e.getMessage());
        }catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
		return posible;
	}
	
	/**
	 * Consulta la base de datos para obtener información de los jugadores y genera un archivo XML
	 * con los datos obtenidos. El archivo se guarda en la ruta especificada.
	 *
	 * <p>El método utiliza Hibernate para realizar la consulta y la API DOM para construir el archivo XML.
	 * Si no se encuentran jugadores en la base de datos, se emite un mensaje informativo. En caso de errores,
	 * se manejan las excepciones pertinentes para garantizar la estabilidad del programa.</p>
	 *
	 * @param rutaDirectorio Directorio donde se guardará el archivo XML generado.
	 * @return <b>true</b> si el archivo XML se generó correctamente; <b>false</b> en caso contrario.
	 * @throws HibernateException Si ocurre un error durante la interacción con Hibernate.
	 * @throws NullPointerException Si se intenta acceder a datos nulos en los resultados de la consulta.
	 * @throws IllegalArgumentException Si se pasa un argumento inválido al intentar buscar registros.
	 * @throws ParserConfigurationException Si ocurre un error al configurar el constructor del documento XML.
	 * @throws Exception Para cualquier otro error inesperado durante la ejecución del método.
	 */
	private boolean consultarJugadorYConstruirXML(File rutaDirectorio) {
		boolean posible = false;
		String nombreArchivo = "Jugador.xml";
		Session s = null;
		Transaction trans = null;
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Jugador").setReadOnly(true);
		
			List<Jugador> listaJugadores = q.getResultList();
			
			if(listaJugadores.size()>0) {
	            // Crear el documento XML
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.newDocument();

	            // Crear el elemento raíz
	            Element elementoRaiz = doc.createElement("Jugadores");
	            doc.appendChild(elementoRaiz);

	            // Agregar elementos 
	            for (Jugador jugador : listaJugadores) {
	                Element elementoJugador = doc.createElement("Jugador");

	                Element idJugador = doc.createElement("idJugador");
	                idJugador.appendChild(doc.createTextNode(String.valueOf(jugador.getIdJugador())));
	                elementoJugador.appendChild(idJugador);

	                Element idEquipo = doc.createElement("idEquipo");
	                if (jugador.getEquipo() != null) {
	                    idEquipo.appendChild(doc.createTextNode(String.valueOf(jugador.getEquipo().getIdEquipo())));
	                } else {
	                    idEquipo.appendChild(doc.createTextNode("null")); //"Sin equipo"
	                }
	                elementoJugador.appendChild(idEquipo);
	                
	                Element nombre = doc.createElement("nombre");
	                nombre.appendChild(doc.createTextNode(manejarNulo(jugador.getNombre())));
	                elementoJugador.appendChild(nombre);
	                
	                Element apellidos = doc.createElement("apellidos");
	                apellidos.appendChild(doc.createTextNode(manejarNulo(jugador.getApellidos())));
	                elementoJugador.appendChild(apellidos);
	                
	                Element alturaCm = doc.createElement("alturaCM");
	                alturaCm.appendChild(doc.createTextNode(manejarNulo(jugador.getAlturaCm())));
	                elementoJugador.appendChild(alturaCm);
	                
	                Element edad = doc.createElement("edad");
	                edad.appendChild(doc.createTextNode(manejarNulo(jugador.getEdad())));
	                elementoJugador.appendChild(edad);
   
	                Element nacionalidad = doc.createElement("nacionalidad");
	                nacionalidad.appendChild(doc.createTextNode(manejarNulo(jugador.getNacionalidad())));
	                elementoJugador.appendChild(nacionalidad);
	                
	                elementoRaiz.appendChild(elementoJugador);
	            }

	            escribirArchivoXML(rutaDirectorio, nombreArchivo, doc);
	            
			}else {
				System.out.println("No hay resultados para consulta HQL de Jugador.\n");
			}
			
			trans.commit();
			posible = true;
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (ParserConfigurationException e) {
            System.err.println("Error al configurar el constructor del documento XML:");
            System.err.println("Detalle: " + e.getMessage());
        }catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
		return posible;
	}
	
	/**
	 * Consulta la base de datos para obtener información de los partidos y genera un archivo XML
	 * con los datos obtenidos. El archivo se guarda en la ruta especificada.
	 *
	 * <p>El método utiliza Hibernate para realizar la consulta y la API DOM para construir el archivo XML.
	 * Si no se encuentran partidos en la base de datos, se emite un mensaje informativo. En caso de errores,
	 * se manejan las excepciones pertinentes para garantizar la estabilidad del programa.</p>
	 *
	 * @param rutaDirectorio Directorio donde se guardará el archivo XML generado.
	 * @return <b>true</b> si el archivo XML se generó correctamente; <b>false</b> en caso contrario.
	 * @throws HibernateException Si ocurre un error durante la interacción con Hibernate.
	 * @throws NullPointerException Si se intenta acceder a datos nulos en los resultados de la consulta.
	 * @throws IllegalArgumentException Si se pasa un argumento inválido al intentar buscar registros.
	 * @throws ParserConfigurationException Si ocurre un error al configurar el constructor del documento XML.
	 * @throws Exception Para cualquier otro error inesperado durante la ejecución del método.
	 */
	private boolean consultarPartidoYConstruirXML(File rutaDirectorio) {
		boolean posible = false;
		String nombreArchivo = "Partido.xml";
		Session s = null;
		Transaction trans = null;
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Partido").setReadOnly(true);
		
			List<Partido> listaPartidos = q.getResultList();
			
			if(listaPartidos.size()>0) {
	            // Crear el documento XML
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.newDocument();

	            // Crear el elemento raíz
	            Element elementoRaiz = doc.createElement("Partidos");
	            doc.appendChild(elementoRaiz);

	            // Agregar elementos 
	            for (Partido partido : listaPartidos) {
	                Element elementoPartido = doc.createElement("Partido");

	                Element idPartido = doc.createElement("idPartido");
	                idPartido.appendChild(doc.createTextNode(String.valueOf(partido.getIdPartido())));
	                elementoPartido.appendChild(idPartido);

	                Element idEquipoLocal = doc.createElement("idEquipoLocal");
	                if (partido.getEquipoByIdEquipoLocal() != null) {
	                    idEquipoLocal.appendChild(doc.createTextNode(String.valueOf(partido.getEquipoByIdEquipoLocal().getIdEquipo())));
	                } else {
	                    idEquipoLocal.appendChild(doc.createTextNode("null")); //"Sin equipo local"
	                }
	                elementoPartido.appendChild(idEquipoLocal);
	                
	                Element idEquipoVisitante = doc.createElement("idEquipoVisitante");
	                if (partido.getEquipoByIdEquipoVisitante() != null) {
	                    idEquipoVisitante.appendChild(doc.createTextNode(String.valueOf(partido.getEquipoByIdEquipoVisitante().getIdEquipo())));
	                } else {
	                    idEquipoVisitante.appendChild(doc.createTextNode("null")); //"Sin equipo visitante"
	                }
	                elementoPartido.appendChild(idEquipoVisitante);
	                
	                
	                Element puntosLocal = doc.createElement("puntosLocal");
	                puntosLocal.appendChild(doc.createTextNode(manejarNulo(partido.getPuntosLocal())));
	                elementoPartido.appendChild(puntosLocal);
   
	                Element puntosVisitante = doc.createElement("puntosVisitante");
	                puntosVisitante.appendChild(doc.createTextNode(manejarNulo(partido.getPuntosVisitante())));
	                elementoPartido.appendChild(puntosVisitante);
	                
	                elementoRaiz.appendChild(elementoPartido);
	            }
	            
	            escribirArchivoXML(rutaDirectorio, nombreArchivo, doc);

			}else {
				System.out.println("No hay resultados para consulta HQL de Partido.\n");
			}
			
			trans.commit();
			posible = true;
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (ParserConfigurationException e) {
            System.err.println("Error al configurar el constructor del documento XML:");
            System.err.println("Detalle: " + e.getMessage());
        }catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
		return posible;
	}
	
	/**
	 * Consulta la base de datos para obtener los datos de las participaciones de jugadores en partidos y 
	 * genera un archivo XML con los datos obtenidos. El archivo se guarda en la ruta especificada.
	 *
	 * <p>El método utiliza Hibernate para realizar la consulta y la API DOM para construir el archivo XML.
	 * Si no se encuentran jugadores en la base de datos, se emite un mensaje informativo. En caso de errores,
	 * las excepciones son manejadas para garantizar la estabilidad del programa.</p>
	 *
	 * @param rutaDirectorio Directorio donde se guardará el archivo XML generado.
	 * @return <b>true</b> si el archivo XML se generó correctamente; <b>false</b> en caso contrario.
	 * @throws HibernateException Si ocurre un error durante la interacción con Hibernate.
	 * @throws NullPointerException Si se intenta acceder a datos nulos en los resultados de la consulta.
	 * @throws IllegalArgumentException Si se pasa un argumento inválido al intentar buscar registros.
	 * @throws ParserConfigurationException Si ocurre un error al configurar el constructor del documento XML.
	 * @throws Exception Para cualquier otro error inesperado durante la ejecución del método.
	 */
	private boolean consultarDatosJPYConstruirXML(File rutaDirectorio) {
		boolean posible = false;
		String nombreArchivo = "Datosjugadorpartido.xml";
		
		Session s = null;
		Transaction trans = null;
		try {
			s=HibernateUtil.getSessionFactory().openSession();
			trans = s.beginTransaction();
			
			Query q = s.createQuery("from Jugador").setReadOnly(true);
		
			List<Jugador> listaJugadores = q.getResultList();
			
			if(listaJugadores.size()>0) {
	            // Crear el documento XML
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.newDocument();

	            // Crear el elemento raíz
	            Element elementoRaiz = doc.createElement("DatosJugadoresPartidos");
	            doc.appendChild(elementoRaiz);

	            
	            for (Jugador jugador : listaJugadores) {
	            	
	            	Set<Datosjugadorpartido> partidosJugados = jugador.getDatosjugadorpartidos();
	            	
	            	// Agregar elementos 
	            	for(Datosjugadorpartido datosPartido :  partidosJugados) {

		                Element elementoDatosJP = doc.createElement("Datosjugadorpartido");

		                Element idJugador = doc.createElement("idJ");
		                idJugador.appendChild(doc.createTextNode(String.valueOf(datosPartido.getId().getIdJ())));
		                elementoDatosJP.appendChild(idJugador);
		                
		                Element idPartido = doc.createElement("idP");
		                idPartido.appendChild(doc.createTextNode(String.valueOf(datosPartido.getId().getIdP())));
		                elementoDatosJP.appendChild(idPartido);
		                
		                Element valoracion = doc.createElement("valoracion");
		                valoracion.appendChild(doc.createTextNode(manejarNulo(datosPartido.getValoracion())));
		                elementoDatosJP.appendChild(valoracion);
		                
		                Element puntos = doc.createElement("puntos");
		                puntos.appendChild(doc.createTextNode(manejarNulo(datosPartido.getPuntos())));
		                elementoDatosJP.appendChild(puntos);
		                
		                Element asistencias = doc.createElement("asistencias");
		                asistencias.appendChild(doc.createTextNode(manejarNulo(datosPartido.getAsistencias())));
		                elementoDatosJP.appendChild(asistencias);
		                
		                Element rebotes = doc.createElement("rebotes");
		                rebotes.appendChild(doc.createTextNode(manejarNulo(datosPartido.getRebotes())));
		                elementoDatosJP.appendChild(rebotes);
	   
		                Element tapones = doc.createElement("tapones");
		                tapones.appendChild(doc.createTextNode(manejarNulo(datosPartido.getTapones())));
		                elementoDatosJP.appendChild(tapones);
		                
		                Element titular = doc.createElement("titular");
		                titular.appendChild(doc.createTextNode(manejarNulo(datosPartido.getTitular())));
		                elementoDatosJP.appendChild(titular);
		                
		                elementoRaiz.appendChild(elementoDatosJP);
	            	}

	            }

	            escribirArchivoXML(rutaDirectorio, nombreArchivo, doc);
			}else {
				System.out.println("No hay resultados para consulta HQL de Jugador.\n");
			}
			
			trans.commit();
			posible = true;
			
		} catch (HibernateException he) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error de Hibernate: " + he.getMessage());
	    } catch (NullPointerException npe) {
	        System.err.println("Error: Intento de acceder a un objeto nulo. Verifique los datos.");
	    } catch (IllegalArgumentException iae) {
	        System.err.println("Error: Argumento inválido al intentar buscar el registro: " + iae.getMessage());
	    } catch (ParserConfigurationException e) {
            System.err.println("Error al configurar el constructor del documento XML:");
            System.err.println("Detalle: " + e.getMessage());
        } catch (Exception e) {
	        if (trans != null) trans.rollback();
	        System.err.println("Error inesperado: " + e.getMessage());
	    } finally {
	        if (s != null) s.close();
	    }
		
		return posible;
	}

	/**
	 * Escribe un documento XML en el sistema de archivos en la ubicación especificada.
	 * 
	 * <p>El método utiliza un <b>Transformer</b> para convertir un objeto <b>Document</b> 
	 * en un archivo XML y lo guarda en el directorio proporcionado. Además, aplica 
	 * configuraciones para formatear la salida con indentación.</p>
	 *
	 * @param rutaDirectorio El directorio donde se guardará el archivo XML.
	 * @param nombreArchivo El nombre del archivo XML a generar.
	 * @param doc El objeto <b>Document</b> que contiene los datos a escribir en el archivo.
	 * 
	 * @throws TransformerException Si ocurre un error durante el proceso de transformación o escritura del contenido XML.
	 */
	private void escribirArchivoXML(File rutaDirectorio, String nombreArchivo, Document doc) {
		try {
			// Escribir el contenido en un archivo XML 
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
	
			// Configuración para una salida formateada (con indentación)
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	
			DOMSource source = new DOMSource(doc);
	
			File xmlFile = new File(rutaDirectorio, nombreArchivo);
			StreamResult result = new StreamResult(xmlFile);
	
			transformer.transform(source, result);
	
			//System.out.println("Archivo XML generado en: " + xmlFile.getAbsolutePath());
		} catch (TransformerException e) {
			System.err.println("Error al transformar o escribir el contenido XML:");
			System.err.println("Detalle: " + e.getMessage());
		}
	}

	/**
	 * Maneja valores nulos y los convierte en cadenas de texto.
	 *
	 * <p>Este método verifica si el valor proporcionado es nulo. Si no lo es, convierte el valor a 
	 * su representación en cadena utilizando <b>Object#toString()</b>. Si el valor es nulo, 
	 * retorna la cadena literal "null".</p>
	 *
	 * @param valor El valor a manejar, que puede ser cualquier objeto.
	 * @return Una cadena que representa el valor, o la cadena "null" si el valor es nulo.
	 */
	private String manejarNulo(Object valor) {
	    if (valor != null) {
	        return valor.toString(); // Si el valor no es null, lo convierte a String y lo devuelve.
	    } else {
	        return "null"; // Si el valor es null, devuelve "null".
	    }
	}
	
	/**
	 * Realiza una consulta sobre una base de datos XML, buscando información de jugadores 
	 * y mostrando los resultados en la consola.
	 *
	 * <p>Este método se conecta a una base de datos XML utilizando eXist-db, realiza una consulta XQuery 
	 * para obtener detalles de los jugadores almacenados en una colección especificada, 
	 * y luego imprime los resultados en la consola. Los detalles de cada jugador incluyen 
	 * su ID, ID del equipo, nombre, apellidos, altura, edad y nacionalidad.</p>
	 *
	 * @param nomCol El nombre de la colección en la base de datos desde la cual se realizarán las consultas.
	 * @throws ClassNotFoundException Si no se encuentra la clase del controlador de la base de datos.
	 * @throws InstantiationException Si no se puede instanciar el controlador de la base de datos.
	 * @throws IllegalAccessException Si no se tiene acceso al controlador de la base de datos.
	 * @throws XMLDBException Si ocurre un error relacionado con la base de datos XML.
	 * @throws Exception Si ocurre cualquier otro error inesperado.
	 */
	public void imprimirJugadores(String nomCol) {
	
		Database dbDriver;
		Collection col;
		try {
			dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			col = DatabaseManager.getCollection(uri+ nomCol,userName,password);
			
			int contadorElementos = 0;
			if(col == null) {
				System.out.println("La colección no existe.");
			}
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			ResourceSet result = servicio.query(
					"for $j in /Jugadores/Jugador "+
					"let $idJ := $j/idJugador "+
					"let $idE := $j/idEquipo "+
					"let $nombre := $j/nombre "+
					"let $apellidos := $j/apellidos "+
					"let $alturaCm := $j/alturaCM "+
					"let $edad := $j/edad "+
					"let $nacionalidad := $j/nacionalidad "+
					"return concat('Jugador ID: ', string($idJ), ', Equipo ID: ', string($idE), ', Nombre: ', string($nombre), "+
					"', Apellidos: ', string($apellidos), ', Altura (cm): ', string($alturaCm), "+
					"', Edad: ', string($edad), ', Nacionalidad: ', string($nacionalidad))"
					);
			ResourceIterator i;
			i= result.getIterator();
			
			if(!i.hasMoreResources()) {
				System.out.println("La consulta no devuelve nada.");
			}
			while (i.hasMoreResources()) {
				contadorElementos++;
				Resource r = i.nextResource();
				System.out.println((String)r.getContent());
			}
			System.out.println("Se han encontrado "+contadorElementos+" elementos.");
			col.close();
			
	    } catch (InstantiationException e) {
	        System.err.println("Error al instanciar el controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "InstantiationException: {0}", e.getMessage());
	    } catch (IllegalAccessException e) {
	        System.err.println("Acceso ilegal al controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "IllegalAccessException: {0}", e.getMessage());
	    } catch (ClassNotFoundException e) {
	        System.err.println("No se pudo encontrar la clase del controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "ClassNotFoundException: {0}", e.getMessage());
	    } catch (XMLDBException e) {
			int b = ErrorCodes.VENDOR_ERROR;
			int a = e.vendorErrorCode;
			System.out.println("Error a:" + a);
			System.out.println("Error b:" + b);
			System.out.println("Mensaje de error 2" + e.getMessage());
			Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
	        System.err.println("Ocurrió un error inesperado.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "Exception: {0}", e.getMessage());
	    }
	}
	
	/**
	 * Realiza una consulta XPath o XQuery en una base de datos XML eXist-db. 
	 * El método se conecta a la base de datos utilizando las credenciales proporcionadas y ejecuta 
	 * la consulta indicada sobre una colección de documentos XML.
	 * 
	 * @param nomCol Nombre de la colección en la base de datos eXist-db sobre la que se realizará la consulta.
	 * @param query Cadena que contiene la consulta XPath o XQuery a ejecutar.
	 * 
	 * @throws InstantiationException Si ocurre un error al instanciar el controlador de la base de datos.
	 * @throws IllegalAccessException Si el acceso al controlador de la base de datos es ilegal.
	 * @throws ClassNotFoundException Si no se encuentra la clase del controlador de la base de datos.
	 * @throws XMLDBException Si ocurre un error en la base de datos XML.
	 * @throws Exception Si ocurre cualquier otro tipo de error inesperado.
	 */
	public void consultaXPathXQueryDelUsuario(String nomCol , String query) {
		
		Database dbDriver;
		Collection col;
		try {
			dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			col = DatabaseManager.getCollection(uri+ nomCol,userName,password);
			
			int contadorElementos = 0;
			if(col == null) {
				System.out.println("La colección no existe.");
			}
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			ResourceSet result = servicio.query(query);
			ResourceIterator i;
			i= result.getIterator();
			
			if(!i.hasMoreResources()) {
				System.out.println("La consulta no devuelve nada.");
			}
			while (i.hasMoreResources()) {
				contadorElementos++;
				Resource r = i.nextResource();
				System.out.println((String)r.getContent());
			}
			System.out.println("Se han encontrado "+contadorElementos+" elementos.");
			col.close();
			
	    } catch (InstantiationException e) {
	        System.err.println("Error al instanciar el controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "InstantiationException: {0}", e.getMessage());
	    } catch (IllegalAccessException e) {
	        System.err.println("Acceso ilegal al controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "IllegalAccessException: {0}", e.getMessage());
	    } catch (ClassNotFoundException e) {
	        System.err.println("No se pudo encontrar la clase del controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "ClassNotFoundException: {0}", e.getMessage());
	    } catch (XMLDBException e) {
			int b = ErrorCodes.VENDOR_ERROR;
			int a = e.vendorErrorCode;
			System.out.println("Error a:" + a);
			System.out.println("Error b:" + b);
			System.out.println("Mensaje de error 2" + e.getMessage());
			Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
	        System.err.println("Ocurrió un error inesperado.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "Exception: {0}", e.getMessage());
	    }
	}
	
	/**
	 * Imprime los partidos jugados por un equipo específico desde una colección XML en eXist-db. 
	 * El usuario selecciona el equipo a través de un ID previamente mostrado por el método
	 * <b>#mostrarEquiposExistentes(String)</b>. Luego, se consulta la base de datos eXist-db
	 * para  mostrar los partidos jugados por dicho equipo, ya sea como equipo local o visitante.
	 * 
	 * @param nomCol Nombre de la colección en la base de datos eXist-db donde se almacenan los documentos XML de los partidos.
	 * @see mostrarEquiposExistentes(String).
	 * @throws InstantiationException Si ocurre un error al instanciar el controlador de la base de datos.
	 * @throws IllegalAccessException Si el acceso al controlador de la base de datos es ilegal.
	 * @throws ClassNotFoundException Si no se encuentra la clase del controlador de la base de datos.
	 * @throws XMLDBException Si ocurre un error al interactuar con la base de datos XML.
	 * @throws Exception Si ocurre cualquier otro tipo de error inesperado.
	 */
	public void imprimirPartidosDeEquipo(String nomCol ) {
		
		Database dbDriver;
		Collection col;
		int idEquipoSeleccionado =-1;
		Scanner sc = new Scanner(System.in);
		try {
			
			idEquipoSeleccionado = mostrarEquiposExistentes("/ColeccionBaloncestoHibernate");
			System.out.println("Los partidos jugados por el equipo con ID '"+idEquipoSeleccionado+"' son los siguientes:");
			dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			col = DatabaseManager.getCollection(uri+ nomCol,userName,password);
			
			int contadorElementos = 0;
			if(col == null) {
				System.out.println("La colección no existe.");
			}
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			ResourceSet result = servicio.query(
					"for $p in /Partidos/Partido "+
					"let $idP := $p/idPartido "+
					"let $idEL := $p/idEquipoLocal "+
					"let $idEV := $p/idEquipoVisitante "+
					"let $pL := $p/puntosLocal "+
					"let $pV := $p/puntosVisitante "+
					"where idEquipoLocal= "+idEquipoSeleccionado+" or idEquipoVisitante = "+idEquipoSeleccionado+" "+
					"return concat('Partido ID: ', string($idP), ', Equipo Local ID: ', string($idEL), "+
					"', Equipo Visitante ID: ', string($idEV),', Puntos Local: ',string($pL),', Puntos Visitante: ',string($pV)) "
					);
			ResourceIterator i;
			i= result.getIterator();
			
			if(!i.hasMoreResources()) {
				System.out.println("El equipo seleccionado no cuenta con partidos jugados actualmente.");
			}
			while (i.hasMoreResources()) {
				contadorElementos++;
				Resource r = i.nextResource();
				System.out.println((String)r.getContent());
			}
			System.out.println("Se han encontrado "+contadorElementos+" elementos.");
			col.close();
			
	    } catch (InstantiationException e) {
	        System.err.println("Error al instanciar el controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "InstantiationException: {0}", e.getMessage());
	    } catch (IllegalAccessException e) {
	        System.err.println("Acceso ilegal al controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "IllegalAccessException: {0}", e.getMessage());
	    } catch (ClassNotFoundException e) {
	        System.err.println("No se pudo encontrar la clase del controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "ClassNotFoundException: {0}", e.getMessage());
	    } catch (XMLDBException e) {
			int b = ErrorCodes.VENDOR_ERROR;
			int a = e.vendorErrorCode;
			System.out.println("Error a:" + a);
			System.out.println("Error b:" + b);
			System.out.println("Mensaje de error 2" + e.getMessage());
			Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
	        System.err.println("Ocurrió un error inesperado.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "Exception: {0}", e.getMessage());
	    }
	}

	/**
	 * Muestra los equipos existentes en una colección XML y permite al usuario seleccionar uno de ellos 
	 * mediante su ID. La información de los equipos se obtiene mediante una consulta XQuery y se presenta 
	 * al usuario en la consola. El usuario debe ingresar un ID de equipo válido para continuar.
	 * 
	 * @param nomCol El nombre de la colección en la base de datos eXist-db donde se almacenan los equipos.
	 * 
	 * @return El ID del equipo seleccionado por el usuario.
	 * 
	 * @throws InstantiationException Si ocurre un error al instanciar el controlador de la base de datos.
	 * @throws IllegalAccessException Si el acceso al controlador de la base de datos es ilegal.
	 * @throws ClassNotFoundException Si no se pudo encontrar la clase del controlador de la base de datos.
	 * @throws XMLDBException Si ocurre un error al interactuar con la base de datos XML.
	 * @throws Exception Si ocurre cualquier otro tipo de error inesperado.
	 */
	private int mostrarEquiposExistentes(String nomCol ) {
		
		Database dbDriver;
		Collection col;
		int idSc = -1;
		boolean eleccionValida = false;
		Scanner sc= new Scanner(System.in);
		List<Integer> idsValidos = new ArrayList<Integer>();
		try {
			
			System.out.println("Los ids de los equipos actuales son los siguientes:");
			dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			col = DatabaseManager.getCollection(uri+ nomCol,userName,password);
			
			if(col == null) {
				System.out.println("La colección no existe.");
			}
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			ResourceSet result = servicio.query(
					"for $e in /Equipos/Equipo "+
					"let $idE := $e/idEquipo "+
					"let $nombre := $e/nombre "+
					"let $localidad := $e/localidad "+
					"let $pais := $e/pais "+
					"return concat('Equipo ID: ', string($idE), ', Nombre: ', string($nombre), "+
					"', Localidad: ', string($localidad), ', País: ', string($pais)) "
					);
			ResourceIterator i;
			i= result.getIterator();
			
			if(!i.hasMoreResources()) {
				System.out.println("La consulta no devuelve nada.");
			}
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String resultado = (String)r.getContent();
				System.out.println(resultado);
				String[] campos = resultado.split(", ");
				String[] camposIdEquipo = campos[0].split("ID: ");
				Integer idEquipo = Integer.parseInt(camposIdEquipo[1]);
				idsValidos.add(idEquipo);
			}

			do {
				System.out.println("Selecciona un id de equipo válido para poder continuar: ");
				if(sc.hasNextInt()) {
					idSc=sc.nextInt();
					sc.nextLine();//Limpiamos el buffer.
					if(idsValidos.contains(idSc)) {//Evaluamos pertenencia al conjunto
						eleccionValida = true;			
					}
				}else {
					sc.nextLine();
				}
			}while(!eleccionValida );
			col.close();
			
	    } catch (InstantiationException e) {
	        System.err.println("Error al instanciar el controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "InstantiationException: {0}", e.getMessage());
	    } catch (IllegalAccessException e) {
	        System.err.println("Acceso ilegal al controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "IllegalAccessException: {0}", e.getMessage());
	    } catch (ClassNotFoundException e) {
	        System.err.println("No se pudo encontrar la clase del controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "ClassNotFoundException: {0}", e.getMessage());
	    } catch (XMLDBException e) {
			int b = ErrorCodes.VENDOR_ERROR;
			int a = e.vendorErrorCode;
			System.out.println("Error a:" + a);
			System.out.println("Error b:" + b);
			System.out.println("Mensaje de error 2" + e.getMessage());
			Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
	        System.err.println("Ocurrió un error inesperado.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "Exception: {0}", e.getMessage());
	    }
		return idSc;
	}
	
	/**
	 * Obtiene y muestra los jugadores de un equipo específico almacenado en una colección XML.
	 * El usuario selecciona el equipo a través de un ID previamente mostrado por el método
	 * <b>#mostrarEquiposExistentes(String)</b>. Luego, se consulta la base de datos eXist-db
	 * para obtener los jugadores que pertenecen a dicho equipo y se muestran en la consola.
	 * 
	 * @param nomCol El nombre de la colección en la base de datos eXist-db donde se almacenan los jugadores.
	 * @see mostrarEquiposExistentes(String).
	 * @throws InstantiationException Si ocurre un error al instanciar el controlador de la base de datos.
	 * @throws IllegalAccessException Si el acceso al controlador de la base de datos es ilegal.
	 * @throws ClassNotFoundException Si no se pudo encontrar la clase del controlador de la base de datos.
	 * @throws XMLDBException Si ocurre un error al interactuar con la base de datos XML.
	 * @throws Exception Si ocurre cualquier otro tipo de error inesperado.
	 */
	public void obtenerJugadoresDeEquipo(String nomCol ) {
		
		Database dbDriver;
		Collection col;
		int idEquipoSeleccionado =-1;
		Scanner sc = new Scanner(System.in);
		try {
			
			idEquipoSeleccionado = mostrarEquiposExistentes("/ColeccionBaloncestoHibernate");
			System.out.println("Los jugadores del equipo con ID '"+idEquipoSeleccionado+"' son los siguientes:");
			dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			col = DatabaseManager.getCollection(uri+ nomCol,userName,password);
			
			int contadorElementos = 0;
			if(col == null) {
				System.out.println("La colección no existe.");
			}
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			ResourceSet result = servicio.query(
					"for $j in /Jugadores/Jugador "+
					"let $idJ := $j/idJugador "+
					"let $idE := $j/idEquipo "+
					"let $nombre := $j/nombre "+
					"let $apellidos := $j/apellidos "+
					"let $alturaCm := $j/alturaCM "+
					"let $edad := $j/edad "+
					"let $nacionalidad := $j/nacionalidad "+
					"where number($idE) ="+idEquipoSeleccionado+" "+
					"return concat('Jugador ID: ', string($idJ), ', Equipo ID: ', string($idE), ', Nombre: ', string($nombre), "+
					"', Apellidos: ', string($apellidos), ', Altura (cm): ', string($alturaCm), "+
					"', Edad: ', string($edad), ', Nacionalidad: ', string($nacionalidad))"
					);
			ResourceIterator i;
			i= result.getIterator();
			
			if(!i.hasMoreResources()) {
				System.out.println("La consulta no devuelve nada.");
			}
			while (i.hasMoreResources()) {
				contadorElementos++;
				Resource r = i.nextResource();
				System.out.println((String)r.getContent());
			}
			System.out.println("Se han encontrado "+contadorElementos+" elementos.");
			col.close();
			
	    } catch (InstantiationException e) {
	        System.err.println("Error al instanciar el controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "InstantiationException: {0}", e.getMessage());
	    } catch (IllegalAccessException e) {
	        System.err.println("Acceso ilegal al controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "IllegalAccessException: {0}", e.getMessage());
	    } catch (ClassNotFoundException e) {
	        System.err.println("No se pudo encontrar la clase del controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "ClassNotFoundException: {0}", e.getMessage());
	    } catch (XMLDBException e) {
			int b = ErrorCodes.VENDOR_ERROR;
			int a = e.vendorErrorCode;
			System.out.println("Error a:" + a);
			System.out.println("Error b:" + b);
			System.out.println("Mensaje de error 2" + e.getMessage());
			Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
	        System.err.println("Ocurrió un error inesperado.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "Exception: {0}", e.getMessage());
	    }
	}
	
	/**
	 * Obtiene y muestra los jugadores con la valoración más alta entre todos los partidos jugados.
	 * En caso de empate, muestra todos los jugadores que tienen la misma valoración máxima.
	 * 
	 * La consulta se realiza a través de la base de datos eXist-db, buscando el jugador o los jugadores
	 * con la máxima valoración de todos los jugadores registrados en la colección. El método también muestra
	 * los detalles del jugador como su ID, equipo, nombre, apellidos, altura, edad y nacionalidad.
	 * 
	 * @param nomCol El nombre de la colección en la base de datos eXist-db que contiene los datos de los jugadores y las valoraciones de los partidos.
	 * 
	 * @throws InstantiationException Si ocurre un error al instanciar el controlador de la base de datos.
	 * @throws IllegalAccessException Si el acceso al controlador de la base de datos es ilegal.
	 * @throws ClassNotFoundException Si no se puede encontrar la clase del controlador de la base de datos.
	 * @throws XMLDBException Si ocurre un error al interactuar con la base de datos XML.
	 * @throws Exception Si ocurre cualquier otro tipo de error inesperado.
	 */
	public void obtenerJugadoresConValoracionMasAlta(String nomCol ) {
		
		Database dbDriver;
		Collection col;
		Scanner sc = new Scanner(System.in);
		try {
			
			System.out.println("El jugador, o los jugadores (en caso de empate) con valoración más alta son los siguientes:");
			dbDriver = (Database) Class.forName("org.exist.xmldb.DatabaseImpl").newInstance();
			DatabaseManager.registerDatabase(dbDriver);
			col = DatabaseManager.getCollection(uri+ nomCol,userName,password);
			
			int contadorElementos = 0;
			if(col == null) {
				System.out.println("La colección no existe.");
			}
			XPathQueryService servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
			ResourceSet result = servicio.query(
				    "for $d in /DatosJugadoresPartidos/Datosjugadorpartido " +
				    "let $maxValoracion := max(/DatosJugadoresPartidos/Datosjugadorpartido/number(valoracion)) " +
				    "let $idJ := $d/idJ " +
				    "where number($d/valoracion) = $maxValoracion " +
				    "return " +
				    "for $j in /Jugadores/Jugador " +
				    "let $idE := $j/idEquipo " +
				    "let $nombre := $j/nombre " +
				    "let $apellidos := $j/apellidos " +
				    "let $alturaCm := $j/alturaCM " +
				    "let $edad := $j/edad " +
				    "let $nacionalidad := $j/nacionalidad "+
				    "where $j/idJugador = $idJ " +
				    "return concat('Jugador ID: ', string($idJ), ', Equipo ID: ', string($idE),', Valoración Máxima: ', string($d/valoracion),', Nombre: ', string($nombre), "+
				    "', Apellidos: ', string($apellidos), ', Altura (cm): ', string($alturaCm), "+
				    "', Edad: ', string($edad), ', Nacionalidad: ', string($nacionalidad))"
				);
			ResourceIterator i;
			i= result.getIterator();
			
			if(!i.hasMoreResources()) {
				System.out.println("La consulta no devuelve nada.");
			}
			 
			while (i.hasMoreResources()) {
				contadorElementos++;
				Resource r = i.nextResource();
				String resultado = (String)r.getContent();
				System.out.println(resultado);
				
			}
			System.out.println("Se han encontrado "+contadorElementos+" elementos.");
			col.close();
			
	    } catch (InstantiationException e) {
	        System.err.println("Error al instanciar el controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "InstantiationException: {0}", e.getMessage());
	    } catch (IllegalAccessException e) {
	        System.err.println("Acceso ilegal al controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "IllegalAccessException: {0}", e.getMessage());
	    } catch (ClassNotFoundException e) {
	        System.err.println("No se pudo encontrar la clase del controlador de base de datos.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "ClassNotFoundException: {0}", e.getMessage());
	    } catch (XMLDBException e) {
			int b = ErrorCodes.VENDOR_ERROR;
			int a = e.vendorErrorCode;
			System.out.println("Error a:" + a);
			System.out.println("Error b:" + b);
			System.out.println("Mensaje de error 2" + e.getMessage());
			Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, null, e);
		} catch (Exception e) {
	        System.err.println("Ocurrió un error inesperado.");
	        Logger.getLogger(ConsultasXML.class.getName()).log(Level.SEVERE, "Exception: {0}", e.getMessage());
	    }
	}

}
