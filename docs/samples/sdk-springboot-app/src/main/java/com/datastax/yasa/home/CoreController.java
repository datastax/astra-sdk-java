package com.datastax.yasa.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoreController {
    
    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/403")
    public String error403() {
        return "/error/403";
    }
    
    @GetMapping("/404")
    public String error404() {
        return "/error/404";
    }
    
    @GetMapping("/500")
    public String error500() {
        return "/error/500";
    }

}
