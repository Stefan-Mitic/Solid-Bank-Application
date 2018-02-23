package com.bank.terminals;

import com.bank.exceptions.DoesNotOwnException;
import com.bank.messages.Message;

import java.io.Serializable;
import java.util.List;

/**
 * Generic bank terminal that can authenticate and deauthenticate users.
 */
public interface Terminal extends Serializable {

  /**
   * String message for unauthenticated admin.
   */
  static final String UNAUTHENTICATED_ADMIN_MSG = "admin has not been authenticated";

  /**
   * String message for unauthenticated teller.
   */
  static final String UNAUTHENTICATED_TELLER_MSG = "teller has not been authenticated";

  /**
   * String message for unauthenticated customer.
   */
  static final String UNAUTHENTICATED_CUSTOMER_MSG = "customer has not been authenticated";

  /**
   * String message for illegal amount exception instances.
   */
  static final String ILLEGAL_AMOUNT_MSG = "amount must be positive";

  /**
   * String message for illegal amount exception instances.
   */
  static final String CONNECTION_FAILED_MSG = "update to database failed";

  /**
   * String message for accounts the user the does not own.
   */
  static final String DOES_NOT_OWN_ACCOUNT_MSG = "user does not own account";

  /**
   * String message for messages the user the does not own.
   */
  static final String DOES_NOT_OWN_MESSAGE_MSG = "user does not own message";


  /**
   * Deauthenticates the current user.
   */
  public void deAuthenticate();

  /**
   * Returns a list of messages for the current user using this terminal.
   *
   * @return list of messages for current user
   */
  public List<Message> listMessages();

  /**
   * Returns a list of message IDs for the current user using this terminal.
   *
   * @return list of message IDs for current user
   */
  public List<Integer> listMessageIds();

  /**
   * Returns the message and changes the message status to viewed.
   *
   * @param messageId a message ID number
   * @return the message with ID messageId
   * @throws DoesNotOwnException if user does not own the message with messageId
   */
  public String viewMessage(int messageId) throws DoesNotOwnException;

}
