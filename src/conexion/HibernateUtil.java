package conexion;


import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;


public class HibernateUtil {

  private static final SessionFactory sessionFactory;
  
  static {
    try {
      sessionFactory = new Configuration().configure().buildSessionFactory();
    } catch (Throwable ex) {
      System.err.println("Fallo en la creación de session Factory" + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }
  
  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}
