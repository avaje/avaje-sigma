package io.avaje.sigma.body;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.jsonb.JsonIoException;

public class JacksonService implements BodyMapper {

  ObjectMapper delegate;

  public JacksonService(ObjectMapper delegate) {
    this.delegate = delegate;
  }

  public JacksonService() {
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
