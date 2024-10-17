package com.github.ynverxe.windowadapter.player;

import com.github.ynverxe.windowadapter.listener.ListenerRegistration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cache used to hold instances of {@link PlayerConnectionBridge} weakly.
 */
@Internal
public final class PlayerConnectionCache {

  private PlayerConnectionCache() {}

  public static final PlayerConnectionCache INSTANCE = new PlayerConnectionCache();

  private final Map<UUID, PlayerConnectionBridge> cache = new ConcurrentHashMap<>();

  public void bind(@NotNull Player player, @Nullable PlayerConnectionBridge connection) {
    ListenerRegistration.ensureListenersAreRegistered();

    UUID playerId = player.getUniqueId();

    if (connection == null) {
      this.cache.remove(playerId);
      return;
    }

    this.cache.put(playerId, connection);
  }

  public @Nullable PlayerConnectionBridge get(@NotNull Player player) {
    return cache.get(player.getUniqueId());
  }
}