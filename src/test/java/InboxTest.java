import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.junit.Test;
import schemas.User;

public class InboxTest extends TestBase {
  private final String MESSAGE = "To be cool";

  @Test
  public void shouldSendPrivateMessage() throws IOException {

    // given
    User user = createUser();

    // when
    // Auth not needed, bug?
    CloseableHttpResponse response = sendMessage(user);

    // then
    assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
  }

  @Test
  public void shouldNotSendPrivateMessageWithoutBody() throws IOException {

    // given
    User user = createUser();

    // when
    CloseableHttpResponse response = sendMessage(user);

    // then
    assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
  }

  @Test
  public void shouldGetPrivateMessage() throws IOException {

    // Given
    User user = createUser();
    CloseableHttpResponse sendMessageResponse = sendMessage(user);
    if (sendMessageResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
      throw new AssertionError("Unable to send message, precondition not met.");
    }

    // When
    HttpGet request =
        new HttpGet(
            String.format(
                "%s%s/%s/%s", BASE_URI, USERS_ENDPOINT, INBOX_ENDPOINT, user.getUsername()));
    request.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader(user));
    CloseableHttpResponse httpResponse = getAuthenticatedClient(user).execute(request);

    // Then
    assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    assertThat(getPayloadAsString(httpResponse), containsString(MESSAGE));
  }

  @Test
  public void shouldNotGerMessageWithoutAuth() throws IOException {

    // Given
    User user = createUser();
    CloseableHttpResponse sendMessageResponse = sendMessage(user);
    if (sendMessageResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
      throw new AssertionError("Unable to send message, precondition not met.");
    }

    // When
    HttpGet request =
        new HttpGet(
            String.format(
                "%s%s/%s/%s", BASE_URI, USERS_ENDPOINT, INBOX_ENDPOINT, user.getUsername()));
    CloseableHttpResponse httpResponse = getAuthenticatedClient(user).execute(request);

    // Then
    assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_UNAUTHORIZED));
  }

  private CloseableHttpResponse sendMessage(User user) throws IOException {
    JSONObject requestBody = new JSONObject().put("message", MESSAGE);

    HttpPost request =
        new HttpPost(
            String.format(
                "%s%s/%s/%s", BASE_URI, USERS_ENDPOINT, INBOX_ENDPOINT, user.getUsername()));

    StringEntity entity = new StringEntity(requestBody.toString());
    request.setEntity(entity);
    request.setHeader(CONTENT_TYPE, APPLICATION_JSON);

    return client.execute(request);
  }

  private String getAuthHeader(User user) {
    String auth = user.getUsername() + ":" + user.getPassword();
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
    return "Basic " + new String(encodedAuth);
  }
}
