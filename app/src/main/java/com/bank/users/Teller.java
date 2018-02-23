package com.bank.users;

/**
 * A class to represent a bank teller.
 * 
 * @author Jeffrey Li
 */
public class Teller extends User {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = 1473521848486544341L;

  /**
   * Creates a nullified teller for user builder.
   */
  protected Teller() {
    /* empty constructor for builder */
  }

  /**
   * Creates an unauthenticated teller with an ID number, name, age, and address.
   * 
   * @param id the teller ID number
   * @param name the name of the teller
   * @param age the age of the teller
   * @param address the address of the teller
   */
  public Teller(int id, String name, int age, String address) {
    setId(id);
    setName(name);
    setAge(age);
    setAddress(address);
    findAndSetRoleId();
    findAndUpdateMessageIds();
  }

  /**
   * Creates a teller with an ID number, name, age, address, and age. Teller is authenticated based
   * on the argument input.
   * 
   * @param id the teller ID number
   * @param name the name of the teller
   * @param age the age of the teller
   * @param address the address of the teller
   * @param authenticated whether or not this teller has been authenticated
   */
  public Teller(int id, String name, int age, String address, boolean authenticated) {
    this(id, name, age, address);
    if (authenticated) {
      forceAuthenticate();
    }
  }

}
