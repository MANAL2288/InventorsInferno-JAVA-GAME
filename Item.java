import java.util.List;
/**
 * Represents an item in the game.
 */


public class Item {
    private String name;
    private String description;
    private boolean isKeyItem;
    private boolean usable;
    private boolean isTaskItem;
    private boolean takeable;
    private int unlocksRoomId;
    private String hiddenItemName;
    private Room currentRoom;
    /**
     * Creates a new item.
     * @param name Item name
     * @param description Item description
     * @param effects Optional effects (can be null)
     * @param isKeyItem True if it unlocks a room
     * @param usable True if it can be used
     */
    
    //Creates new item with name, description, and properties.
    public Item(String name, String description, List<String> effects, boolean isKeyItem, boolean usable) {
        this.name = name;
        this.description = description;
        this.isKeyItem = isKeyItem;
        this.usable = usable;
        this.isTaskItem = false;
        this.takeable = true;
        this.unlocksRoomId = -1;
        this.hiddenItemName = null;
        this.currentRoom = null;
    }

    /** @return Description when inspected */
    public String inspect() {
        if (hiddenItemName != null) {
            return name + ": " + description + " Inside you find: " + hiddenItemName + "!";
        }
        return name + ": " + description;
    }

    /** Use the item in a room can complete tasks or unlock rooms*/
    public String useInRoom(Player player, Room currentRoom) {
        if (!usable && !isKeyItem) {
            return "This item cannot be used.";
        }

        // Key items unlock specific rooms
        if (isKeyItem) {
            for (Direction direction : Direction.values()) {
                Room adjacentRoom = currentRoom.getAdjacentRoom(direction);

                if (adjacentRoom != null && adjacentRoom.getRoomId() == unlocksRoomId) {
                    if (adjacentRoom.isLocked()) {
                        adjacentRoom.unlock();
                        return "You used the " + name + " to unlock the " +
                                adjacentRoom.getName() + " to the " + direction + ".";
                    } else {
                        return "The " + adjacentRoom.getName() + " is already unlocked.";
                    }
                }
            }
            return "This key cannot be used here.";
        }

        // Only task items can complete tasks
        if (!isTaskItem) {
            return "This item doesn't help with the current task.";
        }

        Task task = currentRoom.getTask();
        if (task != null && !task.isCompleted()) {
            // Check if player has required sense for this task
            if (task.getRequiredSense() != null && !player.hasSense(task.getRequiredSense())) {
                return "You don't have the required sense to complete this task yet.";
            }

            if (this.currentRoom != null && this.currentRoom.getTask() != task) {
                return "This item doesn't work here. Try using it in the " + this.currentRoom.getName() + ".";}

            // Complete the task
            task.complete(player);
            return "You used the " + name + " successfully! The task is complete!";
        }

        return "This item has no effect here.";
    }

   /** sets the name of an item hidden inside this container*/
    public void setHiddenItem(String itemName) {
        this.hiddenItemName = itemName;
    }

    /** @return Name of hidden item */
    public String getHiddenItem() {
        return hiddenItemName;
    }

  /**Sets whether this item can complete task*/
    public void setTaskItem(boolean isTaskItem) {
        this.isTaskItem = isTaskItem;
    }

    /**Sets which room this key unlocks*/
    public void setUnlocksRoomId(int roomId) {
        this.unlocksRoomId = roomId;
    }

/**Sets the room this item belongs to*/
    public void setRoom(Room room) {
        this.currentRoom = room;
    }

    /** Gets the room this item belongs to*/
    public Room getRoom() {
        return currentRoom;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isKeyItem() {
        return isKeyItem;
    }

    public boolean isUsable() {
        return usable;
    }

    public boolean isTaskItem() {
        return isTaskItem;
    }

    public int getUnlocksRoomId() {
        return unlocksRoomId;
    }

    public boolean isTakeable() {
        return takeable;
    }
    
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }
}