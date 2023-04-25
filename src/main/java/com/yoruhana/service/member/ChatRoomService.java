package com.yoruhana.service.member;

import com.yoruhana.entity.ChatList;
import com.yoruhana.entity.ChatRoom;
import com.yoruhana.mapper.ChatRoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class ChatRoomService implements ChatRoomMapper {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ChatRoomMapper chatRoomMapper;


    @Override
    public void addChatRoom(ChatRoom chatRoom) throws IOException {

        Timestamp createdDate = Timestamp.valueOf(LocalDateTime.now());

        chatRoom.setCreatedDate(createdDate);

        chatRoomMapper.addChatRoom(chatRoom);

    }

    //no connection with DB
    public List<ChatRoom> readChatHistory(ChatRoom chatRoom, String fileUploadPath) throws IOException {


        String pathName = fileUploadPath + chatRoom.getFileName();

        //DB에 저장된 chat.txt 파일을 읽어옴
        BufferedReader br = new BufferedReader(new FileReader(pathName));
        //View에 ChatRoom 객체로 전달
        ChatRoom chatRoomLines = new ChatRoom();
        ChatRoom lastChat = new ChatRoom();
        List<ChatRoom> chatHistory = new ArrayList<ChatRoom>();

        String chatLine;
        int idx = 1;

        while ((chatLine = br.readLine()) != null) {

            //1개 메시지는 3줄(보낸사람,메시지내용,보낸시간)로 구성돼있음
            int answer = idx % 3;
            if (answer == 1) {
                //보낸사람
                chatRoomLines.setSenderName(chatLine);
                idx++;
            } else if (answer == 2) {
                //메시지내용
                chatRoomLines.setContent(chatLine);


                idx++;
            } else {
                //보낸시간
                chatRoomLines.setSendTime(chatLine);
                //메시지 담긴 ChatRoom 객체 List에 저장
                chatHistory.add(chatRoomLines);
                //객체 초기화, 줄(row)인덱스 초기화
                chatRoomLines = new ChatRoom();
                idx = 1;
            }
        }

        return chatHistory;
    }

    @Override
    public void updateFileName(int id, String fileName) {

        chatRoomMapper.updateFileName(id, fileName);
    }

    public void createFile(int mb_no, int id, String fileUploadPath) throws IOException {

        String fileName = mb_no + "_" + id + ".txt";
        String pathName = fileUploadPath + fileName;
        //File 클래스에 pathName 할당
        File txtFile = new File(pathName);
        //로컬경로에 파일 생성
        txtFile.createNewFile();

        System.out.println("createFile : " + id + "FileName : "+fileName);

        chatRoomMapper.updateFileName(id, fileName);
    }

    @Override
    public List<ChatList> findByNick(String nick) {

        return chatRoomMapper.findByNick(nick);
    }

    @Override
    public int countByChatId(int mb_no, String mb_nick_a) {

        return chatRoomMapper.countByChatId(mb_no, mb_nick_a);
    }


    //no connection with DB
    public void appendMessage(ChatRoom chatRoom) throws IOException {
        // 파일업로드 상대경로 추출
        String fileUploadPath = applicationContext.getResource("classpath:/static").getFile().getAbsolutePath();;

        int mb_no = chatRoom.getMb_no();
        String mb_nick_a = chatRoom.getMb_nick_a();
        String mb_nick_b = chatRoom.getMb_nick_b();

        ChatRoom chatRoomAppend = chatRoomMapper.findByChatId(mb_nick_a, mb_nick_b);

        String pathName = fileUploadPath + chatRoomAppend.getFileName();

        FileOutputStream fos = new FileOutputStream(pathName, true);
        String content = chatRoom.getContent();
        String senderName = chatRoom.getSenderName();
        String senderNick = chatRoom.getSenderNick();
        String sendTime = chatRoom.getSendTime();
        System.out.println("print:" + content);

        String writeContent = senderName + "\n" + content + "\n"  +  sendTime + "\n";

        byte[] b = writeContent.getBytes();

        fos.write(b);
        fos.close();

        System.out.println("senderEmail: "+ senderNick);
        System.out.println("mb_nick_b: "+ chatRoom.getMb_nick_b());
        System.out.println(senderNick.equals(chatRoom.getMb_nick_b()));

        if (senderNick.equals(chatRoom.getMb_nick_b())) {
            updateChatReadBuy(chatRoom.getId(), 0);
        } else {
            updateChatReadSell(chatRoom.getId(), 0);
        }

    }
    public ChatRoom chatInfo(int read){
        return  chatRoomMapper.chatInfo(read);
    }
    @Override
    public ChatRoom findByChatId2(int mb_no, String mb_nick_a) {
        return chatRoomMapper.findByChatId2(mb_no, mb_nick_a);
    }

    @Override
    public int getId(String mb_nick_a, String mb_nick_b) {
        return chatRoomMapper.getId(mb_nick_a, mb_nick_b);
    }

    @Override
    public void updateChatReadBuy(int id, int chatRead_a) {
        chatRoomMapper.updateChatReadBuy(id, chatRead_a);

    }

    @Override
    public void updateChatReadSell(int id, int chatRead_b) {
        chatRoomMapper.updateChatReadSell(id, chatRead_b);

    }

    @Override
    public int getUnreadMessages(String nick) {

        return chatRoomMapper.getUnreadMessages(nick);
    }

    @Override
    public List<Integer> getUnreadChatRoom(String nick) {

        List<Integer> unread = chatRoomMapper.getUnreadChatRoom(nick);
        return unread;
    }

    public int countByChatNick(String mb_nick_a, String mb_nick_b) {
        // TODO Auto-generated method stub
        return chatRoomMapper.countByChatNick(mb_nick_a, mb_nick_b);
    }

    @Override
    public ChatRoom findByChatId(String mb_nick_a, String mb_nick_b) {
        // TODO Auto-generated method stub
        return chatRoomMapper.findByChatId(mb_nick_a, mb_nick_b);
    }

    public String getFile(String nick){
        return chatRoomMapper.getFile(nick);
    }

    public void last_chat(String id, String content){
        chatRoomMapper.last_chat(id,content);
    }
    public String getLast_chat(int id){
        return chatRoomMapper.getLast_chat(id);
    }
    public void update_chat(String id, String formatedNow){
        chatRoomMapper.update_chat(id, formatedNow);
    }
}
