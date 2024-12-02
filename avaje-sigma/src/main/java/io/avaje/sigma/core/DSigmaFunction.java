package io.avaje.sigma.core;

import com.amazonaws.services.lambda.runtime.Context;
import io.avaje.sigma.Router;
import io.avaje.sigma.Sigma;
import io.avaje.sigma.aws.events.AWSHttpResponse;
import io.avaje.sigma.aws.events.AWSRequest;
import io.avaje.sigma.routes.SpiRoutes;
import java.util.Map;

final class DSigmaFunction implements Sigma.HttpFunction {

  private final SpiRoutes routes;
  private final ServiceManager manager;

  public DSigmaFunction(SpiRoutes routes, ServiceManager manager) {
    this.routes = routes;
    this.manager = manager;
  }

  @Override
  public AWSHttpResponse apply(AWSRequest req, Context context) {

    SigmaContext ctx;

    final Router.HttpMethod routeType = req.httpMethod();
    final String uri = req.path();
    SpiRoutes.Entry route = routes.match(routeType, uri);
    if (route != null) {
      final Map<String, String> params = route.pathParams(uri);
      ctx = new SigmaContext(manager, req, context, route.matchPath(), params);
      try {
        new BaseFilterChain(routes.filters(), route.handler(), ctx).proceed();
      } catch (Exception e) {
        handleException(ctx, e);
      }
    } else {
      ctx = new SigmaContext(manager, req, context, uri);
      ctx.status(404)
          .json(
              "{\"error\":\"No route matching http method %s, with path %s\"}"
                  .formatted(req.httpMethod(), uri));
    }
    return ctx.createResponse();
  }

  private void handleException(SigmaContext ctx, Exception e) {
    ctx.resetResponse();
    routes.handleException(ctx, e);
  }
}
