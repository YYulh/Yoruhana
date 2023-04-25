package com.yoruhana.controller.chat;

import ch.qos.logback.core.util.TimeUtil;
import com.yoruhana.entity.ChatList;
import com.yoruhana.entity.ChatRoom;
import com.yoruhana.entity.MemberVO;
import com.yoruhana.service.member.ChatRoomService;
import com.yoruhana.service.member.MemberService;
import org.apache.ibatis.jdbc.Null;
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
            throws IOException  {

        //현재 접속한 국가 정보 확인
        HttpSession session = request.getSession();
        String lang = (String) session.getAttribute("lang");

        // 현재 로그인 유저의 정보 세팅
        int mb_no = (int) session.getAttribute("no");
        String mb_nick_b = (String) session.getAttribute("nick");
        String mb_name = (String) session.getAttribute("name");

        String mb_nick_a = nick2; //상대 닉네임

        //chatRoom 객체 생성
        ChatRoom chatRoom = new ChatRoom();

        //parameter로 받아온 상대닉네임을 통해 상대 정보 select
        MemberVO target_user = memberService.getInfo_Nick(mb_nick_a); //1차 정상
        String target_mb_name = target_user.getMb_name(); //상대이름
        int target_mb_no= target_user.getMb_no(); //상대 mb_no
        String target_mb_file = target_user.getMb_file(); //상대 사진

        // 파일업로드 상대경로 추출 (임시, 추후 호스트서버 내 경로 이용할 것)
        String fileUploadPath = applicationContext.getResource("classpath:/static").getFile().getAbsolutePath();
        model.addAttribute("fileUploadPath", fileUploadPath);

        //이미 chatRoom이 만들어져있는지 확인
        if (chatRoomService.countByChatNick(mb_nick_a, mb_nick_b) > 0) {
            //이미 만들어져 있을경우 A멤버와 B멤버가 정해져있으므로 기존 데이터를 핸들링

            //chatRoom Id(Primary) get
            ChatRoom chatRoomTemp = chatRoomService.findByChatId(mb_nick_a, mb_nick_b);
            //해당 채팅방 내용 불러오기
            List<ChatRoom> chatHistory = chatRoomService.readChatHistory(chatRoomTemp, fileUploadPath);

           /* 기존 사용된 방식대로 하면 읽음처리 시 상대방 닉네임이 유동적으로 변하게 됨
            상대의 정보를 받아오는 로직은 유지하되, 이미 채팅방이 만들어져있으면 DB에 저장되어 있는 기본 정보를 반환 해주도록 함*/
            chatRoom = chatRoomTemp;

            model.addAttribute("chatHistory", chatHistory);
        } else {

            //만들어져 있는 채팅방이 없다면 상대방 닉네임을 기반으로 정보를 저장
            chatRoom.setMb_no(mb_no);
            chatRoom.setMb_nick_a(mb_nick_a);
            chatRoom.setMb_nick_b(mb_nick_b);
            chatRoom.setMb_name_a(target_mb_name);
            chatRoom.setMb_name_b(mb_name);
            chatRoom.setUsers_title("YORUHANA");

            //chatRoom 생성
            chatRoomService.addChatRoom(chatRoom);
            //대화 내용을 저장할 text file 생성
            chatRoomService.createFile(chatRoom.getMb_no(),chatRoomService.getId(chatRoom.getMb_nick_a(), chatRoom.getMb_nick_b()), fileUploadPath);
        }
        //실시간 읽음 확인 처리
        int read = chatRoomService.getId(chatRoom.getMb_nick_a(), chatRoom.getMb_nick_b());
        ChatRoom chatRoomRead = chatRoomService.chatInfo(read);
        model.addAttribute("chatRoomRead",chatRoomRead);

        // chatRoom Add 시 생성될 chatId
        chatRoom.setId(chatRoomService.getId(chatRoom.getMb_nick_a(), chatRoom.getMb_nick_b()));

        // chatRoom 객체 Model에 저장해 view로 전달
        model.addAttribute("chatRoomInfo", chatRoom);
        model.addAttribute("login_flag", "login");

        // 로그인 유저 정보 셋팅
        model.addAttribute("mb_no", mb_no);
        model.addAttribute("mb_nick", mb_nick_b);
        model.addAttribute("mb_name", mb_name);

        // 상대 유저 정보 셋팅
        model.addAttribute("target_user_id", target_mb_no);
        model.addAttribute("target_nick", mb_nick_a);
        model.addAttribute("target_user_name", target_mb_name);
        model.addAttribute("target_mb_file",target_mb_file);

        //접속 국가에 따라 해당되는 view로 리턴
        return "chat/"+ lang + "/chatBroadcastProduct";
    }

    //설정해준 EndPoint("/broadcast") 를 통해 MessageMapping에 내용전달 (broadcast 진입)
    @MessageMapping("/broadcast")
    public void send(ChatRoom chatRoom) throws IOException {

        //SimpleDateFormat을 통한 날짜 포맷 변환
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String formatedNow = formatter.format(now);

        chatRoom.setSendTime(formatedNow);

        int id = chatRoom.getId();

        //SimpleBroker에서 설정해준 Subscribe(구독) url
        String url = "/topic/" + id + "/queue/messages";

        simpMessage.convertAndSend(url, new ChatRoom(chatRoom.getContent(), chatRoom.getSenderName(),
        chatRoom.getSendTime(), chatRoom.getSenderNick()));

        //채팅 내용 text file에 추가
        chatRoomService.appendMessage(chatRoom);
    }

    //실시간 메세지 읽음 처리 확인
    @ResponseBody
    @RequestMapping(value = "/chatread.do",method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public void ajaxChatProductRead(@RequestBody String json) throws IOException {

        //json 파라미터 값을 통해 읽음 처리를 해줄 멤버를 정함
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

    //채팅 리스트 form
    @RequestMapping(value = "/chatList.do", method = RequestMethod.GET)
    public String getChatList(Model model, HttpSession session) {
        return "chatList";
    }

/*    @RequestMapping(value = "/chatRoom/{mb_no}/{mb_nick_a}/{mb_nick_b}/getChat", method = RequestMethod.GET)
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
    }*/

/*    @RequestMapping(value = "/chatUnreadAlert/ajax", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    @ResponseBody
    public int chatUnread(@RequestBody String json) {

        //json 파라미터 정보로 안읽은 메세지가 있다면 해당 채팅방에 알림 표시
        JSONObject jsn = new JSONObject(json);
        String nick = (String) jsn.get("nick");
        int messages = chatRoomService.getUnreadMessages(nick);

        return messages;
    }*/

    @RequestMapping(value = "/chatUnreadMessageInfo/ajax", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    @ResponseBody
    public String chatListUnread(@RequestBody String json, HttpSession session)  {

        JSONObject jsn = new JSONObject(json);
        // JSON.get([mapped name])으로 value 추출하기
        String nick = (String) jsn.get("nick");
        // 닉네임에 해당되는 모든 chatRoom select 받기
        List<ChatList> chatRoomList = chatRoomService.findByNick(nick);

        // chatRoom 정보 JSON Array에 저장
        JSONArray ja = new JSONArray();
        // 닉네임에 해당되는 읽지 않은 채팅방 목록 받기
        List<Integer> unreadChatId = chatRoomService.getUnreadChatRoom(nick);

        String file = "";
        String last_chat = "";

        for (ChatList chatList : chatRoomList) {

            // chatRoom 정보를 JSON Object에 put 해줌. chatRoom이 반복문에서 넘어갈 때마다 객체 초기화
            JSONObject jo = new JSONObject();
            jo.put("id",chatList.getId());
            jo.put("mb_no", chatList.getMb_no());

            last_chat = chatRoomService.getLast_chat(chatList.getId());
            jo.put("last_chat",last_chat);
            jo.put("users_title", chatList.getUsers_title());

            // 리스트에 출력할 상대방 닉네임,프로필사진 확인
            if (chatList.getMb_nick_a().equals(nick)) {
                jo.put("senderName", chatList.getMb_name_b());
                jo.put("senderNick",chatList.getMb_nick_b());
                file = chatRoomService.getFile(chatList.getMb_nick_b());
                jo.put("file",file);
            } else {
                jo.put("senderName", chatList.getMb_name_a());
                jo.put("senderNick",chatList.getMb_nick_a());
                file = chatRoomService.getFile(chatList.getMb_nick_a());
                jo.put("file",file);
            }

            // 읽지 않은 chatRoom이 0개일때
            if (unreadChatId.size() == 0) {
                jo.put("messageUnread", "");
            } else {
                // 읽지 않은 chatRoomId들과 현재 chatRoomId 대조
                for (int ele : unreadChatId) {
                    //읽지 않은 메세지가 있을 경우 "NEW" 표시
                    if (chatList.getId() == ele) {
                        jo.put("messageUnread", "NEW");
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

        // String으로 변환
        String result = jsnResult.toString();

        // View로 result를 return해줌
        return result;
    }

    //주기적으로 chatRoom을 확인할 Ajax
    @RequestMapping(value = "/chatList/ajax", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
    @ResponseBody
    public String chatList(@RequestBody String json, HttpSession session) {

        JSONObject jsn = new JSONObject(json);
        String nick = (String) jsn.get("nick");
        List<ChatList> chatRoomList = chatRoomService.findByNick(nick);
        JSONArray ja = new JSONArray();

        //해당 닉네임으로 존재하는 chatRoom 만큼 Json Object에 roomId를 넣고 Json Array에 저장
        for (ChatList chatList : chatRoomList) {
            JSONObject jo = new JSONObject();
            jo.put("id",chatList.getId());
            ja.put(jo);
        }

        JSONObject jsnResult = new JSONObject();
        jsnResult.put("chatList", ja);
        String result = jsnResult.toString();

        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/insert_last_chat.do", method = RequestMethod.POST,produces = "application/json; charset=UTF-8" )
    public void insertlastchat(@RequestBody String json) throws IOException{
        JSONObject jsn = new JSONObject(json);

        //채팅리스트에서 미리 보여줄 마지막 채팅 내용을 저장
        String content = (String)jsn.get("content");
        String id = (String) jsn.get("id");

        //가장 최근에 채팅이 온 채팅방부터 보여주는 sorting을 하기 위한 날짜 저장
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String formatedNow = formatter.format(now);

        chatRoomService.last_chat(id, content);
        chatRoomService.update_chat(id,formatedNow);
    }

    //아무 채팅방도 들어가지 않은 초기화면
    @RequestMapping("/chatBasic.do")
    public String chatBasic (HttpServletRequest request){
    HttpSession session = request.getSession();
    String lang = (String)request.getSession().getAttribute("lang");

    return "chat/" +lang +"/chatBasic";
    }
}
