# QR Code Generator - Architecture Diagram

## 🏗️ Complete System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                          │
│                      (Jetpack Compose UI)                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              QRGeneratorViewModel                             │  │
│  │  ┌────────────────────────────────────────────────────────┐ │  │
│  │  │ StateFlow<QRGeneratorUiState>                          │ │  │
│  │  │ onEvent(QRGeneratorEvent)                             │ │  │
│  │  └────────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                   ▲                                 │
│                                   │                                 │
│  ┌──────────────────────────────┬─┴────────────────────────────┐  │
│  │      QRGeneratorNavigation    │                             │  │
│  ├───────────────────────────────┼──────────────────────────────┤  │
│  │                               │                              │  │
│  │ Step 1: Type Selection  Step 2: Content  Step 3: Design Step 4:│
│  │ ┌────────────────────┐ ┌─────────────┐ ┌──────────────┐ Result│
│  │ │ QRTypeSelection    │ │ ContentInput│ │Customization │ ┌──────┤
│  │ │ Screen             │ │ Screen      │ │ Screen       │ │Result│
│  │ │                    │ │             │ │              │ │Screen│
│  │ │ - 12 QR Types     │ │ - Input box │ │ - Colors     │ │      │
│  │ │ - Grid Layout     │ │ - Validation│ │ - Size       │ │ - QR │
│  │ │ - Selection       │ │ - Back/Next │ │ - Error Level│ │ - Info│
│  │ │                    │ │             │ │ - Preview    │ │ - Save│
│  │ └────────────────────┘ └─────────────┘ │ - Back/Gener│ │ - Share
│  │                                         └──────────────┘ │      │
│  │                                                           └──────┤
│  └─────────────────────────────────────────────────────────────────┘
│                              │
│                              │ Uses
│                              ▼
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │               QRGeneratorUiState (Sealed Class)              │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │ • Idle                                                        │  │
│  │ • StepTypeSelection(selectedType)                            │  │
│  │ • StepContentInput(selectedType, content)                    │  │
│  │ • StepDesignCustomization(type, content, design)             │  │
│  │ • StepQRGeneration(qrCode, design)                           │  │
│  │ • Success(qrCode)                                            │  │
│  │ • Error(message)                                             │  │
│  │ • Loading                                                     │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ Calls
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          DOMAIN LAYER                               │
│                      (Business Logic)                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                   UseCases (Interfaces)                      │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                                                              │  │
│  │  ┌─────────────────┐  ┌──────────────┐  ┌──────────────┐  │  │
│  │  │GenerateQRUseCase│  │ SaveQRUseCase│  │ GetQRHistory │  │  │
│  │  │                 │  │ UseCase      │  │ UseCase      │  │  │
│  │  │ invoke(content, │  │              │  │              │  │  │
│  │  │ design):Result  │  │ invoke(qr):  │  │ invoke():    │  │  │
│  │  │ <QRCode>        │  │ Result<Unit> │  │ Result<List> │  │  │
│  │  └─────────────────┘  └──────────────┘  └──────────────┘  │  │
│  │           │                   │                 │           │  │
│  │           └───────────────────┼─────────────────┘           │  │
│  │                               ▼                             │  │
│  │          ┌────────────────────────────────────┐             │  │
│  │          │   QRCodeRepository (Interface)     │             │  │
│  │          ├────────────────────────────────────┤             │  │
│  │          │ • generateQR(content)              │             │  │
│  │          │ • saveQR(qrCode)                   │             │  │
│  │          │ • getQRHistory()                   │             │  │
│  │          │ • getQRById(id)                    │             │  │
│  │          │ • deleteQR(id)                     │             │  │
│  │          │ • syncData()                       │             │  │
│  │          └────────────────────────────────────┘             │  │
│  │                                                              │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    Domain Models                             │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │  │
│  │  │ QRCode       │  │ QRSourceType │  │ QRDesign         │ │  │
│  │  ├──────────────┤  ├──────────────┤  ├──────────────────┤ │  │
│  │  │• id          │  │• URL         │  │• backgroundColor │ │  │
│  │  │• content     │  │• WiFi        │  │• codeColor       │ │  │
│  │  │• sourceType  │  │• Contact     │  │• size            │ │  │
│  │  │• designSettings│ │• Email       │  │• errorCorrLevel  │ │  │
│  │  │• imageUrl    │  │• Phone       │  └──────────────────┘ │  │
│  │  │• createdAt   │  │• SMS         │                        │  │
│  │  │• isSynced    │  │• Music       │  ┌──────────────────┐  │  │
│  │  └──────────────┘  │• PDF         │  │ErrorCorrectionL. │  │  │
│  │                    │• Image       │  ├──────────────────┤  │  │
│  │  ┌──────────────┐  │• Facebook    │  │• LOW (7%)        │  │  │
│  │  │ QRHistory    │  │• Instagram   │  │• MEDIUM (15%)    │  │  │
│  │  ├──────────────┤  │• vCard       │  │• QUARTILE (25%)  │  │  │
│  │  │• id          │  └──────────────┘  │• HIGH (30%)      │  │  │
│  │  │• qrCode      │                    └──────────────────┘  │  │
│  │  │• lastAccess  │                                          │  │
│  │  └──────────────┘  ┌──────────────────┐                   │  │
│  │                    │Result<T>          │                   │  │
│  │  ┌──────────────┐  ├──────────────────┤                   │  │
│  │  │ QRSource     │  │• Success(data)   │                   │  │
│  │  ├──────────────┤  │• Error(exception)│                   │  │
│  │  │• type        │  │• Loading         │                   │  │
│  │  │• value       │  └──────────────────┘                   │  │
│  │  │• metadata    │                                          │  │
│  │  └──────────────┘                                          │  │
│  │                                                              │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ Implements
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          DATA LAYER                                 │
│              (Repository Implementation & Data Sources)             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │           QRCodeRepositoryImpl (Implementation)               │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                           │                                  │  │
│  │                ┌──────────┴──────────┐                       │  │
│  │                ▼                      ▼                       │  │
│  │      ┌────────────────┐      ┌────────────────┐              │  │
│  │      │Local Data      │      │Remote Data     │              │  │
│  │      │Sources         │      │Sources         │              │  │
│  │      ├────────────────┤      ├────────────────┤              │  │
│  │      │ QRCodeDao      │      │ QRCodeApi      │              │  │
│  │      │ (Room Database)│      │ (Retrofit)     │              │  │
│  │      │                │      │                │              │  │
│  │      │ • insert       │      │ • generate     │              │  │
│  │      │ • query        │      │ • get          │              │  │
│  │      │ • delete       │      │ • save         │              │  │
│  │      │ • markSynced   │      │ • sync         │              │  │
│  │      └────────────────┘      │ • delete       │              │  │
│  │            │                 └────────────────┘              │  │
│  │            ▼                         │                       │  │
│  │      ┌────────────────┐             ▼                        │  │
│  │      │ AppDatabase    │      ┌────────────────┐              │  │
│  │      │                │      │ Retrofit       │              │  │
│  │      │ @Database      │      │ Configuration  │              │  │
│  │      │ entities:      │      │                │              │  │
│  │      │ [QRCodeEntity] │      │ Base URL:      │              │  │
│  │      │ version: 1     │      │ Interceptors:  │              │  │
│  │      └────────────────┘      │ Converters:    │              │  │
│  │            │                 └────────────────┘              │  │
│  │            ▼                                                  │  │
│  │      ┌────────────────┐                                      │  │
│  │      │ Room Database  │                                      │  │
│  │      │ qr_app_db      │                                      │  │
│  │      │                │                                      │  │
│  │      │ Table:         │                                      │  │
│  │      │ qr_codes       │                                      │  │
│  │      └────────────────┘                                      │  │
│  │                                                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              Data Type Conversion (Mappers)                  │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                                                               │  │
│  │     QRCodeEntity ←→ QRCode (Domain) ←→ QRCodeDto             │  │
│  │     (Room Entity)    (Business Model)   (API DTO)            │  │
│  │                                                               │  │
│  │     Extension Functions:                                     │  │
│  │     • QRCodeEntity.toDomain()                                │  │
│  │     • QRCodeDto.toDomain()                                   │  │
│  │     • QRCode.toEntity()                                      │  │
│  │     • QRCode.toDto()                                         │  │
│  │                                                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                   SyncManager (Data Sync)                    │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                                                               │  │
│  │  Interface: SyncManager                                      │  │
│  │  ├─ markForSync(id)                                          │  │
│  │  └─ syncAll()                                                │  │
│  │                                                               │  │
│  │  Implementation: SyncManagerImpl                              │  │
│  │  ├─ Coordinates with DAO & API                               │  │
│  │  ├─ Handles offline-first sync                               │  │
│  │  └─ Manages sync state                                       │  │
│  │                                                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ Configured by
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      DEPENDENCY INJECTION LAYER                     │
│                        (Hilt Configuration)                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              AppModule (Hilt Module)                          │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                                                               │  │
│  │  @Provides                                                   │  │
│  │  ┌─────────────────────────────────────────────────────┐    │  │
│  │  │ Retrofit  ──→  OkHttpClient  ──→  QRCodeApi        │    │  │
│  │  └─────────────────────────────────────────────────────┘    │  │
│  │                                                               │  │
│  │  @Provides (Singleton)                                       │  │
│  │  ┌─────────────────────────────────────────────────────┐    │  │
│  │  │ AppDatabase  ──→  QRCodeDao                         │    │  │
│  │  └─────────────────────────────────────────────────────┘    │  │
│  │                                                               │  │
│  │  @Provides (Singleton)                                       │  │
│  │  ┌─────────────────────────────────────────────────────┐    │  │
│  │  │ SyncManager  ──→  SyncManagerImpl                    │    │  │
│  │  └─────────────────────────────────────────────────────┘    │  │
│  │                                                               │  │
│  │  @Provides (Singleton)                                       │  │
│  │  ┌─────────────────────────────────────────────────────┐    │  │
│  │  │ QRCodeRepository  ──→  QRCodeRepositoryImpl         │    │  │
│  │  └─────────────────────────────────────────────────────┘    │  │
│  │                                                               │  │
│  │  UseCases auto-injected with Repository                     │  │
│  │  ┌─────────────────────────────────────────────────────┐    │  │
│  │  │ GenerateQRUseCase                                   │    │  │
│  │  │ SaveQRUseCase                                       │    │  │
│  │  │ GetQRHistoryUseCase                                 │    │  │
│  │  └─────────────────────────────────────────────────────┘    │  │
│  │                                                               │  │
│  │  ViewModel auto-injected with UseCases                      │  │
│  │  ┌─────────────────────────────────────────────────────┐    │  │
│  │  │ QRGeneratorViewModel (Activity Scoped)              │    │  │
│  │  └─────────────────────────────────────────────────────┘    │  │
│  │                                                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ Bootstrapped by
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      APPLICATION CLASS                              │
│                    (Entry Point)                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  @HiltAndroidApp                                                    │
│  class QRGeneratorApplication : Application()                       │
│                                                                      │
│  Initializes:                                                       │
│  • Hilt dependency injection                                       │
│  • Database instance                                               │
│  • API client configuration                                        │
│  • ViewModel factory                                               │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        USER INTERACTION                             │
│                     (Touch Event on UI)                             │
└──────────────────────────────────────┬──────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    UI SCREEN (Jetpack Compose)                      │
│             Calls: viewModel.onEvent(event)                         │
└──────────────────────────────────────┬──────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    QRGeneratorViewModel                             │
│  onEvent(QRGeneratorEvent)                                          │
│  └─ Validate input                                                  │
│  └─ Update internal state                                           │
│  └─ Call UseCase if needed                                          │
└──────────────────────────────────────┬──────────────────────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
        ┌─────────────────┐   ┌─────────────────┐  ┌────────────────┐
        │GenerateQRUseCase│   │SaveQRUseCase    │  │GetHistoryUseCase
        └────────┬────────┘   └────────┬────────┘  └────────┬────────┘
                 │                      │                   │
                 └──────────────────────┼───────────────────┘
                                        │
                                        ▼
        ┌───────────────────────────────────────────────────────────┐
        │            QRCodeRepository (Interface)                   │
        │                                                            │
        │  Decides: Local or Remote or Both?                        │
        └───────────────────────────────────────────────────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
        ┌──────────────────┐   ┌──────────────────┐ ┌──────────────┐
        │   Room Database  │   │  Retrofit API    │ │  SyncManager │
        │                  │   │                  │ │              │
        │ • Query Data     │   │ • Send Request   │ │• Coordinates │
        │ • Save Data      │   │ • Parse Response │ │• Offline-first
        │ • Update Status  │   │ • Handle Errors  │ └──────────────┘
        │ • Delete Data    │   │                  │
        └────────┬─────────┘   └────────┬─────────┘
                 │                      │
                 └──────────────────────┼──────────────────┐
                                        │                  │
                        ┌───────────────┘                  ▼
                        │                        ┌────────────────┐
                        │                        │  Convert Data  │
                        │                        │  (QRCodeMapper)│
                        │                        │                │
                        │                        │Entity → Domain │
                        │                        │Dto → Domain    │
                        │                        │Domain → Entity │
                        │                        │Domain → Dto    │
                        │                        └────────┬───────┘
                        │                                 │
                        └─────────────────────────────────┘
                                        │
                                        ▼
                        ┌────────────────────────────────┐
                        │   Result<T> (Success/Error)    │
                        └────────────────┬───────────────┘
                                        │
                                        ▼
                        ┌────────────────────────────────┐
                        │   Return to ViewModel          │
                        │   Update _uiState.value        │
                        └────────────────┬───────────────┘
                                        │
                                        ▼
                        ┌────────────────────────────────┐
                        │   StateFlow Update             │
                        │   UI Observes State Change     │
                        └────────────────┬───────────────┘
                                        │
                                        ▼
                        ┌────────────────────────────────┐
                        │   Jetpack Compose Recompose    │
                        │   Render New UI                │
                        └────────────────────────────────┘
```

---

## 📊 State Machine Diagram

```
                         ┌─────────────┐
                         │    START    │
                         └──────┬──────┘
                                │
                                ▼
                    ┌────────────────────────┐
                    │  Idle / Initialization │
                    └────────────┬───────────┘
                                 │
                                 ▼
        ┌────────────────────────────────────────┐
        │  StepTypeSelection                      │
        │  - Show 12 QR types                    │
        │  - Wait for user selection             │
        │  - Enable Next button                  │
        └────┬───────────────────────────────────┘
             │
             ├─ GoToPreviousStep ──→ (stays at Step 1)
             │
             └─ GoToNextStep ──────→ (on type selected)
                                     │
                                     ▼
                    ┌────────────────────────────┐
                    │  StepContentInput          │
                    │  - Show type-specific form │
                    │  - Get user content        │
                    │  - Enable Next button      │
                    └────┬───────────────────────┘
                         │
                         ├─ GoToPreviousStep ──→ StepTypeSelection
                         │
                         └─ GoToNextStep ──────→ (on content entered)
                                                 │
                                                 ▼
                    ┌────────────────────────────────┐
                    │  StepDesignCustomization       │
                    │  - Show design options         │
                    │  - Colors, size, error level   │
                    │  - Real-time preview           │
                    └────┬──────────────────────────┘
                         │
                         ├─ GoToPreviousStep ──→ StepContentInput
                         │
                         └─ GenerateQR ─────────────→ (on design set)
                                                      │
                                                      ▼ Loading
                                                      │
                                        ┌─────────────────────────┐
                                        │  StepQRGeneration       │
                                        │  - Show QR preview      │
                                        │  - Show metadata        │
                                        │  - Save/Share buttons   │
                                        └────┬────────────────────┘
                                             │
                    ┌────────────────────────┼──────────────────┐
                    │                        │                  │
                    ▼                        ▼                  ▼
        ┌──────────────────┐   ┌──────────────────┐   ┌────────────────┐
        │ SaveQR ──→       │   │ Share ──→        │   │ Reset ──→      │
        │ Success(qrCode)  │   │ (external app)   │   │ Step 1         │
        └──────────────────┘   └──────────────────┘   └────────────────┘
                    │
                    ▼
        ┌──────────────────┐
        │ Success(qrCode)  │
        │ - Display result │
        │ - Option to new  │
        └──────────────────┘


Error State (reachable from any step):
┌────────────────────────────┐
│  Error(message)            │
│  - Show error message      │
│  - Retry / Reset button    │
└────────────────────────────┘
                │
                └─ Reset ──→ StepTypeSelection


Loading State (during async operations):
┌────────────────────────────┐
│  Loading                   │
│  - Show progress indicator │
│  - Block user interaction  │
└────────────────────────────┘
```

---

## 🔗 Dependency Graph

```
MainActivity
    │
    ├─ QRGeneratorViewModel (via @HiltViewModel)
    │   │
    │   ├─ GenerateQRUseCase
    │   ├─ SaveQRUseCase
    │   └─ GetQRHistoryUseCase
    │       │
    │       └─ QRCodeRepository (interface)
    │           │
    │           └─ QRCodeRepositoryImpl (implementation)
    │               │
    │               ├─ QRCodeDao
    │               │   │
    │               │   └─ AppDatabase
    │               │       │
    │               │       └─ Context (Application)
    │               │
    │               ├─ QRCodeApi
    │               │   │
    │               │   └─ Retrofit
    │               │       │
    │               │       └─ OkHttpClient
    │               │
    │               └─ SyncManager
    │                   │
    │                   └─ SyncManagerImpl
    │                       │
    │                       ├─ QRCodeDao
    │                       └─ QRCodeApi
    │
    └─ QRGeneratorNavigation
        │
        ├─ QRTypeSelectionScreen
        ├─ QRContentInputScreen
        ├─ QRDesignCustomizationScreen
        ├─ QRGenerationResultScreen
        ├─ ErrorScreen
        └─ LoadingScreen
```

---

This architecture ensures:
✅ **Separation of Concerns** - Each layer has a specific responsibility  
✅ **Testability** - Each component can be tested independently  
✅ **Maintainability** - Easy to modify specific features  
✅ **Scalability** - Easy to add new features  
✅ **Reusability** - Domain logic is independent of Android

