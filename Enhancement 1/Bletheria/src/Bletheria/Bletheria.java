package Bletheria;

/**
 * Entry point for the Bletheria text adventure game.
 * This class initializes the core components and starts the game loop.
 */
import java.util.Scanner;

public class Bletheria {
    public static void main(String[] args) {
        // Create a Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Build the game world
        WorldMap world = new WorldMap();

        // Run the setup sequence and create a Player object
        Player player = GameEngine.runIntroSetup(scanner, world);

        // Initialize the ending resolver
        EndingResolver endings = new EndingResolver();

        // Start the main game engine loop
        GameEngine engine = new GameEngine(scanner, world, player, endings);
        engine.run();

        // Clean up
        scanner.close();
    }
}
