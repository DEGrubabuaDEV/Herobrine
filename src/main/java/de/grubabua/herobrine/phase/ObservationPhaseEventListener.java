package de.grubabua.herobrine.phase;

import de.grubabua.herobrine.Herobrine;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ObservationPhaseEventListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player) || !Herobrine.observationphase) return;
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
