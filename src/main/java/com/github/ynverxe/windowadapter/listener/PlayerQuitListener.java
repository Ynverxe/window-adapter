package com.github.ynverxe.windowadapter.listener;

import com.github.ynverxe.windowadapter.player.PlayerConnectionCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    PlayerConnectionCache.INSTANCE.bind(event.getPlayer(), null);
  }
}