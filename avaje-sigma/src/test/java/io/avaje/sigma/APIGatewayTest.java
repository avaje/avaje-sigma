package io.avaje.sigma;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.avaje.jsonb.Json;
import io.avaje.sigma.aws.events.APIGatewayV2HttpEvent;
import io.avaje.sigma.body.JacksonBodyMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Json.Import(APIGatewayV2HttpEvent.class)
public class APIGatewayTest {

  private final Sigma sigma =
      Sigma.create().addBodyMapper(new JacksonBodyMapper(new ObjectMapper()));
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
            r.put(
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
  void testInvalidMediaType() {
    sigma.routing(
        r ->
            r.put(
                "/my/path",
                ctx -> {
                  ctx.contentType("fake").writeBody(r);
                }));
    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(500);
  }
}
