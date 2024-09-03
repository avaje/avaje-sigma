package io.avaje.sigma.json;

public interface JsonService {

  <T> T jsonRead(Class<T> clazz, String body);

  String jsonWrite(Object bean);

}
