package io.avaje.sigma.body;

public interface BodyMapper {

  /**
   * @return supported MediaType
   */
  default String mediaType() {

    return "application/json";
  }

  <T> T readBody(Class<T> clazz, String body);

  String writeBody(Object bean);
}
