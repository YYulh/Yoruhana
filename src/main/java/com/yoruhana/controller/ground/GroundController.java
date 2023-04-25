package com.yoruhana.controller.ground;

import com.yoruhana.entity.BlogVO;
import com.yoruhana.service.member.GroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Timestamp;
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

    @PostMapping("/insertPost.do")
    public String insertPost(HttpServletRequest request,BlogVO vo,@RequestParam("imgList") List<MultipartFile> list){
        String lang =  (String)request.getSession().getAttribute("lang");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        String post_date = formatter.format(now);

        vo.setPost_date(post_date);
        System.out.println(vo.getPost_date());
        System.out.println("mb_no:" + vo.getMb_no());

        int blog_no = groundService.getPostListByMb_no(vo.getMb_no());
        vo.setBlog_no(blog_no);

        int post_no = groundService.insertPost(vo);
        System.out.println("selectkey:" + post_no);
        vo.setPost_no(post_no);
        //------------------
        String savePath = "C:\\Users\\82107\\Desktop\\storage\\post\\";
        String filename = null;



            if (!list.isEmpty()) { //가져온 사진이 있으면
                for(int i =0; i<list.size(); i++) {
                    filename = list.get(i).getOriginalFilename();//업로드된 실제파일명

                //File 객체 생성
                File saveFile = new File(savePath, filename);

                if (!saveFile.exists()) { //경로에 파일이 없으면
                    saveFile.mkdirs();
                } else { //있으면
                    long time = System.currentTimeMillis(); //단지 이름 바꿔주는 방식, 다르게 바꿔줘도 됨.

                    filename = String.format("%s%d%s", filename.substring(0, filename.lastIndexOf(".")), time, filename.substring(filename.lastIndexOf(".")));

                    saveFile = new File(savePath, filename);
                }

                //업로드된 파일은 MultipartResolver라는 클래스가 지정한 임시저장소에 저장되어 있다...
                //파일이 일정시간이 지나면 사라지기때문에 내가 지정한 경로로 복사해준다...
                try {
                    list.get(i).transferTo(saveFile);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    saveFile.delete(); // 타임밀리언즈라 왠만하면 이름이 안겹치겠지만 호옥시라도 겹쳐서 오류나면 해당 파일 삭제!
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //등록된 파일이 없으면 null 대신 빈 문자열이 대신 저장될 수 있도록

                vo.setPost_p_file(filename);

                groundService.insertPostPic(vo);
                }
            } else {
                filename = "";
            }




        //--------------------------
        return "ground/" + lang + "/groundForm";
    }

}
