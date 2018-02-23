package com.bank.generics;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import java.util.EnumMap;
import java.util.List;

public class RolesEnumMap {

  private static EnumMap<Roles, Integer> enumMap = new EnumMap<>(Roles.class);

  /**
   * Returns the id number correlated to the Role.
   * 
   * @param role is a type from the Roles enum representing a specific role
   * @return an int representing the Role id number
   */
  public static int getRoleId(Roles role) {
    if (enumMap.get(role) != null) {
      return enumMap.get(role);
    } else {
      return -2;
    }
  }

  /**
   * Given the id of a role return the name of the role.
   * 
   * @param roleId the id of the role
   * @return the name of the role
   */
  public static String getRoleName(int roleId) {
    for (Roles currRole : enumMap.keySet()) {
      if (enumMap.get(currRole) == roleId) {
        return currRole.toString();
      }
    }
    return null;
  }

  /**
   * Inserts a new role into the enum map as well as the database. If successful returns the id of
   * the Role, otherwise -1.
   * 
   * @param role is a String representing the new role to be inserted
   */
  public static int insertRole(String role) {
    int roleId = DatabaseInsertHelper.insertRole(role);
    Roles setRole = null;
    // Loops through all Roles in the enum and checks if the role to be inserted is not already part
    // of the enum map
    forLoop: for (Roles currRole : Roles.values()) {
      if (!enumMap.containsKey(currRole) && role.equalsIgnoreCase(currRole.toString())) {
        setRole = currRole;
        break forLoop;
      }
    }
    // Checks if the role was properly inserted
    if (roleId != -1 && setRole != null) {
      enumMap.put(setRole, roleId);
      return roleId;
    } else {
      return -1;
    }
  }

  /**
   * Updates the enum map according to the roles found in the database.
   */
  public static void update() {
    List<Integer> roleIds = DatabaseSelectHelper.getRoles();
    for (int currRoleId : roleIds) {
      // Loops through all roles in the enum and compares to the ones in the database, adds the
      // roles with their id numbers accordingly to the enum map
      forLoop: for (Roles currRole : Roles.values()) {
        if (DatabaseSelectHelper.getRole(currRoleId).equalsIgnoreCase(currRole.toString())) {
          enumMap.put(currRole, currRoleId);
          break forLoop;
        }
      }
    }
  }
}
