package com.gamusdev.lowlatency.performance.tests.aeronvega.exception;


/**
 * GenericAeronVegaException exception type
 */
public class GenericAeronVegaException extends Exception
{
    /**
     * Constructor with an exception code and a message
     *
     * @param customMsg the message for the exception
     */
    public GenericAeronVegaException(final String customMsg)
    {
        super(customMsg);
    }

    /**
     * Constructor with an exception code, and cause of the exception
     *
     * @param cause the cause of the exception
     */
    public GenericAeronVegaException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructor with an exception code, message and cause of the exception
     *
     * @param cause the cause of the exception
     * @param customMessage the message for the exception
     */
    public GenericAeronVegaException(final String customMessage, final Throwable cause)
    {
        super(customMessage, cause);
    }
}
