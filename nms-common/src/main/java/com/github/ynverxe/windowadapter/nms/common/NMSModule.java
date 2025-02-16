package com.github.ynverxe.windowadapter.nms.common;

import io.netty.channel.ChannelPipeline;
import net.minestom.server.network.packet.server.ServerPacket.Play;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class NMSModule {

  public abstract @NotNull ChannelPipeline getChannelPipeline(@NotNull Player player);

  public abstract void sendPacket(@NotNull Player player, @NotNull Play playPacket);

  public abstract int incrementContainerId(@NotNull Player player);

  /**
   * This method just accepts client-bound play-packets.
   */
  public abstract int packetId(@NotNull Object packet);

  public static @NotNull NMSModule instance() {
    return NMSModuleLoader.INSTANCE.loadedModule();
  }
}