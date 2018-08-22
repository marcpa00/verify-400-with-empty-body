package foo.verify400withemptybody;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class FooControllerTest {
  private final static String FOO_URL = "/foos";

  @Autowired
  private WebTestClient webTestClient;

  @Before
  public void setup() {
    webTestClient = webTestClient.mutate()
        .responseTimeout(Duration.ofMillis(1000))
        .build();
  }

  @Test
  public void createTest() {
    String foo = "foo string from unit test";
    EntityExchangeResult entityExchangeResult = webTestClient.post()
        .uri(FOO_URL)
        .body(Mono.just(foo), String.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .isEmpty();

    EntityExchangeResult<String> fooEntityExchangeResult = webTestClient.get()
        .uri(entityExchangeResult.getResponseHeaders().getLocation())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .returnResult();

    String fooFromResponse = fooEntityExchangeResult.getResponseBody();

    assert foo.equals(fooFromResponse);
  }

  @Test
  public void badRequestTest() {
    webTestClient.post()
        .uri(FOO_URL )
        .body(Mono.empty(), String.class)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(Void.class)
        .returnResult();
  }
}
