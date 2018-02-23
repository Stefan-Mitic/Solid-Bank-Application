package com.bank.messages;

import com.bank.accounts.TaxFreeSavingsAccount;
import com.bank.databasehelper.DatabaseInsertHelper;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * Helper functions for the bank leaving messages.
 */
public class MessageHelpers {

  /**
   * Writes a message to the database, notifying the user of a TFSA account changing into a savings
   * account. Returns the message ID.
   * 
   * @param userId the ID of the user to leave message for
   * @param accountId the ID of the account affected
   * @return the message ID number
   */
  public static int notifySavingsAccountChange(int userId, int accountId) {
    String msgFormat = "SYSTEM: TFSA balance was under $%s. ";
    msgFormat += "Account with ID %d has now been transformed into a SAVINGS account.";
    String msg = String.format(Locale.CANADA, msgFormat, TaxFreeSavingsAccount.MIN_BALANCE,
            accountId);
    return DatabaseInsertHelper.insertMessage(userId, msg);
  }

  /**
   * Writes a message to the database, notifying the user of interest being added all of their
   * accounts. Returns the message ID.
   * 
   * @param userId the ID of the user to leave message for
   * @return the message ID number
   */
  public static int notifyInterest(int userId, BigDecimal totalInterest) {
    String msgFormat = "SYSTEM: interest totaling $%s has been added to all accounts.";
    String msg = String.format(msgFormat, totalInterest);
    return DatabaseInsertHelper.insertMessage(userId, msg);
  }

  /**
   * Writes a message to the database, notifying the user of interest being added to one of their
   * accounts. Returns the message ID.
   * 
   * @param userId the ID of the user to leave message for
   * @param accountId the ID of the account affected
   * @param interest the amount of interest added to account
   * @return the message ID number
   */
  public static int notifyInterest(int userId, int accountId, BigDecimal interest) {
    String msgFormat = "SYSTEM: interest of $%s has been added to account with ID %d.";
    String msg = String.format(Locale.CANADA, msgFormat, interest, accountId);
    return DatabaseInsertHelper.insertMessage(userId, msg);
  }

  /**
   * Writes a message to the database, notifying the user they have been added to a joint account.
   *
   * @param userId the ID of the user to leave message for
   * @param accountId the ID of the joint account
   * @return the message ID number
   */
  public static int notifyJointAccount(int userId, int accountId) {
    String msgFormat = "SYSTEM: a joint account with ID %d has been created.";
    String msg = String.format(Locale.CANADA, msgFormat, accountId);
    return DatabaseInsertHelper.insertMessage(userId, msg);
  }

}
