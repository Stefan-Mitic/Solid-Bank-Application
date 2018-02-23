package com.bank.terminals;

import android.os.Parcel;
import android.os.Parcelable;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.generics.Roles;
import com.bank.generics.RolesEnumMap;
import com.bank.messages.Message;
import com.bank.messages.MessageHelpers;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SolidTellerTerminal implements TellerTerminal {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = -3529590982878849736L;

  /**
   * The current teller using this terminal.
   */
  private Teller currentTeller = null;

  /**
   * The ID number of the current customer the current teller oversees.
   */
  private int customerId = DatabaseValidHelper.INVALID_ID;

  /**
   * Holds the current automated teller machine the teller is using.
   */
  private AutoTellerMachine currentAtm = null;

  /**
   * This constructor is used for SolidAdminTerminal (DESIGN CHOICE!).
   */
  public SolidTellerTerminal() {
    /* empty terminal */
  }

  public SolidTellerTerminal(int userId, String password) {
    // teller is assumed authenticated
    currentTeller = (Teller) DatabaseSelectHelper.getUserDetails(userId);
  }

  @Override
  public boolean setCustomer(int customerId) {
    boolean success;
    User selectCustomer = DatabaseSelectHelper.getUserDetails(customerId);
    if (success = selectCustomer instanceof Customer) {
      this.customerId = customerId;
    }
    return success;
  }

  @Override
  public boolean authenticateCustomer(String password) {
//    // if there was already an ATM
//    if (currentAtm != null) {
//      success = currentAtm.authenticate(customerId, password);
//    } else {
//      this.currentAtm = new SolidAutoTellerMachine(customerId, password);
//      success = currentAtm.isAuthenticated();
//    }
    this.currentAtm = new SolidAutoTellerMachine(customerId, password);
    return currentAtm != null;
  }

  @Override
  public BigDecimal checkBalance(int accountId)
          throws DoesNotOwnException {
    BigDecimal balance = null;
    if (currentAtm != null) {
      balance = currentAtm.checkBalance(accountId);
    }
    return balance;
  }

  @Override
  public BigDecimal checkBalance() {
    BigDecimal totalBalance = BigDecimal.ZERO;
    if (currentAtm != null) {
      for (Account account : currentAtm.listAccounts()) {
        totalBalance = totalBalance.add(account.getBalance());
      }
    }
    return totalBalance;
  }

  @Override
  public boolean makeDeposit(int accountId, BigDecimal amount) throws
          IllegalAmountException, DoesNotOwnException, ConnectionFailedException {
    boolean success = false;
    if (currentAtm != null) {
      success = currentAtm.makeDeposit(accountId, amount);
    }
    return success;
  }

  @Override
  public boolean makeWithdrawal(int accountId, BigDecimal amount)
          throws InsufficientFundsException, IllegalAmountException,
          DoesNotOwnException, ConnectionFailedException {

    boolean success = false;
    if (currentAtm != null) {
      if (DatabaseValidHelper.isAccountType("OWING", accountId)) {
        throw new InsufficientFundsException(
                "Add loans to an Balance Owing Account using the Add Loans option.");
      } else {
        success = currentAtm.makeWithdrawal(accountId, amount);
      }
    }
    return success;
  }

  @Override
  public List<Account> listAccounts() {
    List<Account> accounts = new ArrayList<>();
    if (currentAtm != null) {
      accounts = currentAtm.listAccounts();
    }
    return accounts;
  }

  @Override
  public int makeNewUser(String name, int age, String address, String password) {
    // get role ID number of customer from database
    int roleId = RolesEnumMap.getRoleId(Roles.CUSTOMER);
    // write new customer into the database
    int userId = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
    if (userId != DatabaseValidHelper.INVALID_ID) {
      deAuthenticateCustomer();
      setCustomer(userId);
      authenticateCustomer(password);
    }
    // return the user ID of the newly written user
    return userId;
  }

  @Override
  public int makeNewAccount(String name, BigDecimal balance, int type) {
    if (AccountTypesEnumMap.getAccountTypeName(type).equals("OWING")) {
      balance = balance.negate();
    }
    int accountId = DatabaseInsertHelper.insertAccount(name, balance, type);
    DatabaseInsertHelper.insertUserAccount(customerId, accountId);
    // return the account ID if account creation was successful
    return accountId;
  }

  @Override
  public BigDecimal giveInterest(int accountId) throws
          DoesNotOwnException {
    Account account = DatabaseSelectHelper.getAccountDetails(accountId);
    if (!DatabaseValidHelper.userOwnsAccount(customerId, accountId)) {
      throw new DoesNotOwnException(DOES_NOT_OWN_ACCOUNT_MSG);
    }
    // set interest rate and add to account
    account.findAndSetInterestRate();
    BigDecimal interest = account.addInterest();
    // send system message to customer
    MessageHelpers.notifyInterest(customerId, account.getId(), interest);
    return interest;
  }

  @Override
  public BigDecimal giveInterest() {
    BigDecimal totalInterest = BigDecimal.ZERO;
    List<Integer> accountIds = DatabaseSelectHelper.getAccountIds(customerId);
    // set interest rates and add to each account
    for (int accountId : accountIds) {
      Account account = DatabaseSelectHelper.getAccountDetails(accountId);
      account.findAndSetInterestRate();
      totalInterest = totalInterest.add(account.addInterest());
    }
    // send system message to customer
    MessageHelpers.notifyInterest(customerId, totalInterest);
    return totalInterest;
  }

  @Override
  public boolean deAuthenticateCustomer() {
    if (currentAtm == null) {
      return false;
    }
    customerId = DatabaseValidHelper.INVALID_ID;
    currentAtm.deAuthenticate();
    currentAtm = null;
    return true;
  }

  @Override
  public void deAuthenticate() {
    currentTeller = null;
  }

  @Override
  public List<Message> listMessages() {
    int tellerId = currentTeller.getId();
    return DatabaseSelectHelper.getAllMessages(tellerId);
  }

  @Override
  public List<Integer> listMessageIds() {
    currentTeller.findAndUpdateMessageIds();
    return currentTeller.getMessageIds();
  }

  @Override
  public List<Integer> listCustomerMessageIds() {
    List<Integer> customerMessageIds = new ArrayList<>();
    if (currentAtm != null) {
      customerMessageIds = currentAtm.listMessageIds();
    }
    return customerMessageIds;
  }

  @Override
  public int leaveMessage(int userId, String message) {
    int messageId = DatabaseValidHelper.INVALID_ID;
    User selectCustomer = DatabaseSelectHelper.getUserDetails(userId);
    if (selectCustomer instanceof Customer) {
      messageId = DatabaseInsertHelper.insertMessage(userId, message);
    }
    return messageId;
  }

  @Override
  public int leaveMessage(String message) {
    int messageId = DatabaseInsertHelper.insertMessage(customerId, message);
    // if the messageId is invalid, then it means customer was not authenticated
    return messageId;
  }

  @Override
  public String viewMessage(int messageId) throws DoesNotOwnException {
    String message = null;
    List<Integer> allMessageIds = currentTeller.getMessageIds();
    if (allMessageIds.contains(messageId)) {
      message = DatabaseSelectHelper.getSpecificMessage(messageId);
      DatabaseUpdateHelper.updateUserMessageState(messageId);
    } else {
      if (currentAtm != null) {
        // view customer message and update view status
        message = currentAtm.viewMessage(messageId);
      }
    }
    return message;
  }

  @Override
  public int getCustomerId() {
    return this.customerId;
  }

  @Override
  public int checkAccount(int accountId, BigDecimal balance) {
    return currentAtm.checkAccount(accountId, balance);
  }

  @Override
  public boolean addLoans(int accountId, BigDecimal amount) throws
          ConnectionFailedException, IllegalAmountException, DoesNotOwnException {
    boolean success;
    if (DatabaseValidHelper.userOwnsOwingAccount(customerId, accountId)) {
      // check if amount is positive
      if (amount.compareTo(BigDecimal.ZERO) > 0) {
        amount = amount.negate();
        // get current account balance from database, add deposit
        Account account = DatabaseSelectHelper.getAccountDetails(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        // set and update new balance
        if (success = DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId)) {
          account.setBalance(newBalance);
        } else {
          // throw exception if connection and update to database failed
          throw new ConnectionFailedException(CONNECTION_FAILED_MSG);
        }
      } else {
        // throw exception if amount is not positive
        throw new IllegalAmountException(ILLEGAL_AMOUNT_MSG);
      }
    } else {
      // throw exception if user does not own account
      throw new DoesNotOwnException("this is not a balance owing account owned by the user");
    }
    return success;
  }

  @Override
  public boolean updateUserName(String name, int userId, String userPassword) {
    // get customer from database
    User selectCustomer = DatabaseSelectHelper.getUserDetails(userId);
    if (selectCustomer instanceof Customer) {
      if (selectCustomer.authenticate(userPassword)) {
        return DatabaseUpdateHelper.updateUserName(name, userId);
      }
    }
    return false;
  }

  @Override
  public boolean updateUserAge(int age, int userId, String userPassword) {
    // get customer from database
    User selectCustomer = DatabaseSelectHelper.getUserDetails(userId);
    if (selectCustomer instanceof Customer) {
      if (selectCustomer.authenticate(userPassword)) {
        return DatabaseUpdateHelper.updateUserAge(age, userId);
      }
    }
    return false;
  }

  @Override
  public boolean updateUserAddress(String address, int userId, String userPassword) {
    // get customer from database
    User selectCustomer = DatabaseSelectHelper.getUserDetails(userId);
    if (selectCustomer instanceof Customer) {
      if (selectCustomer.authenticate(userPassword)) {
        return DatabaseUpdateHelper.updateUserAddress(address, userId);
      }
    }
    return false;
  }

  @Override
  public boolean updateUserPassword(String password, int userId, String userPassword) {
    // get customer from database
    User selectCustomer = DatabaseSelectHelper.getUserDetails(userId);
    if (selectCustomer instanceof Customer) {
      if (selectCustomer.authenticate(userPassword)) {
        return DatabaseUpdateHelper.updateUserPassword(password, userId);
      }
    }
    return false;
  }

  @Override
  public boolean createJointAccount(int accountId, int userId) {
    User user = DatabaseSelectHelper.getUserDetails(userId);
    if (user instanceof Customer) {
      int userAccountId = DatabaseInsertHelper.insertUserAccount(userId, accountId);
      if (userAccountId != DatabaseValidHelper.INVALID_ID) {
        MessageHelpers.notifyJointAccount(userId, accountId);
        return true;
      }
    }
    return false;
  }

}
