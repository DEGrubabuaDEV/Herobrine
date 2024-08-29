package de.grubabua.herobrine.phase;

import de.grubabua.herobrine.Herobrine;
import de.grubabua.herobrine.npc.HerobrineNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;


public class PhaseManager {
    private Thread phaseThread = null;
    private final HerobrineNPC herobrineNPC;

    public PhaseManager(HerobrineNPC herobrineNPC) {
        this.herobrineNPC = herobrineNPC;
    }

    public boolean isActive() {
        return phaseThread != null;
    }

    public void start() {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(AttackPhase.class).withName("AttackPhase"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ObservationPhase.class).withName("ObservePhase"));
        if (this.phaseThread != null) {
            throw new IllegalStateException("PhaseManager already started");
        }

        this.phaseThread = new Thread(() -> {
            try {
                Bukkit.getScheduler().runTask(Herobrine.getInstance(), () -> {
                    Herobrine.observationphase = true;

                    if (herobrineNPC.getNPC() != null) {
                        herobrineNPC.getNPC().addTrait(ObservationPhase.class);
                    } else {
                        Bukkit.getLogger().warning("NPC is null, cannot add ObservePhase trait.");
                        return;
                    }
                });

                long startTime = System.currentTimeMillis();
                long remainingTime = Herobrine.observationtime;

                while (remainingTime > 0) {
                    if (Bukkit.getOnlinePlayers().isEmpty()) {
                        Herobrine.observationtime = 2 * 60 * 1000;
                        return;
                    }

                    Thread.sleep(1000);
                    remainingTime -= 1000;
                }


                Bukkit.getScheduler().runTask(Herobrine.getInstance(), () -> {
                    Herobrine.observationphase = false;
                    Herobrine.attackphase = true;

                    if (herobrineNPC.getNPC() != null) {
                        herobrineNPC.getNPC().removeTrait(ObservationPhase.class);
                        herobrineNPC.getNPC().addTrait(AttackPhase.class);
                    } else {
                        Bukkit.getLogger().warning("NPC is null, cannot add AttackPhase trait.");
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        this.phaseThread.start();
    }
}


