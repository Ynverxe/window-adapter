package com.github.ynverxe.windowadapter;

import com.github.ynverxe.windowadapter.player.PlayerHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoPlugin extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    MinecraftServer.init();

    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.empty());
    PlayerHelper.openInventory(player, inventory);

    List<Material> materials = new ArrayList<>(Material.values());

    for (int i = 0; i < inventory.getSize(); i++) {
      int random = ThreadLocalRandom.current().nextInt(materials.size() - 1);
      ItemStack itemStack = ItemStack.of(materials.get(random));
      inventory.addItemStack(itemStack);
    }
  }
}