package com.bank.exceptions;

/**
 * Exception for illegal balance amounts.
 */
public class IllegalAmountException extends Exception {

  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new illegal amount exception.
   */
  public IllegalAmountException() {
    super();
  }

  /**
   * Constructs a new illegal amount exception with the specified detail message.
   * 
   * @param msg the detail message
   */
  public IllegalAmountException(String msg) {
    super(msg);
  }

}
