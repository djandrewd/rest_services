package ua.goit.offline.rs.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import ua.goit.offline.rs.DispatcherServlet;
import ua.goit.offline.rs.ServicesStore;
import ua.goit.offline.rs.convertors.ConvertersStore;
import ua.goit.offline.rs.services.WeatherService;

/**
 * Context initialize listener.
 * <p>
 * Created by andreymi on 7/18/2017.
 */
@WebListener
public class InitializeListener implements ServletContextListener {

  private static ConvertersStore convertersStore = new ConvertersStore();
  private static ServicesStore servicesStore = new ServicesStore(convertersStore);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    registerServices();
    //
    ServletContext servletContext = sce.getServletContext();
    ServletRegistration.Dynamic servlet = servletContext
        .addServlet("dispatcher", new DispatcherServlet(servicesStore));
    servlet.setLoadOnStartup(1);
    servlet.addMapping("/services/*");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {

  }

  private void registerServices() {
    servicesStore.addService(new WeatherService());
  }
}
