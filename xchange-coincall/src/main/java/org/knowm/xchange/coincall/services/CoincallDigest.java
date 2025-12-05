package org.knowm.xchange.coincall.services;

import static org.knowm.xchange.utils.DigestUtils.bytesToHex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.HeaderParam;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.crypto.Mac;
import lombok.SneakyThrows;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.BaseParamsDigest;
import si.mazi.rescu.Params;
import si.mazi.rescu.RestInvocation;

/**
 * <p>
 * Coincall REST signature: prehash = METHOD + URI + "?" + canonicalParams + "&uuid=" + apiKey +
 * "&ts=" + ts + "&x-req-ts-diff=" + diff
 * </p>
 * <p>
 * sign = HMAC_SHA256(secret, prehash) -> HEX UPPERCASE
 * </p>
 * Docs: <a
 * href="https://docs.coincall.com/#overview-authentication-signature">https://docs.coincall.com/#overview-authentication-signature</a>
 */
public class CoincallDigest extends BaseParamsDigest {

  static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
  }

  private final String apiKey;

  public CoincallDigest(String apiKey, String secretKey) {
    super(secretKey, HMAC_SHA_256);
    this.apiKey = apiKey;
  }

  /**
   * Build canonical parameter string according to Coincall rules. - GET: sort query pairs
   * alphabetically. - POST with application/json body: convert JSON to sorted key=value pairs
   * (nulls removed), arrays/objects as compact JSON. - POST with query string (rare): same as GET.
   */
  static String canonicalParams(RestInvocation inv) {
    String method = inv.getHttpMethod().toUpperCase(Locale.ROOT);

    if ("GET".equals(method)) {
      return sortQueryString(inv.getQueryString());
    }
    if ("POST".equals(method)) {
      String body = inv.getRequestBody();
      if (body != null && !body.isEmpty()) {
        return jsonBodyToCanonicalPairs(body);
      }
      // Fall back to query string (some POST endpoints may use query)
      return sortQueryString(inv.getQueryString());
    }
    throw new NotYetImplementedForExchangeException(
        "Only GET and POST are supported for Coincall signing");
  }


  static String sortQueryString(String qs) {
    if (qs == null || qs.isEmpty()) {
      return "";
    }
    String[] parts = qs.split("&");
    List<String> pairs = new ArrayList<>();
    for (String p : parts) {
      if (p == null || p.isEmpty()) {
        continue;
      }
      pairs.add(p);
    }
    pairs.sort(Comparator.comparing(s -> s.split("=", 2)[0]));
    return String.join("&", pairs);
  }

  static String jsonBodyToCanonicalPairs(String body) {
    try {
      JsonNode root = MAPPER.readTree(body);
      // root should be an object; if array, we treat it as a single parameter 'data=[...]'
      if (!root.isObject()) {
        // Fallback: pass as "data=<compact json>"
        return "data=" + compactJson(root);
      }
      ObjectNode pruned = pruneNulls((ObjectNode) root);
      // sort field names
      List<String> names = new ArrayList<>();
      pruned.fieldNames().forEachRemaining(names::add);
      Collections.sort(names);

      List<String> pairs = new ArrayList<>(names.size());
      for (String name : names) {
        JsonNode val = pruned.get(name);
        String v;
        if (val.isValueNode()) {
          // primitives: use raw textual representation (no quotes for numbers/booleans; strings as raw text)
          if (val.isTextual()) {
            v = val.asText();
          } else if (val.isNumber() || val.isBoolean()) {
            v = val.asText();
          } else if (val.isNull()) {
            // removed by pruneNulls; defensive fallback
            continue;
          } else {
            v = val.asText();
          }
        } else {
          // arrays/objects -> compact JSON (no spaces), with sorted keys and nulls pruned recursively
          v = compactJson(val);
        }
        pairs.add(name + "=" + v);
      }
      return String.join("&", pairs);
    } catch (JsonProcessingException e) {
      // as a last resort, pass through the original body (unsafe, but better than breaking the call)
      return body;
    }
  }

  static ObjectNode pruneNulls(ObjectNode obj) {
    ObjectNode out = MAPPER.createObjectNode();
    List<String> names = new ArrayList<>();
    obj.fieldNames().forEachRemaining(names::add);
    for (String n : names) {
      JsonNode v = obj.get(n);
      if (v == null || v.isNull()) {
        continue; // drop nulls
      }
      if (v.isObject()) {
        out.set(n, pruneNulls((ObjectNode) v));
      } else if (v.isArray()) {
        out.set(n, pruneNulls((ArrayNode) v));
      } else {
        out.set(n, v);
      }
    }
    return out;
  }

  static ArrayNode pruneNulls(ArrayNode arr) {
    ArrayNode out = MAPPER.createArrayNode();
    for (JsonNode v : arr) {
      if (v == null || v.isNull()) {
        continue;
      }
      if (v.isObject()) {
        out.add(pruneNulls((ObjectNode) v));
      } else if (v.isArray()) {
        out.add(pruneNulls((ArrayNode) v));
      } else {
        out.add(v);
      }
    }
    return out;
  }

  static String compactJson(JsonNode node) {
    // Ensure objects have sorted keys and nulls removed
    if (node.isObject()) {
      node = pruneNulls((ObjectNode) node);
    } else if (node.isArray()) {
      node = pruneNulls((ArrayNode) node);
    }
    try {
      ObjectWriter w = MAPPER.writer(); // default is compact
      return w.writeValueAsString(node);
    } catch (JsonProcessingException e) {
      return node.toString();
    }
  }

  @SneakyThrows
  @Override
  public String digestParams(RestInvocation restInvocation) {

    long timestamp = System.currentTimeMillis();
    int timestampServerDiff = 5000;

    Params params = restInvocation.getParamsMap().get(HeaderParam.class);
    params.add("X-CC-APIKEY", apiKey);
    params.add("ts", timestamp);
    params.add("X-REQ-TS-DIFF", timestampServerDiff);

    String method = restInvocation.getHttpMethod().toUpperCase(Locale.ROOT);
    String path = restInvocation.getPath();

    String canonicalParams = canonicalParams(restInvocation);

    StringBuilder prehash = new StringBuilder(128)
        .append(method)
        .append("/")
        .append(path)
        .append('?');

    if (!canonicalParams.isEmpty()) {
      prehash.append(canonicalParams).append('&');
    }

    prehash
        .append("uuid=").append(apiKey)
        .append("&ts=").append(timestamp)
        .append("&x-req-ts-diff=")
        .append(timestampServerDiff);

    Mac mac = getMac();
    mac.update(prehash.toString().getBytes(StandardCharsets.UTF_8));

    // Coincall requires UPPERCASE hex
    return bytesToHex(mac.doFinal()).toUpperCase(Locale.ROOT);
  }
}
