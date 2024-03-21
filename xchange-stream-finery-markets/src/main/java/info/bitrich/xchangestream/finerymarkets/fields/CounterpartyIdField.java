package info.bitrich.xchangestream.finerymarkets.fields;

import quickfix.IntField;

public class CounterpartyIdField extends IntField {

  public static final int FIELD = 958;

  public CounterpartyIdField() {
    super(FIELD);
  }

  public CounterpartyIdField(int data) {
    super(FIELD, data);
  }
}
