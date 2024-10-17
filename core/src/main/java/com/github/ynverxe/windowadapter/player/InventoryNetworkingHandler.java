package com.github.ynverxe.windowadapter.player;

import com.github.ynverxe.windowadapter.nms.common.NMSModule;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public class InventoryNetworkingHandler {

  /**
   * This value can be outdated sometimes. It is only updated when calling {@link #onMinestomInventoryOpen()}
   */
  private int bukkitContainerId;
  private final @NotNull PlayerConnectionBridge playerConnectionBridge;
  private final @NotNull org.bukkit.entity.Player bukkitPlayer;
  public boolean sendClosePacket = true;

  public InventoryNetworkingHandler(@NotNull PlayerConnectionBridge playerConnectionBridge,
      @NotNull Player bukkitPlayer) {
    this.playerConnectionBridge = Objects.requireNonNull(playerConnectionBridge, "minestomPlayer");
    this.bukkitPlayer = Objects.requireNonNull(bukkitPlayer, "bukkitPlayer");
  }

  public @NotNull Player bukkitPlayer() {
    return this.bukkitPlayer;
  }

  public @NotNull net.minestom.server.entity.Player minestomPlayer() {
    return Objects.requireNonNull(this.playerConnectionBridge.getPlayer());
  }

  @Internal
  public int bukkitContainerId() {
    return this.bukkitContainerId;
  }

  public void onMinestomInventoryOpen() {
    if (bukkitPlayer.getOpenInventory().getType() != InventoryType.CRAFTING) {
      this.bukkitPlayer.closeInventory();
    }

    this.bukkitContainerId = NMSModule.instance().incrementContainerId(bukkitPlayer);
  }

  public boolean hasMinestomInventoryOpen() {
    return this.minestomPlayer().getOpenInventory() != null;
  }

  public boolean hasBukkitInventoryOpen() {
    return this.bukkitPlayer.getOpenInventory().getType() != InventoryType.CRAFTING;
  }

  public void closeInventorySilently() {
    this.sendClosePacket = false;
    this.minestomPlayer().closeInventory();
  }
}