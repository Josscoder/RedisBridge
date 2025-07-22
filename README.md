<p align="center">
  <img src="/assets/logo.png" alt="fsmgo logo" width="300"/>
</p>

**RedisBridge** is a complete rewrite of my previous plugin [JBridge](https://github.com/JossArchived/JBridge), developed for Nukkit and WaterdogPE. It provides automatic server registration, player management, and seamless communication between backend servers and the proxy.

# ⚙️ Available Commands
RedisBridge includes ready-to-use commands for your proxy (WaterdogPE, Velocity, BungeeCord) or backend servers:

- `/lobby`
Teleports the player to an available lobby instance using the `LOWEST_PLAYERS` strategy to avoid overloading a single lobby while keeping activity balanced.

- `/transfer <server>`
Transfers the player to a specific instance if available. Useful for networks with multiple game modes.

- `/whereami`
Displays information to the player showing which instance and group they are currently in, along with the number of players and instance capacity.

# 📡 Instance Management Usage
RedisBridge includes a **distributed instance discovery and selection system** for minigame servers, lobbies, or backend servers using Redis and a low-latency distributed cache.

Each server instance sends **automatic heartbeats** via `InstanceHeartbeatMessage` containing:

- `id` (unique identifier)

- `group` (e.g., `lobby`, `solo_skywars`, `duels`)

- `players` (current online players)

- `maxPlayers` (maximum capacity)

- `host` and `port`

This allows other servers and the proxy to know in real time which instances are available, their capacity, and their status.

The `InstanceManager`:

- Uses a local cache with a 10-second expiration to keep instance state updated efficiently.

- Allows you to:

  - Retrieve instances by ID (`getInstanceById`)

  - Retrieve all instances in a group (`getGroupInstances`)

  - Get total player counts or per group (`getTotalPlayerCount`, `getGroupPlayerCount`)

  - Get total maximum player capacity or per group (`getTotalMaxPlayers`, `getGroupMaxPlayers`)

Provides **automatic available instance selection** using different strategies:

- `RANDOM`: Selects a random instance in the group.

- `LOWEST_PLAYERS`: Selects the instance with the fewest players.

- `MOST_PLAYERS_AVAILABLE`: Selects the instance with the most players.

Example:

```java
InstanceInfo instance = InstanceManager.getInstance().selectAvailableInstance("lobby", InstanceManager.SelectionStrategy.LOWEST_PLAYERS);

if (instance != null) {
    // Connect player to this instance
}
```

This system enables your network to distribute players dynamically without relying on a heavy centralized matchmaking server.

# 🚀 Communication Usage
RedisBridge simplifies inter-server communication over Redis, enabling you to publish and subscribe to messages seamlessly between your proxy and backend servers.

## How it works?
- Uses Redis Pub/Sub on a single channel (redis-bridge-channel) for all message transmission.

- Messages are serialized in JSON and identified using their type field.

- Each message type can have:

    - A registered class (MessageRegistry) for deserialization.

    - An optional handler (MessageHandlerRegistry) for automatic processing when received.

- Includes default messages for instance heartbeat and shutdown announcements to enable automatic instance tracking.

## Publishing Messages
To send a message to all connected instances:
```java
YourCustomMessage message = new YourCustomMessage();
// fill your message data here

redisBridge.publish(message, "sender-id");
```

## Handling Incoming Messages
- Register your message type:
    ```java
    MessageRegistry.register("your-message-type", YourCustomMessage.class);
    ```
- Register your message handler:
    ```java
    MessageHandlerRegistry.register("your-message-type", new MessageHandler<YourCustomMessage>() {
        @Override
        public void handle(YourCustomMessage message) {
            // handle your message here
        }
    });
    ```

## License
**RedisBridge** is licensed under the [MIT License](./LICENSE). Feel free to use, modify, and distribute it in your projects.