package foo.verify400withemptybody;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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
    FooDTO foo = new FooDTO().setFooId("foo dto from unit test").setUserId("user-test-00");
    webTestClient.post()
        .uri(FOO_URL)
        .body(Mono.just(foo), FooDTO.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(FooDTO.class)
        .returnResult();
  }

  @Test
  public void badRequestTest() {
    webTestClient.post()
        .uri(FOO_URL )
        .body(Mono.empty(), FooDTO.class)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(Void.class)
        .returnResult();
  }
}
