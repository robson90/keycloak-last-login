package de.robinmeese.keycloak.events;


import dasniko.testcontainers.keycloak.KeycloakContainer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.TokenUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
class LastLoginListenerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(LastLoginListenerTest.class);
  private static final String REALM_NAME = "test-realm";
  private static final String CLIENT_NAME = "direct-access-grant-client";
  private static final String USERNAME_DIETER = "hansdieterbohlen";
  private static final String USERNAME_PETER = "peterpommes";
  private static final String DEFAULT_USER_PASSWORD = "thisIsJustForDemonstrationDoNotCopyMe";
  private static final String LAST_LOGIN_ATTRIBUTE_KEY = "lastLogin";
  private final TokenUtil tokenUtil = new TokenUtil();
  private Keycloak admin;
  private static final String KEYCLOAK_VERSION = System.getProperty("keycloak.version", "latest");


  @Container
  private final KeycloakContainer keycloak =
      new KeycloakContainer("quay.io/keycloak/keycloak:" + KEYCLOAK_VERSION)
          .withEnv("KC_SPI_EVENTS_LISTENER_LAST_LOGIN_ATTRIBUTE_NAME", LAST_LOGIN_ATTRIBUTE_KEY)
          .withEnv("KEYCLOAK_ADMIN", "admin")
          .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
          .withProviderClassesFrom("target/classes")
          .withRealmImportFile("/realm-imports/test-realm-realm.json");

  @BeforeEach
  public void setUp() {
    admin = keycloak.getKeycloakAdminClient();
    var realm = admin.realm(REALM_NAME);
    var upconfig = realm.users().userProfile().getConfiguration();
    upconfig.setUnmanagedAttributePolicy(UPConfig.UnmanagedAttributePolicy.ENABLED);
    realm.users().userProfile().update(upconfig);
  }

  @Test
  void testThatLoginAttributeIsWrittenAndAlsoUpdated() throws InterruptedException {
    // check user has no attributes
    List<UserRepresentation> users = admin.realm(REALM_NAME).users().searchByUsername(USERNAME_DIETER, true);
    UserRepresentation testUser = users.get(0);
    Map<String, List<String>> attributes = testUser.getAttributes();
    assertNull(attributes);

    // "login" user
    tokenUtil.requestToken(keycloak, REALM_NAME, USERNAME_DIETER, DEFAULT_USER_PASSWORD, CLIENT_NAME);

    // check user has last-login-time attribute
    testUser = admin.realm(REALM_NAME).users().searchByUsername(USERNAME_DIETER, true).get(0);
    String firstLoginTimeStamp = testUser.firstAttribute(LAST_LOGIN_ATTRIBUTE_KEY);
    assertNotNull(firstLoginTimeStamp);

    // Wait for one second -> Timestamp is in seconds
    TimeUnit.SECONDS.sleep(1);

    // Login Second time
    tokenUtil.requestToken(keycloak, REALM_NAME, USERNAME_DIETER, DEFAULT_USER_PASSWORD);

    //  check if new value is after first login
    testUser = admin.realm(REALM_NAME).users().searchByUsername(USERNAME_DIETER, true).get(0);
    String secondLoginTimeStamp = testUser.firstAttribute(LAST_LOGIN_ATTRIBUTE_KEY);
    assertThat(firstLoginTimeStamp).isLessThan(secondLoginTimeStamp);
  }
}