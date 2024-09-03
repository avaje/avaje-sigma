package io.avaje.sigma.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.avaje.sigma.ExceptionHandler;
import io.avaje.sigma.Handler;
import io.avaje.sigma.HttpService;
import io.avaje.sigma.Routing;

/** */
class DefaultRouting implements Routing {

  private static final String SLASH = "/";
  private final List<Routing.Entry> handlers = new ArrayList<>();
  private final Deque<String> pathDeque = new ArrayDeque<>();
  private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers = new HashMap<>();

  @Override
  public List<Routing.Entry> all() {
    return handlers;
  }

  private String path(String path) {
    return String.join("", pathDeque)
        + ((path.startsWith(SLASH) || path.isEmpty()) ? path : SLASH + path);
  }

  @Override
  public Routing add(HttpService service) {
    service.setup(this);
    return this;
  }

  @Override
  public Routing addAll(Collection<HttpService> routes) {
    for (var route : routes) {
      route.setup(this);
    }
    return this;
  }

  private void add(HttpMethod verb, String path, Handler handler) {
    Entry lastEntry;
    lastEntry = new Entry(verb, path(path), handler);
    handlers.add(lastEntry);
  }

  private void addBefore(String path, Handler handler) {
    add(HttpMethod.BEFORE, path(path), handler);
  }

  private void addAfter(String path, Handler handler) {
    add(HttpMethod.AFTER, path(path), handler);
  }

  // ********************************************************************************************
  // HTTP verbs
  // ********************************************************************************************

  @Override
  public Routing get(String path, Handler handler) {
    add(HttpMethod.GET, path, handler);
    return this;
  }

  @Override
  public Routing get(Handler handler) {
    get("", handler);
    return this;
  }

  @Override
  public Routing post(String path, Handler handler) {
    add(HttpMethod.POST, path, handler);
    return this;
  }

  @Override
  public Routing post(Handler handler) {
    post("", handler);
    return this;
  }

  @Override
  public Routing put(String path, Handler handler) {
    add(HttpMethod.PUT, path, handler);
    return this;
  }

  @Override
  public Routing put(Handler handler) {
    put("", handler);
    return this;
  }

  @Override
  public Routing patch(String path, Handler handler) {
    add(HttpMethod.PATCH, path, handler);
    return this;
  }

  @Override
  public Routing patch(Handler handler) {
    patch("", handler);
    return this;
  }

  @Override
  public Routing delete(String path, Handler handler) {
    add(HttpMethod.DELETE, path, handler);
    return this;
  }

  @Override
  public Routing delete(Handler handler) {
    delete("", handler);
    return this;
  }

  @Override
  public Routing head(String path, Handler handler) {
    add(HttpMethod.HEAD, path, handler);
    return this;
  }

  @Override
  public Routing head(Handler handler) {
    head("", handler);
    return this;
  }

  @Override
  public Routing trace(String path, Handler handler) {
    add(HttpMethod.TRACE, path, handler);
    return this;
  }

  @Override
  public Routing trace(Handler handler) {
    trace("", handler);
    return this;
  }

  // ********************************************************************************************
  // Before/after handlers (filters)
  // ********************************************************************************************

  @Override
  public Routing before(String path, Handler handler) {
    addBefore(path, handler);
    return this;
  }

  @Override
  public Routing before(Handler handler) {
    before("/*", handler);
    return this;
  }

  @Override
  public Routing after(String path, Handler handler) {
    addAfter(path, handler);
    return this;
  }

  @Override
  public Routing after(Handler handler) {
    after("/*", handler);
    return this;
  }

  // ********************************************************************************************
  // Exception handlers (filters)
  // ********************************************************************************************
  @Override
  public <T extends Exception> Routing exception(Class<T> type, ExceptionHandler<T> handler) {
    exceptionHandlers.put(type, handler);
    return this;
  }

  @Override
  public Map<Class<?>, ExceptionHandler<?>> exceptionHandlers() {

    return exceptionHandlers;
  }

  private static class Entry implements Routing.Entry {

    private final HttpMethod type;
    private final String path;
    private final Handler handler;

    Entry(HttpMethod type, String path, Handler handler) {
      this.type = type;
      this.path = path;
      this.handler = handler;
    }

    @Override
    public HttpMethod getType() {
      return type;
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public Handler getHandler() {
      return handler;
    }
  }
}
