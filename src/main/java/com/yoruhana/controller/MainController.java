package com.yoruhana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(){
        return "main/loading";
    }

    @GetMapping("/index.do")
    public String index(HttpServletRequest request){

        String country = request.getParameter("country");

       request.setAttribute("country",country);

        return "main/index";
    }

    @GetMapping("/require.do")
    public String require(Model model){
        String msg="로그인이 필요한 서비스 입니다. 회원가입 페이지로 이동합니다.";
        String url="/joinForm.do";

        model.addAttribute("msg",msg);
        model.addAttribute("url",url);

        return "common/result";
    }
}
