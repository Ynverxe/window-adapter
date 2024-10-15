Use Minestom inventories on Bukkit servers.

## Use in your project
Clone the repository and [add it to your project via gradle](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html).

## Usage
It's very basic.

```java
Inventory inventory = new Inventory(InventoryType.CHEST_1_ROW, Component.empty());
org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer("Ynverxe");
net.minestom.server.entity.Player minestomPlayer = PlayerHelper.adapt(bukkitPlayer);

inventory.addViewer(minestomPlayer);
// or
minestomPlayer.openInventory(inventory);
// or PlayerHelper#openInventory to avoid instantiating net.minestom.server.entity.Player
PlayerHelper.openInventory(bukkitPlayer, inventory);
```