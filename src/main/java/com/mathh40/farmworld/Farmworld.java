package com.mathh40.farmworld;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Farmworld extends JavaPlugin {

  static MultiverseCore core;

  static HashMap<String, FarmworldInstances> farmworlds = new HashMap<>();

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    this.getCommand("farmworld").setExecutor(new CommandFarmworld(this));
    core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
  }

  @Override
  public void onDisable() {
    saveConfig();
  }

  public MVWorldManager getMVWorldManager() {
    return core.getMVWorldManager();
  }
}
