package com.bank.databasehelper;

import android.database.Cursor;
import com.bank.accounts.Account;
import com.bank.accounts.AccountBuilder;
import com.bank.accounts.SolidAccountBuilder;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.generics.Roles;
import com.bank.generics.RolesEnumMap;
import com.bank.messages.Message;
import com.bank.users.SolidUserBuilder;
import com.bank.users.User;
import com.bank.users.UserBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for obtaining data from database tables.
 */
public class DatabaseSelectHelper {

  /**
   * Connects to database and returns the role name with the ID number id.
   * 
   * @param roleId the role's ID number
   * @return the role name
   */
  public static String getRole(int roleId) {
    // declare role to return
    String role = "";
    // check if id is valid
    if (DatabaseValidHelper.validId(roleId)) {
      // get info from database
      role = DatabaseDriverHelper.driverGetRole(roleId);
    }
    // return role of user with ID id
    return role;
  }

  /**
   * Connects to database and returns the hash password for user with ID number userId.
   * 
   * @param userId the user's ID number
   * @return the hash password of the user
   */
  public static String getPassword(int userId) {
    // declare hash password to return
    String hashPassword = "";
    // check if id is valid
    if (DatabaseValidHelper.userExists(userId)) {
      // get info from database
      hashPassword = DatabaseDriverHelper.driverGetPassword(userId);
    }
    // return hash password
    return hashPassword;
  }

  /**
   * Connects to database and returns a user with the user ID number userId from the Users table.
   * Returns <code>null</code> if userId is invalid.
   * 
   * @param userId a user ID number
   * @return the user with ID number userId, <code>null</code> if invalid userId
   */
  public static User getUserDetails(int userId) {
    // declare user to return
    User user = null;
    // check if user ID is valid
    if (DatabaseValidHelper.userExists(userId)) {
      // declare variables to hold user details
      int roleId = DatabaseValidHelper.INVALID_ID;
      String name = "";
      int age = 0;
      String address = "";
      // get info from database
      Cursor cursor = DatabaseDriverHelper.driverGetUserDetails(userId);
      if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
        do {
          roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));
          name = cursor.getString(cursor.getColumnIndex("NAME"));
          age = cursor.getInt(cursor.getColumnIndex("AGE"));
          address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
        } while (cursor.moveToNext());
        cursor.close();
        // create user object based on roleId
        String role = getRole(roleId);
        UserBuilder builder = new SolidUserBuilder(role);
        user = builder.setId(userId)
                .setName(name)
                .setAge(age)
                .setAddress(address)
                .buildUser();
      }
    }
    // return user object
    return user;
  }

  /**
   * Connects to database and returns a list of account ID numbers held by user with the user ID
   * number userId. Returns <code>null</code> if userId is invalid.
   * 
   * @param userId the ID number of the user
   * @return a list of account ID numbers held by the user, <code>null</code> if invalid userId
   */
  public static List<Integer> getAccountIds(int userId) {
    // list of account IDs to return
    List<Integer> accountIds = new ArrayList<>();
    // check if user ID is valid
    if (DatabaseValidHelper.userExists(userId)) {
      // get data from database and build list
      Cursor cursor = DatabaseDriverHelper.driverGetAccountIds(userId);
      if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
        do {
          accountIds.add(cursor.getInt(cursor.getColumnIndex("ACCOUNTID")));
        } while (cursor.moveToNext());
        cursor.close();
      }
    }
    // return a list of account IDs
    return accountIds;
  }

  /**
   * Connects to database, obtains and returns an account with ID number accountId from the Accounts
   * table. Returns <code>null</code> if accountId is invalid.
   * 
   * @param accountId an account ID number
   * @return the account with the ID number accountId, <code>null</code> if invalid accountId
   */
  public static Account getAccountDetails(int accountId) {
    // declare account to return
    Account account = null;
    // check if account ID is valid
    if (DatabaseValidHelper.accountExists(accountId)) {
      // declare variables to hold account data
      String name = "";
      BigDecimal balance = null;
      int accountType = DatabaseValidHelper.INVALID_ID;
      // get data from database
      Cursor cursor = DatabaseDriverHelper.driverGetAccountDetails(accountId);
      if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
        do {
          name = cursor.getString(cursor.getColumnIndex("NAME"));
          balance = new BigDecimal(cursor.getString(cursor.getColumnIndex("BALANCE")));
          accountType = cursor.getInt(cursor.getColumnIndex("TYPE"));
        } while (cursor.moveToNext());
        cursor.close();
        // create account object based on accountTypeId
        String typeName = getAccountTypeName(accountType);
        AccountBuilder builder = new SolidAccountBuilder(typeName);
        account = builder.setId(accountId)
                .setName(name)
                .setBalance(balance)
                .buildAccount();
      }
    }
    // return account object
    return account;
  }

  /**
   * Connects to database and returns the current total balance that the account with ID number
   * accountId holds. Returns <code>null</code> if accountId is invalid.
   * 
   * @param accountId an account ID number
   * @return the balance of account with ID accountId, <code>null</code> if invalid accountId
   */
  public static BigDecimal getBalance(int accountId) {
    // declare balance to return
    BigDecimal balance = null;
    // check if account ID is valid
    if (DatabaseValidHelper.accountExists(accountId)) {
      // get data from database
      balance = DatabaseDriverHelper.driverGetBalance(accountId);
    }
    // return balance
    return balance;
  }

  /**
   * Connects to database and returns the interest rate of the given account type.
   * 
   * @param accountType an account type number
   * @return the interest rate of the account type
   */
  public static BigDecimal getInterestRate(int accountType) {
    // declare interest rate to return
    BigDecimal interestRate = null;
    // check if account type ID is valid
    if (DatabaseValidHelper.validAccountTypeId(accountType)) {
      // get data from database
      interestRate = DatabaseDriverHelper.driverGetInterestRate(accountType);
    }
    // return interest rate
    return interestRate;
  }

  /**
   * Connects to database and returns a list of all account type ID numbers stored in the database.
   * 
   * @return a list of all account type IDs
   */
  public static List<Integer> getAccountTypesIds() {
    // declare list to return
    List<Integer> ids = new ArrayList<>();
    // build list of account type IDs
    Cursor cursor = DatabaseDriverHelper.driverGetAccountTypesId();
    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        ids.add(cursor.getInt(cursor.getColumnIndex("ID")));
      } while (cursor.moveToNext());
      cursor.close();
    }
    // return a list of account type IDs
    return ids;
  }

  /**
   * Connects to database and returns the account type name from the given account type ID number.
   * 
   * @param accountTypeId an account type ID number
   * @return the name of the account type
   */
  public static String getAccountTypeName(int accountTypeId) {
    // declare name to return
    String accountTypeName = "";
    // check if input is valid
    if (DatabaseValidHelper.validAccountTypeId(accountTypeId)) {
      // get data from database
      accountTypeName = DatabaseDriverHelper.driverGetAccountTypeName(accountTypeId);
    }
    // return account type name
    return accountTypeName;
  }

  /**
   * Connects to database and returns a list of role IDs from the Roles table.
   * 
   * @return a list of role ID numbers
   */
  public static List<Integer> getRoles() {
    // declare list to return
    List<Integer> ids = new ArrayList<>();
    // build list of role IDs
    Cursor cursor = DatabaseDriverHelper.driverGetRoles();
    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        ids.add(cursor.getInt(cursor.getColumnIndex("ID")));
      } while (cursor.moveToNext());
      cursor.close();
    }
    // return a list of role IDs
    return ids;
  }

  /**
   * Connects to database and returns the account type number of the account with ID accountId.
   * 
   * @param accountId an account ID number
   * @return the account type number of the account with ID accountId
   */
  public static int getAccountType(int accountId) {
    return DatabaseDriverHelper.driverGetAccountType(accountId);
  }

  /**
   * Connects to database and returns the user role number of the user with ID userId.
   * 
   * @param userId a user ID number
   * @return the user role number of user with ID userId
   */
  public static int getUserRole(int userId) {
    return DatabaseDriverHelper.driverGetUserRole(userId);
  }

  /**
   * Connects to database and returns a list of message IDs for the user of userId.
   * 
   * @param userId a user ID number
   * @return a list of message IDs for the user
   */
  public static List<Integer> getAllMessageIds(int userId) {
    // declare list of message IDs to return
    List<Integer> messageIds = new ArrayList<>();
    // build list of account type IDs
    Cursor cursor = DatabaseDriverHelper.driverGetAllMessages(userId);
    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        messageIds.add(cursor.getInt(cursor.getColumnIndex("ID")));
      } while (cursor.moveToNext());
      cursor.close();
    }
    // return list of message IDs
    return messageIds;
  }

  /**
   * Connects to database and returns a list of message objects for the user of userId.
   * 
   * @param userId a user ID number
   * @return a list of message objects for the user
   */
  public static List<Message> getAllMessages(int userId) {
    // declare list of message objects to return
    List<Message> messages = new ArrayList<>();
    // build list of account type IDs
    Cursor cursor = DatabaseDriverHelper.driverGetAllMessages(userId);
    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        int msgId = cursor.getInt(cursor.getColumnIndex("ID"));
        String msg = cursor.getString(cursor.getColumnIndex("MESSAGE"));
        boolean viewed = cursor.getInt(cursor.getColumnIndex("VIEWED")) == 1;
        Message msgObject = new Message(msgId, userId, msg, viewed);
        messages.add(msgObject);
      } while (cursor.moveToNext());
      cursor.close();
    }
    // return list of message objects
    return messages;
  }

  /**
   * Connects to database and returns the message with the respective message ID. Returns
   * <code>null</code> if message ID is invalid.
   * 
   * @param messageId a message ID number
   * @return the message denoted by messageId
   */
  public static String getSpecificMessage(int messageId) {
    return DatabaseDriverHelper.driverGetSpecificMessage(messageId);
  }

  /**
   * Returns a list of all user IDs in the database
   *
   * @return a list of all user IDs
   */
  public static List<Integer> getAllUserIds() {
    // clear list of users to return
    List<Integer> allUserIds = new ArrayList<>();
    // get cursor from database
    Cursor cursor = DatabaseDriverHelper.driverGetUsersDetails();
    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        // get and add user Id
        int userId = cursor.getInt(cursor.getColumnIndex("ID"));
        allUserIds.add(userId);
      } while (cursor.moveToNext());
    }
    return allUserIds;
  }

  /**
   * Returns a list of all users in the database.
   * 
   * @return a list of all users
   */
  public static List<User> getAllUsers() {
    // clear list of users to return
    List<User> allUsers = new ArrayList<>();
    // get cursor from database
    Cursor cursor = DatabaseDriverHelper.driverGetUsersDetails();
    if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
      do {
        // get user details
        int userId = cursor.getInt(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        int age = cursor.getInt(cursor.getColumnIndex("AGE"));
        String address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
        int roleId = cursor.getInt(cursor.getColumnIndex("ROLEID"));
        String role = DatabaseSelectHelper.getRole(roleId);
        // build the user and add to list
        UserBuilder builder = new SolidUserBuilder(role);
        User user = builder.setId(userId)
                            .setName(name)
                            .setAge(age)
                            .setAddress(address)
                            .buildUser();
        allUsers.add(user);
      } while (cursor.moveToNext());
    }
    return allUsers;
  }

  /**
   * Returns a list of users of the given role.
   * 
   * @param role the users of the role to return
   * @return a list of users of the inputed role
   */
  public static List<User> getAllUsers(Roles role) {
    List<User> allUsers = new ArrayList<>();
    int roleId = RolesEnumMap.getRoleId(role);
    int userId = DatabaseValidHelper.MIN_ID;
    // continue looping as long as there's a user with id existing in the database
    while (DatabaseValidHelper.userExists(userId)) {
      // check that the role matches the given and add to array list
      if (getUserRole(userId) == roleId) {
        allUsers.add(getUserDetails(userId));
      }
      userId++;
    }
    return allUsers;
  }

  /**
   * Returns a list of all accounts in the database.
   *
   * @return a List of all accounts of that type
   */
  public static List<Account> getAllAccounts() {
    List<Account> allAccounts = new ArrayList<>();
    int accountId = DatabaseValidHelper.MIN_ID;
    // all ids are incremental, so continue by steps of 1 until
    // there are no such accounts in the database
    while (DatabaseValidHelper.accountExists(accountId)) {
      // check if the account type matches the account
      allAccounts.add(getAccountDetails(accountId++));
    }
    return allAccounts;
  }

  /**
   * Returns a list of all accounts of the specified type.
   * 
   * @param accountType the Account type
   * @return a List of all accounts of that type
   */
  public static List<Account> getAllAccounts(AccountTypes accountType) {
    List<Account> allAccounts = new ArrayList<>();
    int typeId = AccountTypesEnumMap.getAccountTypeId(accountType);
    int accountId = DatabaseValidHelper.MIN_ID;
    // all ids are incremental, so continue by steps of 1 until
    // there are no such accounts in the database
    while (DatabaseValidHelper.accountExists(accountId)) {
      // check if the account type matches the account
      if (getAccountType(accountId) == typeId) {
        allAccounts.add(getAccountDetails(accountId));
      }
      accountId++;
    }
    return allAccounts;
  }

}
