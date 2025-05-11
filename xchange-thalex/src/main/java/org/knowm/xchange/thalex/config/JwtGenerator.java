package org.knowm.xchange.thalex.config;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@FunctionalInterface
public interface JwtGenerator {

  String generate(String key, String secret)
      throws NoSuchAlgorithmException, InvalidKeySpecException;
}