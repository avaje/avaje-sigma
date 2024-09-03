package io.avaje.sigma.routes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class UrlDecode {

  public static String decode(String s) {
    return URLDecoder.decode(s.replace("+", "%2B"), StandardCharsets.UTF_8).replace("%2B", "+");
  }
}
