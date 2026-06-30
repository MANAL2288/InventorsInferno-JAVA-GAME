import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {
    private List<Item> carriedItems;
    private Set<Sense> sensesRestored;  // Tracks which senses player has restored
    private Room currentRoom;
    private Set<Task> completedTasks;
    /** Creates a new player starting in a room */
    public Player(Room startingRoom) {
        this.carriedItems = new ArrayList<>();
        this.sensesRestored = new HashSet<>();
        this.currentRoom = startingRoom;
        this.completedTasks = new HashSet<>();
        sensesRestored.add(Sense.SIGHT);
    }

    /** Add item to inventory */
    public void addItem(Item item) {
        carriedItems.add(item);
        System.out.println("You picked up: " + item.getName() + "\n");
    }

    public void removeItem(Item item) {
        carriedItems.remove(item);
    }

    public boolean hasItem(String itemName) {
        for (Item item : carriedItems) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public Item getItem(String itemName) {
        for (Item item : carriedItems) {
            if (item.getName().equalsIgnoreCase(itemName)) { 
                return item;
            }
        }
        return null;
    }
    /** @return Player inventory */
    public List<Item> getCarriedItems() {
        return carriedItems;
    }


    public void restoreSense(Sense sense) {
        sensesRestored.add(sense);
        System.out.println("You restored the sense of: " + sense.toString() + "\n");
    }
    /** Check if player has a sense */
    public boolean hasSense(Sense sense) {
        return sensesRestored.contains(sense);
    }

    public Set<Sense> getSensesRestored() {
        return sensesRestored;
    }
    /** Move player to a room */
    public void moveTo(Room room) {
        this.currentRoom = room;
    }

    /** @return current room */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void completeTask(Task task) {
        completedTasks.add(task);
    }

    public boolean hasCompletedTask(Task task) {
        return completedTasks.contains(task);
    }

    public Set<Task> getCompletedTasks() {
        return completedTasks;
    }

}