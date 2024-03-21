package org.knowm.xchange.exceptions;

public class SignatureFieldsUnsetException extends Exception {

  public SignatureFieldsUnsetException() {
    super("All fields must be set before creating a signature.");
  }
}
