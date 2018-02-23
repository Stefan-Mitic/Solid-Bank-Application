package com.bank.databasehelper;

import java.math.BigDecimal;

/**
 * Helper methods for inserting new data into the database tables. Starter code by Joe Bettridge.
 */
public class DatabaseInsertHelper {

  /**
   * Returns <code>true</code> if all arguments for user information are valid.
   * 
   * @param name the name of the account
   * @param balance the initial balance of the account
   * @param typeId the account type ID number of the account
   * @return <code>true</code> if all arguments are valid
   */
  private static boolean validAccountInfo(String name, BigDecimal balance, int typeId) {
    boolean validBalance = DatabaseValidHelper.validBalance(balance, typeId);
    boolean validTypeId = DatabaseValidHelper.validAccountTypeId(typeId);
    return !name.isEmpty() && validBalance && validTypeId;
  }

  /**
   * Connects and inserts a new entry into the Accounts table, and returns the account ID number.
   * 
   * @param name the name of the account
   * @param balance the balance that this account holds
   * @param typeId the type ID number of the account
   * @return the account ID number, -1 otherwise
   */
  public static int insertAccount(String name, BigDecimal balance, int typeId) {
    // check if inputs are valid
    if (validAccountInfo(name, balance, typeId)) {
      // establish connection to database and insert to table
      return DatabaseDriverHelper.driverInsertAccount(name, balance, typeId);
    }
    return DatabaseValidHelper.INVALID_ID;
  }

  /**
   * Connects and inserts new entry into the AccountTypes table, and returns <code>true</code> if
   * insertion into AccountTypes table was successful.
   * 
   * @param name the name of the account type, must be CHEQUING, SAVING, or TFSA
   * @param interestRate the interest rate of the account type
   * @return <code>true</code> if table insertion was successful
   */
  public static int insertAccountType(String name, BigDecimal interestRate) {
    // check if inputs are valid
    if (!name.isEmpty() && DatabaseValidHelper.validInterestRate(interestRate)) {
      // establish connection to database and insert to table
      return DatabaseDriverHelper.driverInsertAccountType(name, interestRate);
    }
    return DatabaseValidHelper.INVALID_ID;
  }

  /**
   * Returns <code>true</code> if all arguments for user information are valid.
   * 
   * @param name the name of the user
   * @param age the age of the user
   * @param address the address of the user
   * @param roleId the role ID number of the user
   * @param password the password of the user
   * @return <code>true</code> if all arguments are valid
   */
  private static boolean validUserInfo(String name, int age, String address, int roleId,
      String password) {
    boolean validAddress = DatabaseValidHelper.validAddress(address);
    boolean validRoleId = DatabaseValidHelper.validRoleId(roleId);
    boolean validPassword = DatabaseValidHelper.validPassword(password);
    return !name.isEmpty() && age >= 0 && validAddress && validRoleId && validPassword;
  }

  /**
   * Connects and inserts a new entry into the Users table. Returns the account id of the user if
   * insertion to Users table was successful. Otherwise returns -1.
   * 
   * @param name the name of the user
   * @param age the age of the user
   * @param address the address of the user
   * @param roleId the role ID number of the user
   * @param password the password of the user
   * @return the user's ID number if successful, -1 otherwise
   */
  public static int insertNewUser(String name, int age, String address, int roleId,
      String password) {
    // check if inputs are valid
    if (validUserInfo(name, age, address, roleId, password)) {
      // establish connection to database and insert to table
      return DatabaseDriverHelper.driverInsertNewUser(name, age, address, roleId, password);
    }
    return DatabaseValidHelper.INVALID_ID;
  }

  /**
   * Connects and inserts a new entry into the Roles table. Returns the role ID number if insertion
   * into the Roles table was successful, otherwise returns -1.
   * 
   * @param role the new role to insert
   * @return the role ID if insertion was successful,-1 otherwise
   */
  public static int insertRole(String role) {
    // check if input is valid
    if (DatabaseValidHelper.isRoleEnum(role)) {
      // establish connection to database and insert to table
      return DatabaseDriverHelper.driverInsertRole(role);
    }
    return DatabaseValidHelper.INVALID_ID;
  }

  /**
   * Connects and inserts a new entry into the UserAccount table. Returns the UserAccount ID if
   * insertion was successful, and -1 otherwise.
   * 
   * @param userId the ID number of a user
   * @param accountId the ID number of an account
   * @return UserAccount ID if insertion was successful, -1 otherwise
   */
  public static int insertUserAccount(int userId, int accountId) {
    // check if user and account exist in the database
    if (DatabaseValidHelper.userExists(userId) && DatabaseValidHelper.accountExists(accountId)) {
      // check for uniqueness
      if (!DatabaseSelectHelper.getAccountIds(userId).contains(accountId)) {
        // establish connection to database and insert to table
        return DatabaseDriverHelper.driverInsertUserAccount(userId, accountId);
      }
    }
    return DatabaseValidHelper.INVALID_ID;
  }

  /**
   * Connects and inserts a new message into the database for user with user ID. Returns the message
   * ID of the new message if insertion was successful, otherwise returns -1.
   * 
   * @param userId a user ID number
   * @param message the message to leave for the user
   * @return the message ID if successful, -1 otherwise
   */
  public static int insertMessage(int userId, String message) {
    // check if user exists and message is valid
    if (DatabaseValidHelper.userExists(userId) && DatabaseValidHelper.validMessage(message)) {
     // insert message to database, get the message ID
      return DatabaseDriverHelper.driverInsertMessage(userId, message);
    }
    return DatabaseValidHelper.INVALID_ID;
  }

}
