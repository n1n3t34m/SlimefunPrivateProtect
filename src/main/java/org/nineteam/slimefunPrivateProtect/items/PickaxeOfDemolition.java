package org.nineteam.slimefunPrivateProtect.items;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class PickaxeOfDemolition extends SimpleSlimefunItem<ToolUseHandler> implements DamageableItem {

    private final ItemSetting<Integer> maxBlocks = new IntRangeSetting(this, "max-blocks", 1, 3456, Integer.MAX_VALUE);
    private final ItemSetting<Boolean> damageOnUse = new ItemSetting<>(this, "damage-on-use", false);

    @ParametersAreNonnullByDefault
    public PickaxeOfDemolition(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemSetting(maxBlocks);
        addItemSetting(damageOnUse);
    }

    @Override
    public boolean isDamageable() {
        return damageOnUse.getValue();
    }

    private void playEffects(Location loc) {
        loc.getWorld().playSound(loc, XSound.ENTITY_GENERIC_EXPLODE.get(), 1.0F, 1.0F);
        loc.getWorld().spawnParticle(XParticle.EXPLOSION.get(), loc, 1);
    }

    @Override
    public @Nonnull ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {
            e.setCancelled(true);

            if (e.getBlock().getType() == Material.COBBLESTONE) {
                playEffects(e.getBlock().getLocation());
                Set<Block> blocks = findConnected(e.getBlock());

                // Break blocks
                blocks.forEach(b -> {
                    if (Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b.getLocation(), Interaction.BREAK_BLOCK)) {
                        if (Math.random() < 0.1)
                            playEffects(b.getLocation());
                        b.setType(Material.AIR);
                    }
                });

                if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
                    damageItem(e.getPlayer(), tool);
            } else {
                e.getBlock().getWorld().playSound(e.getBlock().getLocation(), XSound.BLOCK_ANVIL_LAND.get(), 0.5F, 1.0F);
                e.getPlayer().sendMessage("Can only be used on cobblestone!");  // TODO: Customizable message
            }
        };
    }

    private Set<Block> findConnected(Block b) {
        Set<Block> found = new HashSet<>();

        Deque<Block> queue = new LinkedList<>();
        queue.addLast(b);

        // Simple Flood fill search
        while (found.size() < maxBlocks.getValue() && !queue.isEmpty()) {
            Block next = queue.pollFirst();

            switch (next.getType()) {
                case COBBLESTONE -> {
                    found.add(next);

                    // Adjacent blocks
                    for (Faces f : Faces.values()) {
                        Block adjacent = next.getRelative(f.getModX(), f.getModY(), f.getModZ());
                        if (!queue.contains(adjacent) && !found.contains(adjacent))
                            queue.addLast(adjacent);
                    }
                }
                case STONE, OBSIDIAN -> found.add(next);  // Allow 1 connection with other type
                case WATER, LAVA -> {  // Allow 1 connection with fluid source
                    Levelled fluid = (Levelled) next.getBlockData();
                    if (fluid.getLevel() == 0)
                        found.add(next);
                }
            }
        }

        return found;
    }

    @RequiredArgsConstructor
    @Getter
    enum Faces {
        // 6 sides
        NORTH(0, 0, -1),
        EAST(1, 0, 0),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0),
        UP(0, 1, 0),
        DOWN(0, -1, 0),

        // 12 edges
        NORTH_UP(0, 1, -1),
        NORTH_DOWN(0, -1, -1),
        EAST_UP(1, 1, 0),
        EAST_DOWN(1, -1, 0),
        SOUTH_UP(0, 1, 1),
        SOUTH_DOWN(0, -1, 1),
        WEST_UP(-1, 1, 0),
        WEST_DOWN(-1, -1, 0),
        NORTH_EAST(1, 0, -1),
        EAST_SOUTH(1, 0, 1),
        SOUTH_WEST(-1, 0, 1),
        WEST_NORTH(-1, 0, -1);

        // 8 corners
        /*NORTH_EAST_UP(1, 1, -1),
        NORTH_EAST_DOWN(1, -1, -1),
        EAST_SOUTH_UP(1, 1, 1),
        EAST_SOUTH_DOWN(1, -1, 1),
        SOUTH_WEST_UP(-1, 1, 1),
        SOUTH_WEST_DOWN(-1, -1, 1),
        WEST_NORTH_UP(-1, 1, -1),
        WEST_NORTH_DOWN(-1, -1, -1);*/

        private final int modX;
        private final int modY;
        private final int modZ;
    }
}
