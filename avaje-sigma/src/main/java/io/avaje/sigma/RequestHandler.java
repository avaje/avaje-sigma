package io.avaje.sigma;

/**
 * Functional interface for handling HTTP requests.
 *
 * @see HttpContext
 */
@FunctionalInterface
public interface RequestHandler {

  /**
   * Handles the given HTTP request context.
   *
   * @param req The HTTP request context.
   */
  void handle(HttpContext req) throws Exception;
}
