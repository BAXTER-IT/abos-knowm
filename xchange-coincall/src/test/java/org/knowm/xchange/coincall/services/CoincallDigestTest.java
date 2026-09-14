package org.knowm.xchange.coincall.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.HeaderParam;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import si.mazi.rescu.Params;
import si.mazi.rescu.RestInvocation;

public class CoincallDigestTest {

  @Test
  public void testSortQueryString_sortsByKeyAndSkipsEmpty() {
    String qs = "b=2&&a=1&c=3&"; // includes empty pieces
    String sorted = CoincallDigest.sortQueryString(qs);
    assertEquals("a=1&b=2&c=3", sorted);
  }

  @Test
  public void testSortQueryString_nullOrEmptyReturnsEmpty() {
    assertEquals("", CoincallDigest.sortQueryString(null));
    assertEquals("", CoincallDigest.sortQueryString(""));
  }

  // ---------- jsonBodyToCanonicalPairs & JSON helpers ----------

  @Test
  public void testJsonBodyToCanonicalPairs_objectRoot_sortsAndPrunesNulls() {
    // unsorted fields, includes null, nested with nulls
    String body =
        "{\"b\":2,\"a\":\"x\",\"c\":null,\"nested\":{\"z\":1,\"y\":null},\"arr\":[1,null,2]}";

    String canonical = CoincallDigest.jsonBodyToCanonicalPairs(body);

    // fields: a, arr, b, nested (sorted); null "c" removed; nested null removed; array pruned
    assertEquals("a=x&arr=[1,2]&b=2&nested={\"z\":1}", canonical);
  }

  @Test
  public void testJsonBodyToCanonicalPairs_arrayRootWrappedAsData() {
    String body = "[1,null,{\"a\":1}]";

    String canonical = CoincallDigest.jsonBodyToCanonicalPairs(body);

    // Root is array -> "data=<compactJson(array-with-nulls-pruned)>"
    assertEquals("data=[1,{\"a\":1}]", canonical);
  }

  @Test
  public void testPruneNulls_objectAndArray() {
    ObjectNode obj = CoincallDigest.MAPPER.createObjectNode();
    obj.put("a", "x");
    obj.putNull("b");

    ObjectNode nested = CoincallDigest.MAPPER.createObjectNode();
    nested.putNull("k");
    nested.put("z", 1);
    obj.set("nested", nested);

    ArrayNode arr = CoincallDigest.MAPPER.createArrayNode();
    arr.add(1);
    arr.addNull();
    arr.add(2);
    obj.set("arr", arr);

    ObjectNode pruned = CoincallDigest.pruneNulls(obj);
    JsonNode prunedArr = pruned.get("arr");

    assertFalse(pruned.has("b"));
    assertTrue(pruned.get("nested").has("z"));
    assertEquals(2, prunedArr.size());
    assertEquals(1, prunedArr.get(0).asInt());
    assertEquals(2, prunedArr.get(1).asInt());
  }

  @Test
  public void testCompactJson_compactsAndPrunes() {
    ObjectNode obj = CoincallDigest.MAPPER.createObjectNode();
    obj.put("b", 2);
    obj.putNull("c");
    obj.put("a", "x");

    String compact = CoincallDigest.compactJson(obj);

    // keys sorted because of ObjectMapper config; null "c" removed
    assertEquals("{\"b\":2,\"a\":\"x\"}", compact);
  }

  // ---------- canonicalParams ----------

  @Test
  public void testCanonicalParams_getUsesSortedQuery() {
    RestInvocation inv = mock(RestInvocation.class);
    when(inv.getHttpMethod()).thenReturn("get");
    when(inv.getQueryString()).thenReturn("b=2&a=1&c=3");

    String canonical = CoincallDigest.canonicalParams(inv);

    assertEquals("a=1&b=2&c=3", canonical);
  }

  @Test
  public void testCanonicalParams_postWithJsonBody() {
    RestInvocation inv = mock(RestInvocation.class);
    when(inv.getHttpMethod()).thenReturn("POST");
    String body = "{\"b\":2,\"a\":\"x\",\"c\":null}";
    when(inv.getRequestBody()).thenReturn(body);
    when(inv.getQueryString()).thenReturn("ignored=1");

    String canonical = CoincallDigest.canonicalParams(inv);

    // fields sorted, null removed
    assertEquals("a=x&b=2", canonical);
  }

  @Test
  public void testCanonicalParams_postEmptyBodyFallsBackToQuery() {
    RestInvocation inv = mock(RestInvocation.class);
    when(inv.getHttpMethod()).thenReturn("POST");
    when(inv.getRequestBody()).thenReturn("");
    when(inv.getQueryString()).thenReturn("z=9&a=1");

    String canonical = CoincallDigest.canonicalParams(inv);

    assertEquals("a=1&z=9", canonical);
  }

  @Test(expected = NotYetImplementedForExchangeException.class)
  public void testCanonicalParams_unsupportedMethodThrows() {
    RestInvocation inv = mock(RestInvocation.class);
    when(inv.getHttpMethod()).thenReturn("DELETE");
    when(inv.getQueryString()).thenReturn("");

    CoincallDigest.canonicalParams(inv);
  }

  // ---------- digestParams ----------

  @Test
  public void testDigestParams_producesUppercaseHexSignatureAndSetsHeaders() {
    RestInvocation inv = mock(RestInvocation.class);

    // prepare header params map
    Map<Class<? extends Annotation>, Params> paramsMap = new HashMap<>();
    paramsMap.put(HeaderParam.class, Params.of());
    when(inv.getParamsMap()).thenReturn(paramsMap);

    when(inv.getHttpMethod()).thenReturn("GET");
    when(inv.getPath()).thenReturn("v1/test");
    when(inv.getQueryString()).thenReturn("b=2&a=1");
    when(inv.getRequestBody()).thenReturn(null);

    CoincallDigest digest = new CoincallDigest("test-api-key", "test-secret");

    String sig = digest.digestParams(inv);

    assertNotNull(sig);
    assertEquals(64, sig.length());
    assertTrue(sig.matches("[0-9A-F]{64}"));

    // Verify headers were added
    Params headerParams = paramsMap.get(HeaderParam.class);
    assertEquals("test-api-key", headerParams.getParamValue("X-CC-APIKEY"));
    assertNotNull(headerParams.getParamValue("ts"));
    assertEquals(5000, headerParams.getParamValue("X-REQ-TS-DIFF"));
  }
}