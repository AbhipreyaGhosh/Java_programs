import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AddDltServer {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final HashMap<String, List<String>> serverData = new HashMap<>();

    // Hash function using SHA-256
    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(key.getBytes());
            int hashValue = 0;
            for (int i = 0; i < 4; i++) { // Use first 4 bytes (32 bits)
                hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
            }
            return Math.abs(hashValue); // Ensure positive value
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not available", e);
        }
    }

    // Add a server to the ring
    public void addServer(String server) {
        int hash = hash(server);
        ring.put(hash, server);
        serverData.put(server, new ArrayList<>());
        System.out.println("Added server: " + server);

        // Redistribute all keys after adding the server
        redistributeKeys();
    }

    // Remove a server from the ring and redistribute its keys
    public void removeServer(String server) {
        int hash = hash(server);
        List<String> keysToRedistribute = new ArrayList<>(serverData.get(server)); // Collect the keys to redistribute

        ring.remove(hash);
        serverData.remove(server);
        System.out.println("Removed server: " + server);

        // Redistribute the keys
        for (String key : keysToRedistribute) {
            addKey(key); // Reassign keys to the appropriate server
        }
    }

    // Find the nearest server for a key
    private String getServer(String key) {
        int keyHash = hash(key);
        if (ring.ceilingEntry(keyHash) != null) {
            return ring.ceilingEntry(keyHash).getValue(); // Next server clockwise
        }
        return ring.firstEntry().getValue(); // Wrap around to first server
    }

    // Add a key and assign it to a server
    public void addKey(String key) {
        String server = getServer(key);
        serverData.computeIfAbsent(server, k -> new ArrayList<>()).add(key);
        serverData.get(server).add(key);
        System.out.println("Key: " + key + " assigned to Server: " + server);
    }

    // Redistribute all keys after adding/removing a server
    private void redistributeKeys() {
        List<String> allKeys = new ArrayList<>();
        // Collect all the keys from each server
        for (var entry : serverData.entrySet()) {
            allKeys.addAll(entry.getValue());
        }

        // Clear the current key distribution
        for (var entry : serverData.entrySet()) {
            entry.getValue().clear();
        }

        // Reassign all keys to the nearest server
        for (String key : allKeys) {
            addKey(key);
        }
    }

    // Print the current key distribution
    public void printServerData() {
        System.out.println("\nCurrent Key Distribution:");
        for (var entry : serverData.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            AddDltServer ch = new AddDltServer();

            // Initial server setup
            System.out.print("Enter server names (comma-separated): ");
            String[] servers = scanner.nextLine().split(",");
            for (String server : servers) {
                ch.addServer(server.trim());
            }

            // Initial keys setup
            System.out.print("Enter keys (comma-separated): ");
            String[] keys = scanner.nextLine().split(",");
            for (String key : keys) {
                ch.addKey(key.trim());
            }

            // Print the initial key distribution
            ch.printServerData();

            // Main loop for adding/removing servers and keys
            boolean running = true;
            while (running) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Add a server");
                System.out.println("2. Remove a server");
                System.out.println("3. Add a key");
                System.out.println("4. Print key distribution");
                System.out.println("Press ESC (27) to exit");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1": // Add a server
                        System.out.print("Enter server name to add: ");
                        String newServer = scanner.nextLine().trim();
                        ch.addServer(newServer);
                        break;
                    case "2": // Remove a server
                        System.out.print("Enter server name to remove: ");
                        String serverToRemove = scanner.nextLine().trim();
                        ch.removeServer(serverToRemove);
                        break;
                    case "3": // Add a key
                        System.out.print("Enter key to add: ");
                        String newKey = scanner.nextLine().trim();
                        ch.addKey(newKey);
                        break;
                    case "4": // Print current key distribution
                        ch.printServerData();
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                }

                // Print the updated distribution chart after every change
                ch.printServerData();

                // Exit the program if ESC key is pressed
                System.out.println("\nPress ESC to exit, or any other key to continue...");
                String exitOption = scanner.nextLine();
                if (exitOption.equalsIgnoreCase("ESC")) {
                    running = false;
                }
            }

        }
    }
}
