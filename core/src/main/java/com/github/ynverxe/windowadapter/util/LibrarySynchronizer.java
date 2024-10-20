package com.github.ynverxe.windowadapter.util;

import com.github.ynverxe.windowadapter.player.PlayerConnectionBridge;
import com.github.ynverxe.windowadapter.player.PlayerHelper;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to represent a window-adapter instance.
 * If two shadowed libraries are present in the same classpath, that
 * may cause inconsistency when they are trying to handle inventory logic.
 */
// TODO this was not tested yet
public final class LibrarySynchronizer {

  private static final String REVOKER_LISTENER_KEY = "RevokerListener";

  @SuppressWarnings("ALL")
  public static final LibrarySynchronizer INSTANCE = new LibrarySynchronizer();
  private static final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(LibrarySynchronizer.class);

  private LibrarySynchronizer() {}

  /**
   * Before send an open container packet, this method searches for a closer stored by another instance into the player's metadata
   * and call it if is found, after that stores this instance closer.
   *
   * @param bukkitPlayer the player to own
   */
  public void ownPlayer(@NotNull Player bukkitPlayer) {
    MetadataValue instanceRevokerListenerValue = bukkitPlayer.getMetadata(REVOKER_LISTENER_KEY)
        .stream()
        .findFirst()
        .orElse(null);

    if (instanceRevokerListenerValue != null) {
      bukkitPlayer.removeMetadata(REVOKER_LISTENER_KEY, instanceRevokerListenerValue.getOwningPlugin());
      ((Runnable) instanceRevokerListenerValue.value()).run();
    }

    bukkitPlayer.setMetadata(REVOKER_LISTENER_KEY, new FixedMetadataValue(PLUGIN,
        (Runnable) () -> onInstanceRemoval(bukkitPlayer)));
  }

  public void removeCloser(@NotNull Player bukkitPlayer) {
    MetadataValue instanceRevokerListenerValue = bukkitPlayer.getMetadata(REVOKER_LISTENER_KEY)
        .stream()
        .filter(value -> value.getOwningPlugin() == PLUGIN)
        .findFirst()
        .orElse(null);

    if (instanceRevokerListenerValue != null) {
      bukkitPlayer.removeMetadata(REVOKER_LISTENER_KEY, instanceRevokerListenerValue.getOwningPlugin());
    }
  }

  private void onInstanceRemoval(@NotNull Player player) {
    PlayerConnectionBridge connection = PlayerHelper.cachedConnection(player);

    if (connection == null) {
      return;
    }

    connection.inventoryNetworkingHandler().closeInventorySilently();
  }
}