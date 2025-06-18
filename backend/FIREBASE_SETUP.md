# 🔥 Firebase Setup Guide for WhatsApp Chatbot

## Prerequisites
- Google account
- Java 17+ 
- Maven
- Your Firebase project created

## Step 1: Firebase Console Setup

### 1.1 Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Enter project name: `whatsapp-chatbot-{your-name}`
4. Enable Google Analytics (recommended)
5. Click **"Create project"**

### 1.2 Enable Firebase Services
1. **Authentication**
   - Go to **Authentication** → **Get started**
   - Click **"Sign-in method"** tab
   - Enable **Email/Password** authentication
   - (Optional) Enable **Google** sign-in

2. **Firestore Database**
   - Go to **Firestore Database** → **Create database**
   - Choose **"Start in test mode"** (we'll secure it later)
   - Select a location close to your users
   - Click **"Done"**

3. **Cloud Messaging (FCM)**
   - Go to **Cloud Messaging** → **Get started**
   - This enables push notifications

### 1.3 Generate Service Account Key
1. Click the gear icon ⚙️ next to "Project Overview"
2. Select **"Project settings"**
3. Go to **"Service accounts"** tab
4. Click **"Generate new private key"**
5. Click **"Generate key"**
6. Download the JSON file
7. Rename it to `firebase-service-account.json`

## Step 2: Project Configuration

### 2.1 Place Service Account File
Place the `firebase-service-account.json` file in your project root:
```bash
# Firebase Configuration
FIREBASE_SERVICE_ACCOUNT_PATH=firebase-service-account.json
FIREBASE_PROJECT_ID=your_firebase_project_id_here

# Other required variables
POSTGRES_URL=jdbc:postgresql://localhost:5432/chatbot
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
PORT=8080
WHATSAPP_ACCESS_TOKEN=your_whatsapp_access_token_here
WHATSAPP_VERIFY_TOKEN=your_whatsapp_verify_token_here
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id_here
API_KEY=your_api_key_here
```

### 2.2 Environment Variables
Create a `.env` file in the backend directory with these variables:

```bash
# Firebase Configuration
FIREBASE_SERVICE_ACCOUNT_PATH=firebase-service-account.json
FIREBASE_PROJECT_ID=your_firebase_project_id_here

# Other required variables
POSTGRES_URL=jdbc:postgresql://localhost:5432/chatbot
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
PORT=8080
WHATSAPP_ACCESS_TOKEN=your_whatsapp_access_token_here
WHATSAPP_VERIFY_TOKEN=your_whatsapp_verify_token_here
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id_here
API_KEY=your_api_key_here
```

### 2.3 Update Project ID
Replace `your_firebase_project_id_here` with your actual Firebase project ID from the Firebase console.

## Step 3: Verify Setup

### 3.1 Build the Project
```bash
cd backend
mvn clean compile
```

### 3.2 Run the Application
```bash
mvn spring-boot:run
```

### 3.3 Check Logs
Look for these success messages in the logs:
```
Firebase Admin SDK initialized successfully
```

## Step 4: Test Firebase Integration

### 4.1 Test Authentication
The `FirebaseService` class provides these methods:
- `verifyIdToken()` - Verify Firebase ID tokens
- `createCustomToken()` - Create custom tokens
- `sendNotification()` - Send push notifications
- `sendNotificationToTopic()` - Send notifications to topics

### 4.2 Test Firestore
You can access Firestore through the `FirebaseService.getFirestore()` method.

## Step 5: Security Rules (Optional)

### 5.1 Firestore Security Rules
In Firebase Console → Firestore Database → Rules, update rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Allow authenticated users to read chat data
    match /chats/{chatId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Troubleshooting

### Common Issues

1. **"Failed to initialize Firebase Admin SDK"**
   - Check if `firebase-service-account.json` exists in project root
   - Verify the file path in `FIREBASE_SERVICE_ACCOUNT_PATH`
   - Ensure the JSON file is valid

2. **"Project ID not found"**
   - Verify `FIREBASE_PROJECT_ID` environment variable
   - Check the project ID in Firebase console

3. **Permission denied errors**
   - Ensure the service account has proper permissions
   - Check Firestore security rules

### Getting Help
- Check the application logs for detailed error messages
- Verify all environment variables are set correctly
- Ensure Firebase services are enabled in the console

## Next Steps

After successful setup, you can:
1. Implement user authentication in your controllers
2. Store chat data in Firestore
3. Send push notifications for new messages
4. Implement real-time features using Firestore listeners

## Security Notes

⚠️ **Important Security Considerations:**
- Never commit `firebase-service-account.json` to version control
- Use environment variables for sensitive configuration
- Regularly rotate service account keys
- Implement proper authentication and authorization
- Set up Firestore security rules 