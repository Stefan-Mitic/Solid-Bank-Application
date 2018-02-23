package com.bank.bankapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.DatabaseValidHelper;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesEnumMap;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An admin updates the new interest rate for a selected account type.
 */
public class SetInterestRateActivity extends AppCompatActivity {

  int typeId;
  AccountTypes accountType;
  Spinner spnAccountType;
  EditText etInterestRate;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    // set views from layout
    etInterestRate = (EditText) findViewById(R.id.set_interest_rate_et_interest_rate);
    spnAccountType = (Spinner) findViewById(R.id.set_interest_rate_spn_type_selection);
    // populate account type spinner
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
    // set the edit text to the current interest rate of selected account type
    spnAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String typeName = parent.getItemAtPosition(position).toString();
        accountType = AccountTypes.valueOf(typeName);
        typeId = AccountTypesEnumMap.getAccountTypeId(accountType);
        BigDecimal currInterestRate = DatabaseSelectHelper.getInterestRate(typeId);
        String strInterestRate = currInterestRate.toString();
        etInterestRate.setText(strInterestRate);
      }
      public void onNothingSelected(AdapterView<?> parent) {
        /* do nothing here */
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_set_interest_rate);
    setViewsAndAttributes();
  }

  /**
   * Submits the new interest rate to the database, if valid.
   *
   * @param view a button that calls this method.
   */
  public void submitInterestRate(View view) {
    boolean validRate = true;
    // check if interest rate is valid
    BigDecimal interestRate = BigDecimal.ZERO;
    String strInterestRate = etInterestRate.getText().toString();
    if (strInterestRate.isEmpty()) {
      etInterestRate.setError("Field cannot be empty.");
      validRate = false;
    } else {
      interestRate = (new BigDecimal(strInterestRate)).setScale(2, RoundingMode.CEILING);
      if (!DatabaseValidHelper.validInterestRate(interestRate)) {
        etInterestRate.setError("Interest rate must a decimal value between 0 and 1.");
        validRate = false;
      }
    }
    // check if user input is valid
    if (validRate) {
      String toastMsg;
      if (DatabaseUpdateHelper.updateAccountTypeInterestRate(interestRate, typeId)) {
        toastMsg = "Interest rate for " + accountType.toString() + " has been set to ";
        toastMsg += interestRate.toString() + ".";
      } else {
        toastMsg = "Something went wrong with updating account type interest rate.";
      }
      Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
      finish();
    }
  }

}
