package ro.cofi.incendiumtownyfix.listener.weapon;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FireworkExplodeEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Util;

public class VoltaicTridentListener extends AbstractListener {

    public VoltaicTridentListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.getScoreboardTags().contains("in.voltaic_trident_firework"))
            return;

        // fix this firework's shooter if necessary, so that other plugins may properly identify it
        Util.getOrFixShooter(firework);
    }
}
