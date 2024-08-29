package de.grubabua.herobrine;

import de.grubabua.herobrine.commands.StartCommand;
import de.grubabua.herobrine.npc.HerobrineNPC;
import de.grubabua.herobrine.phase.*;
import de.grubabua.herobrine.start.StartEvents;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Herobrine extends JavaPlugin {
    public static Player targetplayer;
    public static double npcHealth = 200.0;
    public static int observationtime = 5 * 60 * 1000;
    public static final double healthdividend = 200.0;
    public static Location spawnlocation;
    public static boolean attackphase;
    public static boolean observationphase;
    private HerobrineNPC herobrineNPC;
    private PhaseManager phaseManager;
    private static Herobrine instance;

    @Override
    public void onEnable() {
        instance = this;
        herobrineNPC = new HerobrineNPC(this);
        phaseManager = new PhaseManager(herobrineNPC);

        getServer().getPluginManager().registerEvents(new AttackPhaseEventListener(this), this);
        getServer().getPluginManager().registerEvents(new ObservationPhaseEventListener(), this);
        getServer().getPluginManager().registerEvents(new StartEvents(phaseManager, herobrineNPC, this), this);
        getCommand("starthunt").setExecutor(new StartCommand(this));
        if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
            Bukkit.getLogger().warning("Please make sure, that 'Citizens' is enabled!");
        }
    }
    @Override
    public void onDisable() {
        instance = null;
    }

    public static Herobrine getInstance() {
        return instance;
    }

    public HerobrineNPC getHerobrineNPC() {
        return herobrineNPC;
    }

    public PhaseManager getPhaseManager() {
        return phaseManager;
    }
}
