package ro.cofi.incendiumtownyfix;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ro.cofi.incendiumtownyfix.listener.*;

import java.util.Arrays;
import java.util.function.Function;

public final class IncendiumTownyFix extends JavaPlugin {

    private static IncendiumTownyFix plugin;

    public IncendiumTownyFix() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // register listeners
        Arrays.<Function<IncendiumTownyFix, ? extends Listener>>asList(
            SentrysWrathListener::new,
            HolyWrathListener::new,
            MultiplexCrossbowListener::new,
            FirestormListener::new,
            RagnarokListener::new
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener.apply(this), this));
    }

    public static IncendiumTownyFix getPlugin() {
        return plugin;
    }

}
