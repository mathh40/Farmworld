package com.mathh40.farmworld;

import com.onarandombox.MultiverseCore.MultiverseCore;
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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;


public final class Farmworld extends JavaPlugin {

    static MultiverseCore core;
    private FileConfiguration createtimefileconfig;
    private Clipboard clipboard;
    private final File createtimefile = new File(getDataFolder(), "farmworldcreate.yml");

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        createCustomConfig();
        this.getCommand("farmworld").setExecutor(new CommandFarmworld(this));

        // Plugin startup logic
        String path = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator + getConfig().getString("spawn-schematic");
        Bukkit.getServer().getLogger().log(Level.INFO,"Schematics Path " + path );
        File schematicfile = new File(path);
        ClipboardFormat format = ClipboardFormats.findByFile(schematicfile);
        try {
            assert format != null;
            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicfile))) {
                clipboard = reader.read();
            }
        } catch (IOException e) {
            Bukkit.getServer().getLogger().log(Level.WARNING,"Can not Load " + path );
        }

        core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        assert core != null;
        MVWorldManager worldManager = core.getMVWorldManager();
        if(worldManager != null) {
            MultiverseWorld farmworldWorld = worldManager.getMVWorld("farmworld");
            if (farmworldWorld == null) {
                worldManager.addWorld(
                        "farmworld", // The worldname
                        World.Environment.NORMAL, // The overworld environment type.
                        null, // The world seed. Any seed is fine for me, so we just pass null.
                        WorldType.LARGE_BIOMES, // Nothing special. If you want something like a flat world, change this.
                        false, // This means we want to structures like villages to generator, Change to false if you don't want this.
                        null // Specifies a custom generator. We are not using any so we just pass null.
                );
                farmworldWorld = worldManager.getMVWorld("farmworld");
                World world = farmworldWorld.getCBWorld();
                placeSchematics(clipboard,new Location(world, 0, world.getHighestBlockAt(0, 0).getY(), 0));
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                createtimefileconfig.set("time", now.format(formatter));
                try {
                    createtimefileconfig.save(createtimefile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            CheckTimeRunnable checktime = new CheckTimeRunnable(this);
            checktime.runTaskAsynchronously(this);

            farmworldWorld.allowPortalMaking(AllowedPortalType.NONE);
            farmworldWorld.setBedRespawn(false);
            farmworldWorld.setGameMode(GameMode.SURVIVAL);
            World world = farmworldWorld.getCBWorld();
            farmworldWorld.setSpawnLocation(new Location(world, 0, world.getHighestBlockAt(0, 0).getY() + 1, 0));

        }
        else
        {
            Bukkit.getServer().getLogger().log(Level.WARNING,"Can not Load Multiverse-Core");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void placeSchematics(Clipboard clipboard , Location loc)
    {//Pasting Operation
// We need to adapt our world into a format that worldedit accepts. This looks like this:
// Ensure it is using com.sk89q... otherwise we'll just be adapting a world into the same world.

            EditSession  editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(loc.getWorld()));

// Saves our operation and builds the paste - ready to be completed.
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(false).build();

            try { // This simply completes our paste and then cleans up.
                Operations.complete(operation);
                editSession.close();

            } catch (WorldEditException e) { // If worldedit generated an exception it will go here
                getServer().getLogger().warning(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                e.printStackTrace();
            }
    }

    public MVWorldManager getMVWorldManager()
    {
        return core.getMVWorldManager();
    }

    private void createCustomConfig() {

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
    }

    public FileConfiguration getcreatetimefileconfig()
        {
        return this.createtimefileconfig;
    }

    public void regenFarmworld()
    {
        getServer().broadcastMessage("Farmwelt wird neu erstellt .....");
        getMVWorldManager().regenWorld("farmworld",true,true,"",true);
        MultiverseWorld farmworldWorld = getMVWorldManager().getMVWorld("farmworld");
        World world = farmworldWorld.getCBWorld();
        placeSchematics(getClipboard(),new Location(world, 0, world.getHighestBlockAt(0, 0).getY() - 1, 0));
        getServer().broadcastMessage("Farmwelt wurde erstellt");
    }

    public Clipboard getClipboard() {
        return clipboard;
    }
}
