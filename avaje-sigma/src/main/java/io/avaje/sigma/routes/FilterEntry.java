package io.avaje.sigma.routes;

import java.util.Map;

import io.avaje.sigma.Handler;
import io.avaje.sigma.HttpContext;
import io.avaje.sigma.Routing;

/**
 * Filter with special matchAll.
 */
class FilterEntry implements SpiRoutes.Entry {

  private final String path;
  private final boolean matchAll;
  private final PathParser pathParser;
  private final Handler handler;

  FilterEntry(Routing.Entry entry, boolean ignoreTrailingSlashes) {
    this.path = entry.getPath();
    this.matchAll = "/*".equals(path) || "*".equals(path);
    this.pathParser = matchAll ? null : new PathParser(path, ignoreTrailingSlashes);
    this.handler = entry.getHandler();
  }

  @Override
  public String matchPath() {
    return path;
  }

  @Override
  public boolean matches(String requestUri) {
    return matchAll || pathParser.matches(requestUri);
  }

  @Override
  public void handle(HttpContext ctx) {
    handler.handle(ctx);
  }

  @Override
  public Map<String, String> pathParams(String uri) {
    throw new IllegalStateException("not allowed");
  }

  @Override
  public int segmentCount() {
    throw new IllegalStateException("not allowed");
  }

  @Override
  public boolean multiSlash() {
    return pathParser != null && pathParser.multiSlash();
  }

  @Override
  public boolean literal() {
    return pathParser != null && pathParser.literal();
  }
}
