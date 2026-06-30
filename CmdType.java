/**
 * Command types that a player can input.
 */
public enum CmdType {
    MOVE,    // Move to another room
    TAKE,    // Pick up an item
    USE,     // Use an item
    INSPECT, // Inspect an object
    HELP,    // Show help
    QUIT,    // Quit the game
    UNKNOWN  // Invalid command
}
