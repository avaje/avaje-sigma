module io.avaje.sigma {
  exports io.avaje.sigma;
  exports io.avaje.sigma.aws.events;
  exports io.avaje.sigma.json;

  requires static com.fasterxml.jackson.databind;
  requires static io.avaje.jsonb;

  // Why is there not even an automatic module???
  requires transitive aws.lambda.java.core;
}
