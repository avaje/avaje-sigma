package io.avaje.sigma.routes;

import io.avaje.sigma.HttpContext;
import io.avaje.sigma.Router;
import java.util.Map;

/** Route matching and filter handling. */
public interface SpiRoutes {

  /** Find the matching handler entry given the type and request URI. */
  Entry match(Router.HttpMethod type, String pathInfo);

  /** Execute all appropriate before filters for the given request URI. */
  void before(String pathInfo, HttpContext ctx) throws Exception;

  /** Execute all appropriate after filters for the given request URI. */
  void after(String pathInfo, HttpContext ctx) throws Exception;

  void handleException(HttpContext ctx, Exception e);

  /** A route entry. */
  interface Entry {

    /** Return true if it matches the request URI. */
    boolean matches(String requestUri);

    /** Handle the request. */
    void handle(HttpContext ctx) throws Exception;

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
