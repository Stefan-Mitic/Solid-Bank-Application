package com.bank.bankapplication;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.messages.Message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A user views a list of items. The list may be clickable.
 */
public class ListViewActivity extends AppCompatActivity {

  /**
   * Maps from message info strings to message IDs.
   */
  Map<String, Integer> messageInfoMap = new HashMap<>();

  /**
   * Maps from account info strings to respective account balances.
   */
  Map<String, BigDecimal> accountInfoBalanceMap = new HashMap<>();

  Bundle bundle;
  ListView lvInformation;
  ListAdapter adapter;

  /**
   * Sets views from layout and attributes from intent.
   */
  private void setViewsAndAttributes() {
    bundle = getIntent().getExtras();
    lvInformation = (ListView) findViewById(R.id.list_information_lv_contents);
  }

  /**
   * Populates the list view with the given array list of strings.
   *
   * @param strList an array list of strings to populate the list view with
   */
  private void populateListView(ArrayList<String> strList) {
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strList);
    lvInformation.setAdapter(adapter);
  }

  /**
   * Converts a list of account objects into an array list of account info strings, and populates
   * the respective account info balance map.
   *
   * @param accounts a list of account objects
   * @return an array list of account info strings
   */
  private ArrayList<String> accountListToStringArray(List<Account> accounts,
                                                     Map<String, BigDecimal> accountBalanceMap) {
    ArrayList<String> accountInfoList = new ArrayList<>();
    for (Account account : accounts) {
      int accountId = account.getId();
      String name = account.getName();
      String typeName = DatabaseSelectHelper.getAccountTypeName(account.getType());
      String info = String.format(Locale.CANADA, "%d - %s\n%s ", accountId, typeName, name);
      accountInfoList.add(info);
      accountBalanceMap.put(info, account.getBalance());
    }
    return accountInfoList;
  }

  /**
   * Converts a list of message objects into an array list of message info strings.
   *
   * @param messages a list of message objects
   * @return an array list of message info strings
   */
  private ArrayList<String> messagesListToStringArray(List<Message> messages) {
    ArrayList<String> messageInfoList = new ArrayList<>();
    for (Message message : messages) {
      int msgId = message.getId();
      String preview = message.getMessage();
      if (preview.length() > 50) {
        preview = preview.substring(0, 47) + "...";
      }
      String viewMarker = message.isViewed() ? "VIEWED" : "NEW";
      String msgInfo = String.format(Locale.CANADA, "ID%d - %s\n%s", msgId, viewMarker, preview);
      messageInfoList.add(msgInfo);
      messageInfoMap.put(msgInfo, msgId);
    }
    return messageInfoList;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_information);
    setViewsAndAttributes();
    ArrayList<String> infoList = new ArrayList<>();
    final int intentOption = bundle.getInt("OPTION");
    switch (intentOption) {
      case IntentOptions.LIST_ACCOUNTS:
        infoList = bundle.getStringArrayList("LIST_ACCOUNT_INFO");
        break;
      case IntentOptions.LIST_USERS:
        infoList = bundle.getStringArrayList("LIST_USER_INFO");
        break;
      case IntentOptions.LIST_MESSAGES:
        int userId = bundle.getInt("USERID");
        List<Message> messages = DatabaseSelectHelper.getAllMessages(userId);
        // create message info list and populate hash map
        infoList = messagesListToStringArray(messages);
        break;
      case IntentOptions.VIEW_BALANCE:
        List<Account> accounts = (ArrayList<Account>) bundle.getSerializable("LIST_ACCOUNTS");
        // create account info list and populate hash map
        infoList = accountListToStringArray(accounts, accountInfoBalanceMap);
        break;
    }
    // populate list with information
    populateListView(infoList);

    // when user clicks on an item, display dialog box
    lvInformation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        String previewKey = String.valueOf(adapterView.getItemAtPosition(i));
        if (intentOption == IntentOptions.LIST_MESSAGES) {
          int messageId = messageInfoMap.get(previewKey);
          String content = DatabaseSelectHelper.getSpecificMessage(messageId);
          // update message to viewed (if not peeking)
          if (!bundle.getBoolean("PEEKING")) {
            DatabaseUpdateHelper.updateUserMessageState(messageId);
          }
          // display message contents in dialog box
          alertDialogBuilder.setTitle("Message Content")
                  .setMessage(content);
        } else if (intentOption == IntentOptions.VIEW_BALANCE) {
          BigDecimal balance = accountInfoBalanceMap.get(previewKey);
          alertDialogBuilder.setTitle("Current Balance")
                  .setMessage("Current Balance: " + balance);
        } else {
          alertDialogBuilder.setMessage("You just clicked on me :D");
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
      }
    });
  }

}
