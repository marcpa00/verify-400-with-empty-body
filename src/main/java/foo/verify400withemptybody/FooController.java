package foo.verify400withemptybody;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebInputException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/foos")
public class FooController {

  private static final Map<UUID, FooDTO> foos = new HashMap<>();

  @ExceptionHandler(value = { ServerWebInputException.class,
                              NullPointerException.class,
                              IllegalStateException.class,
                              IllegalArgumentException.class })
  public @ResponseBody
  ResponseEntity sendBadRequest(Exception exception) {
    System.err.println(exception.getMessage());
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }

  @PostMapping
  public ResponseEntity<FooDTO> createFoo(@RequestBody FooDTO dto) {
    FooDTO result = new FooDTO();
    result.setFooId(dto.getFooId());
    result.setUserId(dto.getUserId());
    UUID uuid = UUID.randomUUID() ;
    foos.put(uuid, result);
    return ResponseEntity.created(URI.create("/foos/" + uuid)).build();
  }

  @GetMapping("/foos/{uuid}")
  public ResponseEntity<FooDTO> getFoo(@PathVariable String uuid) {
    Optional<FooDTO> optionalFooDTO = Optional.ofNullable(foos.get(UUID.fromString(uuid)));

    if (optionalFooDTO.isPresent()) {
      return ResponseEntity.ok(optionalFooDTO.get());
    }
    else {
      return ResponseEntity.notFound().build();
    }
  }

}
