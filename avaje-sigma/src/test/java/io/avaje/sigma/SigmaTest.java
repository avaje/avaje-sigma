package io.avaje.sigma;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.avaje.sigma.aws.events.ALBHttpEvent;

class SigmaTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Sigma sigma = Sigma.create();
  private ALBHttpEvent albExample;

  @BeforeEach
  void setUpBeforeClass() throws JsonProcessingException {

    String exampleEvent =
        """
                {
          "requestContext": {
              "elb": {
                  "targetGroupArn": "arn:aws:elasticloadbalancing:us-east-1:123456789012:targetgroup/lambda-279XGJDqGZ5rsrHC2Fjr/49e9d65c45c6791a"
              }
          },
          "httpMethod": "GET",
          "path": "/lambda/1234",
          "queryStringParameters": {
              "query": "1234ABCD"
          },
          "headers": {
              "accept": "text/html",
              "accept-encoding": "gzip",
              "accept-language": "en-US",
              "connection": "keep-alive",
              "host": "lambda-alb-123578498.us-east-1.elb.amazonaws.com",
              "upgrade-insecure-requests": "1",
              "x-amzn-trace-id": "Root=1-5c536348-3d683b8b04734faae651f476",
              "x-forwarded-for": "72.12.164.125",
              "x-forwarded-port": "80",
              "x-forwarded-proto": "http",
              "x-imforwards": "20"
          },
          "body": "",
          "isBase64Encoded": false
      }
                """;
    this.albExample = OBJECT_MAPPER.readValue(exampleEvent, ALBHttpEvent.class);
  }

  record Error(String msg) {}

  @Test
  void testError() {
    sigma.routing(
        r ->
            r.get(
                    "/lambda/{pathParam}",
                    ctx -> {
                      throw new IllegalStateException();
                    })
                .exception(
                    IllegalStateException.class,
                    (e, ctx) -> {
                      assertThat(e instanceof IllegalStateException).isTrue();

                      ctx.json(Map.of("msg", "failed")).status(500);
                    }));

    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(500);

    assertThat(result.body()).isEqualTo("{\"msg\":\"failed\"}");
  }

  @Test
  void test() {
    sigma.routing(
        r ->
            r.get(
                    "/lambda/{pathParam}",
                    ctx -> {
                      assertThat(ctx.awsRequest() instanceof ALBHttpEvent).isTrue();
                      assertThat("1234".equals(ctx.pathParam("pathParam"))).isTrue();
                      assertThat("1234ABCD".equals(ctx.queryParam("query"))).isTrue();

                      ctx.text("hello world");
                    })
                .before(ctx -> ctx.attribute("before", "attribute")));

    sigma.createHttpFunction().apply(albExample, null);
  }
}
