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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 5/23/2017.
 */
public class Main extends JavaPlugin {
    private static Main instance;
    private YamlConfigProvider configProvider;
    private MenuHandler menuHandler;
    @Override
    public void onEnable() {
        Main.instance = this;
        this.configProvider = new YamlConfigProvider(getConfig());
        this.menuHandler = new MenuHandler(new KitServiceAPI(getKitServiceAddress()), getKitPackageId());
        Bukkit.getPluginManager().registerEvents(menuHandler, this);
        Bukkit.getPluginManager().registerEvents(new ItemHandler(menuHandler, configProvider), this);
    }


    private String getKitPackageId(){
        String kitPackageId = configProvider.getKitPackageId();
        if (kitPackageId == null)
            Main.terminate("No kitPackageId config.yaml field found.");
        return kitPackageId;
    }
    private String getKitServiceAddress() {
        String address = System.getenv("KIT_SERVICE_ADDRESS");
        if (address == null)
            Main.terminate("No KIT_SERVICE_ADDRESS env found.");
        return address;
    }
    public static void terminate() {
        System.out.println("1v1Plugin is terminating...");
        Bukkit.shutdown();
        System.out.println("Termination failed, force exiting system...");
        System.exit(1);
    }
    public static void terminate(String message) {
        System.out.println(message);
        System.out.println("1v1Plugin is terminating...");
        Bukkit.shutdown();
        System.out.println("Termination failed, force exiting system...");
        System.exit(1);
    }

    public static Main getInstance() {
        return instance;
    }
}
