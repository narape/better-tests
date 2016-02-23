package com.pettermahlen.login;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.test.StubClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.pettermahlen.login.CommonUtilities.jsonRepresentation;
import static com.spotify.apollo.test.unit.RequestMatchers.uri;
import static okio.ByteString.encodeUtf8;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertEquals;

public class RemoteUserStoreTest {

  private StubClient client;
  private RemoteUserStore store;

  @Before
  public void setUp() throws Exception {
    client = new StubClient();
    store = new RemoteUserStore(client);
  }

  @Test
  public void shouldConvertRemoteResponseToUser() throws Exception {
    // Arrange
    client.respond(Response.forPayload(encodeUtf8(jsonRepresentation("matti", "pwd"))))
          .to(uri(endsWith("matti")));

    // Act
    Optional<User> user = store.findByName("matti");

    // Assert
    assertEquals(Optional.of(User.create("matti", "pwd")), user);
  }

  @Test
  public void shouldConvert404ToEmpty() throws Exception {
    client.respond(Response.forStatus(Status.NOT_FOUND))
           .to(uri(endsWith("landen")));

    Optional<User> user = store.findByName("landen");

    assertEquals(Optional.empty(), user);
  }

  @Test
  public void shouldReturnEmptyForOtherCodes() throws Exception {
    client.respond(Response.forStatus(Status.IM_A_TEAPOT))
          .to(any(Request.class));

    Optional<User> user = store.findByName("teapod?");

    assertEquals(Optional.empty(), user);
  }

}
