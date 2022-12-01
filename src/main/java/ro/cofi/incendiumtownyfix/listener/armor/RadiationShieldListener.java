package ro.cofi.incendiumtownyfix.listener.armor;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Util;

import java.util.ArrayList;
import java.util.List;

public class RadiationShieldListener extends AbstractListener {

    public RadiationShieldListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRadiationCloudApply(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();
        if (!cloud.getScoreboardTags().contains("in.radiation"))
            return;

        // retrieve the cloud's owner, as set by the datapack
        Player player = Util.getOwner(cloud);
        if (player == null)
            return;

        // only apply to damageable entities
        List<LivingEntity> affectedEntities = event.getAffectedEntities();
        List<LivingEntity> damageableEntities = new ArrayList<>();

        Util.testDamageAndApply(player, affectedEntities, 1, damageableEntities::add);

        // remove all non-damageable entities from the list
        affectedEntities.clear();
        affectedEntities.addAll(damageableEntities);
    }
}
