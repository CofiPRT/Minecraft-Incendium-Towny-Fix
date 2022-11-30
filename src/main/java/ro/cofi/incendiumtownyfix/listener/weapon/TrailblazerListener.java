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

public class TrailblazerListener extends AbstractListener {

    private static final double BURN_RANGE = 5;
    private static final double FIRE_PROBABILITY = 0.25;
    private static final int FIRE_TICKS = 80;

    public TrailblazerListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();

        // if this is an explosion firework, fix this firework's shooter, so that other plugins may properly identify it
        if (firework.getScoreboardTags().contains("in.trailblazer_firework")) {
            Util.getOrFixShooter(firework);
            return;
        }

        // if this is a dummy, burn firework, burn nearby entities
        if (!firework.getScoreboardTags().contains("in.trailblazer_firework_burn"))
            return;

        // always cancel the event
        event.setCancelled(true);

        // if no shooter could be found or set, we can't do anything
        Player shooter = Util.getOrFixShooter(firework);
        if (shooter == null)
            return;

        // get nearby entities and attempt to set them on fire (25% chance)
        List<LivingEntity> nearbyEntities = Util.getNearbyEntities(
            firework, BURN_RANGE,
            Predicates.MOBS_NOT_PLAYER, Predicates.random(FIRE_PROBABILITY)
        );
        Util.attemptEntityIgnition(shooter, nearbyEntities, FIRE_TICKS);

        // kill the firework
        firework.remove();
    }

}
