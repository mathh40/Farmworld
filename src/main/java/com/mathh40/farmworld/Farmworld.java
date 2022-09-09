package com.mathh40.farmworld;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public final class Farmworld extends JavaPlugin {

    static MultiverseCore core;
    private File createtimefile;
    private FileConfiguration createtimefileconfig;

    private CheckTimeRunnable checktime;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        createCustomConfig();
        // Plugin startup logic
        core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        MVWorldManager worldManager = core.getMVWorldManager();
        MultiverseWorld farmworldWorld = worldManager.getMVWorld("farmworld");
        if(farmworldWorld == null) {
            worldManager.addWorld(
                    "farmworld", // The worldname
                    World.Environment.NORMAL, // The overworld environment type.
                    null, // The world seed. Any seed is fine for me, so we just pass null.
                    WorldType.LARGE_BIOMES, // Nothing special. If you want something like a flat world, change this.
                    false, // This means we want to structures like villages to generator, Change to false if you don't want this.
                    null // Specifies a custom generator. We are not using any so we just pass null.
            );
            farmworldWorld = worldManager.getMVWorld("farmworld");
            LocalDateTime now = LocalDateTime .now();
            createtimefileconfig.set("time",now.toString());
        }
        checktime = new CheckTimeRunnable(this);
        checktime.runTaskAsynchronously(this);

        farmworldWorld.allowPortalMaking(AllowedPortalType.NONE);
        farmworldWorld.setBedRespawn(false);
        farmworldWorld.setGameMode(GameMode.SURVIVAL);
        farmworldWorld.setSpawnLocation(new Location(farmworldWorld.getCBWorld(),0,65,0));


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MVWorldManager getMVWorldManager()
    {
        return core.getMVWorldManager();
    }

    private void createCustomConfig() {
        createtimefile = new File(getDataFolder(), "farmworldcreate.yml");
        if (!createtimefile.exists()) {
            createtimefile.getParentFile().mkdirs();
            saveResource("farmworldcreate.yml", false);
        }

        createtimefileconfig = new YamlConfiguration();
        try {
            createtimefileconfig.load(createtimefile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        /* User Edit:
            Instead of the above Try/Catch, you can also use
            YamlConfiguration.loadConfiguration(customConfigFile)
        */
    }

    public FileConfiguration getcreatetimefileconfig()
        {
        return this.createtimefileconfig;
    }
}
