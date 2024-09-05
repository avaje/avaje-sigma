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
   * @return default Sigma instance to configure
   */
  static Sigma create() {
    return new DSigma();
  }

  /**
   * Adds routes to the Sigma instance.
   *
   * @param routes A consumer that defines the routes.
   * @return The Sigma instance for method chaining.
   */
  Sigma routing(Consumer<Router> routes);

  /**
   * Adds a collection of {@link HttpService} instances to the Sigma instance.
   *
   * @param routes A collection of {@link HttpService} instances.
   * @return The Sigma instance for method chaining.
   */
  Sigma routing(Collection<HttpService> routes);

  /**
   * Adds a {@link BodyMapper} to the Sigma instance.. Multiple mappers can be added for different
   * media types
   *
   * @param mapper The {@link BodyMapper} to add.
   * @return The Sigma instance for method chaining.
   */
  Sigma addBodyMapper(BodyMapper mapper);

  /**
   * Sets whether to ignore trailing slashes in URLs. Defaults to true.
   *
   * @param ignoreTrailingSlashes Whether to ignore trailing slashes.
   * @return The Sigma instance for method chaining.
   */
  Sigma ignoreTrailingSlashes(boolean ignoreTrailingSlashes);

  /**
   * Creates an {@link HttpFunction} that can handle AWS Lambda requests.
   *
   * @return The created {@link HttpFunction}.
   */
  HttpFunction createHttpFunction();

  public interface HttpFunction {
    /**
     * Handles an incoming AWS Lambda request.
     *
     * @param request The incoming AWS request.
     * @param ctx The Lambda context.
     * @return The HTTP response to be sent back to the client.
     */
    <T extends AWSRequest> AWSHttpResponse apply(T request, Context ctx);
  }
}
