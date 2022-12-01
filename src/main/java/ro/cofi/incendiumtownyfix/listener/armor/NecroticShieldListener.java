package ro.cofi.incendiumtownyfix.listener.armor;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Util;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NecroticShieldListener extends AbstractListener {

    public NecroticShieldListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onNecroticCloudApply(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();
        if (!cloud.getScoreboardTags().contains("in.necrotic_wither"))
            return;

        Util.fixAreaOfEffectOwner(cloud, event.getAffectedEntities());
    }

    @EventHandler(ignoreCancelled = true)
    public void onNecroticFangsHurt(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof EvokerFangs fangs))
            return;

        if (!fangs.getScoreboardTags().contains("in.necrotic_fangs"))
            return;

        if (!(event.getEntity() instanceof LivingEntity victim))
            return;

        // attempt to retrieve this fang's owner
        Player owner = Util.getOwner(fangs);
        if (owner == null)
            return;

        // test if the owner can damage the victim
        AtomicBoolean allowed = new AtomicBoolean(false);
        Util.testDamageAndApply(owner, List.of(victim), event.getDamage(), entity -> allowed.set(true));

        if (!allowed.get())
            event.setCancelled(true);
    }
}
