package io.avaje.sigma.routes;

import io.avaje.sigma.Handler;
import io.avaje.sigma.HttpContext;
import io.avaje.sigma.Router;
import java.util.Map;

/** Filter with special matchAll. */
final class FilterEntry implements SpiRoutes.Entry {

  private final String path;
  private final boolean matchAll;
  private final PathParser pathParser;
  private final Handler handler;

  FilterEntry(Router.Entry entry, boolean ignoreTrailingSlashes) {
    this.path = entry.path();
    this.matchAll = "/*".equals(path) || "*".equals(path);
    this.pathParser = matchAll ? null : new PathParser(path, ignoreTrailingSlashes);
    this.handler = entry.handler();
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
  public void handle(HttpContext ctx) throws Exception {
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
