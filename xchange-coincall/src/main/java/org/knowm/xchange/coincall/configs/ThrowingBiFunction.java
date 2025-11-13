package org.knowm.xchange.coincall.configs;

import java.io.IOException;
import org.knowm.xchange.coincall.exceptions.CoincallException;

@FunctionalInterface
public interface ThrowingBiFunction<A, B, R> {

  R apply(A a, B b) throws IOException, CoincallException;
}