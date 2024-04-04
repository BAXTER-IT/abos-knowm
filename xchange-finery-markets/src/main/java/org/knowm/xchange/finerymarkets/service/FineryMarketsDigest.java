package org.knowm.xchange.finerymarkets.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.service.BaseParamsDigest;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

@Slf4j
public class FineryMarketsDigest extends BaseParamsDigest {

  public static final String EFX_KEY = "EFX-Key";
  public static final String EFX_SIGN = "EFX-Sign";

  private FineryMarketsDigest(String secretKeyBase64) {
    super(secretKeyBase64, HMAC_SHA_384);
  }

  public static ParamsDigest createInstance(String secretKeyBase64) {
    return secretKeyBase64 == null ? null : new FineryMarketsDigest(secretKeyBase64);
  }

  /** <a href="https://faq.finerymarkets.com/api-reference/rest-api">REST API</a> */
  @SneakyThrows
  @Override
  public String digestParams(RestInvocation restInvocation) {
    String method = restInvocation.getMethodPath();
    String payload = restInvocation.getRequestBody();
    String toSign = method + payload;
    Mac mac = getMac();
    mac.update(toSign.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(mac.doFinal());
  }
}
