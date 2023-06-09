#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Index Controller.
 */
@Controller
public class IndexController {

    /**
     * Redirect to APIS.
     */
    @GetMapping("/")
    void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/todos/");
    }

}