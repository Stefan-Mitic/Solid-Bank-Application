package com.bank.exceptions;

/**
 * Exception for insufficient account funds.
 */
public class InsufficientFundsException extends Exception {

  /**
   * Default serial version ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new insufficient funds exception.
   */
  public InsufficientFundsException() {
    super();
  }

  /**
   * Constructs a new insufficient funds exception with the specified detail message.
   * 
   * @param msg the detail message
   */
  public InsufficientFundsException(String msg) {
    super(msg);
  }

}
