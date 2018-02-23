package com.bank.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bank.database.android.DatabaseDriverA;
import com.bank.databasehelper.DatabaseDriverHelper;
import com.bank.security.PasswordHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseDeserializeUserInfo extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "bank.db";

  public DatabaseDeserializeUserInfo(Context context) {
    super(context, DATABASE_NAME, null, 1);

  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("CREATE TABLE ROLES "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL)");
    sqLiteDatabase.execSQL("CREATE TABLE ACCOUNTTYPES "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL,"
            + "INTERESTRATE TEXT)");
    sqLiteDatabase.execSQL("CREATE TABLE ACCOUNTS "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL,"
            + "BALANCE TEXT,"
            + "TYPE INTEGER NOT NULL,"
            + "FOREIGN KEY(TYPE) REFERENCES ACCOUNTTYPES(ID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERS "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL,"
            + "AGE INTEGER NOT NULL,"
            + "ADDRESS CHAR(100),"
            + "ROLEID INTEGER,"
            + "FOREIGN KEY(ROLEID) REFERENCES ROLE(ID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERACCOUNT "
            + "(USERID INTEGER NOT NULL,"
            + "ACCOUNTID INTEGER NOT NULL,"
            + "FOREIGN KEY(USERID) REFERENCES USER(ID),"
            + "FOREIGN KEY(ACCOUNTID) REFERENCES ACOUNT(ID),"
            + "PRIMARY KEY(USERID, ACCOUNTID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERPW "
            + "(USERID INTEGER NOT NULL,"
            + "PASSWORD CHAR(64),"
            + "FOREIGN KEY(USERID) REFERENCES USER(ID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERMESSAGES "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "USERID INTEGER NOT NULL,"
            + "MESSAGE CHAR(512) NOT NULL,"
            + "VIEWED CHAR(1) NOT NULL,"
            + "FOREIGN KEY(USERID) REFERENCES USER(ID))");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERMESSAGES");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERPW");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERACCOUNT");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERS");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNTS");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNTTYPES");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ROLES");

    onCreate(sqLiteDatabase);
  }

  /**
   * Connects and inserts a User into the database with the corresponding info and a hashed
   * password. Returns the User Id if the insertion was successful, otherwise returns -1.
   *
   * @param name           is the name of the user
   * @param age            is the age of the user
   * @param address        is the address of the user
   * @param roleId         is the roleId of the user
   * @param hashedPassword is the hashed password of the user
   * @return the id of the user that was inserted
   */
  protected long insertUserHashed(String name, int age, String address, int roleId,
                                  String hashedPassword) {
    long id = insertUser(name, age, address, roleId);
    insertPassword(hashedPassword, (int) id);
    return id;
  }

  private void insertPassword(String hashedPassword, int userId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();

    contentValues.put("USERID", userId);
    contentValues.put("PASSWORD", hashedPassword);
    sqLiteDatabase.insert("USERPW", null, contentValues);
  }

  private long insertUser(String name, int age, String address, int roleId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    contentValues.put("AGE", age);
    contentValues.put("ADDRESS", address);
    contentValues.put("ROLEID", roleId);

    return sqLiteDatabase.insert("USERS", null, contentValues);
  }
}
