package com.bank.databasehelper;

import com.bank.accounts.Account;
import com.bank.accounts.BalanceOwingAccount;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.generics.Roles;
import com.bank.users.Customer;
import com.bank.users.User;
import java.math.BigDecimal;
import java.util.List;

/**
 * Helper methods for checking if input to database is valid.
 */
public class DatabaseValidHelper {

  /**
   * Minimum or starting ID number.
   */
  public static final int MIN_ID = 1;

  /**
   * Invalid ID number to return for exceptions.
   */
  public static final int INVALID_ID = -1;

  /**
   * Minimum interest rate.
   */
  public static final BigDecimal MIN_INTEREST_RATE = BigDecimal.ZERO;

  /**
   * Maximum interest rate.
   */
  public static final BigDecimal MAX_INTEREST_RATE = BigDecimal.ONE;

  /**
   * Maximum address length.
   */
  public static final int MAX_ADDRESS_LENGTH = 100;

  /**
   * Maximum password length.
   */
  public static final int MAX_PASSWORD_LENGTH = 64;

  /**
   * Minimum password length.
   */
  public static final int MIN_PASSWORD_LENGTH = 4;

  /**
   * Maximum message length.
   */
  public static final int MAX_MESSAGE_LENGTH = 512;

  /**
   * Returns <code>true</code> if type is an element in AccountTypes.java.
   * 
   * @param type string to check if is an enum
   * @return <code>true</code> if type is an enum
   */
  public static boolean isAccountTypeEnum(String type) {
    boolean isEnum = false;
    try {
      // check if type matches one of the enums as a string
      forLoop: for (AccountTypes typeEnum : AccountTypes.values()) {
        isEnum = type.equalsIgnoreCase(typeEnum.toString());
        if (isEnum) {
          break forLoop;
        }
      }
    } catch (NullPointerException npe) {
      // return false if null was passed in
      isEnum = false;
    }
    return isEnum;
  }

  /**
   * Returns <code>true</code> if role is an element in Roles.java.
   * 
   * @param role string to check if is an enum
   * @return <code>true</code> if role is an enum
   */
  public static boolean isRoleEnum(String role) {
    boolean isEnum = false;
    try {
      // check if role matches one of the enums as a string
      forLoop: for (Roles roleEnum : Roles.values()) {
        isEnum = role.equalsIgnoreCase(roleEnum.toString());
        if (isEnum) {
          break forLoop;
        }
      }
    } catch (NullPointerException npe) {
      // return false if null was passed in
      isEnum = false;
    }
    return isEnum;
  }

  /**
   * Returns <code>true</code> if ID is a non-negative integer.
   * 
   * @param id an ID number
   * @return <code>true</code> if id is a non-negative integer
   */
  public static boolean validId(int id) {
    return id >= MIN_ID;
  }

  /**
   * Returns <code>true</code> if roleId is a valid ID existing in database.
   * 
   * @param roleId the role ID number to check for
   * @return <code>true</code> if roleId exists in Roles table
   */
  public static boolean validRoleId(int roleId) {
    List<Integer> roleIds = DatabaseSelectHelper.getRoles();
    return roleIds.contains(roleId);
  }

  /**
   * Returns <code>true</code> if accountTypeId is a valid ID existing in database.
   * 
   * @param accountTypeId the account type ID number to check for
   * @return <code>true</code> if accountTypeId exists in AccountTypes table
   */
  public static boolean validAccountTypeId(int accountTypeId) {
    List<Integer> typeIds = DatabaseSelectHelper.getAccountTypesIds();
    return typeIds.contains(accountTypeId);
  }

  /**
   * Returns <code>true</code> if balance is at two decimal places and is non-negative.
   * 
   * @param balance an account balance
   * @return <code>true</code> if balance is at two decimal places and is non-negative
   */
  public static boolean validBalance(BigDecimal balance) {
    boolean valid;
    try {
      // check if balance has at most two decimal places, is non-negative
      boolean twoDecimalPlaces = Math.max(0, balance.stripTrailingZeros().scale()) <= 2;
      boolean isNonNegative = balance.compareTo(BigDecimal.ZERO) >= 0;
      valid = twoDecimalPlaces && isNonNegative;
    } catch (NullPointerException npe) {
      // return false if null was passed in
      valid = false;
    }
    return valid;
  }
  
  /**
   * Valid Balance method: Special case for Balance Owing Accounts.
   * 
   * @param balance an account balance
   * @param typeId the account type ID
   * @return <code>true</code> if balance is at two decimal places and is non-negative
   */
  public static boolean validBalance(BigDecimal balance, int typeId) {
    boolean result;
    if (AccountTypesEnumMap.getAccountTypeName(typeId).equals("OWING") && balance.signum() == -1) {
      result = validBalance(balance.negate());
    } else {
      result = validBalance(balance);
    }
    return result;
  }

  /**
   * Returns <code>true</code> if interest rate is valid, between 0.0 and 1.0.
   * 
   * @param rate an interest rate
   * @return <code>true</code> if interest rate is between 0.0 and 1.0.
   */
  public static boolean validInterestRate(BigDecimal rate) {
    boolean valid;
    try {
      // check if interest rate is within range (1.0 > interestRate >= 0)
      boolean aboveMin = rate.compareTo(MIN_INTEREST_RATE) >= 0;
      boolean belowMax = rate.compareTo(MAX_INTEREST_RATE) <= 0;
      valid = aboveMin && belowMax;
    } catch (NullPointerException npe) {
      // return false if null was passed in
      valid = false;
    }
    return valid;
  }

  /**
   * Returns <code>true</code> if address is valid, between 1 and 100 characters in length.
   * 
   * @param address an address
   * @return <code>true</code> if address length is between 1 and 100 characters
   */
  public static boolean validAddress(String address) {
    boolean valid;
    try {
      // check if address has between 1 and 100 chars
      valid = !address.isEmpty() && address.length() <= MAX_ADDRESS_LENGTH;
    } catch (NullPointerException npe) {
      // return false if null was passed in
      valid = false;
    }
    return valid;
  }

  /**
   * Returns <code>true</code> if password is valid, between 4 and 64 characters in length.
   * 
   * @param password a password
   * @return <code>true</code> if password length is between 4 and 64 characters
   */
  public static boolean validPassword(String password) {
    boolean valid;
    try {
      // check if password has between 4 and 64 chars
      valid = password.length() >= MIN_PASSWORD_LENGTH;
      valid = valid && password.length() <= MAX_PASSWORD_LENGTH;
    } catch (NullPointerException npe) {
      // return false if null was passed in
      valid = false;
    }
    return valid;
  }

  /**
   * Returns <code>true</code> if message is valid, between 1 and 512 characters in length.
   * 
   * @param message a message
   * @return <code>true</code> if message length is between 1 and 512 characters
   */
  public static boolean validMessage(String message) {
    boolean valid = false;
    try {
      // check if message has between 1 and 512 chars
      valid = !message.isEmpty() && message.length() <= MAX_MESSAGE_LENGTH;
    } catch (NullPointerException npe) {
      // return false if null was passed in
      valid = false;
    }
    return valid;
  }

  /**
   * Returns <code>true</code> if the user with userId exists in the database.
   * 
   * @param userId a user ID number
   * @return <code>true</code> if user exists
   */
  public static boolean userExists(int userId) {
    return DatabaseSelectHelper.getUserRole(userId) != INVALID_ID;
  }

  /**
   * Returns <code>true</code> if the account with the accountId exists in the database.
   * 
   * @param accountId an account ID number
   * @return <code>true</code> if account exists
   */
  public static boolean accountExists(int accountId) {
    return DatabaseSelectHelper.getAccountType(accountId) != INVALID_ID;
  }

  /**
   * Returns <code>true</code> if the user with userId owns the account with accountId.
   * 
   * @param userId a user ID number
   * @param accountId an account ID number
   * @return <code>true</code> if user owns account
   */
  public static boolean userOwnsAccount(int userId, int accountId) {
    if (userId == INVALID_ID || accountId == INVALID_ID) {
      return false;
    }
    boolean result = false;
    // get user from database
    User user = DatabaseSelectHelper.getUserDetails(userId);
    // check if user is a customer
    if (user instanceof Customer) {
      // check if account ID belongs in user's list of account IDs
      List<Integer> accountIds = DatabaseSelectHelper.getAccountIds(userId);
      result = accountIds.contains(accountId);
    }
    return result;
  }
  
  /**
   * Returns <code>true</code> if the account is the type given.
   * 
   * @param type the account type given.
   * @param accountId an account ID number.
   * @return <code>true</code> if account is the type given, false otherwise.
   */
  public static boolean isAccountType(String type, int accountId) {
    int accountTypeId = DatabaseSelectHelper.getAccountType(accountId);
    if (AccountTypesEnumMap.getAccountTypeName(accountTypeId).equals(type)) {
      return true;
    }
    return false;
  }

  /**
   * Returns <code>true</code> if user with userId owns the owing account with accountId.
   * 
   * @param userId a user ID number.
   * @param accountId an account ID number.
   * @return <code>true</code> if user owns owing account, false otherwise.
   */
  public static boolean userOwnsOwingAccount(int userId, int accountId) {
    Account account = DatabaseSelectHelper.getAccountDetails(accountId);
    return userOwnsAccount(userId, accountId) && account instanceof BalanceOwingAccount;
  }

}
