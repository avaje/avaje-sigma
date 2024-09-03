package io.avaje.sigma.aws.events;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.avaje.sigma.Routing.HttpMethod;
import io.avaje.sigma.routes.UrlDecode;

public record APIGatewayV2HttpEvent(
    String version,
    String routeKey,
    String rawPath,
    String rawQueryString,
    List<String> cookies,
    Map<String, String> headers,
    Map<String, String> queryStringParameters,
    Map<String, String> pathParameters,
    Map<String, String> stageVariables,
    String body,
    boolean isBase64Encoded,
    RequestContext requestContext)
    implements AWSRequest {

  public record RequestContext(
      String routeKey,
      String accountId,
      String stage,
      String apiId,
      String domainName,
      String domainPrefix,
      String time,
      long timeEpoch,
      Http http,
      Authorizer authorizer,
      String requestId) {

    public record Authorizer(JWT jwt, Map<String, Object> lambda, IAM iam) {}

    public record JWT(Map<String, String> claims, List<String> scopes) {}

    public record Http(
        HttpMethod method, String path, String protocol, String sourceIp, String userAgent) {}

    public record IAM(
        String accessKey,
        String accountId,
        String callerId,
        CognitoIdentity cognitoIdentity,
        String principalOrgId,
        String userArn,
        String userId) {}

    public record CognitoIdentity(List<String> amr, String identityId, String identityPoolId) {}
  }

  @Override
  public List<String> queryParams(String name) {

    return Optional.ofNullable(queryStringParameters.get(name))
        .map(UrlDecode::decode)
        .map(s -> List.of(s.split(",")))
        .orElse(List.of());
  }

  @Override
  public List<String> headers(String name) {

    return Optional.ofNullable(headers.get(name)).map(s -> List.of(s.split(","))).orElse(List.of());
  }

  @Override
  public HttpMethod httpMethod() {
    return requestContext.http.method;
  }

  @Override
  public String path() {
    return requestContext.http.path;
  }

  @Override
  public boolean hasMultiValueParams() {
    return false;
  }

  @Override
  public Map<String, List<String>> multiValueQueryStringParameters() {

    return Map.of();
  }

  @Override
  public Map<String, List<String>> multiValueHeaders() {

    return Map.of();
  }
}
