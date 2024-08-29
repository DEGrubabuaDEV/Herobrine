package de.grubabua.herobrine.commands;

import de.grubabua.herobrine.Herobrine;
import de.grubabua.herobrine.npc.NPCManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


public class StartCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public StartCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Herobrine.targetplayer = (Player) sender;
        Herobrine.spawnlocation = npcspawnLocation(Herobrine.targetplayer);
        NPCManager npcManager = new NPCManager(plugin);
        npcManager.createHerobrineNPC();

        return true;
    }
    private Location npcspawnLocation(Player player) {
        Location playerLocation = player.getLocation();
        Random random = new Random();

        double xOffset = (random.nextDouble() - 0.5) * 2 * 40;
        double zOffset = (random.nextDouble() - 0.5) * 2 * 40;

        Location targetLocation = playerLocation.clone().add(xOffset, 0, zOffset);

        targetLocation.setY(playerLocation.getWorld().getHighestBlockYAt(targetLocation) + 1);
        for (int i = 0; i < 4; i++) {
            new BukkitRunnable() {
                @Override public void run() {
                    player.getWorld().strikeLightning(targetLocation);
                }
            }.runTaskLater(plugin, 20L);
        }
        return targetLocation;
    }
}
