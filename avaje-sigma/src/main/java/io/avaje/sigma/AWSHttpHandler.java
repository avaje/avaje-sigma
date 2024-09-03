package io.avaje.sigma;

import com.amazonaws.services.lambda.runtime.Context;

import io.avaje.sigma.aws.events.AWSHttpResponse;
import io.avaje.sigma.aws.events.AWSRequest;

/**
 * The configured handler that takes AWS HTTP Requests, routes the request to the correct function
 * and performs error handling
 */
public interface AWSHttpHandler {
  <T extends AWSRequest> AWSHttpResponse handle(T request, Context ctx);
}
