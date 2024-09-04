package io.avaje.sigma;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.avaje.jsonb.Json;
import io.avaje.sigma.Routing.HttpMethod;
import io.avaje.sigma.aws.events.ALBHttpEvent;
import io.avaje.sigma.aws.events.APIGatewayV2HttpEvent;
import io.avaje.sigma.body.JacksonService;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Json.Import(APIGatewayV2HttpEvent.class)
public class APIGatewayMavenTest {

  private final Sigma sigma = Sigma.create().addBodyMapper(new JacksonService(new ObjectMapper()));
  private APIGatewayV2HttpEvent albExample;

  @BeforeEach
  void setUpBeforeClass() throws JsonProcessingException {

    String exampleEvent =
        """
{
  "version": "2.0",
  "routeKey": "$default",
  "rawPath": "/my/path",
  "rawQueryString": "parameter1=value1&parameter1=value2&parameter2=value",
  "headers": {
    "header1": "value1",
    "header2": "value1,value2"
  },
  "queryStringParameters": {
    "parameter1": "value1,value2",
    "parameter2": "value"
  },
  "requestContext": {
    "accountId": "123456789012",
    "apiId": "api-id",
    "authorizer": {
      "jwt": {
        "claims": {
          "claim1": "value1",
          "claim2": "value2"
        },
        "scopes": [
          "scope1",
          "scope2"
        ]
      }
    },
    "domainName": "id.execute-api.us-east-1.amazonaws.com",
    "domainPrefix": "id",
    "http": {
      "method": "PUT",
      "path": "/my/path",
      "protocol": "HTTP/1.1",
      "sourceIp": "192.0.2.1",
      "userAgent": "agent"
    },
    "requestId": "id",
    "routeKey": "$default",
    "stage": "$default",
    "time": "12/Mar/2020:19:03:58 +0000",
    "timeEpoch": 1583348638390
  },
  "body": "Hello from Lambda",
  "pathParameters": {
    "parameter1": "value1"
  },
  "isBase64Encoded": false,
  "stageVariables": {
    "stageVariable1": "value1",
    "stageVariable2": "value2"
  }
}
""";
    this.albExample = new ObjectMapper().readValue(exampleEvent, APIGatewayV2HttpEvent.class);
  }

  @Test
  void test() {
    sigma.routing(
        r ->
            r
                .put(
                    "/my/path",
                    ctx -> {
                      assertThat(ctx.queryParams("parameter1")).hasSize(2);
                      assertThat(ctx.queryParam("parameter2")).isEqualTo("value");
                      assertThat(ctx.queryParam("null")).isNull();
                      assertThat(ctx.header("null")).isNull();
                      assertThat(ctx.header("header1")).isEqualTo("value1");
                      assertThat(ctx.headers("header2")).hasSize(2);
                      assertThat(ctx.body()).isEqualTo("Hello from Lambda");
                      assertThat(ctx.method()).isNotNull();
                      assertThat(ctx.matchedPath()).isNotNull();
                      ctx.text("hello world");
                    }));

    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(200);
    assertThat(result.body()).isEqualTo("hello world");
    assertThat(result.multiValueHeaders()).isNull();
  }

  @Test
  void test404() {
    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(404);
    assertThat(result.body()).contains("No route matching: /my/path");
  }

  @Json
  public record Body(String str) {}

  @Test
  void testTRace() {
    sigma.routing(
        r ->
            r.trace("/", ctx -> fail(""))
                .post(
                    "/lambda/post/",
                    ctx -> {
                      assertThat(ctx.bodyAsClass(Body.class)).isInstanceOf(Body.class);
                      ctx.html("pretend html");
                    }));
    var result =
        sigma
            .createHttpFunction()
            .apply(
                new ALBHttpEvent(
                    null,
                    HttpMethod.POST,
                    "/lambda/post/",
                    null,
                    null,
                    null,
                    null,
                    "{\"str\":\"what the sigma?\"}",
                    false),
                null);
    assertThat(result.statusCode()).isEqualTo(200);
    assertThat(result.body()).contains("pretend html");
  }

  @Test
  void testMultiValue() {
    sigma.routing(
        r ->
            r.patch(
                "/lambda/patch/",
                ctx -> {
                  assertThat(ctx.queryParams("params")).hasSize(2);
                  assertThat(ctx.headers("headers")).hasSize(2);
                  ctx.base64EncodedBody("pretend 64");
                  ctx.responseHeader("response", "response");
                }));
    var result =
        sigma
            .createHttpFunction()
            .apply(
                new ALBHttpEvent(
                    null,
                    HttpMethod.PATCH,
                    "/lambda/patch/",
                    null,
                    Map.of("params", List.of("1", "2")),
                    null,
                    Map.of("headers", List.of("1", "2")),
                    null,
                    false),
                null);
    assertThat(result.statusCode()).isEqualTo(200);
    assertThat(result.multiValueHeaders()).isNotNull();
  }
}
