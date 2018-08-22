package foo.verify400withemptybody;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class FooControllerTest {
  private final static String FOO_URL = "/foos";

  @Rule
  public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  @Autowired
  private WebTestClient webTestClient;

  @Before
  public void setup() {
    webTestClient = webTestClient.mutate()
        .filter(documentationConfiguration(restDocumentation).operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint()))
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
        .consumeWith(document("create-foo"))
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
        .consumeWith(document("bad-request-empty-body"))
        .returnResult();
  }
}
