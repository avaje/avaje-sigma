package io.avaje.sigma;

/**
 * Functional interface for handling HTTP requests.
 *
 * @see HttpContext
 */
@FunctionalInterface
public interface Handler {

  /**
   * Handles the given HTTP request context.
   *
   * @param req The HTTP request context.
   */
  void handle(HttpContext req);
}
