package org.nineteam.slimefunPrivateProtect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTeleportEvent;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.sefiraat.crystamaehistoria.slimefun.CrystaStacks;

public class CrystamaeHistoriaListener implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTeleportEvent(EntityTeleportEvent e) {
        // Prevent stealing items with displaced voids
        if(e.getEntity() instanceof Item) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.getLocation().equals(e.getTo())) return;
                if(SlimefunUtils.containsSimilarItem(p.getInventory(), CrystaStacks.DISPLACED_VOID, false))
                    SlimefunPrivateProtect.check(e, p, e.getFrom());
            }
        }
    }
}
