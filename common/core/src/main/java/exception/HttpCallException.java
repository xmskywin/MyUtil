package exception;


/**
 * @author xcm
 * @date 2024/12/20 10:15
 */
public class HttpCallException extends RuntimeException {
    public HttpCallException(String message) {
        super(message);
    }

    public HttpCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
