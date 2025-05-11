package si.mazi.rescu;

public class ThalexRestProxyFactory implements IRestProxyFactory {

  @Override
  public <I> I createProxy(Class<I> restInterface, String baseUrl, ClientConfig config,
      Interceptor... interceptors) {
    return RestProxyFactory.createProxy(restInterface,
        RestProxyFactory.wrap(new ThalexRestInvocationHandler(restInterface, baseUrl, config),
            interceptors));
  }

  @Override
  public <I> I createProxy(Class<I> restInterface, String baseUrl) {
    return null;
  }
}
