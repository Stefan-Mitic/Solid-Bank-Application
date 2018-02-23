package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.accounts.BalanceOwingAccount;
import com.bank.accounts.RestrictedSavingsAccount;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.Roles;
import com.bank.terminals.SolidTellerTerminal;
import com.bank.terminals.TellerTerminal;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Teller terminal interface menu.
 */
public class TellerMenuActivity extends AppCompatActivity {

  private static final int DEPOSIT_REQUEST = 1;
  private static final int WITHDRAW_REQUEST = 2;
  private static final int VIEW_MESSAGE_REQUEST = 3;
  private static final int AUTHENTICATE_CUSTOMER_REQUEST = 4;
  private static final int ADD_LOANS_REQUEST = 5;
  private static final int LEAVE_MESSAGE_REQUEST = 6;
  private static final int GIVE_INTEREST_REQUEST = 7;
  private static final int CREATE_JOINT_ACCOUNT_REQUEST = 8;

  int tellerId;
  TellerTerminal terminal;

  Button btnAuthenticateCustomer;
  Button btnCreateUser;
  Button btnCreateAccount;
  Button btnCreateJointAccount;
  Button btnGiveInterest;
  Button btnDeposit;
  Button btnWithdraw;
  Button btnAddLoans;
  Button btnCheckBalance;
  Button btnListAccounts;
  Button btnViewCustomerMessages;
  Button btnChangeCustomerInfo;
  Button btnCloseCustomerSession;
  Button btnLeaveMessage;
  Button btnViewMessages;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Teller Terminal");
    // set attributes from intent
    Bundle bundle = getIntent().getExtras();
    tellerId = bundle.getInt("USERID");
    // set views from layout
    btnAuthenticateCustomer = (Button) findViewById(R.id.teller_menu_btn_authenticate_customer);
    btnCreateUser = (Button) findViewById(R.id.teller_menu_btn_make_customer);
    btnCreateAccount = (Button) findViewById(R.id.teller_menu_btn_make_account);
    btnCreateJointAccount = (Button) findViewById(R.id.teller_menu_btn_create_joint_account);
    btnGiveInterest = (Button) findViewById(R.id.teller_menu_btn_give_interest);
    btnDeposit = (Button) findViewById(R.id.teller_menu_btn_deposit);
    btnWithdraw = (Button) findViewById(R.id.teller_menu_btn_withdraw);
    btnAddLoans = (Button) findViewById(R.id.teller_menu_btn_add_loans);
    btnCheckBalance = (Button) findViewById(R.id.teller_menu_btn_check_balance);
    btnListAccounts = (Button) findViewById(R.id.teller_menu_btn_list_customer_accounts);
    btnViewCustomerMessages = (Button) findViewById(R.id.teller_menu_btn_view_customer_messages);
    btnChangeCustomerInfo = (Button) findViewById(R.id.teller_menu_btn_change_customer_info);
    btnCloseCustomerSession = (Button) findViewById(R.id.teller_menu_btn_close_customer_session);
    btnLeaveMessage = (Button) findViewById(R.id.teller_menu_btn_leave_customer_message);
    btnViewMessages = (Button) findViewById(R.id.teller_menu_btn_view_messages);
    // disable customer buttons on startup
    disableCustomerOptions();
  }

  private void disableCustomerOptions() {
    btnAuthenticateCustomer.setEnabled(true);
    btnCreateUser.setEnabled(true);
    btnCreateAccount.setEnabled(false);
    btnCreateJointAccount.setEnabled(false);
    btnGiveInterest.setEnabled(false);
    btnDeposit.setEnabled(false);
    btnWithdraw.setEnabled(false);
    btnAddLoans.setEnabled(false);
    btnCheckBalance.setEnabled(false);
    btnListAccounts.setEnabled(false);
    btnViewCustomerMessages.setEnabled(false);
    btnChangeCustomerInfo.setEnabled(false);
    btnCloseCustomerSession.setEnabled(false);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_teller_menu);
    setViewsAndAttributes();
    String password = getIntent().getStringExtra("PASSWORD");
    terminal = new SolidTellerTerminal(tellerId, password);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if first admin has been created, disable option after
    if (resultCode == RESULT_OK) {
      Bundle bundle = data.getExtras();
      switch (requestCode) {
        case AUTHENTICATE_CUSTOMER_REQUEST:
          terminal.setCustomer(bundle.getInt("CUSTOMER_ID"));
          terminal.authenticateCustomer(bundle.getString("CUSTOMER_PASSWORD"));
          btnAuthenticateCustomer.setEnabled(false);
          btnCreateUser.setEnabled(false);
          btnCreateAccount.setEnabled(true);
          btnCreateJointAccount.setEnabled(true);
          btnGiveInterest.setEnabled(true);
          btnDeposit.setEnabled(true);
          btnWithdraw.setEnabled(true);
          btnAddLoans.setEnabled(true);
          btnCheckBalance.setEnabled(true);
          btnListAccounts.setEnabled(true);
          btnViewCustomerMessages.setEnabled(true);
          btnChangeCustomerInfo.setEnabled(true);
          btnCloseCustomerSession.setEnabled(true);
          break;

        case DEPOSIT_REQUEST:
          int depositAccountId = bundle.getInt("ACCOUNTID");
          BigDecimal deposit = new BigDecimal(bundle.getString("AMOUNT"));
          try {
            terminal.makeDeposit(depositAccountId, deposit);
          } catch (IllegalAmountException | DoesNotOwnException | ConnectionFailedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
          }
          break;

        case WITHDRAW_REQUEST:
          int withdrawAccountId = bundle.getInt("ACCOUNTID");
          BigDecimal withdraw = new BigDecimal(bundle.getString("AMOUNT"));
          try {
            terminal.makeWithdrawal(withdrawAccountId, withdraw);
          } catch (IllegalAmountException | DoesNotOwnException | ConnectionFailedException
                  | InsufficientFundsException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
          }
          break;

        case ADD_LOANS_REQUEST:
          int owingAccountId = bundle.getInt("ACCOUNTID");
          BigDecimal loan = new BigDecimal(bundle.getString("AMOUNT"));
          try {
            terminal.addLoans(owingAccountId, loan);
          } catch (IllegalAmountException | DoesNotOwnException | ConnectionFailedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
          }
          break;

        case LEAVE_MESSAGE_REQUEST:
          int recipientId = bundle.getInt("USERID");
          String msgContent = bundle.getString("MESSAGE");
          terminal.leaveMessage(recipientId, msgContent);
          break;

        case GIVE_INTEREST_REQUEST:
          int interestOption = bundle.getInt("INTEREST_OPTION");
          // give interest to all accounts if option is 0, or a specific account
          if (interestOption == 0) {
            terminal.giveInterest();
          } else {
            try {
              terminal.giveInterest(interestOption);
            } catch (DoesNotOwnException e) {
              Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
          }
          break;

        case CREATE_JOINT_ACCOUNT_REQUEST:
          int jointAccountUserId = bundle.getInt("USERID");
          int jointAccountId = bundle.getInt("ACCOUNTID");
          if (terminal.createJointAccount(jointAccountId, jointAccountUserId)) {
            Toast.makeText(this, "Joint account has been created with user ID" + jointAccountId
                    + ".", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(this, "Something went wrong with creating joint account.",
                    Toast.LENGTH_LONG).show();
          }
          break;
      }
    }
  }

  public void authenticateCustomer(View view) {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.putExtra("ROLE", Roles.CUSTOMER.toString())
            .putExtra("TELLER_AUTHENTICATION", true);
    startActivityForResult(intent, AUTHENTICATE_CUSTOMER_REQUEST);
  }

  public void createNewCustomer(View view) {
    Intent intent = new Intent(this, CreateUserActivity.class);
    intent.putExtra("ROLE", Roles.CUSTOMER.toString())
            .putExtra("TELLER_AUTHENTICATION", true);
    startActivityForResult(intent, AUTHENTICATE_CUSTOMER_REQUEST);
  }

  public void createNewAccount(View view) {
    Intent intent = new Intent(this, CreateAccountActivity.class);
    intent.putExtra("CUSTOMER_ID", terminal.getCustomerId());
    startActivity(intent);
  }

  public void giveInterest(View view) {
    ArrayList<Account> accounts = (ArrayList<Account>) terminal.listAccounts();
    if (accounts.isEmpty()) {
      Toast.makeText(this, "No accounts to give interest to.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, GiveInterestActivity.class);
    intent.putExtra("LIST_ACCOUNTS", accounts);
    startActivityForResult(intent, GIVE_INTEREST_REQUEST);
  }

  public void makeDeposit(View view) {
    // get list of accounts that are not restricted savings
    ArrayList<Account> validAccounts = new ArrayList<>();
    for (Account account : terminal.listAccounts()) {
      if (!(account instanceof RestrictedSavingsAccount)) {
        validAccounts.add(account);
      }
    }
    // if there are no accounts to deposit to, cancel action
    if (validAccounts.isEmpty()) {
      Toast.makeText(this, "No accounts to deposit to.", Toast.LENGTH_SHORT).show();
      return;
    }
    // start update balance activity
    Intent intent = new Intent(this, UpdateBalanceActivity.class);
    intent.putExtra("OPTION", IntentOptions.MAKE_DEPOSIT);
    intent.putExtra("LIST_ACCOUNTS", validAccounts);
    startActivityForResult(intent, DEPOSIT_REQUEST);
  }

  public void makeWithdrawal(View view) {
    // get list of accounts that are not balance owing or restricted savings
    ArrayList<Account> validAccounts = new ArrayList<>();
    for (Account account : terminal.listAccounts()) {
      boolean isOwing = account instanceof BalanceOwingAccount;
      boolean isRestricted = account instanceof RestrictedSavingsAccount;
      if (!(isOwing || isRestricted)) {
        validAccounts.add(account);
      }
    }
    // if there are no accounts to withdraw from, cancel action
    if (validAccounts.isEmpty()) {
      Toast.makeText(this, "No accounts to withdraw from.", Toast.LENGTH_SHORT).show();
      return;
    }
    // start update balance activity
    Intent intent = new Intent(this, UpdateBalanceActivity.class);
    intent.putExtra("OPTION", IntentOptions.MAKE_WITHDRAW)
            .putExtra("LIST_ACCOUNTS", validAccounts);
    startActivityForResult(intent, WITHDRAW_REQUEST);
  }

  public void addLoans(View view) {
    List<Account> accounts = terminal.listAccounts();
    // get list of accounts that are balance owing
    ArrayList<Account> owingAccounts = new ArrayList<>();
    for (Account account : accounts) {
      if (account instanceof BalanceOwingAccount) {
        owingAccounts.add(account);
      }
    }
    // if there are no accounts to add loans to, cancel accounts
    if (owingAccounts.isEmpty()) {
      Toast.makeText(this, "No balance owing accounts to add loans to.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, UpdateBalanceActivity.class);
    intent.putExtra("OPTION", IntentOptions.ADD_LOANS)
            .putExtra("LIST_ACCOUNTS", owingAccounts);
    startActivityForResult(intent, ADD_LOANS_REQUEST);
  }

  public void checkBalance(View view) {
    Intent intent = new Intent(this, ListViewActivity.class);
    ArrayList<Account> accounts = (ArrayList<Account>) terminal.listAccounts();
    intent.putExtra("OPTION", IntentOptions.VIEW_BALANCE)
            .putExtra("LIST_ACCOUNTS", accounts);
    startActivity(intent);
  }

  public void listAccounts(View view) {
    List<Account> accounts = terminal.listAccounts();
    if (accounts.isEmpty()) {
      Toast.makeText(this, "No accounts to view.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, ListViewActivity.class);
    ArrayList<String> accountInfoList = ActivityHelpers.accountListToStringList(accounts);
    intent.putExtra("OPTION", IntentOptions.LIST_ACCOUNTS)
            .putExtra("LIST_ACCOUNT_INFO", accountInfoList);
    startActivity(intent);
  }

  public void createJointAccount(View view) {
    ArrayList<Account> accounts = (ArrayList<Account>) terminal.listAccounts();
    if (accounts.isEmpty()) {
      Toast.makeText(this, "No accounts to create joint account with.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, AccountSelectionActivity.class);
    intent.putExtra("LIST_ACCOUNTS", accounts);
    startActivityForResult(intent, CREATE_JOINT_ACCOUNT_REQUEST);
  }

  public void viewCustomerMessages(View view) {
    Intent intent = new Intent(this, ListViewActivity.class);
    intent.putExtra("OPTION", IntentOptions.LIST_MESSAGES)
            .putExtra("USERID", terminal.getCustomerId());
    startActivityForResult(intent, VIEW_MESSAGE_REQUEST);
  }

  public void changeCustomerInfo(View view) {
    Intent intent = new Intent(this, ChangeUserInfoActivity.class);
    intent.putExtra("USERID", terminal.getCustomerId());
    startActivity(intent);
  }

  public void closeCustomerSession(View view) {
    setViewsAndAttributes();
    terminal.deAuthenticateCustomer();
    Toast.makeText(this, "Customer has been logged out.", Toast.LENGTH_LONG).show();
  }

  public void leaveMessage(View view) {
    ArrayList<User> customers = (ArrayList<User>) DatabaseSelectHelper.getAllUsers(Roles.CUSTOMER);
    if (customers.isEmpty()) {
      Toast.makeText(this, "No customers to leave message for.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, CreateMessageActivity.class);
    intent.putExtra("USER_LIST", customers);
    startActivityForResult(intent, LEAVE_MESSAGE_REQUEST);
  }

  public void viewMessages(View view) {
    if (terminal.listMessageIds().isEmpty()) {
      Toast.makeText(this, "No messages to display.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, ListViewActivity.class);
    intent.putExtra("OPTION", IntentOptions.LIST_MESSAGES);
    intent.putExtra("USERID", tellerId);
    startActivityForResult(intent, VIEW_MESSAGE_REQUEST);
  }

  public void logout(View view) {
    Toast.makeText(this, getString(R.string.logout_message), Toast.LENGTH_LONG).show();
    setResult(RESULT_OK);
    finish();
  }

}
