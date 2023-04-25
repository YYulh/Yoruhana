package com.yoruhana.controller.member;

import com.yoruhana.entity.MemberVO;
import com.yoruhana.service.member.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.String.valueOf;
import static jdk.nashorn.internal.objects.NativeDate.now;

@Controller
public class MemberController {
    @Autowired
    private MemberService memberService;

    //회원가입 form
    @GetMapping("/joinForm.do")
    public String joinForm(HttpServletRequest request) {

        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");

        return "member/" + lang + "/joinForm";
    }

    //회원가입
    @PostMapping("/joinSubmit.do")
    public String joinSubmit(Model model, MemberVO vo) {

        String msg = "";
        String url = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Calendar c1 = Calendar.getInstance();
        String strToday = sdf.format(c1.getTime());

        String mb_bir = (vo.getMb_bir1() + vo.getMb_bir2() + vo.getMb_bir3());
        int parse = ((Integer.parseInt(strToday) - Integer.parseInt(mb_bir)) / 10000);
        vo.setMb_old(parse);



        int result = memberService.insertJoin(vo);
        if (result != 0) {
            int lastno = memberService.lastNo();
            memberService.insertBlog(lastno);

            msg = "회원가입에 성공하였습니다";
            url = "/";
        } else {
            msg = "회원가입에 실패하였습니다. 관리자에게 문의해주세요.";
            url = "/";
        }
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        return "common/result";
    }

    //로그인 form
    @GetMapping("/loginForm.do")
    public String loginForm(HttpServletRequest request, MemberVO vo) {

        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");

        String mb_id = vo.getMb_id();

        boolean check = false;

        if (mb_id == null) {

            Cookie[] cks = request.getCookies();

            if (cks != null) {
                for (Cookie ck : cks) {
                    if (ck.getName().equals("ckid")) {
                        mb_id = ck.getValue();
                        check = true;
                        break;
                    }
                }
            }

            if (mb_id == null) {
                mb_id = "";
            }
        }

        request.setAttribute("mb_id", mb_id);
        request.setAttribute("check", check);

        return "member/" + lang + "/loginForm";

    }

    //로그인
    @PostMapping(value = "/login.do")
    public String login(MemberVO vo, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");

        String msg = "";
        String url = "/loginForm.do";

        vo = memberService.login(vo);

        boolean check = false;

        if (vo != null) {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            String loginT = formatter.format(now);
            String mb_no = valueOf(vo.getMb_no());
            memberService.loginTime(mb_no,loginT);

            check = true;
            session.setAttribute("login",vo);
            session.setAttribute("name", vo.getMb_name());
            session.setAttribute("no", vo.getMb_no());
            session.setAttribute("nick", vo.getMb_nick());
            session.setAttribute("country", vo.getMb_country());
            session.setAttribute("id", vo.getMb_id());
            session.setAttribute("file", vo.getMb_file());

            String ckid = request.getParameter("ckid");

            Cookie ck = null;

            Cookie[] cks = request.getCookies();


            if (cks != null) {
                for (Cookie c : cks) {
                    if (c.getName().equals("ckid")) {
                        ck = c;
                        break;
                    }
                }
            }

            if (ckid != null) {
                if (ck == null) {
                    ck = new Cookie("ckid", vo.getMb_id());


                    ck.setPath("/");

                    ck.setMaxAge(60 * 60 * 24);

                    response.addCookie(ck);
                } else {
                    if (!ck.getValue().equals(vo.getMb_id())) {
                        ck.setValue(vo.getMb_id());
                        response.addCookie(ck);
                    }
                }
            } else {
                if (ck != null) {
                    if (ck.getValue().equals(vo.getMb_id())) {
                        ck.setMaxAge(0);
                        ck.setPath("/");
                        response.addCookie(ck);
                    }
                }
            }
        } else {
            if (lang.equals("KR")) {
                msg = "확인되지 않는 아이디/비밀번호 입니다.";
            } else if (lang.equals("JP")) {
                msg = "確認できないIDまたはパスワードです。";
            }

            request.setAttribute("msg", msg);
            request.setAttribute("url", url);
            return "common/result";
        }

        request.setAttribute("check", check);

        return "main/index";
    }

    //닉네임 중복확인
    @GetMapping("/checkNick")
    @ResponseBody
    public String checkNick(String mb_nick, HttpServletRequest request) {

        MemberVO nick = memberService.checkNick(mb_nick);

        HttpSession session = request.getSession();
        String lang = (String) session.getAttribute("lang");

        if (lang.equals("KR")) {
            if (nick == null) {
                return "사용가능한 닉네임입니다.";
            } else {
                return "이미 사용중인 닉네임입니다.";
            }
        }

        if (lang.equals("JP")) {
            if (nick == null) {
                return "使用可能なニックネームです。";
            } else {
                return "すでに使用中のニックネームです。";
            }
        }
        return "error";
    }

    //ID 중복확인
    @GetMapping("/checkId")
    @ResponseBody
    public String checkId(String mb_id, HttpServletRequest request) {

        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");
        MemberVO id = memberService.checkId(mb_id);


        if (lang.equals("KR")) {
            if (id == null) {
                return "사용가능한 아이디입니다.";
            } else {
                return "이미 사용중인 아이디입니다.";
            }
        }
        if (lang.equals("JP")) {
            if (id == null) {
                return "使用可能なIDです。";
            } else {
                return "すでに使用中のIDです。";
            }
        }
        return "error";
    }

    //로그아웃
    @GetMapping("/logout.do")
    public String logout(Model model, HttpSession sessionEnd, HttpServletRequest request) {

        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");

        if (lang.equals("KR")) {
            model.addAttribute("msg", "로그아웃 되었습니다.");
        } else if (lang.equals("JP")) {
            model.addAttribute("msg", "ログアウトされました。");
        }

        sessionEnd.invalidate();

        model.addAttribute("url", "/");

        return "common/result";
    }

    //마이페이지 비밀번호 확인
    @GetMapping("/mypageCon.do")
    public String mypageCon(HttpServletRequest request) {
        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");

        return "member/" + lang + "/mypageCon";
    }

    //마이페이지
    @PostMapping("/mypageForm.do")
    public String mypageForm(HttpServletRequest request, MemberVO vo) {

        HttpSession session = request.getSession();

        String lang = (String) session.getAttribute("lang");
        String msg ="";
        vo = memberService.login(vo);
        if (vo == null) {
            if (lang.equals("KR")) {
                msg = "아이디 혹은 비밀번호가 일치하지 않습니다.";
            } else if (lang.equals("JP")) {
                msg = "IDまたはパスワードが一致しません。";
            }

            String url = "/mypageCon.do";

            request.setAttribute("msg", msg);
            request.setAttribute("url", url);

            return "common/result";
        } else {

            int mb_no = (Integer) session.getAttribute("no");
            vo = memberService.getInfo(mb_no); //회원정보
        }
        String str = vo.getMb_tel();
        String[] tel = str.split("-");

        vo.setMb_tel1(tel[0]);
        vo.setMb_tel2(tel[1]);
        vo.setMb_tel3(tel[2]);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Calendar c1 = Calendar.getInstance();
        String strToday = sdf.format(c1.getTime());
        int parse = ((Integer.parseInt(strToday) - Integer.parseInt(vo.getMb_bir())) / 10000);
        String mb_bir = valueOf(parse);
        vo.setMb_bir(mb_bir);

        request.setAttribute("vo", vo);

        return "member/" + lang + "/mypageForm";
    }

    @GetMapping("/profile.do")
    public String profileForm(HttpServletRequest request) {
        HttpSession session = request.getSession();
        int mb_no = (Integer) session.getAttribute("no");
        String lang = (String) session.getAttribute("lang");

        MemberVO vo = memberService.getInfo(mb_no);

        request.setAttribute("vo", vo);

        return "member/" + lang + "/profile";
    }

    @PostMapping("/profileUpdateForm.do")
    public String profileUpdateForm(HttpServletRequest request) {
        HttpSession session = request.getSession();
        int mb_no = (Integer) session.getAttribute("no");
        String lang = (String) session.getAttribute("lang");

        MemberVO vo = memberService.getInfo(mb_no);

        request.setAttribute("vo", vo);

        return "member/" + lang + "/profileUpdate";
    }

    @PostMapping("/mypageUpdate.do")
    public String mypageUpdate(MemberVO vo, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String lang = (String) session.getAttribute("lang");
        String msg = "";
        String url = "/mypageCon.do";

        int result = memberService.mypageUpdate(vo);

        if (lang.equals("KR")) {

            if (result == 0) {
                msg = "업데이트에 실패하였습니다. 관리자에게 문의해주세요.";
            } else {
                msg = "성공적으로 수정되었습니다.";
            }
        } else if (lang.equals("JP")) {
            if (result == 0) {
                msg = "アップデートに失敗しました。 管理者にお問い合わせください。";
            } else {
                msg = "修正に成功しました。";
            }
        }

        request.setAttribute("msg", msg);
        request.setAttribute("url", url);

        return "common/result";
    }

    @PostMapping("/profileUpdate.do")
    public String profileUpdate(MemberVO vo, HttpServletRequest request,@RequestParam("file") MultipartFile photo) {
        HttpSession session = request.getSession();
        String lang = (String) session.getAttribute("lang");
        String msg = "";
        String url = "/profile.do";
        //--------------------------------------
        //파일을 저장할 경로
        String savePath = "C:\\Users\\82107\\Desktop\\storage\\";
        String filename = null;


            //업로드된 실제파일명
            filename = photo.getOriginalFilename();

            if(!photo.isEmpty()) { //가져온 사진이 있으면

                //File 객체 생성
                File saveFile = new File(savePath,filename);

                if(!saveFile.exists()) { //경로에 파일이 없으면
                    saveFile.mkdirs();
                }else { //있으면
                    long time = System.currentTimeMillis(); //단지 이름 바꿔주는 방식, 다르게 바꿔줘도 됨.

                    filename = String.format("%s%d%s", filename.substring(0, filename.lastIndexOf(".")),time,filename.substring(filename.lastIndexOf(".")));

                    saveFile = new File(savePath,filename);
                }

                //업로드된 파일은 MultipartResolver라는 클래스가 지정한 임시저장소에 저장되어 있다...
                //파일이 일정시간이 지나면 사라지기때문에 내가 지정한 경로로 복사해준다...
                try {
                    photo.transferTo(saveFile);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    saveFile.delete(); // 타임밀리언즈라 왠만하면 이름이 안겹치겠지만 호옥시라도 겹쳐서 오류나면 해당 파일 삭제!
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //등록된 파일이 없으면 null 대신 빈 문자열이 대신 저장될 수 있도록
            }else {
                filename = (String) session.getAttribute("file");
            }

            vo.setMb_file(filename);

        //--------------------------------------
        int result = memberService.updateProfile(vo);

        if (lang.equals("KR")) {

            if (result == 0) {
                msg = "업데이트에 실패하였습니다. 관리자에게 문의해주세요.";
            } else {
                msg = "성공적으로 수정되었습니다.";
            }
        } else if (lang.equals("JP")) {
            if (result == 0) {
                msg = "アップデートに失敗しました。 管理者にお問い合わせください。";
            } else {
                msg = "修正に成功しました。";
            }
        }

        session.setAttribute("file",vo.getMb_file());

        request.setAttribute("msg", msg);
        request.setAttribute("url", url);

        return "common/result";
    }
    @GetMapping("/searchPenpalForm.do")
    public String searchPenpalForm(Model model,HttpServletRequest request,Integer start, Integer limit,MemberVO vo) throws NullPointerException{

        HttpSession session = request.getSession();
        String lang = (String) session.getAttribute("lang");

        if(start == null) {
            start = 0;
        }
        if(limit == null) {
            limit = 8;
        }

        //현재 페이지
        int nowPage = (start) / limit + 1;

        vo.setStart(start);
        vo.setLimit(limit);

        List<MemberVO> list;

         list = memberService.searchPenpalList(vo);



        //총 글의 개수
        int total = memberService.getTotal();

        if(session.getAttribute("no") !=null){
            total =total-1;
        }
        int totalPage = total % limit == 0 ? total / limit : total / limit + 1;

        for(int i = 0; i <list.size(); i++){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Calendar c1 = Calendar.getInstance();
            String strToday = sdf.format(c1.getTime());

            int parse = ((Integer.parseInt(strToday) - Integer.parseInt(list.get(i).getMb_bir())) / 10000);
            String mb_bir = valueOf(parse);

            list.get(i).setMb_bir(mb_bir);

        }

        model.addAttribute("start_old",vo.getStart_old());
        model.addAttribute("end_old",vo.getEnd_old());
        model.addAttribute("searchSex",vo.getSearchSex());
        model.addAttribute("searchCountry",vo.getSearchCountry());
        model.addAttribute("searchPic",vo.getSearchPic());

        model.addAttribute("start", start);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("total", total);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("list",list);
        model.addAttribute("limit",limit);

        return "member/"+lang + "/searchPenpal";
    }

    @ResponseBody
    @RequestMapping(value = "/increase_see.do", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    public void chatListUnread(@RequestBody String json, HttpSession session) {

        JSONObject jsn = new JSONObject(json);

        int mb_no = (int) jsn.get("mb_no");

        int see = memberService.getMbSee(mb_no);

        int total = see + 1;
        memberService.increaseSee(mb_no,total);

    }

    @GetMapping("/deleteRoom.do")
    public String deleteRoom(HttpServletRequest request,int id){

        HttpSession session = request.getSession();
        String lang = (String)request.getSession().getAttribute("lang");

        memberService.deleteRoom(id);

        return "chat/"+ lang + "/chatBasic";
    }
}