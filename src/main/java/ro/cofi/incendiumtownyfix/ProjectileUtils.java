package ro.cofi.incendiumtownyfix;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.stream.Stream;

public class ProjectileUtils {

    private ProjectileUtils() { }

    /**
     * Attempts to fix the owner (shooter) of a projectile using reflection.
     *
     * @return the owner of the projectile if they exist, null otherwise
     */
    @SuppressWarnings("squid:S3011") // we need reflection
    public static Player getOrFixShooter(Projectile projectile) {
        Player shooter = (Player) projectile.getShooter();
        if (shooter != null)
            return shooter;

        // acquire the firework's UUID
        UUID fireworkUUID = projectile.getUniqueId();

        // recursively traverse the firework's parent classes until a field called "entity" is found
        // obtain the entity object of this firework
        Object entityObj = null;
        Class<?> clazz = projectile.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField("entity");
                field.setAccessible(true);
                entityObj = field.get(projectile);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (entityObj == null)
            return null;

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
            return null;

        // get the owner's name
        Player owner = IncendiumTownyFix.getPlugin().getServer().getPlayer(ownerUUID);

        // set the shooter to be used by other plugins
        projectile.setShooter(owner);

        return owner;
    }
}
