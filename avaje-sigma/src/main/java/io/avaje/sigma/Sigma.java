package io.avaje.sigma;

import com.amazonaws.services.lambda.runtime.Context;
import io.avaje.sigma.aws.events.AWSHttpResponse;
import io.avaje.sigma.aws.events.AWSRequest;
import io.avaje.sigma.body.BodyMapper;
import io.avaje.sigma.core.DSigma;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Create, configure and start Sigma.
 *
 * <pre>{@code
 * final HttpFunction app = Sigma.create()
 *   .routing(routing -> routing
 *     .get("/", ctx -> ctx.text("hello world"))
 *     .get("/one", ctx -> ctx.text("one"))
 *   .createHttpFunction();
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

  /** Set the BodyMapper. */
  Sigma addBodyMapper(BodyMapper jsonService);

  /** Set to true to ignore trailing slashes. Defaults to true. */
  Sigma ignoreTrailingSlashes(boolean ignoreTrailingSlashes);

  HttpFunction createHttpFunction();

  public interface HttpFunction {
    <T extends AWSRequest> AWSHttpResponse apply(T request, Context ctx);
  }
}
