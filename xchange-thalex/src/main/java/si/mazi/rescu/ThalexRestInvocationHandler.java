package si.mazi.rescu;

import java.io.IOException;

public class ThalexRestInvocationHandler extends RestInvocationHandler {

  public ThalexRestInvocationHandler(Class<?> restInterface, String url, ClientConfig config) {
    super(restInterface, url, config);
  }

  @Override
  protected Object mapInvocationResult(
      InvocationResult invocationResult, RestMethodMetadata methodMetadata) throws IOException {
    if (invocationResult.getHttpBody().contains("error")) {
      super.mapInvocationResult(
          new InvocationResult(invocationResult.getHttpBody(), 400), methodMetadata);
    }

    return super.mapInvocationResult(invocationResult, methodMetadata);
  }
}
