package io.avaje.sigma.core;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.avaje.sigma.json.JsonService;

/** Core implementation of SpiServiceManager provided to specific implementations like jetty etc. */
class ServiceManager {

  private final JsonService jsonService;

  public ServiceManager(JsonService jsonService) {
    this.jsonService = jsonService;
  }

  public <T> T jsonRead(Class<T> clazz, String ctx) {
    return jsonService.jsonRead(clazz, ctx);
  }

  public String jsonWrite(Object bean) {
    return jsonService.jsonWrite(bean);
  }

  public Map<String, List<String>> parseFormMap(String body) {
    if (body == null || body.isEmpty()) {
      return Map.of();
    }
    Map<String, List<String>> map = new HashMap<>();
    for (String pair : body.split("&")) {
      final String[] split1 = pair.split("=", 2);
      String key = URLDecoder.decode(split1[0], StandardCharsets.UTF_8);
      String val = split1.length > 1 ? URLDecoder.decode(split1[1], StandardCharsets.UTF_8) : "";
      map.computeIfAbsent(key, s -> new ArrayList<>()).add(val);
    }
    return map;
  }
}
