package ro.cofi.incendiumtownyfix.listener.weapon;

import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FireworkExplodeEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Predicates;
import ro.cofi.incendiumtownyfix.logic.Util;

import java.util.List;

public class FirestormListener extends AbstractListener {

    private static final double EXPLOSION_RANGE = 1;
    private static final double FIRE_PROBABILITY = 0.5;
    private static final int FIRE_TICKS = 80;

    public FirestormListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.getScoreboardTags().contains("in.firestorm_firework"))
            return;

        // if no shooter could be found or set, we can't do anything
        Player shooter = Util.getOrFixShooter(firework);
        if (shooter == null)
            return;

        // if this is a big explosion, apply harm potion damage to nearby entities
        if (!firework.getScoreboardTags().contains("in.firestorm_firework_explosion"))
            return;

        // gather entities hit by the firework explosion and attempt to set them on fire (50% chance)
        List<LivingEntity> hitEntities = Util.getNearbyEntities(
            firework, EXPLOSION_RANGE,
            Predicates.MOBS, Predicates.random(FIRE_PROBABILITY)
        );
        Util.attemptEntityIgnition(shooter, hitEntities, FIRE_TICKS);
    }
}
