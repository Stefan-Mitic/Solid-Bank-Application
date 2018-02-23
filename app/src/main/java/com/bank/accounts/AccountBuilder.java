package com.bank.accounts;

import java.math.BigDecimal;

/**
 * Builder design pattern for creating account objects.
 */
public interface AccountBuilder {

  /**
   * Sets the ID number of the account.
   * 
   * @param id an account ID number
   * @return the account builder (for method cascading)
   */
  public AccountBuilder setId(int id);

  /**
   * Sets the name of the account.
   * 
   * @param name the name of the account
   * @return the account builder (for method cascading)
   */
  public AccountBuilder setName(String name);

  /**
   * Sets the initial balance of the account.
   * 
   * @param balance the balance of the account
   * @return the account builder (for method cascading)
   */
  public AccountBuilder setBalance(BigDecimal balance);

  /**
   * Returns the newly created account object.
   * 
   * @return the newly created account
   */
  public Account buildAccount();

}
