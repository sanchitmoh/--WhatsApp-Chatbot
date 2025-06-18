package com.whatsapp.chatbot.repository;

import com.whatsapp.chatbot.entity.Conversation;
import com.whatsapp.chatbot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    
    Optional<Conversation> findByConversationId(String conversationId);
    
    List<Conversation> findByUser(User user);
    
    List<Conversation> findByUserAndStatus(User user, Conversation.ConversationStatus status);
    
    List<Conversation> findByPhoneNumber(String phoneNumber);
    
    List<Conversation> findByStatus(Conversation.ConversationStatus status);
    
    @Query("SELECT c FROM Conversation c WHERE c.createdAt >= :since")
    List<Conversation> findConversationsSince(@Param("since") Instant since);
    
    @Query("SELECT c FROM Conversation c WHERE c.user = :user ORDER BY c.updatedAt DESC")
    Page<Conversation> findRecentConversationsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.user = :user AND c.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") Conversation.ConversationStatus status);
    
    @Query("SELECT c FROM Conversation c WHERE c.lastMessage ILIKE %:keyword%")
    List<Conversation> searchByLastMessage(@Param("keyword") String keyword);
    
    boolean existsByConversationId(String conversationId);
} 