package com.starchat.repository;

import com.starchat.model.Message;

import java.util.List;

public interface MessageRepository  {

    Message getMessageById(Long id);

    Message addMessage(Message message);

    List<Message> getAllMessagesByChatRoomId(Long id);

    List<Long> getUserChatIds(Long id);
}
