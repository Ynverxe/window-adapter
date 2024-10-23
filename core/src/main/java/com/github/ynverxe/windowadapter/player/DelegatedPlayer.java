package com.github.ynverxe.windowadapter.player;

import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.chat.ChatType.Bound;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.chat.SignedMessage.Signature;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public class DelegatedPlayer extends Player {

  private final @NotNull Audience bukkitPlayer;

  public DelegatedPlayer(@NotNull org.bukkit.entity.Player player,
      @NotNull PlayerConnectionBridge playerConnection) {
    super(player.getUniqueId(), player.getName(), playerConnection);
    this.bukkitPlayer = Objects.requireNonNull(player, "player");
  }

  @Override
  public void sendMessage(@NotNull Component message, @NotNull Bound boundChatType) {
    bukkitPlayer.sendMessage(message, boundChatType);
  }

  @Override
  public void sendMessage(@NotNull SignedMessage signedMessage, @NotNull Bound boundChatType) {
    bukkitPlayer.sendMessage(signedMessage, boundChatType);
  }

  @Override
  public void deleteMessage(@NotNull Signature signature) {
    bukkitPlayer.deleteMessage(signature);
  }

  @Override
  public void sendActionBar(@NotNull Component message) {
    bukkitPlayer.sendActionBar(message);
  }

  @Override
  public void sendPlayerListHeader(@NotNull Component header) {
    bukkitPlayer.sendPlayerListHeader(header);
  }

  @Override
  public void sendPlayerListFooter(@NotNull Component footer) {
    bukkitPlayer.sendPlayerListFooter(footer);
  }

  @Override
  public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
    bukkitPlayer.sendPlayerListHeaderAndFooter(header, footer);
  }

  @Override
  public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
    bukkitPlayer.sendTitlePart(part, value);
  }

  @Override
  public void clearTitle() {
    bukkitPlayer.clearTitle();
  }

  @Override
  public void resetTitle() {
    bukkitPlayer.resetTitle();
  }

  @Override
  public void showBossBar(@NotNull BossBar bar) {
    bukkitPlayer.showBossBar(bar);
  }

  @Override
  public void hideBossBar(@NotNull BossBar bar) {
    bukkitPlayer.hideBossBar(bar);
  }

  @Override
  public void playSound(@NotNull Sound sound) {
    bukkitPlayer.playSound(sound);
  }

  @Override
  public void playSound(@NotNull Sound sound, double x, double y, double z) {
    bukkitPlayer.playSound(sound, x, y, z);
  }

  @Override
  public void playSound(@NotNull Sound sound, @NotNull Emitter emitter) {
    bukkitPlayer.playSound(sound, emitter);
  }

  @Override
  public void stopSound(@NotNull SoundStop stop) {
    bukkitPlayer.stopSound(stop);
  }

  @Override
  public void openBook(@NotNull Book book) {
    bukkitPlayer.openBook(book);
  }

  @Override
  public void sendResourcePacks(@NotNull ResourcePackRequest request) {
    bukkitPlayer.sendResourcePacks(request);
  }

  @Override
  public void removeResourcePacks(@NotNull Iterable<UUID> ids) {
    bukkitPlayer.removeResourcePacks(ids);
  }

  @Override
  public void removeResourcePacks(@NotNull UUID id, @NotNull UUID @NotNull ... others) {
    bukkitPlayer.removeResourcePacks(id, others);
  }

  @Override
  public void clearResourcePacks() {
    bukkitPlayer.clearResourcePacks();
  }
}