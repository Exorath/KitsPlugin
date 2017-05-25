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

package com.exorath.plugin.kit;

import com.exorath.commons.ItemStackSerialize;
import com.exorath.exomenus.InventoryMenu;
import com.exorath.exomenus.MenuItem;
import com.exorath.exomenus.Size;
import com.exorath.service.kit.api.KitServiceAPI;
import com.exorath.service.kit.res.GetPlayerKitsResponse;
import com.exorath.service.kit.res.Kit;
import com.exorath.service.kit.res.KitPackage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (kitPackage.getKits().isEmpty()) {
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
                lore.add(ChatColor.GREEN + "You own this kit.");
            menuItems.add(new MenuItem(displayName, itemStack, lore.toArray(new String[lore.size()])));
        }
        return menuItems.toArray(new MenuItem[menuItems.size()]);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        //Remove inventory cache once cache comes along
    }


}
