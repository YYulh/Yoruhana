package com.yoruhana.controller;

import com.yoruhana.entity.MemberVO;
import com.yoruhana.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/joinForm.do")
    public String joinForm() {

    return "member/joinForm";
    }

    @GetMapping("/joinSubmit.do")
    public String joinSubmit(Model model, MemberVO vo){

        String msg = "";
        String url = "";

        int result = memberService.insertJoin(vo);
        if(result != 0){
            msg = "회원가입에 성공하였습니다";
            url = "/";
        } else {
            msg = "회원가입에 실패하였습니다. 관리자에게 문의해주세요.";
            url = "/";
        }
        model.addAttribute("msg",msg);
        model.addAttribute("url",url);

        return "common/result";
    }

    @GetMapping("/loginForm.do")
    public String loginForm(HttpServletRequest request,MemberVO vo){


            String mb_id = vo.getMb_id();

            boolean check = false;

            if(mb_id == null){

                Cookie[] cks = request.getCookies();

                if(cks != null){
                    for(Cookie ck : cks){
                        if(ck.getName().equals("ckid")){
                            mb_id = ck.getValue();
                            check = true;
                            break;
                        }
                    }
                }

                if(mb_id == null){
                    mb_id = "";
                }
            }

            request.setAttribute("mb_id", mb_id);
            request.setAttribute("check", check);

        return "member/loginForm";

    }

    @GetMapping("/login.do")
    public String login(MemberVO vo, HttpServletRequest request, HttpServletResponse response){
        String msg = "";
        String url = "/loginForm.do";
        String mb_name = vo.getMb_id();

        vo = memberService.login(vo);

        boolean check = false;

        if(vo != null){
            msg = vo.getMb_nick() + "님 로그인에 성공하셨습니다.";
            check = true;
            url = "/";

            HttpSession session = (HttpSession) request.getSession();

            session.setAttribute("no",vo.getMb_no());
            session.setAttribute("nick", vo.getMb_nick());
            session.setAttribute("country", vo.getMb_country());
            session.setAttribute("id", vo.getMb_id());

            String ckid = request.getParameter("ckid");

            Cookie ck = null;

            Cookie[] cks = request.getCookies();


            if(cks != null){
                for(Cookie c : cks){
                    if(c.getName().equals("ckid")){
                        ck = c;
                        break;
                    }
                }
            }

            if(ckid != null){
                if(ck == null){
                    ck = new Cookie("ckid",vo.getMb_id());


                    ck.setPath("/");

                    ck.setMaxAge(60*60*24);

                    response.addCookie(ck);
                }else{
                    if(!ck.getValue().equals(vo.getMb_id())){
                        ck.setValue(vo.getMb_id());
                        response.addCookie(ck);
                    }
                }
            }else{
                if(ck != null){
                    if(ck.getValue().equals(vo.getMb_id())){
                        ck.setMaxAge(0);
                        ck.setPath("/");
                        response.addCookie(ck);
                    }
                }
            }
        }else{
            msg = "로그인에 실패하셨습니다.";
        }
        request.setAttribute("msg", msg);
        request.setAttribute("check", check);
        request.setAttribute("url", url);

        return "common/result";
    }

    @GetMapping("/checkNick")
    @ResponseBody
    public String checkNick(String mb_nick){

        MemberVO nick = memberService.checkNick(mb_nick);

        if(nick == null){
            return "사용가능한 닉네임입니다.";
        } else {
            return "이미 사용중인 닉네임입니다.";
        }

    }
    @GetMapping("/checkId")
    @ResponseBody
    public String checkId(String mb_id){

        MemberVO id = memberService.checkId(mb_id);

        if(id == null){
            return "사용가능한 아이디입니다.";
        } else {
            return "이미 사용중인 아이디입니다.";
        }
    }

    @GetMapping("/logout.do")
    public String logout(Model model, HttpSession session) {

        session.invalidate();

        model.addAttribute("msg", "로그아웃 되었습니다.");
        model.addAttribute("url", "/");

        return "common/result";
    }

    @GetMapping("/mypageCon.do")
    public String mypageCon(HttpServletRequest request){

        return "member/mypageCon";
    }

    @GetMapping("/mypageForm.do")
    public String mypageForm(HttpServletRequest request,MemberVO vo){

        vo = memberService.login(vo);
        if(vo==null){
            String msg = "아이디 혹은 비밀번호가 일치하지 않습니다.";
            String url = "member/mypageCon";

            return "common/result";
        } else {

            HttpSession session = (HttpSession) request.getSession();

            int mb_no = (Integer) session.getAttribute("no");

            System.out.println(mb_no);

            vo = memberService.getInfo(mb_no); //회원정보
        }

        if(vo == null){
            String msg = "잘못된 계정 정보입니다. 관리자에게 문의해 주십시오";
            return "/";
        }else if(vo.getMb_tel()!=null){

                String str = vo.getMb_tel();
                String[] tel = str.split("-");

                vo.setMb_tel1(tel[0]);
                vo.setMb_tel2(tel[1]);
                vo.setMb_tel3(tel[2]);

        }
        request.setAttribute("vo",vo);

        return "member/mypageForm";
    }
}
