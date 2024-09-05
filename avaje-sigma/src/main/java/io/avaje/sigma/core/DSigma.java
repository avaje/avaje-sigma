package io.avaje.sigma.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import io.avaje.sigma.HttpService;
import io.avaje.sigma.Router;
import io.avaje.sigma.Sigma;
import io.avaje.sigma.body.BodyMapper;
import io.avaje.sigma.body.JacksonBodyMapper;
import io.avaje.sigma.body.JsonbBodyMapper;
import io.avaje.sigma.routes.RoutesBuilder;

public final class DSigma implements Sigma {

  private final Router routing = new DefaultRouting();
  private final List<BodyMapper> bodyMappers = new ArrayList<>();
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
  public Sigma routing(Consumer<Router> consumer) {
    consumer.accept(routing);
    return this;
  }

  @Override
  public Sigma addBodyMapper(BodyMapper mapper) {
    this.bodyMappers.add(mapper);
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

    if (bodyMappers.isEmpty()) bodyMappers.add(getJsonService());

    var routes = new RoutesBuilder(routing, this.ignoreTrailingSlashes).build();
    return new DSigmaFunction(routes, new ServiceManager(bodyMappers));
  }

  private BodyMapper getJsonService() {

    try {
      Class.forName("io.avaje.jsonb.Jsonb");
      return new JsonbBodyMapper();
    } catch (ClassNotFoundException e) {
      // nothing to do
    }
    try {
      Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
      return new JacksonBodyMapper();
    } catch (ClassNotFoundException e) {
      // nothing to do
    }

    throw new IllegalStateException("No BodyMapper Provided");
  }
}
