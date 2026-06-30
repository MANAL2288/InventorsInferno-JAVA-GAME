/**
 * Game - The main controller class for "The Inventor's Inferno" game.
 * This class manages the entire game flow including:
 * - Room setup and connections
 * - Item and task management  
 * - Player movement and interactions
 * - Game timer and win/lose conditions
 * - Command processing from the player
 * 
 * The game features 6 rooms, each with a unique task that restores
 * one of the player's senses (sight, hearing, smell, taste, touch, intuition)
 * Players must complete all tasks within 20 minutes to win
 */

import java.util.*;

public class Game {
    /** Maximum allowed time to finish the game */
    private static final long TIME_LIMIT = 20 * 60 * 1000;

    private Room currentRoom;
    private Map<Integer, Room> rooms;
    private Map<String, Item> allItems;
    private Player player;
    private List<Task> tasks;
    private CommandParser parser;

    private long startTime;
    private long timeLimit;  
    private boolean isRunning;

    /**
     * Creates a new Game instance and initializes
     * rooms, items, tasks, and the command parser.
     */
    public Game() {
        rooms = new HashMap<>();
        allItems = new HashMap<>();
        tasks = new ArrayList<>();
        parser = new CommandParser();
        timeLimit = TIME_LIMIT;
    }
    /** Starts the game,
     * This method loads rooms and tasks, links tasks to rooms,
     * initializes the player, starts the game timer,
     * and displays the introduction text. */
    public void startGame() {
        loadRooms();   
        loadTasks();
        linkTasksToRooms();

        player = new Player(currentRoom);
        isRunning = true;
        startTime = System.currentTimeMillis();

        System.out.println("================ Welcome to The Inventor's Inferno! =================");
        System.out.println("EMERGENCY! The reactor is about to explode!\n");
        System.out.println("You've lost your senses in the blast! You can only SEE...");
        System.out.println("Complete tasks to restore your senses one by one.\n");
        System.out.println("You have 20 minutes to stop the reactor!\n");
        System.out.println("Type 'help' for guidance\n");
        System.out.println();
        currentRoom.describeRoom();
    }
    /** Main game loop
     * Continuously reads player input, processes commands,
     * updates the game state, and checks win/lose conditions
     * until the game ends.*/
    public void run() {
        startGame();
        Scanner scanner = new Scanner(System.in);
        
        while (isRunning) {
            System.out.print("\n> ");
            String input = scanner.nextLine();
            
            if (input == null || input.isBlank()) {
                continue;
            }
            
            Command cmd = parser.parse(input);
            processCommand(cmd);
            unlockRooms();      // Check if rooms should be unlocked
            displayTimer();     // Show remaining time
            checkWinCondition(); // Check if player won
            checkLoseCondition(); // Check if player lost
        }
        
        scanner.close();
    }

    private void loadRooms() {
        Room powerRoom = new Room(
                "Power Control Room",
                "A dim room with exposed wires and humming machines.", 1
        );
        Room soundRoom = new Room(
                "Sound Calibration Room",
                "A circular room filled with speakers and monitors. The noise is deafening! BZZZT! WHIRR! SCREECH!", 2
        );
        Room chemicalRoom = new Room(
                "Chemical Storage Room",
                "A narrow space with shelves of chemicals and colored smoke. *SNIFF* BLEGH! Something reeks of rotten eggs!", 3
        );
        Room alchemicalRoom = new Room(
                "Alchemical Lab Room",
                "A chaotic lab with floating ingredients and bubbling flasks. You taste... sweetness? Bitterness? Magic?", 4
        );
        Room engineeringRoom = new Room(
                "Engineering Workshop Room",
                "A space with vibrating pipes, heat vents, and heavy machinery. OUCHH! Everything's HOT! The vibrations rattle your bones!", 5
        );
        Room reactorRoom = new Room(
                "Reactor Core Room",
                "A critical room filled with blinking switches and alarms. Your intuition SCREAMS danger!", 6
        );

        // Connect rooms
        powerRoom.connect(Direction.SOUTH, soundRoom);
        soundRoom.connect(Direction.NORTH, powerRoom);
        soundRoom.connect(Direction.EAST, chemicalRoom);
        chemicalRoom.connect(Direction.WEST, soundRoom);
        chemicalRoom.connect(Direction.EAST, alchemicalRoom);
        alchemicalRoom.connect(Direction.WEST, chemicalRoom);
        powerRoom.connect(Direction.EAST, engineeringRoom);
        engineeringRoom.connect(Direction.WEST, powerRoom);
        alchemicalRoom.connect(Direction.NORTH, engineeringRoom);
        engineeringRoom.connect(Direction.SOUTH, alchemicalRoom);
        engineeringRoom.connect(Direction.WEST, reactorRoom);  
        reactorRoom.connect(Direction.EAST, engineeringRoom);
        
        // Lock all rooms except Power control room
        soundRoom.lock("You need the Sound Room Key! Complete the Power Control task first.");
        chemicalRoom.lock("You need the Frequency Key! Complete the Sound Calibration task first.");
        alchemicalRoom.lock("You need the Alchemical Key! Complete the Chemical Storage task first.");
        engineeringRoom.lock("You need the Engineering Key! Complete the Alchemical Lab task first.");
        reactorRoom.lock("You need the Core Key! Complete ALL previous tasks first.");

        rooms.put(1, powerRoom);
        rooms.put(2, soundRoom);
        rooms.put(3, chemicalRoom);
        rooms.put(4, alchemicalRoom);
        rooms.put(5, engineeringRoom);
        rooms.put(6, reactorRoom);

        // Power control room
        powerRoom.addHint("[SIGHT] The darkness is overwhelming. You can barely see!");
        powerRoom.addHint("Something's in that dark corner... is it a closet?");
        powerRoom.addHint("The wires are SPARKING! You need the RIGHT tool!");
        powerRoom.addHint("TRICK ALERT: Not all tools work! Choose wisely!");
        
        Item wrench = new Item("wrench", "A PROPER metal wrench! THIS is what reconnects circuits!", null, false, true);
        wrench.setTaskItem(true);
        wrench.setRoom(powerRoom);
        allItems.put("wrench", wrench);
        
        Item pliers = new Item("pliers", "WRONG TOOL! These pliers are for bending, not connecting circuits!", null, false, true);
        pliers.setRoom(powerRoom);
        allItems.put("pliers", pliers);
        
        Item hammer = new Item("hammer", "SERIOUSLY? A hammer? That'll break EVERYTHING!", null, false, true);
        hammer.setRoom(powerRoom);
        allItems.put("hammer", hammer);
        
        Item screwdriver = new Item("screwdriver", "Nice try, but screwdrivers don't reconnect wires!", null, false, true);
        screwdriver.setRoom(powerRoom);
        allItems.put("screwdriver", screwdriver);
        
        Item closet = new Item("closet", "A dark, mysterious closet. What's inside?", null, false, false);
        closet.setHiddenItem("wrench");
        closet.setRoom(powerRoom);
        powerRoom.addItem(closet);
        
        Item toolbox = new Item("toolbox", "A rusty old toolbox. Maybe it has tools?", null, false, false);
        toolbox.setHiddenItem("pliers");
        toolbox.setRoom(powerRoom);
        powerRoom.addItem(toolbox);
        
        Item shelf = new Item("shelf", "A wobbly shelf with random items.", null, false, false);
        shelf.setHiddenItem("hammer");
        shelf.setRoom(powerRoom);
        powerRoom.addItem(shelf);
        
        powerRoom.addItem(screwdriver);

        // Sound calib room
        soundRoom.addHint("[HEARING UNLOCKED!] BZZZZT! WHIRRR! The frequencies are WRONG!");
        soundRoom.addHint("The noise hurts your ears! Find something to BOOST the signal!");
        soundRoom.addHint("Listen carefully... one speaker sounds different!");
        soundRoom.addHint("TRICK: The WALL PANEL hides the real solution!");
        
        Item amplifier = new Item("amplifier", "PERFECT! This amplifier will stabilize ALL frequencies!", null, false, true);
        amplifier.setTaskItem(true);
        amplifier.setRoom(soundRoom);
        allItems.put("amplifier", amplifier);
        
        Item speaker = new Item("speaker", "Just a speaker. It's making noise, not fixing it!", null, false, true);
        speaker.setRoom(soundRoom);
        allItems.put("speaker", speaker);
        soundRoom.addItem(speaker);
        
        Item microphone = new Item("microphone", "A microphone? You need to FIX sound, not record it!", null, false, true);
        microphone.setRoom(soundRoom);
        allItems.put("microphone", microphone);
        
        Item headphones = new Item("headphones", "Headphones block sound but don't fix the system!", null, false, true);
        headphones.setRoom(soundRoom);
        allItems.put("headphones", headphones);
        
        Item wallPanel = new Item("wall panel", "A broken panel with exposed wires!", null, false, false);
        wallPanel.setHiddenItem("amplifier");
        wallPanel.setRoom(soundRoom);
        soundRoom.addItem(wallPanel);
        
        Item cabinet = new Item("cabinet", "A cabinet full of audio equipment.", null, false, false);
        cabinet.setHiddenItem("microphone");
        cabinet.setRoom(soundRoom);
        soundRoom.addItem(cabinet);
        
        soundRoom.addItem(headphones);

        // Chemical lab room
        chemicalRoom.addHint("[SMELL UNLOCKED!] *SNIFF* BLEGH! Rotten eggs! Chemicals LEAKING!");
        chemicalRoom.addHint("RED containers are HISSING! They're the dangerous ones!");
        chemicalRoom.addHint("There's a ventilation shaft... but it's HIGH UP!");
        chemicalRoom.addHint("TRICK: You need to INSPECT things to reach high places!");
        
        Item sealant = new Item("sealant", "CHEMICAL SEALANT! This will seal those leaks!", null, false, true);
        sealant.setTaskItem(true);
        sealant.setRoom(chemicalRoom);
        allItems.put("sealant", sealant);
        
        Item redContainer = new Item("red container", "DANGER! This container is LEAKING toxic fumes! SEAL IT!", null, false, false);
        redContainer.setRoom(chemicalRoom);
        chemicalRoom.addItem(redContainer);
        
        Item blueContainer = new Item("blue container", "This one's fine. It's sealed properly.", null, false, false);
        blueContainer.setRoom(chemicalRoom);
        chemicalRoom.addItem(blueContainer);
        
        Item greenContainer = new Item("green container", "Safe. No leaks here.", null, false, false);
        greenContainer.setRoom(chemicalRoom);
        chemicalRoom.addItem(greenContainer);
        
        Item duct_tape = new Item("duct tape", "Duct tape? Really? This is TOXIC chemicals, not a leaky pipe!", null, false, true);
        duct_tape.setRoom(chemicalRoom);
        allItems.put("duct tape", duct_tape);
        
        Item ventShaft = new Item("ventilation shaft", "A metal shaft HIGH on the wall. Can you reach it?", null, false, false);
        ventShaft.setHiddenItem("sealant");
        ventShaft.setRoom(chemicalRoom);
        chemicalRoom.addItem(ventShaft);
        
        Item crate = new Item("crate", "A wooden crate in the corner.", null, false, false);
        crate.setHiddenItem("duct tape");
        crate.setRoom(chemicalRoom);
        chemicalRoom.addItem(crate);

        //Alchemical lab room
        alchemicalRoom.addHint("[TASTE UNLOCKED!] Mmm... BITTER... SWEET... SOUR... What IS this?!");
        alchemicalRoom.addHint("Ingredients are FLOATING! Magic is real!");
        alchemicalRoom.addHint("The cauldron is bubbling... What's brewing?");
        alchemicalRoom.addHint("TRICK: TASTE things to discover their properties!");
        
        Item coolantFlask = new Item("coolant flask", "COOLANT! This will stabilize the alchemical reaction!", null, false, true);
        coolantFlask.setTaskItem(true);
        coolantFlask.setRoom(alchemicalRoom);
        allItems.put("coolant flask", coolantFlask);
        
        Item herb = new Item("herb", "A floating herb. *TASTE* Bitter! Used for healing.", null, false, true);
        herb.setRoom(alchemicalRoom);
        allItems.put("herb", herb);
        alchemicalRoom.addItem(herb);
        
        Item crystal = new Item("crystal", "A floating crystal. *TASTE* Sweet! Pure magic energy.", null, false, true);
        crystal.setRoom(alchemicalRoom);
        allItems.put("crystal", crystal);
        alchemicalRoom.addItem(crystal);
        
        Item mushroom = new Item("mushroom", "A floating mushroom. *TASTE* SOUR! Definitely magical!", null, false, true);
        mushroom.setRoom(alchemicalRoom);
        allItems.put("mushroom", mushroom);
        alchemicalRoom.addItem(mushroom);
        
        Item potion = new Item("potion", "A random potion. Who knows what THIS does? Don't use it!", null, false, true);
        potion.setRoom(alchemicalRoom);
        allItems.put("potion", potion);
        
        Item cauldron = new Item("cauldron", "A bubbling cauldron with mysterious liquid!", null, false, false);
        cauldron.setHiddenItem("coolant flask");
        cauldron.setRoom(alchemicalRoom);
        alchemicalRoom.addItem(cauldron);
        
        Item chest = new Item("chest", "An old wooden chest covered in runes.", null, false, false);
        chest.setHiddenItem("potion");
        chest.setRoom(alchemicalRoom);
        alchemicalRoom.addItem(chest);

        // ENGINEERING WORKSHOP ROOM - Touch required!
        engineeringRoom.addHint("[TOUCH UNLOCKED!] OUCH! EVERYTHING IS HOT! The pipes are VIBRATING!");
        engineeringRoom.addHint("The machinery is OVERHEATING! You can FEEL the heat!");
        engineeringRoom.addHint("There's an old manual on the ground... Does it help?");
        engineeringRoom.addHint("TRICK: Read the manual to unlock your INTUITION!");
        
        Item intuitiveGuide = new Item("intuitive guide", "THE INTUITIVE GUIDE! Reading this awakens your SIXTH SENSE!", null, false, true);
        intuitiveGuide.setTaskItem(true);
        intuitiveGuide.setRoom(engineeringRoom);
        allItems.put("intuitive guide", intuitiveGuide);
        
        Item hotPipe = new Item("hot pipe", "OUCH! This pipe is BURNING HOT! Don't touch it!", null, false, false);
        hotPipe.setRoom(engineeringRoom);
        engineeringRoom.addItem(hotPipe);
        
        Item coldPipe = new Item("cold pipe", "Ahh... this pipe is nice and cool.", null, false, false);
        coldPipe.setRoom(engineeringRoom);
        engineeringRoom.addItem(coldPipe);
        
        Item valve = new Item("valve", "A rusty valve. You can FEEL it vibrating.", null, false, false);
        valve.setRoom(engineeringRoom);
        engineeringRoom.addItem(valve);
        
        Item fire_extinguisher = new Item("fire extinguisher", "A fire extinguisher. You need intuition, not fire safety!", null, false, true);
        fire_extinguisher.setRoom(engineeringRoom);
        allItems.put("fire extinguisher", fire_extinguisher);
        
        Item manual = new Item("old manual", "A dusty manual titled 'AWAKENING YOUR INTUITION'!", null, false, false);
        manual.setHiddenItem("intuitive guide");
        manual.setRoom(engineeringRoom);
        engineeringRoom.addItem(manual);
        
        Item locker = new Item("locker", "A metal locker covered in warning stickers.", null, false, false);
        locker.setHiddenItem("fire extinguisher");
        locker.setRoom(engineeringRoom);
        engineeringRoom.addItem(locker);

        // REACTOR CORE ROOM - Intuition required!
        reactorRoom.addHint("[INTUITION UNLOCKED!] Your SIXTH SENSE is tingling!");
        reactorRoom.addHint("Multiple switches glow with different colors!");
        reactorRoom.addHint("TRUST YOUR GUT! One switch FEELS different!");
        reactorRoom.addHint("FINAL TRICK: The LEFT switch feels WARM... like it's calling you!");
        
        Item leftSwitch = new Item("left switch", "The LEFT switch. It feels... WARM. Your intuition says: THIS IS IT!", null, false, true);
        leftSwitch.setTaskItem(true);
        leftSwitch.setRoom(reactorRoom);
        reactorRoom.addItem(leftSwitch);
        
        Item rightSwitch = new Item("right switch", "The RIGHT switch. Cold. Lifeless. Wrong choice!", null, false, true);
        rightSwitch.setRoom(reactorRoom);
        reactorRoom.addItem(rightSwitch);
        
        Item centerSwitch = new Item("center switch", "The CENTER switch. Blinking randomly. Feels... wrong.", null, false, true);
        centerSwitch.setRoom(reactorRoom);
        reactorRoom.addItem(centerSwitch);
        
        Item topSwitch = new Item("top switch", "The TOP switch. Sparking dangerously. Nope!", null, false, true);
        topSwitch.setRoom(reactorRoom);
        reactorRoom.addItem(topSwitch);
        
        Item bottomSwitch = new Item("bottom switch", "The BOTTOM switch. Covered in dust. Hasn't been used in years!", null, false, true);
        bottomSwitch.setRoom(reactorRoom);
        reactorRoom.addItem(bottomSwitch);

        currentRoom = powerRoom;
    }

    private void loadTasks() {
        Task powerTask = new Task("Restore Power", "Reconnect the wires using the RIGHT tool. [👁️ SIGHT REQUIRED]", Sense.SIGHT, Sense.HEARING);
        powerTask.addStep("1. INSPECT the closet, toolbox, and shelf to find tools.");
        powerTask.addStep("2. TAKE the WRENCH (the correct tool!)");
        powerTask.addStep("3. USE wrench to reconnect the circuit.");
        Item soundKey = new Item("Sound Room Key", "Opens the Sound Calibration Room.", null, true, false);
        soundKey.setUnlocksRoomId(2);
        powerTask.addRewardItem(soundKey);

        Task soundTask = new Task("Calibrate Sound", "Use your HEARING to fix the frequencies. [HEARING REQUIRED]", Sense.HEARING, Sense.SMELL);
        soundTask.addStep("1. LISTEN to the chaotic sounds.");
        soundTask.addStep("2. INSPECT the wall panel to find the amplifier.");
        soundTask.addStep("3. USE amplifier to stabilize frequencies.");
        Item freqKey = new Item("Frequency Key", "Opens the Chemical Storage Room.", null, true, false);
        freqKey.setUnlocksRoomId(3);
        soundTask.addRewardItem(freqKey);

        Task chemicalTask = new Task("Seal Chemical Leaks", "Use your SMELL to find leaks. [SMELL REQUIRED]", Sense.SMELL, Sense.TASTE);
        chemicalTask.addStep("1. SMELL the air - rotten eggs = leaking chemicals!");
        chemicalTask.addStep("2. INSPECT the ventilation shaft (it's high up).");
        chemicalTask.addStep("3. USE sealant on the RED containers.");
        Item alchemicalKey = new Item("Alchemical Key", "Opens the Alchemical Lab.", null, true, false);
        alchemicalKey.setUnlocksRoomId(4);
        chemicalTask.addRewardItem(alchemicalKey);

        Task alchemicalTask = new Task("Create Stabilizing Potion", "Use TASTE to identify ingredients. [TASTE REQUIRED]", Sense.TASTE, Sense.TOUCH);
        alchemicalTask.addStep("1. TASTE the herb (bitter), crystal (sweet), mushroom (sour).");
        alchemicalTask.addStep("2. INSPECT the cauldron - what's brewing?");
        alchemicalTask.addStep("3. USE coolant flask to create the potion.");
        Item engineeringKey = new Item("Engineering Key", "Opens the Engineering Workshop.", null, true, false);
        engineeringKey.setUnlocksRoomId(5);
        alchemicalTask.addRewardItem(engineeringKey);

        Task engineeringTask = new Task("Cool Down Machinery", "Use TOUCH to feel the heat. [TOUCH REQUIRED]", Sense.TOUCH, Sense.INTUITION);
        engineeringTask.addStep("1. FEEL the hot pipes - OUCH! They're burning!");
        engineeringTask.addStep("2. INSPECT the old manual on the ground.");
        engineeringTask.addStep("3. USE intuitive guide to awaken INTUITION.");
        Item coreKey = new Item("Core Key", "Opens the Reactor Core!", null, true, false);
        coreKey.setUnlocksRoomId(6);
        engineeringTask.addRewardItem(coreKey);

        Task reactorTask = new Task("STOP THE REACTOR!", "Use INTUITION to pick the right switch! [INTUITION REQUIRED]", Sense.INTUITION, null);
        reactorTask.addStep("1. FEEL each switch carefully.");
        reactorTask.addStep("2. TRUST your GUT - one feels RIGHT.");
        reactorTask.addStep("3. USE the WARM LEFT SWITCH to stop the reactor!");

        tasks.add(powerTask);
        tasks.add(soundTask);
        tasks.add(chemicalTask);
        tasks.add(alchemicalTask);
        tasks.add(engineeringTask);
        tasks.add(reactorTask);
    }

    private void linkTasksToRooms() {
        rooms.get(1).setTask(tasks.get(0));
        rooms.get(2).setTask(tasks.get(1));
        rooms.get(3).setTask(tasks.get(2));
        rooms.get(4).setTask(tasks.get(3));
        rooms.get(5).setTask(tasks.get(4));
        rooms.get(6).setTask(tasks.get(5));
    }

    private void unlockRooms() {
        if (tasks.get(0).isCompleted() && rooms.get(2).isLocked()) {
            rooms.get(2).unlock();
            System.out.println("\n[SYSTEM: Sound Calibration Room UNLOCKED!]");
        }
        if (tasks.get(1).isCompleted() && rooms.get(3).isLocked()) {
            rooms.get(3).unlock();
            System.out.println("\n[SYSTEM: Chemical Storage Room UNLOCKED!]");
        }
        if (tasks.get(2).isCompleted() && rooms.get(4).isLocked()) {
            rooms.get(4).unlock();
            System.out.println("\n[SYSTEM: Alchemical Lab Room UNLOCKED!]");
        }
        if (tasks.get(3).isCompleted() && rooms.get(5).isLocked()) {
            rooms.get(5).unlock();
            System.out.println("\n[SYSTEM: Engineering Workshop Room UNLOCKED!]");
        }
        if (tasks.get(4).isCompleted() && rooms.get(6).isLocked()) {
            rooms.get(6).unlock();
            System.out.println("\n[SYSTEM: REACTOR CORE ROOM UNLOCKED!]");
            System.out.println("THIS IS IT! The final challenge awaits!");
        }
    }

    /**
    * Processes player commands and calls appropriate methods
    * Supported commands: move, take, inspect, use, help, quit
    */

    public void processCommand(Command cmd) {
        if (cmd == null || !cmd.isValid()) {
            System.out.println("I don't understand that.");
            return;
        }

        switch (cmd.getType()) {
            case MOVE:
                try {
                    Direction dir = Direction.valueOf(cmd.getTarget().toUpperCase());
                    moveToRoom(dir);
                } catch (IllegalArgumentException e) {
                    System.out.println("You can't go that way.");
                }
                break;

            case TAKE:
                takeItem(cmd.getTarget());
                break;

            case INSPECT:
                inspectItem(cmd.getTarget());
                break;

            case USE:
                useItem(cmd.getTarget());
                break;

            case HELP:
                showHelp();
                break;

            case QUIT:
                endGame(false);
                break;
        }

        if (cmd == null || !cmd.isValid()) {
                System.out.println("I don't understand that.");
                return;
        }
    }



    /** Lets the player take an item */
    private void takeItem(String itemName) {
        Item item = currentRoom.getItem(itemName);
        
        if (item == null) {
            System.out.println("You don't see that item here.");
            return;
        }

        List<String> containers = Arrays.asList("closet", "toolbox", "shelf", "wall panel", 
                                          "cabinet", "ventilation shaft", "crate", 
                                          "cauldron", "chest", "manual", "locker");
    
        if (containers.contains(itemName.toLowerCase())) {
              System.out.println("The " + itemName + " is too large/heavy to carry! Try 'inspect " + itemName + "' instead.");
              return;
        }
    
        List<String> switches = Arrays.asList("left switch", "right switch", "center switch", 
                                        "top switch", "bottom switch");
        if (switches.contains(itemName.toLowerCase())) {
             System.out.println("The " + itemName + " is fixed to the wall! You can't take it. Try 'use " + itemName + "' instead.");
             return;
        }

        if (!item.isTakeable()) {
            System.out.println("You cannot take the " + itemName + ". Try inspecting it instead.");
            return;
        }
        
        // Check if this item is a container with hidden items
        if (item.getHiddenItem() != null) {
            Item hiddenItem = allItems.get(item.getHiddenItem().toLowerCase());
            
            if (hiddenItem != null && !currentRoom.containsItem(hiddenItem)) {
                currentRoom.addItem(hiddenItem);
                System.out.println("You opened the " + item.getName() + " and found: " + item.getHiddenItem() + "!");
                return;
            }
        }
        
        player.addItem(item);
        currentRoom.removeItem(item);
    }
    /** Lets the player inspect an item */
    private void inspectItem(String itemName) {
        Item playerItem = player.getItem(itemName);
        if (playerItem != null) {
            System.out.println(playerItem.inspect());
            return;
        }
        
        Item roomItem = currentRoom.getItem(itemName);
        if (roomItem != null) {
            System.out.println(roomItem.inspect());
            
            if (roomItem.getHiddenItem() != null) {
                Item hiddenItem = allItems.get(roomItem.getHiddenItem().toLowerCase());
                if (hiddenItem != null && !currentRoom.containsItem(hiddenItem)) {
                    currentRoom.addItem(hiddenItem);
                }
            }
        } else {
             System.out.println("You don't see that here.");
        }
    }
    /** Lets the player use an item */
    private void useItem(String itemName) {
        Item playerItem = player.getItem(itemName);
        // Special case: switches in reactor room (room ID 6) can be used directly from room
        if (playerItem == null && currentRoom.getRoomId() == 6 && itemName.toLowerCase().contains("switch")) {
            playerItem = currentRoom.getItem(itemName);
             if (playerItem == null) {
                System.out.println("You don't have that item.");
                return;
            }
        }
        if (playerItem != null) {
            String result = playerItem.useInRoom(player, currentRoom);
            System.out.println(result);
            
            // Check if task was completed
            Task roomTask = currentRoom.getTask();
            if (roomTask != null && roomTask.isCompleted()) {
                System.out.println("\n--- TASK COMPLETED ---");
                System.out.println("Sense Restored: " + roomTask.getRewardSense());

                List<Item> rewards = roomTask.getRewardItems();
                 if (!rewards.isEmpty()) {
                      System.out.println("Items Received:");
                      for (Item item : rewards) {
                         System.out.println("  - " + item.getName());
                        }
                }

                System.out.println("\nAvailable Exits:");
                HashMap<Direction, Room> connectedRooms = currentRoom.getConnectedRooms();
                for (Direction dir : connectedRooms.keySet()) {
                      Room adjacent = connectedRooms.get(dir);
                      System.out.println("  - " + dir.toString().toLowerCase() + ": " + adjacent.getName());
                }
            
                System.out.println();
            }
        } else {
            System.out.println("You don't have that item.");
        }
    }

    /**
     * Moves the player to an adjacent room if possible.
     *
     * @param dir the direction in which the player wants to move
     */
    private void moveToRoom(Direction dir) {
        if (dir == null) {
            System.out.println("Move where?");
            return;
        }

        Room next = currentRoom.getAdjacentRoom(dir);

        if (next == null) {
            System.out.println("You cannot go that way.");
        } else if (next.isLocked()) {
            System.out.println("This room is locked. " + next.getUnlockCondition());
        } else {
            currentRoom = next;
            player.moveTo(next);
            System.out.println("\nYou move " + dir.toString().toLowerCase() + "...\n");
            currentRoom.describeRoom();
        }
    }

    private void showHelp() {
        System.out.println("\n=== GUIDANCE ===");
        System.out.println("Current Senses: " + player.getSensesRestored());
        System.out.println("\n=== ROOM HINTS ===");
        List<String> hints = currentRoom.getHints();
        if (hints != null && !hints.isEmpty()) {
            for (String hint : hints) {
                System.out.println("  - " + hint);
            }
        } else {
            System.out.println("  No hints available.");
        }

        // Show current task information
        Task task = currentRoom.getTask();
        if (task != null && !task.isCompleted()) {
            System.out.println("\n=== TASK: " + task.getName() + " ===");
            System.out.println(task.getDescription());
            System.out.println("Required Sense: " + task.getRequiredSense());
            System.out.println("\nSteps:");
            List<String> steps = task.getSteps();
            if (steps != null && !steps.isEmpty()) {
                for (int i = 0; i < steps.size(); i++) {
                    System.out.println((i + 1) + ". " + steps.get(i));
                }
            }
        }
        System.out.println();
    }
    /**
     * Checks whether the player has completed all tasks
     * and reached the reactor room.
     * If so, the game is won.
     */
    public void checkWinCondition() {
        boolean allTasksCompleted = true;
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                allTasksCompleted = false;
                break;
            }
        }

        //Player wins if all tasks complete and they're in reactor room 
        if (allTasksCompleted && currentRoom.getRoomId() == 6) {
            endGame(true);
        }
    }
    /**
     * Checks whether the time limit has been exceeded.
     * If so, the game is lost.
     */
    public void checkLoseCondition() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= timeLimit) {
            endGame(false);
        }
    }

    private void displayTimer() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        long remaining = (timeLimit - elapsed) / 1000;
        long minutes = remaining / 60;
        long seconds = remaining % 60;
    
        System.out.println("[Time remaining: " + minutes + "m " + seconds + "s]");

        if (minutes == 15 && seconds == 0) {
            System.out.println("[WARNING: 15 minutes remaining!]");
        } else if (minutes == 10 && seconds == 0) {
            System.out.println("[WARNING: 10 minutes remaining! Hurry!]");
        } else if (minutes == 5 && seconds == 0) {
            System.out.println("[WARNING: 5 minutes remaining! This is CRITICAL!]");
        } else if (minutes == 2 && seconds == 0) {
            System.out.println("[FINAL WARNING: 2 minutes remaining!]");
        } else if (minutes == 1 && seconds == 0) {
            System.out.println("[EMERGENCY: 1 minute remaining!]");
        } else if (minutes == 0 && seconds == 30) {
            System.out.println("[EMERGENCY: 30 seconds!]");
        } else if (minutes == 0 && seconds <= 10 && seconds > 0) {
            System.out.println("[FINAL COUNTDOWN: " + seconds + " seconds!]");
        }
    }
 
    public void endGame(boolean won) {
        isRunning = false;
        if (won) {
            System.out.println("\n============ YOU WIN! =============");
            System.out.println("You stopped the reactor and saved the lab!");
            System.out.println("All your senses have been restored!");
            System.out.println("===================================\n");
        } else {
            System.out.println("\n================ GAME OVER ================");
            System.out.println("The reactor exploded! The lab is destroyed.");
            System.out.println("=============================================\n");
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Player getPlayer() {
        return player;
    }


    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}