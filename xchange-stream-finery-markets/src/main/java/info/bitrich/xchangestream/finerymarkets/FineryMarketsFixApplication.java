package info.bitrich.xchangestream.finerymarkets;

import static org.knowm.xchange.enums.HashingAlgorithm.SHA384;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.finerymarkets.fields.CounterpartyIdField;
import io.reactivex.subjects.ReplaySubject;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.knowm.xchange.exceptions.SignatureFieldsUnsetException;
import org.knowm.xchange.fixprotocol.SignatureCreator;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.MessageUtils;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.Password;
import quickfix.field.RawData;
import quickfix.field.RawDataLength;
import quickfix.fix44.ExecutionReport;

class FineryMarketsFixApplication extends MessageCracker implements Application {

  @Getter
  private final ReplaySubject<ExecutionReport> executionReportSubject =
      ReplaySubject.create();
  ObjectMapper objectMapper = new ObjectMapper();
  FineryMarketsStreamingExchange exchange;

  public FineryMarketsFixApplication(FineryMarketsStreamingExchange exchange) {
    this.exchange = exchange;
  }

  protected void onMessage(ExecutionReport message, SessionID sessionID)
      throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
    message.getField(new CounterpartyIdField());
    executionReportSubject.onNext(message);
  }

  @Override
  public void toApp(Message message, SessionID sessionID) throws DoNotSend {
    // TODO What happens when DoNotSend is thrown? Will it try to log on again?
    if (MessageUtils.isLogon(message.toString())) {
      String rawData = generateRawData();

      message.setField(new RawData(rawData));
      message.setField(new RawDataLength(rawData.length()));
      message.setField(new Password(generateSignature(rawData)));
    }
  }

  @Override
  public void fromApp(Message message, SessionID sessionID)
      throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    crack(message, sessionID);
  }

  private String generateRawData() throws DoNotSend {
    Long nonce = System.currentTimeMillis();
    Map<String, Long> rawData = new HashMap<>();
    rawData.put("nonce", nonce);
    rawData.put("timestamp", nonce);
    String rawDataString;
    try {
      rawDataString = objectMapper.writeValueAsString(rawData);
    } catch (JsonProcessingException e) {
      throw new DoNotSend();
    }
    return rawDataString;
  }

  private String generateSignature(String rawData) throws DoNotSend {
    try {
      return new SignatureCreator()
          .withHashingAlgorithm(SHA384)
          .withInformation(rawData)
          .withSecret(exchange.getExchangeSpecification().getSecretKey())
          .createSignature();
    } catch (SignatureFieldsUnsetException | NoSuchAlgorithmException e) {
      throw new DoNotSend();
    }
  }

  // region Method overrides that are not used
  @Override
  public void onCreate(SessionID sessionID) {
    //     This method is called when quickfix creates a new session. A session comes into and
    // remains in existence for the life of the application. Sessions exist whether a counterparty
    // is connected to it. As soon as a session is created, you can begin sending messages to it. If
    // no one is logged on, the messages will be sent at the time a connection is established with
    // the counterparty.
  }

  @Override
  public void onLogon(SessionID sessionID) {
    //      This callback notifies you when a valid logon has been established with a counterparty.
    // This is called when a connection has been established and the FIX logon process has completed
    // with both parties exchanging valid logon messages.
  }

  @Override
  public void onLogout(SessionID sessionID) {
    //      This callback notifies you when an FIX session is no longer online. This could happen
    // during a normal logout exchange or because of a forced termination or a loss of network
    // connection.
  }

  @Override
  public void toAdmin(Message message, SessionID sessionID) {
    //      This callback provides you with a peek at the administrative messages that are being
    // sent from your FIX engine to the counterparty. This is normally not useful for an
    // application however it is provided for any logging you may wish to do. Notice that the
    // FIX::Message is not const. This allows you to add fields to an administrative message before
    // it is sent out.
  }

  @Override
  public void fromAdmin(Message message, SessionID sessionID)
      throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    //      This callback notifies you when an administrative message is sent from a counterparty to
    // your FIX engine. This can be useful for doing extra validation on logon messages such as for
    // checking passwords. Throwing a RejectLogon exception will disconnect the counterparty.
  }
  // endregion
}
