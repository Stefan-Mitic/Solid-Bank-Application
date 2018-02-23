package com.bank.terminals;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.generics.Roles;
import com.bank.users.User;
import java.math.BigDecimal;
import java.util.List;

/**
 * A terminal console used by a bank administrator. An admin can do anything a teller does, with
 * extra administrative abilities.
 */
public interface AdminTerminal extends TellerTerminal {

  /**
   * Returns a list of a users of the given role enum.
   * 
   * @param role the role of the users to return
   * @return a list of users of the given role
   */
  List<User> listUsers(Roles role);

  /**
   * Writes a new user to the database and returns their generated user ID number.
   * 
   * @param name the name of the user
   * @param age the age of the user
   * @param address the address of the user
   * @param password the password of the user
   * @param role the role of the user
   * @return the user's ID number, -1 if unsuccessful creation
   */
  int createNewUser(String name, int age, String address, String password, Roles role);

  /**
   * Updates the interest rate for the given account type. Returns true if successful update.
   * 
   * @param interestRate a new interest rate for accountType
   * @param accountType an account type ID number
   * @return true if interest rate update was successful

   * @throws IllegalAmountException if interestRate is not valid
   * @throws ConnectionFailedException if update to database failed
   */
  boolean updateInterestRate(BigDecimal interestRate, int accountType)
      throws IllegalAmountException, ConnectionFailedException;

  /**
   * Promotes the teller to an admin. Returns true if promotion was successful.
   * 
   * @param tellerId a teller ID number
   * @return true if promotion of teller to admin was successful
   */
  boolean promoteTeller(int tellerId);

  /**
   * Returns the total amount of money stored in all accounts in the bank.
   * 
   * @return total amount of money stored in all accounts in the bank
   */
  BigDecimal getBankTotal();

  /**
   * Serializes the database.
   */
  void serializeDatabase();

  /**
   * Returns the message with ID messageId without updating the view status.
   * 
   * @param messageId a message ID
   * @return the message with ID messageId
   */
  String peekMessage(int messageId);

}
