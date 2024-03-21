package org.knowm.xchange.fixprotocol;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import org.knowm.xchange.enums.HashingAlgorithm;
import org.knowm.xchange.exceptions.SignatureFieldsUnsetException;

public class SignatureCreator {

  Mac mac;
  String secret;
  String information;

  public SignatureCreator withHashingAlgorithm(HashingAlgorithm hashingAlgorithm)
      throws NoSuchAlgorithmException {
    mac = Mac.getInstance(hashingAlgorithm.toString());
    return this;
  }

  public SignatureCreator withSecret(String secret) {
    this.secret = secret;
    return this;
  }

  public SignatureCreator withInformation(String information) {
    this.information = information;
    return this;
  }

  public String createSignature() throws SignatureFieldsUnsetException {
    raiseExceptionIfFieldsAreNotSet();
    return null;
  }

  private void raiseExceptionIfFieldsAreNotSet() throws SignatureFieldsUnsetException {
    if (mac == null || secret == null || information == null) {
      throw new SignatureFieldsUnsetException();
    }
  }
}
