package de.robinmeese.keycloak.events;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class LastLoginListenerFactory implements EventListenerProviderFactory {

  public static final String PROVIDER_ID = "last-login";

  static String attributeName;

  @Override
  public EventListenerProvider create(KeycloakSession session) {
    return new LastLoginListener(session);
  }

  @Override
  public void init(Config.Scope config) {
    attributeName = config.get("attribute-name", "lastLogin");
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
  }

  @Override
  public void close() {
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }
}
