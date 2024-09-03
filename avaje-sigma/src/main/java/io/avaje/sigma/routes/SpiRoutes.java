package io.avaje.sigma.routes;

import io.avaje.sigma.HttpContext;
import io.avaje.sigma.Routing;
import java.util.Map;

/** Route matching and filter handling. */
public interface SpiRoutes {

  /** Find the matching handler entry given the type and request URI. */
  Entry match(Routing.HttpMethod type, String pathInfo);

  /** Execute all appropriate before filters for the given request URI. */
  void before(String pathInfo, HttpContext ctx);

  /** Execute all appropriate after filters for the given request URI. */
  void after(String pathInfo, HttpContext ctx);

  void handleException(HttpContext ctx, Exception e);

  /** A route entry. */
  interface Entry {

    /** Return true if it matches the request URI. */
    boolean matches(String requestUri);

    /**
     * Handle the request.
     *
     * @throws Exception
     */
    void handle(HttpContext ctx);

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
