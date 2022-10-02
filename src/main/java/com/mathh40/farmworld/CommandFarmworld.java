package com.mathh40.farmworld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
public class CommandFarmworld implements CommandExecutor {

    private Farmworld plugin;

    CommandFarmworld(Farmworld plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0)
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                player.teleport(plugin.getMVWorldManager().getMVWorld("farmworld").getSpawnLocation());
            }
            else
            {
                plugin.getServer().getLogger().warning("This command cannot use on the Console");
            }
        } else if (args.length == 1) {
            switch (args[0])
            {
                case "regen":
                    if(sender instanceof Player)
                    {
                        Player player = (Player) sender;
                        if(player.hasPermission("farmworld.regen"))
                        {
                            plugin.regenFarmworld();
                        }
                    }
                    else
                    {
                        plugin.regenFarmworld();
                    }
                    break;
            }
        }
        return false;
    }
}
