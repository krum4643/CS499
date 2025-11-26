package Bletheria;

/**
 * The WorldMap class builds and stores all rooms in the game.
 * It also handles special logic such as removing the secret ending
 * if the player chooses Wisdom, and provides pathfinding utilities
 * for the Algorithms & Data Structures enhancement.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldMap {
    // All rooms in the game, looked up by room name
    private final Map<String, Room> rooms = new HashMap<>();

    public WorldMap() {
        buildWorld();
    }

    // Accessors
    public Room getRoom(String name) { return rooms.get(name); }
    public boolean hasRoom(String name) { return rooms.containsKey(name); }

    //Returns all current room names
    public Set<String> getAllRoomNames() {
        return rooms.keySet();
    }

    //finds room name
    public String findRoomNameIgnoreCase(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;

        for (String roomName : rooms.keySet()) {
            if (roomName.equalsIgnoreCase(trimmed)) {
                return roomName;
            }
        }
        return null;
    }

   //removes secret cavern
    public void lockSecretEnding() {
        Room hut = rooms.get("Forgotten Hut of Secret Spaces");
        if (hut != null) hut.removeExit("East");  // remove path to Secret Cavern
        rooms.remove("Secret Cavern");            // remove the room itself
    }

    /**
     * Algorithms & Data Structures enhancement:
     * Uses Breadth-First Search (BFS) to find the shortest path
     * between two rooms in the world graph.
     *
     * @param startRoomName the name of the starting room
     * @param goalRoomName  the name of the target room
     * @return a list of room names in the path from start to goal,
     * or an empty list if no path exists.
     */
    public List<String> findShortestPath(String startRoomName, String goalRoomName) {
        List<String> path = new ArrayList<>();

        // Validate both rooms exist
        if (!rooms.containsKey(startRoomName) || !rooms.containsKey(goalRoomName)) {
            return path; // empty list = "no path"
        }

        // Standard BFS setup
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> cameFrom = new HashMap<>();

        queue.add(startRoomName);
        visited.add(startRoomName);
        cameFrom.put(startRoomName, null);

        // BFS traversal over room graph
        while (!queue.isEmpty()) {
            String current = queue.remove();

            if (current.equals(goalRoomName)) {
                break; // reached the goal
            }

            Room room = rooms.get(current);
            if (room == null) continue;

            //all connected room names
            for (String neighbor : room.getExits().values()) {
                if (!visited.contains(neighbor) && rooms.containsKey(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // If we never reached the goal, there's no path
        if (!cameFrom.containsKey(goalRoomName)) {
            return path; // still empty
        }

        // Reconstruct path from goal back to start using cameFrom map
        String crawl = goalRoomName;
        List<String> reversed = new ArrayList<>();
        while (crawl != null) {
            reversed.add(crawl);
            crawl = cameFrom.get(crawl);
        }

        for (int i = reversed.size() - 1; i >= 0; i--) {
            path.add(reversed.get(i));
        }

        return path;
    }
    
    
    //Determines which direction you must travel to move
    public String getDirectionBetween(String fromRoomName, String toRoomName) {
        Room from = rooms.get(fromRoomName);
        if (from == null) return null;

        for (Map.Entry<String, String> exit : from.getExits().entrySet()) {
            if (exit.getValue().equals(toRoomName)) {
                return exit.getKey();
            }
        }
        return null;
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
        ashen.addExit("South","The Acrid Swamp");
        ashen.addExit("North","Wiwaria Common");
        ashen.addExit("East","The Haunted Library of Wayward Souls");
        ashen.addExit("West","Rotten Forrest");

        acrid.addExit("North","Ashen Foyer");
        acrid.addExit("East","Cursed Workshop of Azazel the Ensnared");

        workshop.addExit("West","The Acrid Swamp");
        rotten.addExit("East","Ashen Foyer");

        wiwaria.addExit("South","Ashen Foyer");
        wiwaria.addExit("East","Forgotten Hut of Secret Spaces");

        hut.addExit("West","Wiwaria Common");
        hut.addExit("East","Secret Cavern");

        cavern.addExit("West","Forgotten Hut of Secret Spaces");

        library.addExit("West","Ashen Foyer");
        library.addExit("North","Demon High Temple");
    }

    // Helper for adding rooms to the map
    private Room add(String name) {
        Room r = new Room(name);
        rooms.put(name, r);
        return r;
    }
}
