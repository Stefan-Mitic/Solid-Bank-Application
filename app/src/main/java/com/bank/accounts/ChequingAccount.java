package com.bank.accounts;

import java.math.BigDecimal;

/**
 * A class to represent a chequing account.
 */
public class ChequingAccount extends Account {

  /**
   *  Serial version ID.
   */
  private static final long serialVersionUID = -4894084327530923484L;

  /**
   * Creates a nullified chequing account for account builder.
   */
  protected ChequingAccount() {
    /* empty constructor for builder */
  }

  /**
   * Creates a chequing account with an ID number, name, and a balance.
   * 
   * @param id the chequing account's ID number
   * @param name the name of the chequing account
   * @param balance the balance the chequing account holds
   */
  public ChequingAccount(int id, String name, BigDecimal balance) {
    setId(id);
    setName(name);
    setBalance(balance);
    findAndSetAccountType();
    findAndSetInterestRate();
  }

}
