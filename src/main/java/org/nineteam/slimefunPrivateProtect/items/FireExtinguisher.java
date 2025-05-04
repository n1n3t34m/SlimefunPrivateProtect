package org.nineteam.slimefunPrivateProtect.items;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Fire;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class FireExtinguisher extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private final ItemSetting<Integer> radius = new IntRangeSetting(this, "radius", 1, 16, Integer.MAX_VALUE);
    private final ItemSetting<Integer> maxFlames = new IntRangeSetting(this, "max-flames", 0, 0, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public FireExtinguisher(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemSetting(radius);
    }

    private void playEffects(Location loc) {
        loc.getWorld().playSound(loc, XSound.ENTITY_GENERIC_EXTINGUISH_FIRE.get(), 0.5F, 1.0F);
        loc.getWorld().spawnParticle(XParticle.POOF.get(), loc, 8);
    }

    @Override
    public @NotNull ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            if (e.getClickedBlock().stream().anyMatch(b -> b.getBlockData() instanceof Fire)) {
                playEffects(e.getClickedBlock().get().getLocation());
                List<Block> fires = findFire(e.getClickedBlock().get().getLocation());

                // Extinguish
                fires.forEach(b -> {
                    if (Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b.getLocation(), Interaction.BREAK_BLOCK)) {
                        if (Math.random() < 0.5)
                            playEffects(b.getLocation());
                        b.setType(Material.AIR);
                    }
                });
            } else {
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), XSound.ITEM_BUCKET_EMPTY_POWDER_SNOW.get(), 1.0F, 1.0F);
                e.getPlayer().sendMessage("Apply it directly on fire!");  // TODO: Customizable message
            }
        };
    }

    private List<Block> findFire(Location loc) {
        List<Block> found = new ArrayList<>();

        int r = radius.getValue();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Location test = loc.clone().add(x, y, z);
                    if (test.distance(loc) <= r) {  // In radius
                        Block b = test.getBlock();
                        if (b.getBlockData() instanceof Fire fire && !isEternal(b, fire)) { // Valid fire
                            found.add(b);
                            if (found.size() == maxFlames.getValue())  // Limit reached
                                return found;
                        }
                    }
                }
            }
        }

        return found;
    }

    private boolean isEternal(Block b, Fire fire) {
        // https://minecraft.wiki/w/Fire#Eternal_fire
        Material support = b.getRelative(BlockFace.DOWN).getType();
        return b.getWorld().getInfiniburn().contains(support) && fire.getFaces().isEmpty();
    }
}
