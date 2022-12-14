package ro.cofi.incendiumtownyfix.listener.weapon;

import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Util;

public class MultiplexCrossbowListener extends AbstractListener {

    public MultiplexCrossbowListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow))
            return;

        if (!arrow.getScoreboardTags().contains("in.multiplex"))
            return;

        // fix this arrow's shooter if necessary, so that other plugins may properly identify it
        Util.getOrFixShooter(arrow);
    }
}
