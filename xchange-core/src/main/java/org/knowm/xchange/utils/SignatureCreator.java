package org.knowm.xchange.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
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
  boolean encodeInHex = false;
  boolean uppercase = false;

  private static String toHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

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

  public SignatureCreator encodeAsHex() {
    this.encodeInHex = true;
    this.encodeInBase64 = false; // mutually exclusive
    return this;
  }

  public SignatureCreator uppercase() {
    this.uppercase = true;
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
    byte[] raw = mac.doFinal();
    String signature;
    if (encodeInBase64) {
      signature = Base64.getEncoder().encodeToString(raw);
    } else if (encodeInHex) {
      signature = toHex(raw);
    } else {
      signature = new String(raw);
    }
    if (uppercase) {
      signature = signature.toUpperCase(Locale.ROOT);
    }
    return signature;
  }

  private void raiseExceptionIfFieldsAreNotSet() throws SignatureFieldsUnsetException {
    if (secret == null || information == null) {
      throw new SignatureFieldsUnsetException();
    }
  }
}
