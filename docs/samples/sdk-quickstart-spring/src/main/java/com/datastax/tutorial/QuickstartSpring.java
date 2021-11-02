package com.datastax.tutorial;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.astra.sdk.AstraClient;

@RestController
@SpringBootApplication
public class QuickstartSpring {

	public static void main(String[] args) {
		SpringApplication.run(QuickstartSpring.class, args);
	}
	
	@Autowired
	private AstraClient astraClient;
	
	@GetMapping("/")
	public String hello() {
	    return astraClient.apiDevopsOrganizations().organizationId(); 
	}
	
	@Autowired
    private TodosRepository todoRepository;
    
    @PostConstruct
    public void insertTodos() {
        todoRepository.save(new Todos("Create Spring Project"));
        todoRepository.save(new Todos("Setup Astra Starter"));
        todoRepository.save(new Todos("Setup Spring Starter"));
    }
    
	@GetMapping("/todos")
    public List<Todos> todos() {
        return todoRepository.findAll(CassandraPageRequest.first(10)).toList();
    }
	
	@Autowired
    private CassandraTemplate cassandraTemplate;
	
	@GetMapping("/datacenter")
	public String datacenter() {
	    return cassandraTemplate.getCqlOperations().queryForObject("SELECT data_center FROM system.local", String.class);
	}
	

}
