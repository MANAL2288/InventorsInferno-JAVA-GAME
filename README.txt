THE INVENTOR'S INFERNO
=======================

A Java text adventure game where you race against time to stop a reactor explosion!

GAME CONCEPT:
-------------
After a lab explosion, you've lost all senses except sight. You have 20 minutes
to navigate 6 specialized rooms, complete sensory-based tasks, and restore your
senses one by one before the reactor melts down.

HOW TO PLAY:
------------
- Type commands: north, south, east, west (move)
- take [item], use [item], inspect [item] (interact)
- help (get hints), quit (exit game)

GAME FEATURES:
--------------
• 6 unique rooms with sensory puzzles
• 20-minute real-time countdown with warnings
• Progressive sense restoration (Sight → Hearing → Smell → Taste → Touch → Intuition)
• Item interaction and puzzle solving
• Room unlocking through task completion

PROJECT STRUCTURE:
------------------
Game.java      - Main controller
Player.java    - Player character
Room.java      - Game environments
Task.java      - Puzzles/tasks
Item.java      - Interactive objects
CommandParser  - Input handling system
Command        - Command data structure

ENUMS: Direction, Sense, CmdType

RUN:
----
Compile and run Game.java to start the adventure!

GOAL:
-----
Complete all 6 tasks, reach the reactor core, and choose correctly
before time runs out. Save the lab and restore all your senses!