package ro.cofi.incendiumtownyfix.listener;

import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.ProjectileUtils;

import java.util.List;

public class SentrysWrathListener extends AbstractListener {

    private static final double EXPLOSION_RANGE = 4.5;
    private static final int HARMING_LEVEL = 2;

    public SentrysWrathListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @SuppressWarnings("squid:S1874") // no other choice other than the deprecated method
    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.getScoreboardTags().contains("in.sentrys_wrath_firework"))
            return;

        // if no shooter could be found or set, we can't do anything
        Player shooter = ProjectileUtils.getOrFixShooter(plugin, firework);
        if (shooter == null)
            return;

        // if this is a big explosion, apply harm potion damage to nearby entities
        if (!firework.getScoreboardTags().contains("in.sentrys_wrath_firework_explosion"))
            return;

        // gather entities hit by the firework explosion
        List<LivingEntity> hitEntities = firework
            .getNearbyEntities(EXPLOSION_RANGE, EXPLOSION_RANGE, EXPLOSION_RANGE)
            .stream()
            .filter(LivingEntity.class::isInstance)
            .map(LivingEntity.class::cast)
            .toList();

        int damage = (HARMING_LEVEL + 1) * 6; // potion of harming

        // attempt to damage every entity with a potion of harming 2
        for (LivingEntity entity : hitEntities) {
            //noinspection deprecation - no other choice
            EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                shooter,
                entity,
                EntityDamageEvent.DamageCause.MAGIC,
                damage
            );
            plugin.getServer().getPluginManager().callEvent(damageEvent);

            if (!damageEvent.isCancelled())
                entity.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, HARMING_LEVEL));
        }
    }
}
