package com.bank.databasehelper;

import com.bank.security.PasswordHelpers;
import java.math.BigDecimal;

/**
 * Helper methods for updating data to database tables. Starter code by Joe Bettridge.
 */
public class DatabaseUpdateHelper {

  /**
   * Connects to database and updates the name of the role with ID number id.
   * 
   * @param name the role's new name
   * @param id a user role ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateRoleName(String name, int id) {
    // check if inputs are valid
    if (DatabaseValidHelper.isRoleEnum(name) && DatabaseValidHelper.validRoleId(id)) {
      return DatabaseDriverHelper.driverUpdateRoleName(name, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the name of the user with ID number id.
   * 
   * @param name the user's new name
   * @param id a user ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateUserName(String name, int id) {
    // check if inputs are valid
    if (!name.isEmpty() && DatabaseValidHelper.userExists(id)) {
      return DatabaseDriverHelper.driverUpdateUserName(name, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the age of the user with ID number id.
   * 
   * @param age the user's new age
   * @param id a user ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateUserAge(int age, int id) {
    // check if inputs are valid
    if (age >= 0 && DatabaseValidHelper.userExists(id)) {
      return DatabaseDriverHelper.driverUpdateUserAge(age, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the user role ID number.
   * 
   * @param roleId the user role's new ID number
   * @param id a user's current ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateUserRole(int roleId, int id) {
    // check if inputs are valid
    if (DatabaseValidHelper.validId(id) && DatabaseValidHelper.validRoleId(roleId)) {
      return DatabaseDriverHelper.driverUpdateUserRole(roleId, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the address of the user with ID number id.
   * 
   * @param address the user's new address
   * @param id a user ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateUserAddress(String address, int id) {
    // check if inputs are valid
    if (DatabaseValidHelper.validAddress(address) && DatabaseValidHelper.userExists(id)) {
      return DatabaseDriverHelper.driverUpdateUserAddress(address, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the name of the account with ID number id.
   * 
   * @param name the account's new name
   * @param id an account ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateAccountName(String name, int id) {
    // check if inputs are valid
    if (!name.isEmpty() && DatabaseValidHelper.accountExists(id)) {
      return DatabaseDriverHelper.driverUpdateAccountName(name, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the balance of the account with ID number id.
   * 
   * @param balance the new balance the account will hold
   * @param id an account's ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateAccountBalance(BigDecimal balance, int id) {
    int typeId = DatabaseSelectHelper.getAccountType(id);
    // check if inputs are valid
    if (DatabaseValidHelper.validBalance(balance, typeId)
        && DatabaseValidHelper.accountExists(id)) {
      return DatabaseDriverHelper.driverUpdateAccountBalance(balance, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the account type ID number.
   * 
   * @param typeId the account type's new ID number
   * @param id the account's current ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateAccountType(int typeId, int id) {
    // check if inputs are valid
    if (DatabaseValidHelper.validId(id) && DatabaseValidHelper.validAccountTypeId(typeId)) {
      return DatabaseDriverHelper.driverUpdateAccountType(typeId, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the name of the account type with ID number id.
   * 
   * @param name the account type's new name
   * @param id an account type's ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateAccountTypeName(String name, int id) {
    // check if inputs are valid
    if (DatabaseValidHelper.isAccountTypeEnum(name) && DatabaseValidHelper.validAccountTypeId(id)) {
      return DatabaseDriverHelper.driverUpdateAccountTypeName(name, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the interest rate of the account type with ID number id.
   * 
   * @param interestRate the account type's new interest rate
   * @param id an account type's ID number
   * @return <code>true</code> if successful update, <code>false</code> otherwise
   */
  public static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id) {
    // check if inputs are valid
    if (DatabaseValidHelper.validInterestRate(interestRate)
        && DatabaseValidHelper.validAccountTypeId(id)) {
      return DatabaseDriverHelper.driverUpdateAccountTypeInterestRate(interestRate, id);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the user password. Returns <code>true</code> if the update was
   * successful.
   * 
   * @param password the new password to insert
   * @param userId a user ID number
   * @return <code>true</code> if password update was successful
   */
  public static boolean updateUserPassword(String password, int userId) {
    // check if inputs are valid
    if (DatabaseValidHelper.validPassword(password) && DatabaseValidHelper.userExists(userId)) {
      String hashPassword = PasswordHelpers.passwordHash(password);
      return DatabaseDriverHelper.driverUpdateUserPassword(hashPassword, userId);
    }
    // return false if inputs are invalid
    return false;
  }

  /**
   * Connects to database and updates the message to viewed. Returns <code>true</code> if update was
   * successful.
   * 
   * @param messageId the message ID number
   * @return <code>true</code> if message state update was successful
   */
  public static boolean updateUserMessageState(int messageId) {
    return DatabaseDriverHelper.driverUpdateUserMessageState(messageId);
  }

}
