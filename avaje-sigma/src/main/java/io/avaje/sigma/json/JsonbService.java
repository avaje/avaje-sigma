package io.avaje.sigma.json;

import io.avaje.jsonb.Jsonb;

public class JsonbService implements JsonService {

  Jsonb delegate;

  public JsonbService(Jsonb delegate) {
    this.delegate = delegate;
  }

  public JsonbService() {
    this.delegate = Jsonb.builder().build();
  }

  @Override
  public <T> T jsonRead(Class<T> clazz, String body) {

    return delegate.type(clazz).fromJson(body);
  }

  @Override
  public String jsonWrite(Object bean) {
    return delegate.toJson(bean);
  }
}
