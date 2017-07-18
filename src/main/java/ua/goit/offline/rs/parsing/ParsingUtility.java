package ua.goit.offline.rs.parsing;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ua.goit.offline.rs.convertors.ConvertersStore;

/**
 * Parse servlet presentation into {@link Invocation} structure.
 *
 * @author Andrey Minov
 */
public class ParsingUtility {

  /**
   * Parse service class into internal structure presentation
   *
   * @param convertersStore the converters store for storing converters
   * @param clazz           the clazz of service to register
   * @return the list on method invocations that will be registered by this class.
   */
  @SuppressWarnings("unchecked")
  public static List<Invocation> parse(ConvertersStore convertersStore, Class<?> clazz) {
    List<Invocation> invocations = new ArrayList<>();
    for (Method method : clazz.getMethods()) {
      MethodType methodType = getMethodType(method);
      if (methodType == null) {
        continue;
      }
      String url = getMethodUrl(clazz, method);
      Map<Integer, Invocation.Parameter> params = new HashMap<>();

      String inMediaType = getInMediaType(clazz, method);
      String outMediaType = getOutMediaType(clazz, method);

      for (int i = 0; i < method.getParameterCount(); i++) {
        Parameter parameter = method.getParameters()[i];
        String name = getParamName(parameter);
        if (!isNullOrEmpty(name)) {
          Function<String, ?> converter = convertersStore
              .getInConverter(parameter.getType(), inMediaType);
          params.put(i, new Invocation.Parameter(name, converter));
        }
      }
      invocations.add(new Invocation(url, method, params, method
          .getParameterCount(), methodType, outMediaType, convertersStore
          .getOutConverter((Class<Object>) clazz, outMediaType)));
    }
    return invocations;
  }

  private static MethodType getMethodType(Method method) {
    if (method.isAnnotationPresent(GET.class)) {
      return MethodType.GET;
    }
    if (method.isAnnotationPresent(POST.class)) {
      return MethodType.POST;
    }
    return null;
  }

  private static String getMethodUrl(Class<?> clazz, Method method) {
    StringBuilder uri = new StringBuilder();
    if (clazz.isAnnotationPresent(Path.class)) {
      String path = clazz.getAnnotation(Path.class).value();
      if (!path.startsWith("/")) {
        uri.append("/");
      }
      uri.append(path);
    }
    if (method.isAnnotationPresent(Path.class)) {
      String path = method.getAnnotation(Path.class).value();
      if (!path.startsWith("/")) {
        uri.append("/");
      }
      uri.append(path);
    } else {
      uri.append("/");
      uri.append(method.getName());
    }
    return uri.toString();
  }

  private static String getParamName(Parameter parameter) {
    String name = null;
    if (parameter.isNamePresent()) {
      name = parameter.getName();
    }
    if (parameter.isAnnotationPresent(QueryParam.class)) {
      QueryParam param = parameter.getAnnotation(QueryParam.class);
      name = param.value();
    }
    return name;
  }

  private static String getInMediaType(Class<?> clazz, Method method) {
    String type = MediaType.TEXT_PLAIN;
    if (clazz.isAnnotationPresent(Consumes.class)) {
      type = clazz.getAnnotation(Consumes.class).value()[0];
    }
    if (method.isAnnotationPresent(Consumes.class)) {
      type = method.getAnnotation(Consumes.class).value()[0];
    }
    return type;
  }

  private static String getOutMediaType(Class<?> clazz, Method method) {
    String type = MediaType.TEXT_PLAIN;
    if (clazz.isAnnotationPresent(Produces.class)) {
      type = clazz.getAnnotation(Produces.class).value()[0];
    }
    if (method.isAnnotationPresent(Produces.class)) {
      type = method.getAnnotation(Produces.class).value()[0];
    }
    return type;
  }
}
