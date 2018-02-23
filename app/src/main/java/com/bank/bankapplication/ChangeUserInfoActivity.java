package com.bank.bankapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.users.User;

/**
 * A teller changes the current authenticated customer's information.
 */
public class ChangeUserInfoActivity extends AppCompatActivity {

  User user;
  EditText etNewName;
  EditText etNewAge;
  EditText etNewAddress;
  EditText etNewPassword;
  EditText etConfirmPassword;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Change User Information");
    // set attributes from bundle
    Bundle bundle = getIntent().getExtras();
    int userId = bundle.getInt("USERID");
    user = DatabaseSelectHelper.getUserDetails(userId);
    String currName = user.getName();
    String currAge = "" + user.getAge();
    String currAddress = user.getAddress();
    // set views from layout
    etNewName = (EditText) findViewById(R.id.change_user_info_et_name);
    etNewAge = (EditText) findViewById(R.id.change_user_info_et_age);
    etNewAddress = (EditText) findViewById(R.id.change_user_info_et_address);
    etNewPassword = (EditText) findViewById(R.id.change_user_info_et_password);
    etConfirmPassword = (EditText) findViewById(R.id.change_user_info_et_confirm);
    // set current user info onto views (except current password)
    etNewName.setText(currName);
    etNewAge.setText(currAge);
    etNewAddress.setText(currAddress);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_change_user_info);
    setViewsAndAttributes();
  }

  /**
   * Write the new user details to the database.
   *
   * @param view a button view that calls this method
   */
  public void submitUserInfo(View view) {
    boolean validUserInfo = true;
    // get and check if new name is valid
    String newName = etNewName.getText().toString();
    if (newName.isEmpty()) {
      etNewName.setError("Field cannot be empty.");
      validUserInfo = false;
    }
    // get and check if new age is valid
    int newAge = -1;
    String strNewAge = etNewAge.getText().toString();
    if (strNewAge.isEmpty()) {
      etNewAge.setError("Field cannot be empty.");
      validUserInfo = false;
    } else {
      newAge = Integer.parseInt(strNewAge);
    }
    // get and check if new address is valid
    String newAddress = etNewAddress.getText().toString();
    if (!DatabaseValidHelper.validAddress(newAddress)) {
      etNewAddress.setError("Address must be between 1 and 100 characters.");
      validUserInfo = false;
    }
    // get and check if new password is valid (if non-empty)
    String newPassword = etNewPassword.getText().toString();
    if (!newPassword.isEmpty()) {
      if (!DatabaseValidHelper.validPassword(newPassword)) {
        etNewPassword.setError("Password must be between 4 and 64 characters.");
        validUserInfo = false;
      }
    }
    // check for password confirmation
    String confirmPassword = etConfirmPassword.getText().toString();
    if (confirmPassword.isEmpty()) {
      etConfirmPassword.setError("Field cannot be empty.");
      validUserInfo = false;
    }
    // check if all user inputs are valid
    if (validUserInfo) {
      if (user.authenticate(confirmPassword)) {
        boolean done = true;
        done = done && DatabaseUpdateHelper.updateUserName(newName, user.getId());
        done = done && DatabaseUpdateHelper.updateUserAge(newAge, user.getId());
        done = done && DatabaseUpdateHelper.updateUserAddress(newAddress, user.getId());
        if (!newPassword.isEmpty()) {
          done = done && DatabaseUpdateHelper.updateUserPassword(newPassword, user.getId());
        }
        if (done) {
          Toast.makeText(this, "User information successfully updated.", Toast.LENGTH_LONG).show();
          finish();
        } else {
          Toast.makeText(this, "Something went wrong with user update.", Toast.LENGTH_LONG).show();
        }
      } else {
        etConfirmPassword.setError("Password is incorrect.");
        Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
      }
    }
  }

}
