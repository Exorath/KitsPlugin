/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.plugin.kits;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by toonsev on 5/9/2017.
 */
public class ItemHandler implements Listener {
    private MenuHandler menuHandler;
    private Integer itemSlot;

    private Set<Player> antiSpam = new HashSet<>();

    public ItemHandler(MenuHandler menuHandler, YamlConfigProvider yamlConfigProvider) {
        this.menuHandler = menuHandler;
       this.itemSlot = yamlConfigProvider.getItemSlot();
    }


    private ItemStack getItem(Player player) {
        ItemStack itemStack = new ItemStack(Material.EMERALD, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Purchase Kits " + ChatColor.GRAY + "(Right Click)");
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (itemSlot == null)
            return;
        if (event.getPlayer().getInventory().getHeldItemSlot() != itemSlot)
            return;
        event.setCancelled(true);
        if (antiSpam.contains(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Wait a couple of seconds...");
            return;
        }
        antiSpam.add(event.getPlayer());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> antiSpam.remove(event.getPlayer()), 60l);
        menuHandler.openInventory(event.getPlayer());
      }
}
