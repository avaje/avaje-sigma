package io.avaje.sigma.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.jsonb.JsonIoException;

public class JacksonService implements JsonService {

  ObjectMapper delegate;

  public JacksonService(ObjectMapper delegate) {
    this.delegate = delegate;
  }

  public JacksonService() {
    this.delegate = new ObjectMapper();
  }

  @Override
  public <T> T jsonRead(Class<T> clazz, String body) {

    try {
      return delegate.readValue(body, clazz);
    } catch (JsonProcessingException e) {
      throw new JsonIoException(e);
    }
  }

  @Override
  public String jsonWrite(Object bean) {
    try {
      return delegate.writeValueAsString(bean);
    } catch (JsonProcessingException e) {
      throw new JsonIoException(e);
    }
  }
}
