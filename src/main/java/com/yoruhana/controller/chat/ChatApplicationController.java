package com.yoruhana.controller.chat;

import ch.qos.logback.core.util.TimeUtil;
import com.yoruhana.entity.ChatList;
import com.yoruhana.entity.ChatRoom;
import com.yoruhana.entity.MemberVO;
import com.yoruhana.service.member.ChatRoomService;
import com.yoruhana.service.member.MemberService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.core.util.TimeUtil.*;


@Controller
public class ChatApplicationController {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SimpMessagingTemplate simpMessage;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberService us;

    // 채팅으로 매칭연결 하기(view detail 화면)
    @RequestMapping(value = "/chatMessage.do", method = RequestMethod.GET)
    public String getWebSocketWithSockJs(Model model, String nick2, HttpServletRequest request)
            throws IOException {

        // productInfo화면에서 Chat화면에 전달해줄 parameter
        HttpSession session = request.getSession();
         int session_mb_no = (int) session.getAttribute("no");
        String session_mb_nick = (String) session.getAttribute("nick");
        String session_mb_name = (String) session.getAttribute("name");

        String mb_nick_a = nick2; //상대 닉네임

        int mb_no = session_mb_no; // 내 mb_no
        String mb_name = session_mb_name; // 내 이름
        String mb_nick_b = session_mb_nick; //내 닉네임

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMb_no(mb_no);
        chatRoom.setMb_nick_a(mb_nick_a);
        chatRoom.setMb_nick_b(mb_nick_b);


        MemberVO target_user = memberService.getInfo_Nick(mb_nick_a); //1차 정상
        String target_mb_name = target_user.getMb_name(); //상대이름
        int target_mb_no= target_user.getMb_no(); //상대 mb_no
System.out.println("냐옹1");
        chatRoom.setMb_name_a(target_mb_name);
        chatRoom.setMb_name_b(mb_name);
        chatRoom.setUsers_title("YORUHANA");
System.out.println("냐옹2");
        // 파일업로드 상대경로 추출
        String fileUploadPath = applicationContext.getResource("classpath:/static").getFile().getAbsolutePath();
        model.addAttribute("fileUploadPath", fileUploadPath);

        // 이미 chatRoom이 만들어져있는지 확인
        if (chatRoomService.countByChatNick(mb_nick_a, mb_nick_b) > 0) {
            // get ChatRoomInfo
            ChatRoom chatRoomTemp = chatRoomService.findByChatId(mb_nick_a, mb_nick_b);
System.out.println("냐옹3");
            // load existing chat history
            List<ChatRoom> chatHistory = chatRoomService.readChatHistory(chatRoomTemp, fileUploadPath);
            // transfer chatHistory Model to View
            model.addAttribute("chatHistory", chatHistory);
System.out.println("냐옹4");
        } else {
            // chatRoom 생성
            chatRoomService.addChatRoom(chatRoom);
            // text file 생성
            chatRoomService.createFile(chatRoom.getMb_no(),chatRoomService.getId(chatRoom.getMb_nick_a(), chatRoom.getMb_nick_b()), fileUploadPath);
        }
        System.out.println("냐옹5");
        // chatRoom Add 시 생성될 chatId
        chatRoom.setId(chatRoomService.getId(chatRoom.getMb_nick_a(), chatRoom.getMb_nick_b()));

        // chatRoom 객체 Model에 저장해 view로 전달
        model.addAttribute("chatRoomInfo", chatRoom);

        model.addAttribute("login_flag", "login");
        // 로그인 유저 정보 셋팅

        model.addAttribute("mb_no", mb_no);
        model.addAttribute("mb_nick", mb_nick_b);
        model.addAttribute("mb_name", mb_name);

        // 타겟 유저 정보 셋팅
        model.addAttribute("target_user_id", target_mb_no);
        model.addAttribute("target_nick", mb_nick_a);
        model.addAttribute("target_user_name", target_mb_name);

        return "chatBroadcastProduct";
    }

    @MessageMapping("/broadcast")
    public void send(ChatRoom chatRoom) throws IOException {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String formatedNow = formatter.format(now);

        chatRoom.setSendTime(formatedNow);
        //chatRoom.setSenderName("테스트 유저");
        //chatRoom.setSenderEmail("gtu@naver.com"); // 테스트입니다.

        int id = chatRoom.getId();
        //System.out.println("브로드캐스트 진입 : " + id);
        String url = "/topic/" + id + "/queue/messages";
        simpMessage.convertAndSend(url, new ChatRoom(chatRoom.getContent(), chatRoom.getSenderName(),
                chatRoom.getSendTime(), chatRoom.getSenderNick()));
        // append message to txtFile
        chatRoomService.appendMessage(chatRoom);
    }

    @RequestMapping(value = "/chatread/chatroom/ajax", produces = "application/text; charset=UTF-8")
    public void ajaxChatRoomRead(@RequestBody String json) throws IOException {
        JSONObject jsn = new JSONObject(json);
        String idStr = (String) jsn.get("id");
        int id = Integer.parseInt(idStr);
        String flag = (String) jsn.get("flag");
        if (flag.equals("mb_nick_b")) {
            chatRoomService.updateChatReadSell(id, 1);
        } else {
            chatRoomService.updateChatReadBuy(id, 1);
        }
    }

    @RequestMapping(value = "/chatread/product/ajax", produces = "application/json; charset=UTF-8")
    public void ajaxChatProductRead(@RequestBody String json) throws IOException {
        JSONObject jsn = new JSONObject(json);
        String idStr = (String) jsn.get("id");
        int id = Integer.parseInt(idStr);

        String flag = (String) jsn.get("flag");
        if (flag.equals("mb_nick_b")) {
            chatRoomService.updateChatReadSell(id, 1);
        } else {
            chatRoomService.updateChatReadBuy(id, 1);
        }
    }

    @RequestMapping(value = "/chatList.do", method = RequestMethod.GET)
    public String getChatList(Model model, HttpSession session) {

        return "chatList";
    }

    @RequestMapping(value = "/chatRoom/{mb_no}/{mb_nick_a}/{mb_nick_b}/getChat", method = RequestMethod.GET)
    public String getChatRoom(@PathVariable Map<String, String> requestVar, Model model, HttpServletRequest request,
                              HttpSession session) throws IOException {

        String mb_nick_a = requestVar.get("mb_nick_a");
        int mb_no = Integer.parseInt(requestVar.get("mb_no"));
        String mb_nick_b = requestVar.get("mb_nick_b");

        // 파일업로드 상대경로 추출
        String fileUploadPath = request.getSession().getServletContext().getRealPath("/resources/");
        // read chatHistory
        ChatRoom chatRoomRead = chatRoomService.findByChatId(mb_nick_a, mb_nick_b);
        List<ChatRoom> chatHistory = chatRoomService.readChatHistory(chatRoomRead, fileUploadPath);
        model.addAttribute("chatHistory", chatHistory);


        int id = chatRoomService.getId(mb_nick_a, mb_nick_b);
        String users_title = chatRoomRead.getUsers_title();
        // String nick_b = chatRoomRead.getnick_b();

        model.addAttribute("id", id);
        model.addAttribute("mb_no", mb_no);

        String user_type = (String) session.getAttribute("type");
        /* if(user_type.equals("Parent")) { model.addAttribute("nick_a", nick_a);
         * model.addAttribute("nick_b", nick_b); }else { model.addAttribute("nick_a",
         * nick_b); model.addAttribute("nick_b", nick_a); String origin_a =
         * chatRoomRead.getnick_a(); String origin_b = chatRoomRead.getnick_b();
         * String origin_a_name = chatRoomRead.getnick_a_name(); String origin_b_name =
         * chatRoomRead.getnick_b_name(); chatRoomRead.setnick_a(origin_b);
         * chatRoomRead.setnick_b(origin_a);
         * chatRoomRead.setnick_a_name(origin_b_name);
         * chatRoomRead.setnick_b_name(origin_a_name); }
         */
        MemberVO login = (MemberVO) session.getAttribute("login");
        model.addAttribute("chatRoomInfo", chatRoomRead);
        model.addAttribute("users_title", users_title);
        int login_mb_no = (int) session.getAttribute("no");
        String mb_name = login.getMb_name();
        // 탑,풋 유저셋팅값 전달
        model.addAttribute("login_flag", "login");
        model.addAttribute("mb_no", session.getAttribute("no"));
        model.addAttribute("nick", session.getAttribute("nick"));
        model.addAttribute("mb_name", mb_name);

        model.addAttribute("mb_nick_a", chatRoomRead.getMb_nick_a());
        model.addAttribute("mb_nick_b", chatRoomRead.getMb_nick_b());

        return "chatBroadcastChatRoom";
    }

    @RequestMapping(value = "/chatUnreadAlert/ajax", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    @ResponseBody
    public int chatUnread(@RequestBody String json) {

        JSONObject jsn = new JSONObject(json);
        String nick = (String) jsn.get("nick");
        int messages = chatRoomService.getUnreadMessages(nick);

        return messages;
    }

    @RequestMapping(value = "/matching/chatUnreadMessageInfo/ajax", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    @ResponseBody
    public String chatListUnread(@RequestBody String json, HttpSession session) {

        JSONObject jsn = new JSONObject(json);
        // JSON.get([mapped name])으로 value 추출하기
        String nick = (String) jsn.get("nick");
        // email에 해당되는 모든 chatRoom select 받기
        List<ChatList> chatRoomList = chatRoomService.findByEmail(nick);
        // chatRoom 정보는 JSON Array에 저장됨
        JSONArray ja = new JSONArray();
        // email에 해당되는 읽지 않은 chatRoom select 받기
        List<Integer> unreadChatId = chatRoomService.getUnreadChatRoom(nick);

        for (ChatList chatList : chatRoomList) {
            // chatRoom 정보를 JSON Object에 put 해줌. chatRoom이 반복문에서 넘어갈 때마다 객체 초기화
            JSONObject jo = new JSONObject();

            jo.put("mb_no", chatList.getMb_no());
            // 리스트에 출력할 상대방 닉네임 확인
            if (chatList.getMb_nick_a().equals(nick)) {
                jo.put("senderName", chatList.getMb_name_b());
            } else {
                jo.put("senderName", chatList.getMb_name_a());
            }

            jo.put("users_title", chatList.getUsers_title());
            // 읽지 않은 chatRoom이 0개일때
            if (unreadChatId.size() == 0) {
                jo.put("messageUnread", "");
            } else {
                // 읽지 않은 chatRoomId들과 현재 chatRoomId 대조 후 처리
                for (int ele : unreadChatId) {
                    if (chatList.getId() == ele) {
                        jo.put("messageUnread", "메세지가 도착했습니다.");
                        break;
                    } else {
                        jo.put("messageUnread", "");
                    }
                }
            }
            ja.put(jo);
        }
        // Javascript에 parsing 할 수 있도록 JSON Object에 Array를 담아줌
        JSONObject jsnResult = new JSONObject();
        jsnResult.put("chatList", ja);
        // String으로 변환해주면 끝, 프런트<->백엔드 전달은 String으로 이루어지며 형식은 JSON을 선택했음
        String result = jsnResult.toString();
        // View로 result를 return해줌
        return result;
    }

    @RequestMapping(value = "/matching/chatList/ajax", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    @ResponseBody
    public String chatList(@RequestBody String json, HttpSession session) {

        JSONObject jsn = new JSONObject(json);
        String nick = (String) jsn.get("nick");
        List<ChatList> chatRoomList = chatRoomService.findByEmail(nick);
        JSONArray ja = new JSONArray();

        for (ChatList chatList : chatRoomList) {

            JSONObject jo = new JSONObject();
            ja.put(jo);
        }
        JSONObject jsnResult = new JSONObject();
        jsnResult.put("chatList", ja);
        String result = jsnResult.toString();

        return result;
    }

}