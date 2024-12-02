package io.avaje.sigma;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A router for managing HTTP requests and their corresponding handlers.
 *
 * <p>This class provides methods to add various types of HTTP handlers, including GET, POST, PUT,
 * PATCH, DELETE, HEAD, and TRACE. It also supports before and after filters, as well as exception
 * handlers for specific exception types.
 */
public interface Router {

  /**
   * Adds the routes provided by the given HTTP service.
   *
   * @param routes The HTTP service containing the routes to add.
   * @return This `Router` instance for method chaining.
   */
  Router add(HttpService routes);

  /**
   * Adds all the routes provided by the given HTTP services.
   *
   * @param routes The collection of HTTP services containing the routes to add.
   * @return This `Router` instance for method chaining.
   */
  Router addAll(Collection<HttpService> routes);

  /**
   * Adds a HEAD handler for the specified path.
   *
   * @param path The path to match for HEAD requests.
   * @param handler The handler to invoke when a HEAD request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router head(String path, RequestHandler handler);

  /**
   * Adds a GET handler for the specified path.
   *
   * @param path The path to match for GET requests.
   * @param handler The handler to invoke when a GET request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router get(String path, RequestHandler handler);

  /**
   * Adds a POST handler for the specified path.
   *
   * @param path The path to match for POST requests.
   * @param handler The handler to invoke when a POST request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router post(String path, RequestHandler handler);

  /**
   * Adds a PUT handler for the specified path.
   *
   * @param path The path to match for PUT requests.
   * @param handler The handler to invoke when a PUT request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router put(String path, RequestHandler handler);

  /**
   * Adds a PATCH handler for the specified path.
   *
   * @param path The path to match for PATCH requests.
   * @param handler The handler to invoke when a PATCH request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router patch(String path, RequestHandler handler);

  /**
   * Adds a DELETE handler for the specified path.
   *
   * @param path The path to match for DELETE requests.
   * @param handler The handler to invoke when a DELETE request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router delete(String path, RequestHandler handler);

  /**
   * Adds a TRACE handler for the specified path.
   *
   * @param path The path to match for TRACE requests.
   * @param handler The handler to invoke when a TRACE request matches the path.
   * @return This `Router` instance for method chaining.
   */
  Router trace(String path, RequestHandler handler);

  /** Add a filter for all requests. */
  Router filter(HttpFilter handler);

  /**
   * Adds a before filter for all requests.
   *
   * @param handler The handler to invoke before the actual request handler for all requests.
   * @return This `Router` instance for method chaining.
   */
  default Router before(RequestHandler handler) {
    return filter(
        (ctx, chain) -> {
          handler.handle(ctx);
          chain.proceed();
        });
  }

  /**
   * Adds an after filter for all requests.
   *
   * @param handler The handler to invoke after the actual request handler for all requests.
   * @return This `Router` instance for method chaining.
   */
  default Router after(RequestHandler handler) {
    return filter(
        (ctx, chain) -> {
          chain.proceed();
          handler.handle(ctx);
        });
  }

  /**
   * Registers an exception handler for the given exception type.
   *
   * @param exceptionClass The exception class to handle.
   * @param handler The exception handler to invoke when an exception of the given type occurs.
   * @return This `Router` instance for method chaining.
   */
  <T extends Exception> Router exception(Class<T> exceptionClass, ExceptionHandler<T> handler);

  /**
   * Returns all the registered handlers.
   *
   * @return A list of all registered routing entries.
   */
  List<Entry> all();


  /** Return all the registered filters. */
  List<HttpFilter> filters();

  /**
   * Returns a map of exception classes to their corresponding exception handlers.
   *
   * @return A map containing the registered exception handlers.
   */
  Map<Class<?>, ExceptionHandler<?>> exceptionHandlers();

  /** A routing entry. */
  interface Entry {

    /**
     * Returns the HTTP method associated with this entry.
     *
     * @return The HTTP method (e.g., GET, POST, PUT).
     */
    HttpMethod type();

    /**
     * Returns the full path of the entry.
     *
     * @return The path pattern for matching requests.
     */
    String path();

    /**
     * Returns the handler that will be invoked when a request matches this entry.
     *
     * @return The handler to execute.
     */
    RequestHandler handler();
  }

  /** The type of route entry. */
  enum HttpMethod {
    /** HTTP GET. */
    GET,
    /** HTTP POST. */
    POST,
    /** HTTP PUT. */
    PUT,
    /** HTTP PATCH. */
    PATCH,
    /** HTTP DELETE. */
    DELETE,
    /** HTTP HEAD. */
    HEAD,
    /** HTTP TRACE. */
    TRACE;
  }

}
