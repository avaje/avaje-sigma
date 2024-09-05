package io.avaje.sigma.body;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.jsonb.JsonIoException;

/** BodyMapper based on Jackson's ObjectMapper */
public class JacksonBodyMapper implements BodyMapper {

  ObjectMapper delegate;

  public JacksonBodyMapper(ObjectMapper delegate) {
    this.delegate = delegate;
  }

  public JacksonBodyMapper() {
    this(new ObjectMapper());
  }

  @Override
  public <T> T readBody(Class<T> clazz, String body) {

    try {
      return delegate.readValue(body, clazz);
    } catch (JsonProcessingException e) {
      throw new JsonIoException(e);
    }
  }

  @Override
  public String writeBody(Object bean) {
    try {
      return delegate.writeValueAsString(bean);
    } catch (JsonProcessingException e) {
      throw new JsonIoException(e);
    }
  }
}
