package net.minestom.server.network;

import java.nio.ByteBuffer;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NioBufferExtractor {

  private NioBufferExtractor() {}

  public static ByteBuffer from(NetworkBuffer buffer) {
    return buffer.nioBuffer;
  }
}