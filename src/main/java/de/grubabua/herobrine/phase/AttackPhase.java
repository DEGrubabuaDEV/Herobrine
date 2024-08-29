package de.grubabua.herobrine.phase;

import de.grubabua.herobrine.Herobrine;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;


public class AttackPhase extends Trait {
    private static final int TELEPORT_DISTANCE = 15;
    private static final int MAX_DISTANCE = 50;
    private Player targetPlayer;
    public static BossBar bossBar;

    public AttackPhase() {
        super("AttackPhase");
        bossBar = Bukkit.createBossBar("§4Herobrine", BarColor.PURPLE, BarStyle.SOLID, BarFlag.CREATE_FOG);
        bossBar.setVisible(true);
    }

    @Override
    public void load(DataKey key) {
    }

    @Override
    public void save(DataKey key) {
    }

    @Override
    public void run() {
        bossBar.setProgress(AttackPhaseEventListener.healthPercentage);
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        if (npc == null || !npc.isSpawned()) return;

        if (targetPlayer == null || !targetPlayer.isOnline() || targetPlayer.isDead()) {
            targetPlayer = findNearestPlayer();
        }

        if (targetPlayer != null) {
            moveToPlayer(targetPlayer);
        }

        checkAndTeleportIfFloating();
    }

    private Player findNearestPlayer() {
        Player nearestPlayer = null;
        double nearestDistance = 200.0;

        Location npcLocation = npc.getEntity().getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = player.getLocation().distance(npcLocation);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }

        if (nearestPlayer != null) {
            if (nearestPlayer.equals(Herobrine.targetplayer) && nearestDistance > MAX_DISTANCE) {
                npc.teleport(targetPlayer.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                nearestPlayer = null;
            }
        }

        return nearestPlayer;
    }

    public void moveToPlayer(Player player) {
        Location npcLocation = npc.getEntity().getLocation();
        Location playerLocation = player.getLocation();
        Navigator navigator = npc.getNavigator();

        npc.getEntity().setGravity(true);

        navigator.getLocalParameters().speedModifier(2.0F);
        navigator.getLocalParameters().range(200);
        navigator.getLocalParameters().distanceMargin(2.0);
        navigator.getLocalParameters().stuckAction((n, nav) -> {
            Vector jump = n.getEntity().getVelocity();
            jump.setY(0.5);
            n.getEntity().setVelocity(jump);
            return true;
        });

        double dx = playerLocation.getX() - npcLocation.getX();
        double dz = playerLocation.getZ() - npcLocation.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

        double dy = playerLocation.getY() - npcLocation.getY();
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) Math.toDegrees(Math.atan2(dy, distanceXZ));

        if (!(npc.getEntity() instanceof Player)) {
            npc.getEntity().setRotation(yaw, pitch);
        }

        if (npcLocation.distance(playerLocation) < 2.0) {
            player.damage(5.0, npc.getEntity());
        } else {
            navigator.setTarget(playerLocation);
        }

        navigator.setTarget(playerLocation);
        navigator.setTarget(targetPlayer, true);
        npc.faceLocation(player.getLocation());
    }

    private void checkAndTeleportIfFloating() {
        Location location = npc.getEntity().getLocation();
        Material blockUnderneath = location.subtract(0, 1, 0).getBlock().getType();

        while (blockUnderneath == Material.AIR || blockUnderneath == Material.WATER || blockUnderneath == Material.LAVA) {
            if (Herobrine.targetplayer.getLocation().getY() < location.getY()) break;
            location.subtract(0, 1, 0);
            npc.getEntity().teleport(location);
            blockUnderneath = location.getBlock().getType();
        }
    }
}






