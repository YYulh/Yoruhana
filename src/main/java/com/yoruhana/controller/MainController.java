package com.yoruhana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(){
        return "main/index";
    }
    @GetMapping("/login.do")
    public String login(Model model){
        String a = "바보";

        model.addAttribute("a",a);
        return "main/index";
    }
}
