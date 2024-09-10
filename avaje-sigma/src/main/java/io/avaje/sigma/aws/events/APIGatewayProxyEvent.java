package io.avaje.sigma.aws.events;

import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.sigma.Router.HttpMethod;
import java.util.List;
import java.util.Map;

@RecordBuilder
public record APIGatewayProxyEvent(
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
    ProxyRequestContext requestContext,
    String body,
    boolean isBase64Encoded)
    implements AWSRequest {

  public record ProxyRequestContext(
      String accountId,
      String stage,
      String resourceId,
      String requestId,
      String operationName,
      ProxyRequestIdentity identity,
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

  public record ProxyRequestIdentity(
      String principalId,
      String cognitoIdentityId,
      String cognitoIdentityPoolId,
      String sourceIp,
      String account,
      String cognitoAuthenticationType,
      String userArn) {}

  public static APIGatewayProxyEventBuilder builder() {

    return APIGatewayProxyEventBuilder.builder();
  }
}
