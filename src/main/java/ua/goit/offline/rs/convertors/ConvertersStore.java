package ua.goit.offline.rs.convertors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

/**
 * Storage used for convert string parameters into object types and visa versa.
 * <p/>
 * Support basic conventions from scratch: string, primitives.
 *
 * @author Andrey Minov
 */
public class ConvertersStore {

  private final Map<Class<?>, Function<String, ?>> inConverters;
  private final Map<Class<?>, Function<?, String>> outConverters;
  private final Map<String, BiFunction<String, Class<?>, ?>> mediaTypeInConverters;
  private final Map<String, Function<?, String>> mediaTypeOutConverters;
  private final Gson gson;

  /**
   * Instantiates a new Converters store.
   */
  public ConvertersStore() {
    this.inConverters = new ConcurrentHashMap<>();
    this.outConverters = new ConcurrentHashMap<>();
    this.mediaTypeInConverters = new ConcurrentHashMap<>();
    this.mediaTypeOutConverters = new ConcurrentHashMap<>();
    this.gson = new Gson();
    registerBuildInConverters();
  }

  private void registerBuildInConverters() {
    inConverters.put(int.class, Integer::parseInt);
    inConverters.put(Integer.class, Integer::parseInt);
    inConverters.put(double.class, Double::parseDouble);
    inConverters.put(Double.class, Double::parseDouble);
    inConverters.put(long.class, Long::parseLong);
    inConverters.put(Long.class, Long::parseLong);
    inConverters.put(float.class, Float::parseFloat);
    inConverters.put(Float.class, Float::parseFloat);
    inConverters.put(byte.class, Byte::parseByte);
    inConverters.put(Byte.class, Byte::parseByte);
    inConverters.put(boolean.class, Boolean::parseBoolean);
    inConverters.put(Boolean.class, Boolean::parseBoolean);
    inConverters.put(String.class, Function.identity());
    //
    outConverters.put(int.class, Object::toString);
    outConverters.put(Integer.class, Object::toString);
    outConverters.put(double.class, Object::toString);
    outConverters.put(Double.class, Object::toString);
    inConverters.put(long.class, Object::toString);
    outConverters.put(Long.class, Object::toString);
    outConverters.put(float.class, Object::toString);
    outConverters.put(Float.class, Object::toString);
    outConverters.put(byte.class, Object::toString);
    outConverters.put(Byte.class, Object::toString);
    outConverters.put(boolean.class, Object::toString);
    outConverters.put(Boolean.class, Object::toString);
    outConverters.put(String.class, Function.identity());
    //
    mediaTypeInConverters.put(MediaType.APPLICATION_JSON, gson::fromJson);
    mediaTypeInConverters.put(MediaType.TEXT_PLAIN, (v, c) -> v);
    //
    mediaTypeOutConverters.put(MediaType.APPLICATION_JSON, gson::toJson);
    mediaTypeOutConverters.put(MediaType.TEXT_PLAIN, Function.identity());
  }


  /**
   * Register incoming parameter converter.
   *
   * @param <T>     the type parameter of argument.
   * @param clazz   the clazz to register
   * @param convert the convert function from string to object
   */
  public <T> void registerInConverter(Class<T> clazz, Function<String, T> convert) {
    inConverters.put(clazz, convert);
  }

  /**
   * Register out parameter converter.
   *
   * @param <T>     the type parameter of response.
   * @param clazz   the clazz to register
   * @param convert the convert function from string to object.
   */
  public <T> void registerOutConverter(Class<T> clazz, Function<T, String> convert) {
    outConverters.put(clazz, convert);
  }

  /**
   * Gets  converter for incoming message.
   *
   * @param <T>       the type parameter of parameter
   * @param clazz     the clazz type of parameter
   * @param mediaType the media type of parameters
   * @return the converter from string to java type.
   * @throws IllegalArgumentException when no converter found in storage.
   */
  @SuppressWarnings("unchecked")
  public <T> Function<String, T> getInConverter(Class<T> clazz, String mediaType) {
    Function<String, T> converter = (Function<String, T>) inConverters.get(clazz);
    if (converter != null) {
      return converter;
    }
    BiFunction<String, Class<?>, ?> mediaConverter = mediaTypeInConverters.get(mediaType);
    if (mediaConverter != null) {
      return s -> (T) mediaConverter.apply(s, clazz);
    }
    throw new IllegalArgumentException(String
        .format("Not existed in converter for class %s!", clazz));
  }

  /**
   * Gets  converter for out going message.
   *
   * @param <T>       the type parameter of parameter
   * @param clazz     the clazz type of parameter
   * @param mediaType the media type of parameters
   * @return the converter from java type to string
   * @throws IllegalArgumentException when no converter found in storage.
   */
  @SuppressWarnings("unchecked")
  public <T> Function<T, String> getOutConverter(Class<T> clazz, String mediaType) {
    Function<T, String> converter = (Function<T, String>) outConverters.get(clazz);
    if (converter != null) {
      return converter;
    }
    converter = (Function<T, String>) mediaTypeOutConverters.get(mediaType);
    if (converter != null) {
      return converter;
    }
    throw new IllegalArgumentException(String
        .format("Not existed out converter for class %s!", clazz));
  }

}
