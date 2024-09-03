package io.avaje.sigma;

import java.util.Collection;
import java.util.function.Consumer;

import io.avaje.sigma.core.DSigma;
import io.avaje.sigma.json.JsonService;

/**
 * Create, configure and start Sigma.
 *
 * <pre>{@code
 * final AWSHttpHandler app = Sigma.create()
 *   .routing(routing -> routing
 *     .get("/", ctx -> ctx.text("hello world"))
 *     .get("/one", ctx -> ctx.text("one"))
 *   .createAWSHandler();
 *
 * }</pre>
 */
public interface Sigma {

  /**
   * Create Sigma.
   *
   * <pre>{@code
   * final AWSHttpHandler app = Sigma.create()
   *   .routing(routing -> routing
   *     .get("/", ctx -> ctx.text("hello world"))
   *     .get("/one", ctx -> ctx.text("one"))
   *   .createAWSHandler();
   *
   * app.shutdown();
   *
   * }</pre>
   */
  static Sigma create() {
    return new DSigma();
  }

  /** Add routes and handlers to the routing. */
  Sigma routing(Consumer<Routing> routes);

  /** Add many routes and handlers to the routing. */
  Sigma routing(Collection<HttpService> routes);

  /** Set the JsonService. */
  Sigma jsonService(JsonService jsonService);

  /** Set to true to ignore trailing slashes. Defaults to true. */
  Sigma ignoreTrailingSlashes(boolean ignoreTrailingSlashes);

  AWSHttpHandler createAWSHandler();
}
