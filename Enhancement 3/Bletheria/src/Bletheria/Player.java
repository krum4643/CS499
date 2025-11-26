package Bletheria;

/**
 * The Player class stores the player's state and inventory.
 * It keeps track of the current room, name, and attribute choice (Wisdom or Perception).
 */
import java.util.LinkedHashSet;
import java.util.Set;

public class Player {
    private final String name;            // Player's display name (we'll use username here)
    private final String attributeChoice; // Wisdom or Perception
    private String currentRoom;           // Room player is currently in
    private final Set<String> inventory = new LinkedHashSet<>(); // Items collected
    private final int userId;            // Database user ID

    // Constructor to initialize player details
    public Player(String name, String attributeChoice, String startRoom, int userId) {
        this.name = name;
        this.attributeChoice = attributeChoice;
        this.currentRoom = startRoom;
        this.userId = userId;
    }

    // Basic getters and setters
    public String getName() { return name; }
    public String getAttributeChoice() { return attributeChoice; }
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String room) { this.currentRoom = room; }
    public int getUserId() { return userId; }

    // Adds item to inventory (avoiding duplicates)
    public void addItem(String item) {
        inventory.add(capitalize(item.trim()));
    }

    // Checks if the player already has an item
    public boolean hasItem(String item) {
        return inventory.contains(capitalize(item.trim()));
    }

    // Returns full inventory
    public Set<String> getInventory() { return inventory; }

    /**
     * Loads inventory items from a comma-separated string
     * stored in the database.
     */
    public void loadInventoryFromCsv(String csv) {
        if (csv == null || csv.isBlank()) return;
        String[] parts = csv.split(",");
        for (String raw : parts) {
            String item = raw.trim();
            if (!item.isEmpty()) {
                addItem(item);
            }
        }
    }

    // Helper to capitalize input consistently
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
