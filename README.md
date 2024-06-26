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
2. Open in Android Studio
3. Build the Project. Allow Android Studio to download and configure necessary dependencies

## Usage Instructions

### Single Player Mode
1. Start the game and select "Single Player" from the main menu.
2. Gameplay:
     2.1. The game consists of three rounds (with 8, 12 and 16 cards, respectively)
     2.2. Match pairs of Pokémon cards by flipping them. Complete each round to progress to the next.

### Multiplayer Mode
1. Start the game and select "Multiplayer" from the main menu.
2. Gameplay:
     2.1. There's one round with 22 cards, two players can play at the same phone. 
     2.2. Each player gets one turn during which he can: match 2 cards and get +1 points, reveal two different cards and get 0 points. Player with the most points wins.
     2.3. The turns are distinguishable by cards frame's color: red or blue.

## Telemetry Data Transmission
The app uses Firebase to collect and send telemetry data to monitor its functioning and user behavior. This includes:
- Game start
- Game end
- Application launch time
- Clicking the play button time
- Level completion time 
- Number of attempts per round (aka how many time a card was flipped. The cards are identified by index)
- Average time between two consecutive clicks
- Game abandonment
- Error tracking
- OS type
- Device type
- RAM usage (displaying both total RAM (in GB) and available RAM (in MB))
- Battery usage (with initial and final battery percentage)
- Total game duration

## Repository Structure
`app/src/main/java/com/example/matchgame/` 
* `ui/`
     * `BaseRoundFragment.kt` - Abstract class managing the fragments
     * `Round1Fragment.kt` - First round fragment
     * `Round2Fragment.kt` - Second round fragment
     * `Round3Fragment.kt` - Third round fragment
     * `MultiplayerFragment.kt` - Multiplayer fragment
     * `HomeFragment.kt` - Home fragment
     * `YouLoseFragment`, `YouWinFragment`, `Player1WinFragment`, `Player2WinFragment`, `AboutFragment`, `DialogFragment`, `InfoFragment` - display fragments
* `logic/`
    * `IGameLogic` - Game logic interface, contains common methods definition
    * `SingleGameLogic.kt` - Game logic for single player mode
    * `MultiplayerGameLogic.kt` - Game logic for multiplayer mode
* `models/`
    * `MemoryCard.kt` - Represents a single card in the memory game, holding its state and identifier
* `adapter/`
    * `CardAdapter` - Manages the logic for displaying the cards in the RecyclerView
* `telemetry/`
    * `DataCollector` - Manages and sends data to the Firebase console for the telemetry part of the project
* `MainActivity.kt` - `MainActivity.kt` - The main activity that serves as the entry point of the application

## Contact 
1. diana.plosnita@studenti.unipd.it
2. tommaso.bortolin@studenti.unipd.it
3. giuseppe.dauria@studenti.unipd.it


   
