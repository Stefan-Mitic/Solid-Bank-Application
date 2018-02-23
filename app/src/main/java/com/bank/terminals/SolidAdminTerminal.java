package com.bank.terminals;

import android.os.Parcel;
import android.os.Parcelable;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.generics.Roles;
import com.bank.generics.RolesEnumMap;
import com.bank.messages.Message;
import com.bank.users.Admin;
import com.bank.users.Teller;
import com.bank.users.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SolidAdminTerminal extends SolidTellerTerminal implements AdminTerminal {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = 3118532981798775128L;

  /**
   * The current admin using this terminal.
   */
  private Admin currentAdmin = null;

  /**
   * Creates an admin given a admin ID number and a password to authenticate.
   *
   * @param adminId  an admin ID number
   * @param password the password of the admin to authenticate
   */
  public SolidAdminTerminal(int adminId, String password) {
    super();
    deAuthenticate();
    currentAdmin = (Admin) DatabaseSelectHelper.getUserDetails(adminId);
    // authenticate(adminId, password);
  }

  @Override
  public List<User> listUsers(Roles role) {
    List<User> allUsers = new ArrayList<>();
    int roleId = RolesEnumMap.getRoleId(role);
    int userId = DatabaseValidHelper.MIN_ID;
    // continue looping as long as there's a user with id existing in the database
    while (DatabaseValidHelper.userExists(userId)) {
      // check that the role matches the given and add to array list
      if (DatabaseSelectHelper.getUserRole(userId) == roleId) {
        allUsers.add(DatabaseSelectHelper.getUserDetails(userId));
      }
      userId++;
    }
    return allUsers;
  }

  @Override
  public int createNewUser(String name, int age, String address, String password, Roles role) {
    int roleId = RolesEnumMap.getRoleId(role);
    return DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
  }

  @Override
  public boolean updateInterestRate(BigDecimal newRate, int accountType)
          throws IllegalAmountException, ConnectionFailedException {
    return DatabaseUpdateHelper.updateAccountTypeInterestRate(newRate, accountType);
  }

  @Override
  public boolean promoteTeller(int tellerId) {
    boolean success = false;
    User selectTeller = DatabaseSelectHelper.getUserDetails(tellerId);
    if (selectTeller instanceof Teller) {
      int adminRoleId = RolesEnumMap.getRoleId(Roles.ADMIN);
      success = DatabaseUpdateHelper.updateUserRole(adminRoleId, selectTeller.getId());
    }
    return success;
  }

  @Override
  public BigDecimal getBankTotal() {
    BigDecimal bankTotal = BigDecimal.ZERO;
    int accountId = DatabaseValidHelper.MIN_ID;
    // continue looping as long as there's a account with id existing in the database
    while (DatabaseValidHelper.accountExists(accountId)) {
      BigDecimal balance = DatabaseSelectHelper.getBalance(accountId++);
      bankTotal = bankTotal.add(balance);
    }
    return bankTotal;
  }

  @Override
  public void deAuthenticate() {
    currentAdmin = null;
  }

  @Override
  public void serializeDatabase() {
    // TODO Auto-generated method stub
  }

  @Override
  public List<Message> listMessages() {
    int adminId = currentAdmin.getId();
    return DatabaseSelectHelper.getAllMessages(adminId);
  }

  @Override
  public List<Integer> listMessageIds() {
    currentAdmin.findAndUpdateMessageIds();
    return currentAdmin.getMessageIds();
  }

  @Override
  public String viewMessage(int messageId) throws DoesNotOwnException {
    List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(currentAdmin.getId());
    if (!messageIds.contains(messageId)) {
      throw new DoesNotOwnException(DOES_NOT_OWN_MESSAGE_MSG);
    }
    // get the message and update its view status
    String message = DatabaseSelectHelper.getSpecificMessage(messageId);
    DatabaseUpdateHelper.updateUserMessageState(messageId);
    return message;
  }

  @Override
  public int leaveMessage(int userId, String message) {
    return DatabaseInsertHelper.insertMessage(userId, message);
  }

  @Override
  public String peekMessage(int messageId) {
    return DatabaseSelectHelper.getSpecificMessage(messageId);
  }

}
