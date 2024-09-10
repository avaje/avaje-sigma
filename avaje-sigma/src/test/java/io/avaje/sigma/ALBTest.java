package io.avaje.sigma;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.avaje.jsonb.Json;
import io.avaje.jsonb.Jsonb;
import io.avaje.sigma.ALBTest.Body;
import io.avaje.sigma.Router.HttpMethod;
import io.avaje.sigma.aws.events.ALBHttpEvent;
import io.avaje.sigma.body.JacksonBodyMapper;

@Json.Import(ALBHttpEvent.class)
public class ALBTest {

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
        "header": "headon"
    },
    "body": "",
    "isBase64Encoded": false
}
""";
    this.albExample = Jsonb.builder().build().type(ALBHttpEvent.class).fromJson(exampleEvent);
  }

  @Test
  void testError() {
    sigma.routing(
        r ->
            r.get(
                    "/lambda/<pathParam>",
                    ctx -> {
                      assertThat(ctx.queryParam("null")).isNull();
                      assertThat(ctx.header("null")).isNull();
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
            r.before(ctx -> ctx.attribute("before", "attribute"))
                .get(
                    "/lambda/{pathParam}",
                    ctx -> {
                      assertThat(ctx.awsRequest() instanceof ALBHttpEvent).isTrue();
                      assertThat(ctx.pathParam("pathParam")).isEqualTo("1234");
                      assertThat(ctx.queryParam("query")).isEqualTo("1234ABCD");
                      assertThat(ctx.header("header")).isEqualTo("headon");
                      assertThat("attribute").isEqualTo(ctx.attribute("before"));
                      assertThat(ctx.body()).isBlank();
                      ctx.attribute("req", "req");
                      ctx.text("hello world");
                    })
                .after(
                    ctx -> {
                      assertThat("attribute").isEqualTo(ctx.attribute("before"));
                      assertThat("req").isEqualTo(ctx.attribute("req"));
                      assertThat("hello world").isEqualTo(ctx.result());
                    }));

    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(200);
    assertThat(result.body()).isEqualTo("hello world");
  }

  @Test
  void test404() {
    sigma.routing(
        r ->
            r.before("/", ctx -> fail(""))
                .head("/lambda/404/{pathParam}", null)
                .after("/", ctx -> fail("")));
    var result = sigma.createHttpFunction().apply(albExample, null);
    assertThat(result.statusCode()).isEqualTo(404);
    assertThat(result.body()).contains("No route matching");
  }

  @Json
  public record Body(String str) {}

  @Test
  void testPost() {
    sigma.routing(
        r ->
            r.before("/", ctx -> fail(""))
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
                  ctx.base64EncodedResult("pretend 64");
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

  @Test
  void testDeserializationError() {
    final var httpFunction =
        Sigma.create()
            .addBodyMapper(new JacksonBodyMapper())
            .routing(
                r ->
                    r.trace(
                        "/lambda/",
                        ctx -> {
                          ctx.bodyAsClass(Body.class);
                        }))
            .createHttpFunction();
    var result =
        httpFunction.apply(
            new ALBHttpEvent(
                null,
                HttpMethod.TRACE,
                "/lambda/",
                null,
                null,
                null,
                null,
                "{\"fail\":\"what the sigma?\"}",
                false),
            null);
    assertThat(result.statusCode()).isEqualTo(500);
  }
}
