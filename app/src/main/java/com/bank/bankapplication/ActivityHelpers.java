package com.bank.bankapplication;

import android.support.v7.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods to use in activities.
 */
class ActivityHelpers {

  /**
   * Returns amount in a currency string format.
   *
   * @param amount a BigDecimal amount
   * @return the amount in currency
   */
  static String toCurrency(BigDecimal amount) {
    String currency = "";
    if (amount.signum() == -1) {
      currency += "-";
    }
    currency += "$" + amount.abs();
    return currency;
  }

  /**
   * Converts a list of user objects to a list of user info strings.
   *
   * @param users a list of user objects
   * @return a list of user info strings
   */
  static ArrayList<String> userListToStringList(List<User> users) {
    ArrayList<String> userInfoList = new ArrayList<>();
    for (User user : users) {
      String userInfo = String.format(Locale.CANADA, "ID: %d\nName: %s\nAge: %d\nAddress: %s",
              user.getId(), user.getName(), user.getAge(), user.getAddress());
      userInfoList.add(userInfo);
    }
    return userInfoList;
  }

  /**
   * Converts a list of account objects to a list of account info strings.
   *
   * @param accounts a list of account objects
   * @return a list of account info strings
   */
  static ArrayList<String> accountListToStringList(List<Account> accounts) {
    ArrayList<String> accountInfoList = new ArrayList<>();
    for (Account account : accounts) {
      String typeName = DatabaseSelectHelper.getAccountTypeName(account.getType());
      String accountInfo = String.format(Locale.CANADA, "%d - %s\n%s\n%s",
              account.getId(), account.getName(), account.getBalance(), typeName);
      accountInfoList.add(accountInfo);
    }
    return accountInfoList;
  }

}
