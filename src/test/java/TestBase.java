import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.After;
import schemas.User;

public class TestBase {

  public static final String BASE_URI = "https://api-testing-conference.herokuapp.com/v1.0/";
  public CloseableHttpClient client = HttpClients.createDefault();
  public final String INBOX_ENDPOINT = "inbox";
  public final String USERS_ENDPOINT = "users";
  public final String FORUM_ENDPOINT = "forum";
  public final String CONTENT_TYPE = "Content-type";
  public final String APPLICATION_JSON = "application/json";

  @After
  public void stopClient() throws IOException {
    client.close();
  }

  public CloseableHttpClient getAuthenticatedClient(User user) {
    return HttpClientBuilder.create()
        .setDefaultCredentialsProvider(getAuthenticatedProvider(user))
        .build();
  }

  public CloseableHttpResponse createUser(User user) throws IOException {
    HttpPost request = new HttpPost(String.format("%s%s", BASE_URI, USERS_ENDPOINT));

    StringEntity entity = new StringEntity(new JSONObject(user).toString());
    request.setEntity(entity);
    request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
    return client.execute(request);
  }

  public User createUser() throws IOException {

    User user = new User();
    user.setName("Pepe Grillo");
    user.setEmail("as@as.es");
    user.setPassword("pinocho");
    user.setUsername(generateUsername());
    user.setRole("QA");

    if ((createUser(user).getStatusLine().getStatusCode() != HttpStatus.SC_OK)) {
      throw new AssertionError("Unable to create user, precondition not met.");
    }
    return user;
  }

  public String generateUsername() {
    Date today = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    return String.format("%s%s%s", "test_user", dateFormat.format(today), randomString());
  }

  private String randomString() {
    SecureRandom random = new SecureRandom();
    return new BigInteger(5 * 5, random).toString(32);
  }

  private CredentialsProvider getAuthenticatedProvider(User user) {
    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(user.getUsername(), user.getPassword()));
    return provider;
  }

  public String getPayloadAsString(CloseableHttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    return EntityUtils.toString(entity, StandardCharsets.UTF_8);
  }
}
