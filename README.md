
# Think-off (Server)

## Introduction

This repository contains the server-side implementation for **Think-off**, a web-based application designed to challenge and entertain users with a variety of games that test the human brain. The server is responsible for managing user accounts, game logic, friendships, and scoreboards. The corresponding client-side repository can be found [here](https://github.com/NelsonMeier/sopra-fs26-group-34-client).

## Technologies Used

- Java 17
- Spring Boot
- WebSocket
- Gradle
- Docker
- JUnit
- SonarQube

## High-level Components

The server is built around a few core components that work together to provide the application's functionality:

- **[UserController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/UserController.java)**: This is the main entry point for all user-related actions. It handles user registration, login, and profile management. It is responsible for creating, retrieving, updating, and deleting user data.

- **[GameController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/GameController.java)**: This controller manages the core game logic. It handles starting and ending games, validating user answers, and calculating scores.

- **[FriendController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/FriendController.java)**: This component is responsible for all social aspects of the application. It manages friend requests, friendships, and provides endpoints to retrieve friend lists.

- **[RoomController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/RoomController.java)**: This component manages multiplayer game rooms, allowing users to play together.

## Launch & Deployment

To get the application up and running locally, follow these steps:

**Build the project:**
```bash
./gradlew build
```

**Run the application:**
```bash
./gradlew bootRun
```

**Run the tests:**
```bash
./gradlew test
```

**External Dependencies:**
The application uses a persistent database. By default, it is configured to use an in-memory H2 database for development.

## Roadmap

Here are a few ideas for features that could be added to the project:

- **More Games**: Create new and exciting games and their multiplayer and scoreboard implementations.
- **Achievement System**: Add an achievement system to reward users for their progress.
- **Customizable Profiles**: Allow users to personalize their profiles with avatars and custom themes.
- **Chat With Friends**: Introduce a chat feature to let friends communicate through the app.

## Authors and Acknowledgment

<a href="https://github.com/NelsonMeier"><img src="https://github.com/NelsonMeier.png" title="Nelson-Meier" width="50" height="50"></a>
<a href="https://github.com/zara4hmed"><img src="https://github.com/zara4hmed.png" title="Zara-Ahmed" width="50" height="50"></a>
<a href="https://github.com/Lukas81S"><img src="https://github.com/Lukas81S.png" title="Lukas-Stahl" width="50" height="50"></a>
<a href="https://github.com/jonasdkf"><img src="https://github.com/jonasdkf.png" title="Jonas-Fischer" width="50" height="50"></a>
<a href="https://github.com/anitbaum"><img src="https://github.com/anitbaum.png" title="Anita-Baumann" width="50" height="50"></a>



## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
