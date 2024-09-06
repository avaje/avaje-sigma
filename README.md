# Avaje Sigma
[![Build](https://github.com/avaje/avaje-sigma/actions/workflows/build.yml/badge.svg)](https://github.com/avaje/avaje-sigma/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.avaje/avaje-sigma.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/io.avaje/avaje-sigma)
[![javadoc](https://javadoc.io/badge2/io.avaje/avaje-sigma/javadoc.svg?&color=purple)](https://javadoc.io/doc/io.avaje/avaje-sigma)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/avaje/avaje-sigma/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/1074074312421683250?color=%237289da&label=discord)](https://discord.gg/Qcqf9R27BR)
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

    return handler.apply(event, context);
  }
}
```

### Use with Dependency Injection

```java
public class LambdaRequestHandler
    implements RequestHandler<APIGatewayV2HttpEvent, AWSHttpResponse> {

  HttpFunction handler;

  public LambdaRequestHandler() {

    List<HttpService> services = // Retrieve HttpServices via DI;
    handler = Sigma.create().routing(services).createHttpFunction();
  }

  @Override
  public AWSHttpResponse handleRequest(APIGatewayV2HttpEvent event, Context context) {

    return handler.apply(event, context);
  }
}
```
