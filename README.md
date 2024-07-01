# Pokémon Cards Matching Game

Welcome to the Pokémon Cards Matching Game, an Android application designed to provide a fun and engaging way to play a classic card matching game with Pokémons. This project is part of the embedded course assignment to implement telemetry data transmission using a freely available telemetry tool.

## Team Members
- Diana Plosnita
- Tommaso Bortolin
- Giuseppe D'Auria

## Project Overview
This project includes:
- A single player mode with three rounds
- A multiplayer mode
- Telemetry data collection and transmission using Firebase for monitoring user behavior and app performance

## Installation Guide

### Prerequisites
- Android Studio
- A device or emulator running Android 13 or higher

### Steps to Install
1. Clone the Repository
2. Open the project in Android Studio
3. Build the Project. Allow Android Studio to download and configure necessary dependencies

## Usage Instructions

### Single Player Mode
1. Start the game and select "Single Player" from the main menu.
2. Gameplay:
    1. The game consists of three rounds (8, 12, and 16 cards, respectively), each with an appropriate timer (30, 60, and 90 seconds, respectively).
    2. Match pairs of Pokémon cards by flipping them. Complete each round to progress to the next.
    3. The third round has a new feature: cards flipped twice block for 3 seconds (blocked cards are temporarily unclickable).

### Multiplayer Mode
1. Start the game and select "Multiplayer" from the main menu.
2. Gameplay:
    1. There's one round with 22 cards, and two players can play on the same phone.
    2. Each player gets one turn during which they can:
        - Match 2 cards and get +1 points.
        - Reveal two different cards and get 0 points. The player with the most points wins.
    3. The turns are distinguishable by the card frame's color: red or blue.


## Telemetry Data Transmission
The app uses Firebase to collect and send telemetry data to monitor its functioning and user behavior. This includes:
- Application launch time
- Time of clicking the "single player" or "multiplayer" play button
- Level completion time
- Average time between card flips
- Number of attempts per round, i.e., how many times a card was flipped; cards are identified by their index 
- Battery usage, showing the initial and final battery percentage
- OS type and device type (user properties)
- RAM usage, showing both total RAM (in GB) and available RAM (in MB)
- Total game duration
- End of the game with its outcome: loss or win
- Error tracking 

*Note: These telemetry features are implemented for both single-player and multiplayer modes.*

## Repository Structure
`app/src/main/java/com/example/matchgame/`
* `ui/`
     * `BaseRoundFragment.kt` - Abstract class managing the fragments
     * `Round1Fragment.kt` - First round fragment
     * `Round2Fragment.kt` - Second round fragment
     * `Round3Fragment.kt` - Third round fragment
     * `MultiplayerFragment.kt` - Multiplayer fragment
     * `HomeFragment.kt` - Home fragment
     * `YouLoseFragment.kt`, `YouWinFragment.kt`, `Player1WinFragment.kt`, `Player2WinFragment.kt`, `AboutFragment.kt`, `DialogFragment.kt`, `InfoFragment.kt` - Display fragments
* `logic/`
    * `IGameLogic.kt` - Game logic interface, contains common methods definition
    * `SingleGameLogic.kt` - Game logic for single player mode
    * `MultiplayerGameLogic.kt` - Game logic for multiplayer mode
* `models/`
    * `MemoryCard.kt` - Represents a single card in the memory game, holding its state and identifier
* `adapter/`
    * `CardAdapter.kt` - Manages the logic for displaying the cards in the RecyclerView
* `telemetry/`
    * `DataCollector.kt` - Manages and sends data to the Firebase console for the telemetry part of the project
* `MainActivity.kt` - The main activity that serves as the entry point of the application

## Contact 
1. diana.plosnita@studenti.unipd.it
2. tommaso.bortolin@studenti.unipd.it
3. giuseppe.dauria@studenti.unipd.it


   
