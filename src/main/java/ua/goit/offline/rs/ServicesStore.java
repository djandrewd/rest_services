package ua.goit.offline.rs;

import static ua.goit.offline.rs.parsing.ParsingUtility.parse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ua.goit.offline.rs.parsing.ParsingUtility.Invocation;

public class ServicesStore {
  private static final ServicesStore instance =
      new ServicesStore();

  private Map<String, Invocation> getInvocations;

  private ServicesStore() {
    this.getInvocations = new ConcurrentHashMap<>();
  }

  public void addService(Class<?> clazz) {
    for (Invocation invocation : parse(clazz)) {
      getInvocations.put(invocation.getUrl(), invocation);
    }
  }

  public Invocation getService(String url) {
    return getInvocations.get(url);
  }

  public static ServicesStore getInstance() {
    return instance;
  }
}
