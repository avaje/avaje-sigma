package io.avaje.sigma.core;

import static java.util.stream.Collectors.toUnmodifiableMap;

import io.avaje.sigma.body.BodyMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Core implementation of SpiServiceManager provided to specific implementations like jetty etc. */
final class ServiceManager {

  private final Map<String, BodyMapper> mappers;

  public ServiceManager(List<BodyMapper> bodyMappers) {
    this.mappers =
        bodyMappers.stream().collect(toUnmodifiableMap(BodyMapper::mediaType, Function.identity()));
  }

  public <T> T readBody(String contentType, Class<T> clazz, String ctx) {
    return getMapper(contentType).readBody(clazz, ctx);
  }

  public String writeBody(String contentType, Object bean) {
    return getMapper(contentType).writeBody(bean);
  }

  BodyMapper getMapper(String contentType) {
    var mapper = mappers.get(contentType);

    if (mapper == null) {
      throw new IllegalStateException("No mapper provided for media type: " + contentType);
    }
    return mapper;
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
