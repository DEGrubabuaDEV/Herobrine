package de.grubabua.herobrine.start;

import de.grubabua.herobrine.Herobrine;
import de.grubabua.herobrine.npc.HerobrineNPC;
import de.grubabua.herobrine.npc.NPCManager;
import de.grubabua.herobrine.phase.PhaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StartEvents implements Listener {
    private static boolean startedgame;
    private static boolean zeroplayer;
    private final PhaseManager phaseManager;
    private final HerobrineNPC herobrineNPC;
    private final JavaPlugin plugin;

    public StartEvents(PhaseManager phaseManager, HerobrineNPC herobrineNPC, JavaPlugin plugin) {
        this.phaseManager = phaseManager;
        this.herobrineNPC = herobrineNPC;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (startedgame && zeroplayer) {
            zeroplayer = false;
            phaseManager.start();
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                startedgame = true;
                Herobrine.targetplayer = event.getPlayer();
                Herobrine.spawnlocation = npcspawnLocation(player);
                NPCManager npcManager = new NPCManager(plugin);
                npcManager.createHerobrineNPC();
            }
        }.runTaskLater(plugin,2 * 60  * 20L);
    }

    @EventHandler()
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() == 1) {
            zeroplayer = true;
        }
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
