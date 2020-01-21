import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.junit.Test;

public class ForumTest extends TestBase {

  private final JSONObject CORRECT_MESSAGE =
      new JSONObject().put("theme", "Testing").put("subject", "test").put("message", "A message");
  private final JSONObject INCORRECT_MESSAGE =
      new JSONObject().put("theme", "WRONG_THEME").put("subject", "test").put("message", "QA");

  @Test
  public void shouldAddMessageInForum() throws IOException {

    // when
    CloseableHttpResponse response = createMessage(CORRECT_MESSAGE);

    // then
    assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
  }

  @Test
  public void shouldNotAddMessageWithWrongTheme() throws IOException {

    // when
    CloseableHttpResponse response = createMessage(INCORRECT_MESSAGE);

    // then
    assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void shouldGetForumMessage() throws IOException, URISyntaxException {

    // Given
    // In case empty DB
    createMessage(CORRECT_MESSAGE);

    // When
    URIBuilder builder = new URIBuilder(String.format("%s%s", BASE_URI, FORUM_ENDPOINT));
    builder.setParameter("theme", "Testing");
    HttpGet request = new HttpGet(builder.build());
    CloseableHttpResponse httpResponse = client.execute(request);

    // Then
    assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    assertThat(
        getPayloadAsString(httpResponse),
        containsString(CORRECT_MESSAGE.get("message").toString()));
  }

  private CloseableHttpResponse createMessage(JSONObject message) throws IOException {
    HttpPost request = new HttpPost(String.format("%s%s", BASE_URI, FORUM_ENDPOINT));
    StringEntity entity = new StringEntity(message.toString());
    request.setEntity(entity);
    request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
    return client.execute(request);
  }
}
