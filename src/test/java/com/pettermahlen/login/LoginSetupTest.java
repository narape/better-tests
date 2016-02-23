package com.pettermahlen.login;

import com.spotify.apollo.Environment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.spotify.apollo.test.unit.RouteMatchers.hasUriAndMethod;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginSetupTest {

  @Mock
  private Environment env;

  @Mock
  private Environment.RoutingEngine router;

  @Test @SuppressWarnings("unchecked")
  public void initShouldRouteService() {
    when(env.routingEngine()).thenReturn(router);

    LoginSetup.init(env);

    verify(router).registerAutoRoute(argThat(hasUriAndMethod("GET", "/login")));
  }

}