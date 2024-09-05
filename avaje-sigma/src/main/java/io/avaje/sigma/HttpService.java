package io.avaje.sigma;

/** Encapsulates a set of routing rules and related logic. */
public interface HttpService {
  /**
   * Registers routes to the {@link Router}
   *
   * @param router the router to configure
   */
  void setup(Router router);
}
