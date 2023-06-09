#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.todos;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@CrossOrigin(
  methods = {POST, GET, OPTIONS, PUT, DELETE, PATCH},
  maxAge = 3600,
  allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
  origins = "*"
)
@RestController
@RequestMapping("/todos/")
public class TodosRestController {

    private final TodosRepository todosRepository;

    public TodosRestController(TodosRepository todoRepo) {
        this.todosRepository = todoRepo;
    }

    @GetMapping
    public Stream<Todos> findAll() {
        return todosRepository.findAll().stream();
    }

    @PostConstruct
    public void setup() {
        todosRepository.deleteAll();
        todosRepository.save(new Todos("Create a DB In Astra", true));
        todosRepository.save(new Todos("Create Tables for todos", true));
        todosRepository.save(new Todos("Create Spring Boot Application", true));
        todosRepository.save(new Todos("Change the world", false));

    }

    @GetMapping("/{uid}")
    public ResponseEntity<Todos> findById(@PathVariable(value = "uid") UUID uid) {
        return todosRepository
                .findById(uid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todos> create(HttpServletRequest req, @RequestBody Todos todos)
    throws URISyntaxException {
        todosRepository.save(todos);
        return ResponseEntity.created(new URI(req.getRequestURI() + "/" + todos.getUid())).body(todos);
    }

    @PatchMapping("{uid}")
    public ResponseEntity<Todos> update(@PathVariable(value = "uid") UUID uid, @RequestBody Todos todoReq) {
        todoReq.setUid(uid);
        todosRepository.save(todoReq);
        return ResponseEntity.accepted().body(todoReq);
    }

    @DeleteMapping("{uid}")
    public ResponseEntity<Void> deleteById(@PathVariable(value = "uid") UUID uid) {
        return todosRepository
                .findById(uid)
                .map(fruit -> {
                    todosRepository.delete(fruit);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        todosRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}