package com.whatsapp.chatbot.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FirebaseService {

    private final Firestore firestore;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseMessaging firebaseMessaging;

    public FirebaseService() {
        this.firestore = FirestoreClient.getFirestore();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    /**
     * Verify Firebase ID token
     */
    public FirebaseToken verifyIdToken(String idToken) throws Exception {
        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (Exception e) {
            log.error("Failed to verify Firebase ID token", e);
            throw e;
        }
    }

    /**
     * Create a custom token for a user
     */
    public String createCustomToken(String uid, Map<String, Object> claims) throws Exception {
        try {
            return firebaseAuth.createCustomToken(uid, claims);
        } catch (Exception e) {
            log.error("Failed to create custom token for user: {}", uid, e);
            throw e;
        }
    }

    /**
     * Send push notification
     */
    public String sendNotification(String token, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putAllData(data)
                .build();

            return firebaseMessaging.send(message);
        } catch (Exception e) {
            log.error("Failed to send notification to token: {}", token, e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    /**
     * Send notification to topic
     */
    public String sendNotificationToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putAllData(data)
                .build();

            return firebaseMessaging.send(message);
        } catch (Exception e) {
            log.error("Failed to send notification to topic: {}", topic, e);
            throw new RuntimeException("Failed to send notification to topic", e);
        }
    }

    /**
     * Get Firestore instance
     */
    public Firestore getFirestore() {
        return firestore;
    }

    /**
     * Get Firebase Auth instance
     */
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    /**
     * Get Firebase Messaging instance
     */
    public FirebaseMessaging getFirebaseMessaging() {
        return firebaseMessaging;
    }
} 