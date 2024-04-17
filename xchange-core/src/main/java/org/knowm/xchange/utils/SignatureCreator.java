package org.knowm.xchange.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.enums.HashingAlgorithm;
import org.knowm.xchange.exceptions.SignatureFieldsUnsetException;

@ToString
@Slf4j
public class SignatureCreator {

  Mac mac;
  HashingAlgorithm hashingAlgorithm;
  String secret;
  String information;
  boolean encodeInBase64 = true;

  public SignatureCreator withHashingAlgorithm(HashingAlgorithm hashingAlgorithm) {
    this.hashingAlgorithm = hashingAlgorithm;
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

  public SignatureCreator withoutBase64Encode() {
    this.encodeInBase64 = false;
    return this;
  }

  public String create()
      throws SignatureFieldsUnsetException, NoSuchAlgorithmException, InvalidKeyException {
    raiseExceptionIfFieldsAreNotSet();
    mac = Mac.getInstance(hashingAlgorithm.toString());
    SecretKey secretKey =
        new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
    mac.init(secretKey);
    mac.update(information.getBytes(StandardCharsets.UTF_8));
    if (encodeInBase64) {
      return Base64.getEncoder().encodeToString(mac.doFinal());
    }
    return new String(mac.doFinal());
  }

  private void raiseExceptionIfFieldsAreNotSet() throws SignatureFieldsUnsetException {
    if (secret == null || information == null) {
      throw new SignatureFieldsUnsetException();
    }
  }
}
