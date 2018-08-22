package foo.verify400withemptybody;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
@RequestMapping("/foos")
public class FooController {

  private static String foos;

  @PostMapping
  public ResponseEntity<String> createFoo(@RequestBody String dto) {
    foos = dto;
    return ResponseEntity.created(URI.create("/foos/0")).build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<String> getFoo(@PathVariable String id) {
    return ResponseEntity.ok(foos);
  }
}
