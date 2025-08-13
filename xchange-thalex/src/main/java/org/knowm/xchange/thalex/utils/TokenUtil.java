package org.knowm.xchange.thalex.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import org.knowm.xchange.thalex.config.JwtGenerator;

public final class TokenUtil implements JwtGenerator {

  public String generate(String keyName, String privateKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    RSAPrivateKey rsaPrivateKey = getPrivateKeyFromPEM(privateKey);
    String jwt;
    Instant now = Instant.now();
    String nowString = now.getEpochSecond() + "." + now.getNano();
    jwt =
        JWT.create()
            .withKeyId(keyName)
            // Add "iat" to the payload with nanoseconds to avoid collisions in the same second
            .withClaim("iat", Double.parseDouble(nowString))
            .sign(Algorithm.RSA512(rsaPrivateKey));
    return jwt;
  }

  private RSAPrivateKey getPrivateKeyFromPEM(String pem)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (pem == null || pem.isEmpty()) {
      throw new IllegalArgumentException("Private key is null or empty");
    }
    if (pem.startsWith("-----BEGIN RSA PRIVATE KEY-----")) {
      throw new IllegalArgumentException(
          "Invalid PEM format. Convert PKCS#1 to PKCS#8 using OpenSSL.\n"
              + "openssl pkcs8 -topk8 -inform PEM -outform PEM -in private_key.pem -out private_key_pkcs8.pem -nocrypt");
    }
    pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(pem);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPrivateKey) kf.generatePrivate(spec);
  }
}
