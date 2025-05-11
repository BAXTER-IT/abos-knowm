package org.knowm.xchange.thalex.utils;

import java.io.IOException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {

  R apply(T t) throws IOException;
}