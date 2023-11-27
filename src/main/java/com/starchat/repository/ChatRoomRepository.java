package com.starchat.repository;

import com.starchat.model.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomRepository {

    ChatRoom getChatRoomById(Long id);

    ChatRoom addChatRoom(ChatRoom chatRoom);

    void deleteChatRoomById (Long id);

    List<ChatRoom> getAllChatRooms();

    void updateUpdatedAtById(Long id, LocalDateTime updatedAt);
}
