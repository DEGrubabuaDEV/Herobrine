package de.grubabua.herobrine.npc;

import de.grubabua.herobrine.phase.PhaseManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPC {
    private final JavaPlugin plugin;
    private NPC npc;
    private PhaseManager phaseManager;

    public HerobrineNPC(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawn(Location location) {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        Bukkit.broadcastMessage("§eHerobrine joined the game");
        npc = registry.createNPC(EntityType.PLAYER, "§4Herobrine");
        npc.spawn(location);

        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName("JavaKatze", true);

        new BukkitRunnable() {
            @Override
            public void run() {
                npc.setName("§4Herobrine");
                npc.data().setPersistent("nameplate-visible", true);
            }
        }.runTaskLater(plugin, 60L);

        this.phaseManager = new PhaseManager(this);
        phaseManager.start();
    }

    public NPC getNPC() {
        return npc;
    }
}





