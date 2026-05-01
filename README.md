
# X-Log 

X-Log is a gamified habit tracker built with JavaFX. Complete daily habits to earn coins and XP, level up your stats, and customize your personal avatar. It includes a habit dashboard, an avatar store, profile customization, and persistent local storage.

## About the project

X-Log — Zenith bridges the gap between productivity and play. Users build daily habits across four RPG-style stat categories — Intelligence, Strength, Chi, and Charisma — and earn rewards for consistency. Coins can be spent in the avatar store to unlock accessories and personalize their character. All progress is saved locally, requiring no external setup.

## Main features

- **Habit Dashboard:** View, add, and complete daily habits with real-time progress tracking.
- **Stat system:** Habits are categorized into four RPG-style stats: Intelligence, Strength, Chi, and Charisma.
- **Coin and XP rewards:** Earn 30 coins and 15 XP every time you complete a habit.
- **Avatar store:** Spend your coins to unlock display accessories (Football, Sunglasses, Wizard Staff).
- **Avatar customization:** Customize your character's body color, eye type, and equipped display item.
- **Persistent storage:** All data (habits, purchases, avatar selection) is saved locally via SQLite.

## System architecture

The application follows a single-tier desktop architecture:

- **UI layer:** Built with JavaFX 21 using FXML for layout and CSS for styling. Three main screens — Dashboard, Profile, and Store — are each backed by a dedicated controller.
- **Data layer:** A local SQLite database managed via the `sqlite-jdbc` driver. Repositories handle all CRUD operations for habits, purchases, and avatar state.
- **Session management:** A singleton `Session` object holds the active user's state across screens without requiring a server or external auth.

## Tools and technologies

### Application

- **Language:** Java 21
- **UI framework:** JavaFX 21.0.3 (FXML + CSS)
- **Database:** SQLite via `sqlite-jdbc 3.45.3.0`
- **Build tool:** Apache Maven

## Project structure

```
X-Log-main/
├── pom.xml
└── src/main/
    ├── java/com/xlog/app/
    │   ├── MainApp.java                   # Application entry point
    │   ├── controllers/
    │   │   ├── DashboardController.java   # Habit list, progress, add modal
    │   │   ├── ShopController.java        # Store & purchase logic
    │   │   └── UserDetailsController.java # Profile, avatar customization
    │   ├── data/
    │   │   ├── Database.java              # SQLite connection & schema init
    │   │   ├── TaskRepository.java        # CRUD for habits/tasks
    │   │   ├── PurchaseRepository.java    # Purchase records
    │   │   └── AvatarRepository.java      # Avatar selection persistence
    │   └── models/
    │       ├── Task.java                  # Habit model
    │       ├── User.java                  # User stats & currency
    │       ├── Session.java               # Singleton active user session
    │       ├── StatType.java              # Enum: INTELLIGENCE, STRENGTH, CHI, CHARISMA
    │       └── AvatarSelection.java       # Avatar state (body, eyes, display)
    └── resources/com/xlog/app/
        ├── dashboard.fxml
        ├── shop.fxml
        ├── user_details.fxml
        ├── style.css
        └── images/
            ├── avatar/
            │   ├── body/        # blue, red, yellow
            │   ├── eyes/        # angry, annoyed, normal, peace
            │   └── display/     # football, sunglasses, staff
            └── ui/
                ├── coin.png
                └── logo.png
```

## How to run the project

### Prerequisites

- Java 21 or later 
- Apache Maven 3.8+ 

### 1. Clone the repository

```
git clone https://github.com/your-username/X-Log.git
cd X-Log-main
```

### 2. Run the application

```
mvn javafx:run
```

The app will launch a 1280×800 window titled "Zenith".

### 3. Build a JAR (optional)

```
mvn package
```

## How to use

1. **Add a habit** — Click **+ New Habit** on the Dashboard, enter a name, pick a stat category, and confirm.
2. **Complete a habit** — Check the checkbox or click **Mark Done** to log it for today. You will earn coins and XP.
3. **Visit the store** — Navigate to the Store tab and spend your coins on avatar accessories.
4. **Customize your avatar** — Go to your Profile and click **Customize Avatar** to change body color, eyes, and your equipped display item. Only items you have purchased will appear.

## Database

The app uses an embedded SQLite database stored locally. The schema is automatically initialized on first launch via `Database.java`. No external database setup is required.
```
