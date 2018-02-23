package com.bank.users;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.security.PasswordHelpers;
import java.io.Serializable;
import java.util.List;

/**
 * A class to represent a bank user.
 * 
 * @author Jeffrey Li
 */
public abstract class User implements Serializable {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = 3990551775434515294L;

  /**
   * The ID number of this user.
   */
  private int id = DatabaseValidHelper.INVALID_ID;

  /**
   * The name of this user.
   */
  private String name;

  /**
   * The age of this user.
   */
  private int age;

  /**
   * The address of this user.
   */
  private String address;

  /**
   * IDs of messages for this user.
   */
  private List<Integer> messageIds;

  /**
   * The role ID number of this user.
   */
  private int roleId = DatabaseValidHelper.INVALID_ID;

  /**
   * Whether or not this user has authenticated.
   */
  private boolean authenticated = false;

  /**
   * Returns the ID number of this user.
   * 
   * @return the user's ID number.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the new ID number of this user.
   * 
   * @param id the user's new ID number
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the name of this user.
   * 
   * @return the user's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the new name of this user.
   * 
   * @param name the user's new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the age of this user.
   * 
   * @return the user's age
   */
  public int getAge() {
    return age;
  }

  /**
   * Sets the new age of this user.
   * 
   * @param age the user's new age
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * Returns the address of this user.
   * 
   * @return the user's address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the new address of this user.
   * 
   * @param address the user's new address
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Finds the message IDs in database and sets the given list to this user.
   */
  public void findAndUpdateMessageIds() {
    this.messageIds = DatabaseSelectHelper.getAllMessageIds(id);
  }

  /**
   * Returns a list of IDs of messages for this user.
   * 
   * @return list of message IDs for this user
   */
  public List<Integer> getMessageIds() {
    return messageIds;
  }

  /**
   * Finds the role ID as listed in the Roles table, and sets the given role ID to this user.
   */
  protected void findAndSetRoleId() {
    this.roleId = DatabaseSelectHelper.getUserRole(id);
  }

  /**
   * Returns the role ID number of this user.
   * 
   * @return the user's role ID number
   */
  public int getRoleId() {
    return roleId;
  }

  /**
   * Returns <code>true</code> if user was authenticated was the correct password. Returns
   * <code>false</code> otherwise.
   * 
   * @param password the password to input
   * @return <code>true</code> if authenticated with correct password, <code>false</code> otherwise
   */
  public final boolean authenticate(String password) {
    if (password != null) {
      // get hash password from database
      String hashPassword = DatabaseSelectHelper.getPassword(id);
      // compare passwords to authenticate
      authenticated = PasswordHelpers.comparePassword(hashPassword, password);
    }
    return authenticated;
  }

  /**
   * Sets the user to be authenticated.
   */
  protected void forceAuthenticate() {
    authenticated = true;
  }

}
