package com.datastax.astra.sdk.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Index Controller.
 */
@Controller
public class IndexController {

    /**
     * Index operation.
     *
     * @return index.html
     */
    @GetMapping("/")
    void handleFoo(HttpServletResponse response) throws IOException {
        response.sendRedirect("/fruits/");
    }


}