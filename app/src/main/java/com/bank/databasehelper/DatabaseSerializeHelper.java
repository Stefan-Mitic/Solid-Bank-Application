package com.bank.databasehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseDeserializeUserInfo;
import com.bank.databasehelper.DatabaseDriver;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.messages.Message;
import com.bank.users.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

public class DatabaseSerializeHelper extends DatabaseDeserializeUserInfo {

  /**
   * An instance of the database.
   */
  private static DatabaseSerializeHelper driver = null;

  /**
   * Creates a driver helper to open, read, or update a database.
   *
   * @param context to open or create the database
   */
  private DatabaseSerializeHelper(Context context) {
    super(context);
  }

  /**
   * Connects to database.
   *
   * @return the database connection
   */
  public static DatabaseSerializeHelper getDatabaseDriver(Context context) {
    driver = new DatabaseSerializeHelper(context);
    return driver;
  }

  /**
   * Creates and connects to the database.
   *
   * @return the database connection
   */
  public static DatabaseSerializeHelper updateNewDatabase(Context context) {
    driver = new DatabaseSerializeHelper(context);
    SQLiteDatabase db = driver.getReadableDatabase();
    driver.onUpgrade(db, 1, 2);
    db.close();
    return driver;
  }

  /**
   * Serializes the database data into a new file called "database_copy.ser".
   *
   * @return boolean true if it has completed, false otherwise
   */
  public static boolean serializeDatabase(Context context) {
    try {
      FileOutputStream file = context.openFileOutput("database_copy.ser", Context.MODE_PRIVATE);
      ObjectOutputStream output = new ObjectOutputStream(file);

      // Writes the roleId and role Names in the file
      for (Integer roleId : DatabaseSelectHelper.getRoles()) {
        output.writeObject(DatabaseSelectHelper.getRole(roleId));
      }

      // Writes the accountTypeIds and accountTypeNames in the file
      for (Integer accountTypeId : DatabaseSelectHelper.getAccountTypesIds()) {
        output.writeObject(DatabaseSelectHelper.getAccountTypeName(accountTypeId));
        output.writeObject(DatabaseSelectHelper.getInterestRate(accountTypeId));
      }

      // Writes each User into the file with all of their unique information and accounts
      for (User user : DatabaseSelectHelper.getAllUsers()) {
        output.writeObject(user);
        output.writeObject(DatabaseSelectHelper.getPassword(user.getId()));

        // Writes each Message correlated to the specified User
        for (Message message : DatabaseSelectHelper.getAllMessages(user.getId())) {
          output.writeObject(message);
        }

        // Writes each Account correlated to the specified User
        for (Integer accountId : DatabaseSelectHelper.getAccountIds(user.getId())) {
          Account account = DatabaseSelectHelper.getAccountDetails(accountId);
          output.writeObject(account);
        }

        // Used to stop loop in deserializing
        output.writeObject(null);
      }
      output.close();
      file.close();
      return true;
    } catch (Exception e) {
      // Throw an exception message
    }
    return false;
  }

  /**
   * Deserialize the data from "database_copy.ser" and places the data into the new database.
   *
   * @return boolean true if succeeds, otherwise false
   */
  public static boolean deserializeDatabase(Context context) {
    boolean result = deserializeDatabaseHelper(context);
    return result;
  }

  /**
   * Helper that performs the deserialization of the data form the specified path. Returns true if
   * succeeds, otherwise false.
   *
   * @return boolean true if succeeds, otherwise false
   */
  private static boolean deserializeDatabaseHelper(Context context) {
    context.deleteDatabase("bank.db");
    driver = updateNewDatabase(context);

    try {
      FileInputStream file = context.openFileInput("database_copy.ser");
      ObjectInputStream input = new ObjectInputStream(file);
      Object temp;

      // Runs loop and grabs all the info from the serialized file, checks each instance and adds
      // the object into the newly created database file
      int userId = -1;
      temp = input.readObject();
      while (temp != null) {
        if (temp instanceof Roles) {
          Roles rolesTemp = (Roles) temp;
          if (!containsRole(rolesTemp)) {
            System.out.println("Roles do not match those in the Enum Class");
            return false;
          } else {
            DatabaseInsertHelper.insertRole(rolesTemp.toString());
          }
        } else if (temp instanceof AccountTypes) {
          AccountTypes accountTypTemp = (AccountTypes) temp;
          if (!containsAccountType(accountTypTemp)) {
            System.out.println("AccountTypes do not match those in the Enum Class");
            return false;
          } else {
            DatabaseInsertHelper.insertAccountType(accountTypTemp.toString(),
                new BigDecimal((String) input.readObject()));
          }
        } else if (temp instanceof User) {
          User userTemp = (User) temp;
          String password = (String) input.readObject();
          userId = (int) driver.insertUserHashed(userTemp.getName(), userTemp.getAge(), userTemp.getAddress(),
              userTemp.getRoleId(), password);
        } else if (temp instanceof Account) {
          int accountId = DatabaseInsertHelper.insertAccount(((Account) temp).getName(),
              ((Account) temp).getBalance(), ((Account) temp).getType());
          // Insert the account corresponding to the user
          DatabaseInsertHelper.insertUserAccount(userId, accountId);
        } else if (temp instanceof Message) {
          DatabaseInsertHelper.insertMessage(userId, ((Message) temp).getMessage());
        }

        temp = input.readObject();
      }
      input.close();
      file.close();
      driver.close();
      return true;
    } catch (IOException | ClassNotFoundException e) {
      // Throw an exception
      e.printStackTrace();
    }
    return false;
  }

//  /**
//   * Inserts a User into the database with a hashed password.
//   *
//   * @param name           is the name of the User
//   * @param age            is the age of the User
//   * @param address        is the address of the User
//   * @param roleId         is the role id of the User
//   * @param hashedPassword is the hashed password of the User
//   * @return long is a user Id
//   */
//  protected long insertUserHashed(String name, int age, String address, int roleId,
//                                  String hashedPassword) {
//    return driver.insertUserHashed(name, age, address, roleId,
//        hashedPassword);
//  }

  /**
   * Checks if the specified Role is in the Enum.
   *
   * @param role is a Role
   * @return boolean true if the role is in the Enum, otherwise false
   */
  private static boolean containsRole(Roles role) {
    for (Roles currRole : Roles.values()) {
      if (currRole.equals(role)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the specified AccountType is in the Enum.
   *
   * @param accountType is an AccountType
   * @return boolean true if the accountType is in the Enum, otherwise false
   */
  private static boolean containsAccountType(AccountTypes accountType) {
    for (AccountTypes currAccountType : AccountTypes.values()) {
      if (currAccountType.equals(accountType)) {
        return true;
      }
    }
    return false;
  }

}
