package com.yoruhana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(){
        return "main/loading";
    }

    @GetMapping("/index.do")
    public String index(HttpServletRequest request){

       //String country = request.getParameter("country");

        HttpSession session = request.getSession();
        //-------------일본 코드 테스트
        String country="KR";
        //-------------
        session.setAttribute("lang",country);

       request.setAttribute("country",country);

        return "main/index";
    }

    @GetMapping("/require.do")
    public String require(HttpServletRequest request){
        String msg = "";

        HttpSession session = request.getSession();

        String lang = (String)session.getAttribute("lang");

        if(lang.equals("KR")){
            msg = "로그인이 필요한 서비스 입니다. 로그인 페이지로 이동합니다.";
        } else if(lang.equals("JP")) {
            msg = "ログインが必要なサービスです。 ログインページに移動します。";
        }

        String url="/loginForm.do";

        request.setAttribute("msg",msg);
        request.setAttribute("url",url);

        return "common/result";
    }
}
