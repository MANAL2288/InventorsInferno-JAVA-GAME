import java.util.ArrayList;
import java.util.List;


public class Task {
    private String name;
    private String description;
    private Sense requiredSense;  // Sense player must have to attempt task
    private Sense rewardSense;     // Sense restored upon completion
    private List<Item> rewardItems;  // Items given when task completes
    private boolean isCompleted;
    private List<String> steps;

    public Task(String name, String description, Sense requiredSense,
                Sense rewardSense, Item rewardItem) {
        this.name = name;
        this.description = description;
        this.requiredSense = requiredSense;
        this.rewardSense = rewardSense;
        this.rewardItems = new ArrayList<>();
        if (rewardItem != null) {
            this.rewardItems.add(rewardItem);
        }
        this.isCompleted = false;
        this.steps = new ArrayList<>();
    }

    public Task(String name, String description, Sense requiredSense,
                Sense rewardSense) {
        this(name, description, requiredSense, rewardSense, null);
    }

    //Validates if player can attempt task and executes completion
    public boolean attempt(Player player) {
        if (isCompleted) {
            System.out.println("This task is already completed!");
            return false;
        }

        if (requiredSense != null && !player.hasSense(requiredSense)) {
            System.out.println("You cannot complete this task. Required sense: " + requiredSense);
            fail();
            return false;
        }

        // Display task steps if available
        if (!steps.isEmpty()) {
            System.out.println("\nTask steps:");
            for (int i = 0; i < steps.size(); i++) {
                System.out.println((i + 1) + ". " + steps.get(i));
            }
        }

        System.out.println("\nTask completed: " + name);
        complete(player);
        return true;
    }

    //Awarding player with sense and items upon task completion
    public void complete(Player player) {
        isCompleted = true;

        if (rewardSense != null) {
            player.restoreSense(rewardSense);
        }

        for (Item item : rewardItems) {
            player.addItem(item);
            System.out.println("You received: " + item.getName());
        }
    }

    public void fail() {
        System.out.println("Task failed: " + name);
    }

   
    public void addStep(String step) {
        steps.add(step);
    }

 
    public void addRewardItem(Item item) {
        rewardItems.add(item);
    }

    // Getters
    public List<String> getSteps() {
        return steps;
    }

    public List<Item> getRewardItems() {
        return rewardItems;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Sense getRequiredSense() {
        return requiredSense;
    }

    public Sense getRewardSense() {
        return rewardSense;
    }

    public Item getRewardItem() {
        if (!rewardItems.isEmpty()) {
            return rewardItems.get(0);
        }
        return null;
    }

    public void setRewardItem(Item rewardItem) {
        rewardItems.clear();
        if (rewardItem != null) {
            rewardItems.add(rewardItem);
        }
    }
}