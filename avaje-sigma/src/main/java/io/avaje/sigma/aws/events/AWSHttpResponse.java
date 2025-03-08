package io.avaje.sigma.aws.events;

import io.avaje.recordbuilder.RecordBuilder;
import java.util.List;
import java.util.Map;

@RecordBuilder
public record AWSHttpResponse(
    int statusCode,
    Map<String, String> headers,
    Map<String, List<String>> multiValueHeaders,
    String body,
    boolean isBase64Encoded) {

  public static AWSHttpResponseBuilder builder() {
    return AWSHttpResponseBuilder.builder();
  }
}
