package io.avaje.sigma;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Routing {

  /** Add the routes provided by the Routing Service. */
  Routing add(HttpService routes);

  /** Add all the routes provided by the Routing Services. */
  Routing addAll(Collection<HttpService> routes);

  /** Add a HEAD handler. */
  Routing head(String path, Handler handler);

  /** Add a HEAD handler for "/". */
  Routing head(Handler handler);

  /** Add a GET handler. */
  Routing get(String path, Handler handler);

  /** Add a GET handler for "/". */
  Routing get(Handler handler);

  /** Add a POST handler. */
  Routing post(String path, Handler handler);

  /** Add a POST handler for "/". */
  Routing post(Handler handler);

  /** Add a PUT handler. */
  Routing put(String path, Handler handler);

  /** Add a PUT handler for "/". */
  Routing put(Handler handler);

  /** Add a PATCH handler. */
  Routing patch(String path, Handler handler);

  /** Add a PATCH handler for "/". */
  Routing patch(Handler handler);

  /** Add a DELETE handler. */
  Routing delete(String path, Handler handler);

  /** Add a DELETE handler for "/". */
  Routing delete(Handler handler);

  /** Add a TRACE handler. */
  Routing trace(String path, Handler handler);

  /** Add a TRACE handler for "/". */
  Routing trace(Handler handler);

  /** Add a before filter for the given path. */
  Routing before(String path, Handler handler);

  /** Add a before filter for all requests. */
  Routing before(Handler handler);

  /** Add a after filter for the given path. */
  Routing after(String path, Handler handler);

  /** Add an after filter for all requests. */
  Routing after(Handler handler);

  /** Register an exception handler for the given exception type. */
  <T extends Exception> Routing exception(Class<T> exceptionClass, ExceptionHandler<T> handler);

  /** Return all the registered handlers. */
  List<Entry> all();

  Map<Class<?>, ExceptionHandler<?>> exceptionHandlers();

  /** A routing entry. */
  interface Entry {

    /** Return the type of entry. */
    HttpMethod getType();

    /** Return the full path of the entry. */
    String getPath();

    /** Return the handler. */
    Handler getHandler();
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
