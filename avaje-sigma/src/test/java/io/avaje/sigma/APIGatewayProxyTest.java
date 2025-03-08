package io.avaje.sigma;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.jsonb.Json;
import io.avaje.sigma.aws.events.APIGatewayProxyEvent;
import io.avaje.sigma.body.JacksonBodyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Json.Import(APIGatewayProxyEvent.class)
public class APIGatewayProxyTest {

  private final Sigma sigma =
      Sigma.create().addBodyMapper(new JacksonBodyMapper(new ObjectMapper()));
  private APIGatewayProxyEvent albExample;

  @BeforeEach
  void setUpBeforeClass() throws JsonProcessingException {

    String exampleEvent =
        """
{
  "resource": "/my/path",
  "path": "/my/path",
  "httpMethod": "POST",
  "body": "emailId=abc%40example.com&comments=Sample+Feedback&comments=Sample+Feedback2",
  "headers": {
    "Content-Type": "application/x-www-form-urlencoded",
    "header1": "value1",
    "header2": "value1,value2"
  },
  "multiValueHeaders": {
    "header1": [
      "value1"
    ],
    "header2": [
      "value1",
      "value2"
    ]
  },
  "queryStringParameters": {
    "parameter1": "value1,value2",
    "parameter2": "value"
  },
  "multiValueQueryStringParameters": {
    "parameter1": [
      "value1",
      "value2"
    ],
    "parameter2": [
      "value"
    ]
  },
  "requestContext": {
    "accountId": "123456789012",
    "apiId": "id",
    "authorizer": {
      "claims": null,
      "scopes": null
    },
    "domainName": "id.execute-api.us-east-1.amazonaws.com",
    "domainPrefix": "id",
    "extendedRequestId": "request-id",
    "httpMethod": "GET",
    "identity": {
      "account": null,
      "cognitoAuthenticationType": null,
      "cognitoIdentityId": null,
      "cognitoIdentityPoolId": null,
      "principalId": null,
      "sourceIp": "IP",
      "userArn": null
      }
    },
    "path": "/my/path"
  },
  "pathParameters": null,
  "stageVariables": null,
  "isBase64Encoded": false
}
""";
    this.albExample = new ObjectMapper().readValue(exampleEvent, APIGatewayProxyEvent.class);
  }

  @Test
  void test() {
    sigma.routing(
        r ->
            r.post(
                "/my/path",
                ctx -> {
                  assertThat(ctx.queryParams("parameter1")).hasSize(2);
                  assertThat(ctx.queryParam("parameter2")).isEqualTo("value");
                  assertThat(ctx.queryParam("null")).isNull();
                  assertThat(ctx.header("null")).isNull();
                  assertThat(ctx.header("header1")).isEqualTo("value1");
                  assertThat(ctx.headers("header2")).hasSize(2);
                  assertThat(ctx.formParam("emailId")).isEqualTo("abc@example.com");
                  assertThat(ctx.formParams("comments")).hasSize(2);

                  ctx.text("hello world");
                }));

    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(200);
    assertThat(result.body()).isEqualTo("hello world");
  }
}
