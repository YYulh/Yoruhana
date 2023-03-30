package com.yoruhana.controller;

import com.yoruhana.entity.MemberVO;
import com.yoruhana.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/join.do")
    public String join(){
        return "member/joinIntro";
    }

    @GetMapping("/joinForm.do")
    public String joinForm(String joinMode) {
        System.out.println(joinMode);
       /* if (joinMode.equals("nomal")) {
            return "member/nomalJoin";
        }*/

    return "member/"+joinMode + "Join";
    }

    @GetMapping("/joinSubmit.do")
    public String joinSubmit(Model model, MemberVO vo){

        String msg = "";
        String url = "";

        int result = memberService.insertJoin(vo);
        if(result != 0){
            msg = "회원가입에 성공하였습니다";
            url = "result";
        } else {
            msg = "회원가입에 실패하였습니다. 관리자에게 문의해주세요.";
            url = "/";
        }
        model.addAttribute("msg",msg);
        model.addAttribute("url",url);

        return "result";
    }
}
