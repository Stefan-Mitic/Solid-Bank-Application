package com.bank.bankapplication;

import android.app.AlertDialog;
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
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.Roles;
import com.bank.users.Customer;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User selects an entry from a list of users, and goes to a new activity.
 */
public class UserSelectionActivity extends AppCompatActivity {

  /**
   * Maps from user info strings to user ID numbers.
   */
  Map<String, Integer> userInfoIdMap = new HashMap<>();
  int intentOption;
  ListView lvUsers;

  /**
   * Converts a list of user objects to an array list of user info strings, and populates the
   * respective user info ID map.
   *
   * @param users a list of user objects
   * @param userInfoIdMap map to be populated, maps from user info strings to user ID numbers
   * @return an array list of user info strings
   */
  private ArrayList<String> userListToStringArray(List<User> users,
                                                     Map<String, Integer> userInfoIdMap) {
    ArrayList<String> userInfoList = new ArrayList<>();
    for (User user : users) {
      int userId = user.getId();
      String name = user.getName();
      int age = user.getAge();
      String address = user.getAddress();
      String roleName = DatabaseSelectHelper.getRole(user.getRoleId());
      String info = String.format(Locale.CANADA, "%d - %s\nAge: %d\n%s\n%s", userId, name, age,
              address, roleName);
      // add user info to list and map
      userInfoList.add(info);
      userInfoIdMap.put(info, userId);
    }
    return userInfoList;
  }

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    Bundle bundle = getIntent().getExtras();
    intentOption = bundle.getInt("OPTION");
    List<User> users = new ArrayList<>();
    switch (intentOption) {
      case IntentOptions.PEEK_MESSAGE:
        users = DatabaseSelectHelper.getAllUsers();
        break;
      case IntentOptions.VIEW_CUSTOMER_ACCOUNTS:
        users = DatabaseSelectHelper.getAllUsers(Roles.CUSTOMER);
        break;
    }
    ArrayList<String> userInfoList = userListToStringArray(users, userInfoIdMap);

    lvUsers = (ListView) findViewById(R.id.list_information_lv_contents);
    ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
            userInfoList);
    lvUsers.setAdapter(adapter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_information);
    setViewsAndAttributes();
    // when user clicks on an item, go to another activity
    lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String infoKey = String.valueOf(adapterView.getItemAtPosition(i));
        int userId = userInfoIdMap.get(infoKey);

        switch (intentOption) {
          case IntentOptions.PEEK_MESSAGE:
            if (DatabaseSelectHelper.getAllMessageIds(userId).isEmpty()) {
              Toast.makeText(view.getContext(), "User has no messages.", Toast.LENGTH_SHORT).show();
            } else {
              Intent intent = new Intent(view.getContext(), ListViewActivity.class);
              intent.putExtra("OPTION", IntentOptions.LIST_MESSAGES);
              intent.putExtra("USERID", userId);
              intent.putExtra("PEEKING", true);
              startActivity(intent);
            }
            break;
          case IntentOptions.VIEW_CUSTOMER_ACCOUNTS:
            // get customer's list of accounts
            Customer customer = (Customer) DatabaseSelectHelper.getUserDetails(userId);
            List<Account> accounts = customer.getAccounts();
            if (accounts.isEmpty()) {
              Toast.makeText(view.getContext(), "User has no accounts.", Toast.LENGTH_SHORT).show();
            } else {
              Intent intent = new Intent(view.getContext(), ListViewActivity.class);
              ArrayList<String> accountInfoList = ActivityHelpers.accountListToStringList(accounts);
              intent.putExtra("OPTION", IntentOptions.LIST_ACCOUNTS);
              intent.putExtra("LIST_ACCOUNT_INFO", accountInfoList);
              startActivity(intent);
            }
        }
      }
    });
  }

}
