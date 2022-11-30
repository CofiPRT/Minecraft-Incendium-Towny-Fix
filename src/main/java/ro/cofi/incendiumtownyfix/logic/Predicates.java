package ro.cofi.incendiumtownyfix.logic;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public interface Predicates {
    Predicate<Entity> TRUE = entity -> true;
    Predicate<Entity> MOBS = LivingEntity.class::isInstance;
    Predicate<Entity> PLAYER = Player.class::isInstance;
    Predicate<Entity> MOBS_NOT_PLAYER = MOBS.and(PLAYER.negate());
    Predicate<Entity> SANCTUM_GUARDIAN = entity -> entity.getScoreboardTags().contains("in.sanctum_guardian");

    static <T> Predicate<T> random(double probability) {
        return entity -> Math.random() < probability;
    }

}
