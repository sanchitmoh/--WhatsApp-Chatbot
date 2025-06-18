# 🗄️ Hibernate ORM Setup Guide for WhatsApp Chatbot

## ✅ What's Been Configured

Your project now has a complete Hibernate ORM setup with:

### 1. **Entity Models**
- **User** - User management and authentication
- **Conversation** - Chat session management
- **ChatMessage** - Individual message storage
- **Intent** - Bot response patterns

### 2. **Repository Layer**
- **UserRepository** - User data access
- **ConversationRepository** - Conversation management
- **ChatMessageJpaRepository** - Message operations
- **IntentRepository** - Intent management

### 3. **Hibernate Configuration**
- PostgreSQL dialect
- Automatic schema generation
- Performance optimizations
- Caching configuration

## 🚀 Database Schema

### Tables Created:

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    preferences TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Conversations table
CREATE TABLE conversations (
    id UUID PRIMARY KEY,
    conversation_id VARCHAR(255) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    phone_number VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_message TEXT,
    message_count INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Chat messages table
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES conversations(id),
    user_id VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    direction VARCHAR(50) NOT NULL,
    intent_id UUID REFERENCES intents(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Intents table
CREATE TABLE intents (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    trigger VARCHAR(255) NOT NULL,
    response TEXT NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);
```

## 🔧 Configuration Details

### Application Properties
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update          # Auto-create/update schema
    show-sql: true              # Show SQL in logs
    properties:
      hibernate:
        format_sql: true        # Format SQL for readability
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20        # Batch operations
        order_inserts: true     # Optimize inserts
        order_updates: true     # Optimize updates
        connection:
          provider_disables_autocommit: true
        cache:
          use_second_level_cache: true
          use_query_cache: true
```

## 📊 Entity Relationships

```
User (1) ←→ (N) Conversation (1) ←→ (N) ChatMessage
                ↓
Intent (1) ←→ (N) ChatMessage
```

### Key Features:
- **Lazy Loading** - Optimized data fetching
- **Cascade Operations** - Automatic related entity management
- **Audit Fields** - Created/updated timestamps
- **Version Control** - Optimistic locking
- **UUID Primary Keys** - Distributed system friendly

## 🛠️ Usage Examples

### 1. **User Operations**
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(String userId, String name, String email, String phone) {
        User user = User.create(userId, name, email, phone);
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
```

### 2. **Conversation Management**
```java
@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    
    public Conversation startConversation(User user, String phoneNumber) {
        String conversationId = UUID.randomUUID().toString();
        Conversation conversation = Conversation.create(conversationId, user, phoneNumber);
        return conversationRepository.save(conversation);
    }
    
    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUser(user);
    }
}
```

### 3. **Message Handling**
```java
@Service
public class MessageService {
    @Autowired
    private ChatMessageJpaRepository messageRepository;
    
    public ChatMessage saveInboundMessage(Conversation conversation, String userId, String message) {
        ChatMessage chatMessage = ChatMessage.inbound(conversation, userId, message);
        return messageRepository.save(chatMessage);
    }
    
    public List<ChatMessage> getConversationHistory(Conversation conversation) {
        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
    }
}
```

## 🔍 Query Examples

### Complex Queries
```java
// Find recent conversations for a user
Page<Conversation> recentConversations = conversationRepository
    .findRecentConversationsByUser(user, PageRequest.of(0, 10));

// Search messages by content
List<ChatMessage> searchResults = messageRepository
    .searchByMessageContent("hello");

// Get conversation statistics
long inboundCount = messageRepository
    .countByConversationAndDirection(conversation, MessageDirection.INBOUND);
```

## 🚨 Performance Optimizations

### 1. **Batch Operations**
```java
@Transactional
public void saveMessagesBatch(List<ChatMessage> messages) {
    messageRepository.saveAll(messages); // Uses batch_size: 20
}
```

### 2. **Lazy Loading**
```java
// Only loads conversations when accessed
User user = userRepository.findById(userId).orElseThrow();
List<Conversation> conversations = user.getConversations(); // Lazy loaded
```

### 3. **Query Optimization**
```java
// Use specific queries instead of loading all data
List<ChatMessage> recentMessages = messageRepository
    .findRecentMessages(conversation, Instant.now().minus(Duration.ofHours(24)));
```

## 🔒 Security & Validation

### 1. **Input Validation**
```java
@Entity
public class User {
    @Column(nullable = false, unique = true)
    @Email
    private String email;
    
    @Column(nullable = false)
    @Size(min = 2, max = 100)
    private String name;
}
```

### 2. **Audit Trail**
```java
@Entity
public class ChatMessage {
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
    
    @Version
    private Long version; // Optimistic locking
}
```

## 🧪 Testing

### 1. **Repository Tests**
```java
@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldSaveUser() {
        User user = User.create("user123", "John Doe", "john@example.com", "+1234567890");
        User saved = userRepository.save(user);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo("user123");
    }
}
```

### 2. **Integration Tests**
```java
@Testcontainers
@SpringBootTest
class ConversationServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    // Test conversation flow
    @Test
    void shouldHandleCompleteConversationFlow() {
        // Test user creation, conversation start, message exchange
    }
}
```

## 📈 Monitoring

### 1. **Hibernate Statistics**
```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
        jmx:
          enabled: true
```

### 2. **Query Performance**
```java
// Monitor slow queries
@QueryHints(value = {
    @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
    @QueryHint(name = HINT_CACHEABLE, value = "true")
})
List<ChatMessage> findOptimizedMessages(Conversation conversation);
```

## 🎯 Next Steps

1. **Run the application** - Tables will be auto-created
2. **Test the repositories** - Verify CRUD operations
3. **Implement services** - Add business logic
4. **Add controllers** - Create REST endpoints
5. **Configure caching** - Optimize performance
6. **Add migrations** - For production schema changes

## 🚨 Important Notes

- **Development**: `ddl-auto: update` (auto-creates schema)
- **Production**: `ddl-auto: validate` (validates schema)
- **Always use transactions** for data consistency
- **Monitor query performance** in production
- **Use appropriate indexes** for frequently queried fields

Your Hibernate ORM setup is now complete and ready for development! 🎉 