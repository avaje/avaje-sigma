package io.avaje.sigma.routes;

import io.avaje.sigma.ExceptionHandler;
import io.avaje.sigma.HttpContext;
import io.avaje.sigma.HttpFilter;
import io.avaje.sigma.Router;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

final class Routes implements SpiRoutes {

  /** The "real" handlers by http method. */
  private final EnumMap<Router.HttpMethod, RouteIndex> typeMap;

  /** The filters. */
  private final List<HttpFilter> filters;

  private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers;

  Routes(
      EnumMap<Router.HttpMethod, RouteIndex> typeMap,
      List<HttpFilter> filters,
      Map<Class<?>, ExceptionHandler<?>> exceptionHandlers) {
    this.typeMap = typeMap;
    this.filters = filters;
    this.exceptionHandlers = exceptionHandlers;
  }

  @Override
  public Entry match(Router.HttpMethod type, String pathInfo) {

    final var routeIndex = typeMap.get(type);

    if (routeIndex == null) return null;

    return routeIndex.match(pathInfo);
  }

  @Override
  public void handleException(HttpContext ctx, Exception e) {

    var exHandler = find(e.getClass());
    if (exHandler != null) {
      exHandler.handle(ctx, e);
    } else {
      ctx.status(500)
          .json("{\"error\":\"Failed to process request: %s\"}".formatted(e.getMessage()));
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Exception> ExceptionHandler<Exception> find(Class<T> exceptionType) {
    Class<?> type = exceptionType;
    do {
      final ExceptionHandler<?> handler = exceptionHandlers.get(type);
      if (handler != null) {
        return (ExceptionHandler<Exception>) handler;
      }
      type = type.getSuperclass();
    } while (type != null);
    return null;
  }

  @Override
  public List<HttpFilter> filters() {
    return filters;
  }
}
