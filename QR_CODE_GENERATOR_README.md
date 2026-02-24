# QR Code Generator App - Complete Implementation

## 📱 Overview

A feature-complete QR Code generation Android application built with **Clean Architecture**, **MVVM**, and **Jetpack Compose**. The app follows a intuitive 4-step workflow for creating customizable QR codes.

## 🎯 4-Step Workflow

### Step 1: Select QR Type
Choose from 12+ QR code types:
- **URL** - Web links
- **WiFi** - Network credentials
- **Contact** - vCard contact information
- **Email** - Email addresses
- **Phone** - Phone numbers
- **SMS** - Text messages
- **Music** - Music service links
- **PDF** - Document links
- **Image** - Image links
- **Social Media** - Facebook, Instagram profiles
- **vCard** - Business card data

### Step 2: Add Content
Enter the specific content for the selected QR type:
- Type-specific input fields with helpful placeholders
- Input validation to ensure non-empty content
- Content preview and confirmation before proceeding

### Step 3: Design Customization
Customize the QR code appearance:
- **Background Colors** - 5 preset color options (White, Light Gray, Black, Light Blue, Light Purple)
- **Code Colors** - 5 color options (Black, Blue, Red, Green, Purple)
- **Size Selection** - Adjustable from 256px to 1024px
- **Error Correction Level** - Low (7%), Medium (15%), Quartile (25%), High (30%)
- **Live Preview** - See changes in real-time

### Step 4: Generate & Save
- Display final QR code
- Show QR code metadata (type, size, error level)
- Save to device
- Share QR code
- Create new QR code

## 🏗️ Architecture

### Clean Architecture Layers

#### **Domain Layer** (Business Logic)
```
domain/
├── model/
│   └── QRCode.kt (Data models, enums, sealed classes)
├── repository/
│   └── QRCodeRepository.kt (Repository interface)
└── usecase/
    ├── GenerateQRUseCase.kt
    ├── SaveQRUseCase.kt
    └── GetQRHistoryUseCase.kt
```

**Key Models:**
- `QRCode` - Complete QR code data with metadata
- `QRSourceType` - Enum for 12+ QR types
- `QRDesign` - Customization settings
- `ErrorCorrectionLevel` - Error correction options
- `Result<T>` - Sealed class for handling async operations

#### **Data Layer** (Data Management)
```
data/
├── local/
│   ├── QRCodeEntity.kt (Room entity)
│   ├── QRCodeDao.kt (Database DAO)
│   └── AppDatabase.kt (Room database)
├── remote/
│   ├── QRCodeDto.kt (API DTO)
│   └── QRCodeApi.kt (Retrofit interface)
├── mapper/
│   └── QRCodeMapper.kt (Domain ↔ Entity ↔ DTO conversion)
├── repository_impl/
│   └── QRCodeRepositoryImpl.kt (Repository implementation)
└── sync/
    └── SyncManagerImpl.kt (Data synchronization)
```

**Features:**
- **Room Database** - Local persistence with 6 CRUD operations
- **Retrofit API** - Remote data source (ready for integration)
- **Mapper Pattern** - Clean conversion between layers
- **Sync Manager** - Background data synchronization

#### **Presentation Layer** (UI & State Management)
```
presentation/
├── ui/
│   ├── QRGeneratorUiState.kt (Sealed classes for UI state)
│   ├── QRTypeSelectionScreen.kt (Step 1)
│   ├── QRContentInputScreen.kt (Step 2)
│   ├── QRDesignCustomizationScreen.kt (Step 3)
│   ├── QRGenerationResultScreen.kt (Step 4)
│   └── QRGeneratorNavigation.kt (Navigation logic)
└── viewmodel/
    └── QRGeneratorViewModel.kt (State & event management)
```

**Features:**
- **MVVM Pattern** - Clear separation of concerns
- **StateFlow** - Reactive state management
- **Jetpack Compose** - Modern UI framework
- **Event-Driven Architecture** - Clean event handling

## 🛠️ Technology Stack

### Core Android
- **Jetpack Compose** - UI framework (Material Design 3)
- **Lifecycle** - ViewModel, LiveData
- **Navigation** - Compose navigation
- **Room** - Local database
- **Coroutines** - Async operations

### External Libraries
- **Retrofit 2** - REST API client
- **OkHttp 3** - HTTP client
- **Gson** - JSON serialization
- **Hilt** - Dependency injection
- **ZXing** - QR code generation
- **Coil** - Image loading

## 📁 Project Structure

```
app/src/main/
├── java/com/example/qrgrenertor/
│   ├── domain/
│   │   ├── model/
│   │   ├── repository/
│   │   └── usecase/
│   ├── data/
│   │   ├── local/
│   │   ├── remote/
│   │   ├── mapper/
│   │   ├── repository_impl/
│   │   └── sync/
│   ├── presentation/
│   │   ├── ui/
│   │   └── viewmodel/
│   ├── MainActivity.kt
│   ├── QRGeneratorApplication.kt
│   └── AppModule.kt (Hilt DI configuration)
└── res/
    └── AndroidManifest.xml
```

## 🔧 Key Components

### QRCodeRepository Interface
```kotlin
interface QRCodeRepository {
    suspend fun generateQR(content: String): Result<QRCode>
    suspend fun saveQR(qrCode: QRCode): Result<Unit>
    suspend fun getQRHistory(): Result<List<QRHistory>>
    suspend fun getQRById(id: String): Result<QRCode>
    suspend fun deleteQR(id: String): Result<Unit>
    suspend fun syncData(): Result<Unit>
}
```

### QRGeneratorViewModel
- Manages 4-step workflow state
- Handles user events
- Coordinates with UseCases
- Provides StateFlow for reactive updates

### UI State Machine
```kotlin
sealed class QRGeneratorUiState {
    object Idle
    data class StepTypeSelection(...)
    data class StepContentInput(...)
    data class StepDesignCustomization(...)
    data class StepQRGeneration(...)
    data class Success(...)
    data class Error(...)
    object Loading
}
```

## 💾 Database Schema

**Table: qr_codes**
```sql
CREATE TABLE qr_codes (
    id TEXT PRIMARY KEY,
    content TEXT,
    sourceType TEXT,
    backgroundColor INTEGER,
    codeColor INTEGER,
    size INTEGER,
    errorCorrectionLevel TEXT,
    imageUrl TEXT,
    createdAt INTEGER,
    isSynced INTEGER
)
```

## 🔐 Dependency Injection

Hilt provides automatic injection of:
- `QRCodeRepository`
- `GenerateQRUseCase`, `SaveQRUseCase`, `GetQRHistoryUseCase`
- `AppDatabase` & `QRCodeDao`
- `QRCodeApi` (Retrofit)
- `SyncManager`

## 🎨 UI Features

- **Material Design 3** - Modern Material You design
- **Dark Mode Support** - Dynamic theming
- **Responsive Layout** - Adapts to different screen sizes
- **Smooth Transitions** - Between workflow steps
- **Real-time Preview** - Design changes reflected immediately
- **Loading States** - Visual feedback during operations
- **Error Handling** - User-friendly error messages

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 21+
- Kotlin 1.9+

### Build Instructions
```bash
# Clone and open in Android Studio
# Sync Gradle files
./gradlew assembleDebug

# Run on emulator or device
./gradlew installDebug
```

### Configuration
1. Update `AppModule.kt` with actual API base URL
2. Configure Room database encryption if needed
3. Set up Retrofit interceptors for authentication

## 📝 File Manifest

### Domain Layer Files
- `domain/model/QRCode.kt` - Models and enums
- `domain/repository/QRCodeRepository.kt` - Repository interface
- `domain/usecase/GenerateQRUseCase.kt` - QR generation logic
- `domain/usecase/SaveQRUseCase.kt` - Save QR code
- `domain/usecase/GetQRHistoryUseCase.kt` - Retrieve history

### Data Layer Files
- `data/local/QRCodeEntity.kt` - Room entity
- `data/local/QRCodeDao.kt` - Database access object
- `data/local/AppDatabase.kt` - Room database configuration
- `data/remote/QRCodeDto.kt` - API data transfer object
- `data/remote/QRCodeApi.kt` - Retrofit API interface
- `data/mapper/QRCodeMapper.kt` - Data transformation functions
- `data/repository_impl/QRCodeRepositoryImpl.kt` - Repository implementation
- `data/sync/SyncManagerImpl.kt` - Data sync manager

### Presentation Layer Files
- `presentation/ui/QRGeneratorUiState.kt` - UI state models
- `presentation/ui/QRTypeSelectionScreen.kt` - Step 1 UI
- `presentation/ui/QRContentInputScreen.kt` - Step 2 UI
- `presentation/ui/QRDesignCustomizationScreen.kt` - Step 3 UI
- `presentation/ui/QRGenerationResultScreen.kt` - Step 4 UI
- `presentation/ui/QRGeneratorNavigation.kt` - Navigation logic
- `presentation/viewmodel/QRGeneratorViewModel.kt` - State management

### App Configuration Files
- `MainActivity.kt` - Main activity with Compose setup
- `QRGeneratorApplication.kt` - Application class with Hilt
- `AppModule.kt` - Dependency injection configuration
- `build.gradle.kts` - Dependencies and build config
- `AndroidManifest.xml` - App manifest

## 🔄 Data Flow

```
User Input (UI Event)
    ↓
QRGeneratorViewModel.onEvent()
    ↓
Update UI State
    ↓
Trigger UseCase
    ↓
QRCodeRepository (Data Layer)
    ↓
Local/Remote Data Sources
    ↓
Result<T> (Success/Error)
    ↓
Update UI with new State
    ↓
Recompose (Jetpack Compose)
```

## 🧪 Testing Recommendations

- **Unit Tests** - ViewModel logic, UseCase validation
- **Integration Tests** - Repository with local/remote sources
- **UI Tests** - Screen navigation, user interactions
- **Database Tests** - Room DAO operations

## 🐛 Known Limitations

- Actual QR code image generation needs ZXing implementation
- API endpoints require backend implementation
- Image saving/sharing requires additional permissions
- Network sync requires proper error handling

## 📚 Future Enhancements

1. **QR Code Generation** - Integrate ZXing to generate actual QR images
2. **Image Export** - Save QR as PNG/JPEG with custom watermarks
3. **Batch Generation** - Create multiple QR codes at once
4. **Analytics** - Track QR code scans
5. **Cloud Sync** - Firebase integration for backup
6. **Advanced Sharing** - Share to social media
7. **QR Scanning** - Add QR code scanner capability
8. **Templates** - Pre-designed QR templates

## 📄 License

This project is part of the QR Generator application suite.

---

**Created**: 2026-02-23  
**Architecture**: Clean Architecture + MVVM  
**Framework**: Jetpack Compose  
**Minimum SDK**: 21
