import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import schemas.User;

public class UsersTest extends TestBase {

  @Test
  public void shouldReturnUsers() throws IOException {

    // Given
    // Needed in case db is empty
    createUser();

    // When
    HttpUriRequest request = new HttpGet(String.format("%s%s", BASE_URI, USERS_ENDPOINT));
    CloseableHttpResponse httpResponse = client.execute(request);

    // Then
    assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    assertThat(getPayloadAsString(httpResponse), containsString("users"));
  }

  @Test
  public void shouldCreateUser() throws IOException {
    // Assert located on method
    createUser();
  }

  @Test
  public void shouldNotCreateUserWithMissingParameters() throws IOException {

    // Given
    User user = new User();
    user.setName("Pepe Grillo");
    user.setEmail("as@as.es");
    user.setPassword("pinocho");
    user.setUsername(generateUsername());

    // When
    CloseableHttpResponse response = createUser(user);

    // Then
    assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }
}
