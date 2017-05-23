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

import com.exorath.service.kit.api.KitServiceAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by toonsev on 5/23/2017.
 */
public class MenuHandler implements Listener{
    private KitServiceAPI kitServiceAPI;
    public MenuHandler(KitServiceAPI kitServiceAPI) {
        this.kitServiceAPI = kitServiceAPI;
    }

    public void openInventory(Player player){

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        //Remove inventory cache once cache comes along
    }
}
