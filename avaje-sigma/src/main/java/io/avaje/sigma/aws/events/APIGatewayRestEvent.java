package io.avaje.sigma.aws.events;

import java.util.List;
import java.util.Map;

import io.avaje.sigma.Routing.HttpMethod;

public record APIGatewayRestEvent(
    String version,
    String resource,
    String path,
    HttpMethod httpMethod,
    Map<String, String> headers,
    Map<String, List<String>> multiValueHeaders,
    Map<String, String> queryStringParameters,
    Map<String, List<String>> multiValueQueryStringParameters,
    Map<String, String> pathParameters,
    Map<String, String> stageVariables,
    RequestContext requestContext,
    String body,
    Boolean isBase64Encoded)
    implements AWSRequest {

  public record RequestContext(
      String accountId,
      String stage,
      String resourceId,
      String requestId,
      String operationName,
      RequestIdentity identity,
      String resourcePath,
      String httpMethod,
      String apiId,
      String path,
      Map<String, Object> authorizer,
      String extendedRequestId,
      String requestTime,
      long requestTimeEpoch,
      String domainName,
      String domainPrefix,
      String protocol) {}

  public record RequestIdentity(
      String principalId,
      String cognitoIdentityId,
      String cognitoIdentityPoolId,
      String sourceIp,
      String account,
      String cognitoAuthenticationType,
      String userArn) {}
}
