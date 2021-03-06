package com.pettermahlen.login;

import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;

/**
 * Starts up the service
 */
public class ServiceRunner {

  public static final String SERVICE_NAME = "login";

  public static void main(String[] args) throws LoadingException {
    HttpService.boot(LoginSetup::init, SERVICE_NAME, args);
  }
}
