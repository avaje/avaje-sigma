package io.avaje.sigma.core;

import java.util.List;
import java.util.ListIterator;

import io.avaje.sigma.HttpContext;
import io.avaje.sigma.HttpFilter;
import io.avaje.sigma.HttpFilter.FilterChain;
import io.avaje.sigma.RequestHandler;

final class BaseFilterChain implements FilterChain {

  private final ListIterator<HttpFilter> iter;
  private final RequestHandler handler;
  private final HttpContext ctx;

  BaseFilterChain(List<HttpFilter> filters, RequestHandler handler, HttpContext ctx) {
    this.iter = filters.listIterator();
    this.handler = handler;
    this.ctx = ctx;
  }

  @Override
  public void proceed() throws Exception {
    if (!iter.hasNext()) {
      handler.handle(ctx);
    } else {
      iter.next().filter(ctx, this);
    }
  }
}
