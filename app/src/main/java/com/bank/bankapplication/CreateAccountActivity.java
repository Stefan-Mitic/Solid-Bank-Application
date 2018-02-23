package com.bank.bankapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.accounts.TaxFreeSavingsAccount;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesEnumMap;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A teller creates an account for the current authenticated customer.
 */
public class CreateAccountActivity extends AppCompatActivity {

  Bundle bundle;
  int customerId;

  TextView tvTitle;
  EditText etName;
  EditText etBalance;
  Spinner spnAccountType;
  Button btnSubmit;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    setTitle("Create New Account");
    tvTitle = (TextView) findViewById(R.id.create_account_tv_title);
    etName = (EditText) findViewById(R.id.create_account_et_name);
    etBalance = (EditText) findViewById(R.id.create_account_et_balance);
    spnAccountType = (Spinner) findViewById(R.id.create_account_spn_type);
    // dynamically build array of role names
    AccountTypes[] accountTypes = AccountTypes.values();
    String[] sTypeArray = new String[accountTypes.length];
    for (int i = 0; i < accountTypes.length; i++) {
      sTypeArray[i] = accountTypes[i].name();
    }
    // create and set array adapter
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
            sTypeArray);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spnAccountType.setAdapter(adapter);
    btnSubmit = (Button) findViewById(R.id.create_account_btn_submit);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);
    bundle = getIntent().getExtras();
    customerId = bundle.getInt("CUSTOMER_ID");
    setViewsAndAttributes();
  }

  /**
   * Write account details to the database.
   *
   * @param view a button view that calls this method
   */
  public void submitAccountInfo(View view) {
    String name = etName.getText().toString().trim();
    String strBalance = etBalance.getText().toString().trim();
    String strType = spnAccountType.getSelectedItem().toString();
    AccountTypes type = AccountTypes.valueOf(strType);
    int typeId = AccountTypesEnumMap.getAccountTypeId(type);
    boolean validAccountInfo = true;

    // check if name is valid
    if (name.isEmpty()) {
      etName.setError("Field cannot be empty.");
      validAccountInfo = false;
    }
    // get and check if balance is valid
    BigDecimal balance = null;
    if (strBalance.isEmpty()) {
      etBalance.setError("Field cannot be empty.");
      validAccountInfo = false;
    } else {
      balance = (new BigDecimal(strBalance)).setScale(2, RoundingMode.CEILING);
      if (typeId == AccountTypesEnumMap.getAccountTypeId(AccountTypes.TFSA)) {
        if (balance.compareTo(TaxFreeSavingsAccount.MIN_BALANCE) == -1) {
          etBalance.setError("TFSA must have a minimum balance of $5000.00.");
          validAccountInfo = false;
        }
      }
    }
    // check if all inputs are valid
    if (validAccountInfo) {
      String toastMsg;
      // check if account being created is balance owing
      if (type.equals(AccountTypes.OWING)) {
        balance = balance.negate();
      }
      // write account to database and associate with current user
      int accountId = DatabaseInsertHelper.insertAccount(name, balance, typeId);
      DatabaseInsertHelper.insertUserAccount(customerId, accountId);
      if (accountId != DatabaseValidHelper.INVALID_ID) {
        toastMsg = "Success! Account with ID " + accountId + " has been created.";
        finish();
      } else {
        toastMsg = "ERROR: something went wrong with writing account.";
      }
      Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }
  }

}
