package com.github.ynverxe.windowadapter.player;

import java.util.Objects;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.ynverxe.windowadapter.player.PlayerConnectionCache.*;

/**
 * Used to create a {@link net.minestom.server.entity.Player} instance from a {@link org.bukkit.entity.Player} instance that
 * prepared to handle Minestom's inventory logic.
 * All created instanced are cached in {@link PlayerConnectionCache}.
 */
public final class PlayerHelper {

  private PlayerHelper() {}

  public static void openInventory(@NotNull org.bukkit.entity.Player bukkitPlayer, @NotNull
      Inventory inventory) {
    Player player = adapt(bukkitPlayer);
    player.openInventory(inventory);
  }

  public static @NotNull Player adapt(@NotNull org.bukkit.entity.Player bukkitPlayer) {
    PlayerConnection connection = connectionBridge(bukkitPlayer);
    return Objects.requireNonNull(connection.getPlayer(), "PlayerConnectionBridge isn't initialized");
  }

  public static @NotNull PlayerConnectionBridge connectionBridge(@NotNull org.bukkit.entity.Player bukkitPlayer) {
    synchronized (bukkitPlayer) {
      PlayerConnectionBridge connection = INSTANCE.get(bukkitPlayer);

      if (connection != null) {
        return connection;
      }

      PlayerConnectionBridge playerConnectionBridge = new PlayerConnectionBridge(bukkitPlayer);
      new Player(bukkitPlayer.getUniqueId(), bukkitPlayer.getName(), playerConnectionBridge);
      INSTANCE.bind(bukkitPlayer, playerConnectionBridge);
      return playerConnectionBridge;
    }
  }

  public static @Nullable PlayerConnectionBridge cachedConnection(@NotNull org.bukkit.entity.Player bukkitPlayer) {
    synchronized (bukkitPlayer) {
      return INSTANCE.get(bukkitPlayer);
    }
  }

  public static @NotNull org.bukkit.entity.Player fromMinestomPlayer(@NotNull Player minestomPlayer) {
    PlayerConnectionBridge connection = (PlayerConnectionBridge) minestomPlayer.getPlayerConnection();

    return connection.inventoryNetworkingHandler().bukkitPlayer();
  }
}