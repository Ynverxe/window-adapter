package com.github.ynverxe.windowadapter.listener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ListenerRegistration {

  private static ListenerRegistration INSTANCE;

  private final PlayerQuitListener playerQuitListener = new PlayerQuitListener();

  private ListenerRegistration() {
    Plugin plugin = plugin();

    PluginManager pluginManager = Bukkit.getPluginManager();
    pluginManager.registerEvents(playerQuitListener, plugin);
  }

  private static Plugin plugin() {
    return JavaPlugin.getProvidingPlugin(ListenerRegistration.class);
  }

  public static void ensureListenersAreRegistered() {
    if (INSTANCE == null) {
      synchronized (ListenerRegistration.class) {
        if (INSTANCE != null) return;

        INSTANCE = new ListenerRegistration();
      }
    }
  }
}