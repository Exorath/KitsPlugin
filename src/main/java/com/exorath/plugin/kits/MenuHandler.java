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

import com.exorath.commons.AntiSpam;
import com.exorath.commons.ItemStackSerialize;
import com.exorath.exomenus.InventoryMenu;
import com.exorath.exomenus.MenuItem;
import com.exorath.exomenus.Size;
import com.exorath.service.kit.api.KitServiceAPI;
import com.exorath.service.kit.res.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by toonsev on 5/23/2017.
 */
public class MenuHandler implements Listener {
    private KitServiceAPI kitServiceAPI;
    private String kitPackageId;
    private KitPackage kitPackage;

    public MenuHandler(KitServiceAPI kitServiceAPI, String kitPackageId) {
        this.kitServiceAPI = kitServiceAPI;
        this.kitPackageId = kitPackageId;
        kitPackage = kitServiceAPI.getPackage(kitPackageId);
    }

    public void openInventory(Player player) {
        if (kitPackage == null || kitPackage.getKits() == null || kitPackage.getKits().isEmpty()) {
            player.sendMessage(ChatColor.RED + "Error: No kits yet, come back in a couple of days.");
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            InventoryMenu menu = new InventoryMenu("Kits", Size.getSize(kitPackage.getKits().size()), getMenuItems(player), null);

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> menu.open(player));
        });
    }

    private MenuItem[] getMenuItems(Player player) {
        ArrayList<MenuItem> menuItems = new ArrayList<>(kitPackage.getKits().size());
        GetPlayerKitsResponse playerKitsResponse = kitServiceAPI.getKits(kitPackageId, player.getUniqueId().toString());
        for (Map.Entry<String, Kit> kitEntry : kitPackage.getKits().entrySet()) {
            boolean hasKit = playerKitsResponse.getKits().contains(kitEntry.getKey());
            ItemStack itemStack = ItemStackSerialize.toItemStack(kitEntry.getValue().getItemStack());
            String displayName = hasKit ? ChatColor.GREEN + kitEntry.getValue().getName() + ChatColor.GRAY + " (Owned)" : ChatColor.GREEN + kitEntry.getValue().getName() + ChatColor.GRAY + " (Purchase)";
            List<String> lore = new ArrayList<>();
            if (!hasKit) {
                lore.add("");
                lore.add(ChatColor.GOLD + ChatColor.BOLD.toString() + "Costs: ");
                kitEntry.getValue().getCosts().forEach((currencyKey, currencyVal) -> lore.add(ChatColor.WHITE + currencyKey + ": " + ChatColor.GOLD + currencyVal));
            } else
                lore.add(ChatColor.GREEN + "You own this kits.");
            MenuItem menuItem = new MenuItem(displayName, itemStack, lore.toArray(new String[lore.size()]));
            menuItem.getClickObservable().subscribe(event -> {
                if (hasKit) {
                    select(player, kitEntry.getKey());
                } else
                    purchaseKit(player, kitEntry.getKey(), kitEntry.getValue());
            });
            menuItems.add(menuItem);
        }
        return menuItems.toArray(new MenuItem[menuItems.size()]);
    }

    private void select(Player player, String kitId) {
        if (AntiSpam.isSpamming(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Don't spam");
            return;
        }
        AntiSpam.setSpamming(Main.getInstance(), player.getUniqueId());
        String uuid = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Success success = kitServiceAPI.setCurrentKit(kitPackageId, uuid, kitId);
            if(success.isSuccess())
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(ChatColor.GREEN + "Kit selection saved."));

            else
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(ChatColor.RED + "Error while selecting kit: " + success.getError()));
        });
    }


    private void purchaseKit(Player player, String kitId, Kit kit) {
        String playerId = player.getUniqueId().toString();
        if (AntiSpam.isSpamming(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Don't spam");
            return;
        }
        AntiSpam.setSpamming(Main.getInstance(), player.getUniqueId());
        player.closeInventory();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Success success = kitServiceAPI.purchaseKit(new PurchaseKitReq(kitPackageId, playerId, kitId));
            if (success.isSuccess())
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(ChatColor.GREEN + "Kit " + kit.getName() + " bought."));
            else
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(ChatColor.RED + "Error: " + success.getError() + ChatColor.GRAY + " (Error code: " + success.getCode() + ")"));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        //Remove inventory cache once cache comes along
    }


}
