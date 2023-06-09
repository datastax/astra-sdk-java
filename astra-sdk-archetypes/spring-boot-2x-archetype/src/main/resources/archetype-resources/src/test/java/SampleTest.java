#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import ${package}.todos.TodosSimpleRepository;
import com.datastax.astra.sdk.AstraClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
