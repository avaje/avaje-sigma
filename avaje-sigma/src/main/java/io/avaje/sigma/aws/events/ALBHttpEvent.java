package io.avaje.sigma.aws.events;

import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.sigma.Router;
import java.util.List;
import java.util.Map;

@RecordBuilder
public record ALBHttpEvent(
    ALBRequestContext requestContext,
    Router.HttpMethod httpMethod,
    String path,
    Map<String, String> queryStringParameters,
    Map<String, List<String>> multiValueQueryStringParameters,
    Map<String, String> headers,
    Map<String, List<String>> multiValueHeaders,
    String body,
    boolean isBase64Encoded)
    implements AWSRequest {

  public record ALBRequestContext(Elb elb) {
    public record Elb(String targetGroupArn) {}
  }  public static ALBHttpEventBuilder builder() {

    return ALBHttpEventBuilder.builder();
  }
}
