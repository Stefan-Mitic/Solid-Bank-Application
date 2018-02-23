package com.bank.databasehelper;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.bank.database.android.DatabaseDriverA;
import java.math.BigDecimal;

public class DatabaseDriverHelper extends DatabaseDriverA {

  /**
   * An instance of the database.
   */
  private static DatabaseDriverHelper driver = null;

  /**
   * Creates a driver helper to open, read, or update a database.
   *
   * @param context to open or create the database
   */
  private DatabaseDriverHelper(Context context) {
    super(context);
  }

  /**
   * Connects to database.
   *
   * @return the database connection
   */
  public static DatabaseDriverHelper getDatabaseDriver(Context context) {
    driver = new DatabaseDriverHelper(context);
    return driver;
  }

  /**
   * Creates and connects to the database.
   *
   * @return the database connection
   */
  public static DatabaseDriverHelper createNewDatabase(Context context) {
    driver = new DatabaseDriverHelper(context);
    SQLiteDatabase db = driver.getReadableDatabase();
    db.close();
    return driver;
  }

  /* <!------------------------------ DRIVER INSERT METHODS ------------------------------> */

  /**
   * Writes a role to the database.
   *
   * @param role the name of the user role
   * @return the ID number of the role in the Roles table
   */
  static int driverInsertRole(String role) {
    return (int) driver.insertRole(role);
  }

  /**
   * Writes a new user to the database.
   *
   * @param name the name of the user
   * @param age the age of the user
   * @param address the address of the user
   * @param roleId the role ID number of the user
   * @param password the password of the user
   * @return the user ID number in the Users table
   */
  static int driverInsertNewUser(String name, int age, String address, int roleId,
      String password) {
    return (int) driver.insertNewUser(name, age, address, roleId, password);
  }

  /**
   * Writes an account type to the database.
   *
   * @param name the name of the account type
   * @param interestRate the interest rate of the account type
   * @return the account type ID number in the AccountTypes table
   */
  static int driverInsertAccountType(String name, BigDecimal interestRate) {
    return (int) driver.insertAccountType(name, interestRate);
  }

  /**
   * Writes an account to the database.
   *
   * @param name the name of the account
   * @param balance the initial balance of the account
   * @param type the account type
   * @return the account ID number in the Accounts table
   */
  static int driverInsertAccount(String name, BigDecimal balance, int type) {
    return (int) driver.insertAccount(name, balance, type);
  }

  /**
   * Associates a user to an account and writes to the database.
   *
   * @param userId the user ID number
   * @param accountId the account ID number
   * @return the ID number of the UserAccount entry in the UserAccounts table
   */
  static int driverInsertUserAccount(int userId, int accountId) {
    return (int) driver.insertUserAccount(userId, accountId);
  }

  /**
   * Writes a message to the database.
   *
   * @param userId the recipient user ID number
   * @param message the message content
   * @return the message ID number in the Messages table
   */
  static int driverInsertMessage(int userId, String message) {
    return (int) driver.insertMessage(userId, message);
  }

  /* <!------------------------------ DRIVER SELECT METHODS ------------------------------> */

  static Cursor driverGetRoles() {
    return driver.getRoles();
  }

  static String driverGetRole(int id) {
    return driver.getRole(id);
  }

  static int driverGetUserRole(int userId) {
    try {
      return driver.getUserRole(userId);
    } catch (CursorIndexOutOfBoundsException e) {
      return DatabaseValidHelper.INVALID_ID;
    }
  }

  static Cursor driverGetUsersDetails() {
    return driver.getUsersDetails();
  }

  static Cursor driverGetUserDetails(int userId) {
    return driver.getUserDetails(userId);
  }

  static String driverGetPassword(int userId) {
    return driver.getPassword(userId);
  }

  static Cursor driverGetAccountIds(int userId) {
    return driver.getAccountIds(userId);
  }

  static Cursor driverGetAccountDetails(int accountId) {
    return driver.getAccountDetails(accountId);
  }

  static BigDecimal driverGetBalance(int accountId) {
    return driver.getBalance(accountId);
  }

  static int driverGetAccountType(int accountId) {
    try {
      return driver.getAccountType(accountId);
    } catch (CursorIndexOutOfBoundsException e) {
      return DatabaseValidHelper.INVALID_ID;
    }
  }

  static String driverGetAccountTypeName(int accountTypeId) {
    return driver.getAccountTypeName(accountTypeId);
  }

  static Cursor driverGetAccountTypesId() {
    return driver.getAccountTypesId();
  }

  static BigDecimal driverGetInterestRate(int accountTypeId) {
    return driver.getInterestRate(accountTypeId);
  }

  static Cursor driverGetAllMessages(int userId) {
    return driver.getAllMessages(userId);
  }

  static String driverGetSpecificMessage(int messageId) {
    return driver.getSpecificMessage(messageId);
  }

  /* <!------------------------------ DRIVER UPDATE METHODS ------------------------------> */

  static boolean driverUpdateRoleName(String name, int id) {
    return driver.updateRoleName(name, id);
  }

  static boolean driverUpdateUserName(String name, int id) {
    return driver.updateUserName(name, id);
  }

  static boolean driverUpdateUserAge(int age, int id) {
    return driver.updateUserAge(age, id);
  }

  static boolean driverUpdateUserRole(int roleId, int id) {
    return driver.updateUserRole(roleId, id);
  }

  static boolean driverUpdateUserAddress(String address, int id) {
    return driver.updateUserAddress(address, id);
  }

  static boolean driverUpdateAccountName(String name, int id) {
    return driver.updateAccountName(name, id);
  }

  static boolean driverUpdateAccountBalance(BigDecimal balance, int id) {
    return driver.updateAccountBalance(balance, id);
  }

  static boolean driverUpdateAccountType(int typeId, int id) {
    return driver.updateAccountType(typeId, id);
  }

  static boolean driverUpdateAccountTypeName(String name, int id) {
    return driver.updateAccountTypeName(name, id);
  }

  static boolean driverUpdateAccountTypeInterestRate(BigDecimal interestRate, int id) {
    return driver.updateAccountTypeInterestRate(interestRate, id);
  }

  static boolean driverUpdateUserPassword(String password, int userId) {
    return driver.updateUserPassword(password, userId);
  }

  static boolean driverUpdateUserMessageState(int messageId) {
    return driver.updateUserMessageState(messageId);
  }

}
