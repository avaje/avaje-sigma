package io.avaje.sigma.aws.events;

import io.avaje.sigma.Router;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an AWS request, providing a unified interface for accessing request data.
 *
 * <p>This sealed interface defines the common properties and methods for different types of AWS
 * requests, such as {@link APIGatewayProxyEvent}, {@link APIGatewayV2HttpEvent}, and {@link
 * ALBHttpEvent}.
 */
public sealed interface AWSRequest
    permits APIGatewayProxyEvent, APIGatewayV2HttpEvent, ALBHttpEvent {

  /**
   * Returns the request body as a string.
   *
   * @return The request body as a string.
   */
  String body();

  /**
   * Returns the HTTP method of the request.
   *
   * @return The HTTP method of the request.
   */
  Router.HttpMethod httpMethod();

  /**
   * Returns the path of the request.
   *
   * @return The path of the request.
   */
  String path();

  /**
   * Returns a map of query string parameters and their single values.
   *
   * @return A map of query string parameters and their single values.
   */
  Map<String, String> queryStringParameters();

  /**
   * Returns a map of query string parameters and their multiple values.
   *
   * @return A map of query string parameters and their multiple values.
   */
  Map<String, List<String>> multiValueQueryStringParameters();

  /**
   * Returns a list of values for the specified query string parameter.
   *
   * @param name The name of the query string parameter.
   * @return A list of values for the specified query string parameter.
   */
  default List<String> queryParams(String name) {
    return Optional.ofNullable(multiValueQueryStringParameters())
        .map(m -> m.get(name))
        .or(() -> Optional.ofNullable(queryStringParameters()).map(m -> m.get(name)).map(List::of))
        .orElse(List.of());
  }

  /**
   * Returns the first value of the specified query string parameter.
   *
   * @param name The name of the query string parameter.
   * @return The first value of the specified query string parameter, or null if not found.
   */
  default String queryParam(String name) {
    final List<String> vals = queryParams(name);
    if (vals.isEmpty()) {
      return null;
    } else {
      return vals.get(0);
    }
  }

  /**
   * Returns a map of request headers and their single values.
   *
   * @return A map of request headers and their single values.
   */
  Map<String, String> headers();

  /**
   * Returns a map of request headers and their multiple values.
   *
   * @return A map of request headers and their multiple values.
   */
  Map<String, List<String>> multiValueHeaders();

  /**
   * Returns a list of values for the specified request header.
   *
   * @param name The name of the request header.
   * @return A list of values for the specified request header.
   */
  default List<String> headers(String name) {
    return Optional.ofNullable(multiValueHeaders())
        .map(m -> m.get(name))
        .or(() -> Optional.ofNullable(headers()).map(m -> m.get(name)).map(List::of))
        .orElse(List.of());
  }

  /**
   * Returns the value of the "Content-Type" header.
   *
   * @return The value of the "Content-Type" header, or null if not found.
   */
  default String contentType() {
    return header("Content-Type");
  }

  /**
   * Returns the first value of the specified request header.
   *
   * @param name The name of the request header.
   * @return The first value of the specified request header, or null if not found.
   */
  default String header(String name) {
    final List<String> vals = headers(name);
    if (vals.isEmpty()) {
      return null;
    } else {
      return vals.get(0);
    }
  }

  /**
   * Indicates whether the request contains multi-value headers or query string parameters.
   *
   * @return True if the request contains multi-value headers or query string parameters, false
   *     otherwise.
   */
  default boolean hasMultiValueParams() {
    return !Objects.requireNonNullElse(multiValueQueryStringParameters(), Map.of()).isEmpty()
        || !Objects.requireNonNullElse(multiValueHeaders(), Map.of()).isEmpty();
  }
}
