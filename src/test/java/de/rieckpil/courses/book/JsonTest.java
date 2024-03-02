package de.rieckpil.courses.book;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

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
    JSONAssert.assertEquals("""
      {"orders": [42, 42, 16]}
      """, result, false);
  }
}
