package io.avaje.sigma.body;

import io.avaje.jsonb.Jsonb;

public class JsonbBodyMapper implements BodyMapper {

  Jsonb delegate;

  public JsonbBodyMapper(Jsonb delegate) {
    this.delegate = delegate;
  }

  public JsonbBodyMapper() {
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
