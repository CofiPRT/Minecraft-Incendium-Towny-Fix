package ro.cofi.incendiumtownyfix.listener.tool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;
import ro.cofi.incendiumtownyfix.IncendiumTownyFix;
import ro.cofi.incendiumtownyfix.listener.AbstractListener;
import ro.cofi.incendiumtownyfix.logic.Predicates;
import ro.cofi.incendiumtownyfix.logic.Util;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class HeftyPickaxeListener extends AbstractListener {

    private static final Set<Material> SMASHABLES = Set.of(
        Material.NETHERRACK,
        Material.BASALT,
        Material.BLACKSTONE,
        Material.STONE
    );

    private static final double SMASH_PROBABILITY = 0.5;

    public HeftyPickaxeListener(IncendiumTownyFix plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // the event must not be our own
        if (event instanceof CustomBlockBreakEvent)
            return;

        Block block = event.getBlock();

        // only interested in specific blocks
        if (!SMASHABLES.contains(block.getType()))
            return;

        Player player = event.getPlayer();

        // the player must be holding a hefty pickaxe
        String tag = player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
            .get(IncendiumTownyFix.getNamespacedKey("item_id"), PersistentDataType.STRING);

        if (!"hefty_pickaxe".equals(tag))
            return;

        // check all 6 adjacent blocks - if they are smashable, there's a 50% chance to destroy them
        List<Block> adjacentBlocks = Stream
            .of(
                block.getRelative(BlockFace.UP),
                block.getRelative(BlockFace.DOWN),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.WEST)
            )
            .filter(b -> SMASHABLES.contains(b.getType()))
            .filter(Predicates.random(SMASH_PROBABILITY))
            .toList();

        // test if the player is allowed to break the adjacent blocks using events
        for (Block adjacentBlock : adjacentBlocks) {
            BlockBreakEvent adjacentBlockBreakEvent = new CustomBlockBreakEvent(adjacentBlock, player);

            Util.testEventAndApply(adjacentBlockBreakEvent, adjacentBlock::breakNaturally);
        }
    }

    private static class CustomBlockBreakEvent extends BlockBreakEvent {
        public CustomBlockBreakEvent(Block block, Player player) {
            super(block, player);
        }
    }
}
