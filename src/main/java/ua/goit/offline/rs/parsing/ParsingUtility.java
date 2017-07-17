package ua.goit.offline.rs.parsing;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * Created by andreymi on 7/17/2017.
 */
public class ParsingUtility {

  public static List<Invocation> parse(Class<?> clazz) {
    List<Invocation> invocations = new ArrayList<>();
    for (Method method : clazz.getMethods()) {
      if (method.isAnnotationPresent(GET.class)) {
        String url = method.getName();
        if (method.isAnnotationPresent(Path.class)) {
          Path path = method.getAnnotation(Path.class);
          url = path.value();
        }
        Map<Integer, String> params = new HashMap<>();
        for (int i = 0; i < method.getParameterCount(); i++) {
          TypeVariable<?> variable = method.getTypeParameters()[i];
          if (variable.isAnnotationPresent(QueryParam.class)) {
            QueryParam param = variable.getAnnotation(QueryParam.class);
            String paramName = param.value();
            params.put(i, paramName);
          }
        }
        invocations.add(new Invocation(url, method, params, method.getParameterCount()));
      }
    }
    return invocations;
  }

  public static class Invocation {
    private String url;
    private Method method;
    private Map<Integer, String> params;
    private int paramCount;

    public Invocation(String url, Method method, Map<Integer, String> params, int paramCount) {
      this.url = url;
      this.method = method;
      this.params = params;
      this.paramCount = paramCount;
    }

    public String getUrl() {
      return url;
    }

    public Method getMethod() {
      return method;
    }

    public Map<Integer, String> getParams() {
      return params;
    }

    public int getParamCount() {
      return paramCount;
    }
  }
}
