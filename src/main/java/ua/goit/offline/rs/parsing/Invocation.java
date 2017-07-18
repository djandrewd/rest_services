package ua.goit.offline.rs.parsing;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * Presented invocation that must be called when REST method must be called.
 *
 * @author Andrey Minov
 */
public class Invocation {
  private String url;
  private Method method;
  private Map<Integer, Parameter> params;
  private int paramCount;
  private MethodType methodType;
  private String mediaType;
  private Function<Object, String> responseConverter;

  /**
   * Instantiates a new Invocation.
   *
   * @param url               the url on which to register invocation. For example /weather
   * @param method            the method that must be called when calling service
   * @param params            the params to pass to methods above and ones that
   *                          must be get from request.
   * @param paramCount        the number of method call parameters
   * @param methodType        the HTTP method type invocation is bound to.
   * @param mediaType         of response body
   * @param responseConverter the response converter for convertion method response value to string.
   */
  Invocation(String url, Method method, Map<Integer, Parameter> params, int paramCount,
             MethodType methodType, String mediaType, Function<Object, String> responseConverter) {
    this.url = url;
    this.method = method;
    this.params = params;
    this.paramCount = paramCount;
    this.methodType = methodType;
    this.mediaType = mediaType;
    this.responseConverter = responseConverter;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  public String getUrl() {
    return url;
  }

  public Method getMethod() {
    return method;
  }

  public Map<Integer, Parameter> getParams() {
    return params;
  }

  public int getParamCount() {
    return paramCount;
  }


  public Function<Object, String> getResponseConverter() {
    return responseConverter;
  }

  public String getMediaType() {
    return mediaType;
  }

  /**
   * Presents type of method call parameter.
   *
   * @author Andrey Minov
   */
  public static class Parameter {
    private String name;
    private Function<String, ?> converter;

    /**
     * Instantiates a new Parameter.
     *
     * @param name      the name of query parameter
     * @param converter the converter for parameter value
     */
    public Parameter(String name, Function<String, ?> converter) {
      this.name = name;
      this.converter = converter;
    }

    public String getName() {
      return name;
    }

    public Function<String, ?> getConverter() {
      return converter;
    }
  }
}
