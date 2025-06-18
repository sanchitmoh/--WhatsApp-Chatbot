package com.whatsapp.chatbot.repository;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.whatsapp.chatbot.entity.ChatMessage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class ChatMessageRepositoryImpl {
    private static final String COLLECTION_NAME = "conversations";
    
    private final Firestore firestore;

    public ChatMessageRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    public ChatMessage save(ChatMessage message) {
        try {
            firestore.collection(COLLECTION_NAME)
                    .document(String.valueOf(message.getId()))
                    .set(message)
                    .get();
            return message;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save chat message", e);
        }
    }

    public List<ChatMessage> findByConversationId(String conversationId) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("conversationId", conversationId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .get()
                    .toObjects(ChatMessage.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch chat messages", e);
        }
    }

    public List<ChatMessage> findByUserId(String userId, int limit) {
        try {
            return firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(limit)
                    .get()
                    .get()
                    .toObjects(ChatMessage.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch user messages", e);
        }
    }

    public void deleteOlderThan(Timestamp timestamp) {
        try {
            firestore.collection(COLLECTION_NAME)
                    .whereLessThan("timestamp", timestamp)
                    .get()
                    .get()
                    .getDocuments()
                    .forEach(doc -> doc.getReference().delete());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete old messages", e);
        }
    }
}
