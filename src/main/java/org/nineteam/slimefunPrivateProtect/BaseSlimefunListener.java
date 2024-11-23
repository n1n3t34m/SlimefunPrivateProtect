package org.nineteam.slimefunPrivateProtect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

public class BaseSlimefunListener implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        // Prevent dropping radioactive materials
        if(SlimefunUtils.isRadioactive(e.getItemDrop().getItemStack()))
            SlimefunPrivateProtect.check(e);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTeleportEvent(EntityTeleportEvent e) {
        // Prevent stealing items with infused magnets
        if(e.getEntity() instanceof Item) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.getLocation().equals(e.getTo())) return;
                if(SlimefunUtils.containsSimilarItem(p.getInventory(), SlimefunItems.INFUSED_MAGNET, false))
                    SlimefunPrivateProtect.check(e, p, e.getFrom());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        // Prevent trolling with scroll on dimensional teleposition
        // NOTE: This is a bit wonky because the scroll have quite some range
        //  but checking EntityTeleportEvent is harder and may introduce even more ways of trolling
        //  for example player just standing nearby holding the scroll may cancel other player teleportation
        if(SlimefunUtils.isItemSimilar(e.getItem(), SlimefunItems.SCROLL_OF_DIMENSIONAL_TELEPOSITION, false))
            SlimefunPrivateProtect.check(e);
    }
}
