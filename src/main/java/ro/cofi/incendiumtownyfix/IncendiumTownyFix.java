package ro.cofi.incendiumtownyfix;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ro.cofi.incendiumtownyfix.listener.tool.HeftyPickaxeListener;
import ro.cofi.incendiumtownyfix.listener.tool.RestlessNatureListener;
import ro.cofi.incendiumtownyfix.listener.weapon.FirestormListener;
import ro.cofi.incendiumtownyfix.listener.weapon.HolyWrathListener;
import ro.cofi.incendiumtownyfix.listener.weapon.MultiplexCrossbowListener;
import ro.cofi.incendiumtownyfix.listener.weapon.RagnarokListener;
import ro.cofi.incendiumtownyfix.listener.weapon.SentrysWrathListener;
import ro.cofi.incendiumtownyfix.listener.weapon.TrailblazerListener;
import ro.cofi.incendiumtownyfix.listener.weapon.VoltaicTridentListener;

import java.util.Arrays;
import java.util.function.Function;

public final class IncendiumTownyFix extends JavaPlugin {

    private static IncendiumTownyFix plugin;

    @SuppressWarnings("squid:S3010") // paper will call the constructor, can't initialize statically
    public IncendiumTownyFix() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // register listeners
        Arrays.<Function<IncendiumTownyFix, ? extends Listener>>asList(
            // weapons
            SentrysWrathListener::new,
            HolyWrathListener::new,
            MultiplexCrossbowListener::new,
            FirestormListener::new,
            RagnarokListener::new,
            TrailblazerListener::new,
            VoltaicTridentListener::new,

            // tools
            HeftyPickaxeListener::new,
            RestlessNatureListener::new
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener.apply(this), this));
    }

    public static IncendiumTownyFix getPlugin() {
        return plugin;
    }

    public static NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(plugin, key);
    }

}
