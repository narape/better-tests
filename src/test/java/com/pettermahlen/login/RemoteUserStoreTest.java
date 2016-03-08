package com.pettermahlen.login;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.test.StubClient;
import okio.ByteString;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static com.pettermahlen.login.CommonUtilities.jsonRepresentation;
import static com.spotify.apollo.test.unit.RequestMatchers.uri;
import static okio.ByteString.encodeUtf8;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

  @Test
  public void shouldPropagateExceptions_flavor1() throws Exception {
    client.respond(Response.forPayload(ByteString.encodeUtf8("bad json")))
          .to(any(Request.class));

    try {
      store.findByName("random-user");
      fail("Exception expected");
    } catch (RuntimeException e) {
      // or we could insert assertions about the exceptions
    }
  }

  @Test(expected = RuntimeException.class)
  public void shouldPropagateExceptions_flavor2() throws Exception {
    client.respond(Response.forPayload(ByteString.encodeUtf8("bad json")))
        .to(any(Request.class));

    store.findByName("random-user");
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test()
  public void shouldPropagateExceptions_flavor3() throws Exception {
    client.respond(Response.forPayload(ByteString.encodeUtf8("bad json")))
        .to(any(Request.class));
    thrown.expect(RuntimeException.class);

    store.findByName("random-user");
  }
}
