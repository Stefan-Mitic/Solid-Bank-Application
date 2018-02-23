package com.bank.accounts;

import com.bank.generics.AccountTypes;
import java.math.BigDecimal;

/**
 * A factory constructor implementation of AccountBuilder.
 * 
 * @author Jeffrey Li
 */
public class SolidAccountBuilder implements AccountBuilder {

  /**
   * The account object that this builder is creating.
   */
  private Account account;

  /**
   * Creates an Account builder for the account of account type typeName.
   * 
   * @param typeName the role of the user object
   */
  public SolidAccountBuilder(String typeName) {
    if (typeName.equalsIgnoreCase(AccountTypes.SAVING.toString())) {
      account = new SavingsAccount();
    } else if (typeName.equalsIgnoreCase(AccountTypes.CHEQUING.toString())) {
      account = new ChequingAccount();
    } else if (typeName.equalsIgnoreCase(AccountTypes.TFSA.toString())) {
      account = new TaxFreeSavingsAccount();
    } else if (typeName.equalsIgnoreCase(AccountTypes.RESTRICTED.toString())) {
      account = new RestrictedSavingsAccount();
    } else if (typeName.equalsIgnoreCase(AccountTypes.OWING.toString())) {
      account = new BalanceOwingAccount();
    } else {
      // TODO: throws invalid account type exception?
    }
  }

  @Override
  public AccountBuilder setId(int id) {
    account.setId(id);
    return this;
  }

  @Override
  public AccountBuilder setName(String name) {
    account.setName(name);
    return this;
  }

  @Override
  public AccountBuilder setBalance(BigDecimal balance) {
    account.setBalance(balance);
    return this;
  }

  @Override
  public Account buildAccount() {
    account.findAndSetAccountType();
    account.findAndSetInterestRate();
    return account;
  }

}
