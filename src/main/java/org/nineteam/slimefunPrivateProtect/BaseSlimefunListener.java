package org.nineteam.slimefunPrivateProtect;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

public class BaseSlimefunListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        // Prevent dropping radioactive materials
        if(SlimefunUtils.isRadioactive(e.getItemDrop().getItemStack()))
            SlimefunPrivateProtect.check(e);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        // Prevent stealing items with magnets
        if(!e.isSneaking()) return;
        if(SlimefunUtils.containsSimilarItem(e.getPlayer().getInventory(), SlimefunItems.INFUSED_MAGNET, false))
            SlimefunPrivateProtect.check(e);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        // Prevent trolling with scroll on dimensional teleposition
        if(SlimefunUtils.isItemSimilar(e.getItem(), SlimefunItems.SCROLL_OF_DIMENSIONAL_TELEPOSITION, false))
            SlimefunPrivateProtect.check(e);
    }
}
