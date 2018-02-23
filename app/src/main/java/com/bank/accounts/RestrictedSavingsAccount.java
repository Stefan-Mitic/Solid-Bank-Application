package com.bank.accounts;

/**
 * A class to represent a restricted savings account, which can only be edited by a customer.
 */
public class RestrictedSavingsAccount extends Account {

  /**
   *  Serial version ID.
   */
  private static final long serialVersionUID = 7180513546582117741L;

  /**
   * Creates a nullified chequing account for account builder.
   */
  protected RestrictedSavingsAccount() {
    /* empty constructor for builder */
  }

}
