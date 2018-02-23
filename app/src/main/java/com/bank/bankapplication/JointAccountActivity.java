package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.generics.Roles;
import com.bank.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A teller selects the authenticated customer's account and creates a joint account with another
 * selected customer.
 */
public class JointAccountActivity extends AppCompatActivity {

  /**
   * Maps user info strings to user ID numbers.
   */
  Map<String, Integer> userInfoIdMap = new HashMap<>();

  Spinner spnUsers;

  /**
   * Converts a list of user objects into an array list of user info strings, and populates the
   * respective user info map to be used for creating the spinner.
   *
   * @param users a list of user objects
   * @param userInfoIdMap user info to user ID map to populate
   * @return a list of user info strings
   */
  private static ArrayList<String> userListToSpinnerArray(List<User> users,
                                                   Map<String, Integer> userInfoIdMap) {
    ArrayList<String> userInfoList = new ArrayList<>();
    for (User user : users) {
      int userId = user.getId();
      String name = user.getName();
      String roleName = DatabaseSelectHelper.getRole(user.getRoleId());
      String info = String.format(Locale.CANADA, "%s - %s (ID%d)", roleName, name, userId);
      userInfoList.add(info);
      userInfoIdMap.put(info, userId);
    }
    return userInfoList;
  }

  /**
   * Returns a list of customers that do not own the account with ID accountId.
   *
   * @param accountId the ID number of the account
   * @return a list of customer that do not own the account, and thus are able to form the joint
   *         account with
   */
  private static List<User> getValidCustomersForJoining(int accountId) {
    List<User> validCustomers = new ArrayList<>();
    for (User user : DatabaseSelectHelper.getAllUsers(Roles.CUSTOMER)) {
      if (!DatabaseValidHelper.userOwnsAccount(user.getId(), accountId)) {
        validCustomers.add(user);
      }
    }
    return validCustomers;
  }

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Create Joint Account");
    // set attributes from intent
    Bundle bundle = getIntent().getExtras();
    int accountId = bundle.getInt("ACCOUNTID");
    List<User> validCustomers = getValidCustomersForJoining(accountId);
    ArrayList<String> userInfoList = userListToSpinnerArray(validCustomers, userInfoIdMap);
    // set and populate spinner
    spnUsers = (Spinner) findViewById(R.id.joint_account_spn_user_selection);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            userInfoList);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spnUsers.setAdapter(adapter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_joint_account);
    setViewsAndAttributes();
  }

  /**
   * Promotes the selected teller on the spinner to an admin.
   *
   * @param view a button that calls this method.
   */
  public void createJointAccount(View view) {
    String infoKey = spnUsers.getSelectedItem().toString();
    int userId = userInfoIdMap.get(infoKey);
    // pass information back to parent activity
    Intent intent = new Intent();
    intent.putExtra("USERID", userId);
    setResult(RESULT_OK, intent);
    finish();
  }

}
