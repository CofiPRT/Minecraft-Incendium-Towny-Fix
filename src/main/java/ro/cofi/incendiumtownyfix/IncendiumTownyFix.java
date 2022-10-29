package ro.cofi.incendiumtownyfix;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ro.cofi.incendiumtownyfix.listener.FirestormListener;
import ro.cofi.incendiumtownyfix.listener.HolyWrathListener;
import ro.cofi.incendiumtownyfix.listener.MultiplexCrossbowListener;
import ro.cofi.incendiumtownyfix.listener.SentrysWrathListener;

import java.util.Arrays;
import java.util.function.Function;

public final class IncendiumTownyFix extends JavaPlugin {

    @Override
    public void onEnable() {
        // register listeners
        Arrays.<Function<IncendiumTownyFix, ? extends Listener>>asList(
            SentrysWrathListener::new,
            HolyWrathListener::new,
            MultiplexCrossbowListener::new,
            FirestormListener::new
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener.apply(this), this));
    }

}
