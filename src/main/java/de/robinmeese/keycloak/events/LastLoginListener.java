package de.robinmeese.keycloak.events;

import org.keycloak.common.util.Time;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

public class LastLoginListener implements EventListenerProvider {

  private final KeycloakSession session;

  public LastLoginListener(KeycloakSession session) {
    this.session = session;
  }

  @Override
  public void onEvent(Event event) {
    if (event.getType().equals(EventType.LOGIN)) {
      UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
      if (user != null) {
        user.setSingleAttribute(LastLoginListenerFactory.attributeName, Integer.toString(Time.currentTime()));
      }
    }
  }

  @Override
  public void onEvent(AdminEvent event, boolean includeRepresentation) {
    //TODO check if I want to overwrite this too
  }

  @Override
  public void close() {
  }

}
