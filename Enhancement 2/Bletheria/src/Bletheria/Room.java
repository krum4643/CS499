package Bletheria;

/**
 * The Room class defines each location in the world.
 * Each room has a name, optional item, exits, and an optional boss.
 */
import java.util.HashMap;
import java.util.Map;

public class Room {
    private final String name;                     // Room name
    private final Map<String, String> exits = new HashMap <> (); // Direction -> Room name
    private String item;                           // Optional item in this room
    private String bossName;                       // Optional boss name

    public Room(String name) {
        this.name = name;
    }

    // Basic getters/setters
    public String getName() { return name; }
    public Map<String, String> getExits() { return exits; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public String getBossName() { return bossName; }
    public void setBossName(String bossName) { this.bossName = bossName; }

    // Adds an exit to another room
    public void addExit(String direction, String destinationRoom) {
        exits.put(capitalize(direction), destinationRoom);
    }

    // Removes an exit (used to "lock" secret areas)
    public void removeExit(String direction) {
        exits.remove(capitalize(direction));
    }

    // Retrieves a connected room based on direction
    public String getExit(String direction) {
        return exits.get(capitalize(direction));
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        s = s.trim();
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
