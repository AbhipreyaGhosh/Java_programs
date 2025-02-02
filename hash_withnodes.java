import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class hash_withnodes {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final HashMap<String, List<String>> serverData = new HashMap<>();
    private final int VIRTUAL_NODE_COUNT = 20; // Number of virtual nodes per server

    // MD5 hash function to ensure better distribution
    // private int hash(String key) {
    //     try {
    //         MessageDigest md = MessageDigest.getInstance("MD5");
    //         byte[] hashBytes = md.digest(key.getBytes());
    //         // Use all bytes in the MD5 hash, and combine them into a single integer value
    //         int hashValue = 0;
    //         for (int i = 0; i < 4; i++) { // Use first 4 bytes (32 bits)
    //             hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
    //         }
    //         return Math.abs(hashValue); // Ensure positive value
    //     } catch (NoSuchAlgorithmException e) {
    //         throw new RuntimeException("MD5 hashing algorithm not available", e);
    //     }
    // }

    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(key.getBytes());
            // Use all bytes in the SHA-256 hash, and combine them into a single integer value
            int hashValue = 0;
            for (int i = 0; i < 4; i++) { // Use first 4 bytes (32 bits)
                hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
            }
            return Math.abs(hashValue); // Ensure positive value
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not available", e);
        }
    }
    // Add a server to the ring with virtual nodes
    public void addServer(String server) {
        for (int i = 1; i <= VIRTUAL_NODE_COUNT; i++) {
            String virtualNode = server + i; // Create virtual nodes like A1, A2, A3
            int hash = hash(virtualNode);
            ring.put(hash, server); // Assign virtual node hash to server
            System.out.println("Added virtual node: " + virtualNode);
        }
        serverData.put(server, new ArrayList<>());
        System.out.println("Added server: " + server);
    }

    // Find the nearest server for a key
    private String getServer(String key) {
        int keyHash = hash(key);
        // Find the nearest virtual node clockwise
        Integer hash = ring.ceilingKey(keyHash);
        if (hash == null) {
            hash = ring.firstKey(); // Wrap around to first virtual node
        }
        return ring.get(hash); // Return the corresponding real server for the virtual node
    }

    // Add a key and assign it to a server
    public void addKey(String key) {
        String server = getServer(key);
        serverData.get(server).add(key);
        System.out.println("Key: " + key + " assigned to Server: " + server);
    }

    // Print the current key distribution
    public void printServerData() {
        System.out.println("\nCurrent Key Distribution:");
        for (var entry : serverData.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        hash_withnodes ch = new hash_withnodes();

        // Input servers
        System.out.print("Enter server names (comma-separated): ");
        String inputServers = scanner.nextLine().trim();
        String[] servers = inputServers.split("\\s*,\\s*");

        for (String server : servers) {
            ch.addServer(server);
        }

        // Input keys
        System.out.print("Enter keys (comma-separated): ");
        String inputKeys = scanner.nextLine().trim();
        String[] keys = inputKeys.split("\\s*,\\s*");

        for (String key : keys) {
            ch.addKey(key);
        }

        // Print final key distribution
        ch.printServerData();
        
        scanner.close();
    }
}