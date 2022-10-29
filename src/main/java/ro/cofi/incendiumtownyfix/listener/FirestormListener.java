package ro.cofi.incendiumtownyfix.listener;

import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.ProjectileUtils;

import java.util.List;

public class FirestormListener extends AbstractListener {

    private static final double EXPLOSION_RANGE = 1;
    private static final double FIRE_PROBABILITY = 0.5;
    private static final int FIRE_TICKS = 80;

    public FirestormListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @SuppressWarnings("squid:S1874") // no other choice other than the deprecated method
    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.getScoreboardTags().contains("in.firestorm_firework"))
            return;

        // if no shooter could be found or set, we can't do anything
        Player shooter = ProjectileUtils.getOrFixShooter(plugin, firework);
        if (shooter == null)
            return;

        // if this is a big explosion, apply harm potion damage to nearby entities
        if (!firework.getScoreboardTags().contains("in.firestorm_firework_explosion"))
            return;

        // gather entities hit by the firework explosion
        List<LivingEntity> hitEntities = firework
            .getNearbyEntities(EXPLOSION_RANGE, EXPLOSION_RANGE, EXPLOSION_RANGE)
            .stream()
            .filter(LivingEntity.class::isInstance)
            .map(LivingEntity.class::cast)
            .toList();

        // attempt to set entities on fire
        for (LivingEntity entity : hitEntities) {
            //noinspection deprecation - no other choice
            EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                shooter,
                entity,
                EntityDamageEvent.DamageCause.MAGIC,
                1
            );
            plugin.getServer().getPluginManager().callEvent(damageEvent);

            // 50% chance to set the entity on fire
            if (!damageEvent.isCancelled() && Math.random() < FIRE_PROBABILITY)
                entity.setFireTicks(FIRE_TICKS);
        }
    }
}
