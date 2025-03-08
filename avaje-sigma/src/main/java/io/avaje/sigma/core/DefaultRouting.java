package io.avaje.sigma.core;

import io.avaje.sigma.ExceptionHandler;
import io.avaje.sigma.HttpFilter;
import io.avaje.sigma.HttpService;
import io.avaje.sigma.RequestHandler;
import io.avaje.sigma.Router;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class DefaultRouting implements Router {

  private static final String SLASH = "/";
  private final List<HttpFilter> filters = new ArrayList<>();
  private final List<Router.Entry> handlers = new ArrayList<>();
  private final Deque<String> pathDeque = new ArrayDeque<>();
  private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers = new HashMap<>();

  @Override
  public List<Router.Entry> all() {
    return handlers;
  }

  private String path(String path) {
    return String.join("", pathDeque)
        + ((path.startsWith(SLASH) || path.isEmpty()) ? path : SLASH + path);
  }

  @Override
  public Router add(HttpService service) {
    service.setup(this);
    return this;
  }

  @Override
  public Router addAll(Collection<HttpService> routes) {
    for (var route : routes) {
      route.setup(this);
    }
    return this;
  }

  private void add(HttpMethod verb, String path, RequestHandler handler) {
    Entry lastEntry = new Entry(verb, path(path), handler);
    handlers.add(lastEntry);
  }

  // ********************************************************************************************
  // HTTP verbs
  // ********************************************************************************************

  @Override
  public Router get(String path, RequestHandler handler) {
    add(HttpMethod.GET, path, handler);
    return this;
  }

  @Override
  public Router post(String path, RequestHandler handler) {
    add(HttpMethod.POST, path, handler);
    return this;
  }

  @Override
  public Router put(String path, RequestHandler handler) {
    add(HttpMethod.PUT, path, handler);
    return this;
  }

  @Override
  public Router patch(String path, RequestHandler handler) {
    add(HttpMethod.PATCH, path, handler);
    return this;
  }

  @Override
  public Router delete(String path, RequestHandler handler) {
    add(HttpMethod.DELETE, path, handler);
    return this;
  }

  @Override
  public Router head(String path, RequestHandler handler) {
    add(HttpMethod.HEAD, path, handler);
    return this;
  }

  @Override
  public Router trace(String path, RequestHandler handler) {
    add(HttpMethod.TRACE, path, handler);
    return this;
  }

  // ********************************************************************************************
  // Filters
  // ********************************************************************************************

  @Override
  public Router filter(HttpFilter handler) {
    filters.add(handler);
    return this;
  }

  @Override
  public List<HttpFilter> filters() {
    return filters;
  }

  // ********************************************************************************************
  // Exception handlers (filters)
  // ********************************************************************************************
  @Override
  public <T extends Exception> Router exception(Class<T> type, ExceptionHandler<T> handler) {
    exceptionHandlers.put(type, handler);
    return this;
  }

  @Override
  public Map<Class<?>, ExceptionHandler<?>> exceptionHandlers() {

    return exceptionHandlers;
  }

  private record Entry(HttpMethod type, String path, RequestHandler handler)
      implements Router.Entry {}
}
