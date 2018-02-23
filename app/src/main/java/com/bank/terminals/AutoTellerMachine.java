package com.bank.terminals;

import com.bank.accounts.Account;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import java.math.BigDecimal;
import java.util.List;

/**
 * An automated teller machine operated by a customer.
 */
public interface AutoTellerMachine extends Terminal {

  /**
   * Returns the balance of account with ID number accountId, if user has access.
   *
   * @param accountId an account ID number owned under current customer
   * @return the balance of account with ID accountId
   * @throws DoesNotOwnException     if customer does not own account with ID accountId
   */
  BigDecimal checkBalance(int accountId) throws DoesNotOwnException;

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
  boolean makeDeposit(int accountId, BigDecimal amount) throws IllegalAmountException, DoesNotOwnException, ConnectionFailedException;

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
   * Returns a list of bank accounts for the current customer.
   *
   * @return a list of accounts owned under current customer
   */
  List<Account> listAccounts();

  /**
   * Changes the customer account from a TFSA to a Savings Account if their balance is under $5000.
   *
   * @param accountId the customers account id.
   * @param balance   the current balance.
   * @return the messageId if account type was changed
   */
  int checkAccount(int accountId, BigDecimal balance);

}
