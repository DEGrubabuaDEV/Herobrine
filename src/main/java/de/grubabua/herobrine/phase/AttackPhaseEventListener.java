package de.grubabua.herobrine.phase;

import de.grubabua.herobrine.Herobrine;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AttackPhaseEventListener implements Listener {
    private final JavaPlugin plugin;
    public static double healthPercentage = 1;
    public AttackPhaseEventListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !Herobrine.attackphase) return;

        Player player = (Player) event.getDamager();
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity())) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());

            Herobrine.npcHealth -= event.getDamage();

            Herobrine.npcHealth = Math.max(0, Herobrine.npcHealth);


            healthPercentage = Herobrine.npcHealth / Herobrine.healthdividend;

            npc.getEntity().getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, npc.getEntity().getLocation(), 10, 0.2, 0.2, 0.2, 0.1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);

            if (Herobrine.npcHealth <= 0) {
                npc.despawn();
                npc.destroy();
                Bukkit.broadcastMessage("§4Herobrine§7: I'm coming back!");
                Herobrine.npcHealth = 20.0;
                AttackPhase.bossBar.removeAll();
                return;
            }

            if (npc.getEntity() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) npc.getEntity();
                livingEntity.setNoDamageTicks(0);
            }
        }
    }
}
