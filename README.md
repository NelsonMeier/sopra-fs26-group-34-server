# 💭 Think-off
 
> A competitive mini-game platform where you race against friends or yourself across six reflex and cognition challenges.

---

## Table of Contents
 
1. [Introduction](#introduction)
2. [Technologies](#technologies)
3. [High-Level Components](#high-level-components)
4. [Launch & Deployment](#launch--deployment)
5. [Roadmap](#roadmap)
6. [Authors & Acknowledgment](#authors--acknowledgment)
7. [License](#license)

---

## Introduction
 
**Think-Off** is a web-based game platform where you can test your human skills through a collection of minigames. Many people enjoy testing their abilities, but existing platforms often lack social features and real-time competition.
 
Our application allows users to create accounts, track their performance and compete with friends. Players can participate in various minigames that test motor and cognitive abilities:
 
| Game | What it tests |
|---|---|
| **Reaction Time** | How fast can you click when the signal appears? |
| **Typing Speed** | Race to type a random quote accurately |
| **Time Estimation** | Estimate when a set time interval has elapsed |
| **Click Aim** | Click targets as accurately and quickly as possible |
| **Click Speed** | How many clicks per second can you achieve? |
| **Quick Math** | Solve arithmetic problems under time pressure |
 
The platform supports both **singleplayer** sessions and **multiplayer** sessions (invite friends, compete in real time, see a live scoreboard and ranking).

The server is responsible for managing user accounts, game logic, friendships, and scoreboards. The corresponding client-side repository can be found [here](https://github.com/NelsonMeier/sopra-fs26-group-34-client).

---

## Technologies

- Java 17
- Spring Boot
- WebSocket
- Gradle
- Docker
- JUnit
- SonarQube

---

## High-level Components

The server is built around a few core components that work together to provide the application's functionality:

- **[UserController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/UserController.java)**: This is the main entry point for all user-related actions. It handles user registration, login, and profile management. It is responsible for creating, retrieving, updating, and deleting user data.

- **[GameController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/GameController.java)**: This controller manages the core game logic. It handles starting and ending games, validating user answers, and calculating scores.

- **[FriendController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/FriendController.java)**: This component is responsible for all social aspects of the application. It manages friend requests, friendships, and provides endpoints to retrieve friend lists.

- **[RoomController.java](https://github.com/NelsonMeier/sopra-fs26-group-34-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs26/controller/RoomController.java)**: This component manages multiplayer game rooms, allowing users to play together.

---

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

---

## Roadmap

1. **Achievements** — reward players for hitting milestones such as getting the best ranking in the global scoreboard, winning a multiplayer session, or playing a set number of games.
2. **Mobile support** — adapting the platform to also work on mobile phones.
3. **Public lobbies** — adding public rooms that any logged-in user can browse and join, making it possible to compete against strangers.
4. **Customizable Profiles**: Allow users to personalize their profiles with avatars and custom themes.
5. **Chat With Friends**: Introduce a chat feature to let friends communicate through the app.

---

## Authors and Acknowledgment

[@zar4hmed](https://github.com/zar4hmed) · [@anitbaum](https://github.com/anitbaum) · [@Lukas81S](https://github.com/Lukas81S) · [@NelsonMeier](https://github.com/NelsonMeier) · [@jonasdkf](https://github.com/jonasdkf)
 
Thanks to Joël Schmidt who supported us as Scrum Master during this SoPra project!

---


## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
