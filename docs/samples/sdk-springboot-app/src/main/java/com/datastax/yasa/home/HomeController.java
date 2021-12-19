package com.datastax.yasa.home;

import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.datastax.yasa.docapi.person.PersonRepository;

/**
 * Home Controller, we want to show the gate.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Controller
public class HomeController {
    
    /** Data Access. */
    private final PersonRepository personStargateRepository;
    
    /**
     * Injection with Constructor.
     */
    public HomeController(PersonRepository homeRepository) {
		this.personStargateRepository = homeRepository;
	}
    
	@GetMapping("/home")
    public String show(Model model) 
    throws Exception {
        model.addAttribute("persons", personStargateRepository.findAll().collect(Collectors.toList()));
        return "home";
    }
}
