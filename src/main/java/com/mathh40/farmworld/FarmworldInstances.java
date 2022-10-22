package com.mathh40.farmworld;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.enums.AllowedPortalType;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class FarmworldInstances {
    private final String name;
    private Clipboard clipboard;

    private Farmworld plugin;



    public FarmworldInstances(Farmworld plugin, String name, String type, String schem) {
        this.plugin = plugin;
        this.name = name;
        plugin.getConfig().set(schem,name + ".spawn-schematic");
        String path = plugin.getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + schem + ".schem";
        Bukkit.getServer().getLogger().log(Level.INFO, "Schematics Path " + path);
        File schematicfile = new File(path);
        ClipboardFormat format = ClipboardFormats.findByFile(schematicfile);
        try {
            assert format != null;
            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicfile))) {
                clipboard = reader.read();
            }
        } catch (IOException e) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Can not Load " + path);
        }

        MVWorldManager worldManager = plugin.getMVWorldManager();
        if (worldManager != null) {
            MultiverseWorld farmworldWorld = worldManager.getMVWorld(name);
            World.Environment env = World.Environment.valueOf(type.toUpperCase());
            if (farmworldWorld == null) {
                worldManager.addWorld(
                        name, // The worldname
                        env, // The overworld environment type.
                        null, // The world seed. Any seed is fine for me, so we just pass null.
                        WorldType.LARGE_BIOMES, // Nothing special. If you want something like a flat world, change this.
                        false, // This means we want to structures like villages to generator, Change to false if you don't want this.
                        null // Specifies a custom generator. We are not using any so we just pass null.
                );
                farmworldWorld = worldManager.getMVWorld(name);
                World world = farmworldWorld.getCBWorld();
                Location loc;
                if(env == World.Environment.NETHER)
                {
                    loc = new Location(world, 0, 50 , 0);
                }
                else {
                    loc = new Location(world, 0, world.getHighestBlockAt(0, 0).getY() + 40 , 0);
                }

                plugin.getConfig().set(name + ".spawnHeight",loc.getY());
                placeSchematics(clipboard, loc);
                farmworldWorld.allowPortalMaking(AllowedPortalType.NONE);
                farmworldWorld.setBedRespawn(false);
                farmworldWorld.setGameMode(GameMode.SURVIVAL);
                farmworldWorld.setSpawnLocation(new Location(world, 0, loc.getY() + 1, 0));

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                plugin.getConfig().set(name + ".gentime", now.format(formatter));
                plugin.saveConfig();
            }
            CheckTimeRunnable checktime = new CheckTimeRunnable(plugin);
            checktime.runTaskTimerAsynchronously(plugin, 20 * 60 * 60, 20 * 60 * 60);
        } else {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Can not Load Multiverse-Core");
        }
    }

    public void regenFarmworld()
    {
        plugin.getServer().broadcastMessage("Farmwelt wird neu erstellt .....");
        plugin.getMVWorldManager().regenWorld(name,true,true,"",true);
        MultiverseWorld farmworldWorld = plugin.getMVWorldManager().getMVWorld(name);
        World world = farmworldWorld.getCBWorld();
        Location loc = new Location(world, 0, plugin.getConfig().getInt(name + ".spawnHeight"), 0);
        placeSchematics(getClipboard(),loc);
        farmworldWorld.allowPortalMaking(AllowedPortalType.NONE);
        farmworldWorld.setBedRespawn(false);
        farmworldWorld.setGameMode(GameMode.SURVIVAL);
        farmworldWorld.setSpawnLocation(new Location(world, 0, loc.getY() + 1, 0));
        plugin.getServer().broadcastMessage("Farmwelt wurde erstellt");
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public void placeSchematics(Clipboard clipboard , Location loc)
    {//Pasting Operation
// We need to adapt our world into a format that worldedit accepts. This looks like this:
// Ensure it is using com.sk89q... otherwise we'll just be adapting a world into the same world.

        EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(loc.getWorld()));
// Saves our operation and builds the paste - ready to be completed.
        Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(false).build();

        try { // This simply completes our paste and then cleans up.
            Operations.complete(operation);
            editSession.close();

        } catch (WorldEditException e) { // If worldedit generated an exception it will go here
            plugin.getServer().getLogger().warning(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
            e.printStackTrace();
        }
    }

    public String getTime() {
        return plugin.getConfig().getString(name + ".gentime");
    }

    public long getRegTime() {
        return plugin.getConfig().getInt(name + ".regenTime",1);
    }

    public void removeFarmworld() {
        MVWorldManager worldManager = plugin.getMVWorldManager();
        if (worldManager != null) {
            worldManager.deleteWorld(name);
        }
    }
}
