import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//consistent hashing code without virtual servers.

class consistent_hashing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final HashMap<String, List<String>> serverData = new HashMap<>();

    // Hash function (simulating a hash ring)
     private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(key.getBytes());
            // Use all bytes in the MD5 hash, and combine them into a single integer value
            int hashValue = 0;
            for (int i = 0; i < 4; i++) { // Use first 4 bytes (32 bits)
                hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
            }
            return Math.abs(hashValue); // Ensure positive value
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 hashing algorithm not available", e);
        }
    }

    // Add a server to the ring
    public void addServer(String server) {
        int hash = hash(server);
        ring.put(hash, server);
        serverData.put(server, new ArrayList<>());
        System.out.println("Added server: " + server);
    }

    // Remove a server
    public void removeServer(String server) {
        int hash = hash(server);
        ring.remove(hash);
        serverData.remove(server);
        System.out.println("Removed server: " + server);
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
        try (Scanner scanner = new Scanner(System.in)) {
            consistent_hashing ch = new consistent_hashing();

             // Input servers
             System.out.print("Enter server names (comma-separated): ");
             String[] servers = scanner.nextLine().split(",");
             for (String server : servers) {
                 ch.addServer(server.trim());
             }
 
             // Input keys
             System.out.print("Enter keys (comma-separated): ");
             String[] keys = scanner.nextLine().split(",");
             for (String key : keys) {
                 ch.addKey(key.trim());
             }

            // Print current distribution
            ch.printServerData();
            //A, B,C, D, E, F, G, H  
            //1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30
        }
    }
}
