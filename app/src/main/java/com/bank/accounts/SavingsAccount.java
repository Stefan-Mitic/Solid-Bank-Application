package com.bank.accounts;

import java.math.BigDecimal;

/**
 * A class to represent a savings account.
 */
public class SavingsAccount extends Account {

  /**
   *  Serial version ID.
   */
  private static final long serialVersionUID = 6510248137118634111L;

  /**
   * Creates a nullified savings account for account builder.
   */
  protected SavingsAccount() {
    /* empty constructor for builder */
  }

  /**
   * Creates a savings account with an ID number, name, and a balance.
   * 
   * @param id the savings account's ID number
   * @param name the name of the savings account
   * @param balance the balance the savings account holds
   */
  public SavingsAccount(int id, String name, BigDecimal balance) {
    setId(id);
    setName(name);
    setBalance(balance);
    findAndSetAccountType();
    findAndSetInterestRate();
  }

}
