package com.whatsapp.chatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    @Column(nullable = false)
    private Integer messageCount;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    public enum ConversationStatus {
        ACTIVE,
        PAUSED,
        ENDED,
        BLOCKED
    }

    public static Conversation create(String conversationId, User user, String phoneNumber) {
        return Conversation.builder()
                .conversationId(conversationId)
                .user(user)
                .phoneNumber(phoneNumber)
                .status(ConversationStatus.ACTIVE)
                .messageCount(0)
                .build();
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.messageCount = this.messages.size();
        this.lastMessage = message.getMessage();
        this.updatedAt = Instant.now();
    }
} 