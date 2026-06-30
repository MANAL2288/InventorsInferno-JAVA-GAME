import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room {
    private String name;
    private String description;
    private ArrayList<Item> items;
    private HashMap<Direction, Room> connectedRooms; // Maps directions to adjacent rooms
    private Task task;
    private List<String> hints;
    private boolean locked;
    private String unlockCondition;
    private int roomId;

    public Room(String name, String description, int roomId) {
        this.name = name;
        this.description = description;
        this.roomId = roomId;
        this.items = new ArrayList<>();
        this.connectedRooms = new HashMap<>();
        this.hints = new ArrayList<>();
        this.locked = false;
        this.unlockCondition = null;
    }

    // Item management
    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Room getAdjacentRoom(Direction dir) {
        return connectedRooms.get(dir);
    }

    public Item getItem(String name) {
        for (Item item : items) {
             if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
    return null;
}

    // room connections
    public void connect(Direction direction, Room room) {
        connectedRooms.put(direction, room);
    }

    // room state
    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        locked = false;
    }

    public void describeRoom() {
        System.out.println("You are now at:");
        System.out.println( name);
        System.out.println(description);

        if (!items.isEmpty()) {
            System.out.println("Items here:");
            for (Item item : items) {
                System.out.println("- " + item.getName());
            }
        }

        if (task != null && !task.isCompleted()) {
            System.out.println("Task : " + task.getName());
        }

        System.out.println("Exits:");
        for (Direction dir : connectedRooms.keySet()) {
            Room adjacent = connectedRooms.get(dir);
            System.out.println("- " + adjacent.getName() + " - " + dir + "\n");
        }
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<String> getHints() {
        return hints;
    }

    public void lock(String condition) {
        locked = true;
        unlockCondition = condition;
    }

    public String getName() {
        return name;
    }

    public int getRoomId() {  
        return roomId;
    }
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void addHint(String hint) {
        hints.add(hint);
    }

    public String getUnlockCondition() {
        return unlockCondition;
    }

    public Task getTask(){
        return task; 
    }

    public boolean containsItem(Item item) {
        return items.contains(item);
    }

    public HashMap<Direction, Room> getConnectedRooms() {
        return connectedRooms;
    }

}