package com.whatsapp.chatbot.repository;

import com.whatsapp.chatbot.entity.ChatMessage;
import com.whatsapp.chatbot.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, UUID> {
    
    List<ChatMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    
    Page<ChatMessage> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);
    
    List<ChatMessage> findByConversationAndDirection(Conversation conversation, ChatMessage.MessageDirection direction);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.conversation = :conversation AND cm.createdAt >= :since")
    List<ChatMessage> findRecentMessages(@Param("conversation") Conversation conversation, @Param("since") Instant since);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.message ILIKE %:keyword%")
    List<ChatMessage> searchByMessageContent(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.conversation = :conversation AND cm.direction = :direction")
    long countByConversationAndDirection(@Param("conversation") Conversation conversation, 
                                       @Param("direction") ChatMessage.MessageDirection direction);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.conversation = :conversation ORDER BY cm.createdAt DESC LIMIT 1")
    ChatMessage findLastMessageByConversation(@Param("conversation") Conversation conversation);
    
    List<ChatMessage> findByIntentId(UUID intentId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.createdAt BETWEEN :startDate AND :endDate")
    List<ChatMessage> findMessagesBetweenDates(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
} 