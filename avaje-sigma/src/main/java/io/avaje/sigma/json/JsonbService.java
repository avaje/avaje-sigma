package io.avaje.sigma.json;

import io.avaje.jsonb.Jsonb;
import io.avaje.sigma.aws.events.APIGatewayV2HttpEvent;

public class JsonbService implements JsonService {

  Jsonb delegate;

  public JsonbService(Jsonb delegate) {
    this.delegate = delegate;
    testState();
  }

  public JsonbService() {
    this.delegate = Jsonb.builder().build();
  }

  private void testState() {

    try {

      delegate.adapter(APIGatewayV2HttpEvent.class);

    } catch (Exception e) {

      throw new IllegalStateException(
          "Missing @Json.Import({ALBHttpEvent.class, APIGatewayV2HttpEvent.class, APIGatewayRestEvent.class})");
    }
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
