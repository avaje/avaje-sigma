package io.avaje.sigma.body;

/**
 * Provides a mechanism for mapping HTTP request and response bodies.
 *
 * <p>This interface defines methods for reading and writing bodies in a specific media type. By
 * default, the supported media type is "application/json". Subclasses can override the
 * `mediaType()` method to support different media types.
 *
 * @see #mediaType()
 * @see #readBody(Class, String)
 * @see #writeBody(Object)
 */
public interface BodyMapper {

  /**
   * Returns the default supported media type for this mapper.
   *
   * <p>This method can be overridden by subclasses to support different media types.
   *
   * @return The default supported media type (e.g., "application/json").
   */
  default String mediaType() {
    return "application/json";
  }

  /**
   * Reads a body string and deserializes it into an object of the specified class.
   *
   * @param clazz The class of the object to deserialize.
   * @param body The body string to deserialize.
   * @return The deserialized object.
   */
  <T> T readBody(Class<T> clazz, String body);

  /**
   * Serializes an object into a string representation.
   *
   * @param bean The object to serialize.
   * @return The serialized string representation.
   */
  String writeBody(Object bean);
}
