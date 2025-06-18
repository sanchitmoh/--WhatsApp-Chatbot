package com.whatsapp.chatbot.repository;

import com.whatsapp.chatbot.entity.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntentRepository extends JpaRepository<Intent, UUID> {
    Optional<Intent> findByName(String name);
    
    List<Intent> findByActiveTrue();
    
    @Query("SELECT i FROM Intent i WHERE i.active = true AND LOWER(i.trigger) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Intent> findMatchingIntents(String query);
    
    boolean existsByName(String name);
} 