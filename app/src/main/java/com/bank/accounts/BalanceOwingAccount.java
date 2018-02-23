package com.bank.accounts;

/**
 * A class to represent a balance owing account, whose balance is non-positive.
 */
public class BalanceOwingAccount extends Account {

  /**
   *  Serial version ID.
   */
  private static final long serialVersionUID = 7053893186150432012L;

  /**
   * Creates a nullified balance owing account for account builder.
   */
  protected BalanceOwingAccount() {
    /* empty constructor for builder */
  }

}
