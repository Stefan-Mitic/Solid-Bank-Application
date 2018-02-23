//package com.bank.serialize;
//
//import com.bank.accounts.Account;
//import com.bank.database.DatabaseDriver;
//import com.bank.databasehelper.DatabaseInsertHelper;
//import com.bank.databasehelper.DatabaseSelectHelper;
//import com.bank.exceptions.ConnectionFailedException;
//import com.bank.generics.AccountTypes;
//import com.bank.generics.Roles;
//import com.bank.messages.Message;
//import com.bank.users.User;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.math.BigDecimal;
//
//public class Serialize {
//
//    /**
//     * Serializes the database data into a new file called "database_copy.ser".
//     *
//     * @return boolean true if it has completed, false otherwise
//     */
//    protected boolean serializeDatabase() {
//        try {
//            FileOutputStream file = new FileOutputStream("database_copy.ser");
//            ObjectOutputStream output = new ObjectOutputStream(file);
//
//            // Writes the roleId and role Names in the file
//            for (Integer roleId : DatabaseSelectHelper.getRoles()) {
//                output.writeObject(DatabaseSelectHelper.getRole(roleId));
//                // output.writeInt(roleId);
//            }
//
//            // Writes the accountTypeIds and accountTypeNames in the file
//            for (Integer accountTypeId : DatabaseSelectHelper.getAccountTypesIds()) {
//                output.writeObject(DatabaseSelectHelper.getAccountTypeName(accountTypeId));
//                output.writeObject(DatabaseSelectHelper.getBalance(accountTypeId));
//            }
//
//            // Writes each User into the file with all of their unique information and accounts
//            for (User user : DatabaseSelectHelper.getAllUsers()) {
//                output.writeObject(user);
//                output.writeObject(DatabaseSelectHelper.getPassword(user.getId()));
//
//                // Writes each Message correlated to the specified User
//                for (Message message : DatabaseSelectHelper.getAllMessages(user.getId())) {
//                    output.writeObject(message);
//                }
//
//                // Writes each Account correlated to the specified User
//                for (Integer accountId : DatabaseSelectHelper.getAccountIds(user.getId())) {
//                    Account account = DatabaseSelectHelper.getAccountDetails(accountId);
//                    output.writeObject(account);
//                }
//            }
//            output.close();
//            file.close();
//            return true;
//        } catch (Exception e) {
//            // Throw an exception message
//        }
//        return false;
//    }
//
//    /**
//     * Deserialize the data from "database_copy.ser" and places the data into the new database.
//     *
//     * @return boolean if the task is completed, otherwise false
//     */
//    protected boolean deserializeDatabase() {
//        // Reinitialize the Database
//        try {
//            DatabaseDriver.reInitialize();
//        } catch (ConnectionFailedException e1) {
//            System.out.println("Connection Failed when Reinitializing Database");
//            return false;
//        }
//
//        try {
//            FileInputStream file = new FileInputStream("database_copy.ser");
//            ObjectInputStream input = new ObjectInputStream(file);
//            Object temp;
//
//            // Inserts each Role into the Database
//            while (containsRole((Roles) (temp = input.readObject()))) {
//                DatabaseInsertHelper.insertRole(((Roles) temp).toString());
//            }
//
//            // Inserts each AccountType into the Database
//            while (containsAccountType((AccountTypes) (temp = input.readObject()))) {
//                DatabaseInsertHelper.insertAccountType(((AccountTypes) temp).toString(),
//                        (BigDecimal) input.readObject());
//            }
//
//            // Inserts each User and their corresponding Messages and Accounts into the Database
//            while ((temp = input.readObject()) != null) {
//                int userId =
//                        DatabaseInsertHelper.insertNewUser(((User) temp).getName(), ((User) temp).getAge(),
//                                ((User) temp).getAddress(), ((User) temp).getRoleId(), (String) input.readObject());
//                while (!((temp = input.readObject()) instanceof Account)) {
//                    DatabaseInsertHelper.insertMessage(userId, ((Message) temp).getMessage());
//                }
//                while (!((temp = input.readObject()) instanceof User)) {
//                    int accountId = DatabaseInsertHelper.insertAccount(((Account) temp).getName(),
//                            ((Account) temp).getBalance(), ((Account) temp).getType());
//                    // Insert the account corresponding to the user
//                    DatabaseInsertHelper.insertUserAccount(userId, accountId);
//                }
//            }
//
//            input.close();
//            file.close();
//            return true;
//        } catch (IOException i) {
//            // Throw an exception
//        } catch (ClassNotFoundException e) {
//            // Throw an exception
//        }
//        return false;
//    }
//
//    /**
//     * Checks if the specified Role is in the Enum.
//     *
//     * @param role is a Role
//     * @return boolean true if the role is in the Enum, otherwise false
//     */
//    private boolean containsRole(Roles role) {
//        for (Roles currRole : Roles.values()) {
//            if (currRole.equals(role)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Checks if the specified AccountType is in the Enum.
//     *
//     * @param accountType is an AccountType
//     * @return boolean true if the accountType is in the Enum, otherwise false
//     */
//    private boolean containsAccountType(AccountTypes accountType) {
//        for (AccountTypes currAccountType : AccountTypes.values()) {
//            if (currAccountType.equals(accountType)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
