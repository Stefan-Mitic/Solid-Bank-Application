package com.bank.bankapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseDriverHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.generics.Roles;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

/**
 * User inputs their credentials and logs into to their respective terminal.
 */
public class LoginActivity extends AppCompatActivity {

  private static final int LOGOUT_REQUEST = 1;

  Bundle bundle;
  Roles userRole;
  TextView tvTitle;
  EditText etId;
  EditText etPassword;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    tvTitle = (TextView) findViewById(R.id.login_tv_title);
    String roleName = userRole.toString();
    tvTitle.setText(roleName.substring(0, 1) + roleName.substring(1).toLowerCase() + " Login");
    etId = (EditText) findViewById(R.id.login_et_id);
    etPassword = (EditText) findViewById(R.id.login_et_password);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    DatabaseDriverHelper.getDatabaseDriver(this).close();
    bundle = getIntent().getExtras();
    userRole = Roles.valueOf(bundle.getString("ROLE"));
    setViewsAndAttributes();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if user has logged out, go back to start activity
    if (requestCode == LOGOUT_REQUEST && resultCode == RESULT_OK) {
      finish();
    }
  }

  /**
   * Submits login information, and logs user in if valid credentials.
   *
   * @param view a button view that calls this method
   */
  public void submitLoginInfo(View view) {
    String strId = etId.getText().toString().trim();
    String password = etPassword.getText().toString();

    boolean validLoginInfo = true;
    if (strId.isEmpty()) {
      etId.setError("Field cannot be empty.");
      validLoginInfo = false;
    }
    if (password.isEmpty()) {
      etPassword.setError("Field cannot be empty.");
      validLoginInfo = false;
    }
    if (validLoginInfo) {
      int userId;
      try {
        userId = Integer.parseInt(strId);
      } catch (NumberFormatException e) {
        userId = DatabaseValidHelper.INVALID_ID;
      }
      String toastMsg = "";
      // check if user ID is of correct userRole
      boolean correctRole = false;
      User selectUser = DatabaseSelectHelper.getUserDetails(userId);
      switch (userRole) {
        case ADMIN: // check if user is of admin
          if (!(correctRole = selectUser instanceof Admin)) {
            toastMsg = "User ID is not of an admin.";
          }
          break;
        case TELLER: // check if user is of teller
          if (!(correctRole = selectUser instanceof Teller)) {
            toastMsg = "User ID if not of a teller.";
          }
          break;
        case CUSTOMER: // check if user is of customer
          if (!(correctRole = selectUser instanceof Customer)) {
            toastMsg = "User ID is not of a customer.";
          }
          break;
      }

      // check if user inputted correct password
      if (correctRole) {
        if (selectUser.authenticate(password)) {
          toastMsg = "User login successful!";
          Intent intent;
          switch (userRole) {
            case ADMIN:
              intent = new Intent(this, AdminMenuActivity.class);
              intent.putExtra("USERID", userId);
              intent.putExtra("PASSWORD", password);
              startActivityForResult(intent, LOGOUT_REQUEST);
              break;
            case TELLER:
              intent = new Intent(this, TellerMenuActivity.class);
              intent.putExtra("USERID", userId);
              intent.putExtra("PASSWORD", password);
              startActivityForResult(intent, LOGOUT_REQUEST);
              break;
            case CUSTOMER:
              if (bundle.getBoolean("TELLER_AUTHENTICATION")) {
                intent = new Intent();
                intent.putExtra("CUSTOMER_ID", userId);
                intent.putExtra("CUSTOMER_PASSWORD", password);
                setResult(RESULT_OK, intent);
                finish();
              } else {
                intent = new Intent(this, AtmMenuActivity.class);
                intent.putExtra("USERID", userId);
                intent.putExtra("PASSWORD", password);
                startActivityForResult(intent, LOGOUT_REQUEST);
              }
              break;
          }
        } else {
          toastMsg = "Incorrect password.";
        }
        finish();
      }
      // display toast on the screen
      Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

  }

}
