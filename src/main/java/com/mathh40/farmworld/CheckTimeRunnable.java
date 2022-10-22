package com.mathh40.farmworld;

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
    LocalDateTime now = LocalDateTime.now();
    for (FarmworldInstances instances : Farmworld.farmworlds.values()) {
      String time = instances.getTime();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime last = LocalDateTime.parse(time, formatter);
      Duration duration = Duration.between(now, last);
      if (duration.toDays() == instances.getRegTime()) {
        instances.regenFarmworld();
      }
    }
  }
}
