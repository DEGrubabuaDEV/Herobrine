package de.grubabua.herobrine.phase;

import de.grubabua.herobrine.Herobrine;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class ObservationPhase extends Trait {
    private Player targetPlayer;
    private static final int TELEPORT_DISTANCE = 15;
    private static final int CHECK_DISTANCE = 200;
    private static final int SAFE_DISTANCE = 10;
    private static final int MAX_DISTANCE = 50;

    public ObservationPhase() {
        super("ObservePhase");
    }
    @Override
    public void load(DataKey key) {
    }

    @Override
    public void save(DataKey key) {
    }

    @Override
    public void run() {
        if (npc == null || !npc.isSpawned()) return;

        targetPlayer = findNearestPlayer();

        if (targetPlayer != null && !Herobrine.attackphase) {
            lookToPlayer(targetPlayer);
        }
    }

    private Player findNearestPlayer() {
        Player nearestPlayer = null;
        double nearestDistance = CHECK_DISTANCE;

        Location npcLocation = npc.getEntity().getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = player.getLocation().distance(npcLocation);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }


        if (nearestPlayer != null) {
            if (nearestDistance < SAFE_DISTANCE) {
                teleportNpcAway();
                nearestPlayer = null;
            }
            else if (nearestPlayer.equals(Herobrine.targetplayer) && nearestDistance > MAX_DISTANCE) {
                teleportNpcInRadius();
                nearestPlayer = null;
            }
        }

        return nearestPlayer;
    }

    private void teleportNpcAway() {
        Location npcLocation = npc.getEntity().getLocation();
        Random random = new Random();

        Vector direction = new Vector(random.nextDouble() - 0.5, 0, random.nextDouble() - 0.5).normalize();
        direction.multiply(TELEPORT_DISTANCE);

        Location targetLocation = npcLocation.clone().add(direction);

        targetLocation.setY(npcLocation.getWorld().getHighestBlockYAt(targetLocation) + 1);

        npc.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    private void teleportNpcInRadius() {
        Location npcLocation = npc.getEntity().getLocation();
        Random random = new Random();

        double xOffset = (random.nextDouble() - 0.5) * 2 * TELEPORT_DISTANCE;
        double zOffset = (random.nextDouble() - 0.5) * 2 * TELEPORT_DISTANCE;

        Location targetLocation = npcLocation.clone().add(xOffset, 0, zOffset);

        targetLocation.setY(npcLocation.getWorld().getHighestBlockYAt(targetLocation) + 1);

        npc.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void lookToPlayer(Player player) {
        Navigator navigator = npc.getNavigator();
        navigator.getLocalParameters().range(CHECK_DISTANCE);
        npc.faceLocation(player.getLocation());
    }
}

