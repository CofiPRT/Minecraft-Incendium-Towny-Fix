package ro.cofi.incendiumtownyfix.listener;

import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.logic.Predicates;
import ro.cofi.incendiumtownyfix.logic.Util;

import java.util.List;

public class HolyWrathListener extends AbstractListener {

    private static final double EXPLOSION_RANGE_SMALL = 2;
    private static final double EXPLOSION_RANGE_BIG = 8;
    private static final double KNOCKBACK_FORCE = 2.1;

    public HolyWrathListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @SuppressWarnings("squid:S1874") // no other choice other than the deprecated method
    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.getScoreboardTags().contains("in.holy_wrath_firework"))
            return;

        // if no shooter could be found or set, we can't do anything
        Player shooter = Util.getOrFixShooter(firework);
        if (shooter == null)
            return;

        double explosionRange = 0;

        // apply knockback to nearby entities
        if (firework.getScoreboardTags().contains("in.holy_wrath_firework_explosion_big"))
            explosionRange = EXPLOSION_RANGE_BIG;
        else if (firework.getScoreboardTags().contains("in.holy_wrath_firework_explosion_small"))
            explosionRange = EXPLOSION_RANGE_SMALL;

        // if this isn't an explosion, stop
        if (explosionRange == 0)
            return;

        // gather entities hit by the firework explosion
        List<LivingEntity> hitEntities = Util.getNearbyEntities(
            firework, explosionRange,
            Predicates.MOBS_NOT_PLAYER, Predicates.SANCTUM_GUARDIAN.negate()
        );

        // attempt to apply motion to every entity
        Util.testDamageAndApply(
            shooter, hitEntities, 1,
            entity -> {
                // apply knockback, from the entity towards the firework - minecraft reverses the direction
                Vector direction = firework.getLocation().toVector().subtract(
                    entity.getLocation().toVector()
                ).normalize();

                entity.knockback(KNOCKBACK_FORCE * KNOCKBACK_FORCE, direction.getX(), direction.getZ());
            }
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireballHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof SmallFireball fireball))
            return;

        if (!fireball.getScoreboardTags().contains("in.cluster"))
            return;

        // fix this fireball's shooter if necessary, so that other plugins may properly identify it
        Util.getOrFixShooter(fireball);
    }
}
