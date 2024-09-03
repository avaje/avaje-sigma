package io.avaje.sigma.aws.events;

import io.avaje.sigma.Routing;
import java.util.List;
import java.util.Map;

public record ALBHttpEvent(
    RequestContext requestContext,
    Routing.HttpMethod httpMethod,
    String path,
    Map<String, String> queryStringParameters,
    Map<String, List<String>> multiValueQueryStringParameters,
    Map<String, String> headers,
    Map<String, List<String>> multiValueHeaders,
    String body,
    boolean isBase64Encoded)
    implements AWSRequest {

  public record RequestContext(Elb elb) {
    public record Elb(String targetGroupArn) {}
  }
}
