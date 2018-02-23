package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A user selects an entry from a list of accounts, and goes to a new activity.
 */
public class AccountSelectionActivity extends AppCompatActivity {

  private static final int CREATE_JOINT_ACCOUNT_REQUEST = 1;

  int accountId = -1;
  private Map<String, Integer> accountInfoIdMap = new HashMap<>();
  private ListView lvAccounts;

  /**
   * Converts a list of account objects to an array list of account info strings, and populates
   * the respective account info ID map.
   *
   * @param accounts list of account objects
   * @param accountInfoIdMap map to be populated, maps from info string to account ID
   * @return an array list of account info strings
   */
  private static ArrayList<String> accountListToStringArray(ArrayList<Account> accounts,
                                                            Map<String, Integer> accountInfoIdMap) {
    ArrayList<String> accountInfoList = new ArrayList<>();
    for (Account account : accounts) {
      int accountId = account.getId();
      String info = String.format(Locale.CANADA, "%d - %s\n%s\n%s", accountId,
              DatabaseSelectHelper.getAccountTypeName(account.getType()), account.getName(),
              ActivityHelpers.toCurrency(account.getBalance()));
      // add info string to array list and map
      accountInfoList.add(info);
      accountInfoIdMap.put(info, accountId);
    }
    return accountInfoList;
  }
  /**
   * Set views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Select an Account");
    Bundle bundle = getIntent().getExtras();
    ArrayList<Account> accounts = (ArrayList<Account>) bundle.getSerializable("LIST_ACCOUNTS");
    ArrayList<String> accountInfoList = accountListToStringArray(accounts, accountInfoIdMap);
    lvAccounts = (ListView) findViewById(R.id.list_information_lv_contents);
    ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
            accountInfoList);
    lvAccounts.setAdapter(adapter);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // once user has been selected for join account activity creation, go back to teller menu
    if (requestCode == CREATE_JOINT_ACCOUNT_REQUEST && resultCode == RESULT_OK) {
      Intent intent = new Intent();
      intent.putExtra("USERID", data.getExtras().getInt("USERID"))
              .putExtra("ACCOUNTID", accountId);
      setResult(RESULT_OK, intent);
      finish();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_information);
    setViewsAndAttributes();
    // go to next activity if somebody clicks on an entry on the list view
    lvAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String infoKey = adapterView.getItemAtPosition(i).toString();
        accountId = accountInfoIdMap.get(infoKey);
        Intent intent = new Intent(view.getContext(), JointAccountActivity.class);
        intent.putExtra("ACCOUNTID", accountId);
        startActivityForResult(intent, CREATE_JOINT_ACCOUNT_REQUEST);
      }
    });
  }

}
