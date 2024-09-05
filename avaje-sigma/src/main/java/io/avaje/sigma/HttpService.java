package io.avaje.sigma;

public interface HttpService {
/**
 * Registers routes to the {@link Router}
 * @param router the current router
 */
  void setup(Router router);
}
