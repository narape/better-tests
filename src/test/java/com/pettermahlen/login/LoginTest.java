package com.pettermahlen.login;

import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.request.RequestContexts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static com.spotify.apollo.test.unit.ResponseMatchers.hasPayload;
import static com.spotify.apollo.test.unit.ResponseMatchers.hasStatus;
import static com.spotify.apollo.test.unit.StatusTypeMatchers.withCode;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {
  private static final String URI_BASE = "http://localhost/login?";

  private Login login;

  @Mock
  private Client client;

  @Mock
  private UserStore userStore;

  @Before
  public void setUp() throws Exception {
    when(userStore.findByName("petter"))
        .thenReturn(Optional.of(User.create("petter", "super-encrypted")));
    when(userStore.findByName("matti"))
        .thenReturn(Optional.empty());

    login = new Login(userStore);
  }

  // Old school assertions
  @Test
  public void shouldReturnSuccessForCorrectCredentials() throws Exception {
    Response<String> response = login.authenticate(as("petter", "super-encrypted"));

    assertEquals(Status.OK.code(), response.status().code());
    assertEquals(Optional.of("SUCCESS"), response.payload());
  }

  // New school assertions using hamcrest
  @Test
  public void shouldReturnFailureForBadCredentials() throws Exception {
    Response<String> response = login.authenticate(as("petter", "petter"));

    assertThat(response, allOf(hasStatus(withCode(Status.OK)), hasPayload(is("FAILURE"))));
  }

  @Test
  public void shouldReturnFailureForMissingUser() throws Exception {
    Response<String> response = login.authenticate(as("matti", "super-encrypted"));

    assertThat(response, allOf(hasStatus(withCode(Status.OK)), hasPayload(is("FAILURE"))));
  }

  @Test
  public void shouldReturnBadRequestForMissingUsername() throws Exception {
    RequestContext requestContext = RequestContexts.create(
        Request.forUri(URI_BASE + "password=super-encrypted"),
        client,
        ImmutableMap.of());

    Response<String> response = login.authenticate(requestContext);

    assertThat(response, hasStatus(withCode(Status.BAD_REQUEST)));
  }

  @Test
  public void shouldReturnBadRequestForMissingPassword() throws Exception {
    RequestContext requestContext = RequestContexts.create(
        Request.forUri(URI_BASE + "userName=petter"),
        client,
        ImmutableMap.of());

    Response<String> response = login.authenticate(requestContext);

    assertThat(response, hasStatus(withCode(Status.BAD_REQUEST)));
  }

  private RequestContext as(String userName, String password) {
    Request request = Request.forUri(String.format(URI_BASE + "userName=%s&password=%s", userName, password));
    return RequestContexts.create(request, client, ImmutableMap.of());
  }
}
