package ro.cofi.incendiumtownyfix.listener.armor;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Util;

public class RadiationShieldListener extends AbstractListener {

    public RadiationShieldListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRadiationCloudApply(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();
        if (!cloud.getScoreboardTags().contains("in.radiation"))
            return;

        Util.fixAreaOfEffectOwner(cloud, event.getAffectedEntities());
    }
}
