package io.avaje.sigma.routes;

import io.avaje.sigma.RequestHandler;
import io.avaje.sigma.HttpContext;
import java.util.Map;

final class RouteEntry implements SpiRoutes.Entry {

  private final PathParser path;
  private final RequestHandler handler;

  RouteEntry(PathParser path, RequestHandler handler) {
    this.path = path;
    this.handler = handler;
  }

  @Override
  public boolean matches(String requestUri) {
    return path.matches(requestUri);
  }

  @Override
  public RequestHandler handler() {
    return handler;
  }

  @Override
  public Map<String, String> pathParams(String uri) {
    return path.extractPathParams(uri);
  }

  @Override
  public String matchPath() {
    return path.raw();
  }

  @Override
  public int segmentCount() {
    return path.segmentCount();
  }

  @Override
  public boolean multiSlash() {
    return path.multiSlash();
  }

  @Override
  public boolean literal() {
    return path.literal();
  }
}
