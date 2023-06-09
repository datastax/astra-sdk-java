package com.datastax.astra.sdk;

import com.datastax.astra.sdk.todos.TodosSimpleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.datastax.astra.sdk.AstraClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SampleTest {

    @Autowired
    TodosSimpleRepository todoRepository;

    @Autowired
    AstraClient astraClient;

    @Test
    public void demoAstraClient() {
        System.out.println("Connected to " + astraClient.apiDevops().getOrganization().getName());
    }

    @Test
    public void listTodos() {
        Assertions.assertTrue(todoRepository.findAll().size() > 0);
    }

}
