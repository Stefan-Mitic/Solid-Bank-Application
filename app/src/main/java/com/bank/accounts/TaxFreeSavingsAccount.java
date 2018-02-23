package com.bank.accounts;

import java.math.BigDecimal;

/**
 * A class to represent a tax-free savings account (TFSA).
 * 
 * @author Jeffrey Li
 */
public class TaxFreeSavingsAccount extends Account {

  /**
   *  Serial version ID.
   */
  private static final long serialVersionUID = -5225000707439971081L;
  /**
   * Minimum balance for this account to remain a TFSA.
   */
  public static final BigDecimal MIN_BALANCE = new BigDecimal(5000.00);

  /**
   * Creates a nullified tax-free savings account for account builder.
   */
  protected TaxFreeSavingsAccount() {
    /* empty constructor for builder */
  }

  /**
   * Creates a tax-free savings account with an ID number, name, and a balance.
   * 
   * @param id the tax-free savings account's ID number
   * @param name the name of the tax-free savings account
   * @param balance the balance the tax-free savings account holds
   */
  public TaxFreeSavingsAccount(int id, String name, BigDecimal balance) {
    setId(id);
    setName(name);
    setBalance(balance);
    findAndSetAccountType();
    findAndSetInterestRate();
  }

}
