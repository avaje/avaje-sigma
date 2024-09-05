package io.avaje.sigma;

import com.amazonaws.services.lambda.runtime.Context;
import io.avaje.sigma.aws.events.AWSHttpResponse;
import io.avaje.sigma.aws.events.AWSRequest;
import io.avaje.sigma.body.BodyMapper;
import io.avaje.sigma.core.DSigma;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Configures and creates an {@link HttpFunction} for serving AWS Lambda requests.
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
   * Create a Sigma instance used to create a function for AWS request Handler.
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
   *
   *  @return default Sigma instance to configure
   */
  static Sigma create() {
    return new DSigma();
  }

  /** Add routes and handlers to the router. */
  Sigma routing(Consumer<Router> routes);

  /** Add HttpServices to configure the routing and handlers to the routing. */
  Sigma routing(Collection<HttpService> routes);

  /** Add a BodyMapper. Multiple mappers can be added for different media types */
  Sigma addBodyMapper(BodyMapper mapper);

  /** Set to true to ignore trailing slashes. Defaults to true. */
  Sigma ignoreTrailingSlashes(boolean ignoreTrailingSlashes);

  /** Create a function that will serve aws requests to the registered routes */
  HttpFunction createHttpFunction();

  public interface HttpFunction {
    <T extends AWSRequest> AWSHttpResponse apply(T request, Context ctx);
  }
}
