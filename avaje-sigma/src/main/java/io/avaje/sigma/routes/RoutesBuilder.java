package io.avaje.sigma.routes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.avaje.sigma.ExceptionHandler;
import io.avaje.sigma.Routing;

public class RoutesBuilder {

  private final EnumMap<Routing.HttpMethod, RouteIndex> typeMap = new EnumMap<>(Routing.HttpMethod.class);
  private final List<SpiRoutes.Entry> before = new ArrayList<>();
  private final List<SpiRoutes.Entry> after = new ArrayList<>();
  private final boolean ignoreTrailingSlashes;
  private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers;

  public RoutesBuilder(Routing routing, boolean ignoreTrailingSlashes) {
  this.exceptionHandlers=   routing.exceptionHandlers();
    this.ignoreTrailingSlashes = ignoreTrailingSlashes;
    for (Routing.Entry handler : routing.all()) {
      switch (handler.getType()) {
        case BEFORE:
          before.add(filter(handler));
          break;
        case AFTER:
          after.add(filter(handler));
          break;
        default:
          typeMap.computeIfAbsent(handler.getType(), h -> new RouteIndex()).add(convert(handler));
      }
    }
  }

  private FilterEntry filter(Routing.Entry entry) {
    return new FilterEntry(entry, ignoreTrailingSlashes);
  }

  private SpiRoutes.Entry convert(Routing.Entry handler) {
    final PathParser pathParser = new PathParser(handler.getPath(), ignoreTrailingSlashes);
    return new RouteEntry(pathParser, handler.getHandler());
  }

  public SpiRoutes build() {
    return new Routes(typeMap, before, after, exceptionHandlers);
  }
}
