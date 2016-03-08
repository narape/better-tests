package com.pettermahlen.login;

class CommonUtilities {

  static String jsonRepresentation(String userName, String password) {
    return String.format("{\"userName\": \"%s\", \"password\": \"%s\"}", userName, password);
  }

}
