package ua.goit.offline.rs;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ua.goit.offline.rs.parsing.ParsingUtility.Invocation;


@WebServlet(urlPatterns = "/services/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req,
                       HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getContextPath();
    String uri = path.replaceAll("/services/", "");
    Invocation invocation = ServicesStore.getInstance().getService(uri);

    Object[] params = new Object[invocation.getParamCount()];
    for (Map.Entry<Integer, String> reqParam : invocation.getParams().entrySet()) {
      String param = req.getParameter(reqParam.getValue());
      params[reqParam.getKey()] = param;
    }

    Method method  = invocation.getMethod();
    // TODO : method.invoke()
  }
}
