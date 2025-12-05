package org.knowm.xchange.utils.jackson;
/**
 * Marker interface for types that want to receive their original raw JSON during deserialization.
 * <p>
 * When a class implements this interface, and it is deserialized through
 * {@link RawJsonDataDeserializer}, the deserializer will set the exact JSON substring
 * corresponding to that object (or list element) into {@link #setRawJson(String)}.
 * <p>
 */
public interface RawJsonAware {

  /**
   * Stores the exact raw JSON string that was used to deserialize this object. This is injected by
   * {@link RawJsonDataDeserializer} after normal Jackson deserialization has already populated all
   * mapped fields.
   *
   * @param rawJson the unmodified JSON corresponding exactly to this object
   */
  void setRawJson(String rawJson);
}