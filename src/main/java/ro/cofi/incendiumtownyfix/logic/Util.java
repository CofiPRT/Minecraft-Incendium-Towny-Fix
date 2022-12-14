package ro.cofi.incendiumtownyfix.logic;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Util {

    private Util() { }

    /**
     * Attempts to fix the owner (shooter) of a projectile.
     *
     * @return the owner of the projectile if they exist, null otherwise
     */
    public static Player getOrFixShooter(Projectile projectile) {
        // get the owner's name
        Player owner = getOwner(projectile);
        if (owner == null)
            return null;

        // set the shooter to be used by other plugins
        projectile.setShooter(owner);

        return owner;
    }

    /**
     * Attempts to retrieve the owner of an entity, stored in a persistent data container.
     *
     * @return the owner of the entity if they exist, null otherwise
     */
    public static Player getOwner(Entity entity) {
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        int[] uuidArray = persistentDataContainer.get(
            IncendiumTownyFix.getNamespacedKey("owner"),
            PersistentDataType.INTEGER_ARRAY
        );

        if (uuidArray == null || uuidArray.length != 4)
            return null;

        // convert the array to a UUID - the array has 4 elements, each representing a 32-bit integer
        // the UUID constructor takes 2 longs, so we need to convert the array to 2 longs
        long mostSigBits = ((long) uuidArray[0] << 32) | (uuidArray[1] & 0xFFFFFFFFL);
        long leastSigBits = ((long) uuidArray[2] << 32) | (uuidArray[3] & 0xFFFFFFFFL);

        UUID uuid = new UUID(mostSigBits, leastSigBits);

        // get the owner's name
        return IncendiumTownyFix.getPlugin().getServer().getPlayer(uuid);
    }

    @SafeVarargs
    public static List<LivingEntity> getNearbyEntities(Entity entity, double radius, Predicate<Entity>... filters) {
        return entity.getNearbyEntities(radius, radius, radius) // within box
            .stream()
            .filter(Stream.of(filters).reduce(Predicate::and).orElse(Predicates.TRUE)) // filter
            .filter(e -> e.getLocation().distanceSquared(entity.getLocation()) <= radius * radius) // within sphere
            .map(LivingEntity.class::cast)
            .toList();
    }

    public static void fixAreaOfEffectOwner(AreaEffectCloud cloud, List<LivingEntity> affectedEntities) {
        // retrieve the cloud's owner, as set by the datapack
        Player player = Util.getOwner(cloud);
        if (player == null)
            return;

        // only apply to damageable entities
        List<LivingEntity> damageableEntities = new ArrayList<>();

        Util.testDamageAndApply(player, affectedEntities, 1, damageableEntities::add);

        // remove all non-damageable entities from the list
        affectedEntities.clear();
        affectedEntities.addAll(damageableEntities);
    }

    public static boolean testEventAndApply(Cancellable event, Runnable action) {
        IncendiumTownyFix.getPlugin().getServer().getPluginManager().callEvent((Event) event);

        if (event.isCancelled())
            return false;

        action.run();
        return true;
    }

    @SuppressWarnings("squid:S1874") // no other choice other than the deprecated method
    public static void testDamageAndApply(
        Player shooter, List<LivingEntity> entities, double damage, Consumer<LivingEntity> action
    ) {
        for (LivingEntity entity : entities) {
            //noinspection deprecation - no other choice
            EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
                shooter,
                entity,
                EntityDamageEvent.DamageCause.MAGIC,
                damage
            );

            testEventAndApply(damageEvent, () -> action.accept(entity));
        }
    }

    public static void attemptEntityIgnition(Player shooter, List<LivingEntity> entities, int fireTicks) {
        testDamageAndApply(shooter, entities, 1, entity -> entity.setFireTicks(fireTicks));
    }
}
