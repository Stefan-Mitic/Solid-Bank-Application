package com.bank.exceptions;

/**
 * Exception for users attempting to access assets they do not own.
 */
public class DoesNotOwnException extends Exception {

  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new does not own amount exception.
   */
  public DoesNotOwnException() {
    super();
  }

  /**
   * Constructs a new does not own exception with the specified detail message.
   * 
   * @param msg the detail message
   */
  public DoesNotOwnException(String msg) {
    super(msg);
  }

}
