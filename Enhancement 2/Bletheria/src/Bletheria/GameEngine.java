package Bletheria;

/**
 * GameEngine controls the main gameplay loop, input handling, and interactions.
 * Enhancement 2: Adds a 'hint' command that uses BFS pathfinding to show
 * the shortest route from the player's current room to a target room,
 * and a 'rooms' command to list all available room names.
 */
import java.util.Scanner;
import java.util.List;

public class GameEngine {
    private final Scanner scanner;
    private final WorldMap world;
    private final Player player;
    private final EndingResolver endings;
    private String lastMessage = "";

    public GameEngine(Scanner scanner, WorldMap world, Player player, EndingResolver endings) {
        this.scanner = scanner;
        this.world = world;
        this.player = player;
        this.endings = endings;
    }

    //Handles the introduction and player setup before the game begins.
    public static Player runIntroSetup(Scanner scanner, WorldMap world) {
        System.out.println("Welcome to Bletheria!");
        System.out.println("""
Your master was murdered by the High Demon Wizard Nozgorath the Decrepit.  
You must make your way through the lower demon city of Black Bletheria in The Realm of Misfortune, 
to the Demon's high temple to take your revenge, avenge your master, and try not to let 
the darkness consume you along the way.\n\n
In order to exact your revenge, you must collect the following:
    - The Rare Charred Demon Wand to vanquish Nozgorath, 
    - The Invisibility Cloak to hide your intentions, 
    - The Dark Potion of Bravery to steady your nerves, 
    - The Spellbook of the Dead and Withered to bolster your magic prowess, 
    - The Demon Grieves of Wiwaria Common to protect your soul, 
    - The Mask of Intuition to increase your perception, 
    - Finally, if you are high in luck, the Secret Key to unlock High Demon Wisdom
""");

        System.out.print("What is your name, adventurer? ");
        String name = scanner.nextLine().trim();
        clear();

        System.out.println(name + " the Lowly! Welcome to The Realm of Misfortune.");
        System.out.println("""
To move: use 'travel north/south/east/west'.
To collect an item: use 'equip <item>'.
To see all room names: use 'rooms'.
To get help finding a room: use 'hint <room name>'.
""");

        System.out.print("Will you buff Wisdom or Perception? ");
        String attribute = cap(scanner.nextLine());

        // Player choice determines if secret area is locked
        if ("Wisdom".equals(attribute)) {
            System.out.println("\nExcellent choice wizard. Wise you shall be.\n");
            world.lockSecretEnding();
        } else {
            System.out.println("\nInteresting choice wizard. Stay alert, the realm of misfortune has many secrets to uncover.\n");
        }

        return new Player(name, attribute, "Ashen Foyer");
    }

    //Primary gameplay loop. Runs until the user types "exit".
    public void run() {
        while (true) {
            printHud();
            Room room = world.getRoom(player.getCurrentRoom());

            if (room == null) {
                System.out.println("You are lost between realms… exiting.");
                break;
            }

            // Display any visible item
            if (room.getItem() != null && !player.hasItem(room.getItem())) {
                System.out.println("You see the " + room.getItem() + " on a pedestal.\n");
            }

            // If in the boss room, show ending text
            if (room.getBossName() != null) {
                System.out.println(endings.getEndingText(player));
            }

            System.out.print("Enter your move: ");
            String input = scanner.nextLine();
            clear();

            // Split command into action + argument
            String[] parts = input.trim().split("\\s+");
            if (parts.length == 0 || parts[0].isBlank()) {
                lastMessage = "Invalid command.\n";
                continue;
            }

            String action = cap(parts[0]);
            String argument = parts.length > 1 ? join(parts, 1) : "";

            switch (action) {
                case "Travel" -> handleTravel(argument);
                case "Equip"  -> handleEquip(argument);
                case "Hint"   -> handleHint(argument);   // BFS hint
                case "Rooms"  -> handleListRooms();      // list rooms
                case "Exit"   -> { return; }
                default       -> lastMessage = "Invalid command.\n";
            }
        }
    }

    //movement between rooms.
    private void handleTravel(String directionRaw) {
        String direction = cap(directionRaw);
        Room current = world.getRoom(player.getCurrentRoom());

        if (current == null) { lastMessage = "You can't go that way.\n"; return; }

        String next = current.getExit(direction);
        if (next != null && world.hasRoom(next)) {
            player.setCurrentRoom(next);
            lastMessage = "You travel " + direction + "\n";
        } else {
            lastMessage = "You can't go that way.\n";
        }
    }

    //item collection.
    private void handleEquip(String itemRaw) {
        Room current = world.getRoom(player.getCurrentRoom());
        if (current == null) { lastMessage = "Can't find " + itemRaw + "\n"; return; }

        String desired = cap(itemRaw);
        String roomItem = current.getItem() == null ? null : cap(current.getItem());

        if (roomItem != null && roomItem.equals(desired)) {
            if (!player.hasItem(roomItem)) {
                player.addItem(roomItem);
                lastMessage = roomItem + " equipped!\n";
            } else {
                lastMessage = "You already have the " + roomItem + "\n";
            }
        } else {
            lastMessage = "Can't find " + desired + "\n";
        }
    }

    /**
     * Enhancement 2:
     * Uses WorldMap's BFS pathfinding to show the shortest path from the
     * player's current room to a target room.
     *
     * Command format:  hint Demon High Temple
     */
    private void handleHint(String targetRoomRaw) {
        if (targetRoomRaw == null || targetRoomRaw.isBlank()) {
            lastMessage = "Please specify a room name, e.g., 'hint Demon High Temple'.\n";
            return;
        }

        // Normalize the room name using a case-insensitive lookup
        String resolvedRoomName = world.findRoomNameIgnoreCase(targetRoomRaw);
        if (resolvedRoomName == null) {
            lastMessage = "No such room: " + targetRoomRaw + "\n";
            return;
        }

        if (resolvedRoomName.equals(player.getCurrentRoom())) {
            lastMessage = "You are already in " + resolvedRoomName + ".\n";
            return;
        }

        List<String> path = world.findShortestPath(player.getCurrentRoom(), resolvedRoomName);

        if (path.isEmpty()) {
            lastMessage = "No path found from " + player.getCurrentRoom()
                        + " to " + resolvedRoomName + ".\n";
            return;
        }
   
        // Build directional instructions
        StringBuilder sb = new StringBuilder();
        sb.append("Shortest path to ").append(resolvedRoomName).append(":\n\n");

        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            String direction = world.getDirectionBetween(from, to);

            sb.append((i + 1))
              .append(". From ")
              .append(from)
              .append(", go ")
              .append(direction == null ? "??" : direction)
              .append(" to ")
              .append(to)
              .append("\n");
        }

        lastMessage = sb.toString();
    }

    /**
     * Lists all currently available rooms in the world.
     * This lets the player know what target names they can use with the hint command.
     *
     * Command: rooms
     */
    private void handleListRooms() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available rooms:\n");
        for (String roomName : world.getAllRoomNames()) {
            sb.append("- ").append(roomName).append("\n");
        }
        lastMessage = sb.toString();
    }

    /** Prints current room, inventory, and feedback. */
    private void printHud() {
        System.out.println("""
------------------------------
Commands:
travel <direction>
equip <item>
hint <room name>
rooms
exit
------------------------------""");
        System.out.println("You are in the " + player.getCurrentRoom());
        System.out.println("Inventory: " + player.getInventory());
        System.out.println("\n" + lastMessage);
    }

    //capitalizes input consistently
    private static String cap(String s) {
        if (s == null || s.isEmpty()) return "";
        s = s.trim();
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    //joins command arguments
    private static String join(String[] arr, int startIdx) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIdx; i < arr.length; i++) {
            if (i > startIdx) sb.append(' ');
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    // Simple "clear screen" effect (visual spacing only)
    private static void clear() { System.out.print("\n\n"); }
}