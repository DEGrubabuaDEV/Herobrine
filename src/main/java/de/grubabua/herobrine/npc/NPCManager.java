package de.grubabua.herobrine.npc;

import de.grubabua.herobrine.Herobrine;
import org.bukkit.plugin.java.JavaPlugin;

public class NPCManager {
    private final JavaPlugin plugin;

    public NPCManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createHerobrineNPC(){
        HerobrineNPC npc = new HerobrineNPC(plugin);
        npc.spawn(Herobrine.spawnlocation);
    }
}
