package io.avaje.sigma;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;

import io.avaje.sigma.Routing.HttpMethod;
import io.avaje.sigma.aws.events.AWSRequest;
import io.avaje.sigma.body.BodyMapper;

/** Provides access to functions for handling the request and response in a lambda invocation. */
public interface HttpContext {

  /**
   * Sets an attribute on the request.
   *
   * <p>Attributes are available to other handlers in the request lifecycle
   *
   * @param key the key for the attribute.
   * @return The current HTTP context.
   */
  HttpContext attribute(String key, Object value);

  /**
   * Get the specified attribute from the request.
   *
   * @param key the key for the attribute.
   * @return The Attribute, or null if not available.
   */
  <T> T attribute(String key);

  /**
   * Returns the AWS request associated with the current invocation.
   *
   * @return The AWS request object.
   */
  <T extends AWSRequest> T awsRequest();

  /**
   * Returns the AWS Lambda context of the current invocation.
   *
   * @return The AWS Lambda context object.
   */
  Context awsContext();

  /**
   * Returns the matched path of the current HTTP request.
   *
   * @return The matched path.
   */
  String matchedPath();

  /**
   * Returns the request body as a bean of the specified type.
   *
   * @param beanType The desired bean type.
   * @return The request body as a bean of the specified type.
   */
  <T> T bodyAsClass(Class<T> beanType);

  /**
   * Returns the request body as a string.
   *
   * @return The request body as a string.
   */
  String body();

  /**
   * Returns the content type of the request.
   *
   * @return The content type of the request.
   */
  String contentType();

  /**
   * Sets the content type of the response.
   *
   * @param contentType The desired content type.
   * @return The current HTTP context.
   */
  HttpContext contentType(String contentType);

  /**
   * Returns the value of the specified path parameter.
   *
   * @param name The name of the path parameter.
   * @return The value of the path parameter, or null if not found.
   */
  String pathParam(String name);

  /**
   * Returns the value of the first query parameter with the specified name.
   *
   * @param name The name of the query parameter.
   * @return The value of the first query parameter, or null if not found.
   */
  String queryParam(String name);

  /**
   * Returns a list of all query parameters with the specified name.
   *
   * @param name The name of the query parameter.
   * @return A list of all query parameters with the specified name.
   */
  List<String> queryParams(String name);

  /** Return the first form param value for the specified key or null. */
  default String formParam(String key) {
    var val = formParams(key);
    return val == null || val.isEmpty() ? null : val.get(0);
  }

  /** Return the form params for the specified key, or empty list. */
  List<String> formParams(String key);

  /**
   * Sets the status code of the response.
   *
   * @param statusCode The desired status code.
   * @return The current HTTP context.
   */
  HttpContext status(int statusCode);

  /**
   * Returns the current status code of the response.
   *
   * @return The current status code of the response.
   */
  int status();

  /**
   * Writes plain text content to the response. Sets content-type Header to text/plain
   *
   * @param content The plain text content to write.
   * @return The current HTTP context.
   */
  HttpContext text(String content);

  /**
   * Writes HTML content to the response. Sets content-type Header to text/html
   *
   * @param content The HTML content to write.
   * @return The current HTTP context.
   */
  HttpContext html(String content);

  /**
   * Sets the response body as JSON for the given bean. Sets content-type Header to application/json
   *
   * @param bean The bean to serialize as JSON.
   * @return The current HTTP context.
   */
  HttpContext json(Object bean);

  /**
   * Writes raw string content to the response body.
   *
   * <p>Will overwrite the current result if there is one.
   *
   * @param content The raw content to write.
   * @return The current HTTP context.
   */
  HttpContext result(String content);

  /**
   * Writes object to the response. Depending on the content type, a {@link BodyMapper} will be used
   * to serialize to a string.
   *
   * <p>Will overwrite the current result if there is one.
   *
   * @param content The raw content to write.
   * @return The current HTTP context.
   */
  HttpContext result(Object bean);

  /**
   * Returns the response body as a string.
   *
   * @return The response body as a string.
   */
  String result();

  /**
   * Returns the value of the specified request header.
   *
   * @param key The name of the request header.
   * @return The value of the request header, or null if not found.
   */
  String header(String key);

  /**
   * Returns a list of all headers values with the specified name.
   *
   * @param name The name of the headers.
   * @return A list of all header values with the specified name.
   */
  List<String> headers(String name);

  /**
   * Sets the value of the specified response header.
   *
   * @param key The name of the response header.
   * @param value The value of the response header.
   * @return The current HTTP context.
   */
  HttpContext responseHeader(String key, String value);

  /**
   * Returns the HTTP method of the request.
   *
   * @return The HTTP method of the request.
   */
  HttpMethod method();

  /**
   * Returns the path of the request.
   *
   * @return The path of the request.
   */
  String path();

  /**
   * Sets the response body to the given base64-encoded content.
   *
   * @param content The base64-encoded content.
   * @return The current HTTP context.
   */
  HttpContext base64EncodedResult(String content);
}
