#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to start the Spring Boot application.
 */
@SpringBootApplication
public class SampleSpringApplication {

    /**
     * Main operation.
     *
     * @param args
     *      command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SampleSpringApplication.class, args);
    }

}
