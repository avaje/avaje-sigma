package io.avaje.sigma.routes;

import io.avaje.sigma.ExceptionHandler;
import io.avaje.sigma.HttpFilter;
import io.avaje.sigma.Router;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RoutesBuilder {

  private final EnumMap<Router.HttpMethod, RouteIndex> typeMap =
      new EnumMap<>(Router.HttpMethod.class);
  private final List<HttpFilter> filters;
  private final boolean ignoreTrailingSlashes;
  private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers;

  public RoutesBuilder(Router routing, boolean ignoreTrailingSlashes) {
    this.exceptionHandlers = routing.exceptionHandlers();
    this.ignoreTrailingSlashes = ignoreTrailingSlashes;
    this.filters = List.copyOf(routing.filters());
    for (Router.Entry handler : routing.all()) {
      typeMap.computeIfAbsent(handler.type(), h -> new RouteIndex()).add(convert(handler));
    }
  }

  private SpiRoutes.Entry convert(Router.Entry handler) {
    final PathParser pathParser = new PathParser(handler.path(), ignoreTrailingSlashes);
    return new RouteEntry(pathParser, handler.handler());
  }

  public SpiRoutes build() {
    return new Routes(typeMap, filters, exceptionHandlers);
  }
}
