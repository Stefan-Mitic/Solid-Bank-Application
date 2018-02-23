package com.bank.users;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent a bank customer.
 */
public class Customer extends User {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = -4408305051333216206L;
  
  /**
   * The list of accounts this customer owns.
   */
  private List<Account> accounts;

  /**
   * Creates a nullified customer for user builder.
   */
  protected Customer() {
    accounts = new ArrayList<Account>();
  }

  /**
   * Creates an unauthenticated customer with an ID number, name, age, and address.
   * 
   * @param id the customer ID number
   * @param name the name of the customer
   * @param age the age of the customer
   * @param address the address of the customer
   */
  public Customer(int id, String name, int age, String address) {
    setId(id);
    setName(name);
    setAge(age);
    setAddress(address);
    findAndSetRoleId();
    accounts = new ArrayList<Account>();
    findAndUpdateAccounts();
    findAndUpdateMessageIds();
  }

  /**
   * Creates a customer with an ID number, name, age, address, and age. Customer is authenticated
   * based on the argument input.
   * 
   * @param id the customer ID number
   * @param name the name of the customer
   * @param age the age of the customer
   * @param address the address of the customer
   * @param authenticated whether or not this customer has been authenticated
   */
  public Customer(int id, String name, int age, String address, boolean authenticated) {
    this(id, name, age, address);
    if (authenticated) {
      forceAuthenticate();
    }
  }

  /**
   * Connects to database and updates the current customer's list of accounts.
   */
  public void findAndUpdateAccounts() {
    this.accounts.clear();
    List<Integer> accountIds = DatabaseSelectHelper.getAccountIds(getId());
    for (int accountId : accountIds) {
      Account account = DatabaseSelectHelper.getAccountDetails(accountId);
      accounts.add(account);
    }
  }

  /**
   * Returns a list of accounts that this user owns.
   * 
   * @return a list of accounts under this user
   */
  public List<Account> getAccounts() {
    return accounts;
  }

  /**
   * Adds an account to be owned by this user.
   * 
   * @param account the account to add
   */
  public void addAccount(Account account) {
    accounts.add(account);
  }

}
