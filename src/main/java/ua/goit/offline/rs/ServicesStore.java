package ua.goit.offline.rs;

import static ua.goit.offline.rs.parsing.ParsingUtility.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ua.goit.offline.rs.parsing.ParsingUtility.Invocation;

public class ServicesStore {
  private static final ServicesStore instance =
      new ServicesStore();

  private Map<String, Invocation> getInvocations;
  private Map<Class<?>, Object> services;

  private ServicesStore() {
    this.getInvocations = new ConcurrentHashMap<>();
    this.services = new HashMap<>();
  }

  public void addService(Object service) {
    for (Invocation invocation : parse(service.getClass())) {
      getInvocations.put(invocation.getUrl(), invocation);
    }
    services.put(service.getClass(), service);
  }

  public Invocation getService(String url) {
    return getInvocations.get(url);
  }

  public Object getServiceInstance(Class<?> clazz) {
    return services.get(clazz);
  }

  public static ServicesStore getInstance() {
    return instance;
  }

}
