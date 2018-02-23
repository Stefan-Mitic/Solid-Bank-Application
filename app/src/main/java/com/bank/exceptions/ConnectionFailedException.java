package com.bank.exceptions;

/**
 * Exception for connection failures with the database.
 */
public class ConnectionFailedException extends Exception {

  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new connection failed exception.
   */
  public ConnectionFailedException() {
    super();
  }

  /**
   * Constructs a new connection failed exception with the specified detail message.
   * 
   * @param msg the detail message
   */
  public ConnectionFailedException(String msg) {
    super(msg);
  }

}
