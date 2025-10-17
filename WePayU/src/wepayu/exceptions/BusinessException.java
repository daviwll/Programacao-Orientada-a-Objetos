package wepayu.exceptions;

/**
 * Base type for all domain (business) checked exceptions.
 *
 * :param message: Human-readable error text.
 * :type message: String
 */
public class BusinessException extends Exception {
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}