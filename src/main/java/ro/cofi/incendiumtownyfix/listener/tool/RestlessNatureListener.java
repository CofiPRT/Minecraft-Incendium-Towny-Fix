package ro.cofi.incendiumtownyfix.listener.tool;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RestlessNatureListener extends AbstractListener {

    public RestlessNatureListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkExplosion(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();

        if (!firework.getScoreboardTags().contains("in.restless_nature_firework"))
            return;

        // always cancel the event
        event.setCancelled(true);

        // if no shooter could be found or set, we can't do anything
        Player shooter = Util.getOrFixShooter(firework);
        if (shooter == null)
            return;

        Location location = firework.getLocation();
        Block fireworkBlock = location.getBlock();
        Block anchorBlock = fireworkBlock.getRelative(BlockFace.DOWN);
        ItemStack mainHandItem = shooter.getInventory().getItemInMainHand();
        EquipmentSlot mainHandSlot = EquipmentSlot.HAND;

        // kill the firework
        firework.remove();

        // test if the player is allowed to place at the location
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(
            fireworkBlock, fireworkBlock.getState(), anchorBlock,
            mainHandItem, shooter, true, mainHandSlot
        );

        // if they can't place the root, they can't place the tree
        if (!Util.testEventAndApply(blockPlaceEvent, () -> { }))
            return;

        // if the anchor block is not a crimson nylium, it needs to be replaced
        if (anchorBlock.getType() != Material.CRIMSON_NYLIUM) {
            // test if the player is allowed to replace the anchor block
            BlockPlaceEvent anchorReplaceEvent = new BlockPlaceEvent(
                anchorBlock, anchorBlock.getState(), anchorBlock.getRelative(BlockFace.DOWN),
                mainHandItem, shooter, true, mainHandSlot
            );

            if (!Util.testEventAndApply(anchorReplaceEvent, () -> { }))
                return;

            anchorBlock.setType(Material.CRIMSON_NYLIUM);
        }

        // simulate tree placement - only collect the blocks, without actually placing them
        List<BlockState> changes = new ArrayList<>();
        World world = location.getWorld();
        boolean result = world.generateTree(
            location,
            new Random(),
            TreeType.CRIMSON_FUNGUS,
            blockState -> {
                changes.add(blockState);
                return false;
            }
        );

        // if the tree could not be placed due to vanilla reasons, stop
        if (!result)
            return;

        // create a new event for this action
        StructureGrowEvent structureGrowEvent = new StructureGrowEvent(
            location, TreeType.CRIMSON_FUNGUS, false, shooter, changes
        );

        // if the event is allowed, place the blocks - keep in mind that the list may be modified
        Util.testEventAndApply(structureGrowEvent, () -> {
            for (BlockState blockState : changes)
                blockState.update(true, false);
        });
    }
}
