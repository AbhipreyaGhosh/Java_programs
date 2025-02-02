Consistent Hashing with Server Addition/Deletion
This project demonstrates a basic implementation of consistent hashing with server addition and server removal. It allows dynamic updates to the hash ring by adding or removing servers and ensures that keys are consistently distributed across available servers.

Features:
Consistent Hashing: Distributes keys across servers with minimal movement of existing keys when servers are added or removed.
Server Addition and Removal: Allows adding and removing servers from the system and ensures key redistribution when the topology changes.
Key Assignment: Keys are dynamically assigned to the nearest server in the hash ring using the SHA-256 hash function.
Real-time Updates: After each addition or removal of servers or keys, the current key distribution is printed out to reflect changes.
