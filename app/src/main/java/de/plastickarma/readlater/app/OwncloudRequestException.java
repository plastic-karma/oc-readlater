package de.plastickarma.readlater.app;

/**
 * Exception to signal, that there has been a problem, while communication with the owncloud instance.
 */
public final class OwncloudRequestException extends Exception {

    public OwncloudRequestException(final String message, final Exception cause) {
        super(message, cause);
    }

    public OwncloudRequestException(final String message) {
        super(message);
    }
}
