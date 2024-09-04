package io.avaje.sigma.body;

import io.avaje.jsonb.Jsonb;

public class JsonbService implements BodyMapper {

  Jsonb delegate;

  public JsonbService(Jsonb delegate) {
    this.delegate = delegate;
  }

  public JsonbService() {
    this(Jsonb.builder().build());
  }

  @Override
  public <T> T readBody(Class<T> clazz, String body) {

    return delegate.type(clazz).fromJson(body);
  }

  @Override
  public String writeBody(Object bean) {
    return delegate.toJson(bean);
  }
}
