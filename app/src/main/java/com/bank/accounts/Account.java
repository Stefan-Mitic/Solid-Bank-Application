package com.bank.accounts;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A class to represent a bank account.
 */
public abstract class Account implements Serializable {

  /**
   *  Serial version ID.
   */
  private static final long serialVersionUID = -5818107574140475134L;

  /**
   * The ID number of this account.
   */
  private int id = DatabaseValidHelper.INVALID_ID;

  /**
   * The name of this account.
   */
  private String name;

  /**
   * The total balance amount this account holds.
   */
  private BigDecimal balance;

  /**
   * The interest rate of this chequing account.
   */
  private BigDecimal interestRate;

  /**
   * Integer to represent the type of account.
   */
  private int type = DatabaseValidHelper.INVALID_ID;

  /**
   * Returns the ID number of this account.
   * 
   * @return the account's ID number
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the new ID number of this account.
   * 
   * @param id the account's new ID number
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the name of this account.
   * 
   * @return the account's name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the new name of this account.
   * 
   * @param name the account's new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the balance this account holds.
   * 
   * @return the account's balance
   */
  public BigDecimal getBalance() {
    return balance;
  }

  /**
   * Sets the new balance this account holds.
   * 
   * @param balance the account's new balance
   */
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  /**
   * Finds the account type as listed in the Accounts table, and sets the given account type to this
   * account.
   */
  protected void findAndSetAccountType() {
    this.type = DatabaseSelectHelper.getAccountType(id);
  }

  /**
   * Returns the type number for this account.
   * 
   * @return the account's type number
   */
  public int getType() {
    return type;
  }

  /**
   * Finds the interest rate as listed in the AccountTypes table, and sets the given interest rate
   * to this account.
   */
  public void findAndSetInterestRate() {
    interestRate = DatabaseSelectHelper.getInterestRate(getType());
  }

  /**
   * Calculates the interest on the current account balance, and updates the balance of this
   * chequing account with the added interest.
   * 
   * @return the interest amount added to the account's balance
   */
  public BigDecimal addInterest() {
    BigDecimal interest = BigDecimal.ZERO;
    if (interestRate != null) {
      // calculate the balance with added interest
      BigDecimal interestMultiplier = interestRate.add(BigDecimal.ONE);
      BigDecimal newBalance = balance.multiply(interestMultiplier);
      newBalance = newBalance.setScale(2, RoundingMode.CEILING);
      interest = newBalance.subtract(balance);
      // set the balance to the new balance
      if (DatabaseUpdateHelper.updateAccountBalance(newBalance, getId())) {
        setBalance(newBalance);
      }
    }
    return interest;
  }

}
