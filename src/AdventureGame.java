import java.util.*;

public class AdventureGame {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, Room> rooms = new HashMap<>();
    static String currentRoom = "Entrance";
    static List<String> inventory = new ArrayList<>();
    static int playerHealth = 100;

    public static void main(String[] args) {
        setupGame();
        System.out.println("Welcome to the Text-Based Adventure Game!");
        System.out.println("Your goal is to reach the Treasure Room and collect the treasure.");
        System.out.println("Type commands like the following:");
        System.out.println("1. 'go <direction>' - Move to a different room.");
        System.out.println("2. 'check inventory' - View the items you have collected.");
        System.out.println("3. 'pick up <item>' - Pick up an item from the room and add it to your inventory (e.g., potion, treasure).");
        System.out.println("4. 'talk' - Talk to an NPC (Non-Player Character) in the room.");
        System.out.println("5. 'attack' - Fight an enemy that may be present in the room.");
        System.out.println("6. 'run' - Run away from a fight with an enemy.");
        System.out.println();

        while (true) {
            Room room = rooms.get(currentRoom);
            System.out.println("You are in the " + currentRoom + ".");
            System.out.println(room.getDescription());
            System.out.println("Exits: " + room.getExits());
            System.out.print("> ");
            String command = scanner.nextLine().toLowerCase();

            if (command.startsWith("go ")) {
                move(command.substring(3));
            } else if (command.equals("check inventory")) {
                checkInventory();
            } else if (command.startsWith("pick up ")) {
                pickUpItem(command.substring(8), room);
            } else if (command.equals("talk")) {
                talkToNPC(room);
            } else if (command.startsWith("attack") || command.equals("run")) {
                combat(command, room);
            } else {
                System.out.println("Invalid command. Try again.");
            }

            if (playerHealth <= 0) {
                System.out.println("Game Over! You have been defeated.");
                break;
            }

            if (inventory.contains("treasure")) {
                System.out.println("You Win! You have collected the treasure.");
                break;
            }
        }
    }

    static void setupGame() {
        Room entrance = new Room("You are at the entrance of a mysterious world.");
        entrance.setExit("north", "Forest");
        entrance.addNPC(new NPC("Guard",
                "Welcome to the adventure! Be careful, the path ahead is treacherous.", null));

        Room forest = new Room("You are in a dense forest. There's a potion here.");
        forest.setExit("south", "Entrance");
        forest.setExit("east", "Dungeon");
        forest.addItem("potion");
        forest.addNPC(new NPC("Wanderer",
                "Greetings, adventurer. The dungeon ahead is dangerous, but you'll find riches there.",
                "golden coin"));

        Room dungeon = new Room("You are in a dark dungeon. An enemy is here!");
        dungeon.setExit("west", "Forest");
        dungeon.setExit("north", "Treasure Room");
        dungeon.addEnemy(new Enemy("Goblin", 30, 10));
        dungeon.addNPC(new NPC("Captured Knight",
                "Help me escape! The goblin is guarding a great treasure.", null));

        Room treasureRoom = new Room("You are in the Treasure Room. The treasure is here!");
        treasureRoom.addItem("treasure");
        treasureRoom.addNPC(new NPC("Treasure Keeper",
                "Congratulations! You've reached the treasure. Take it and be victorious!", null));

        rooms.put("Entrance", entrance);
        rooms.put("Forest", forest);
        rooms.put("Dungeon", dungeon);
        rooms.put("Treasure Room", treasureRoom);
    }

    static void move(String direction) {
        Room room = rooms.get(currentRoom);
        String nextRoom = room.getExit(direction);

        if (nextRoom != null) {
            currentRoom = nextRoom;
        } else {
            System.out.println("You can't go that way.");
        }
    }

    static void checkInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Your inventory: " + inventory);
        }
    }

    static void pickUpItem(String item, Room room) {
        if (room.hasItem(item)) {
            if (!inventory.contains(item)) {
                System.out.println("You picked up the " + item + "!");
                inventory.add(item);
                room.removeItem(item);
            } else {
                System.out.println("You already have the " + item + ".");
            }
        } else {
            System.out.println("There's no " + item + " here to pick up.");
        }
    }

    static void talkToNPC(Room room) {
        NPC npc = room.getNPC();
        if (npc != null) {
            System.out.println(npc.getName() + " says: " + npc.getDialogue());
            if (npc.hasItem()) {
                System.out.println("The NPC gives you a " + npc.getItem() + ".");
                inventory.add(npc.getItem());
            }
        } else {
            System.out.println("There's no one to talk to here.");
        }
    }

    static void combat(String command, Room room) {
        Enemy enemy = room.getEnemy();
        if (enemy != null) {
            if (command.equals("attack")) {
                System.out.println("You attack the " + enemy.getName() + ".");
                enemy.decreaseHealth(20);
                if (enemy.getHealth() <= 0) {
                    System.out.println("You defeated the " + enemy.getName() + "!");
                    room.removeEnemy();
                } else {
                    playerHealth -= enemy.getAttackPower();
                    System.out.println("The " + enemy.getName() + " attacks you! Your health: " + playerHealth);
                }
            } else if (command.equals("run")) {
                run(room);
            }
        } else {
            System.out.println("There's nothing to fight here.");
        }
    }

    static void run(Room room) {
        String[] exits = room.getExits().replaceAll("[\\[\\]]", "").split(", ");
        if (exits.length > 0) {
            String escapeDirection = exits[0];
            System.out.println("You ran away to the " + escapeDirection + ".");
            move(escapeDirection);
        } else {
            System.out.println("No way to escape!");
        }
    }
}

class Room {
    private String description;
    private Map<String, String> exits = new HashMap<>();
    private List<String> items = new ArrayList<>();
    private NPC npc;
    private Enemy enemy;

    public Room(String description) {
        this.description = description;
    }

    public void setExit(String direction, String roomName) {
        exits.put(direction, roomName);
    }

    public String getExit(String direction) {
        return exits.get(direction);
    }

    public void addItem(String item) {
        items.add(item);
    }

    public void removeItem(String item) {
        items.remove(item);
    }

    public boolean hasItem(String item) {
        return items.contains(item);
    }

    public String getDescription() {
        String itemDescription = items.isEmpty() ? "" : " You see: " + items;
        return description + itemDescription;
    }

    public void addNPC(NPC npc) {
        this.npc = npc;
    }

    public NPC getNPC() {
        return npc;
    }

    public void addEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void removeEnemy() {
        this.enemy = null;
    }

    public String getExits() {
        return exits.keySet().toString();
    }
}

class NPC {
    private String name;
    private String dialogue;
    private String item;

    public NPC(String name, String dialogue, String item) {
        this.name = name;
        this.dialogue = dialogue;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public String getDialogue() {
        return dialogue;
    }

    public boolean hasItem() {
        return item != null;
    }

    public String getItem() {
        return item;
    }
}

class Enemy {
    private String name;
    private int health;
    private int attackPower;

    public Enemy(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void decreaseHealth(int amount) {
        health -= amount;
    }
}
