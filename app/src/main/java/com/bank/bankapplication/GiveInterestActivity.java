package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Teller gives interest to an authenticated customer account.
 */
public class GiveInterestActivity extends AppCompatActivity {

  /**
   * Maps basic account info to account ID.
   */
  Map<String, Integer> accountInfoIdMap = new HashMap<>();
  Spinner spnAccounts;

  /**
   * Converts list of account objects into a list of account info string to populate the spinner
   * with. Populates the account info ID map.
   *
   * @param accounts a list of account objects
   * @return a list of account info strings
   */
  private ArrayList<String> accountListToSpinnerArray(List<Account> accounts) {
    ArrayList<String> accountInfoList = new ArrayList<>();
    for (Account account : accounts) {
      String typeName = DatabaseSelectHelper.getAccountTypeName(account.getType());
      String accountInfo = String.format(Locale.CANADA, "%s - %s (%s) ",
              typeName, account.getBalance(), account.getName());
      accountInfoList.add(accountInfo);
      accountInfoIdMap.put(accountInfo, account.getId());
    }
    return accountInfoList;
  }

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Give Interest to Account");
    spnAccounts = (Spinner) findViewById(R.id.give_interest_spn_account_selection);
    Bundle bundle = getIntent().getExtras();
    ArrayList<Account> accounts = (ArrayList<Account>) bundle.getSerializable("LIST_ACCOUNTS");
    ArrayList<String> accountInfoList = accountListToSpinnerArray(accounts);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            accountInfoList);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spnAccounts.setAdapter(adapter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_give_interest);
    setViewsAndAttributes();
  }

  /**
   * Adds interest to the selected account on the spinner.
   *
   * @param view a button view that calls this method
   */
  public void applyInterest(View view) {
    String infoKey = spnAccounts.getSelectedItem().toString();
    int accountId = accountInfoIdMap.get(infoKey);
    Toast.makeText(this, "Applied interest to account ID" + accountId + ".",
            Toast.LENGTH_LONG).show();
    Intent intent = new Intent();
    intent.putExtra("INTEREST_OPTION", accountId);
    setResult(RESULT_OK, intent);
    finish();
  }

  /**
   * Adds interest to all accounts.
   *
   * @param view a button view that calls this method
   */
  public void applyInterestAllAccounts(View view) {
    Toast.makeText(this, "Applied interest to all accounts.", Toast.LENGTH_LONG).show();
    Intent intent = new Intent();
    intent.putExtra("INTEREST_OPTION", 0);
    setResult(RESULT_OK, intent);
    finish();
  }

}
