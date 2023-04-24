package com.yoruhana.controller.ground;

import com.yoruhana.entity.BlogVO;
import com.yoruhana.service.member.GroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class GroundController {

    @Autowired
    private GroundService groundService;

    @GetMapping("/groundForm.do")
    public String groundForm(HttpServletRequest request){
        String lang = (String)request.getSession().getAttribute("lang");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
        String formatedNow = formatter.format(now);

        List<BlogVO> list = groundService.getPostList();

        request.setAttribute("time_now",formatedNow);
        request.setAttribute("list",list);

        return "ground/" + lang + "/groundForm";
    }
}
