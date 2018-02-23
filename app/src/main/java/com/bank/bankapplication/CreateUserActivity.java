package com.bank.bankapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.generics.Roles;
import com.bank.generics.RolesEnumMap;

/**
 * An admin or a teller inputs user details and writes a new user to the database.
 */
public class CreateUserActivity extends AppCompatActivity {

  Bundle bundle;
  Roles userRole;

  TextView tvTitle;
  EditText etName;
  EditText etAge;
  EditText etAddress;
  EditText etPassword;
  EditText etConfirm;
  Button btnSubmit;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Create New User");
    bundle = getIntent().getExtras();
    userRole = Roles.valueOf(bundle.getString("ROLE"));
    // set views
    etName = (EditText) findViewById(R.id.create_user_et_name);
    etAge = (EditText) findViewById(R.id.create_user_et_age);
    etAddress = (EditText) findViewById(R.id.create_user_et_address);
    etPassword = (EditText) findViewById(R.id.create_user_et_password);
    etConfirm = (EditText) findViewById(R.id.create_user_et_confirm);
    btnSubmit = (Button) findViewById(R.id.create_user_btn_submit);
    // set title
    tvTitle = (TextView) findViewById(R.id.create_user_tv_title);
    String roleName = userRole.toString();
    tvTitle.setText("Create " + roleName.charAt(0) + roleName.substring(1).toLowerCase());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_user);
    setViewsAndAttributes();
  }

  /**
   * Writes a new user to the database.
   *
   * @param view a button view that calls this method
   */
  public void submitUserInfo(View view) {
    boolean validUserInfo = true;
    // check if name is valid
    String name = etName.getText().toString().trim();
    if (name.isEmpty()) {
      etName.setError("User name field cannot be blank");
      validUserInfo = false;
    }
    // check if age is valid
    String strAge = etAge.getText().toString().trim();
    int age = -1;
    if (strAge.isEmpty()) {
      etAge.setError("User age field cannot be blank");
      validUserInfo = false;
    } else {
      age = Integer.parseInt(strAge);
      if (age < 0) {
        etAge.setError("User age cannot be negative");
        validUserInfo = false;
      }
    }
    // check if address is valid
    String address = etAddress.getText().toString().trim();
    if (address.isEmpty()) {
      etAddress.setError("User address field cannot be blank");
      validUserInfo = false;
    } else if (!DatabaseValidHelper.validAddress(address)) {
      etAddress.setError("User address must be between 1 and 100 characters");
      validUserInfo = false;
    }
    // check if password is valid
    String password = etPassword.getText().toString().trim();
    if (password.isEmpty()) {
      etPassword.setError("User password field cannot be blank");
      validUserInfo = false;
    }
    // check if password confirmation is valid
    String confirm = etConfirm.getText().toString().trim();
    if (confirm.isEmpty()) {
      etConfirm.setError("User password field cannot be blank");
      validUserInfo = false;
    } else if (!confirm.equals(password)) {
      etConfirm.setError("Password does not match");
      validUserInfo = false;
    }
    // check if edit text inputs are valid
    if (validUserInfo) {
      int roleId = RolesEnumMap.getRoleId(userRole);
      int userId = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
      // check if user was properly written to database
      String toastMsg;
      if (userId != DatabaseValidHelper.INVALID_ID) {
        toastMsg = "User with ID " + userId + " has been created.";
        if (bundle.getBoolean("CREATE_FIRST_ADMIN")) {
          // if first admin was created, send result back to main activity
          setResult(RESULT_OK, new Intent());
        } else if (bundle.getBoolean("TELLER_AUTHENTICATION")) {
          // if teller is creating customer, send result back to authenticate customer
          Intent intent = new Intent();
          intent.putExtra("CUSTOMER_ID", userId);
          intent.putExtra("CUSTOMER_PASSWORD", password);
          setResult(RESULT_OK, intent);
        }
        finish();
      } else {
        toastMsg = "ERROR: cannot write user!";
      }
      Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }
  }

}
