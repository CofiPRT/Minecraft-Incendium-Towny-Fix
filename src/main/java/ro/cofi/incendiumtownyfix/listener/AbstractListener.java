package ro.cofi.incendiumtownyfix.listener;

import org.bukkit.event.Listener;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;

public abstract class AbstractListener implements Listener {

    protected final IncendiumTownyFix plugin;

    protected AbstractListener(IncendiumTownyFix plugin) {
        this.plugin = plugin;
    }

}
