package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.accounts.BalanceOwingAccount;
import com.bank.databasehelper.DatabaseSelectHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateBalanceActivity extends AppCompatActivity {

  Bundle bundle;
  ArrayList<Account> accounts = new ArrayList<>();
  Map<String, Account> accountInfoMap = new HashMap<>();
  int intentOption;
  Spinner spnAccounts;
  EditText etAmount;
  Button btnSubmit;

  private ArrayList<String> accountListToSpinnerArray(List<Account> accounts) {
    ArrayList<String> accountInfoList = new ArrayList<>();
    for (Account account : accounts) {
      String typeName = DatabaseSelectHelper.getAccountTypeName(account.getType());
      String info = String.format(Locale.CANADA, "%s - %s (%s)", typeName,
              ActivityHelpers.toCurrency(account.getBalance()), account.getName());
      accountInfoList.add(info);
      accountInfoMap.put(info, account);
    }
    return accountInfoList;
  }

  /**
   * Filter down the list of accounts to used based on type of transaction.
   *
   * @param accounts an array list of accounts
   * @return a filtered list of accounts
   */
  private static ArrayList<Account> filterAccounts(List<Account> accounts, int intentOption) {
    if (intentOption == IntentOptions.MAKE_DEPOSIT) {
      return (ArrayList<Account>) accounts;
    }
    ArrayList<Account> filtered = new ArrayList<>();
    for (Account account : accounts) {
      boolean isBalanceOwing = account instanceof BalanceOwingAccount;
      if (intentOption == IntentOptions.ADD_LOANS) {
        if (isBalanceOwing) {
          filtered.add(account);
        }
      } else if (intentOption == IntentOptions.MAKE_WITHDRAW) {
        if (!isBalanceOwing) {
          filtered.add(account);
        }
      }
    }
    return filtered;
  }

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    // set attributes from intent
    bundle = getIntent().getExtras();
    intentOption = bundle.getInt("OPTION");
    accounts = (ArrayList<Account>) bundle.getSerializable("LIST_ACCOUNTS");
    accounts = filterAccounts(accounts, intentOption);
    // set title
    switch (intentOption) {
      case IntentOptions.MAKE_DEPOSIT:
        setTitle("Make a Deposit to Account");
        break;
      case IntentOptions.MAKE_WITHDRAW:
        setTitle("Make a Withdrawal from Account");
        break;
      case IntentOptions.ADD_LOANS:
        setTitle("Add Loans to Balance Owning Account");
        break;
    }
    // set views from layout
    etAmount = (EditText) findViewById(R.id.update_balance_et_amount);
    btnSubmit = (Button) findViewById(R.id.update_balance_btn_submit);
    // create and set array adapter
    ArrayList<String> accountInfoList = accountListToSpinnerArray(accounts);

    spnAccounts = (Spinner) findViewById(R.id.update_balance_spn_account_select);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            accountInfoList);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spnAccounts.setAdapter(adapter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_balance);
    setViewsAndAttributes();
  }

  /**
   * Commits transaction and updates the balance.
   *
   * @param view a button that calls this method
   */
  public void submitBalanceUpdate(View view) {
    boolean validAmount = true;
    // get account info from spinner selection
    Account account = accountInfoMap.get(spnAccounts.getSelectedItem().toString());
    int accountId = account.getId();
    BigDecimal balance = account.getBalance();
    int typeId = account.getType();
    // check if amount input is valid
    BigDecimal amount = null;
    String strAmount = etAmount.getText().toString();
    if (strAmount.isEmpty()) {
      etAmount.setError("Field cannot be empty.");
      validAmount = false;
    } else {
      amount = (new BigDecimal(strAmount)).setScale(2, RoundingMode.CEILING);
    }

    if (validAmount) {
      boolean success = true;
      Intent intent = new Intent();
      String toastMsg = "";
      String currency = ActivityHelpers.toCurrency(amount);
      switch (intentOption) {
        case IntentOptions.MAKE_DEPOSIT:
          toastMsg = "An amount of " + currency + " has been deposited.";
          break;
        case IntentOptions.MAKE_WITHDRAW:
          if (balance.compareTo(amount) >= 0) {
            toastMsg = "An amount of " + currency + " has been withdrawn.";

          } else {
            toastMsg = "Amount withdrawn must be less than or equal to current balance.";
            etAmount.setError(toastMsg);
            success = false;
          }
          break;
        case IntentOptions.ADD_LOANS:
          toastMsg = "A loan of " + currency + " has been added.";
          break;
      }
      if (success) {
        intent.putExtra("ACCOUNTID", accountId);
        intent.putExtra("AMOUNT", amount.toString());
        setResult(RESULT_OK, intent);
        finish();
      }
      Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }
  }

}
