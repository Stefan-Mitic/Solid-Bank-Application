package com.bank.bankapplication;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseDriverHelper;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.generics.Roles;
import com.bank.generics.RolesEnumMap;

import java.math.BigDecimal;

/**
 * First activity to be loaded. User chooses the option of logging in, or creating a first admin
 * if this was the app's first time opening.
 *
 * <!===== WORKS CITED (for code not taught in class) =====>
 * - Android naming convention:
 *    https://stackoverflow.com/questions/12870537/
 * - createActivityForResult, setResult, onActivityResult:
 *    https://stackoverflow.com/questions/920306/
 * - Spinners, ListViews, ArrayAdapter, setOnItemClickListener
 *    https://www.youtube.com/watch?v=A-_hKWMA7mk
 *    https://stackoverflow.com/questions/4508979/
 * - Use of Spinners and Maps (HashMap):
 *    https://stackoverflow.com/questions/24712540/
 * - AlertDialogs and Popup messages:
 *    https://stackoverflow.com/questions/36747369/
 */
public class StartActivity extends AppCompatActivity {

  private static final int CREATE_FIRST_ADMIN_REQUEST = 1;

  TextView tvTitle;
  Button btnFirstAdmin;
  Button btnAdminLogin;
  Button btnTellerLogin;
  Button btnCustomerLogin;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    tvTitle = (TextView) findViewById(R.id.start_menu_tv_title);
    btnFirstAdmin = (Button) findViewById(R.id.start_menu_btn_create_first_admin);
    btnAdminLogin = (Button) findViewById(R.id.start_menu_btn_access_admin_login);
    btnTellerLogin = (Button) findViewById(R.id.start_menu_btn_access_teller_login);
    btnCustomerLogin = (Button) findViewById(R.id.start_menu_btn_access_atm_login);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if first admin has been created, disable option after
    if (requestCode == CREATE_FIRST_ADMIN_REQUEST && resultCode == RESULT_OK) {
      btnFirstAdmin.setEnabled(false);
      btnAdminLogin.setEnabled(true);
      btnTellerLogin.setEnabled(true);
      btnCustomerLogin.setEnabled(true);
    }
  }

  /**
   * Adds all role enums to the Roles table.
   *
   * @return <code>true</code> if all roles are added to the Roles table.
   */
  private boolean fillRolesTable() {
    boolean done = true;
    for (Roles role : Roles.values()) {
      int roleId = DatabaseInsertHelper.insertRole(role.toString());
      done = done && roleId != DatabaseValidHelper.INVALID_ID;
    }
    return done;
  }

  /**
   * Adds all account type enums to the AccountTypes table.
   *
   * @return <code>true</code> if all account types are added to the AccountTypes table.
   */
  private boolean fillAccountTypesTable() {
    boolean done = true;
    for (AccountTypes type: AccountTypes.values()) {
      int typeId = DatabaseInsertHelper.insertAccountType(type.toString(), BigDecimal.ZERO);
      done = done && typeId != DatabaseValidHelper.INVALID_ID;
    }
    done = done && DatabaseUpdateHelper.updateAccountTypeInterestRate(new BigDecimal("0.01"), 1);
    done = done && DatabaseUpdateHelper.updateAccountTypeInterestRate(new BigDecimal("0.02"), 2);
    done = done && DatabaseUpdateHelper.updateAccountTypeInterestRate(new BigDecimal("0.03"), 3);
    done = done && DatabaseUpdateHelper.updateAccountTypeInterestRate(new BigDecimal("0.04"), 4);
    done = done && DatabaseUpdateHelper.updateAccountTypeInterestRate(new BigDecimal("0.02"), 5);
    return done;
  }

  /**
   * Initializes the database and enables or disables buttons on this activity.
   *
   * @return the database driver
   */
  private DatabaseDriverHelper enableButtonsAndDatabaseDriver() {
    DatabaseDriverHelper driver = DatabaseDriverHelper.getDatabaseDriver(this);
    boolean createNewDatabase = !DatabaseValidHelper.userExists(1);
    // on app startup, get instance of database
    if (createNewDatabase) {
      // since first admin was not created, disable access to login
      btnAdminLogin.setEnabled(false);
      btnTellerLogin.setEnabled(false);
      btnCustomerLogin.setEnabled(false);
      driver = DatabaseDriverHelper.createNewDatabase(this);
      // check if the roles and account types tables are filled in
      boolean emptyRolesTable = DatabaseSelectHelper.getRoles().isEmpty();
      boolean emptyAccountTypesTable = DatabaseSelectHelper.getAccountTypesIds().isEmpty();
      if (emptyRolesTable && emptyAccountTypesTable) {
        if (fillRolesTable() && fillAccountTypesTable()) {
          Toast.makeText(this, "Bank is ready to use.", Toast.LENGTH_LONG).show();
        }
      }
      Toast.makeText(this, "Create a first admin!", Toast.LENGTH_LONG).show();
    } else {
      // if first admin was already created, disable option
      btnFirstAdmin.setEnabled(false);
    }
    RolesEnumMap.update();
    AccountTypesEnumMap.update();
    return driver;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start_menu);
    setViewsAndAttributes();
    DatabaseDriverHelper driver = enableButtonsAndDatabaseDriver();
    driver.close();
  }

  public void createFirstAdmin(View view) {
    Intent intent = new Intent(this, CreateUserActivity.class);
    intent.putExtra("ROLE", Roles.ADMIN.toString());
    intent.putExtra("CREATE_FIRST_ADMIN", true);
    startActivityForResult(intent, CREATE_FIRST_ADMIN_REQUEST);
  }

  public void accessAdminLogin(View view) {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.putExtra("ROLE", Roles.ADMIN.toString());
    startActivity(intent);
  }

  public void accessTellerLogin(View view) {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.putExtra("ROLE", Roles.TELLER.toString());
    startActivity(intent);
  }

  public void accessCustomerLogin(View view) {
    Intent intent = new Intent(this, LoginActivity.class);
    intent.putExtra("ROLE", Roles.CUSTOMER.toString());
    startActivity(intent);
  }

}
