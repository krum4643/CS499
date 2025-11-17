package Bletheria;

/**
 * The WorldMap class builds and stores all rooms in the game.
 * It also handles special logic such as removing the secret ending if the player chooses Wisdom.
 */
import java.util.HashMap;
import java.util.Map;

public class WorldMap {
    private final Map<String, Room> rooms = new HashMap <> ();

    public WorldMap() {
        buildWorld();
    }

    // Accessors
    public Room getRoom(String name) { return rooms.get(name); }
    public boolean hasRoom(String name) { return rooms.containsKey(name); }

    // Removes the secret cavern and its connection for the "Wisdom" path
    public void lockSecretEnding() {
        Room hut = rooms.get("Forgotten Hut of Secret Spaces");
        if (hut != null) hut.removeExit("East");
        rooms.remove("Secret Cavern");
    }

    // Builds all rooms and links them
    private void buildWorld() {
        // Create each room and assign items/bosses
        Room ashen = add("Ashen Foyer");
        Room acrid = add("The Acrid Swamp"); acrid.setItem("Potion");
        Room workshop = add("Cursed Workshop of Azazel the Ensnared"); workshop.setItem("Mask");
        Room rotten = add("Rotten Forrest"); rotten.setItem("Wand");
        Room wiwaria = add("Wiwaria Common"); wiwaria.setItem("Grieves");
        Room hut = add("Forgotten Hut of Secret Spaces"); hut.setItem("Cloak");
        Room cavern = add("Secret Cavern"); cavern.setItem("Key");
        Room library = add("The Haunted Library of Wayward Souls"); library.setItem("Spellbook");
        Room temple = add("Demon High Temple"); temple.setBossName("Nozgorath the Decrepit");

        // Define exits between rooms
        ashen.addExit("South", "The Acrid Swamp");
        ashen.addExit("North", "Wiwaria Common");
        ashen.addExit("East", "The Haunted Library of Wayward Souls");
        ashen.addExit("West", "Rotten Forrest");

        acrid.addExit("North", "Ashen Foyer");
        acrid.addExit("East", "Cursed Workshop of Azazel the Ensnared");

        workshop.addExit("West", "The Acrid Swamp");
        rotten.addExit("East", "Ashen Foyer");

        wiwaria.addExit("South", "Ashen Foyer");
        wiwaria.addExit("East", "Forgotten Hut of Secret Spaces");

        hut.addExit("West", "Wiwaria Common");
        hut.addExit("East", "Secret Cavern");

        cavern.addExit("West", "Forgotten Hut of Secret Spaces");

        library.addExit("West", "Ashen Foyer");
        library.addExit("North", "Demon High Temple");
    }

    // Helper for adding rooms to the map
    private Room add(String name) {
        Room r = new Room(name);
        rooms.put(name, r);
        return r;
    }
}
