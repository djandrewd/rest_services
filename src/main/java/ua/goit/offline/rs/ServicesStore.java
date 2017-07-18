package ua.goit.offline.rs;

import static java.util.EnumSet.allOf;
import static ua.goit.offline.rs.parsing.ParsingUtility.parse;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ua.goit.offline.rs.convertors.ConvertersStore;
import ua.goit.offline.rs.parsing.Invocation;
import ua.goit.offline.rs.parsing.MethodType;

/**
 * Stores services together with metadata for calling the methods.
 *
 * @author Andrey Minov
 */
public class ServicesStore {

  private ConvertersStore convertersStore;
  private EnumMap<MethodType, Map<String, Invocation>> invocations;
  private Map<Class<?>, Object> services;

  /**
   * Instantiates a new Services store.
   *
   * @param convertersStore the converters store
   */
  public ServicesStore(ConvertersStore convertersStore) {
    this.convertersStore = convertersStore;
    this.invocations = new EnumMap<>(MethodType.class);
    this.services = new HashMap<>();
    invocations.putAll(allOf(MethodType.class).stream().collect(Collectors
        .toMap(Function.identity(), k -> new ConcurrentHashMap<>())));
  }

  /**
   * Add service together with metadata into the storage.
   *
   * @param service the service to register
   */
  public void addService(Object service) {
    for (Invocation invocation : parse(convertersStore, service.getClass())) {
      invocations.get(invocation.getMethodType()).put(invocation.getUrl(), invocation);
    }
    services.put(service.getClass(), service);
  }

  /**
   * Gets service metadata from storage.
   *
   * @param methodType the HTTP method type to call. One of {@link MethodType}
   * @param url        the url on which service is registered
   * @return the service invocation used for service to invoke or null if empty.
   */
  public Invocation getServiceMeta(MethodType methodType, String url) {
    return invocations.get(methodType).get(url);
  }

  /**
   * Gets service instance.
   *
   * @param clazz the clazz
   * @return the service instance stored or null if not registered.
   */
  public Object getServiceInstance(Class<?> clazz) {
    return services.get(clazz);
  }
}
