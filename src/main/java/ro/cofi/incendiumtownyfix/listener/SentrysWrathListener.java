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

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class SentrysWrathListener extends AbstractListener {

    private static final double EXPLOSION_RANGE = 4.5;
    private static final int HARMING_LEVEL = 2;

    public SentrysWrathListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @SuppressWarnings("squid:S1874") // no other choice other than the deprecated method
    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        if (!(event.getEntity().getScoreboardTags().contains("in.sentrys_wrath_firework")))
            return;

        // gather entities hit by the firework explosion
        List<LivingEntity> hitEntities = event.getEntity()
            .getNearbyEntities(EXPLOSION_RANGE, EXPLOSION_RANGE, EXPLOSION_RANGE)
            .stream()
            .filter(LivingEntity.class::isInstance)
            .map(LivingEntity.class::cast)
            .toList();

        int damage = (HARMING_LEVEL + 1) * 6; // potion of harming
        Player shooter = (Player) event.getEntity().getShooter();
        if (shooter == null)
            setAttacker(event.getEntity());

        // if the shooter is still null, we can't do anything
        shooter = (Player) event.getEntity().getShooter();
        if (shooter == null)
            return;

        // if this is a big explosion, apply harm potion damage to nearby entities
        if (!(event.getEntity().getScoreboardTags().contains("in.sentrys_wrath_firework_explosion")))
            return;

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

    @SuppressWarnings("squid:S3011") // we need reflection
    private void setAttacker(Firework firework) {
        // acquire the firework's UUID
        UUID fireworkUUID = firework.getUniqueId();

        // recursively traverse the firework's parent classes until a field called "entity" is found
        // obtain the entity object of this firework
        Object entityObj = null;
        Class<?> clazz = firework.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField("entity");
                field.setAccessible(true);
                entityObj = field.get(firework);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (entityObj == null)
            return;

        // recursively traverse the firework entity's parent classes until a UUID field
        // that's different from the firework's UUID is found
        UUID ownerUUID = null;
        Object finalEntityObj = entityObj;
        clazz = entityObj.getClass();
        while (clazz != null) {
            ownerUUID = Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.getType() == UUID.class)
                .flatMap(field -> {
                    try {
                        field.setAccessible(true);
                        return Stream.of((UUID) field.get(finalEntityObj));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return Stream.empty();
                    }
                })
                .filter(uuid -> uuid != null && !uuid.equals(fireworkUUID))
                .findFirst()
                .orElse(null);

            if (ownerUUID != null)
                break;

            clazz = clazz.getSuperclass();
        }

        // if the owner UUID is null, we can't do anything
        if (ownerUUID == null)
            return;

        // get the owner's name
        Player owner = plugin.getServer().getPlayer(ownerUUID);

        // set the shooter to be used by other plugins
        firework.setShooter(owner);
    }
}
