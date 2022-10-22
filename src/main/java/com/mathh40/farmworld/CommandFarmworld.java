package com.mathh40.farmworld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CommandFarmworld implements CommandExecutor {

    private Farmworld plugin;

    CommandFarmworld(Farmworld plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1)
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                player.teleport(plugin.getMVWorldManager().getMVWorld(args[0]).getSpawnLocation());
            }
            else
            {
                plugin.getServer().getLogger().warning("This command cannot use on the Console");
            }
        } else if (args.length > 1) {
            String name = args[1];
            switch (args[0])
            {
                case "regen":
                    if(sender instanceof Player)
                    {
                        Player player = (Player) sender;
                        if(player.hasPermission("farmworld.regen"))
                        {
                            FarmworldInstances instances = Farmworld.farmworlds.get(name);
                            instances.regenFarmworld();
                        }
                    }
                    else
                    {
                        FarmworldInstances instances = Farmworld.farmworlds.get(name);
                        instances.regenFarmworld();
                    }
                    break;
                case "create":
                    if (args.length > 3) {

                        FarmworldInstances instances = new FarmworldInstances(plugin, name, args[2],args[3]);
                        Farmworld.farmworlds.put(name, instances);
                    }
                    break;
                case "remove":
                    Farmworld.farmworlds.get(name).removeFarmworld();
                    Farmworld.farmworlds.remove(name);
                    break;
            }
        }
        return false;
    }
}
