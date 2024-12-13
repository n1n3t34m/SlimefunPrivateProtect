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
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.nineteam.slimefunPrivateProtect.SlimefunPrivateProtect.*;

public class BaseSlimefunListener implements Listener {
    private static final Map<UUID, Long> scrollUsed = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        // Prevent dropping radioactive materials
        if(SlimefunUtils.isRadioactive(e.getItemDrop().getItemStack()))
            SlimefunPrivateProtect.check(e);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTeleportEvent(EntityTeleportEvent e) {
        // Prevent stealing items with infused magnets
        // NOTE: Player location and item teleport "to" location are the same when event triggers
        //  this gives us a direct reference to player attempting to use magnet. Permissions checked
        //  at the item "from" location in case stealing attempt is performed through chunks border
        if(e.getEntity() instanceof Item) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.getLocation().equals(e.getTo())
                        && SlimefunUtils.containsSimilarItem(p.getInventory(), SlimefunItems.INFUSED_MAGNET, false)) {
                    SlimefunPrivateProtect.check(e, p, e.getFrom());
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
        // Prevent trolling with scroll of dimensional teleposition
        if(e.getTo().getYaw() - e.getFrom().getYaw() == 180F) {
            for(Player p : e.getFrom().getNearbyPlayers(16D)) {
                UUID id = p.getUniqueId();
                if(!id.equals(e.getPlayer().getUniqueId())
                        && scrollUsed.getOrDefault(id, -1L) == e.getFrom().getWorld().getTime()) {
                    SlimefunPrivateProtect.check(e, p, e.getFrom());
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        // Maintain map of players who used scroll of dimensional teleposition
        if(SlimefunUtils.isItemSimilar(e.getItem(), SlimefunItems.SCROLL_OF_DIMENSIONAL_TELEPOSITION, false)) {
            UUID id = e.getPlayer().getUniqueId();
            scrollUsed.put(id, e.getPlayer().getWorld().getTime());
            Bukkit.getScheduler().runTaskLater(instance, () -> scrollUsed.remove(id), 2);
        }
    }
}
