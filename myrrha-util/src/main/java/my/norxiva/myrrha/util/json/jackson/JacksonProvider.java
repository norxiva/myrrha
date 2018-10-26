package my.norxiva.myrrha.util.json.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.util.json.JsonProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@code JacksonProvider} is the Jackson implementation for {@code JsonProvider}.
 */
@Slf4j
public class JacksonProvider implements JsonProvider {
  public static final ObjectMapper DEFAULT_OBJECT_MAPPER = initObjectMapper();

  private static ObjectMapper initObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new GuavaModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // It is ugly to enable ALLOW_UNQUOTED_FIELD_NAMES
    // because some *.ftl under LLPay does not follow the standard JSON formatter
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    // It is ugly again, because the Json formatter in DB setting is single quote formater
    // which is not JSON standard either.
    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }

  @Override
  public String toJsonString(Object value) {
    try {
      return DEFAULT_OBJECT_MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException err) {
      log.error("Failed to convert the object {} to json string!", value);
      throw new IllegalArgumentException("Failed to convert the object" + value.toString()
          + "to json string", err);
    }
  }

  @Override
  public Map parse(String text) {
    try {
      return DEFAULT_OBJECT_MAPPER.readValue(text, Map.class);
    } catch (IOException err) {
      log.error("Failed to parse text " + text + " to map with error ", err);
      throw new IllegalArgumentException("Error parsing \'" + text
          + "\' to map with error", err);
    }
  }

  @Override
  public <K, V> Map<K, V> parse(String text, Class<K> keyType, Class<V> valueType) {
    return parse(text, Map.class, keyType, valueType);
  }

  @Override
  public <T extends Map, K, V> Map<K, V> parse(String text, Class<T> mapType,
                                               Class<K> keyType, Class<V> valueType) {
    try {
      MapType javaType = DEFAULT_OBJECT_MAPPER.getTypeFactory().constructMapType(mapType,
          keyType, valueType);
      return DEFAULT_OBJECT_MAPPER.readValue(text, javaType);
    } catch (IOException err) {
      log.error("Failed to parse text " + text + " to map with error ", err);
      throw new IllegalArgumentException("Error parsing \'" + text
          + "\' to map with error", err);
    }
  }

  @Override
  public <T> T parse(String text, Class<T> targetType) {
    try {
      return DEFAULT_OBJECT_MAPPER.readValue(text, targetType);
    } catch (IOException err) {
      log.error("Failed to parse text " + text + " with error ", err);
      throw new IllegalArgumentException("Error parsing \'" + text
          + "\' with error", err);
    }
  }

  @Override
  public <T> List<T> parseList(String text, Class<T> elementType) {
    try {
      JavaType listType = DEFAULT_OBJECT_MAPPER.getTypeFactory()
          .constructParametricType(ArrayList.class, List.class, elementType);
      return DEFAULT_OBJECT_MAPPER.readValue(text, listType);
    } catch (IOException err) {
      log.error("Failed to parse text " + text + " to list with error ", err);
      throw new IllegalArgumentException("Error parsing \'" + text
          + "\' to list with error", err);
    }
  }

  @Override
  public String convertObj(Object obj) {
    try {
      return DEFAULT_OBJECT_MAPPER.writeValueAsString(obj);
    } catch (IOException err) {
      log.error("Failed to convert obj " + obj + " to Json text with error ", err);
      throw new IllegalArgumentException("Error converting object \'" + obj
          + "\' to Json text with err ", err);
    }
  }

  @Override
  public <T> T convertObj(Object obj, Class<T> targetType) {
    return DEFAULT_OBJECT_MAPPER.convertValue(obj, targetType);
  }
}
