package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.DoesNotOwnException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.terminals.AutoTellerMachine;
import com.bank.terminals.SolidAutoTellerMachine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Automated teller machine interface menu.
 */
public class AtmMenuActivity extends AppCompatActivity {

  private static final int DEPOSIT_REQUEST = 1;
  private static final int WITHDRAW_REQUEST = 2;
  private static final int VIEW_MESSAGE_REQUEST = 3;

  int userId;
  AutoTellerMachine terminal;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if first admin has been created, disable option after
    if (resultCode == RESULT_OK) {
      Bundle bundle = data.getExtras();
      switch (requestCode) {
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
        case VIEW_MESSAGE_REQUEST:
          int messageId = bundle.getInt("MESSAGEID");
          try {
            terminal.viewMessage(messageId);
          } catch (DoesNotOwnException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
          }
          break;
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_atm_menu);
    setTitle("Automated Teller Machine");
    Bundle bundle = getIntent().getExtras();
    userId = bundle.getInt("USERID");
    String password = bundle.getString("PASSWORD");
    terminal = new SolidAutoTellerMachine(userId, password);
  }

  public void listAccounts(View view) {
    List<Account> accounts = terminal.listAccounts();
    if (accounts.isEmpty()) {
      Toast.makeText(this, "No accounts to display.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, ListViewActivity.class);
    ArrayList<String> accountInfoList = ActivityHelpers.accountListToStringList(accounts);
    intent.putExtra("OPTION", IntentOptions.LIST_ACCOUNTS);
    intent.putExtra("LIST_ACCOUNT_INFO", accountInfoList);
    startActivity(intent);
  }

  public void makeDeposit(View view) {
    ArrayList<Account> accounts = (ArrayList<Account>) terminal.listAccounts();
    if (accounts.isEmpty()) {
      Toast.makeText(this, "No accounts to withdraw from.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, UpdateBalanceActivity.class);
    intent.putExtra("OPTION", IntentOptions.MAKE_DEPOSIT);
    intent.putExtra("LIST_ACCOUNTS", accounts);
    startActivityForResult(intent, DEPOSIT_REQUEST);
  }

  public void checkBalance(View view) {
    if (terminal.listAccounts().isEmpty()) {
      Toast.makeText(this, "No accounts to check balance.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, ListViewActivity.class);
    ArrayList<Account> accounts = (ArrayList<Account>) terminal.listAccounts();
    intent.putExtra("OPTION", IntentOptions.VIEW_BALANCE);
    intent.putExtra("LIST_ACCOUNTS", accounts);
    startActivity(intent);
  }

  public void makeWithdrawal(View view) {
    ArrayList<Account> accounts = (ArrayList<Account>) terminal.listAccounts();
    if (accounts.isEmpty()) {
      Toast.makeText(this, "No accounts to withdraw from.", Toast.LENGTH_SHORT).show();
      return;
    }
    Intent intent = new Intent(this, UpdateBalanceActivity.class);
    intent.putExtra("OPTION", IntentOptions.MAKE_WITHDRAW);
    intent.putExtra("LIST_ACCOUNTS", accounts);
    startActivityForResult(intent, WITHDRAW_REQUEST);
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

  public void logout(View view) {
    Toast.makeText(this, getString(R.string.logout_message), Toast.LENGTH_LONG).show();
    setResult(RESULT_OK);
    finish();
  }

}
