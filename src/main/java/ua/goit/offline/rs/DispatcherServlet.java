package ua.goit.offline.rs;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import ua.goit.offline.rs.parsing.Invocation;
import ua.goit.offline.rs.parsing.MethodType;

/**
 * Dispatcher servlet for handing all HTTP requests for some path and delegate
 * execution to services.
 *
 * @author Andrey Minov
 */
public class DispatcherServlet extends HttpServlet {

  private static final String MEDIA_TYPE_HEADER = "Content-Type";

  private ServicesStore servicesStore;

  /**
   * Instantiates a new Dispatcher servlet.
   *
   * @param servicesStore the services store
   */
  public DispatcherServlet(ServicesStore servicesStore) {
    this.servicesStore = servicesStore;
  }

  @Override
  protected void doGet(HttpServletRequest req,
                       HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getRequestURI();
    String servletUri = req.getServletPath();
    String uri = path.replaceAll(servletUri, "");
    if (Strings.isNullOrEmpty(uri)) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    Invocation invocation = servicesStore.getServiceMeta(MethodType.GET, uri);
    if (invocation == null) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    try {
      Object[] params = new Object[invocation.getParamCount()];
      for (Map.Entry<Integer, Invocation.Parameter> reqParam : invocation.getParams().entrySet()) {
        Invocation.Parameter parameter = reqParam.getValue();
        Object param = parameter.getConverter().apply(req.getParameter(parameter.getName()));
        params[reqParam.getKey()] = param;
      }

      Method method = invocation.getMethod();
      Object instance = servicesStore.getServiceInstance(method.getDeclaringClass());
      String out = invocation.getResponseConverter().apply(method.invoke(instance, params));
      resp.setHeader(MEDIA_TYPE_HEADER, invocation.getMediaType());
      resp.getOutputStream().println(out);
      resp.getOutputStream().flush();
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

}
