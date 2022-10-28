package ro.cofi.incendiumtownyfix;

import org.bukkit.plugin.java.JavaPlugin;

public final class IncendiumTownyFix extends JavaPlugin {

    @Override
    public void onEnable() {
        // register listeners
        getServer().getPluginManager().registerEvents(new EntityHurtListener(this), this);
    }
    
}
