# 📱 WhatsApp Business API Setup Guide

## ✅ Your WhatsApp API Credentials

Your WhatsApp Business API is now configured with:

```bash
WHATSAPP_ACCESS_TOKEN=YOUR_ACCESS_TOKEN_HERE
WHATSAPP_PHONE_NUMBER_ID=YOUR_PHONE_NUMBER_ID_HERE
WHATSAPP_BUSINESS_ACCOUNT_ID=YOUR_BUSINESS_ACCOUNT_ID_HERE
```

## 🔧 Configuration Files Created

### 1. **WhatsAppConfig.java**
- Centralized configuration management
- URL builders for different API endpoints
- Credential management

### 2. **WhatsAppService.java**
- Send text messages
- Send template messages
- Send media messages
- Webhook processing
- Phone number information

### 3. **WhatsAppController.java**
- Webhook verification endpoint
- Message sending endpoints
- Health check endpoint

### 4. **DTO Classes**
- `WhatsAppMessageRequest.java` - API request structure
- `WhatsAppMessageResponse.java` - API response structure
- `WhatsAppWebhookRequest.java` - Webhook data structure

## 🚀 API Endpoints Available

### **Webhook Endpoints**
```
GET  /api/whatsapp/webhook    - Webhook verification
POST /api/whatsapp/webhook    - Receive incoming messages
```

### **Message Sending Endpoints**
```
POST /api/whatsapp/send/text     - Send text message
POST /api/whatsapp/send/template - Send template message
POST /api/whatsapp/send/media    - Send media message
```

### **Information Endpoints**
```
GET /api/whatsapp/phone-info - Get phone number information
GET /api/whatsapp/health     - Health check
```

## 📝 Usage Examples

### **1. Send a Text Message**
```bash
curl -X POST "http://localhost:8080/api/whatsapp/send/text" \
  -d "to=1234567890" \
  -d "message=Hello from WhatsApp Chatbot!"
```

### **2. Send a Template Message**
```bash
curl -X POST "http://localhost:8080/api/whatsapp/send/template" \
  -d "to=1234567890" \
  -d "templateName=hello_world" \
  -d "languageCode=en_US"
```

### **3. Send a Media Message**
```bash
curl -X POST "http://localhost:8080/api/whatsapp/send/media" \
  -d "to=1234567890" \
  -d "mediaId=MEDIA_ID_HERE" \
  -d "caption=Check out this image!"
```

### **4. Check Phone Number Info**
```bash
curl "http://localhost:8080/api/whatsapp/phone-info"
```

### **5. Health Check**
```bash
curl "http://localhost:8080/api/whatsapp/health"
```

## 🔗 Webhook Setup

### **1. Set Verify Token**
You need to set a verify token in your environment:
```bash
WHATSAPP_VERIFY_TOKEN=your_custom_verify_token_here
```

### **2. Configure Webhook URL**
In your WhatsApp Business API dashboard, set:
- **Webhook URL**: `https://your-domain.com/api/whatsapp/webhook`
- **Verify Token**: Same as `WHATSAPP_VERIFY_TOKEN`

### **3. Subscribe to Events**
Subscribe to these events:
- `messages`
- `message_deliveries`
- `message_reads`

## 🛠️ Integration with Your Chatbot

### **1. Message Processing**
```java
@Service
public class ChatbotService {
    @Autowired
    private WhatsAppService whatsAppService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    
    public void handleIncomingMessage(String from, String message) {
        // Find or create user
        User user = userRepository.findByPhoneNumber(from)
            .orElseGet(() -> createNewUser(from));
        
        // Find or create conversation
        Conversation conversation = conversationRepository
            .findByPhoneNumber(from)
            .orElseGet(() -> startNewConversation(user, from));
        
        // Process message with your chatbot logic
        String response = processMessage(message);
        
        // Send response
        whatsAppService.sendTextMessage(from, response);
    }
}
```

### **2. Template Messages**
```java
// Send welcome message
whatsAppService.sendTemplateMessage(
    phoneNumber, 
    "welcome_message", 
    "en_US"
);

// Send order confirmation
whatsAppService.sendTemplateMessage(
    phoneNumber, 
    "order_confirmation", 
    "en_US"
);
```



## 📊 Monitoring & Logging

### **1. Application Logs**
```yaml
logging:
  level:
    com.whatsapp.chatbot: DEBUG
    org.springframework.web: INFO
```

### **2. WhatsApp API Logs**
- All API calls are logged
- Webhook events are tracked
- Error responses are captured

### **3. Health Monitoring**
```bash
# Check WhatsApp service health
curl "http://localhost:8080/api/whatsapp/health"

# Check phone number status
curl "http://localhost:8080/api/whatsapp/phone-info"
```

## 🚨 Error Handling

### **Common Errors**
1. **401 Unauthorized** - Invalid access token
2. **403 Forbidden** - Invalid webhook token
3. **429 Too Many Requests** - Rate limit exceeded
4. **400 Bad Request** - Invalid message format

### **Error Response Format**
```json
{
  "error": {
    "message": "Error description",
    "type": "OAuthException",
    "code": 190,
    "error_subcode": 33
  }
}
```



### **1. Test  Setup**
```bash
# Start your application
mvn spring-boot:run

# Test health endpoint
curl "http://localhost:8080/api/whatsapp/health"

# Test phone info
curl "http://localhost:8080/api/whatsapp/phone-info"
```

### **2. Configure Webhook**
1. Set `WHATSAPP_VERIFY_TOKEN` environment variable
2. Configure webhook URL in WhatsApp Business API
3. Subscribe to message events

### **3. Test Message Sending**
```bash
# Send a test message
curl -X POST "http://localhost:8080/api/whatsapp/send/text" \
  -d "to=YOUR_TEST_PHONE_NUMBER" \
  -d "message=Hello from your chatbot!"
```

### **4. Integrate with Your Bot Logic**
- Connect webhook processing to your chatbot
- Implement message routing
- Add conversation management
- Set up user authentication

## 📚 API Documentation

### **WhatsApp Business API v17.0**
- [Official Documentation](https://developers.facebook.com/docs/whatsapp)
- [API Reference](https://developers.facebook.com/docs/whatsapp/api/reference)
- [Webhook Guide](https://developers.facebook.com/docs/whatsapp/webhook)

### **Message Types Supported**
- Text messages
- Template messages
- Media messages (image, audio, video, document)
- Interactive messages
- Location messages
- Contact messages

Your WhatsApp Business API is now fully configured and ready to use! 🎉

## 🚨 Important Notes

1. **Phone Number Format**: Use international format (e.g., `1234567890`)
2. **Message Limits**: Text messages limited to 4096 characters
3. **Rate Limits**: Respect WhatsApp's rate limiting
4. **Templates**: Pre-approved templates required for business messages
5. **Testing**: Use test phone numbers during development

Start your application and test the endpoints to verify everything is working correctly! 