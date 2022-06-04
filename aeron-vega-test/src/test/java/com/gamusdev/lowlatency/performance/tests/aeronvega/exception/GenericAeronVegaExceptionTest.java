package com.gamusdev.lowlatency.performance.tests.aeronvega.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class GenericAeronVegaExceptionTest {

    private static final String MSG = "MSG";

    @Mock
    private Throwable throwable;

    @Test
    public void GenericAeronVegaExceptionStringConstructor() {
        // When
        final var ex = new GenericAeronVegaException(MSG);

        // Verify
        Assertions.assertEquals(MSG, ex.getMessage());
    }

    @Test
    public void GenericAeronVegaExceptionThrowableConstructor() {
        // When
        final var ex = new GenericAeronVegaException(throwable);

        // Verify
        Assertions.assertEquals(throwable, ex.getCause());
    }

    @Test
    public void GenericAeronVegaExceptionStringAndThrowableConstructor() {
        // When
        final var ex = new GenericAeronVegaException(MSG, throwable);

        // Verify
        Assertions.assertEquals(MSG, ex.getMessage());
        Assertions.assertEquals(throwable, ex.getCause());
    }
}
