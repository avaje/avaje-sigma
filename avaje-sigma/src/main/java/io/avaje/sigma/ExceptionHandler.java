package io.avaje.sigma;

/**
 * Defines an interface for handling exceptions in a given HTTP context.
 *
 * @param <T> The specific type of exception to handle.
 */
public interface ExceptionHandler<T extends Exception> {

  /**
   * Handles the given exception.
   *
   * @param exception The exception to handle.
   * @param ctx The HTTP context of the invocation.
   */
  void handle(T exception, HttpContext ctx);
}
