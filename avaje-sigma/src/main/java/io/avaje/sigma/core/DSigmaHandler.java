package io.avaje.sigma.core;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;

import io.avaje.sigma.AWSHttpHandler;
import io.avaje.sigma.Routing;
import io.avaje.sigma.aws.events.AWSHttpResponse;
import io.avaje.sigma.aws.events.AWSRequest;
import io.avaje.sigma.routes.SpiRoutes;

class DSigmaHandler implements AWSHttpHandler {

  private final SpiRoutes routes;
  private final ServiceManager manager;

  public DSigmaHandler(SpiRoutes routes, ServiceManager manager) {
    this.routes = routes;
    this.manager = manager;
  }

  @Override
  public AWSHttpResponse handle(AWSRequest req, Context context) {

    SigmaContext ctx;

    final Routing.HttpMethod routeType = req.httpMethod();
    final String uri = req.path();
    SpiRoutes.Entry route = routes.match(routeType, uri);
    if (route == null) {
      ctx = new SigmaContext(manager, req, context, uri);

      try {
        processNoRoute(ctx, uri, routeType);
      } catch (Exception e) {
        handleException(ctx, e);
      }
    } else {
      final Map<String, String> params = route.pathParams(uri);
      ctx = new SigmaContext(manager, req, context, route.matchPath(), params);

      try {
        processRoute(ctx, uri, route);
        routes.after(uri, ctx);
      } catch (Exception e) {
        handleException(ctx, e);
      }
    }
    return ctx.createResponse();
  }

  private void handleException(SigmaContext ctx, Exception e) {
    ctx.resetResponse();
    routes.handleException(ctx, e);
  }

  private void processRoute(SigmaContext ctx, String uri, SpiRoutes.Entry route) {
    routes.before(uri, ctx);
    route.handle(ctx);
  }

  private void processNoRoute(SigmaContext ctx, String uri, Routing.HttpMethod routeType) {

    if (routeType == Routing.HttpMethod.HEAD && hasGetHandler(uri)) {
      routes.before(uri, ctx);
      processHead(ctx);
      routes.after(uri, ctx);
      return;
    }
    ctx.status(404).json("{\"error\":\"No route matching: %s\"}".formatted(uri));
  }

  private void processHead(SigmaContext ctx) {
    ctx.status(200);
  }

  private boolean hasGetHandler(String uri) {
    return routes.match(Routing.HttpMethod.GET, uri) != null;
  }
}
