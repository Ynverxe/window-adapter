package com.github.ynverxe.windowadapter.netty;

import com.github.ynverxe.windowadapter.player.InventoryNetworkingHandler;
import com.github.ynverxe.windowadapter.player.PlayerHelper;
import com.github.ynverxe.windowadapter.protocol.AllowedPackets;
import com.github.ynverxe.windowadapter.util.LibrarySynchronizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.minestom.server.MinecraftServer;
import net.minestom.server.listener.WindowListener;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientCloseWindowPacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.binary.BinaryBuffer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ALL")
public class CustomInboundAdapter extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger("CustomInboundAdapter");
  private static final PacketListenerManager PACKET_LISTENER_MANAGER = new PacketListenerManager();
  private static final PacketProcessor PACKET_PROCESSOR = MinecraftServer.getPacketProcessor();

  {
    PACKET_LISTENER_MANAGER.setPlayListener(ClientCloseWindowPacket.class, (clientCloseWindowPacket, player) -> {
      try {
        WindowListener.closeWindowListener(clientCloseWindowPacket, player);
      } finally {
        Player bukkitPlayer = PlayerHelper.fromMinestomPlayer(player);
        LibrarySynchronizer.INSTANCE.removeCloser(bukkitPlayer);
      }
    });
  }

  private final @NotNull ChannelInboundHandlerAdapter vanillaDelegate;
  private @Nullable BinaryBuffer incompletePacket;
  private final @NotNull InventoryNetworkingHandler inventoryNetworkingHandler;
  private final @NotNull PlayerConnection playerConnection;

  public CustomInboundAdapter(@NotNull ChannelInboundHandlerAdapter vanillaDelegate, @NotNull InventoryNetworkingHandler inventoryNetworkingHandler) {
    this.vanillaDelegate = Objects.requireNonNull(vanillaDelegate, "vanillaDelegate");
    this.inventoryNetworkingHandler = Objects.requireNonNull(inventoryNetworkingHandler, "inventoryNetworkingHandler");
    this.playerConnection = inventoryNetworkingHandler.minestomPlayer().getPlayerConnection();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (!this.inventoryNetworkingHandler.hasMinestomInventoryOpen()) {
      //System.out.println("Received packet, but menu isn't open ");
      vanillaDelegate.channelRead(ctx, msg);
      return;
    }

    if (!(msg instanceof ByteBuf byteBuf)) {
      //System.out.println("Received packet, but msg isn't a ByteBuf ");
      vanillaDelegate.channelRead(ctx, msg);
      return;
    }

    //System.out.println("Received packet");

    try {
      ByteBuffer nioBuffer = byteBuf.nioBuffer();

      NetworkBuffer buffer = new NetworkBuffer(nioBuffer, false);
      int packetId = buffer.read(buffer.VAR_INT);

      int bodyLength = buffer.readableBytes();

      // update read index
      nioBuffer.position(buffer.readIndex());

      //System.out.println("Processing packet: " + packetId);

      try {
        if (!AllowedPackets.CLIENT_TO_SERVER.containsKey(packetId)) {
          //System.out.println("Packet delegated to vanilla");

          vanillaDelegate.channelRead(ctx, msg);
          return;
        }

        ClientPacket clientPacket = PACKET_PROCESSOR.create(ConnectionState.PLAY, packetId, nioBuffer);
        PACKET_LISTENER_MANAGER.processClientPacket(clientPacket, playerConnection);
      } catch (Exception e) {
        LOGGER.error("Unexpected error when processing packet(id={})", packetId, e);
      }
    } catch (Exception e) {
      LOGGER.error("Unexpected error", e);
    }
  }
}