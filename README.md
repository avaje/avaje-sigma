# Avaje Sigma
[![Build](https://github.com/avaje/avaje-sigma/actions/workflows/build.yml/badge.svg)](https://github.com/avaje/avaje-sigma/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.avaje/avaje-sigma.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/io.avaje/avaje-sigma)
[![javadoc](https://javadoc.io/badge2/io.avaje/avaje-sigma/javadoc.svg?&color=purple)](https://javadoc.io/doc/io.avaje/avaje-sigma)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/avaje/avaje-sigma/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/1074074312421683250?color=%237289da&label=discord)](https://discord.gg/Qcqf9R27BR)

Provides javalin/jex style request handling for AWS Lambda HTTP requests.

Depending on your infrastructure, you can use `ALBHttpEvent`, `APIGatewayV2HttpEvent`, or `APIGatewayProxyEvent`.

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
## Use with [Avaje Http](https://avaje.io/http/)

[![Avaje-HTTP](https://img.shields.io/maven-central/v/io.avaje/avaje-http-api.svg?label=avaje.http.version)](https://mvnrepository.com/artifact/io.avaje/avaje-jex)

### Add dependencies
```xml
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-sigma</artifactId>
  <version>${sigma.version}</version>
</dependency>

<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-http-api</artifactId>
  <version>${avaje.http.version}</version>
</dependency>

<!-- Annotation processor -->
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-http-sigma-generator</artifactId>
  <version>${avaje.http.version}</version>
  <scope>provided</scope>
  <optional>true</optional>
</dependency>
```

#### JDK 23+

In JDK 23+, annotation processors are disabled by default, you will need to add a flag to re-enable.
```xml
<properties>
  <maven.compiler.proc>full</maven.compiler.proc>
</properties>
```

### Define a Controller
```java
package org.example.hello;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import java.util.List;

@Controller("/widgets")
public class WidgetController {
  private final HelloComponent hello;
  public WidgetController(HelloComponent hello) {
    this.hello = hello;
  }

  @Get("/{id}")
  Widget getById(int id) {
    return new Widget(id, "you got it"+ hello.hello());
  }

  @Get()
  List<Widget> getAll() {
    return List.of(new Widget(1, "Rob"), new Widget(2, "Fi"));
  }

  record Widget(int id, String name){};
}
```

This will generate routing code we can register using any JSR-330 compliant DI:

```java
@Generated("avaje-sigma-generator")
@Singleton
public class WidgetController$Route implements HttpService {

  private final WidgetController controller;

  public WidgetController$Route(WidgetController controller) {
    this.controller = controller;
  }

  @Override
  public void setup(Router router) {

    router.get("/widgets/{id}", ctx -> {
      ctx.status(200);
      var id = asInt(ctx.pathParam("id"));
      var result = controller.getById(id);
      ctx.json(result);
    });

    router.get("/widgets", ctx -> {
      ctx.status(200);
      var result = controller.getAll();
      ctx.json(result);
    });

  }

}
```

### DI Usage

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

