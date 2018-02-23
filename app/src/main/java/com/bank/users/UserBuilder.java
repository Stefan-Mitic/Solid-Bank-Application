package com.bank.users;

/**
 * Builder design pattern for creating user objects.
 */
public interface UserBuilder {

  /**
   * Sets the ID number of this user.
   * 
   * @param id a user ID number
   * @return the user builder (for method cascading)
   */
  public UserBuilder setId(int id);

  /**
   * Sets the name of this user.
   * 
   * @param name the name of this user
   * @return the user builder (for method cascading)
   */
  public UserBuilder setName(String name);

  /**
   * Sets the age of this user.
   * 
   * @param age the age of this user
   * @return the user builder (for method cascading)
   */
  public UserBuilder setAge(int age);

  /**
   * Sets the address of this user.
   * 
   * @param address the address of this user
   * @return the user builder (for method cascading)
   */
  public UserBuilder setAddress(String address);

  /**
   * Returns the newly created user object.
   * 
   * @return the newly created user
   */
  public User buildUser();

}
