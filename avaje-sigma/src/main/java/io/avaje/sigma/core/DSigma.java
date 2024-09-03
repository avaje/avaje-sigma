package io.avaje.sigma.core;

import java.util.Collection;
import java.util.function.Consumer;

import io.avaje.sigma.HttpService;
import io.avaje.sigma.Routing;
import io.avaje.sigma.Sigma;
import io.avaje.sigma.json.JacksonService;
import io.avaje.sigma.json.JsonService;
import io.avaje.sigma.json.JsonbService;
import io.avaje.sigma.routes.RoutesBuilder;

public final class DSigma implements Sigma {

  private final Routing routing = new DefaultRouting();
  private JsonService jsonService;
  private boolean ignoreTrailingSlashes = true;

  public Sigma routing(HttpService routes) {
    routing.add(routes);
    return this;
  }

  @Override
  public Sigma routing(Collection<HttpService> routes) {
    routing.addAll(routes);
    return this;
  }

  @Override
  public Sigma routing(Consumer<Routing> consumer) {
    consumer.accept(routing);
    return this;
  }

  @Override
  public Sigma jsonService(JsonService jsonService) {
    this.jsonService = jsonService;
    return this;
  }

  /** Set to true to ignore trailing slashes. Defaults to true. */
  @Override
  public Sigma ignoreTrailingSlashes(boolean ignoreTrailingSlashes) {
    this.ignoreTrailingSlashes = ignoreTrailingSlashes;
    return this;
  }

  @Override
  public Sigma.HttpFunction createHttpFunction() {

    if (jsonService == null) jsonService = getJsonService();

    var routes = new RoutesBuilder(routing, this.ignoreTrailingSlashes).build();
    return new DSigmaFunction(routes, new ServiceManager(this.jsonService));
  }

  private JsonService getJsonService() {

    try {
      Class.forName("io.avaje.jsonb.Jsonb");
      return new JsonbService();
    } catch (ClassNotFoundException e) {
      // nothing to do
    }
    try {
      Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
      return new JacksonService();
    } catch (ClassNotFoundException e) {
      // nothing to do
    }

    throw new IllegalStateException("No JsonService Provided");
  }
}
