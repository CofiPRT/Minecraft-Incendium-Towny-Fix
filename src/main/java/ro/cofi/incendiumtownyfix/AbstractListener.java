package ro.cofi.incendiumtownyfix;

import org.bukkit.event.Listener;

public abstract class AbstractListener implements Listener {

    protected final IncendiumTownyFix plugin;

    protected AbstractListener(IncendiumTownyFix plugin) {
        this.plugin = plugin;
    }

}
