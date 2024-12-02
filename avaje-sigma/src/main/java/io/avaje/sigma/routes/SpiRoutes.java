package io.avaje.sigma.routes;

import io.avaje.sigma.HttpContext;
import io.avaje.sigma.HttpFilter;
import io.avaje.sigma.RequestHandler;
import io.avaje.sigma.Router;

import java.util.List;
import java.util.Map;

/** Route matching and filter handling. */
public sealed interface SpiRoutes permits Routes {

  /** Find the matching handler entry given the type and request URI. */
  Entry match(Router.HttpMethod type, String pathInfo);

  /** all filters. */
  List<HttpFilter> filters();

  void handleException(HttpContext ctx, Exception e);

  /** A route entry. */
  sealed interface Entry permits RouteEntry {

    /** Return true if it matches the request URI. */
    boolean matches(String requestUri);

    /** Get the handler for this request */
    RequestHandler handler();

    /** Return the path parameter map given the uri. */
    Map<String, String> pathParams(String uri);

    /** Return the raw path expression. */
    String matchPath();

    /** Return the segment count. */
    int segmentCount();

    /** Return true if one of the segments is the wildcard match or accepting slashes. */
    boolean multiSlash();

    /** Return true if all segments are literal. */
    boolean literal();
  }
}
