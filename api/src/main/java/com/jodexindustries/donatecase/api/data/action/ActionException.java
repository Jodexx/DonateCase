package com.jodexindustries.donatecase.api.data.action;

public class ActionException extends RuntimeException {

  public ActionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ActionException(String message) {
    super(message);
  }

}
