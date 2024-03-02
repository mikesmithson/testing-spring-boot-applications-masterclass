package de.rieckpil.courses.book;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonTest {

  @Test
  void testWithJSONAssert() throws JSONException {
    String result = """
      {"name": "duke", "age":"42", "hobbies": ["soccer", "java"]}
      """;

    JSONAssert.assertEquals("""
      { "hobbies": ["soccer", "java"]}
       """, result, false);
  }

  @Test
  void testWithJsonPath() throws JSONException {
    String result = """
        {"age":"42", "name": "duke", "tags":["java", "jdk"], "orders": [42, 42, 16]};
      """;
    assertThat(JsonPath.parse(result).read("$.age", Integer.class)).isEqualTo(42);
    assertThat(JsonPath.parse(result).read("$.name", String.class)).isEqualTo("duke");
    assertThat(JsonPath.parse(result).read("$.tags", List.class)).isEqualTo(List.of("java", "jdk"));
    assertThat(JsonPath.parse(result).read("$.orders", List.class)).isEqualTo(List.of(42, 42, 16));
  }
}
