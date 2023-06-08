package com.datastax.astra.sdk.web;

import com.datastax.astra.sdk.data.Fruit;
import com.datastax.astra.sdk.data.FruitRepository;
import com.datastax.astra.sdk.data.FruitSimpleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Fruit Controller.
 */
@RestController
public class FruitController {

    /** Inject repository. */
    private final FruitRepository fruitRepository;

    /** Inject repository. */
    private final FruitSimpleRepository fruitSimpleRepository;

    /**
     * Inject repository.
     *
     * @param fruitRepository
     *      current repository
     * @param fruitSimpleRepository
     *      current repository
     */
    public FruitController(FruitRepository fruitRepository, FruitSimpleRepository fruitSimpleRepository) {
        this.fruitRepository = fruitRepository;
        this.fruitSimpleRepository = fruitSimpleRepository;
    }

    /**
     * Add fruits.
     */
    @PostConstruct
    public void setup() {
        fruitRepository.save(new Fruit("Apple", 0.99));
        fruitRepository.save(new Fruit("Orange", 1.29));
        fruitRepository.save(new Fruit("Banana", 0.59));
        fruitRepository.save(new Fruit("Pineapple", 2.99));
        fruitRepository.save(new Fruit("Kiwi", 0.99));
    }

    /**
     * Find all fruits.
     *
     * @return list of fruits
     */
    @GetMapping("/fruits/")
    public List<Fruit> findAll() {
        return fruitRepository.findAll();
    }

    /**
     * Find a fruit.
     *
     * @param name
     *      name of the fruit
     * @param req
     *      input http response
     * @return fruit if exists
     */
    @GetMapping("/fruits/{name}")
    public ResponseEntity<Fruit> findById(HttpServletRequest req, @PathVariable(value = "name") String name) {
        return fruitRepository
                .findById(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Find a fruit.
     *
     * @param req
     *      source reauest to get URL
     * @param fruit
     *      fruit to update
     * @throws URISyntaxException
     *      error when building URL
     * @return fruit if exists
     *
     */
    @PostMapping("/fruits/")
    public ResponseEntity<Fruit> create(HttpServletRequest req, @RequestBody Fruit fruit)
    throws URISyntaxException {
        fruitRepository.save(fruit);
        return ResponseEntity
                .created(new URI(req.getRequestURI() + "/" + fruit.getName()))
                .body(fruit);
    }

    /**
     * Update a fruit.
     *
     * @param name
     *      fruit name
     * @param fruit
     *      name value for fruit.
     * @return
     *      updated fruit
     */
    @PatchMapping("/fruits/{name}")
    public ResponseEntity<Fruit> update(@PathVariable(value = "name") String name, @RequestBody Fruit fruit) {
        fruitRepository.save(fruit);
        return ResponseEntity.accepted().body(fruit);
    }

    /**
     * Delete a fruit.
     *
     * @param name
     *      name of the fruit
     * @return empty response
     */
    @DeleteMapping("/fruits/{name}")
    public ResponseEntity<Void> deleteById(@PathVariable(value = "name") String name) {
        return fruitRepository.findById(name).map(fruit -> {
            fruitRepository.delete(fruit);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete all fruits.
     *
     * @return empty response
     */
    @DeleteMapping("/fruits/")
    public ResponseEntity<Void> deleteAll(HttpServletRequest request) {
        fruitRepository.deleteAll();
        return ResponseEntity.noContent().<Void>build();
    }

}
