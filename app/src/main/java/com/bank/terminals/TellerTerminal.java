package com.bank.terminals;

import com.bank.accounts.Account;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import java.math.BigDecimal;
import java.util.List;

/**
 * A terminal operated by a teller. A teller terminal can operate on an automated teller machine.
 */
public interface TellerTerminal extends Terminal {

  /**
   * String exception message for unset customer.
   */
  static final String UNSET_CUSTOMER_MSG = "customer has not been authenticated by teller";

  /**
   * String exception message for attempting to access restricted savings account as a teller.
   */
  static final String RESTRICTED_ERROR = "cannot access restricted savings account as a teller";

  /**
   * Sets the current customer for the teller to modify.
   *
   * @param customerId a customer ID number
   */
  boolean setCustomer(int customerId);

  /**
   * Attempts to authenticate the current customer. If the given password is correct, the customer
   * is authenticated, otherwise they remain unauthenticated.
   *
   * @param password the password of the customer to authenticate
   */
  boolean authenticateCustomer(String password);

  /**
   * Creates a new customer and writes them into the database, if teller has been authenticated.
   *
   * @param name     the name of the new customer
   * @param age      the age of the new customer
   * @param address  the address of the new customer
   * @param password the password of the new customer
   * @return the userId of the new user made.
   */
  int makeNewUser(String name, int age, String address, String password)
  ;

  /**
   * Creates an account with the given information and register it to the current customer, if both
   * the customer and teller are authenticated. Returns <code>true</code> if update was successful.
   *
   * @param name        the name of the new account
   * @param balance     the balance of the new account
   * @param accountType the type of the new account
   * @return the accountId of the new account made for user
   */
  int makeNewAccount(String name, BigDecimal balance, int accountType)
  ;

  /**
   * Returns the balance of account with ID number accountId, if user has access. Returns
   * <code>null</code> if account ID is not owned by user.
   *
   * @param accountId an account ID number owned under current customer
   * @return the balance of account with ID accountId
   * @throws DoesNotOwnException if customer does not own account with ID accountId
   */
  BigDecimal checkBalance(int accountId) throws DoesNotOwnException;

  /**
   * Returns the total balance of all accounts the current customer holds.
   *
   * @return the total balances of the current customer
   */
  BigDecimal checkBalance();

  /**
   * Makes a deposit of the given amount to the account with ID accountId.
   *
   * @param amount    the amount to deposit
   * @param accountId an account ID number owned under current customer
   * @return <code>true</code> if deposit was successful
   * @throws IllegalAmountException    if amount is not positive
   * @throws DoesNotOwnException       if customer does not own account with ID accountId
   * @throws ConnectionFailedException if connection and update to database failed
   */
  boolean makeDeposit(int accountId, BigDecimal amount) throws
          IllegalAmountException, DoesNotOwnException, ConnectionFailedException;

  /**
   * Makes a withdrawal of the given amount from the account with ID accountId.
   *
   * @param amount    the amount to withdraw
   * @param accountId an account ID number owned under current customer
   * @return <code>true</code> if withdrawal was successful
   * @throws InsufficientFundsException if account balance is less than withdrawal amount
   * @throws IllegalAmountException     if amount is not positive
   * @throws DoesNotOwnException        if customer does not own account with ID accountId
   * @throws ConnectionFailedException  if connection and update to database failed
   */
  boolean makeWithdrawal(int accountId, BigDecimal amount)
          throws InsufficientFundsException, IllegalAmountException,
          DoesNotOwnException, ConnectionFailedException;

  /**
   * Adds interest to the account with ID accountId, if customer and teller are both authenticated,
   * and the account belongs to the current customer.
   *
   * @param accountId an account ID number
   * @throws DoesNotOwnException if customer does not own account with ID accountId
   */
  BigDecimal giveInterest(int accountId) throws DoesNotOwnException;

  /**
   * Adds interest to every account under the current customer, if customer and teller are both
   * authenticated.
   */
  BigDecimal giveInterest();

  /**
   * Returns a list of bank accounts for the current customer.
   *
   * @return a list of accounts owned under current customer
   */
  List<Account> listAccounts();

  /**
   * Leaves a message for the user with ID userId and returns the message ID from the database.
   *
   * @param userId  a user ID number
   * @param message the message to leave to the user with ID userId
   * @return the message ID number of the message
   */
  int leaveMessage(int userId, String message);

  /**
   * Leaves a message for the current customer the teller is operating on.
   *
   * @param message the message to leave to the current customer
   * @return the message ID number of the message
   */
  int leaveMessage(String message);

  /**
   * Returns a list of message IDs of the customer the teller is operating on.
   *
   * @return a list of message IDs of the customer the teller is operating on
   */
  List<Integer> listCustomerMessageIds();

  /**
   * Deauthenticates the current customer. Returns <code>true</code> if customer was
   * deauthenticated, <code>false</code> if there was not customer to deauthenticate.
   *
   * @returns <code>true</code> if customer was deauthenticated, <code>false</code> otherwise
   */
  boolean deAuthenticateCustomer();

  /**
   * Returns the id the current customer if valid, -1 if there is none.
   *
   * @return the current customer's id, or -1 if there's no current customer
   */
  int getCustomerId();

  /**
   * Changes the customer account from a TFSA to a savings accounts if the balance is under $5000.
   *
   * @param accountId the customers account id.
   * @param balance   the current balance.
   * @return the messageId if account type was changed
   */
  int checkAccount(int accountId, BigDecimal balance);

  /**
   * Adds money owed to the Balance Owing Account.
   *
   * @param accountId the ID of the balance owing account
   * @param amount    the loan amount
   * @return <code>true</code> if adding loan was successful
   * @throws ConnectionFailedException if connection and update to database failed
   * @throws IllegalAmountException    if amount is not positive
   * @throws DoesNotOwnException       if customer does not own balance owing account with ID accountId
   */
  boolean addLoans(int accountId, BigDecimal amount) throws
          ConnectionFailedException, IllegalAmountException, DoesNotOwnException;

  /**
   * Updates the name of the customer with ID userId.
   *
   * @param name         the new name to update
   * @param userId       the ID of the user to be updated
   * @param userPassword the password of the user to be updated
   * @return <code>true</code> if update was successful
   */
  boolean updateUserName(String name, int userId, String userPassword);

  /**
   * Updates the age of the customer with ID userId.
   *
   * @param age          the new age to update
   * @param userId       the ID of the user to be updated
   * @param userPassword the password of the user to be updated
   * @return <code>true</code> if update was successful
   */
  boolean updateUserAge(int age, int userId, String userPassword);

  /**
   * Updates the address of the customer with ID userId.
   *
   * @param address      the new address to update
   * @param userId       the ID of the user to be updated
   * @param userPassword the password of the user to be updated
   * @return <code>true</code> if update was successful
   */
  boolean updateUserAddress(String address, int userId, String userPassword);

  /**
   * Updates the password of the customer with ID userId.
   *
   * @param password     the new password to update
   * @param userId       the ID of the user to be updated
   * @param userPassword the password of the user to be updated
   * @return <code>true</code> if update was successful
   */
  boolean updateUserPassword(String password, int userId, String userPassword);

  /**
   * Adds a customer to an account.
   *
   * @param accountId the id of the account to be added
   * @param userId the id of the user to add
   * @return whether the joint was successful
   */
  boolean createJointAccount(int accountId, int userId);

}
