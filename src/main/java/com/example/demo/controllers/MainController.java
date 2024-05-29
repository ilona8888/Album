package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/index")
    public String index(Model model){
        model.addAttribute("name","Илона Игоревна");
        return "index";
    }

    @GetMapping("about")
    public String about(Model model){
        model.addAttribute("name","Илона Игоренва");
        return "about";
    }
}
