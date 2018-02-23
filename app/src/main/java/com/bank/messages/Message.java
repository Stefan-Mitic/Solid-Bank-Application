package com.bank.messages;

import java.io.Serializable;

public class Message implements Serializable {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = 5678788740737547142L;

  /**
   * The unique ID number of this message.
   */
  private int id;

  /**
   * The ID of the recipient this message was intended for.
   */
  private int userId;

  /**
   * The actual message itself.
   */
  private String message;

  /**
   * Whether or not this message has been viewed by the recipient.
   */
  private boolean viewed;

  /**
   * Creates a message object.
   * 
   * @param id the ID of this message
   * @param userId the ID of the recipient user
   * @param message the content of the message
   * @param viewed whether or this message has been viewed by the recipient
   */
  public Message(int id, int userId, String message, boolean viewed) {
    this.id = id;
    this.userId = userId;
    this.message = message;
    this.viewed = viewed;
  }

  /**
   * Returns the ID number of this message.
   * 
   * @return the ID of this message
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the ID number of the recipient user this message was intended for.
   * 
   * @return the ID of the recipient user
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Returns the content of the message itself.
   * 
   * @return the message itself
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns whether or not this message has been viewed by recipient.
   * 
   * @return <code>true</code> if this message has been viewed
   */
  public boolean isViewed() {
    return viewed;
  }

  /**
   * Sets whether or not this message has been viewed by recipient.
   * 
   * @param viewed whether or not this message is now viewed
   */
  public void setViewed(boolean viewed) {
    this.viewed = viewed;
  }

}
