package org.knowm.xchange.thalex.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import org.junit.Before;
import org.junit.Test;

public class TokenUtilTest {

  private RSAPublicKey publicKey;
  private String privateKeyPem;

  @Before
  public void setUp() throws Exception {
    // Generate test RSA keypair (PKCS#8)
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    KeyPair keyPair = generator.generateKeyPair();
    publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

    byte[] encoded = privateKey.getEncoded(); // PKCS#8 format
    String base64 = Base64.getEncoder().encodeToString(encoded);
    privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
        base64.replaceAll("(.{64})", "$1\n") +
        "\n-----END PRIVATE KEY-----";
  }


  @Test
  public void testGenerate_validToken() throws Exception {
    TokenUtil tokenUtil = new TokenUtil();
    String token = tokenUtil.generate("test-key", privateKeyPem);

    assertNotNull(token);

    DecodedJWT jwt = JWT.require(
            Algorithm.RSA512(publicKey))
        .build()
        .verify(token);

    assertEquals("test-key", jwt.getKeyId());
    assertNotNull(jwt.getClaim("iat").asDouble());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate_withPKCS1_shouldFail() throws Exception {
    String invalidPem = "-----BEGIN RSA PRIVATE KEY-----\nABCDEF\n-----END RSA PRIVATE KEY-----";
    new TokenUtil().generate("key", invalidPem);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerate_withEmptyPem_shouldFail() throws Exception {
    new TokenUtil().generate("key", "");
  }
}