package donabase;

/**
 * Exception for DonaBase operations.
 */
public class DonaBaseException extends Exception {
  
  /**
   * Create exception with message and cause message
   * 
   * @param message exception message
   * @param cause cause message
   */
  public DonaBaseException(String message, String cause) {
    super("%s (%s)".formatted(message, cause));
  }
  
  /**
   * Create exception with message and cause exception
   * 
   * @param message exception message
   * @param cause cause exception
   */
  public DonaBaseException(String message, Throwable cause) {
    this(message, cause.getMessage());
  }
}
