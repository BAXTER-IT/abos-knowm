package org.knowm.xchange.thalex.services;

import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.thalex.config.JwtGenerator;
import org.knowm.xchange.thalex.utils.TokenUtil;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

public final class ThalexDigest implements ParamsDigest {

  private final String keyName;
  private final String privateKey;
  private final JwtGenerator jwtGenerator;

  public ThalexDigest(String keyName, String privateKey) {
    this(keyName, privateKey, new TokenUtil());
  }

  public ThalexDigest(String keyName, String privateKey, JwtGenerator jwtGenerator) {
    this.keyName = keyName;
    this.privateKey = privateKey;
    this.jwtGenerator = jwtGenerator;
  }

  @Override
  public String digestParams(RestInvocation restInvocation) throws ExchangeException {
    try {
      String jwt = jwtGenerator.generate(keyName, privateKey);
      return "Bearer " + jwt;
    } catch (Exception e) {
      throw new ExchangeException(
          "Error creating JWT for " + restInvocation.getMethodMetadata().getMethodName(), e);
    }
  }

}
