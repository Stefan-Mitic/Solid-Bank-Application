package com.bank.terminals;

import android.os.Parcel;
import android.os.Parcelable;

import com.bank.accounts.Account;
import com.bank.accounts.TaxFreeSavingsAccount;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.messages.Message;
import com.bank.messages.MessageHelpers;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SolidAutoTellerMachine implements AutoTellerMachine {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = -1402725789422750118L;

  /**
   * String message for illegal amount exception instances.
   */
  private static final String ILLEGAL_AMOUNT_MSG = "amount must be positive";

  /**
   * String message for illegal amount exception instances.
   */
  private static final String CONNECTION_FAILED_MSG = "update to database failed";

  /**
   * The current customer using this ATM.
   */
  private Customer currentCustomer = null;

  /**
   * Creates an ATM without customer authentication. The authenticate method must be later used.
   *
   * @param customerId a customer ID number
   */
  public SolidAutoTellerMachine(int customerId) {
    deAuthenticate();
    User selectCustomer = DatabaseSelectHelper.getUserDetails(customerId);
    if (selectCustomer instanceof Customer) {
      currentCustomer = (Customer) selectCustomer;
    }
  }

  /**
   * Creates an ATM given a customer ID number and a customer password to authenticate.
   *
   * @param customerId a customer ID number
   * @param password   the password of the customer to authenticate
   */
  public SolidAutoTellerMachine(int customerId, String password) {
    this(customerId);
  }

  @Override
  public BigDecimal checkBalance(int accountId)
          throws DoesNotOwnException {
    BigDecimal balance;
    if (DatabaseValidHelper.userOwnsAccount(currentCustomer.getId(), accountId)) {
      Account account = DatabaseSelectHelper.getAccountDetails(accountId);
      balance = account.getBalance();
    } else {
      // throw exception if user does not own account
      throw new DoesNotOwnException(DOES_NOT_OWN_ACCOUNT_MSG);
    }
    return balance;
  }

  @Override
  public boolean makeDeposit(int accountId, BigDecimal amount) throws
          IllegalAmountException, DoesNotOwnException, ConnectionFailedException {
    boolean success;
    if (DatabaseValidHelper.userOwnsAccount(currentCustomer.getId(), accountId)) {
      // check if amount is positive
      if (amount.compareTo(BigDecimal.ZERO) > 0) {
        // get current account balance from database, add deposit
        BigDecimal balance = DatabaseSelectHelper.getBalance(accountId);
        BigDecimal newBalance = balance.add(amount);
        // set and update new balance
        if (success = DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId)) {
          currentCustomer.findAndUpdateAccounts();

        } else {
          // throw exception if connection and update to database failed
          throw new ConnectionFailedException(CONNECTION_FAILED_MSG);
        }
      } else {
        // throw exception if amount is not positive
        throw new IllegalAmountException(ILLEGAL_AMOUNT_MSG);
      }
    } else {
      // throw exception if user does not own account
      throw new DoesNotOwnException(DOES_NOT_OWN_ACCOUNT_MSG);
    }
    return success;
  }

  @Override
  public boolean makeWithdrawal(int accountId, BigDecimal amount)
          throws InsufficientFundsException, IllegalAmountException,
          DoesNotOwnException, ConnectionFailedException {
    boolean success;
    if (DatabaseValidHelper.userOwnsAccount(currentCustomer.getId(), accountId)) {
      if (amount.compareTo(BigDecimal.ZERO) > 0) {
        // get current account balance from database
        BigDecimal balance = DatabaseSelectHelper.getBalance(accountId);
        // check if amount is valid
        if (balance.compareTo(amount) >= 0) {

          // subtract amount from current balance
          BigDecimal newBalance = balance.subtract(amount);
          // set and update new balance
          if (success = DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId)) {
            checkTfsaToSavings(accountId);
            currentCustomer.findAndUpdateAccounts();

          } else {
            // throw exception if connection and update to database failed
            throw new ConnectionFailedException(CONNECTION_FAILED_MSG);
          }
        } else {
          // throw exception if balance is less than withdraw amount
          String msg = "insufficient funds, current balance is less than given amount";
          throw new InsufficientFundsException(msg);
        }
      } else {
        // throw exception if amount is not positive
        throw new IllegalAmountException(ILLEGAL_AMOUNT_MSG);
      }
    } else {
      // throw exception if user does not own account
      throw new DoesNotOwnException(DOES_NOT_OWN_ACCOUNT_MSG);
    }
    return success;
  }

  @Override
  public List<Account> listAccounts() {
    // clear and retrieve updated list of accounts
    currentCustomer.findAndUpdateAccounts();
    // return list of accounts the customer owns
    return currentCustomer.getAccounts();
  }

  @Override
  public List<Message> listMessages() {
    int customerId = currentCustomer.getId();
    return DatabaseSelectHelper.getAllMessages(customerId);
  }

  @Override
  public List<Integer> listMessageIds() {
    currentCustomer.findAndUpdateMessageIds();
    return currentCustomer.getMessageIds();
  }

  @Override
  public String viewMessage(int messageId) throws DoesNotOwnException {
    List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(currentCustomer.getId());
    if (!messageIds.contains(messageId)) {
      throw new DoesNotOwnException(DOES_NOT_OWN_MESSAGE_MSG);
    }
    // get the message and update its view status
    String message = DatabaseSelectHelper.getSpecificMessage(messageId);
    DatabaseUpdateHelper.updateUserMessageState(messageId);
    return message;
  }

  @Override
  public void deAuthenticate() {
    currentCustomer = null;
//    authenticated = false;
  }

  /**
   * Changes the customer account from a TFSA to a savings account if the balance is under $5000.
   * Returns the message ID notifying the user of such change.
   *
   * @param accountId the customers account id
   * @return the messageId if account type was changed, -1 otherwise
   */
  private int checkTfsaToSavings(int accountId) {
    int messageId = DatabaseValidHelper.INVALID_ID;
    // get TFSA type ID and given account type ID
    int tfsaTypeId = AccountTypesEnumMap.getAccountTypeId(AccountTypes.TFSA);
    int typeId = DatabaseSelectHelper.getAccountType(accountId);
    // check if account is a TFSA
    if (tfsaTypeId == typeId) {
      // check if balance is less than $5000
      BigDecimal balance = DatabaseSelectHelper.getBalance(accountId);
      if (balance.compareTo(TaxFreeSavingsAccount.MIN_BALANCE) == -1) {
        // if below $5000, change account type to savings
        int savingsTypeId = AccountTypesEnumMap.getAccountTypeId(AccountTypes.SAVING);
        if (DatabaseUpdateHelper.updateAccountType(savingsTypeId, accountId)) {
          // write message to user notifying of account type change
          int customerId = currentCustomer.getId();
          messageId = MessageHelpers.notifySavingsAccountChange(customerId, accountId);
        }
      }
    }
    return messageId;
  }

  @Override
  public int checkAccount(int accountId, BigDecimal balance) {
    Account account = DatabaseSelectHelper.getAccountDetails(accountId);
    // check if account is a TFSA and balance is less than 5000
    if (account instanceof TaxFreeSavingsAccount
            && balance.compareTo(TaxFreeSavingsAccount.MIN_BALANCE) == -1) {
      // get accountType id of savings account
      int typeId = AccountTypesEnumMap.getAccountTypeId(AccountTypes.SAVING);
      if (DatabaseUpdateHelper.updateAccountType(typeId, accountId)) {
        String message = ("Balance is under $5000.00. Account with id " + accountId
                + " has now been transformed into a Savings Account.");
        return DatabaseInsertHelper.insertMessage(currentCustomer.getId(), message);
      }
    }
    return DatabaseValidHelper.INVALID_ID;
  }
}
