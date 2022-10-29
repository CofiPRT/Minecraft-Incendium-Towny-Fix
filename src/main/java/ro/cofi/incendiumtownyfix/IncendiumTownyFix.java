package ro.cofi.incendiumtownyfix;

import org.bukkit.plugin.java.JavaPlugin;
import ro.cofi.incendiumtownyfix.listener.HolyWrathListener;
import ro.cofi.incendiumtownyfix.listener.MultiplexCrossbowListener;
import ro.cofi.incendiumtownyfix.listener.SentrysWrathListener;

public final class IncendiumTownyFix extends JavaPlugin {

    @Override
    public void onEnable() {
        // register listeners
        getServer().getPluginManager().registerEvents(new SentrysWrathListener(this), this);
        getServer().getPluginManager().registerEvents(new HolyWrathListener(this), this);
        getServer().getPluginManager().registerEvents(new MultiplexCrossbowListener(this), this);
    }

}
