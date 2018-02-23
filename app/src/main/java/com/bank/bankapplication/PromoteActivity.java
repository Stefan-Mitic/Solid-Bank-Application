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
import com.bank.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An admin selects and promotes a teller to an admin.
 */
public class PromoteActivity extends AppCompatActivity {

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
  private ArrayList<String> userListToSpinnerArray(List<User> users,
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
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Promote Teller");
    // set attributes from intent
    Bundle bundle = getIntent().getExtras();
    ArrayList<User> users = (ArrayList<User>) bundle.getSerializable("LIST_USERS");
    ArrayList<String> userInfoList = userListToSpinnerArray(users, userInfoIdMap);
    // set and populate spinner
    spnUsers = (Spinner) findViewById(R.id.promote_spn_user_selection);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            userInfoList);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spnUsers.setAdapter(adapter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_promote);
    setViewsAndAttributes();
  }

  /**
   * Promotes the selected teller on the spinner to an admin.
   *
   * @param view a button that calls this method.
   */
  public void promoteTeller(View view) {
    String infoKey = spnUsers.getSelectedItem().toString();
    int userId = userInfoIdMap.get(infoKey);
    // display toast for successful promotion
    Toast.makeText(this, "Teller has been promoted to Admin.", Toast.LENGTH_LONG).show();
    // send result back to parent admin menu activity
    Intent intent = new Intent();
    intent.putExtra("USERID", userId);
    setResult(RESULT_OK, intent);
    finish();
  }

}
