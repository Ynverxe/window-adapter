package com.github.ynverxe.windowadapter.player;

import com.github.ynverxe.windowadapter.netty.CustomInboundAdapter;
import com.github.ynverxe.windowadapter.netty.CustomPacketEncoder;
import com.github.ynverxe.windowadapter.protocol.AllowedPackets;
import com.github.ynverxe.windowadapter.nms.common.NMSModule;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.net.SocketAddress;
import java.util.Objects;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.ServerPacket.Play;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerConnectionBridge extends PlayerConnection {

  private final InventoryNetworkingHandler inventoryNetworkingHandler;

  public PlayerConnectionBridge(@NotNull Player bukkitPlayer) {
    this.inventoryNetworkingHandler = new InventoryNetworkingHandler(this, bukkitPlayer);
    addChannelHandlersIfAbsent();
  }

  @Override
  public void sendPacket(@NotNull SendablePacket sendablePacket) {
    sendablePacket = SendablePacket.extractServerPacket(ConnectionState.PLAY, sendablePacket);

    if (!AllowedPackets.SERVER_TO_CLIENT.containsValue(sendablePacket.getClass())) {
      //throw new IllegalArgumentException("Packet '" + sendablePacket.getClass() + "' cannot be sent");
      return;
    }

    if (!(sendablePacket instanceof Play playPacket)) {
      return;
    }

    byte containerId = (byte) this.inventoryNetworkingHandler.bukkitContainerId();
    if (playPacket instanceof OpenWindowPacket openWindowPacket) {
      this.inventoryNetworkingHandler.onMinestomInventoryOpen();

      playPacket = new OpenWindowPacket(
          containerId,
          openWindowPacket.windowType(),
          openWindowPacket.title()
      );
    }

    if (playPacket instanceof WindowItemsPacket windowItemsPacket) {
      playPacket = new WindowItemsPacket(
          containerId,
          windowItemsPacket.stateId(),
          windowItemsPacket.items(),
          windowItemsPacket.carriedItem()
      );
    }

    if (playPacket instanceof SetSlotPacket setSlotPacket) {
      int windowId = setSlotPacket.windowId();

      // avoid Network Protocol Error screen message
      // https://wiki.vg/Protocol#Set_Container_Slot
      switch (windowId) {
        case 0:
        case -1:
        case -2:
          break; // do nothing
        default:
          playPacket = new SetSlotPacket(
              containerId,
              setSlotPacket.stateId(),
              setSlotPacket.slot(),
              setSlotPacket.itemStack()
          );
          break;
      }
    }

    NMSModule.instance().sendPacket(inventoryNetworkingHandler.bukkitPlayer(), playPacket);
  }

  @Override
  public @NotNull SocketAddress getRemoteAddress() {
    return Objects.requireNonNull(this.inventoryNetworkingHandler.bukkitPlayer().getAddress());
  }

  @Override
  public @NotNull ConnectionState getConnectionState() {
    return ConnectionState.PLAY;
  }

  private void addChannelHandlersIfAbsent() {
    Player bukkitPlayer = this.inventoryNetworkingHandler.bukkitPlayer();
    ChannelPipeline channelPipeline = NMSModule.instance().getChannelPipeline(bukkitPlayer);

    MessageToByteEncoder<?> vanillaPacketEncoder = (MessageToByteEncoder<?>) channelPipeline.get("encoder");
    channelPipeline.replace("encoder", "encoder", new CustomPacketEncoder(vanillaPacketEncoder, this.inventoryNetworkingHandler));

    ByteToMessageDecoder vanillaPacketDecoder = (ByteToMessageDecoder) channelPipeline.get("decoder");
    channelPipeline.replace("decoder", "decoder", new CustomInboundAdapter(vanillaPacketDecoder, this.inventoryNetworkingHandler));
  }
}