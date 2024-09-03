# Avaje Sigma (Final Name TBD)

Provides javalin/jex style request handling for AWS lambda http requests.


```java
public class LambdaRequestHandler
    implements RequestHandler<APIGatewayV2HttpEvent, AWSHttpResponse> {

  HttpFunction handler =
      Sigma.create()
          .routing(
              r ->
                  r.get("/lambda", ctx -> ctx.text("Hello World"))
                   .get("/route2/{param}", ctx -> ctx.text(ctx.pathParam("param"))))
          .createHttpFunction();

  @Override
  public AWSHttpResponse handleRequest(APIGatewayV2HttpEvent event, Context context) {

    return handler.handle(event, context);
  }
}
```
