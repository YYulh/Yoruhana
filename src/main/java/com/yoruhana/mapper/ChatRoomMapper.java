package com.yoruhana.mapper;


import com.yoruhana.entity.ChatList;
import com.yoruhana.entity.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


@Mapper
public interface ChatRoomMapper {

    public void addChatRoom (ChatRoom chatRoom) throws IOException;

    public int countByChatId(@Param("mb_no")int mb_no, @Param("mb_nick_a")String mb_nick_a);

    public void appendMessage(ChatRoom chatRoom) throws FileNotFoundException, IOException;

    public int getUnreadMessages(@Param("nick")String nick);

    public List<Integer> getUnreadChatRoom(@Param("nick")String nick);


    public ChatRoom findByChatId(@Param("mb_nick_a")String mb_nick_a, @Param("mb_nick_b")String mb_nick_b);

    public int getId(@Param("mb_nick_a")String mb_nick_a, @Param("mb_nick_b")String mb_nick_b);

    public void updateFileName(@Param("id")int id, @Param("fileName")String fileName);

    public void updateChatReadBuy(int id,int chatRead_a);

    public void updateChatReadSell(int id, int chatRead_b);

    public ChatRoom findByChatId2(@Param("mb_no")int mb_no, @Param("mb_nick_a")String mb_nick_a);

    public List<ChatList> findByNick(@Param("nick")String nick);

    public int countByChatNick(@Param("mb_nick_a")String mb_nick_a, @Param("mb_nick_b")String mb_nick_b);

    public ChatRoom chatInfo(int read);

    public String getFile(String nick);

    public void last_chat(String id,String content);

    public String getLast_chat(int id);

    public void update_chat(String id, String formatedNow);
}
