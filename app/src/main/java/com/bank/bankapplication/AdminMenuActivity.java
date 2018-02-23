package com.bank.bankapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseSerializeHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.Roles;
import com.bank.terminals.AdminTerminal;
import com.bank.terminals.SolidAdminTerminal;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin terminal interface menu.
 */
public class AdminMenuActivity extends AppCompatActivity {

  private static final int VIEW_MESSAGE_REQUEST = 1;
  private static final int LEAVE_MESSAGE_REQUEST = 2;
  private static final int PROMOTE_TELLER_REQUEST = 3;

  int userId;
  AdminTerminal terminal;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_menu);
    setTitle("Admin Terminal");
    Bundle bundle = getIntent().getExtras();
    userId = bundle.getInt("USERID");
    String password = bundle.getString("PASSWORD");
    terminal = new SolidAdminTerminal(userId, password);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if first admin has been created, disable option after
    if (resultCode == RESULT_OK) {
      Bundle bundle = data.getExtras();
      switch (requestCode) {
        case LEAVE_MESSAGE_REQUEST:
          int recipientId = bundle.getInt("USERID");
          String msgContent = bundle.getString("MESSAGE");
          terminal.leaveMessage(recipientId, msgContent);
          break;
        case PROMOTE_TELLER_REQUEST:
          int promotingUserId = bundle.getInt("USERID");
          if (terminal.promoteTeller(promotingUserId)) {
            terminal.leaveMessage(promotingUserId, "SYSTEM: You have been promoted to an admin.");
          } else {
            Toast.makeText(this, "Something went wrong with promoting teller to admin",
                    Toast.LENGTH_SHORT).show();
          }
          break;
      }
    }
  }

  public void createNewTeller(View view) {
    Intent intent = new Intent(this, CreateUserActivity.class);
    intent.putExtra("ROLE", Roles.TELLER.toString());
    startActivity(intent);
  }

  public void createNewAdmin(View view) {
    Intent intent = new Intent(this, CreateUserActivity.class);
    intent.putExtra("ROLE", Roles.ADMIN.toString());
    startActivity(intent);
  }

  public void listAllAdmins(View view) {
    Intent intent = new Intent(this, ListViewActivity.class);
    List<User> admins = terminal.listUsers(Roles.ADMIN);
    ArrayList<String> adminInfoList = ActivityHelpers.userListToStringList(admins);
    intent.putExtra("OPTION", IntentOptions.LIST_USERS);
    intent.putExtra("LIST_USER_INFO", adminInfoList);
    startActivity(intent);
  }

  public void listAllTellers(View view) {
    Intent intent = new Intent(this, ListViewActivity.class);
    List<User> tellers = terminal.listUsers(Roles.TELLER);
    ArrayList<String> tellerInfoList = ActivityHelpers.userListToStringList(tellers);
    intent.putExtra("OPTION", IntentOptions.LIST_USERS);
    intent.putExtra("LIST_USER_INFO", tellerInfoList);
    startActivity(intent);
  }

  public void listAllCustomers(View view) {
    Intent intent = new Intent(this, ListViewActivity.class);
    List<User> customers = terminal.listUsers(Roles.CUSTOMER);
    ArrayList<String> customerInfoList = ActivityHelpers.userListToStringList(customers);
    intent.putExtra("OPTION", IntentOptions.LIST_USERS);
    intent.putExtra("LIST_USER_INFO", customerInfoList);
    startActivity(intent);
  }

  public void viewBankTotal(View view) {
    String bankTotal = ActivityHelpers.toCurrency(terminal.getBankTotal());
    String dialogMsg = "SYSTEM: the bank total is: " + bankTotal;
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    AlertDialog alertDialog = alertDialogBuilder.setTitle("Bank Total")
            .setMessage(dialogMsg)
            .create();
    alertDialog.show();
  }

  public void promoteTeller(View view) {
    Intent intent = new Intent(this, PromoteActivity.class);
    ArrayList<User> tellers = (ArrayList<User>) terminal.listUsers(Roles.TELLER);
    intent.putExtra("LIST_USERS", tellers);
    startActivityForResult(intent, PROMOTE_TELLER_REQUEST);
  }

  public void viewAllAccounts(View view) {
    Intent intent = new Intent(this, ListViewActivity.class);
    List<Account> accounts = DatabaseSelectHelper.getAllAccounts();
    ArrayList<String> accountInfoList = ActivityHelpers.accountListToStringList(accounts);
    intent.putExtra("OPTION", IntentOptions.LIST_ACCOUNTS);
    intent.putExtra("LIST_ACCOUNT_INFO", accountInfoList);
    startActivity(intent);
  }

  public void viewCustomerAccounts(View view) {
    Intent intent = new Intent(this, UserSelectionActivity.class);
    intent.putExtra("OPTION", IntentOptions.VIEW_CUSTOMER_ACCOUNTS);
    startActivity(intent);
  }

  public void peekMessage(View view) {
    Intent intent = new Intent(this, UserSelectionActivity.class);
    intent.putExtra("OPTION", IntentOptions.PEEK_MESSAGE);
    startActivity(intent);
  }

  public void leaveMessage(View view) {
    ArrayList<User> users = (ArrayList<User>) DatabaseSelectHelper.getAllUsers();
    if (users.isEmpty()) {
      Toast.makeText(this, "No customers to leave message for.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, CreateMessageActivity.class);
    intent.putExtra("USER_LIST", users);
    startActivityForResult(intent, LEAVE_MESSAGE_REQUEST);
  }

  public void viewMessages(View view) {
    if (terminal.listMessageIds().isEmpty()) {
      Toast.makeText(this, "No messages to display.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, ListViewActivity.class);
    intent.putExtra("OPTION", IntentOptions.LIST_MESSAGES);
    intent.putExtra("USERID", userId);
    startActivityForResult(intent, VIEW_MESSAGE_REQUEST);
  }

  public void setInterestRate(View view) {
    Intent intent = new Intent(this, SetInterestRateActivity.class);
    startActivity(intent);
  }

  public void serializeDatabase(View view) {
    DatabaseSerializeHelper.getDatabaseDriver(this);
    String dialogMsg;
    if (DatabaseSerializeHelper.serializeDatabase(this)) {
      dialogMsg = "Database has been serialized.";
    } else {
      dialogMsg = "Database failed to be serialized.";
    }
    // display dialog message
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage(dialogMsg);
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  public void deserializeDatabase(View view) {
    String dialogMsg;
    if (DatabaseSerializeHelper.deserializeDatabase(this)) {
      dialogMsg = "Database has been deserialized.";
    } else {
      dialogMsg = "Database has failed to be deserialized.";
    }
    // display dialog message
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage(dialogMsg);
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  public void logout(View view) {
    Toast.makeText(this, getString(R.string.logout_message), Toast.LENGTH_LONG).show();
    setResult(RESULT_OK);
    finish();
  }

}
