package io.avaje.sigma;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Router {

  /** Add the routes provided by the Router Service. */
  Router add(HttpService routes);

  /** Add all the routes provided by the Router Services. */
  Router addAll(Collection<HttpService> routes);

  /** Add a HEAD handler. */
  Router head(String path, Handler handler);

  /** Add a GET handler. */
  Router get(String path, Handler handler);

  /** Add a POST handler. */
  Router post(String path, Handler handler);

  /** Add a PUT handler. */
  Router put(String path, Handler handler);

  /** Add a PATCH handler. */
  Router patch(String path, Handler handler);

  /** Add a DELETE handler. */
  Router delete(String path, Handler handler);

  /** Add a TRACE handler. */
  Router trace(String path, Handler handler);

  /** Add a before filter for the given path. */
  Router before(String path, Handler handler);

  /** Add a before filter for all requests. */
  Router before(Handler handler);

  /** Add a after filter for the given path. */
  Router after(String path, Handler handler);

  /** Add an after filter for all requests. */
  Router after(Handler handler);

  /** Register an exception handler for the given exception type. */
  <T extends Exception> Router exception(Class<T> exceptionClass, ExceptionHandler<T> handler);

  /** Return all the registered handlers. */
  List<Entry> all();

  Map<Class<?>, ExceptionHandler<?>> exceptionHandlers();

  /** A routing entry. */
  interface Entry {

    /** Return the type of entry. */
    HttpMethod type();

    /** Return the full path of the entry. */
    String path();

    /** Return the handler. */
    Handler handler();
  }

  /** The type of route entry. */
  enum HttpMethod {
    /** Before filter. */
    BEFORE,
    /** After filter. */
    AFTER,
    /** Http GET. */
    GET,
    /** Http POST. */
    POST,
    /** HTTP PUT. */
    PUT,
    /** HTTP PATCH. */
    PATCH,
    /** HTTP DELETE. */
    DELETE,
    /** HTTP HEAD. */
    HEAD,
    /** HTTP TRACE. */
    TRACE;
  }
}
