package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An admin or a teller writes a message to a recipient user.
 */
public class CreateMessageActivity extends AppCompatActivity {

  /**
   * Maps user info strings to the respective user ID number.
   */
  Map<String, Integer> userInfoMap = new HashMap<>();

  EditText etContent;
  Spinner spnRecipient;

  /**
   * Converts a list of user objects into an array list of user info strings, and populates the
   * respective user info map to be used for creating the spinner.
   *
   * @param users a list of user objects
   * @return an array of user info strings
   */
  private ArrayList<String> userListToSpinnerArray(List<User> users) {
    ArrayList<String> userInfoList = new ArrayList<>();
    for (User user : users) {
      int userId = user.getId();
      String name = user.getName();
      String roleName = DatabaseSelectHelper.getRole(user.getRoleId());
      String info = String.format(Locale.CANADA, "%s - %s (ID%d)", roleName, name, userId);
      userInfoList.add(info);
      userInfoMap.put(info, userId);
    }
    return userInfoList;
  }

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Create a Message");
    // set attributes from intent
    Bundle bundle = getIntent().getExtras();
    ArrayList<User> users = (ArrayList<User>) bundle.getSerializable("USER_LIST");
    // set views from layout
    etContent = (EditText) findViewById(R.id.create_message_et_content);
    etContent.setHorizontallyScrolling(false);
    etContent.setMaxLines(Integer.MAX_VALUE);
    // set and populate spinner
    spnRecipient = (Spinner) findViewById(R.id.create_message_spn_recipient);
    ArrayList<String> userInfoList = userListToSpinnerArray(users);
    // create and set array adapter
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            userInfoList);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spnRecipient.setAdapter(adapter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_message);
    setViewsAndAttributes();
  }

  /**
   * Writes a new message to the database.
   *
   * @param view a button view that calls this method
   */
  public void submitMessage(View view) {
    boolean validMessage = true;
    String msgContent = etContent.getText().toString();
    if (msgContent.isEmpty()) {
      etContent.setError("Message cannot be empty.");
      validMessage = false;
    } else if (!DatabaseValidHelper.validMessage(msgContent)) {
      etContent.setError("Message must have at most 512 characters.");
      validMessage = false;
    }
    // check if user input was valid
    if (validMessage) {
      String infoKey = spnRecipient.getSelectedItem().toString();
      int userId = userInfoMap.get(infoKey);
      Toast.makeText(this, "Message with " + msgContent.length() + " characters has been sent.",
              Toast.LENGTH_LONG).show();
      Intent returnIntent = new Intent();
      returnIntent.putExtra("MESSAGE", msgContent);
      returnIntent.putExtra("USERID", userId);
      setResult(RESULT_OK, returnIntent);
      finish();
    }
  }

}
