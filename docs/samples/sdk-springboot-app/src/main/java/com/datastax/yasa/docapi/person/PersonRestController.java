package com.datastax.yasa.docapi.person;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.stargate.sdk.doc.Document;

@RestController
@CrossOrigin(
        methods = {POST, GET, OPTIONS, PUT, DELETE, PATCH},
        maxAge = 3600,
        allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
        origins = "*" 
      )
@RequestMapping("/api/documents/persons/")
public class PersonRestController {
    
    private final PersonRepository repo;
    
    public PersonRestController(PersonRepository repo) {
        this.repo = repo;
    }
    
    @GetMapping
    public List<Document<Person>> findAll() {
        return repo.findAll().collect(Collectors.toList());
    }

}
