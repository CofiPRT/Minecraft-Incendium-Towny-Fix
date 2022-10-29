package ro.cofi.incendiumtownyfix.listener;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.executors.TownyActionEventExecutor;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.ProjectileUtils;

public class RagnarokListener extends AbstractListener {

    public RagnarokListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.getScoreboardTags().contains("in.ragnarok_firework"))
            return;

        // fix this firework's shooter if necessary, so that other plugins may properly identify it
        ProjectileUtils.getOrFixShooter(firework);
    }

    /**
     * Prevents mobs from being set on fire by lightning strikes if they are protected by Towny.
     * <br><br>
     * A PaperMC bug allows the entity to be set on fire when the lightning strikes are summoned too fast.
     * See <a href="https://github.com/PaperMC/Paper/issues/8523">PaperMC/Paper#8523</a>.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLightningStrike(EntityCombustByEntityEvent event) {
        if (!(event.getCombuster() instanceof LightningStrike))
            return;

        if (Towny.getPlugin().isError()) {
            event.setCancelled(true);
            return;
        }

        if (!TownyAPI.getInstance().isTownyWorld(event.getEntity().getWorld()))
            return;

        boolean shouldCancel = !TownyActionEventExecutor.canExplosionDamageEntities(
            event.getEntity().getLocation(), event.getEntity(), EntityDamageEvent.DamageCause.LIGHTNING
        );

        if (shouldCancel)
            event.setCancelled(true);
    }
}
