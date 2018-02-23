package com.bank.generics;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

public class AccountTypesEnumMap {

  private static EnumMap<AccountTypes, Integer> enumMap = new EnumMap<>(AccountTypes.class);

  /**
   * Returns the id number correlated to the AccountType.
   * 
   * @param accountType is a type from the AccountType enum representing a specific AccountType
   * @return an int representing the AccountType id number
   */
  public static int getAccountTypeId(AccountTypes accountType) {
    if (enumMap.get(accountType) != null) {
      return enumMap.get(accountType);
    } else {
      return -2;
    }
  }

  /**
   * Given the id of an account type returns the name of the account type.
   * 
   * @param accountTypeId the id of the account type
   * @return the name of the account type
   */
  public static String getAccountTypeName(int accountTypeId) {
    for (AccountTypes currAccountType : enumMap.keySet()) {
      if (enumMap.get(currAccountType) == accountTypeId) {
        return currAccountType.toString();
      }
    }
    return null;
  }

  /**
   * Inserts a new accountType into the enum map as well as the database. If successful returns the
   * id of the Role, otherwise -1.
   * 
   * @param accountType is a String representing the accountType to be inserted
   * @param interestRate is a BigDecimal representing the interest rate on the new accountType
   */
  public static int insertAccountType(String accountType, BigDecimal interestRate) {
    AccountTypes accountTypeEnum = null;
    int accountTypeId = DatabaseInsertHelper.insertAccountType(accountType, interestRate);
    // Loops through all AccountTypes in the enum and checks if the AccountType to be inserted is
    // not already part of the enum map
    forLoop: for (AccountTypes currAccountType : AccountTypes.values()) {
      if (!enumMap.containsKey(currAccountType)
          && accountType.equalsIgnoreCase(currAccountType.toString())) {
        accountTypeEnum = currAccountType;
        break forLoop;
      }
    }
    // Checks if the accountType was properly inserted
    if (accountTypeEnum != null && accountTypeId != -1) {
      enumMap.put(accountTypeEnum, accountTypeId);
      return accountTypeId;
    } else {
      return -1;
    }
  }

  /**
   * Updates the enum map according to the accountTypes found in the database. Runs update to create
   * an EnumMap according to the data in the database.
   */
  public static void update() {
    List<Integer> accountTypesIds = DatabaseSelectHelper.getAccountTypesIds();
    for (int currAccountIds : accountTypesIds) {
      // Loops through all AccountTypes in the enum and compares to the ones in the database, adds
      // the
      // AccountTypes with their id numbers accordingly to the enum map
      forLoop: for (AccountTypes currAccountType : AccountTypes.values()) {
        if ((DatabaseSelectHelper.getAccountTypeName(currAccountIds)
            .equalsIgnoreCase(currAccountType.toString()))) {
          enumMap.put(currAccountType, currAccountIds);
          break forLoop;
        }
      }
    }
  }
}
