package com.bank.users;

/**
 * A class to represent a bank administrator.
 * 
 * @author Jeffrey Li
 */
public class Admin extends User {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = -3285970488517182415L;

  /**
   * Creates a nullified administrator for user builder.
   */
  protected Admin() {
    /* empty constructor for builder */
  }

  /**
   * Creates an unauthenticated administrator with an ID number, name, age, and address.
   * 
   * @param id the administrator ID number
   * @param name the name of the administrator
   * @param age the age of the administrator
   * @param address the address of the administrator
   */
  public Admin(int id, String name, int age, String address) {
    setId(id);
    setName(name);
    setAge(age);
    setAddress(address);
    findAndSetRoleId();
    findAndUpdateMessageIds();
  }

  /**
   * Creates a administrator with an ID number, name, age, address, and age. Administrator is
   * authenticated based on the argument input.
   * 
   * @param id the administrator ID number
   * @param name the name of the administrator
   * @param age the age of the administrator
   * @param address the address of the administrator
   * @param authenticated whether or not this administrator has been authenticated
   */
  public Admin(int id, String name, int age, String address, boolean authenticated) {
    this(id, name, age, address);
    if (authenticated) {
      forceAuthenticate();
    }
  }

}
