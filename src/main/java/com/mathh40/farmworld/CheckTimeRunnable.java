package com.mathh40.farmworld;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckTimeRunnable extends BukkitRunnable {
    private final Farmworld plugin;

    public CheckTimeRunnable(Farmworld plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LocalDateTime  now = LocalDateTime.now();
        String time = plugin.getcreatetimefileconfig().getString("time");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime last = LocalDateTime.parse(time, formatter);
        Duration duration = Duration.between(now, last);
        if(duration.toDays() == 1)
        {
            plugin.getServer().broadcastMessage("Farmwelt wird neu erstellt .....");
            plugin.getMVWorldManager().regenWorld("farmworld",true,true,"",true);
            MultiverseWorld farmworldWorld = plugin.getMVWorldManager().getMVWorld("farmworld");
            World world = farmworldWorld.getCBWorld();
            plugin.placeSchematics(plugin.getClipboard(),new Location(world, 0, world.getHighestBlockAt(0, 0).getY() - 1, 0));
            plugin.getServer().broadcastMessage("Farmwelt wurde erstellt");
        }

    }
}
