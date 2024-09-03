package io.avaje.sigma.aws.events;

import java.util.List;
import java.util.Map;

public record AWSHttpResponse(
    int statusCode,
    Map<String, String> headers,
    Map<String, List<String>> multiValueHeaders,
    String body,
    boolean isBase64Encoded) {}
