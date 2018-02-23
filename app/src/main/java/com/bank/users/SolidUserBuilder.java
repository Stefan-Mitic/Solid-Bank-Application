package com.bank.users;

import com.bank.generics.Roles;

/**
 * A factory constructor implementation of UserBuilder.
 */
public class SolidUserBuilder implements UserBuilder {

  /**
   * The user object that this builder is creating.
   */
  private User user;

  /**
   * Creates a User builder for the user of Role role.
   * 
   * @param role the role of the user object
   */
  public SolidUserBuilder(String role) {
    if (role.equalsIgnoreCase(Roles.ADMIN.toString())) {
      user = new Admin();
    } else if (role.equalsIgnoreCase(Roles.TELLER.toString())) {
      user = new Teller();
    } else if (role.equalsIgnoreCase(Roles.CUSTOMER.toString())) {
      user = new Customer();
    } else {
      // TODO: throws invalid role exception?
    }
  }

  @Override
  public UserBuilder setId(int id) {
    user.setId(id);
    return this;
  }

  @Override
  public UserBuilder setName(String name) {
    user.setName(name);
    return this;
  }

  @Override
  public UserBuilder setAge(int age) {
    user.setAge(age);
    return this;
  }

  @Override
  public UserBuilder setAddress(String address) {
    user.setAddress(address);
    return this;
  }

  @Override
  public User buildUser() {
    user.findAndSetRoleId();
    if (user instanceof Customer) {
      ((Customer) user).findAndUpdateAccounts();
    }
    user.findAndUpdateMessageIds();
    return user;
  }

}
